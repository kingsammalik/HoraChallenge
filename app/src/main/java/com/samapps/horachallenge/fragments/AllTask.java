package com.samapps.horachallenge.fragments;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.samapps.horachallenge.R;
import com.samapps.horachallenge.TaskDetail;
import com.samapps.horachallenge.fragments.dummy.DummyContent;
import com.samapps.horachallenge.fragments.dummy.DummyContent.DummyItem;
import com.samapps.horachallenge.model.BaseTask;
import com.samapps.horachallenge.model.Task;
import com.samapps.horachallenge.service.LocationRequestService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.ToDoubleFunction;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link }
 * interface.
 */
public class AllTask extends Fragment implements MyItemRecyclerViewAdapter.OnListFragmentInteractionListener {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private MyItemRecyclerViewAdapter.OnListFragmentInteractionListener mListener;
    private DatabaseReference dbRef;
    private static final String TAG = "ALLTASK";
    RecyclerView recyclerView;
    int checkItem = -1;
    List<Task> adsList;
    List<Task> fullList;
    LocationManager mLocationManager;
    LocationRequestService locationRequestService;
    MyItemRecyclerViewAdapter recyclerViewAdapter;
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public AllTask() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static AllTask newInstance(int columnCount) {
        AllTask fragment = new AllTask();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);

        dbRef = FirebaseDatabase.getInstance().getReference();
        adsList = new ArrayList<Task>();
        fullList = new ArrayList<Task>();
        recyclerView = (RecyclerView) view.findViewById(R.id.list);
        askPermission();
        mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        }
        locationRequestService= new LocationRequestService(getActivity());
        recyclerViewAdapter = new
                MyItemRecyclerViewAdapter(adsList, AllTask.this);
        try {
            recyclerView.setAdapter(recyclerViewAdapter);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        getAllTasks();
        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            //recyclerView.setAdapter(new MyItemRecyclerViewAdapter(DummyContent.ITEMS, mListener));
        }

        (view.findViewById(R.id.filter)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFilter();
            }
        });

        (view.findViewById(R.id.sort)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Collections.sort(adsList, new SortByDist());
                recyclerViewAdapter.notifyDataSetChanged();
            }
        });

        return view;
    }

    void askPermission(){
        Dexter.withActivity(getActivity())
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        // permission is granted, open the camera
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        // check for permanent denial of permission
                        if (response.isPermanentlyDenied()) {
                            // navigate user to app settings
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }


    void showFilter(){
        final CharSequence[] CAT_TYPE_ITEMS =
                {"category 1", "category 2"};
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(getActivity(), android.R.style.Theme_Material_Light_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(getActivity());
        }
        builder.setTitle("Categories")
                //.setMessage("Pick a Category")
                .setSingleChoiceItems(CAT_TYPE_ITEMS, checkItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.e(TAG,"category "+(i+1));
                        checkItem=i;
                        filterList(checkItem);
                        dialogInterface.dismiss();
                    }
                })
                //.setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }


    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
            dist = dist * 1.609344;
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    private void filterList(int checkItem) {
        adsList.clear();
        for(Task task:fullList){
            if (task.getCategory().equals("category "+(checkItem+1)))
                adsList.add(task);
        }

        recyclerViewAdapter.notifyDataSetChanged();
    }


    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,final int id) {
                        dialog.dismiss();
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.dismiss();
                        // spinner.setVisibility(View.GONE);
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onResume() {
        super.onResume();

        locationRequestService.executeService();


    }


    void getAllTasks(){
        dbRef.orderByChild("assignedTo").equalTo("").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                adsList.clear();
                fullList.clear();
                for (DataSnapshot adSnapshot: dataSnapshot.getChildren()) {
                    Log.e(TAG,"value "+adSnapshot.getValue(Task.class).getName());
                    adsList.add(adSnapshot.getValue(Task.class));
                    fullList.add(adSnapshot.getValue(Task.class));
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


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MyItemRecyclerViewAdapter.OnListFragmentInteractionListener) {
            mListener = (MyItemRecyclerViewAdapter.OnListFragmentInteractionListener) context;
        } else {
            //throw new RuntimeException(context.toString()
              //      + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onListFragmentInteraction(Task item) {
    Log.e(TAG,"name "+item.getName());
    BaseTask.setNewtask(item);
    startActivity(new Intent(getActivity(), TaskDetail.class));
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


    class SortByDist implements Comparator<Task>{


        @Override
        public int compare(Task task, Task t1) {
            double d1 = distance(task.getLat(),task.getLng(),locationRequestService.getLatitude(),locationRequestService.getLongitude());
            double d2 = distance(t1.getLat(),t1.getLng(),locationRequestService.getLatitude(),locationRequestService.getLongitude());
            Double dt = d1-d2;
            Log.e(TAG,"dist "+ dt.intValue());
            return dt.intValue();
        }
    }

}
