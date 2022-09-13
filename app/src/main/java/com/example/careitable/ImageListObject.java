package com.example.careitable;

public class ImageListObject {

    private String pid;
    private int pos;

    public ImageListObject(String pid, int pos ) {
        this.pid = pid;
        this.pos = pos;
    }

    public String getPid() {return pid;}

    public int getPos() {return pos;}

}