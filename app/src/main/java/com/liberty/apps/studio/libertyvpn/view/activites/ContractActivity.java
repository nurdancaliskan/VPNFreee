package com.liberty.apps.studio.libertyvpn.view.activites;


import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.liberty.apps.studio.libertyvpn.databinding.ContractBinding;

public class ContractActivity extends AppCompatActivity {

    ContractBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ;
        binding = ContractBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        binding.accept.setOnClickListener(V -> {
            finish();

        });

    }
}
