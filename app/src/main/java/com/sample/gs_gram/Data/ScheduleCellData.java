package com.sample.gs_gram.Data;

public class ScheduleCellData {
    private int row;
    private int col;
    private int color;
    private int colorIndex;
    private String title;
    private String time;
    private String location;
    private String professorName;

    public ScheduleCellData(int row, int col, int color, int colorIndex, String title, String time, String location, String professorName) {
        this.row = row;
        this.col = col;
        this.color = color;
        this.colorIndex = colorIndex;
        this.title = title;
        this.time = time;
        this.location = location;
        this.professorName = professorName;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getColorIndex() {
        return colorIndex;
    }

    public void setColorIndex(int colorIndex) {
        this.colorIndex = colorIndex;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getProfessorName() {
        return professorName;
    }

    public void setProfessorName(String professorName) {
        this.professorName = professorName;
    }
}