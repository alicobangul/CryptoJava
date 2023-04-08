package com.basesoftware.cryptojava.roomdb;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import com.basesoftware.cryptojava.model.CryptoModel;

@Database(entities = {CryptoModel.class}, version = 1)
public abstract class CryptoDatabase extends RoomDatabase {
    public abstract CryptoDao cryptoDao();
}
