package com.sundy.boot.inventory.domain;


import java.util.List;

public class Freq {
    private Rate rate;
    private List<Item> itemList;

    public Freq(Rate rate, List<Item> itemList) {
        this.rate = rate;
        this.itemList = itemList;
    }

    public Rate getRate() {
        return rate;
    }

    public void setRate(Rate rate) {
        this.rate = rate;
    }

    public List<Item> getItemList() {
        return itemList;
    }

    public void setItemList(List<Item> itemList) {
        this.itemList = itemList;
    }
}
