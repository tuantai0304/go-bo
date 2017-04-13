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
import android.widget.Toast;

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
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class HistoryListFragment extends BaseFragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";

    // TODO: Customize parameters
    private int mColumnCount = 1;
//    private OnListFragmentInteractionListener mListener;

    private FirebaseRecyclerAdapter<Request, HistoryViewHolder>
            mFirebaseAdapter;


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public HistoryListFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static HistoryListFragment newInstance(int columnCount) {
        HistoryListFragment fragment = new HistoryListFragment();
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
        final View view = inflater.inflate(R.layout.fragment_history_list, container, false);


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
                    HistoryViewHolder>(
                    Request.class,
                    R.layout.item_history_request,
                    HistoryViewHolder.class,
                    m_dbRequest.orderByChild("userID").equalTo(mFirebaseUser.getUid())) {
                @Override
                protected void populateViewHolder(HistoryViewHolder viewHolder, final Request request, final int position) {

                    final String mode = request.getMode();
                    switch (mode) {
                        case MODE_CHAT:
                            viewHolder.modeImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_mode_comment_black_36dp));
                            break;
                        case MODE_VIDEO:
                            viewHolder.modeImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_video_call_black_36dp));
                            break;
                        case MODE_IMAGE:
                            viewHolder.modeImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_collections_black_36dp));
                            break;

                    }
                    viewHolder.langToLangTextView.setText(request.getSrcLang() + " to " + request.getTarLang());

                    String translatorName = request.getTranslatorName();
                    if (translatorName != null)
                        viewHolder.userTextView.setText(translatorName);

                    SimpleDateFormat sfd = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                    String date = sfd.format(new Date((Long) request.getTimestamp()));
                    viewHolder.dateTextView.setText(date);
                }

                @Override
                public HistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                    HistoryViewHolder viewHolder = super.onCreateViewHolder(parent, viewType);
                    viewHolder.setOnClickListener(new HistoryViewHolder.ClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            Toast.makeText(getActivity(), "Item clicked at " + position, Toast.LENGTH_SHORT).show();

                            Request request = getItem(position);
                            String mode = request.getMode();

                            switch (mode){
                                case MODE_CHAT:
                                    changeToChatFragment(request.getRoomId());
                                    break;
                            }
                        }

                        @Override
                        public void onItemLongClick(View view, int position) {
                            Toast.makeText(getActivity(), "Item long clicked at " + position, Toast.LENGTH_SHORT).show();
                        }
                    });
                    return viewHolder;
                }


            };

            mLinearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

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

    private void changeToChatFragment(String new_room_key) {

        Bundle bundle = new Bundle();
        bundle.putString(ChatUIFragment.ARG_ROOMID, new_room_key);

        /* Change to chat UI screen */
        ChatUIFragment chatUIFragment = new ChatUIFragment();

        chatUIFragment.setArguments(bundle);

        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction tx = fm.beginTransaction();
        tx.replace(R.id.content_main , chatUIFragment).addToBackStack(null);
        tx.commit();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnListFragmentInteractionListener) {
//            mListener = (OnListFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnListFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
//        mListener = null;
    }

    public static class HistoryViewHolder extends RecyclerView.ViewHolder {
        public TextView langToLangTextView;
        public ImageView modeImageView;
        public TextView userTextView;
        public TextView dateTextView;


        public HistoryViewHolder(View v) {
            super(v);
            langToLangTextView = (TextView) itemView.findViewById(R.id.langToLangTextView);
            modeImageView = (ImageView) itemView.findViewById(R.id.modeImageView);
            userTextView = (TextView) itemView.findViewById(R.id.userTextView);
            dateTextView = (TextView) itemView.findViewById(R.id.dateTextView);

            //listener set on ENTIRE ROW, you may set on individual components within a row.
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mClickListener.onItemClick(v, getAdapterPosition());

                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mClickListener.onItemLongClick(v, getAdapterPosition());
                    return true;
                }
            });

        }

        private HistoryViewHolder.ClickListener mClickListener;

        //Interface to send callbacks...
        public interface ClickListener{
            public void onItemClick(View view, int position);
            public void onItemLongClick(View view, int position);
        }

        public void setOnClickListener(HistoryViewHolder.ClickListener clickListener){
            mClickListener = clickListener;
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
