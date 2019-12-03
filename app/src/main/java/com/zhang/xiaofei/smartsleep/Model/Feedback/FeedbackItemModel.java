package com.zhang.xiaofei.smartsleep.Model.Feedback;

public class FeedbackItemModel {
    String ideaId;
    String userId;
    String userName;
    String content;
    int type;
    FeedbackItemImageModel[] img;

    public String getIdeaId() {
        return ideaId;
    }

    public void setIdeaId(String ideaId) {
        this.ideaId = ideaId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public FeedbackItemImageModel[] getImg() {
        return img;
    }

    public void setImg(FeedbackItemImageModel[] img) {
        this.img = img;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
