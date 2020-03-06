package pt.ulisboa.tecnico.socialsoftware.tutor.post.dto;

public class PostQuestionDto {
    private int id;
    private int qid;
    private int uid;
    private String question;
    private String studentQuestion;

    public PostQuestionDto() {
    }

    public PostQuestionDto(int qid, int uid, String question, String studentQuestion) {
        this.qid = qid;
        this.uid = uid;
        this.question = question;
        this.studentQuestion = studentQuestion;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getQid() {
        return qid;
    }

    public void setQid(int qid) {
        this.qid = qid;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getStudentQuestion() {
        return studentQuestion;
    }

    public void setStudentQuestion(String studentQuestion) {
        this.studentQuestion = studentQuestion;
    }
}
