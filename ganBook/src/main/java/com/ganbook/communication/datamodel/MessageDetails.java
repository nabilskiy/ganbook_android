package com.ganbook.communication.datamodel;

import com.ganbook.user.User;
import com.ganbook.utils.DateFormatter;

public class MessageDetails {
	public String message_id; //": "15148",
    public String message_text; //": "message from PTA:\nVaad2",
    public String message_date; //": "2015-01-29 13:22:14",
    public String user_id; //": "16100",
    public String user_type; //": "4",
    public String user_first_name; //": "P900",
    public String user_last_name; //": "",
    public String views; //": "1"
    
    public static MessageDetails createNewMessage(String newMsgText) {
    	MessageDetails msg = new MessageDetails();
    	msg.message_text = newMsgText;
    	msg.views = "0";
    	msg.message_date = DateFormatter.getCurrent_In_Message_Format(); 
    	return msg;
	}
    
    public int numViewed() {
    	if (views==null) {
    		return 0;
    	}
    	try { 
    		int num = Integer.parseInt(views);
    		return num;
    	} catch (Exception e) {
    		return 0;
    	}
    	
    }
}
