package com.android.health.data;

public class Appointment {

    private String p_name,p_number,p_id,a_date,a_time,a_status;

    public Appointment(){}

    public Appointment(String name,String number,String id,String date,String time,String status){
        this.p_name = name;
        this.p_number = number;
        this.p_id = id;
        this.a_date = date;
        this.a_time = time;
        this.a_status = status;
    }

    public String getP_name() {
        return p_name;
    }

    public String getP_number() {
        return p_number;
    }

    public String getP_id() {
        return p_id;
    }

    public String getA_time() {
        return a_time;
    }

    public String getA_date() {
        return a_date;
    }

    public String getA_status() {
        return a_status;
    }
}
