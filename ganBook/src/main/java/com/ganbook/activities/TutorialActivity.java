package com.ganbook.activities;

import com.codemybrainsout.onboarder.AhoyOnboarderActivity;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.core.content.ContextCompat;

import com.codemybrainsout.onboarder.AhoyOnboarderCard;
import com.project.ganim.R;
import java.util.ArrayList;
import java.util.List;
public class TutorialActivity extends AhoyOnboarderActivity {

    @SuppressLint("ObsoleteSdkInt")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String page1Content, page2Content, page4Content, page5Content, page6Content, page7Content;
        String page1Title;

        if(getPackageName().equals("com.project.ganim")) {
            page1Title = getString(R.string.tutorial_page_1_title);
            page1Content = getString(R.string.tutorial_page_1_content);

            page2Content = getString(R.string.tutorial_page_2_content);
            page4Content = getString(R.string.tutorial_page_4_content);
            page5Content = getString(R.string.tutorial_page_5_content);
            page6Content = getString(R.string.tutorial_page_6_content);
            page7Content = getString(R.string.tutorial_page_7_content);
        } else {
            page1Title = getString(R.string.tutorial_page_1_title_apparent);
            page1Content = getString(R.string.tutorial_page_1_content_apparent);

            page2Content = getString(R.string.tutorial_page_2_content_apparent);
            page4Content = getString(R.string.tutorial_page_4_content_apparent);
            page5Content = getString(R.string.tutorial_page_5_content_apparent);
            page6Content = getString(R.string.tutorial_page_6_content_apparent);
            page7Content = getString(R.string.tutorial_page_7_content_apparent);

        }

        AhoyOnboarderCard ahoyOnboarderCard1 = new AhoyOnboarderCard(page1Title, page1Content, R.drawable.ic_launcher);
        AhoyOnboarderCard ahoyOnboarderCard2 = new AhoyOnboarderCard(getString(R.string.tutorial_page_2_title), page2Content, R.drawable.tutorial_account);
        AhoyOnboarderCard ahoyOnboarderCard3 = new AhoyOnboarderCard(getString(R.string.tutorial_page_3_title), getString(R.string.tutorial_page_3_content), R.drawable.tutorial_camera);
        AhoyOnboarderCard ahoyOnboarderCard4 = new AhoyOnboarderCard(getString(R.string.tutorial_page_4_title), page4Content, R.drawable.tutorial_drawings);
        AhoyOnboarderCard ahoyOnboarderCard5 = new AhoyOnboarderCard(getString(R.string.tutorial_page_5_title), page5Content, R.drawable.tutorial_messages);
        AhoyOnboarderCard ahoyOnboarderCard6 = new AhoyOnboarderCard(getString(R.string.tutorial_page_6_title), page6Content, R.drawable.tutorial_contact);
        AhoyOnboarderCard ahoyOnboarderCard7 = new AhoyOnboarderCard(getString(R.string.tutorial_page_7_title), page7Content, R.drawable.tutorial_events);

        ahoyOnboarderCard1.setBackgroundColor(R.color.black_transparent);
        ahoyOnboarderCard2.setBackgroundColor(R.color.black_transparent);
        ahoyOnboarderCard3.setBackgroundColor(R.color.black_transparent);
        ahoyOnboarderCard4.setBackgroundColor(R.color.black_transparent);
        ahoyOnboarderCard5.setBackgroundColor(R.color.black_transparent);
        ahoyOnboarderCard6.setBackgroundColor(R.color.black_transparent);
        ahoyOnboarderCard7.setBackgroundColor(R.color.black_transparent);

        List<AhoyOnboarderCard> pages = new ArrayList<>();

        pages.add(ahoyOnboarderCard1);
        pages.add(ahoyOnboarderCard2);
        pages.add(ahoyOnboarderCard3);
        pages.add(ahoyOnboarderCard4);
        pages.add(ahoyOnboarderCard5);
        pages.add(ahoyOnboarderCard6);
        pages.add(ahoyOnboarderCard7);

        for (AhoyOnboarderCard page : pages) {
            page.setTitleColor(R.color.white);
            page.setDescriptionColor(R.color.grey_200);
            page.setIconLayoutParams(200, 200, 0, 0, 0, 0);
        }

        setFinishButtonTitle(getString(R.string.tutorial_finish_btn));
        showNavigationControls(true);
        setGradientBackground();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            setFinishButtonDrawableStyle(ContextCompat.getDrawable(this, R.drawable.rounded_button));
        }

        setOnboardPages(pages);
    }

    @Override
    public void onFinishButtonPressed() {
        Intent intent = new Intent(TutorialActivity.this, EntryScreenActivity.class);
        startActivity(intent);
        finish();
    }
}
