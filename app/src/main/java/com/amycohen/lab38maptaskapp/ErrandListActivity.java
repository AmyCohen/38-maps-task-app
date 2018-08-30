package com.amycohen.lab38maptaskapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ErrandListActivity extends AppCompatActivity implements ValueEventListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_errand_list);

        DatabaseReference errands = FirebaseDatabase.getInstance().getReference("errands");
        errands.addValueEventListener(this);
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        List<Errand> errands = new ArrayList<>();

        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
            errands.add(Errand.fromSnapshot(snapshot));
        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
}
