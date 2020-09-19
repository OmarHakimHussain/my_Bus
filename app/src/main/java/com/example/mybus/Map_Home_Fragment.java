package com.example.mybus;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


/**
 * A simple {@link Fragment} subclass.
 */
public class Map_Home_Fragment extends Fragment implements OnMapReadyCallback {
    SupportMapFragment mapFragment;
    private GoogleMap mMap;

    public Map_Home_Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_map__home_, container, false);
        mapFragment =(SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment==null){
            FragmentManager fm = getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            mapFragment = SupportMapFragment.newInstance();
            ft.replace(R.id.map,mapFragment).commit();
        }
        mapFragment.getMapAsync(this);
        return v;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(4);
        // Add a marker in Sydney and move the camera
        LatLng position1 = new LatLng(30.042967, 31.005780);
        mMap.addMarker(new MarkerOptions().position(position1).title("Mohamed's Home"));
        // mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position1,20.0f));

        LatLng position2 = new LatLng(30.034938, 31.002118);
        mMap.addMarker(new MarkerOptions().position(position2).title("Amr's Home"));
        // mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position2,20.0f));

        LatLng position3 = new LatLng(29.977453231811523, 31.21539878845215);
        mMap.addMarker(new MarkerOptions().position(position3).title("Omar's Home"));
        // mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position3,20.0f));

        LatLng position4 = new LatLng(30.030093, 31.021370);
        mMap.addMarker(new MarkerOptions().position(position4).title("Hyper one"));
        //  mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position4,20.0f));

        LatLng position5 = new LatLng(30.025956, 31.201075);
        mMap.addMarker(new MarkerOptions().position(position5).title("Metro Station"));
        // mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position5,20.0f));

        LatLng position6 = new LatLng(30.044393, 31.235887);
        mMap.addMarker(new MarkerOptions().position(position6).title("Tahrir Square"));
        // mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position6,20.0f));

        LatLng position7 = new LatLng(30.038783, 31.214742);
        mMap.addMarker(new MarkerOptions().position(position7).title("Russian Cultural Center"));
        //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position7,15.0f));

        LatLng position8 = new LatLng(30.044729, 30.989672);
        mMap.addMarker(new MarkerOptions().position(position8).title("Canadian International College "+" CIC"));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position8,20.0f));
    }
    public void call(View view) {
        Intent go = new Intent(Intent.ACTION_DIAL);
        go.setData(Uri.parse("tel:" +"+2"+"01141024562"));
        startActivity(go);
    }
    public void tostation(View view) {
        Intent go =new Intent(getActivity(),Customer_MapsActivity.class);
        Bundle b = new Bundle();
        b.putString("LineName","Hyper One");
        b.putDouble("lat",30.030093);
        b.putDouble("lng",31.021370);

        go.putExtras(b);
        startActivity(go);
    }

}
