package com.ganbook.communication.json;

import com.ganbook.communication.datamodel.MessageDetails;

public class getmessage_Response extends BaseResponse {
    public String total_active_parents; //": "2",
    public MessageDetails[] messages;
    
//    public int getTotalNumber() {
//    	try { 
//    		return Integer.parseInt(total_active_parents);
//    	}
//    	catch (Exception e) {
//    		int jj=234;
//    		jj++;
//    		return 0;
//    	}
//    }
}