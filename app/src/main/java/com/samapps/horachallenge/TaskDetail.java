package com.samapps.horachallenge;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.samapps.horachallenge.model.BaseTask;

import java.util.HashMap;
import java.util.Map;

import mehdi.sakout.fancybuttons.FancyButton;

public class TaskDetail extends AppCompatActivity {


    private DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dbRef = FirebaseDatabase.getInstance().getReference();
        ((TextView)findViewById(R.id.title)).setText(BaseTask.getNewtask().getName());
        ((TextView)findViewById(R.id.description)).setText(BaseTask.getNewtask().getDescription());
        ((FancyButton)findViewById(R.id.accept)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateDb();
            }
        });
    }

    void updateDb(){
        BaseTask.getNewtask().setAssignedTo(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        Map<String, Object> updates = new HashMap<>();
        updates.put("assignedTo", FirebaseAuth.getInstance().getCurrentUser().getEmail());
        dbRef.child(BaseTask.getNewtask().getRuleId()).updateChildren(updates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                            Toast.makeText(TaskDetail.this,
                                    "task has been posted",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(TaskDetail.this,
                                    "Classified could not be added",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
