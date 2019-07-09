package com.nuist.find;

public class Question {
    private String title;
    private String msg;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "Question [title=" + title + ", msg=" + msg + "]";
    }
}
