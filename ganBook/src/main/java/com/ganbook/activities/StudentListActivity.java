package com.ganbook.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import com.ganbook.adapters.StudentListAdapter;
import com.ganbook.models.StudentModel;
import com.ganbook.user.User;
import com.project.ganim.R;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StudentListActivity extends BaseAppCompatActivity {

    private StudentListAdapter studentListAdapter;
    private ListView studentListView;
    private ProgressBar progressBar;
    private List<StudentModel> studentModelList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.student_list_layout);
        setActionBar(getString(R.string.student_list_text), true);

        studentListView = findViewById(R.id.studentList);
        progressBar = findViewById(R.id.studentListProgress);

        getStudentList();


        studentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(StudentListActivity.this, SingleStudentInfo.class);
                intent.putExtra("studentInfo", studentModelList.get(i));
                startActivity(intent);
                Log.d("STUDENT", studentModelList.get(i).getStudentImage());
            }
        });
    }

    private void getStudentList() {
        progressBar.setVisibility(View.VISIBLE);
        Call<List<StudentModel>> call = ganbookApiInterfaceGET.getStudentList(User.current.getCurrentClassId());

        call.enqueue(new Callback<List<StudentModel>>() {
            @Override
            public void onResponse(Call<List<StudentModel>> call, Response<List<StudentModel>> response) {

                if(response.body() != null) {
                    studentModelList = response.body();
                    studentListAdapter = new StudentListAdapter(StudentListActivity.this, studentModelList);
                    studentListView.setAdapter(studentListAdapter);
                    progressBar.setVisibility(View.GONE);

                }

            }

            @Override
            public void onFailure(Call<List<StudentModel>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
