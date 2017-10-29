import com.amazonaws.services.s3.AmazonS3;

public class Utils {

    public static String s3Key(String organization, String subject, int section, int questionId) {
        return organization +
               "/" +
               subject +
               "/" +
               Integer.toString(section) +
               "/" +
               Integer.toString(questionId) + ".json";
    }

    public static String metadataS3Key(String organization, String subject) {
        return organization +
               "/" +
               subject +
               "/metadata.json";
    }

    public static Question getQuestion( String organization, String subject, int section ) {
        AmazonS3 s3Client = new AmazonS3Client(new ProfileCredentialsProvider() );
        String s3Key = s3Key( improvementState, region, improvementId );
        S3Object obj = s3Client.getObject( new GetObjectRequest( SUGGESTION_BUCKET, s3Key ) );
        InputStream dataStream = obj.getObjectContent();
        BufferedReader r = new BufferedReader( new InputStreamReader( dataStream ) );
        String data = r.lines().collect( Collectors.joining( "\n" ) );
        try {
            obj.close();
        } catch ( Exception e ) {
            System.out.println( "IOException" );
            return null;
        }
        return new Gson().fromJson( data, ImprovementSuggestion.class );
    }
}
