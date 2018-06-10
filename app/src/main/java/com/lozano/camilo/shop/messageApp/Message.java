package com.lozano.camilo.shop.messageApp;

public class Message {
    String msg;
    String nameMsg;
    String hour;
    boolean typemsg;

    public Message(String msg, String nameMsg, String hour, boolean typemsg) {
        this.msg = msg;
        this.nameMsg = nameMsg;
        this.hour = hour;
        this.typemsg = typemsg;
    }

    public String getMsg() {
        return msg;
    }

    public String getNameMsg() {
        return nameMsg;
    }

    public String getHour() {
        return hour;
    }

    public boolean isTypemsg() {
        return typemsg;
    }
}
