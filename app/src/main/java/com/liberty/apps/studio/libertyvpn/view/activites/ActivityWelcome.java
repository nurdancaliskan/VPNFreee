package com.liberty.apps.studio.libertyvpn.view.activites;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearSnapHelper;

import com.liberty.apps.studio.libertyvpn.SharedPreference;
import com.liberty.apps.studio.libertyvpn.adapter.ListItemAdapter;
import com.liberty.apps.studio.libertyvpn.databinding.ActivityWelcomeBinding;
import com.liberty.apps.studio.libertyvpn.utils.ViewUtils;


public class ActivityWelcome extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    ActivityWelcomeBinding binding;
    int scrollIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityWelcomeBinding.inflate(getLayoutInflater());
        initUI();
        setContentView(binding.getRoot());

        SharedPreferences sharedPreferences = this.getSharedPreferences("VPNShared",
                Context.MODE_PRIVATE);

        binding.bContinue.setOnClickListener(view -> {
            scrollIndex += 1;
            if (scrollIndex > 2) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("isFirst",false);
                editor.apply();
                editor.commit();
                startActivity(new Intent(ActivityWelcome.this, ControllerActivity.class));
                return;
            }
            updateTexts(scrollIndex);
            binding.recyclerView.smoothScrollToPosition(scrollIndex);
        });

        //binding.contract.setOnClickListener(view -> {
          //  startActivity(new Intent(ActivityWelcome.this, ContractActivity.class));
        //});


    }



    private void updateTexts(int index) {
        Log.d(TAG, "updateTexts() called with: index = [" + index + "]");
        if (index == 0) {
            Log.d(TAG, "updateTexts() called with: 0");
            binding.tTitle.setText("Secure and Private");
            binding.tSubtext.setText("Surf in the internet without getting tracked");
            binding.scrollView.animate()
                    .translationX(0f)
                    .setDuration(250L)
                    .setStartDelay(0)
                    .start();
        }
        if (index == 1) {
            Log.d(TAG, "updateTexts() called with: 1");
            binding.tTitle.setText("Abudant servers");
            binding.tSubtext.setText("Select more than 50+ servers to connect");
            binding.scrollView.animate()
                    .translationX(ViewUtils.convertDpToPx(25f))
                    .setDuration(250L)
                    .setStartDelay(0)
                    .start();
        }
        if (index == 2) {
            Log.d(TAG, "updateTexts() called with: 2");
            binding.tTitle.setText("Unlimited and Free");
            binding.tSubtext.setText("No subsribtion fee and unlimited bandwith");
            binding.scrollView.animate()
                    .translationX(ViewUtils.convertDpToPx(50f))
                    .setDuration(250L)
                    .setStartDelay(0)
                    .start();
        }

    }

    private void initUI() {
        new LinearSnapHelper().attachToRecyclerView(binding.recyclerView);
        binding.recyclerView.setAdapter(new ListItemAdapter());
    }

}

