package pt.ulisboa.tecnico.socialsoftware.tutor.classroom.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.classroom.domain.Document;

import java.io.Serializable;
import java.util.Arrays;

public class DocumentDto implements Serializable {
    private Integer id;
    private String type;
    private String title;
    private String content;
    private String url;
    private Integer classroomId;
    private byte[] pdf;
    private String extension;


    public DocumentDto(){}

    public DocumentDto(Document document){
        this.id = document.getId();
        this.title = document.getTitle();
        this.type = document.getType().name();
        this.extension = document.getExtension();
        this.content = document.getContent(); //
        this.classroomId = document.getClassroom().getId();
        this.url = document.getUrl(); //


    }

    public byte[] getPdf() {
        return pdf;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public void setPdf(byte[] pdf) {
        this.pdf = pdf;
    }

    public Integer getClassroomId() {
        return classroomId;
    }

    public void setClassroomId(Integer classroomId) {
        this.classroomId = classroomId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }


    @Override
    public String toString() {
        return "DocumentDto{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", url='" + url + '\'' +
                ", classroomId=" + classroomId +
                ", pdf=" + Arrays.toString(pdf) +
                ", extension='" + extension + '\'' +
                '}';
    }
}
