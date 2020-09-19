package com.example.mybus;

public class Lines {
    private String Line,p1,p2 ;
    private  Boolean expandable;

    public Lines() {
    }

    public Lines(String line, String p1, String p2, Boolean expandable) {
        Line = line;
        this.p1 = p1;
        this.p2 = p2;
        this.expandable = expandable;
    }

    public String getLine() {
        return Line;
    }

    public void setLine(String line) {
        Line = line;
    }

    public String getP1() {
        return p1;
    }

    public void setP1(String p1) {
        this.p1 = p1;
    }

    public String getP2() {
        return p2;
    }

    public void setP2(String p2) {
        this.p2 = p2;
    }

    public Boolean Expandable() {
        return expandable;
    }

    public void setExpandable(Boolean expandable) {
        this.expandable = expandable;
    }
}
