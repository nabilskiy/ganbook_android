package com.ganbook.adapters;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ganbook.activities.UserAttachmentsActivity;
import com.ganbook.dialogs.Dialogs;
import com.ganbook.models.UserAttachmentModel;
import com.ganbook.user.User;
import com.project.ganim.R;

import java.util.List;

public class UserAttachmentsListAdapter extends ArrayAdapter<UserAttachmentModel>  {

    private List<UserAttachmentModel> userAttachmentModelList;
    private Context context;

    public UserAttachmentsListAdapter(@NonNull Context context, List<UserAttachmentModel> userAttachmentModelList) {
        super(context, R.layout.user_attachments_layout, userAttachmentModelList);
        this.context = context;
        this.userAttachmentModelList = userAttachmentModelList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;
        final UserAttachmentModel userAttachmentModel = getItem(position);
        assert userAttachmentModel != null;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.user_attachments_list_item, parent, false);
            viewHolder.fileName = convertView.findViewById(R.id.attachmentFileName);
            viewHolder.attachmentTypeImage = convertView.findViewById(R.id.attachmentTypeImage);
            viewHolder.deleteFileImage = convertView.findViewById(R.id.deleteAttachmentFile);
            viewHolder.attachmentFileDescription = convertView.findViewById(R.id.attachmentFileDescription);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if(User.isParent() || User.isStaff()) {
            viewHolder.deleteFileImage.setVisibility(View.GONE);
        }

        viewHolder.fileName.setText(userAttachmentModel.getDocumentTitle());
        viewHolder.attachmentFileDescription.setText(userAttachmentModel.getDocumentDescription());
        viewHolder.deleteFileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialogs.successDialogWithButton(context, context.getString(R.string.delete_document_string), context.getString(R.string.are_you_sure_string), context.getString(R.string.ok), context.getString(R.string.cancel), new Dialogs.ButtonDialogInterface() {
                    @Override
                    public void onButtonOkClicked() {
                        ((UserAttachmentsActivity) context).deleteDocument(userAttachmentModel.getDocumentId());

                    }
                });
            }
        });

        return convertView;
    }

    private class ViewHolder {
        TextView fileName, attachmentFileDescription;
        ImageView attachmentTypeImage;
        ImageView deleteFileImage;
    }
}
