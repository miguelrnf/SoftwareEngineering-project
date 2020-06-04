package pt.ulisboa.tecnico.socialsoftware.tutor.classroom.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.classroom.dto.DocumentDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.DomainEntity;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Image;

import javax.persistence.*;


import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Entity
@Table(name = "documents")
public class Document implements DomainEntity {
    public enum Type {VIDEO, DOC}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    private Document.Type type;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name = "url")
    private String url;

    @ManyToOne
    @JoinColumn(name = "classroom_id")
    private Classroom classroom;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "question")
    private Image image;


    public Document(){
    }

    public Document(DocumentDto documentDto) {
        checkInfo(documentDto);
        this.type = Document.Type.valueOf(documentDto.getType());
        this.title = documentDto.getTitle();

        if (documentDto.getimage() != null)
            setImage(new Image(documentDto.getimage()));
    }

    public void checkInfo(DocumentDto documentDto){
        if (documentDto.getTitle().trim().length() == 0 ){
            throw new TutorException(INVALID_TITLE_FOR_DOCUMENT);
        }
        if (documentDto.getType().trim().length() == 0){
            throw new TutorException(NO_TYPE);
        }

    }

    @Override
    public void accept(Visitor visitor) {

    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
        image.setDocument(this);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Document.Type getType() {
        return type;
    }

    public void setType(Document.Type type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Classroom getClassroom() {
        return classroom;
    }

    public void setClassroom(Classroom classroom) {
        this.classroom = classroom;
    }

    public void editDocument(DocumentDto documentDto){

        if (documentDto.getType().equals("DOC") && this.type==Type.DOC ){

            this.setContent(documentDto.getContent());

        }
        else if (documentDto.getType().equals("VIDEO") && this.type==Type.VIDEO){

            this.setUrl(documentDto.getUrl());
        }
        else
            throw new TutorException(NO_DOCUMENT_TYPE);
    }

    @Override
    public String toString() {
        return "Document{" +
                "id=" + id +
                ", type=" + type +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", url='" + url + '\'' +
                ", classroom=" + classroom +
                ", image=" + image +
                '}';
    }
}