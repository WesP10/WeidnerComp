package com.example.application.data;

import com.vaadin.flow.server.StreamResource;
import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "images")
public class AnalyzedContent{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private Integer id;
    @Lob
    @Column(length = 1000000)
    private StreamResource img;
    private String name;
    private String attributes;
    private Date date;

    public StreamResource getImg() {
        return img;
    }
    public void setImg(StreamResource img) {
        this.img = img;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getAttributes() {
        return attributes;
    }
    public void setAttributes(String attributes) {
        this.attributes = attributes;
    }

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public Date getDate(){return date;}
    public void setDate(Date date){this.date=date;}
}
