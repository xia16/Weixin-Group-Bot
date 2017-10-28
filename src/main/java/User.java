import lombok.Data;

import java.util.HashSet;

@Data
public class User {
    private String username;
    private int userId;
    private HashSet<String> sections;

    private HashSet<Integer> questions;
    private HashSet<Integer> answers;
}
