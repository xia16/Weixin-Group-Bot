import lombok.Builder;
import lombok.Data;

import java.util.HashMap;

@Data
@Builder
public class SubjectMetadata {
    int count = 0;
    private HashMap<String, Integer> sections;
}
