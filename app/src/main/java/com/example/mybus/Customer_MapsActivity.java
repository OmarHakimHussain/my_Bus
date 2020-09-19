package com.example.mybus;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class Customer_MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    String LineName;
    Double lat,lng;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer__maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Bundle data =getIntent().getExtras();
        LineName=data.getString("LineName");
        lat =data.getDouble("lat");
        lng =data.getDouble("lng");
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(4);
        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(lat, lng);
      //  mMap.addMarker(new MarkerOptions().position(sydney).title("LineName"));
       // mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        LatLng position1 = new LatLng(lat, lng);
        mMap.addMarker(new MarkerOptions().position(position1).title(LineName));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position1,20.0f));
    }

    public void call(View view) {
        Intent go = new Intent(Intent.ACTION_DIAL);
        go.setData(Uri.parse("tel:" +"+2"+"01141024562"));
        startActivity(go);
    }

    public void tostation(View view) {
        Intent go =new Intent(Customer_MapsActivity.this,Customer_MapsActivity.class);
        Bundle b = new Bundle();
        b.putString("LineName","Hyper One");
        b.putDouble("lat",30.030093);
        b.putDouble("lng",31.021370);
        go.putExtras(b);
        startActivity(go);
    }
}
