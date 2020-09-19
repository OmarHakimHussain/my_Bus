package com.example.mybus;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class CustomerLoginActivity extends AppCompatActivity {
    private static final String TAG ="" ;
    GoogleSignInClient C_GoogleSignInClient;
    //views
    EditText mEmailEt, mPasswordEt;
    TextView notHaveAccountTv, mRecoverPassTv;
    Button C_LoginBtn;
   // SignInButton C_GoogleLoginBtn;
    //Declare an instance of FirebaseAuth
    FirebaseAuth auth;
    private DatabaseReference reff;
    String  Name;
    //Progress Dialog
    ProgressDialog pd;
    private int RC_SIGN_IN = 100;

    FirebaseAuth mAuth;
    private String userID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_login);


        //Actionbar and its title
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Log In");

        //enable back button
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        //before mAuth
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        C_GoogleSignInClient = GoogleSignIn.getClient(this, gso);


        //Login activity initialize variable
        mEmailEt = findViewById(R.id.emailEt);
        mPasswordEt = findViewById(R.id.passwordEt);
        notHaveAccountTv = findViewById(R.id.nothave_accountTv);
        mRecoverPassTv = findViewById(R.id.recoverpassTv);

        C_LoginBtn = findViewById(R.id.C_loginBtn);

        //C_GoogleLoginBtn = findViewById(R.id.C_googleLoginBtn);


        auth = FirebaseAuth.getInstance();

        //not have account textview click
        notHaveAccountTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CustomerLoginActivity.this, CustomerRegisterActivity.class));
                finish();
            }
        });

        //recover pass textview click
        mRecoverPassTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRecoverPasswordDialog();
            }
        });

        //login button click
        C_LoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog pd = new ProgressDialog(CustomerLoginActivity.this);
                pd.setMessage("Logging In ... Please Wait");
                pd.show();
                //get email and password from edit text
                final String str_email = mEmailEt.getText().toString();
                String str_password = mPasswordEt.getText().toString();
                if (TextUtils.isEmpty(str_email) || !Patterns.EMAIL_ADDRESS.matcher(str_email).matches()) {
                    pd.dismiss();
                    mEmailEt.setError("Please Enter Your E-mail");
                    mEmailEt.setFocusable(true);
                    //Toast.makeText(CustomerLoginActivity.this, "All Fields are required!", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(str_password)) {
                    pd.dismiss();
                    mPasswordEt.setError("Please Enter Your Password");
                    mPasswordEt.setFocusable(true);
                } else {
                    // code for signing in with email and password
                    auth.signInWithEmailAndPassword(str_email, str_password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        DatabaseReference  reference = FirebaseDatabase.getInstance().getReference()
                                                .child("Users").child("Customers").child(auth.getCurrentUser().getUid());

                                        reference.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                // if user successfully login then send user to main activity file
                                                // getUserInfo();
                                                pd.dismiss();
                                                //  Toast.makeText(CustomerLoginActivity.this, "Welcome Mr/Ms: "+Name, Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(CustomerLoginActivity.this, NavbarActivity.class);
                                                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(intent);
                                                //finish();
                                            }
                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                                pd.dismiss();
                                            }
                                        });
                                    } else {
                                        pd.dismiss();
                                        Toast.makeText(CustomerLoginActivity.this, "Login Failed - You Entered Wrong Data", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });

        // Method yfdal Loged in until sign out
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // User is signed in
            Intent i = new Intent(CustomerLoginActivity.this, NavbarActivity.class);
            CustomerLoginActivity.this.finish();
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        } else {
            // User is signed out
            Log.d(TAG, "onAuthStateChanged:signed_out");
        }


        //handle google login btn click
       /* C_GoogleLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //begin google login progress
                Intent signInIntent = C_GoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });*/
    }

    private String getUserInfo() {
        mAuth = FirebaseAuth.getInstance();
        userID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        reff = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(userID);
        reff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                    Map<String, String> map = (Map<String, String>) dataSnapshot.getValue();
                    if (map.get("Name") != null) {
                        Name= map.get("Name").toString();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(CustomerLoginActivity.this, "Connection Failed", Toast.LENGTH_SHORT).show();
            }
        });
        return Name;
    }

    private void showRecoverPasswordDialog() {
        //AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(CustomerLoginActivity.this);
        builder.setTitle("Recover Your Password");
        //set layout linear layout
        LinearLayout linearLayout = new LinearLayout(CustomerLoginActivity.this);
        //views to se in dialog
        final EditText emailEt = new EditText(CustomerLoginActivity.this);
        emailEt.setHint("Email");
        emailEt.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        /* set the main width of the EditView to fit  n 'M' letters regardless of the actual text
         extension and text size*/
        emailEt.setMinEms(16);
        linearLayout.addView(emailEt);
        linearLayout.setPadding(10,10,10,10);
        builder.setView(linearLayout);
        //buttons recover
        builder.setPositiveButton("Recover", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //input email
                String email=emailEt.getText().toString().trim();
                beginRecovery(email);

            }
        });
        //buttons cancel
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        //show dialog
        builder.create().show();
    }

    private void beginRecovery(String email) {
        auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()){

                    Toast.makeText(CustomerLoginActivity.this, "Email Sent", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(CustomerLoginActivity.this, "Failed To Sent", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //get and show proper error message
                Toast.makeText(CustomerLoginActivity.this, "Connection Failed"+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


   @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); // go previous activity
        return super.onSupportNavigateUp();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately

                Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            FirebaseUser user = auth.getCurrentUser();

                            //if user signing in first time then get and show user info from google account
                            if (task.getResult().getAdditionalUserInfo().isNewUser()){
                                //Get user email and Uid from auth
                                String email = user.getEmail();
                                String uid = user.getUid();
                                //when user is registered store user info in firebase realtime database too
                                // using HashMap
                                HashMap<Object,String> hashMap = new HashMap<>();
                                //put info in HashMap
                                hashMap.put("Reference_ID",uid);
                                hashMap.put("Name","");
                                hashMap.put("Email",email);
                                hashMap.put("ID","");
                                hashMap.put("Phone","");
                                hashMap.put("Password","");
                                hashMap.put("Receipt Number","");
                                hashMap.put("Gender","");
                                hashMap.put("Line_Name","");
                                hashMap.put("Image_URL","");
                                //hashMap.put("Complaint", "");
                                hashMap.put("Login Type","Customer");
                                //firebase database instance
                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                //path to store user data named "Users"
                                DatabaseReference reference = database.getReference("Users").child("Customers");
                                //put data within hashmap in database
                                reference.child(uid).setValue(hashMap);
                            }
                            //show user email in Toast
                          //  Toast.makeText(CustomerLoginActivity.this, "Welcome Mr/Ms : "+"", Toast.LENGTH_SHORT).show();

                            //Toast.makeText(CustomerLoginActivity.this, "Welcome Mr/Ms : "+user.getEmail(), Toast.LENGTH_SHORT).show();


                            //go to profile activity after logged in
                            startActivity(new Intent(CustomerLoginActivity.this, NavbarActivity.class));
                            finish();
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(CustomerLoginActivity.this, "Connection Failed", Toast.LENGTH_SHORT).show();
                            // updateUI(null);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // get and show error message
                Toast.makeText(CustomerLoginActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // heta bta3t shared prefrences
   /* @Override
    protected void onStart() {
        super.onStart();
        // dy al bda5lo 3la account bta3o 3latool without sharedprefreces

        FirebaseUser user = mAuth.getCurrentUser();

        if(user!= null)
        {
            // user is already connected so we need to redirct him to home page

            startActivity(new Intent(CustomerLoginActivity.this, NavbarActivity.class));


        }
    }*/


}