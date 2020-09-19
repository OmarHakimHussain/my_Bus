package com.example.mybus;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class Splach_Screen_Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splach__screen);
        Handler h = new Handler();
        Runnable r =new Runnable() {
            @Override
            public void run() {
                Intent go = new Intent(Splach_Screen_Activity.this,CustomerLoginActivity.class);
                startActivity(go);
                Splach_Screen_Activity.this.finish();
            }
        };
        h.postDelayed(r,3000);
        // Method yfdal Loged in until sign out
     /* FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // User is signed in
            Intent i = new Intent(Splach_Screen_Activity.this, NavbarActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        } else {
            // User is signed out
            Log.d(TAG, "onAuthStateChanged:signed_out");
        }*/
    }
}
