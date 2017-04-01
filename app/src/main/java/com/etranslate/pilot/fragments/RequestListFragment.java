package com.etranslate.pilot.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.etranslate.pilot.R;
import com.etranslate.pilot.dto.Request;
import com.etranslate.pilot.dummy.DummyContent.DummyItem;
import com.firebase.ui.database.FirebaseRecyclerAdapter;

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
                    R.layout.item_message,
                    RequestViewHolder.class,
                    m_dbRequest) {
                @Override
                protected void populateViewHolder(RequestViewHolder viewHolder, Request request, int position) {
                    if (request.getSrcLang() != null) {
                        viewHolder.messageTextView.setText(request.getSrcLang());
                        viewHolder.messageTextView.setVisibility(TextView.VISIBLE);
                        viewHolder.messageImageView.setVisibility(ImageView.GONE);
                    } else {
//                        String imageUrl = friendlyMessage.getImageUrl();
//                        if (imageUrl.startsWith("gs://")) {
//                            StorageReference storageReference = FirebaseStorage.getInstance()
//                                    .getReferenceFromUrl(imageUrl);
//                            storageReference.getDownloadUrl().addOnCompleteListener(
//                                    new OnCompleteListener<Uri>() {
//                                        @Override
//                                        public void onComplete(@NonNull Task<Uri> task) {
//                                            if (task.isSuccessful()) {
//                                                String downloadUrl = task.getResult().toString();
//                                                Glide.with(viewHolder.messageImageView.getContext())
//                                                        .load(downloadUrl)
//                                                        .into(viewHolder.messageImageView);
//                                            } else {
//                                                Log.w(TAG, "Getting download url was not successful.",
//                                                        task.getException());
//                                            }
//                                        }
//                                    });
//                        } else {
//                            Glide.with(viewHolder.messageImageView.getContext())
//                                    .load(friendlyMessage.getImageUrl())
//                                    .into(viewHolder.messageImageView);
//                        }
//                        viewHolder.messageImageView.setVisibility(ImageView.VISIBLE);
//                        viewHolder.messageTextView.setVisibility(TextView.GONE);
                    }


                    viewHolder.messengerTextView.setText(request.getTarLang());
                    Log.i("View holder text", "populateViewHolder: " + viewHolder.messengerTextView.getText());
//                    if (friendlyMessage.getPhotoUrl() == null) {
//                        viewHolder.messengerImageView.setImageDrawable(ContextCompat.getDrawable(MainActivity.this,
//                                R.drawable.ic_account_circle_black_36dp));
//                    } else {
//                        Glide.with(MainActivity.this)
//                                .load(friendlyMessage.getPhotoUrl())
//                                .into(viewHolder.messengerImageView);
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
        public TextView messageTextView;
        public ImageView messageImageView;
        public TextView messengerTextView;
        public CircleImageView messengerImageView;

        public RequestViewHolder(View v) {
            super(v);
            messageTextView = (TextView) itemView.findViewById(R.id.messageTextView);
            messageImageView = (ImageView) itemView.findViewById(R.id.messageImageView);
            messengerTextView = (TextView) itemView.findViewById(R.id.messengerTextView);
            messengerImageView = (CircleImageView) itemView.findViewById(R.id.messengerImageView);
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
