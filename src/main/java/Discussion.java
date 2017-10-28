import lombok.Data;

import java.util.Date;

@Data
public class Discussion {
    private int posterId;
    private String contents;
    private Date postDate;
}
