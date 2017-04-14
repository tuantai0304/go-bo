package com.etranslate.pilot;

import android.content.Intent;
import android.os.Bundle;

import com.etranslate.pilot.user.RegisterActivity;
import com.etranslate.pilot.user.UserLoginActivity;
import com.stephentuso.welcome.BasicPage;
import com.stephentuso.welcome.TitlePage;
import com.stephentuso.welcome.WelcomeActivity;
import com.stephentuso.welcome.WelcomeConfiguration;
import com.stephentuso.welcome.WelcomeHelper;

/**
 * Created by TuanTai on 13/04/2017.
 */

public class WelcomeScreen extends WelcomeActivity {

    @Override
    protected WelcomeConfiguration configuration() {
        return new WelcomeConfiguration.Builder(this)
                .defaultBackgroundColor(R.color.colorPrimary)
                .page(new TitlePage(R.drawable.logo,
                        "Welcome to Pilot Translate")
                )
                .bottomLayout(WelcomeConfiguration.BottomLayout.BUTTON_BAR)
//                .page(new BasicPage(R.drawable.image,
//                        "Header",
//                        "More text.")
//                        .background(R.color.red_background)
//                )
//                .page(new BasicPage(R.drawable.image,
//                        "Lorem ipsum",
//                        "dolor sit amet.")
//                )
//                .useCustomDoneButton(true)
                .swipeToDismiss(true)
                .build();
    }

    @Override
    protected void onButtonBarFirstPressed() {
        super.onButtonBarFirstPressed();
        Intent intent = new Intent(this, UserLoginActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onButtonBarSecondPressed() {
        super.onButtonBarSecondPressed();
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    @Override
    protected void completeWelcomeScreen() {
        super.completeWelcomeScreen();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
