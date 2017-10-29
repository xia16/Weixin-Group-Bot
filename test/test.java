import java.util.HashSet;

public class test {
    public static void main(String[] args) {
//        Utils.createSubject("AdamEducation", "SampleSubject");
//        Utils.createSection("AdamEducation", "SampleSubject", "Section1");
        Utils.postQuestion("AdamEducation", "SampleSubject", "Section1",
                           new HashSet<>(), "How Smart is Adam Xia?", "He seems pretty smart in Dota.");
        String title = Utils.getQuestion("AdamEducation", "SampleSubject", 1, 4).getTitle();
        System.out.println(title);
    }
}
