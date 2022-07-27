package com.liberty.apps.studio.libertyvpn.view.activites

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.android.billingclient.api.SkuDetails
import com.google.android.gms.ads.MobileAds
import com.liberty.apps.studio.libertyvpn.AppSettings
import com.liberty.apps.studio.libertyvpn.billing.BillingClass
import com.liberty.apps.studio.libertyvpn.databinding.ActivitySplashBinding
import com.onesignal.OneSignal


class SplashActivity : AppCompatActivity(), BillingClass.BillingErrorHandler,
    BillingClass.SkuDetailsListener {

    private var binding: ActivitySplashBinding ?= null
    private var billingClass: BillingClass? = null
    val IS_FIRST: String = "isFirst"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        initGoogleAds()
        initOneSignal()
        initBilling()
    }

    override fun onDestroy() {
        binding = null
        super.onDestroy()
    }

    private fun initGoogleAds() {
        MobileAds.initialize(
            this
        ) { }
    }

    private fun initOneSignal() {
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);
        OneSignal.initWithContext(this);
        OneSignal.setAppId(AppSettings.oneSignalId);
    }

    private fun initBilling() {
        billingClass = BillingClass(this@SplashActivity)
        billingClass!!.setmCallback(this, this);
        billingClass!!.startConnection();
    }

    private fun startMainActivity() {
        val sharedPreferences: SharedPreferences = this.getSharedPreferences("VPNShared",
            Context.MODE_PRIVATE)
        Handler(Looper.myLooper()!!).postDelayed({
            if(!sharedPreferences.getBoolean(IS_FIRST,true)) {
                startActivity(Intent(this@SplashActivity, ControllerActivity::class.java))
                finish()
            }else{
                startActivity(Intent(this@SplashActivity, ActivityWelcome::class.java))
                finish()
            }}, 1800)
    }

    override fun displayErrorMessage(message: String?) {
        when {
            message.equals("done") -> {
                AppSettings.isUserPaid =
                    billingClass!!.isSubscribedToSubscriptionItem(AppSettings.one_month_subscription_id) ||
                            billingClass!!.isSubscribedToSubscriptionItem(AppSettings.three_month_subscription_id) ||
                            billingClass!!.isSubscribedToSubscriptionItem(AppSettings.one_year_subscription_id)

                startMainActivity()
            }
            message.equals("error") -> {
                AppSettings.isUserPaid = false;
                startMainActivity()
            }
            else -> {
                AppSettings.isUserPaid = false;
                startMainActivity()
            }
        }
    }

    override fun subscriptionsDetailList(skuDetailsList: MutableList<SkuDetails>?) {
    }
}