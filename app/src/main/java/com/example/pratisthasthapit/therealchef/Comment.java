package com.example.pratisthasthapit.therealchef;

public class Comment {

    private String comment;
    private String chef;
    private String commentId;


    public Comment(String comment, String chef, String commentId) {
        this.comment = comment;
        this.chef = chef;
        this.commentId = commentId;
    }

    public Comment() {
    }

    /**
     * Getters and setters
     * @return: returns the corresponding values.
     */
    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getChef() {
        return chef;
    }

    public void setChef(String chef) {
        this.chef = chef;
    }
}
