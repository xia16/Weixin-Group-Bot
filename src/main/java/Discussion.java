import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Discussion {
    private int posterId;
    private String contents;
    private String postDate;
}
