package com.example.diplom.Products;

import java.util.Date;

public class Abonement {
    private int id;
    private int nominal;
    private Date dateOfUse;
    private int balance;
    private Date dateOfBuy;
    private Date dateOfEnd;
    private Date dateOfRes;
    private String status;
    private int idClient;

    // конструкторы, геттеры и сеттеры

    public Abonement() {
        // пустой конструктор
    }

    public Abonement(int id, int nominal, Date dateOfUse, int balance, Date dateOfBuy, Date dateOfEnd, Date dateOfRes, String status, int idClient) {
        this.id = id;
        this.nominal = nominal;
        this.dateOfUse = dateOfUse;
        this.balance = balance;
        this.dateOfBuy = dateOfBuy;
        this.dateOfEnd = dateOfEnd;
        this.dateOfRes = dateOfRes;
        this.status = status;
        this.idClient = idClient;
    }

    // геттеры и сеттеры для каждого поля

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNominal() {
        return nominal;
    }

    public void setNominal(int nominal) {
        this.nominal = nominal;
    }

    public Date getDateOfUse() {
        return dateOfUse;
    }

    public void setDateOfUse(Date dateOfUse) {
        this.dateOfUse = dateOfUse;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public Date getDateOfBuy() {
        return dateOfBuy;
    }

    public void setDateOfBuy(Date dateOfBuy) {
        this.dateOfBuy = dateOfBuy;
    }

    public Date getDateOfEnd() {
        return dateOfEnd;
    }

    public void setDateOfEnd(Date dateOfEnd) {
        this.dateOfEnd = dateOfEnd;
    }

    public Date getDateOfRes() {
        return dateOfRes;
    }

    public void setDateOfRes(Date dateOfRes) {
        this.dateOfRes = dateOfRes;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getIdClient() {
        return idClient;
    }

    public void setIdClient(int idClient) {
        this.idClient = idClient;
    }
}
