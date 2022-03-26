package com.example.bookapp.models;

public class Interest {
    public int id;
    public String name;
    public boolean isSelected=false;
    private int categID;
    public Interest() {
    }

    public Interest(String name, int categID, boolean isSelected) {
        this.name = name;
        this.isSelected = isSelected;
        this.categID = categID;
    }

    public int getCategID() {
        return categID;
    }

    public void setCategID(int categID) {
        this.categID = categID;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
