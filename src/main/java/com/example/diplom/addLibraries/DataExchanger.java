package com.example.diplom.addLibraries;

public class DataExchanger {

    private static DataExchanger instance;

    private DataExchanger() {}

    public static DataExchanger getInstance() {
        if (instance == null) {
            instance = new DataExchanger();
        }
        return instance;
    }

    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
