package com.example.dongvan.zingmp3clone.model;

/**
 * Created by VoNga on 28-Apr-17.
 */

public class model_off {
    private String hinh,ten,soluong;

    public String getHinh() {
        return hinh;
    }

    public void setHinh(String hinh) {
        this.hinh = hinh;
    }

    public String getTen() {
        return ten;
    }

    public void setTen(String ten) {
        this.ten = ten;
    }

    public String getSoluong() {
        return soluong;
    }

    public void setSoluong(String soluong) {
        this.soluong = soluong;
    }

    public model_off(String hinh, String ten, String soluong) {
        this.hinh = hinh;
        this.ten = ten;
        this.soluong = soluong;
    }
}
