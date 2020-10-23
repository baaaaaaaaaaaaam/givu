package com.login.donation.Object;

import java.util.ArrayList;

public class campaign {
    String seq;
    String writer;
    String subject;
    String imagePath;
    String startDate;
    String endDate;
    String collection;
    String content;
    String doing;

    // 서버에서 json으로 받은 donation_list를 담기위한 변수이다 담은 변수이다
    String list;

    // 이중 리사이클러뷰를 사용하기 위한 변수이다
    ArrayList<Image_and_id_object> Image_and_id_object;

    public campaign(String seq, String writer, String subject, String imagePath, String startDate, String endDate, String collection, String content, ArrayList<Image_and_id_object> Image_and_id_object, String doing) {
        this.seq=seq;
        this.writer = writer;
        this.subject = subject;
        this.imagePath = imagePath;
        this.startDate = startDate;
        this.endDate = endDate;
        this.collection = collection;
        this.content=content;
        this.Image_and_id_object = Image_and_id_object;
        this.doing=doing;
    }
    public campaign(String seq,String writer, String subject, String imagePath, String startDate, String endDate, String collection,
                    String content,String doing) {
        this.seq=seq;
        this.writer = writer;
        this.subject = subject;
        this.imagePath = imagePath;
        this.startDate = startDate;
        this.endDate = endDate;
        this.collection = collection;
        this.content=content;
        this.doing=doing;
    }


    public String getWriter() {
        return writer;
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getCollection() {
        return collection;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

    public String getList() {
        return list;
    }

    public void setList(String list) {
        this.list = list;
    }

    public ArrayList<Image_and_id_object> getImage_and_id_object() {
        return Image_and_id_object;
    }

    public void setImage_and_id_object(ArrayList<Image_and_id_object> image_and_id_object) {
        this.Image_and_id_object = image_and_id_object;
    }

    public String getSeq() {
        return seq;
    }

    public void setSeq(String seq) {
        this.seq = seq;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }


    public String getDoing() {
        return doing;
    }
}
