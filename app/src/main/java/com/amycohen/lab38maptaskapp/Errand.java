package com.amycohen.lab38maptaskapp;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;

public class Errand {
    String description;
    LatLng start;
    LatLng end;

    public Errand () {}

    public Errand (String description, LatLng start, LatLng end) {
        this.description = description;
        this.start = start;
        this.end = end;
    }

    public static Errand fromSnapshot(DataSnapshot snapshot) {
        Errand errand = new Errand();
        errand.description = snapshot.child("description").getValue(String.class);
        
        float startLatitude = snapshot.child("start").child("lat").getValue(float.class);
        float startLongitude = snapshot.child("start").child("long").getValue(float.class);

        errand.start = new LatLng(startLatitude, startLongitude);



    }
}