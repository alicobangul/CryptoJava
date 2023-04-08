package com.basesoftware.cryptojava.roomdb;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.basesoftware.cryptojava.model.CryptoModel;
import java.util.List;
import io.reactivex.Completable;
import io.reactivex.Observable;

@Dao
public interface CryptoDao {

    @Query("SELECT * FROM Crypto")
    Observable<List<CryptoModel>> getAllData();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insert(CryptoModel crypto);

}
