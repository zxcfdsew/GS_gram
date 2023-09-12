package com.sample.gs_gram.Data;

import java.io.Serializable;
import java.util.ArrayList;

public class SubjectData implements Serializable {
    private String subject;
    private String credit;
    private String grade;
    private String term;
    private String field;
    private String code;
    private String divition;
    private String year;
    private String major;
    private String department;
    private ArrayList<String> subjectList;


    public String getSubject() {return subject;}

    public void setSubject(String subject) {this.subject = subject;}

    public String getCredit() {return credit;}

    public void setCredit(String credit) {this.credit = credit;}


    public String getTerm() {return term;}

    public void setTerm(String term) {this.term = term;}

    public String getField() {return field;}

    public void setField(String field) {this.field = field;}

    public String getDivition() {
        return divition;
    }

    public void setDivition(String divition) {
        this.divition = divition;
    }

    public String getYear() {return year;}

    public void setYear(String year) {this.year = year;}

    public ArrayList<String> getSubjectList() {return subjectList;}

    public void setSubjectList(ArrayList<String> subjectList) {this.subjectList = subjectList;}

    public String getCode() {return code;}

    public void setCode(String code) {this.code = code;}

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getMajor() {return major;}

    public void setMajor(String major) {this.major = major;}

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }
}
