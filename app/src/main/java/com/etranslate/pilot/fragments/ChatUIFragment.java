package com.etranslate.pilot.fragments;


import android.content.Intent;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.etranslate.pilot.R;
import com.etranslate.pilot.dto.Message;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mobsandgeeks.saripaar.annotation.Url;

import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChatUIFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatUIFragment extends BaseFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String ARG_ROOMID = "roomId";
    public static final String ARG_PARAM2 = "param2";
    private static final int CAPTURE_IMAGE = 3;
    private static final int CAPTURE_AUDIO = 4;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    /* UI elements */
    private Button mSendButton;
    private RecyclerView mMessageRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private ProgressBar mProgressBar;
    private EditText mMessageEditText;
    private ImageButton mAddMessageImageView;
    private ImageButton mVoiceMessageImageView;
    private ImageButton mImageMessageImageView;

    /* Firebase elements */
    private FirebaseRecyclerAdapter<Message, MessageViewHolder>
            mFirebaseAdapter;

    /* Code for Intent Result */
    private static final int REQUEST_IMAGE = 2;
    private static final String LOADING_IMAGE_URL = "https://www.google.com/images/spin-32.gif";

    String roomId;

    /* For record audio */
    private MediaRecorder mRecorder;
    private static final String LOG_TAG = "AudioRecordTest";
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
//    private static File mFileName;

    public ChatUIFragment() {
        // Required empty public constructor
    }



    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChatUIFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChatUIFragment newInstance(String param1, String param2) {
        ChatUIFragment fragment = new ChatUIFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ROOMID, param1);
//        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_ROOMID);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_chat_window, container, false);

        /* Get room id*/
        roomId = this.getArguments().getString(ARG_ROOMID);

        mProgressBar = (ProgressBar) v.findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.GONE);
        mMessageRecyclerView = (RecyclerView) v.findViewById(R.id.messageRecyclerView);

        mLinearLayoutManager = new LinearLayoutManager(v.getContext());
        mLinearLayoutManager.setStackFromEnd(true);

        mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);
//        mMessageRecyclerView.setAdapter(mFirebaseAdapter);

        mFirebaseAdapter = new FirebaseRecyclerAdapter<Message,
                MessageViewHolder>(
                Message.class,
                R.layout.item_message,
                MessageViewHolder.class,
                m_dbMessage.child(roomId)) {

            @Override
            protected void populateViewHolder(final MessageViewHolder viewHolder, final Message friendlyMessage, int position) {
                mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                if (friendlyMessage.getContent() != null) {
                    viewHolder.messageTextView.setText(friendlyMessage.getContent());
                    viewHolder.messageTextView.setVisibility(TextView.VISIBLE);
                    viewHolder.messageImageView.setVisibility(ImageView.GONE);
                } else {
                        /* Image or Audio message*/
                        String imageUrl = friendlyMessage.getImageUrl();
                        if (imageUrl.startsWith("gs://")) {
                            StorageReference storageReference = FirebaseStorage.getInstance()
                                    .getReferenceFromUrl(imageUrl);
                            storageReference.getDownloadUrl().addOnCompleteListener(
                                    new OnCompleteListener<Uri>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Uri> task) {
                                            if (task.isSuccessful()) {
                                                String downloadUrl = task.getResult().toString();
                                                if (friendlyMessage.getType().equals("media")) {
                                                    Glide.with(viewHolder.messageImageView.getContext())
                                                            .load(downloadUrl)
                                                            .into(viewHolder.messageImageView);
                                                    Log.i(TAG, "onComplete: Image message");
                                                } else {
                                                    /* Audio type */
                                                    playMedia(downloadUrl);
                                                    Log.i(TAG, "onComplete: Play video");
                                                }

                                            } else {
                                                Log.w(TAG, "Getting download url was not successful.",
                                                        task.getException());
                                            }
                                        }
                                    });
                        } else {
                            Glide.with(viewHolder.messageImageView.getContext())
                                    .load(friendlyMessage.getImageUrl())
                                    .into(viewHolder.messageImageView);
                        }
                        viewHolder.messageImageView.setVisibility(ImageView.VISIBLE);
                        viewHolder.messageTextView.setVisibility(TextView.GONE);
                    }
//                    else if (friendlyMessage.getType().equals("audio")) {
//                        playMedia(friendlyMessage.getImageUrl());
//                    }
//                }

                viewHolder.messengerTextView.setText(friendlyMessage.getName());
                if (friendlyMessage.getPhotoUrl() == null) {
                    viewHolder.messengerImageView.setImageDrawable(ContextCompat.getDrawable(getContext(),
                            R.drawable.ic_account_circle_black_36dp));
                } else {
                    Glide.with(getContext())
                            .load(friendlyMessage.getPhotoUrl())
                            .into(viewHolder.messengerImageView);
                }

            }
        };

        mFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int friendlyMessageCount = mFirebaseAdapter.getItemCount();
                int lastVisiblePosition =
                        mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                // If the recycler view is initially being loaded or the
                // user is at the bottom of the list, scroll to the bottom
                // of the list to show the newly added message.
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (friendlyMessageCount - 1) &&
                                lastVisiblePosition == (positionStart - 1))) {
                    mMessageRecyclerView.scrollToPosition(positionStart);
                }
            }
        });

//        mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);
        mMessageRecyclerView.setAdapter(mFirebaseAdapter);

        mMessageEditText = (EditText) v.findViewById(R.id.messageEditText);
//        mMessageEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(mSharedPreferences
//                .getInt(CodelabPreferences.FRIENDLY_MSG_LENGTH, DEFAULT_MSG_LENGTH_LIMIT))});
        mMessageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    mSendButton.setEnabled(true);
                } else {
                    mSendButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        mSendButton = (Button) v.findViewById(R.id.sendButton);
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Send messages on click.
                Message friendlyMessage = new
                        Message(mMessageEditText.getText().toString(),
                        "text", null,
                        null,
                        roomId, mFirebaseUser.getUid(),
                        mFirebaseUser.getDisplayName() /* no image */);
                m_dbMessage.child(roomId).push().setValue(friendlyMessage);
                mMessageEditText.setText("");
            }
        });

        mAddMessageImageView = (ImageButton) v.findViewById(R.id.addMessageImageView);
        mAddMessageImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Select image for image message on click.
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_IMAGE);
            }
        });

        mImageMessageImageView = (ImageButton) v.findViewById(R.id.imageMessageImageView);
        mImageMessageImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Select image for image message on click.
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                Uri uri = Uri.fromFile(img);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
                startActivityForResult(intent, CAPTURE_IMAGE);
            }
        });

        mVoiceMessageImageView = (ImageButton) v.findViewById(R.id.voiceMessageImageView);
        mVoiceMessageImageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    startRecording();
                    Toast.makeText(getActivity(), "Recording.....", Toast.LENGTH_SHORT).show();
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    Toast.makeText(getActivity(), "Record stopped", Toast.LENGTH_SHORT).show();
                    stopRecording();
                    
                }

                return false;
            }
        });

        // Record to the external cache directory for visibility
        mFileName = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getAbsolutePath();
        mFileName += "/audiorecordtest.3gp";

//        mVoiceMessageImageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
//                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mFileName));
//                startActivityForResult(intent, CAPTURE_AUDIO);
//            }
//        });

        return v;
    }

    private void startRecording() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

        mRecorder.start();
    }

    private void stopRecording() {
        Log.i(TAG, "stopRecording: STOOOOP");
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;

        addNewVoiceMessage(Uri.fromFile(new File(mFileName)));
//        playMedia(Uri.parse(mFileName));
    }


    private void playMedia(String url) {
//        String url = "http://r1.hot.c68.vdc.nixcdn.com/64641f9d542d4738004dc796f306c659/58e481e3/NhacCuaTui931/IDontBelieveInYou-NooPhuocThinhBasick-4659471.mp3?t=1491372029421";

        MediaPlayer mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.setDataSource(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.start();

    }

//    final File mFileName = new File(Environment
//            .getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC), "testAudio.mp3");

    private static String mFileName = null;
    final File img = new File(Environment
            .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "test.jpg");

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);
//        Log.i(TAG, "onActivityResult: data=" + data.toString());

        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    final Uri uri = data.getData();
                    Log.d(TAG, "Uri: " + uri.toString());
                    Log.d(TAG, "data: " + data.toString());

                    addNewImageMessage(uri);
                }
            }
        }

//        Camera c = Camera.open();
//        c.takePicture();
        if (requestCode == CAPTURE_IMAGE) {
            if (resultCode == RESULT_OK) {
//                Log.i(TAG, "onActivityResult: ");
//                if (data != null) {
//                    Bundle bundle = new Bundle();
//                    bundle = data.getExtras();
//                    Log.i(TAG, "onActivityResult: new Image" + bundle.get("data"));
//                }
                final Uri uri = Uri.fromFile(img);
                addNewImageMessage(uri);
//
            }
        }

//        if (requestCode == CAPTURE_AUDIO) {
//                    if (resultCode == RESULT_OK) {
//
//                        final Uri uri = Uri.fromFile(new File(mFileName));
//                        addNewVoiceMessage(uri);
//                        playMedia(uri);
//        //
//                    }
//                }


    }

    private void addNewVoiceMessage(final Uri uri) {
        Message tempMessage = new Message(LOADING_IMAGE_URL, mFirebaseUser.getUid());
        m_dbMessage.child(roomId).push()
                .setValue(tempMessage, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError,
                                           DatabaseReference databaseReference) {
                        if (databaseError == null) {
                            String key = databaseReference.getKey();
                            StorageReference storageReference =
                                    FirebaseStorage.getInstance()
                                            .getReference()
                                            .child(roomId)
                                            .child(uri.getLastPathSegment());

                            putAudioInStorage(storageReference, uri, key);
                        } else {
                            Log.w(TAG, "Unable to write message to database.",
                                    databaseError.toException());
                        }
                    }
                });
    }

    private void putAudioInStorage(StorageReference storageReference, Uri uri, final String key) {
        storageReference.putFile(uri).addOnCompleteListener(getActivity(),
                new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            Message friendlyMessage =
                                    new Message(null, "audio", task.getResult().getMetadata().getDownloadUrl()
                                            .toString(), null, roomId,
                                            mFirebaseUser.getUid(),
                                            mFirebaseUser.getDisplayName());
                            m_dbMessage.child(roomId).child(key)
                                    .setValue(friendlyMessage);
                        } else {
                            Log.w("Firebase", "Audio upload task was not successful.",
                                    task.getException());
                        }
                    }
                });

//        storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                Toast.makeText(getContext(), "Success uploaded", Toast.LENGTH_SHORT).show();
//                Message friendlyMessage =
//                        new Message(null, "audio", taskSnapshot.getDownloadUrl()
//                                .toString(), null, roomId,
//                                mFirebaseUser.getUid(),
//                                mFirebaseUser.getDisplayName());
//                m_dbMessage.child(roomId).child(key)
//                        .setValue(friendlyMessage);
//            }
//        });
    }

    private void addNewImageMessage(final Uri uri) {
        Message tempMessage = new Message(LOADING_IMAGE_URL, mFirebaseUser.getUid());
        m_dbMessage.child(roomId).push()
                .setValue(tempMessage, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError,
                                           DatabaseReference databaseReference) {
                        if (databaseError == null) {
                            String key = databaseReference.getKey();
                            StorageReference storageReference =
                                    FirebaseStorage.getInstance()
                                            .getReference()
//                                                        .getReference(mFirebaseUser.getUid())
                                            .child(roomId)
                                            .child(uri.getLastPathSegment());

                            putImageInStorage(storageReference, uri, key);
                        } else {
                            Log.w(TAG, "Unable to write message to database.",
                                    databaseError.toException());
                        }
                    }
                });
    }

    private void putImageInStorage(StorageReference storageReference, Uri uri, final String key) {
        storageReference.putFile(uri).addOnCompleteListener(getActivity(),
                new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            Message friendlyMessage =
                                    new Message(null, "media", task.getResult().getMetadata().getDownloadUrl()
                                            .toString(), null, roomId,
                                            mFirebaseUser.getUid(),
                                            mFirebaseUser.getDisplayName());
                            m_dbMessage.child(roomId).child(key)
                                    .setValue(friendlyMessage);
                        } else {
                            Log.w("Firebase", "Image upload task was not successful.",
                                    task.getException());
                        }
                    }
                });
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        public TextView messageTextView;
        public ImageView messageImageView;
        public TextView messengerTextView;
        public CircleImageView messengerImageView;

        public MessageViewHolder(View v) {
            super(v);
            messageTextView = (TextView) itemView.findViewById(R.id.messageTextView);
            messageImageView = (ImageView) itemView.findViewById(R.id.messageImageView);
            messengerTextView = (TextView) itemView.findViewById(R.id.messengerTextView);
            messengerImageView = (CircleImageView) itemView.findViewById(R.id.messengerImageView);
        }
    }



}
