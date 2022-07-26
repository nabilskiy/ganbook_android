package com.ganbook.interfaces;

import com.ganbook.models.MessageModel;

/**
 * Created by dmytro_vodnik on 7/8/16.
 * working on ganbook1 project
 */
public interface MessagesFragmentInterface {

    int getTotalActiveParents();

    void editMessage(MessageModel messageModel, int position);
}
