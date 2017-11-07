import com.google.common.collect.Sets;

import java.util.HashSet;

import static spark.Spark.*;

public class Server {
    private static void enableCORS( final String origin, final String methods, final String headers ) {

        options( "/*", ( request, response ) -> {

            String accessControlRequestHeaders = request.headers( "Access-Control-Request-Headers" );
            if ( accessControlRequestHeaders != null ) {
                response.header( "Access-Control-Allow-Headers", accessControlRequestHeaders );
            }

            String accessControlRequestMethod = request.headers( "Access-Control-Request-Method" );
            if ( accessControlRequestMethod != null ) {
                response.header( "Access-Control-Allow-Methods", accessControlRequestMethod );
            }

            return "OK";
        } );

        before( ( request, response ) -> {
            response.header( "Access-Control-Allow-Origin", origin );
            response.header( "Access-Control-Request-Method", methods );
            response.header( "Access-Control-Allow-Headers", headers );
            // Note: this may or may not be necessary in your particular application
            response.type( "application/json" );
        } );
    }

    public static void main( String[] args ) {
        enableCORS("*", "*", "*");
        get( "/getQuestion/*", (req, res) -> {
            String organization = req.queryParams("organization");
            String subject = req.queryParams("subject");
            String section = req.queryParams("section");
            int questionId = Integer.valueOf(req.queryParams("questionId"));
            return Utils.getQuestionFromRequest(organization, subject, section, questionId );
        } );

        get("/addDiscussion/*", (req, res) -> {
            String organization = req.queryParams("organization");
            String subject = req.queryParams("subject");
            String section = req.queryParams("section");
            int questionId = Integer.valueOf(req.queryParams("questionId"));
            int posterId = Integer.valueOf(req.queryParams("posterId"));
            String contents = req.queryParams("contents");
            return Utils.addDiscussion(organization, subject, section, questionId, posterId, contents );
        });

        get("/addQuestion/*", (req, res) -> {
            String organization = req.queryParams("organization");
            String subject = req.queryParams("subject");
            String section = req.queryParams("section");
            String tags = req.queryParams("tags");
            HashSet<String> tagSet = Sets.newHashSet(tags.split(","));
            String title = req.queryParams("title");
            int posterId = Integer.valueOf(req.queryParams("posterId"));
            String contents = req.queryParams("contents");
            return Utils.postQuestion(organization, subject, section, tagSet, title, contents);
        });

        get( "/getSectionMetadata/*", (req, res) -> {
            String organization = req.queryParams("organization");
            String subject = req.queryParams("subject");
            String section = req.queryParams("section");
            return Utils.getSectionMetadataFromRequest(organization, subject, section);
        } );

        get( "/getSubjectMetadata/*", (req, res) -> {
            String organization = req.queryParams("organization");
            String subject = req.queryParams("subject");
            return Utils.getSubjectMetadataFromRequest(organization, subject);
        } );
    }
}