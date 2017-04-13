package com.etranslate.pilot;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.etranslate.pilot.dummy.DummyContent;
import com.etranslate.pilot.fragments.ChatUIFragment;
import com.etranslate.pilot.fragments.RequestFragment;
import com.etranslate.pilot.fragments.RequestListFragment;
import com.etranslate.pilot.user.UserLoginActivity;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener,
                    RequestFragment.OnFragmentInteractionListener,
                    RequestListFragment.OnListFragmentInteractionListener
{


    /* Static variable */


//    View content_main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        content_main = findViewById(R.id.content_main);

        /*
        * For test
        * */
//        mFileName = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getAbsolutePath();
//        mFileName += "/audiorecordtest.3gp";
//
//        btnRecord = (Button) findViewById(R.id.btnAudio);
//        btnRecord.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                if (event.getAction() == MotionEvent.ACTION_DOWN) {
//                    startRecording();
//                    Toast.makeText(MainActivity.this, "Start recording", Toast.LENGTH_SHORT).show();
//                } else if (event.getAction() == MotionEvent.ACTION_UP) {
//                    stopRecording();
//                    Toast.makeText(MainActivity.this, "Stop recording", Toast.LENGTH_SHORT).show();
//                }
//
//                return false;
//            }
//        });
//
//        tvRecord = (TextView) findViewById(R.id.tvRecord);
//        tvRecord.setText("Fort test");
        /*
        *
        * */

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        if (mFirebaseUser == null) {
            startActivity(new Intent(this, UserLoginActivity.class));
            finish();
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
            this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
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

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

//        android.app.FragmentManager app_Fragment_Manager = getFragmentManager();
//        android.app.FragmentTransaction app_tx = app_Fragment_Manager.beginTransaction();
//        Fragment fragment = new Fragment();

        if (id == R.id.nav_request) {
            // Handle the camera action
            /* Start LoginActivity if user not login */
            Fragment fragment = new RequestFragment();
            transaction.replace(R.id.content_main, fragment);
            transaction.commit();

        } else if (id == R.id.nav_all_requests) {
            Fragment fragment = new RequestListFragment();
            transaction.replace(R.id.content_main, fragment);
            transaction.commit();

        } else if (id == R.id.nav_share) {
            Bundle bundle = new Bundle();
            String key = "-KgrP_5NT0MZ5GeW3VFT";
            bundle.putString(ChatUIFragment.ARG_ROOMID, key);

            Fragment fragment = new ChatUIFragment();
            fragment.setArguments(bundle);
            transaction.replace(R.id.content_main, fragment);
            transaction.commit();

        } else if (id == R.id.nav_logout) {
            mFirebaseAuth.signOut();
            startActivity(new Intent(getApplicationContext(), UserLoginActivity.class));
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



    /*
    * For testing audio capture
    *
    * */
/* For record audio */
//    private MediaRecorder mRecorder;
//    private static final String LOG_TAG = "AudioRecordTest";
//    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
//    private String mFileName;
//
//    Button btnRecord;
//    TextView tvRecord;
//
//    private void startRecording() {
//        mRecorder = new MediaRecorder();
//        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
//        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
//        mRecorder.setOutputFile(mFileName);
//        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
//
//        try {
//            mRecorder.prepare();
//        } catch (IOException e) {
//            Log.e(LOG_TAG, "prepare() failed");
//        }
//
//        mRecorder.start();
//    }
//
//    private void stopRecording() {
//        Log.i(TAG, "stopRecording: STOOOOP");
//        mRecorder.stop();
//        mRecorder.release();
//        mRecorder = null;
//    }



}
