package com.example.studentcoursebookingapp;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class ProductList extends ArrayAdapter<Course>{
    private Activity context;
    List<Course> courses;

    public ProductList(Activity context, List<Course> courses){
        super(context,R.layout.layout_course_list,courses);
        this.context=context;
        this.courses=courses;
    }

    public View getView(int position, View convertView, ViewGroup parent){
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.layout_course_list,
                null,true);

        TextView textViewName = (TextView) listViewItem.findViewById(R.id.textCourseName);
        TextView textViewCode = (TextView) listViewItem.findViewById(R.id.textCourseCode);

        Course course = courses.get(position);
        textViewName.setText(course.getCourseName());
        textViewCode.setText(course.getCourseCode());
        return listViewItem;

    }
}
