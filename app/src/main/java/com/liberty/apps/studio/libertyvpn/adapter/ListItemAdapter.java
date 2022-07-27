package com.liberty.apps.studio.libertyvpn.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RawRes;
import androidx.recyclerview.widget.RecyclerView;

import com.liberty.apps.studio.libertyvpn.R;
import com.liberty.apps.studio.libertyvpn.databinding.HomeListItemBinding;

import java.util.ArrayList;

public class ListItemAdapter extends RecyclerView.Adapter<ListItemAdapter.VH> {

    ArrayList<Integer> list = new ArrayList();

    public ListItemAdapter() {
        list.add(R.raw.c3);
        list.add(R.raw.c2);
        list.add(R.raw.c1);
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new VH(HomeListItemBinding.inflate(inflater, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        holder.setData(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class VH extends RecyclerView.ViewHolder {
        HomeListItemBinding binding;

        public VH(HomeListItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void setData(@RawRes int id) {
            binding.getRoot().setAnimation(id);
            //Lottie'ye animasyonunu ver.
        }
    }
}
