import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

@Data
@Builder
public class Question {
    private int userId;

    private int id;
    private int displayId;
    private String postDate;
    private String latestResponseDate;

    private HashSet<String> tags;
    private String title;
    private String body;

    private ArrayList<Discussion> correspondence;
}
