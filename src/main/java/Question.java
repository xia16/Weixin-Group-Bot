import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

@Data
public class Question {
    private User u;

    private int id;
    private int displayId;
    private Date postDate;
    private Date latestResponseDate;

    private HashSet<String> tags;
    private String title;
    private String description;

    private ArrayList<Discussion> correspondence;
}
