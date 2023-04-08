package com.basesoftware.cryptojava.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "Crypto")
public class CryptoModel {

    @PrimaryKey()
    @ColumnInfo(name = "currency")
    @SerializedName("currency")
    @NonNull public String currency;

    @ColumnInfo(name = "price")
    @SerializedName("price")
    public String price;

}
