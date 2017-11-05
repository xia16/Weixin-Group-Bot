import java.util.HashSet;

public class test {
    public static void main(String[] args) {
        Utils.createSubject("AdamEducation", "SampleSubject");
        Utils.createSection("AdamEducation", "SampleSubject", "Section1");
        Utils.postQuestion("AdamEducation", "SampleSubject", "Section1",
                           new HashSet<>(), "How Smart is Adam Xia?", "He seems pretty smart in Dota.");
        String q = Utils.getQuestionFromRequest("AdamEducation", "SampleSubject", "Section1", 1);
        System.out.println(q);
    }
}
