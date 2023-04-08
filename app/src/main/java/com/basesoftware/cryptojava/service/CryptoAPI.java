package com.basesoftware.cryptojava.service;

import com.basesoftware.cryptojava.model.CryptoModel;
import java.util.List;
import io.reactivex.Observable;
import retrofit2.http.GET;

public interface CryptoAPI {

    @GET("atilsamancioglu/K21-JSONDataSet/master/crypto.json")
    Observable<List<CryptoModel>> getCryptoData();

}
