package com.example.mybus;


import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class CustomerChooseActivity extends AppCompatActivity {
    private static final String TAG ="" ;
    Button C_RegisterBtn,C_LoginBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_choose);

        //Actionbar and its title
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Customer");

        //init views
        C_RegisterBtn =findViewById(R.id.C_register_btn);
        C_LoginBtn =findViewById(R.id.C_login_btn);

        //handle register button click
        C_RegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // start RegisterActivity
                startActivity(new Intent(CustomerChooseActivity.this,CustomerRegisterActivity.class));
            }
        });
        //handle login button click
        C_LoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // start LoginActivity
                startActivity(new Intent(CustomerChooseActivity.this,CustomerLoginActivity.class));
            }
        });

        // Method yfdal Loged in until sign out
        /*FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // User is signed in
            Intent i = new Intent(CustomerChooseActivity.this, NavbarActivity.class);
            CustomerChooseActivity.this.finish();
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        } else {
            // User is signed out
            Log.d(TAG, "onAuthStateChanged:signed_out");
        }*/

    }
}

