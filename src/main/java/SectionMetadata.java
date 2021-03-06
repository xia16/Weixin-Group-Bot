import lombok.Builder;
import lombok.Data;

import java.util.HashMap;
import java.util.Set;

@Data
@Builder
public class SectionMetadata {
    int count = 0;
    HashMap<String, Set<Integer>> reverseLookup;
}
