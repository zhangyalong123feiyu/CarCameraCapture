package com.lenovo.mutimodecamera;

import com.google.gson.annotations.SerializedName;

public class CameraDataBean {

    @SerializedName("in")
    private String in;
    @SerializedName("out")
    private String out;

    public String getIn() {
        return in;
    }

    public void setIn(String in) {
        this.in = in;
    }

    public String getOut() {
        return out;
    }

    public void setOut(String out) {
        this.out = out;
    }
}
