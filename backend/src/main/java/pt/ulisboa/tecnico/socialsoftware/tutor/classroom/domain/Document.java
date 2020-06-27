package pt.ulisboa.tecnico.socialsoftware.tutor.classroom.domain;

import groovy.lang.GString;
import pt.ulisboa.tecnico.socialsoftware.tutor.classroom.dto.DocumentDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.DomainEntity;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;

import javax.persistence.*;


import java.util.List;

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

    @Column(name = "pdf")
    private byte[] pdf;

    @Column(name = "extension")
    private String extension;

    public Document(){
    }

    public Document(DocumentDto documentDto) {
        checkInfo(documentDto);
        this.type = Document.Type.valueOf(documentDto.getType());
        this.title = documentDto.getTitle();

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

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public byte[] getPdf() {
        return pdf;
    }

    public void setPdf(byte[] pdf) {
        this.pdf = pdf;
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

            String extensionComplete = documentDto.getExtension();
            String[] parts = extensionComplete.split("\\.");

            this.setContent(documentDto.getContent());

            this.setExtension(parts[parts.length-1]);

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
                '}';
    }
}