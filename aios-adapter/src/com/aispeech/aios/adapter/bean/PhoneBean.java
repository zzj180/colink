package com.aispeech.aios.adapter.bean;

/**
 * Created by Spring on 2016/3/3.
 * to do:
 */
public class PhoneBean extends BaseBean {
    private String name;
    private String number;
    private String title;
    private String address;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
