package com.ganbook.adapters;

import android.app.Activity;
import android.content.Context;

import androidx.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.Paint;

import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ganbook.activities.AttachmentWebView;
import com.ganbook.fragments.SingleMessageFragment;
import com.ganbook.interfaces.CRUDAdapterInterface;
import com.ganbook.interfaces.MessagesFragmentInterface;
import com.ganbook.models.MessageModel;
import com.ganbook.user.User;
import com.ganbook.utils.DateFormatter;
import com.ganbook.utils.FragmentUtils;
import com.project.ganim.R;
import com.project.ganim.databinding.ItemMessageBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dmytro_vodnik on 7/8/16.
 * working on ganbook1 project
 */
public class MessagesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements CRUDAdapterInterface<MessageModel> {

    private static final String TAG = MessagesAdapter.class.getName();
    Context context;
    LayoutInflater layoutInflater;
    List<MessageModel> messageModels;
    MessagesFragmentInterface messagesFragmentInterface;
    HashMap<String, String> attachmentshash;

    public MessagesAdapter(Context context, MessagesFragmentInterface messagesFragmentInterface) {

        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        messageModels = new ArrayList<>();
        this.messagesFragmentInterface = messagesFragmentInterface;
        attachmentshash = new HashMap<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        ItemMessageBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.item_message,
                parent, false);

        return new ViewHolderItem(binding);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        final MessageModel currentMessage = getItem(position);

        String total_number = String.valueOf(messagesFragmentInterface.getTotalActiveParents());

        String viewed = context.getResources().getString(
                R.string.viewed_str, currentMessage.getViews(), total_number);

        ((ViewHolderItem) holder).binding.setViewed(viewed);
        ((ViewHolderItem) holder).binding.setDate(DateFormatter.formatStringDate(currentMessage.getMessageDate(),"yyyy-MM-dd hh:mm","yyyy-MM-dd, HH:mm"));

        if(currentMessage.getMessageText().startsWith("s3.ganbook.co.il")) {
            ((ViewHolderItem) holder).binding.setMessage(currentMessage);
            String splittedMessage [] = currentMessage.getMessageText().split("/");
            attachmentshash.put("http://" + currentMessage.getMessageText(), splittedMessage[splittedMessage.length - 1]);
            currentMessage.setMessageText(splittedMessage[splittedMessage.length - 1]);
            ((ViewHolderItem) holder).binding.tvMessage.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
            ((ViewHolderItem) holder).binding.tvMessage.setTextColor(Color.WHITE);
            ((ViewHolderItem) holder).binding.tvMessage.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_file, 0, 0, 0);
        } else {
            ((ViewHolderItem) holder).binding.setMessage(currentMessage);
        }


        ((ViewHolderItem) holder).binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(((ViewHolderItem) holder).binding.tvMessage.getPaintFlags() == Paint.UNDERLINE_TEXT_FLAG) {
                    for (Map.Entry<String, String> entry : attachmentshash.entrySet()) {
                        String key = entry.getKey();
                        if (currentMessage.getMessageText().equals(entry.getValue())) {
                            AttachmentWebView.start((Activity) context, key);
                        }
                    }
                } else {
                    showItem(currentMessage, position);
                }

            }
        });
        ((ViewHolderItem) holder).binding.getRoot().setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                // Teacher only !!!
                if (User.isTeacher()) {

                    Log.d(TAG, "onLongClick: edit message " + currentMessage + " started");

                    messagesFragmentInterface.editMessage(currentMessage, position);
                }
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {

        if (messageModels != null)
            return messageModels.size();
        else
            return 0;
    }

    @Override
    public MessageModel getItem(int position) {
        return messageModels.get(position);
    }

    @Override
    public void addItem(MessageModel item) {

        messageModels.add(0, item);
        notifyDataSetChanged();
    }

    @Override
    public void addItem(MessageModel item, int index) {

    }

    @Override
    public void removeItem(MessageModel item) {

        messageModels.remove(item);
        notifyDataSetChanged();
    }

    @Override
    public void addItems(List<MessageModel> items) {

        messageModels.addAll(items);
        notifyDataSetChanged();
    }

    @Override
    public void clearList() {

        messageModels.clear();
        notifyDataSetChanged();
    }

    @Override
    public void updateItem(int pos, MessageModel item) {

        messageModels.set(pos, item);
        notifyItemChanged(pos);
    }

    @Override
    public void showItem(MessageModel item, int itemPosition) {

        if (User.isTeacher()) {

            SingleMessageFragment selMessage = SingleMessageFragment.newInstance(item);

            FragmentUtils.openFragment(selMessage, R.id.content_frame, SingleMessageFragment.TAG,
                    context, true);
        }
    }

    @Override
    public List<MessageModel> getItems() {
        return messageModels;
    }

    @Override
    public void removeItem(int currentItemPosition) {

        messageModels.remove(currentItemPosition);
        notifyDataSetChanged();
    }

    private class ViewHolderItem extends RecyclerView.ViewHolder {

        private final ItemMessageBinding binding;
        public TextView messageText;

        public ViewHolderItem(ItemMessageBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
