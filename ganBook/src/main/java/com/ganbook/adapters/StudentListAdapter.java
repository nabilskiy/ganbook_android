package com.ganbook.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ganbook.models.StudentModel;
import com.ganbook.ui.CircleImageView;
import com.project.ganim.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class StudentListAdapter extends ArrayAdapter<StudentModel> {

    private List<StudentModel> studentModelList;
    private Context context;

    public StudentListAdapter(@NonNull Context context, List<StudentModel> studentModelList) {
        super(context, R.layout.student_list_inflater, studentModelList);
        this.context = context;
        this.studentModelList = studentModelList;
    }

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;
        StudentModel studentModel = getItem(position);
        assert studentModel != null;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.student_list_inflater, parent, false);
            viewHolder.studentName = convertView.findViewById(R.id.studentName);
            viewHolder.studentImage = convertView.findViewById(R.id.studentImage);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Picasso.with(context)
                .load("http://s3.ganbook.co.il/ImageStore/users/" + studentModel.getStudentImage() + ".png")
                .into(viewHolder.studentImage);
        viewHolder.studentName.setText(replaceNullString(studentModel.getStudentFirstName()) + " " + replaceNullString(studentModel.getStudentLastName()));
        return convertView;
    }


    private String replaceNullString(String input) {
        return input == null ? "" : input;
    }

    private class ViewHolder {
        TextView studentName;
        CircleImageView studentImage;
    }
}
