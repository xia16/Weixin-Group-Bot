import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.google.gson.Gson;
import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.stream.Collectors;

public class Utils {
    private static final String BUCKET_NAME = "wechatgroupbot";

    private static String s3Key(String organization, String subject, int section, int questionId) {
        return organization +
               "/" +
               subject +
               "/" +
               Integer.toString(section) +
               "/" +
               Integer.toString(questionId) + ".json";
    }

    private static String subjectMetadataS3Key(String organization, String subject) {
        return organization +
               "/" +
               subject +
               "/metadata.json";
    }

    private static String sectionMetadataS3Key(String organization, String subject, int section) {
        return organization +
               "/" +
               subject +
               "/" +
               Integer.toString(section) +
               "/metadata.json";
    }

    public static Question getQuestion( String organization, String subject, int section, int questionId ) {
        String s3Key = s3Key(organization, subject, section, questionId);
        System.out.println(s3Key);
        return getObject(BUCKET_NAME, s3Key, Question.class);
    }

    private static int getSectionId(String organization, String subject, String sectionName) {
        String s3Key = subjectMetadataS3Key(organization, subject);
        SubjectMetadata md = getObject(BUCKET_NAME, s3Key, SubjectMetadata.class);

        if (md == null) {
            return -1;
        }
        return md.getSections().get(sectionName);
    }

    public static int postQuestion(String organization, String subject, String sectionName,
                                    HashSet<String> tags, String title,
                                    String description) {
        int sectionId = getSectionId(organization, subject, sectionName);
        String s3Key = sectionMetadataS3Key(organization, subject, sectionId);
        SectionMetadata md = getObject(BUCKET_NAME, s3Key, SectionMetadata.class);
        if (md == null) {
            return -1;
        }
        int questionId = md.getCount() + 1;

        Question question = Question.builder()
                                    .id(questionId)
                                    .displayId(questionId % 50)
                                    .postDate(ZonedDateTime.now().toString())
                                    .latestResponseDate(ZonedDateTime.now().toString())
                                    .tags(tags)
                                    .title(title)
                                    .body(description)
                                    .correspondence(new ArrayList<>())
                                    .build();
        return postBuiltQuestion(organization, subject, sectionId, question);
    }

    private static <T> void postObject(String bucketName, String s3Key, T object) {
        try {
            AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                                                     .withRegion("us-east-2").withPathStyleAccessEnabled(true)
                                                     .withCredentials(new ProfileCredentialsProvider())
                                                     .build();
            String objJson = new Gson().toJson( object );
            InputStream jsonStream = IOUtils.toInputStream(objJson, "UTF-8");
            Long length = (long) objJson.length();
            ObjectMetadata md = new ObjectMetadata();
            md.setContentLength( length );
            s3Client.putObject( new PutObjectRequest(bucketName, s3Key, jsonStream, md ) );
        } catch ( IOException e ) {
            e.printStackTrace();
        }
    }

    private static <T> T getObject(String bucketName, String s3Key, Class<T> targetClass) {
        AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                                                 .withRegion("us-east-2").withPathStyleAccessEnabled(true)
                                                 .withCredentials(new ProfileCredentialsProvider())
                                                 .build();
        S3Object obj = s3Client.getObject(new GetObjectRequest(bucketName, s3Key));
        InputStream dataStream = obj.getObjectContent();
        BufferedReader r = new BufferedReader(new InputStreamReader(dataStream));
        String data = r.lines().collect(Collectors.joining("\n" ) );
        try {
            obj.close();
        } catch ( Exception e ) {
            System.out.println( "IOException" );
            return null;
        }
        return new Gson().fromJson(data, targetClass);
    }

    private static int postBuiltQuestion(String organization, String subject, int section, Question question) {
        String s3Key = s3Key(organization, subject, section, question.getId());
        postObject(BUCKET_NAME, s3Key, question);

        s3Key = sectionMetadataS3Key(organization, subject, section);
        SectionMetadata md = getObject(BUCKET_NAME, s3Key, SectionMetadata.class);

        if (md == null) {
            return -1;
        }

        md.setCount(md.getCount() + 1);
        for (String tag : question.getTags()) {
            if (!md.getReverseLookup().containsKey(tag)) {
                md.getReverseLookup().put(tag, new HashSet<>());
            }
            md.getReverseLookup().get(tag).add(question.getId());
        }

        postObject(BUCKET_NAME, s3Key, md);

        return question.getDisplayId();
    }

    public static void createSubject(String organization, String subject) {
        String s3Key = subjectMetadataS3Key(organization, subject);
        SubjectMetadata md = SubjectMetadata.builder().sections(new HashMap<>()).build();
        postObject(BUCKET_NAME, s3Key, md);
    }

    public static int createSection(String organization, String subject, String sectionName) {
        String s3Key = subjectMetadataS3Key(organization, subject);
        SubjectMetadata md = getObject(BUCKET_NAME, s3Key, SubjectMetadata.class);
        if (md == null) {
            return -1;
        }
        if (md.getSections().containsKey(sectionName)) {
            return -1;
        }

        int sectionId = md.getCount() + 1;
        md.getSections().put(sectionName, sectionId);
        md.setCount(sectionId);
        postObject(BUCKET_NAME, s3Key, md);

        SectionMetadata sectionMd = SectionMetadata.builder().count(0).reverseLookup(new HashMap<>()).build();
        s3Key = sectionMetadataS3Key(organization, subject, sectionId);
        postObject(BUCKET_NAME, s3Key, sectionMd);

        return 0;
    }
}
