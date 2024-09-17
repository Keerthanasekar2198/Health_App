package com.android.health.data;

public class RecordFile {

    private String file_url,file_name,upload_time;

    public RecordFile(){}

    public RecordFile(String url,String name,String time){
        this.file_url = url;
        this.file_name = name;
        this.upload_time = time;
    }

    public String getFile_name() {
        return file_name;
    }

    public String getFile_url() {
        return file_url;
    }

    public String getUpload_time() {
        return upload_time;
    }
}
