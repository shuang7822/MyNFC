package com.wshuang.mynfc.base;

import org.litepal.crud.DataSupport;

import java.util.Date;

public class Cards extends DataSupport {

    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCardID() {
        return CardID;
    }

    public void setCardID(String cardID) {
        CardID = cardID;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getFltNr() {
        return FltNr;
    }

    public void setFltNr(String fltNr) {
        FltNr = fltNr;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    private Date operationtime;
    private String CardID;
    private String Date;
    private String FltNr;
    private String UserID;

    public Date getOperationtime() {
        return operationtime;
    }

    public void setOperationtime(Date operationtime) {
        this.operationtime = operationtime;
    }

    public String getOperate() {
        return operate;
    }

    public void setOperate(String operate) {
        this.operate = operate;
    }

    private String operate;


}
