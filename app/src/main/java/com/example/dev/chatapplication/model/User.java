package com.example.dev.chatapplication.model;

import com.example.dev.chatapplication.tools.Status;

/**
 * Created by Dev on 1/17/2018.
 */

public class User {
    public String name;
    public String email;
    public String avata;
    public Status status;
    public Message2 message;


    public User(){
        status = new Status();
        message = new Message2();
        status.isOnline = false;
        status.timestamp = 0;
        message.idReceiver = "0";
        message.idSender = "0";
        message.text = "";
        message.image = "";
        message.timestamp = 0;
    }
}
