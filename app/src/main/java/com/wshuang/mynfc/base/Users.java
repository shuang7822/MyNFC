package com.wshuang.mynfc.base;

import org.litepal.crud.DataSupport;

import java.time.LocalDateTime;

public class Users extends DataSupport {

    private int id;

    private String employeeID;

    private String name;

    private String psw;

    private String station;

    private LocalDateTime logintime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmployeeID() {
        return employeeID;
    }

    public void setEmployeeID(String employeeID) {
        this.employeeID = employeeID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPsw() {
        return psw;
    }

    public void setPsw(String psw) {
        this.psw = psw;
    }

    public String getStation() {
        return station;
    }

    public void setStation(String station) {
        this.station = station;
    }

    public LocalDateTime getLogintime() {
        return logintime;
    }

    public void setLogintime(LocalDateTime logintime) {
        this.logintime = logintime;
    }
}
