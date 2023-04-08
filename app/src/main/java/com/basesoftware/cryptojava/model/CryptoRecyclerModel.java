package com.basesoftware.cryptojava.model;

public class CryptoRecyclerModel {

    public String currency;

    public String price;

    public Boolean isApiData;

    public CryptoRecyclerModel(String currency, String price, Boolean isOnlineData) {
        this.currency = currency;
        this.price = price;
        this.isApiData = isOnlineData;
    }
}
