package com.basesoftware.cryptojava.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.basesoftware.cryptojava.R;
import com.basesoftware.cryptojava.databinding.RowCoinBinding;
import com.basesoftware.cryptojava.model.CryptoRecyclerModel;

import java.util.ArrayList;

public class CryptoRecyclerAdapter extends RecyclerView.Adapter<CryptoRecyclerAdapter.RowHolder> {

    private final AsyncListDiffer<CryptoRecyclerModel> mDiffer;

    public CryptoRecyclerAdapter() {

        DiffUtil.ItemCallback<CryptoRecyclerModel> diffCallBack = new DiffUtil.ItemCallback<CryptoRecyclerModel>() {

            @Override
            public boolean areItemsTheSame(CryptoRecyclerModel oldItem, CryptoRecyclerModel newItem) {
                return oldItem.currency.matches(newItem.currency); // Eğer currency'ler aynı ise aynı item var (notifyItemInserted gerekmiyor)
            }

            @Override
            public boolean areContentsTheSame(CryptoRecyclerModel oldItem, CryptoRecyclerModel newItem) {
                return oldItem.currency.matches(newItem.currency) &&
                        oldItem.price.matches(newItem.price) &&
                        oldItem.isApiData.equals(newItem.isApiData);
                // Eğer currency/price/isApiData verileri aynı ise güncelleme gerekmiyor (notifyItemChanged) [aksi durumda güncelle]
            }
        };
        mDiffer = new AsyncListDiffer<>(this, diffCallBack); // AsyncListDiffer oluşturuldu
    }

    @NonNull
    @Override
    public RowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RowCoinBinding rowCoinBinding = RowCoinBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false); // Binding bağlandı
        return new RowHolder(rowCoinBinding); // ViewHolder sınıfı oluşturuldu
    }

    @Override
    public void onBindViewHolder(@NonNull RowHolder holder, int position) {

        holder.rowCoinBinding.setCrypto(mDiffer.getCurrentList().get(holder.getBindingAdapterPosition())); // Databinding verisi XML'e verildi

        // isApiData durumuna göre renk belirlendi [Yeşil/Kırmızı]
        holder.rowCoinBinding.txtDataStatus.setTextColor(
                holder.itemView.getContext().getResources().getColor((mDiffer.getCurrentList().get(holder.getBindingAdapterPosition()).isApiData) ? R.color.green : R.color.red)
        );

    }

    public void updateData(ArrayList<CryptoRecyclerModel> arrayNewCrypto) {

        mDiffer.submitList(arrayNewCrypto); // AsyncListDiffer'a yeni liste verildi

    }

    @Override
    public int getItemCount() { return mDiffer.getCurrentList().size(); } // RecyclerView'da gösterilecek item sayısı

    public CryptoRecyclerModel getItem(int index) { return mDiffer.getCurrentList().get(index); } // Item sorgulama açıldı

    protected class RowHolder extends RecyclerView.ViewHolder {

        RowCoinBinding rowCoinBinding; // Yer tutucu (ViewHolder) binding

        public RowHolder(RowCoinBinding rowCoinBinding) {
            super(rowCoinBinding.getRoot());
            this.rowCoinBinding = rowCoinBinding;
        } // Yer tutucu (ViewHolder) constructor
    }

}
