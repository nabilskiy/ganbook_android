package com.ganbook.models;

import com.ganbook.communication.datamodel.ClassDetails;
import com.ganbook.communication.datamodel.GanDetails;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by dmytro_vodnik on 8/8/16.
 * working on ganbook1 project
 */
public class AddClassAnswer {

    @SerializedName("gan")
    public GanDetails gan;
    @SerializedName("classes")
    public List<ClassDetails> classes;

    @Override
    public String toString() {
        return "AddClassAnswer{" +
                "gan=" + gan +
                ", classes=" + classes +
                '}';
    }
}
