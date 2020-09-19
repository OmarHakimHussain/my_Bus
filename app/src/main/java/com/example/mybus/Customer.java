package com.example.mybus;

public class Customer {
    private String Cname,linename,num;
    private int img,imgCall;





    public Customer(String cname, String linename, String num, int img, int imgCall) {
        this.Cname = cname;
        this.linename = linename;
        this.num = num;
        this.img = img;
        this.imgCall = imgCall;
    }



    public String getCname() {
        return Cname;
    }

    public void setCname(String cname) {
        Cname = cname;
    }

    public String getLinename() {
        return linename;
    }

    public void setLinename(String linename) {
        this.linename = linename;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }

    public int getImgCall() {
        return imgCall;
    }

    public void setImgCall(int imgCall) {
        this.imgCall = imgCall;
    }
}
