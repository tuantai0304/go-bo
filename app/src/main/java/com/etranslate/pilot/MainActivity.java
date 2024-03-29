package com.etranslate.pilot;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.etranslate.pilot.dummy.DummyContent;
import com.etranslate.pilot.fragments.ChatUIFragment;
import com.etranslate.pilot.fragments.HistoryListFragment;
import com.etranslate.pilot.fragments.RequestFragment;
import com.etranslate.pilot.fragments.RequestListFragment;
import com.etranslate.pilot.user.UserLoginActivity;
import com.stephentuso.welcome.WelcomeHelper;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener,
                    RequestFragment.OnFragmentInteractionListener,
                    RequestListFragment.OnListFragmentInteractionListener
{


    /* Static variable */
    FragmentManager supportFragmentManager;
    FragmentTransaction fragmentTransaction;
    private WelcomeHelper welcomeScreen;

//    View content_main;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        welcomeScreen.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* Show welcome screen */
        welcomeScreen = new WelcomeHelper(this, WelcomeScreen.class);
        welcomeScreen.show(savedInstanceState);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        if (mFirebaseUser == null) {
            startActivity(new Intent(this, UserLoginActivity.class));
            finish();
        }

        /* Init Fragment manager */
        supportFragmentManager = getSupportFragmentManager();

        /* Make request service as default screen */
        fragmentTransaction = supportFragmentManager.beginTransaction();
        Fragment requestFragment = new RequestFragment();
        fragmentTransaction.add(R.id.content_main, requestFragment);
        fragmentTransaction.commit();



//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
            this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (mFirebaseUser != null)
            setDrawerNavHeaderInformation(navigationView);


    }

    /**
     * Set User information for Nav Header
     *
     * */

    private void setDrawerNavHeaderInformation(NavigationView navigationView) {
    /* Set header for Nav Drawer */
        Uri photoUrl = mFirebaseUser.getPhotoUrl();
        String displayName = mFirebaseUser.getDisplayName();
        String email = mFirebaseUser.getEmail();

        View header = navigationView.getHeaderView(0);
        ImageView iv_avatar = (ImageView) header.findViewById(R.id.nav_header_avatar_imageview);
        TextView tv_name = (TextView) header.findViewById(R.id.nav_header_displayname_textview);
        TextView tv_email = (TextView) header.findViewById(R.id.nav_header_email_textview);


        if (photoUrl != null)
            Glide.with(getApplicationContext())
                    .load(photoUrl)
                    .into(iv_avatar);

        if (displayName != null)
            tv_name.setText(displayName);

        if (email != null)
            tv_email.setText(email);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (supportFragmentManager.getBackStackEntryCount() > 0) {
                Log.i("MainActivity", "popping backstack");
                supportFragmentManager.popBackStack();
            } else {
                Log.i("MainActivity", "nothing on backstack, calling super");
                super.onBackPressed();
            }
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

//        android.app.FragmentManager app_Fragment_Manager = getFragmentManager();
//        android.app.FragmentTransaction app_tx = app_Fragment_Manager.beginTransaction();
//        Fragment fragment = new Fragment();
        fragmentTransaction = supportFragmentManager.beginTransaction();

        Fragment fragment;

        switch (id) {
            case R.id.nav_request:
                fragment = new RequestFragment();
                fragmentTransaction.replace(R.id.content_main, fragment).addToBackStack(null);
                fragmentTransaction.commit();
                break;

            case R.id.nav_request_history:
                fragment = new HistoryListFragment();
                fragmentTransaction.replace(R.id.content_main, fragment).addToBackStack(null);
                fragmentTransaction.commit();
                break;

            case R.id.nav_all_requests:
                fragment = new RequestListFragment();
                fragmentTransaction.replace(R.id.content_main, fragment).addToBackStack(null);
                fragmentTransaction.commit();
                break;

            case R.id.nav_logout:
                mFirebaseAuth.signOut();
                startActivity(new Intent(getApplicationContext(), UserLoginActivity.class));
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onListFragmentInteraction(DummyContent.DummyItem item) {

    }



}
