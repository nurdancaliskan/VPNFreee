package com.liberty.apps.studio.libertyvpn.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.liberty.apps.studio.libertyvpn.BuildConfig;
import com.liberty.apps.studio.libertyvpn.R;
import com.liberty.apps.studio.libertyvpn.view.activites.loadingWebData;

public class RateDialog extends Dialog implements View.OnClickListener {
    private final Activity activity;
    private final ImageView[] imageViewStars = new ImageView[5];
    private ViewGroup linear_layout_RatingBar;
    private int star_number;
    private TextView text_view_submit;
    private ImageView imageViewRate;

    RelativeLayout dialogRatingButtonPrivacy;
    RelativeLayout dialogRatingButtonAbout;
    RelativeLayout dialogRatingButtonNegative;

    private TextView textViewTitle;
    private TextView textViewDesc;

    public RateDialog(@NonNull Activity activity) {
        super(activity);
        this.activity = activity;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.rating_dialog_layout);
        setCancelable(false);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        initView();
        this.linear_layout_RatingBar.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.shake));
        this.star_number = 0;
    }

    /*
    * Initialize the Views of the Dialog
    * */
    private void initView() {
        this.text_view_submit = findViewById(R.id.text_view_submit);
        this.linear_layout_RatingBar = findViewById(R.id.linear_layout_RatingBar);
        this.text_view_submit.setOnClickListener(this);
        this.imageViewRate = findViewById(R.id.dialog_rating_icon);
        this.textViewTitle = findViewById(R.id.textViewRateTitle);
        this.textViewDesc = findViewById(R.id.textViewRate);
        this.textViewTitle.setVisibility(View.GONE);

        this.dialogRatingButtonAbout = findViewById(R.id.dialog_rating_button_about);
        this.dialogRatingButtonNegative = findViewById(R.id.dialog_rating_button_negative);
        this.dialogRatingButtonPrivacy = findViewById(R.id.dialog_rating_button_privacy);
        this.dialogRatingButtonAbout.setOnClickListener(this);
        this.dialogRatingButtonPrivacy.setOnClickListener(this);
        this.dialogRatingButtonNegative.setOnClickListener(this);

        ImageView image_view_star_1 = findViewById(R.id.image_view_star_1);
        ImageView image_view_star_2 = findViewById(R.id.image_view_star_2);
        ImageView image_view_star_3 = findViewById(R.id.image_view_star_3);
        ImageView image_view_star_4 = findViewById(R.id.image_view_star_4);
        ImageView image_view_star_5 = findViewById(R.id.image_view_star_5);
        image_view_star_1.setOnClickListener(this);
        image_view_star_2.setOnClickListener(this);
        image_view_star_3.setOnClickListener(this);
        image_view_star_4.setOnClickListener(this);
        image_view_star_5.setOnClickListener(this);
        this.imageViewStars[0] = image_view_star_1;
        this.imageViewStars[1] = image_view_star_2;
        this.imageViewStars[2] = image_view_star_3;
        this.imageViewStars[3] = image_view_star_4;
        this.imageViewStars[4] = image_view_star_5;
    }

    /*
    * Perform the Clicks of the Dialog
    * */
    public void onClick(View view) {
        int id = view.getId();
        if (id != R.id.text_view_submit) {
            switch (id) {
                case R.id.image_view_star_1:
                    this.star_number = 1;
                    this.textViewTitle.setVisibility(View.VISIBLE);
                    this.textViewDesc.setText(getContext().getResources().getString(R.string.rating_text_1));
                    setStarBar();
                    return;
                case R.id.image_view_star_2:
                    this.star_number = 2;
                    this.textViewTitle.setVisibility(View.VISIBLE);
                    this.textViewDesc.setText(getContext().getResources().getString(R.string.rating_text_1));
                    setStarBar();
                    return;
                case R.id.image_view_star_3:
                    this.star_number = 3;
                    this.textViewTitle.setVisibility(View.VISIBLE);
                    this.textViewDesc.setText(getContext().getResources().getString(R.string.rating_text_1));
                    setStarBar();
                    return;
                case R.id.image_view_star_4:
                    this.star_number = 4;
                    this.textViewTitle.setVisibility(View.VISIBLE);
                    this.textViewDesc.setText(getContext().getResources().getString(R.string.rating_text_4));
                    setStarBar();
                    return;
                case R.id.image_view_star_5:
                    this.star_number = 5;
                    this.textViewTitle.setVisibility(View.VISIBLE);
                    this.textViewDesc.setText(getContext().getResources().getString(R.string.rating_text_4));
                    setStarBar();
                    return;
                //case R.id.dialog_rating_button_about:
                   // dismiss();
                    //showAboutDialog();
                    //return;
                case R.id.dialog_rating_button_privacy:
                    dismiss();
                    privacyPolicyLink();
                case R.id.dialog_rating_button_negative:
                    dismiss();
                    return;
                default:

            }
        } else if (this.star_number >= 4) {
            this.activity.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID)));
            dismiss();
        } else if (this.star_number > 0) {
            dismiss();
            showFeedbackDialog();
        } else {
            this.linear_layout_RatingBar.startAnimation(AnimationUtils.loadAnimation(this.activity, R.anim.shake));
        }
    }

    /*
    * Show the feedback dialog to the user
    * to perform the feedback about the Liberty VPN.
    * */
    private void showFeedbackDialog() {

        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_feedback);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        RelativeLayout positiveButton = dialog.findViewById(R.id.dialog_rating_button_feedback_submit);
        RelativeLayout negativeButton = dialog.findViewById(R.id.dialog_rating_button_feedback_cancel);
        EditText editText = dialog.findViewById(R.id.dialog_rating_feedback);

        positiveButton.setOnClickListener(v -> {

            if (editText.getText().toString().isEmpty() || editText.getText().toString().length() < 10) {
//                    Helper.showToast(getContext(), "Please explain the issue");
            } else {
                // go to email
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:"));
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{getContext().getResources().getString(R.string.developers_email)});
                intent.putExtra(Intent.EXTRA_SUBJECT, getContext().getResources().getString(R.string.feedback_email_subject));
                intent.putExtra(Intent.EXTRA_TEXT, editText.getText().toString());

                try {
//                        startActivity(Intent.createChooser(intent, "Send E-mail"));
                } catch (ActivityNotFoundException ex) {
//                        Helper.showToast(getContext(), "No email app found.");
                } catch (Exception ex) {
//                        Helper.showToast(getContext(), "Network Error");
                }
            }
        });

        negativeButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    /*
    * Load the privacy policy Link in separate activity
    * */
    private void privacyPolicyLink() {
        Intent webActivity = new Intent(getContext(), loadingWebData.class);
        webActivity.putExtra("activityName", "Privacy Policy");
        webActivity.putExtra("webLink", this.activity.getString(R.string.privacy_policy_link));
        this.activity.startActivity(webActivity);
    }

    /*
    * Show the About dialog
    * */
   /* private void showAboutDialog() {
        Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_about);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        ((RelativeLayout) dialog.findViewById(R.id.bt_close)).setOnClickListener(v -> dialog.dismiss());

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }*/

    /*
    * Set the Stars and perform the corresponding action.
    * */
    private void setStarBar() {
        for (int i = 0; i < this.imageViewStars.length; i++) {
            if (i < this.star_number) {
                this.imageViewStars[i].setImageResource(R.drawable.ic_pointed_star);
            } else {
                this.imageViewStars[i].setImageResource(R.drawable.ic_star1);
            }
        }
        if (this.star_number < 5) {
            this.text_view_submit.setText(R.string.rating_dialog_submit);

        }
    }
}
