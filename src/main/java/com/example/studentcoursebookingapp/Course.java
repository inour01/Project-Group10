package com.example.studentcoursebookingapp;

public class Course {
    private String _id;
    private String _courseName;
    private String _courseCode;

    public Course(){
    }

    public Course(String id, String courseName, String courseCode){
        _id = id;
        _courseName = courseName;
        _courseCode = courseCode;
    }

    public Course(String courseName, String courseCode){
        _courseName = courseName;
        _courseCode = courseCode;
    }

    public void setId(String id){_id = id;}
    public String getId(){return _id;}
    public void setCourseName(String courseName){_courseName = courseName;}
    public String getCourseName(){return _courseName;}
    public void setCourseCode(String courseCode){_courseCode = courseCode;}
    public String getCourseCode(){return _courseCode;}
}
