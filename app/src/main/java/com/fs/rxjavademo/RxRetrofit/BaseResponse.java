package com.fs.rxjavademo.RxRetrofit;


public class BaseResponse<T> {

    public static final int CODE = 10000;


    /**
     * c : 10000
     * m : get the data
     * d : data sample
     */

    private int c;
    private String m;
    private T data;

    private String originDataS;

    public boolean isOk() {
        return c == CODE;
    }

    public int getC() {
        return c;
    }

    public void setC(int c) {
        this.c = c;
    }

    public String getM() {
        return m;
    }

    public void setM(String m) {
        this.m = m;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public void setOriginDataS(String originDataS) {
        this.originDataS = originDataS;
    }

    public String getOriginDataS() {
        return this.originDataS;
    }
}
