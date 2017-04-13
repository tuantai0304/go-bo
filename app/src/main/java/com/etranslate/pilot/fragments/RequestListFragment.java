package com.etranslate.pilot.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.etranslate.pilot.R;
import com.etranslate.pilot.VideoConferenceActivity;
import com.etranslate.pilot.dto.Request;
import com.etranslate.pilot.dto.Room;
import com.etranslate.pilot.dummy.DummyContent.DummyItem;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class RequestListFragment extends BaseFragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";

    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;

    private FirebaseRecyclerAdapter<Request, RequestViewHolder>
            mFirebaseAdapter;


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RequestListFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static RequestListFragment newInstance(int columnCount) {
        RequestListFragment fragment = new RequestListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    RecyclerView recyclerView;
    LinearLayoutManager mLinearLayoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_request_list, container, false);


        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            recyclerView = (RecyclerView) view;

            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }

            mFirebaseAdapter = new FirebaseRecyclerAdapter<Request,
                    RequestViewHolder>(
                    Request.class,
                    R.layout.item_request,
                    RequestViewHolder.class,
                    m_dbRequest.orderByChild("acceptStatus").equalTo("new")) {
                @Override
                protected void populateViewHolder(RequestViewHolder viewHolder, final Request request, final int position) {

//                    if (request.getAcceptStatus().equals("new")) {
                        final String mode = request.getMode();
                        switch (mode) {
                            case MODE_CHAT:
                                viewHolder.modeImageView.setImageDrawable(getResources().getDrawable(android.R.drawable.sym_action_chat));
                                break;
                            case MODE_VIDEO:
                                viewHolder.modeImageView.setImageDrawable(getResources().getDrawable(android.R.drawable.sym_action_call));
                                break;
                            case MODE_IMAGE:
                                viewHolder.modeImageView.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_menu_report_image));
                                break;

                        }
                        viewHolder.langToLangTextView.setText(request.getSrcLang() + " to " + request.getTarLang());

                        viewHolder.btnAccept.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                acceptRequest(getRef(position), mode);
                            }
                        });
//                    }
                }



            };

            mLinearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
//            RecyclerView mMessageRecyclerView;

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
                        recyclerView.scrollToPosition(positionStart);
                    }
                }
            });


            recyclerView.setAdapter(mFirebaseAdapter);
        }

        return view;
    }

    /* When translator accept a request
    * create a new chat room
    *
    * */
    private void acceptRequest(final DatabaseReference requestRef, final String mode) {
        final String new_room_key = m_dbRooms.push().getKey();
//        Request request;
        requestRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                /*Get request info*/
                Request request = dataSnapshot.getValue(Request.class);

                /* Change the roomId field of the current requestRef */
                requestRef.child("roomId").setValue(new_room_key);
                Log.i("New room key", "changeToChatFragment: " + new_room_key);

                /* Create new room */
                Room room = new Room( request.getUserID(), mFirebaseUser.getUid() );
                m_dbRooms.child(new_room_key).setValue(room)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                if (!mode.equals(MODE_VIDEO)) {
                                    changeToChatFragment(new_room_key);
                                }
                                else {
                                    changeToVideoConferenceFragment(new_room_key);
                                }
                                /* Change status of the request  */
                                requestRef.child("acceptStatus").setValue("accepted");
                            }
                        }
                    });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void changeToVideoConferenceFragment(String new_room_key) {
//        Bundle bundle = new Bundle();
//        bundle.putString(ChatUIFragment.ARG_ROOMID, new_room_key);
//
//        /* Change to chat UI screen */
//        VideoConferenceFragment videoConferenceFragment = new VideoConferenceFragment();
//
//        videoConferenceFragment.setArguments(bundle);
//
//        FragmentManager fm = getActivity().getSupportFragmentManager();
//        FragmentTransaction tx = fm.beginTransaction();
//        tx.replace(R.id.content_main , videoConferenceFragment);
//        tx.commit();

        Intent intent = new Intent(getContext(), VideoConferenceActivity.class);
        intent.putExtra(ARG_ROOMID, new_room_key);
        startActivity(intent);
//        startActivity(new Intent(getContext(), VideoConferenceActivity.class));
    }

    private void changeToChatFragment(String new_room_key) {

        Bundle bundle = new Bundle();
        bundle.putString(ChatUIFragment.ARG_ROOMID, new_room_key);

        /* Change to chat UI screen */
        ChatUIFragment chatUIFragment = new ChatUIFragment();

        chatUIFragment.setArguments(bundle);

        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction tx = fm.beginTransaction();
        tx.replace(R.id.content_main , chatUIFragment);
        tx.commit();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public static class RequestViewHolder extends RecyclerView.ViewHolder {
        public TextView langToLangTextView;
        public ImageView modeImageView;
        public TextView userTextView;
        public Button btnAccept;

        public RequestViewHolder(View v) {
            super(v);
            langToLangTextView = (TextView) itemView.findViewById(R.id.langToLangTextView);
            modeImageView = (ImageView) itemView.findViewById(R.id.modeImageView);
            userTextView = (TextView) itemView.findViewById(R.id.userTextView);
            btnAccept = (Button) itemView.findViewById(R.id.btnAccept);
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(DummyItem item);
    }
}
