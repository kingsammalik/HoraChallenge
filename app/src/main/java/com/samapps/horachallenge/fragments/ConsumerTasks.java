package com.samapps.horachallenge.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.samapps.horachallenge.CompleteTask;
import com.samapps.horachallenge.R;
import com.samapps.horachallenge.model.BaseTask;
import com.samapps.horachallenge.model.Task;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ConsumerTasks.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ConsumerTasks#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ConsumerTasks extends Fragment implements MyItemRecyclerViewAdapter.OnListFragmentInteractionListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private DatabaseReference dbRef;
    private static final String TAG = "ALLTASK";
    RecyclerView recyclerView;
    MyItemRecyclerViewAdapter recyclerViewAdapter;

    List<Task> adsList;

    private OnFragmentInteractionListener mListener;

    public ConsumerTasks() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ConsumerTasks.
     */
    // TODO: Rename and change types and number of parameters
    public static ConsumerTasks newInstance(String param1, String param2) {
        ConsumerTasks fragment = new ConsumerTasks();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_consumer_tasks, container, false);
        dbRef = FirebaseDatabase.getInstance().getReference();

        recyclerView = (RecyclerView) view.findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adsList = new ArrayList<Task>();
        recyclerViewAdapter = new
                MyItemRecyclerViewAdapter(adsList, ConsumerTasks.this);
        recyclerView.setAdapter(recyclerViewAdapter);
        getMyTasks();

        return view;
    }

    void getMyTasks(){
        dbRef.orderByChild("createdBy").equalTo(FirebaseAuth.getInstance().getCurrentUser().getEmail()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                adsList.clear();
                for (DataSnapshot adSnapshot: dataSnapshot.getChildren()) {
                    Log.e(TAG,"value "+adSnapshot.getValue(Task.class).getName());
                        adsList.add(adSnapshot.getValue(Task.class));
                }
                Log.d(TAG, "no of ads for search is "+adsList.size());

                recyclerViewAdapter.notifyDataSetChanged();

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "Error trying to get classified ads for "
                        +databaseError);
                Toast.makeText(getActivity(),
                        "Error trying to get classified ads for " ,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onListFragmentInteraction(Task item) {
        Log.e(TAG,"name "+item.getName());
        //BaseTask.setCompletetask(item);
        //startActivity(new Intent(getActivity(), CompleteTask.class));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
           // throw new RuntimeException(context.toString()
             //       + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
