package com.basesoftware.cryptojava;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.room.Room;
import android.os.Bundle;
import android.widget.Toast;
import com.basesoftware.cryptojava.adapter.CryptoRecyclerAdapter;
import com.basesoftware.cryptojava.databinding.ActivityMainBinding;
import com.basesoftware.cryptojava.model.CryptoModel;
import java.util.ArrayList;
import java.util.List;
import com.basesoftware.cryptojava.model.CryptoRecyclerModel;
import com.basesoftware.cryptojava.roomdb.CryptoDao;
import com.basesoftware.cryptojava.roomdb.CryptoDatabase;
import com.basesoftware.cryptojava.service.CryptoAPI;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * <h2>CryptoJava - JAVA Language</h2>
 * <hr>
 *
 * <ul>
 * <li>RxJAVA</li>
 * <li>Room</li>
 * <li>Retrofit</li>
 * <li>ViewBinding</li>
 * <li>DataBinding</li>
 * </ul>
 */

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    private ArrayList<CryptoRecyclerModel> arrayCrypto;

    private CryptoRecyclerAdapter cryptoRecyclerAdapter;

    private final String BASE_URL = "https://raw.githubusercontent.com/";

    private CryptoAPI cryptoAPI;
    private CompositeDisposable compositeDisposable;

    private CryptoDao cryptoDao;

    private Thread threadSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init(); // Değişkenler initialize ediliyor

        checkDbData(); // Veritabanı kontrol ediliyor

    }

    public void init() {

        CryptoDatabase cryptoDatabase = Room.databaseBuilder(getApplicationContext(), CryptoDatabase.class, "CryptoDB").allowMainThreadQueries().build(); // Veritabanı bağlantısı

        cryptoDao = cryptoDatabase.cryptoDao(); // Dao tanımlandı

        arrayCrypto = new ArrayList<>(); // Adaptöre verilecek liste oluşturuldu

        cryptoRecyclerAdapter = new CryptoRecyclerAdapter(); // Adaptör oluşturuldu

        compositeDisposable = new CompositeDisposable(); // CompositeDisposable oluşturuldu

        Gson gson = new GsonBuilder().setLenient().create(); // GsonBuilder oluşturuldu

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        cryptoAPI = retrofit.create(CryptoAPI.class); // Api sınıfı oluşturuldu

        binding.recyclerCrypto.setHasFixedSize(true); // Recyclerview boyutunun değişmeyeceği bildirildi [performans artışı]
        binding.recyclerCrypto.setLayoutManager(new LinearLayoutManager(this)); // Row için layout seçildi
        binding.recyclerCrypto.setAdapter(cryptoRecyclerAdapter); // Adaptör bağlandı

        binding.swipeLayout.setEnabled(false); // Swipe kapalı [DEFAULT]
        binding.swipeLayout.setOnRefreshListener(() -> {

            binding.swipeLayout.setEnabled(false); // Swipe kapatıldı
            binding.swipeLayout.setRefreshing(false); // Refresh animasyonu kapatıldı

            // Şuanki data durumlarına göre işlem yapılıyor

            if(arrayCrypto.isEmpty()) checkDataFromApi(); // Eğer hiç veri yok ise API denemesi yap

            else {

                if(!cryptoRecyclerAdapter.getItem(0).isApiData) checkDataFromApi(); // Eğer veri var ise ve veritabanı verisi ise API denemesi yap

                else {

                    Snackbar.make(binding.getRoot(),"Şuan API verileri kullanılmaktadır", Snackbar.LENGTH_SHORT).show(); // API UI bilgilendirme

                    binding.swipeLayout.setEnabled(true); // Swipe açıldı

                }

            }

        });

    }


    public void checkDbData() {

        compositeDisposable.clear(); // CompositeDisposable temizlendi

        compositeDisposable.add(cryptoDao.getAllData() // Veritabanından List<CryptoModel> döndürmesi gereken observable fonksiyon
                .subscribeOn(Schedulers.io()) // I/O thread kullanıldı
                .observeOn(AndroidSchedulers.mainThread()) // UI gösterim
                .subscribe(this::getDbData)); // Dönen veriyi kontrol etmek için referans verilmiş method

    }

    public void getDbData(List<CryptoModel> cryptoList) {

        compositeDisposable.clear();

        Snackbar.make(binding.getRoot(), (!cryptoList.isEmpty()) ? "Veritabanı verileri getiriliyor" : "Veritabanında veri yok, API bağlantısı deneniyor", Snackbar.LENGTH_SHORT)
                .addCallback(new Snackbar.Callback(){
                    @Override
                    public void onDismissed(Snackbar transientBottomBar, int event) {
                        super.onDismissed(transientBottomBar, event);

                        if(!cryptoList.isEmpty()) showData(cryptoList, false); // Veritabanında veri var [liste boş değil], veriyi göster

                        else checkDataFromApi(); // Veritabanında veri yok [liste boş], API verisi kontrol ediliyor
                    }
                }).show();

    }



    public void checkDataFromApi() {

        compositeDisposable.clear(); // CompositeDisposable temizlendi

        compositeDisposable.add(cryptoAPI.getCryptoData() // Api'den List<CryptoModel> döndürmesi gereken observable fonksiyon
                .subscribeOn(Schedulers.io()) // I/O thread kullanıldı
                .observeOn(AndroidSchedulers.mainThread()) // UI gösterim
                .onErrorResumeNext(Observable.just(new ArrayList<>())) // Eğer hata oluşursa
                .subscribe(this::getDataFromApi)); // Dönen veriyi kontrol etmek için referans verilmiş method

    }

    public void getDataFromApi(List<CryptoModel> cryptoList) {

        compositeDisposable.clear(); // CompositeDisposable temizlendi

        if (!cryptoList.isEmpty()) {

            Snackbar
                    .make(binding.getRoot(), "API verisi alındı, veritabanına yazılsın mı ?", Snackbar.LENGTH_SHORT)
                    .setAction("EVET", v -> saveDbDataFromApi(cryptoList)) // API sonrası veriler veritabanına yazılıyor
                    .show();

            showData(cryptoList, true); // Veriyi göster

        }

        else {

            Snackbar.make(binding.getRoot(), "API verisi bulunamadı, refresh deneyin", Snackbar.LENGTH_SHORT).show(); // UI bilgilendirme

            binding.swipeLayout.setEnabled(true); // Swipe açıldı

        }

    }

    public void showData(List<CryptoModel> cryptoList, Boolean isFromApi) {

        // Veriler RecyclerView'da gösteriliyor

        arrayCrypto.clear();

        for (int i = 0; i < cryptoList.size(); i++) {

            arrayCrypto.add( new CryptoRecyclerModel(cryptoList.get(i).currency, cryptoList.get(i).price, isFromApi) ); // Yeni modeller ile liste hazırlanıyor

            if(i == (cryptoList.size() - 1) ) {
                cryptoRecyclerAdapter.updateData(arrayCrypto); // RecyclerView'a data gönder DiffUtil kullanarak

                binding.swipeLayout.setEnabled(true); // Swipe yapma izni verildi
            }
        }

    }

    public void saveDbDataFromApi(List<CryptoModel> cryptoList) {

        // Yeni thread oluşturuldu UI işlemlerini kitlememek için

        threadSave = new Thread(() -> {

            for (int i = 0; i < cryptoList.size(); i++) {

                cryptoDao.insert(cryptoList.get(i))
                        .subscribeOn(Schedulers.io()) // I/O Thread işlemi
                        .observeOn(AndroidSchedulers.mainThread()) // Main Thread gözlemi
                        .subscribe(); // Takip

                if(i == (cryptoList.size() - 1)) {
                    Snackbar.make(binding.getRoot(), "API verileri veritabanına yazıldı", Toast.LENGTH_SHORT).show(); // UI bilgilendirme
                    threadSave.interrupt(); // Thread kesildi
                }

            }

        });

        threadSave.start(); // Thread başlatıldı

    }

}