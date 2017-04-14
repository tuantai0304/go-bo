package com.etranslate.pilot;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.etranslate.pilot.fragments.BaseFragment;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import org.webrtc.AudioSource;
import org.webrtc.AudioTrack;
import org.webrtc.DataChannel;
import org.webrtc.IceCandidate;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.SdpObserver;
import org.webrtc.SessionDescription;
import org.webrtc.VideoCapturerAndroid;
import org.webrtc.VideoRenderer;
import org.webrtc.VideoRendererGui;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class VideoConferenceActivity extends BaseActivity {
    private static final String VIDEO_TRACK_ID = "video1";
    private static final String AUDIO_TRACK_ID = "audio1";
    private static final String LOCAL_STREAM_ID = "stream1";
    private static final String SDP_MID = "sdpMid";
    private static final String SDP_M_LINE_INDEX = "sdpMLineIndex";
    private static final String SDP = "sdp";

    private PeerConnectionFactory peerConnectionFactory;
    private VideoSource localVideoSource;
    private PeerConnection peerConnection;
    private MediaStream localMediaStream;
    private VideoRenderer otherPeerRenderer;

    String room_id;
    boolean create_offer;
    private String SENDER_ID = "senderId";

    private String currentID;
    private String TAG = "TAG";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_conference);

        /* Request permission */
        /* TODO: Request permission, if not granted, do something else */
        /* Request nessessary permission */
        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA)
                + ContextCompat.checkSelfPermission(getApplicationContext(), RECORD_AUDIO)
                + ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE)
                + ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{CAMERA
                        , RECORD_AUDIO
                        , WRITE_EXTERNAL_STORAGE
                        , READ_EXTERNAL_STORAGE}, 123 );
            }
            return;
        }

        /* Get the room ID and create offer if nessessary*/
        initRoom();

        /* Create Local media stream  */
        initLocalStream();

        /* Create peer connection and Set Listener */
        initPeerConnection();

    }

    /**
     * Init Peer Connection for WebRTC
     *
     * */

    private void initPeerConnection() {
        if (peerConnection != null)
            return;

        ArrayList<PeerConnection.IceServer> iceServers = new ArrayList<>();
        iceServers.add(new PeerConnection.IceServer("stun:stun.l.google.com:19302"));
        iceServers.add(new PeerConnection.IceServer("stun:stunserver.org"));
        iceServers.add(new PeerConnection.IceServer("stun:stun.softjoys.com"));
        iceServers.add(new PeerConnection.IceServer("stun:stun3.l.google.com:19302"));
        iceServers.add(new PeerConnection.IceServer("turn:numb.viagenie.ca","webrtc@live.com","muazkh"));
        iceServers.add(new PeerConnection.IceServer("turn:192.158.29.39:3478?transport=udp","28224511:1379330808","JZEOEt2V3Qb0y27GRntt2u2PAYA="));
        iceServers.add(new PeerConnection.IceServer("turn:192.158.29.39:3478?transport=tcp","28224511:1379330808","JZEOEt2V3Qb0y27GRntt2u2PAYA="));


        peerConnection = peerConnectionFactory.createPeerConnection(
                iceServers,
                new MediaConstraints(),
                peerConnectionObserver);

        peerConnection.addStream(localMediaStream);

        /* Create new offer if this is init person */
        if (create_offer) {
            peerConnection.createOffer(sdpObserver, new MediaConstraints());
        }

        m_dbRooms.child(room_id).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                /* Read messsage */
//                Toast.makeText(MainActivity.this, "Child event runned", Toast.LENGTH_SHORT).show();
                Log.i("onChildAdded:", "onChildAdded: Child event runned");

                Log.i("onChildAdded", "onChildAdded: Datasnapshot = " + dataSnapshot.toString());
                String id = null;

                if (dataSnapshot.hasChild(SENDER_ID))
                    id =  dataSnapshot.child(SENDER_ID).getValue().toString();

                Log.i("onChildAdded:", "onChildAdded: sender id =  " + id);

                String key = dataSnapshot.getKey();
                Log.i("onChildAdded", "key  " + key);
                if (id != null) {
//                    Toast.makeText(MainActivity.this, "Sender ID of message " + id, Toast.LENGTH_SHORT).show();
                    if ( !id.equals(currentID) ) {
//                        Toast.makeText(MainActivity.this, "Child event runned", Toast.LENGTH_SHORT).show();
                        if (dataSnapshot.child("message").hasChild("type")) {
                            /* Anwser or Offfer */
                            String type =  dataSnapshot.child("message").child("type").getValue().toString();
                            String description =  dataSnapshot.child("message").child("description").getValue().toString();
//                            SessionDescription sdp = dataSnapshot.child("message").getValue(SessionDescription.class);
                            SessionDescription sdp = null;

                            if (type.equals("OFFER")) {
                                /* Current Id is Listener */
                                sdp = new SessionDescription(SessionDescription.Type.OFFER, description);
                                peerConnection.setRemoteDescription(sdpObserver, sdp);
                                peerConnection.createAnswer(sdpObserver, new MediaConstraints());
                                Toast.makeText(getApplicationContext(), id + " OFFFER", Toast.LENGTH_SHORT).show();
                                Log.i(TAG, "onChildAdded: Set remote description");
                            } else {
                                sdp = new SessionDescription(SessionDescription.Type.ANSWER, description);
                                peerConnection.setRemoteDescription(sdpObserver, sdp);
                                Toast.makeText(getApplicationContext(), "Set sdp for ANSWER", Toast.LENGTH_SHORT).show();
                                Toast.makeText(getApplicationContext(), id + " ANSWER", Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            /* ICE candidate */
//                            IceCandidate iceCandidate = dataSnapshot.child("message").getValue(IceCandidate.class);
                            IceCandidate iceCandidate = new IceCandidate(
                                    dataSnapshot.child("message").child(SDP_MID).getValue().toString(),
                                    Integer.parseInt(dataSnapshot.child("message").child(SDP_M_LINE_INDEX).getValue().toString()),
                                    dataSnapshot.child("message").child(SDP).getValue().toString()
                            );
                            peerConnection.addIceCandidate(iceCandidate);
                            Log.i("onChildAdded", "onChildAdded: ICE candidate + " + iceCandidate);
//                            Toast.makeText(MainActivity.this, id + " ICE candidate" + (++num_can), Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
//                    Toast.makeText(MainActivity.this, "Id = null", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * Init Video Room method. Get the room ID and determine whether to create a new call offer
     *
     * */
    private void initRoom() {
        /* Get room_id */
        Intent intent = getIntent();
        room_id = intent.getStringExtra(BaseFragment.ARG_ROOMID);
        create_offer = intent.getBooleanExtra(BaseFragment.ARG_CREATE_OFFER, false);
        currentID = mFirebaseUser.getUid();
    }

    private void initLocalStream() {
    /* Create stream */
        AudioManager audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
        audioManager.setSpeakerphoneOn(true);

        PeerConnectionFactory.initializeAndroidGlobals(
                this,  // Context
                true,  // Audio Enabled
                true,  // Video Enabled
                true,  // Hardware Acceleration Enabled
                null); // Render EGL Context

        peerConnectionFactory = new PeerConnectionFactory();

        VideoCapturerAndroid vc = VideoCapturerAndroid.create(VideoCapturerAndroid.getNameOfFrontFacingDevice(), null);

        localVideoSource = peerConnectionFactory.createVideoSource(vc, new MediaConstraints());
        VideoTrack localVideoTrack = peerConnectionFactory.createVideoTrack(VIDEO_TRACK_ID, localVideoSource);
        localVideoTrack.setEnabled(true);

        AudioSource audioSource = peerConnectionFactory.createAudioSource(new MediaConstraints());

        AudioTrack localAudioTrack = peerConnectionFactory.createAudioTrack(AUDIO_TRACK_ID, audioSource);
        localAudioTrack.setEnabled(true);

        localMediaStream = peerConnectionFactory.createLocalMediaStream(LOCAL_STREAM_ID);
        localMediaStream.addTrack(localVideoTrack);
        localMediaStream.addTrack(localAudioTrack);

        GLSurfaceView videoView = (GLSurfaceView) findViewById(R.id.glview_call);

        VideoRendererGui.setView(videoView, null);
        try {
            otherPeerRenderer = VideoRendererGui.createGui(0, 0, 100, 100, VideoRendererGui.ScalingType.SCALE_ASPECT_FILL, true);
            VideoRenderer renderer = VideoRendererGui.createGui(60, 0, 40, 40, VideoRendererGui.ScalingType.SCALE_ASPECT_BALANCED, true);
            localVideoTrack.addRenderer(renderer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    SdpObserver sdpObserver = new SdpObserver() {
        @Override
        public void onCreateSuccess(SessionDescription sessionDescription) {
            peerConnection.setLocalDescription(sdpObserver, sessionDescription);
            sendMessage(currentID, sessionDescription);
        }

        @Override
        public void onSetSuccess() {

        }

        @Override
        public void onCreateFailure(String s) {

        }

        @Override
        public void onSetFailure(String s) {

        }
    };

    /**
     * Peer connection observer
     *
     * */
    PeerConnection.Observer peerConnectionObserver = new PeerConnection.Observer() {
        @Override
        public void onSignalingChange(PeerConnection.SignalingState signalingState) {
            Log.d("RTCAPP", "onSignalingChange:" + signalingState.toString());
        }

        @Override
        public void onIceConnectionChange(PeerConnection.IceConnectionState iceConnectionState) {
            Log.d("RTCAPP", "onIceConnectionChange:" + iceConnectionState.toString());
            if (iceConnectionState == PeerConnection.IceConnectionState.DISCONNECTED)
                peerConnectionClose(null);
        }

        @Override
        public void onIceConnectionReceivingChange(boolean b) {

        }

        @Override
        public void onIceGatheringChange(PeerConnection.IceGatheringState iceGatheringState) {

        }


        @Override
        public void onIceCandidate(IceCandidate iceCandidate) {
            sendMessage(currentID, iceCandidate);
            Log.i("New ICE", "onIceCandidate: " + iceCandidate);
        }

        @Override
        public void onAddStream(MediaStream mediaStream) {
            mediaStream.videoTracks.getFirst().addRenderer(otherPeerRenderer);
            Log.i(TAG, "onAddStream: new stream found");
        }

        @Override
        public void onRemoveStream(MediaStream mediaStream) {

        }

        @Override
        public void onDataChannel(DataChannel dataChannel) {


        }

        @Override
        public void onRenegotiationNeeded() {

        }
    };

    /**
     * Send message to Room of Firebase database
     *
     * */
    private void sendMessage(String id, Object o) {
        String key = m_dbRooms.child(room_id).push().getKey();
//        ref.child(room_id).child(key).child(SENDER_ID).setValue(id.trim().toLowerCase());
//        ref.child(room_id).child(key).child("message").setValue(o);

        Map<String, Object> map = new HashMap<>();
        map.put(SENDER_ID, id);
        map.put("message", o);
        m_dbRooms.child(room_id).child(key).setValue(map);
    }

    public void peerConnectionClose(View view) {
        if (peerConnection != null)
            peerConnection.close();
        Toast.makeText(getApplicationContext(), "Video call end. Thank you", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        peerConnectionClose(null);
    }
}
