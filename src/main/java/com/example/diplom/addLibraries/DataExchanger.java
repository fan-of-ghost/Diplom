package com.example.diplom.addLibraries;

import java.time.LocalDate;

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

    private LocalDate date;

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
