package com.aispeech.aios.adapter.localScanService.bean;

/**
 * @desc 请在此添加类描述
 * @auth AISPEECH
 * @date 2016-03-18
 * @copyright aispeech.com
 */
public class Folder {

    private String path;
    private boolean flag = false;

    public Folder(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }
}
