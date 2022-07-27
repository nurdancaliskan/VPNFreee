package com.liberty.apps.studio.libertyvpn.view.activites;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.badoo.mobile.util.WeakHandler;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdOptionsView;
import com.facebook.ads.AudienceNetworkAds;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAdLayout;
import com.facebook.ads.NativeAdListener;
import com.facebook.ads.NativeBannerAd;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.liberty.apps.studio.libertyvpn.AppSettings;
import com.liberty.apps.studio.libertyvpn.BuildConfig;
import com.liberty.apps.studio.libertyvpn.R;
import com.liberty.apps.studio.libertyvpn.SharedPreference;
import com.liberty.apps.studio.libertyvpn.adapter.ServerAdapter;
import com.liberty.apps.studio.libertyvpn.databinding.ActivityChangeServerBinding;
import com.liberty.apps.studio.libertyvpn.db.DbHelper;
import com.liberty.apps.studio.libertyvpn.model.Server;
import com.liberty.apps.studio.libertyvpn.utils.CsvParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ChangeServerBottomSheetDialog extends BottomSheetDialogFragment {

    //TODO: About License
    private ActivityChangeServerBinding binding;

    private WeakHandler handler;
    private final OkHttpClient okHttpClient = new OkHttpClient();
    private final List<Server> servers = new ArrayList<>();
    private Request request;
    private Call mCall;
    private ServerAdapter adapter;
    private DbHelper dbHelper;
    private static final String TAG = "ChangeServerBottomSheet";

    private SharedPreference sharedPreference;

    public interface OnInputListener {
        void sendServer(Server server);
    }

    public OnInputListener mOnInputListener;


    private NativeAdLayout nativeAdLayout;
    private NativeBannerAd nativeBannerAd;
    private LinearLayout adView;
    private InterstitialAd facebookInterstitialAd;
    private com.google.android.gms.ads.interstitial.InterstitialAd admobInterstitialAd;
    private Server globalServer;
    private final ServerAdapter.ServerClickCallback serverClickCallback =
            server -> {
                Server selectedServer = new Server(
                        server.hostName,
                        server.ipAddress,
                        server.ping,
                        server.speed,
                        server.countryLong,
                        server.countryShort,
                        server.ovpnConfigData,
                        server.port,
                        server.protocol
                );

                sharedPreference.saveServer(selectedServer);

                showInterstitialAd(selectedServer);
            };
    private Dialog infoAlertDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ActivityChangeServerBinding.inflate(getLayoutInflater());

        initFacebookAds();
        handler = new WeakHandler();
        dbHelper = DbHelper.getInstance(requireContext());
        sharedPreference = new SharedPreference(requireContext());

        servers.addAll(dbHelper.getAll());
        setupSwipeRefreshLayout();
        setupRecyclerView();

        if (request == null) {
            request = new Request.Builder()
                    .url(BuildConfig.VPN_GATE_API)
                    .build();
        }

        if (servers.isEmpty()) {
            populateServerList();
        }

        binding.serverBackButton.setOnClickListener(view -> {
            dismiss();
        });

        binding.changeServerRefreshBtn.setOnClickListener(vv -> {
            populateServerList();
        });

        loadBanner();
        loadInterstitialAd();
        return binding.getRoot();
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        try {
            mOnInputListener = (OnInputListener)context;
        }
        catch (ClassCastException e) {
            Log.e(TAG, "onAttach: ClassCastException: "
                    + e.getMessage());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mCall != null) {
            mCall.cancel();
            mCall = null;
        }
        if (infoAlertDialog != null) {
            if (infoAlertDialog.isShowing()) {
                infoAlertDialog.dismiss();
            }
        }
        binding.swipeRefresh.setOnRefreshListener(null);
    }

    private void setupSwipeRefreshLayout() {
        binding.swipeRefresh.setColorSchemeResources(R.color.colorPrimaryDark);
        binding.swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                populateServerList();
            }
        });
    }

    private void setupRecyclerView() {
        adapter = new ServerAdapter(servers, serverClickCallback);
        Log.i(TAG, "setupRecyclerView: Server Size: " + servers.size());
        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(requireContext(), 0);
        binding.recyclerview.setHasFixedSize(true);
        binding.recyclerview.addItemDecoration(itemDecoration);
        binding.recyclerview.setLayoutManager(new LinearLayoutManager(binding.recyclerview.getContext()));
        binding.recyclerview.setAdapter(adapter);
    }

    private void loadServerList(List<Server> serverList) {
        adapter.setServerList(serverList);
        dbHelper.save(serverList);
    }

    /**
     * Displays the updated list of VPN servers
     */
    private void populateServerList() {
        binding.swipeRefresh.setRefreshing(true);
        binding.recyclerview.setVisibility(View.INVISIBLE);

        mCall = okHttpClient.newCall(request);
        mCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        binding.swipeRefresh.setRefreshing(false);
                        binding.recyclerview.setVisibility(View.VISIBLE);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final List<Server> servers = CsvParser.parse(response);

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            loadServerList(servers);
                            binding.swipeRefresh.setRefreshing(false);
                            binding.recyclerview.setVisibility(View.VISIBLE);
                        }
                    });
                }
            }
        });
    }

    private void loadBanner() {
        if (!AppSettings.Companion.isUserPaid()) {
            binding.changeServerAdBlock.setVisibility(View.VISIBLE);
            binding.changeServerBannerAdmob.setVisibility(View.VISIBLE);
            binding.changeServerBannerFacebook.setVisibility(View.VISIBLE);

            if (AppSettings.Companion.getEnableAdmobAds()) {
                loadAdmobBanner();
            } else if (AppSettings.Companion.getEnableFacebookAds()) {
                loadFacebookBanner();
            }
        } else {
            binding.changeServerAdBlock.setVisibility(View.GONE);

        }
    }

    private void loadAdmobBanner() {
        AdView adView = binding.changeServerBannerAdmob;
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }

    private void loadFacebookBanner() {
        nativeBannerAd = new NativeBannerAd(requireContext(), getString(R.string.facebook_native_banner_id));
        NativeAdListener nativeAdListener = new NativeAdListener() {
            @Override
            public void onMediaDownloaded(Ad ad) {
            }

            @Override
            public void onError(Ad ad, AdError adError) {
            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Race condition, load() called again before last ad was displayed
                if (nativeBannerAd == null || nativeBannerAd != ad) {
                    return;
                }
                // Inflate Native Banner Ad into Container
                inflateAd(nativeBannerAd);
            }

            @Override
            public void onAdClicked(Ad ad) {
            }

            @Override
            public void onLoggingImpression(Ad ad) {
            }
        };

        // load the ad
        nativeBannerAd.loadAd(
                nativeBannerAd.buildLoadAdConfig()
                        .withAdListener(nativeAdListener)
                        .build());
    }

    private void initFacebookAds() {
        AudienceNetworkAds.initialize(requireContext());
    }

    private void inflateAd(NativeBannerAd nativeBannerAd) {
        // Unregister last ad
        nativeBannerAd.unregisterView();

        // Add the Ad view into the ad container.
        nativeAdLayout = getView().findViewById(R.id.change_server_banner_facebook);
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        // Inflate the Ad view.  The layout referenced is the one you created in the last step.
        adView = (LinearLayout) inflater.inflate(R.layout.native_banner_ad_unit, nativeAdLayout, false);
        nativeAdLayout.addView(adView);

        // Add the AdChoices icon
        RelativeLayout adChoicesContainer = adView.findViewById(R.id.ad_choices_container);
        AdOptionsView adOptionsView = new AdOptionsView(requireContext(), nativeBannerAd, nativeAdLayout);
        adChoicesContainer.removeAllViews();
        adChoicesContainer.addView(adOptionsView, 0);

        // Create native UI using the ad metadata.
        TextView nativeAdTitle = adView.findViewById(R.id.native_ad_title);
        TextView nativeAdSocialContext = adView.findViewById(R.id.native_ad_social_context);
        TextView sponsoredLabel = adView.findViewById(R.id.native_ad_sponsored_label);
        MediaView nativeAdIconView = adView.findViewById(R.id.native_icon_view);
        Button nativeAdCallToAction = adView.findViewById(R.id.native_ad_call_to_action);

        // Set the Text.
        nativeAdCallToAction.setText(nativeBannerAd.getAdCallToAction());
        nativeAdCallToAction.setVisibility(
                nativeBannerAd.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
        nativeAdTitle.setText(nativeBannerAd.getAdvertiserName());
        nativeAdSocialContext.setText(nativeBannerAd.getAdSocialContext());
        sponsoredLabel.setText(nativeBannerAd.getSponsoredTranslation());

        // Register the Title and CTA button to listen for clicks.
        List<View> clickableViews = new ArrayList<>();
        clickableViews.add(nativeAdTitle);
        clickableViews.add(nativeAdCallToAction);
        nativeBannerAd.registerViewForInteraction(adView, nativeAdIconView, clickableViews);
    }

    private void loadInterstitialAd() {
        if (!AppSettings.Companion.isUserPaid()) {
            if (AppSettings.Companion.getEnableAdmobAds()) {

                AdRequest adRequest = new AdRequest.Builder().build();
                com.google.android.gms.ads.interstitial.InterstitialAd.load(requireContext(), getResources().getString(R.string.admob_interstitial_id), adRequest,
                        new InterstitialAdLoadCallback() {
                            @Override
                            public void onAdLoaded(@NonNull com.google.android.gms.ads.interstitial.InterstitialAd interstitialAd) {
                                // The mInterstitialAd reference will be null until
                                // an ad is loaded.
                                admobInterstitialAd = interstitialAd;
                                admobInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                                    @Override
                                    public void onAdClicked() {
                                        super.onAdClicked();
                                    }

                                    @Override
                                    public void onAdDismissedFullScreenContent() {
                                        super.onAdDismissedFullScreenContent();
                                        setIntentResult(globalServer);
                                    }

                                    @Override
                                    public void onAdFailedToShowFullScreenContent(@NonNull com.google.android.gms.ads.AdError adError) {
                                        super.onAdFailedToShowFullScreenContent(adError);
                                    }

                                    @Override
                                    public void onAdImpression() {
                                        super.onAdImpression();
                                    }

                                    @Override
                                    public void onAdShowedFullScreenContent() {
                                        super.onAdShowedFullScreenContent();
                                        admobInterstitialAd = null;
                                    }
                                });
                            }

                            @Override
                            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                                // Handle the error
                                admobInterstitialAd = null;
                            }
                        });
            } else if (AppSettings.Companion.getEnableFacebookAds()) {
                facebookInterstitialAd = new InterstitialAd(requireContext(), getResources().getString(R.string.facebook_interstitial_id));
                InterstitialAdListener interstitialAdListener = new InterstitialAdListener() {
                    @Override
                    public void onInterstitialDisplayed(Ad ad) {

                    }

                    @Override
                    public void onInterstitialDismissed(Ad ad) {
                        setIntentResult(globalServer);
                    }

                    @Override
                    public void onError(Ad ad, AdError adError) {

                    }

                    @Override
                    public void onAdLoaded(Ad ad) {

                    }

                    @Override
                    public void onAdClicked(Ad ad) {

                    }

                    @Override
                    public void onLoggingImpression(Ad ad) {

                    }
                };

                facebookInterstitialAd.loadAd(
                        facebookInterstitialAd.buildLoadAdConfig()
                                .withAdListener(interstitialAdListener)
                                .build());
            }
        } else {
            facebookInterstitialAd = null;
            admobInterstitialAd = null;
        }
    }

    private void showInterstitialAd(Server server) {
        globalServer = server;
        if (admobInterstitialAd != null) {
            admobInterstitialAd.show(requireActivity());
        } else if (facebookInterstitialAd != null) {
            facebookInterstitialAd.show();
        } else {
            setIntentResult(server);
        }
    }

    private void setIntentResult(Server server) {
        mOnInputListener.sendServer(server);
    }

    private void infoDialog() {

        infoAlertDialog = new Dialog(requireContext());
        infoAlertDialog.setContentView(R.layout.info_dialog);
        infoAlertDialog.setCancelable(false);
        infoAlertDialog.setCanceledOnTouchOutside(false);

        Button okayButton = infoAlertDialog.findViewById(R.id.info_dialog_btn);
        TextView infoTextview = infoAlertDialog.findViewById(R.id.info_dialog_details);

        infoTextview.setMovementMethod(LinkMovementMethod.getInstance());

        okayButton.setOnClickListener(v -> {
            infoAlertDialog.dismiss();
        });

        infoAlertDialog.getWindow().setLayout(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        infoAlertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        infoAlertDialog.show();
    }

}