package com.ganbook.models;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.library.baseAdapters.BR;

import com.ganbook.interfaces.RecyclerViewItem;

/**
 * Created by dmytro_vodnik on 8/1/16.
 * working on ganbook1 project
 */
public class AlbumsYearModel extends BaseObservable implements RecyclerViewItem {

    public String getClassName() {
        return className;
    }

    private final String className;

    public String getGanName() {
        return ganName;
    }

    private final String ganName;

    public void setSelected(boolean selected) {
        this.selected = selected;
        notifyPropertyChanged(BR.selected);
    }

    @Bindable
    private boolean selected;

    public String getGanId() {
        return ganId;
    }

    private final String ganId;
    String year;
    String classId;

    public AlbumsYearModel(String year, String classId, String ganId, String gan_name, String class_name) {
        this.year = year;
        this.classId = classId;
        this.ganId = ganId;
        this.selected = false;
        this.ganName = gan_name;
        this.className = class_name;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public boolean isSelected() {
        return selected;
    }

    @Override
    public String toString() {
        return "AlbumsYearModel{" +
                "className='" + className + '\'' +
                ", ganName='" + ganName + '\'' +
                ", selected=" + selected +
                ", ganId='" + ganId + '\'' +
                ", year='" + year + '\'' +
                ", classId='" + classId + '\'' +
                '}';
    }
}
