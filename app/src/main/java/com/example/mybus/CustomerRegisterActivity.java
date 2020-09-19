package com.example.mybus;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static java.util.Objects.requireNonNull;

public class CustomerRegisterActivity   extends AppCompatActivity {
    //views
    private static final String TAG ="" ;
    EditText mEmailEt, mPasswordEt, mnameEt, mphoneEt, midEt, mrcEt;
    Button C_RegisterBtn;
    TextView mHaveAccountTv;
    RadioGroup radioGroup;
    RadioButton radiomale, radiofemale;
    TextView textView;
    String userid;
    private String str_gender = "";

    //progressbar to display while registering user
    ProgressDialog progressDialog;

    private ImageView img;
    //private Uri filePath;
    // long maxid = 0;
    private StorageReference filePath;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    Spinner spinner;
    String str ="";

    FirebaseAuth auth;
    DatabaseReference reference,reference1;
    ProgressDialog pd;
    private Uri resultUri;
    private String str_email;
    private String str_id;




    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); // go previous activity
        return super.onSupportNavigateUp();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_register);

        //Spinner Lines
        spinner =findViewById(R.id.spinner1);
        ArrayList<String> arrayList= new ArrayList<>();
        arrayList.add("El-Mohandessin");
        arrayList.add("Dokki");
        arrayList.add("El-Haram");
        arrayList.add("Nasr City");
        arrayList.add("Helioplis");
        arrayList.add("New Cairo");
        arrayList.add("El-Maadi");
        arrayList.add("Ain-Shams");
        arrayList.add("Shoubra-Masr");
        arrayList.add("El-Sheikh Zayed City");
        arrayList.add("6-October City");



        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item,arrayList);
        // adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                str = (String) spinner.getSelectedItem();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                str= "No Line";

            }
        });

        str = (String) spinner.getSelectedItem().toString();

        //Actionbar and its title
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Create Account");

        //enable back button
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        //init
        mEmailEt = findViewById(R.id.emailEt);
        mPasswordEt = findViewById(R.id.passwordEt);
        mnameEt = findViewById(R.id.nameEt);
        mphoneEt = findViewById(R.id.phoneEt);
        midEt = findViewById(R.id.idEt);
        mrcEt = findViewById(R.id.rcEt);

        radioGroup = findViewById(R.id.radioGroup);
        radiomale = findViewById(R.id.radio_1);
        radiofemale = findViewById(R.id.radio_2);

        textView = findViewById(R.id.txt_view_selected);

        C_RegisterBtn = findViewById(R.id.C_registerBtn);
        mHaveAccountTv = findViewById(R.id.have_accountTv);
        img = findViewById(R.id.c_img);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        auth = FirebaseAuth.getInstance();

// da 5as b el soora 3shan a3ml choose
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });




        C_RegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd = new ProgressDialog(CustomerRegisterActivity.this);
                pd.setMessage("Please Wait while Register Complete");
                //pd.setMessage("Registering User...");
                pd.show();
                //..........................................................

                //...........................................................
                //Get user all data from edit text
                String str_name  = mnameEt.getText().toString();
                str_email = mEmailEt.getText().toString();
                str_id = midEt.getText().toString();
                String str_phone = mphoneEt.getText().toString();
                String str_password = mPasswordEt.getText().toString();
                String str_rc = mrcEt.getText().toString();

                //validate
                if (mnameEt.getText().toString().isEmpty()) {
                    //set error and focus to name edittext
                    pd.dismiss();
                    mnameEt.setError("Invalid Name");
                    mnameEt.setFocusable(true);
                } else if (mnameEt.getText().toString().length() > 16) {
                    //set error and focus to name edittext
                    pd.dismiss();
                    mnameEt.setError("Name Length at Most 16 Characters");
                    mnameEt.setFocusable(true);
                }  else if (!Patterns.EMAIL_ADDRESS.matcher(mEmailEt.getText().toString()).matches()) {
                    //set error and focus to email edittext
                    pd.dismiss();
                    mEmailEt.setError("Invalid Email");
                    mEmailEt.setFocusable(true);
                } else if (midEt.getText().toString().isEmpty()) {
                    //set error and focus to id edittext
                    pd.dismiss();
                    midEt.setError("Invalid ID");
                    midEt.setFocusable(true);
                } else if (mphoneEt.getText().toString().isEmpty()) {
                    //set error and focus to id edittext
                    pd.dismiss();
                    mphoneEt.setError("Invalid Phone Number");
                    mphoneEt.setFocusable(true);
                } else if (mphoneEt.getText().toString().length() < 11 || mphoneEt.getText().toString().length() > 11 ) {
                    //set error and focus to rc edittext
                    pd.dismiss();
                    mphoneEt.setError("Phone Number Length is 11 Numbers");
                    mphoneEt.setFocusable(true);
                }else if (mrcEt.getText().toString().isEmpty()) {
                    //set error and focus to rc edittext
                    pd.dismiss();
                    mrcEt.setError("Invalid Receipt Number");
                    mrcEt.setFocusable(true);
                } else if (mPasswordEt.getText().toString().length() < 6 || mPasswordEt.getText().toString().isEmpty() ) {
                    //set error and focus to password edit_text
                    pd.dismiss();
                    mPasswordEt.setError("Password Length at Least 6 Characters");
                    mPasswordEt.setFocusable(true);
                } else if (!radiomale.isChecked() && !radiofemale.isChecked() ) {
                    //set error and focus to radiobutton
                    pd.dismiss();
                    radiomale.setError("Please Select Your Gender");
                    radiofemale.setError("Please Select Your Gender");
                    radioGroup.setFocusable(true);
                } else {
                    register(str_name,str_email,str_id,str_phone,str_password,str_rc,str_gender,str);


                    //progressDialog.show();
                    //reference.child(String.valueOf(maxid + 1)).setValue(mnameEt.getText().toString()+mEmailEt.getText().toString()+midEt.getText().toString()
                    //+mphoneEt.getText().toString()+ mPasswordEt.getText().toString());
                    //reference.child(String.valueOf(maxid + 1)).setValue(hashMap);

                }
            }
        });

        // Method yfdal Loged in until sign out
        /*FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
       if (user != null) {
            // User is signed in
            Intent i = new Intent(CustomerRegisterActivity.this, NavbarActivity.class);
                        CustomerRegisterActivity.this.finish();
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        } else {
            // User is signed out
            Log.d(TAG, "onAuthStateChanged:signed_out");
        }*/


        //handle login textview click listener
        mHaveAccountTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CustomerRegisterActivity.this, CustomerLoginActivity.class));
                finish();
            }
        });

    }


    //.....................................................................
//dy el method el mas2ola 3n el e5tyar el sora
    private void chooseImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            //final Uri imageUri = data.getData();
            resultUri = data.getData();
            if (resultUri != null) {
                // saveUserInformation();
                img.setImageURI(resultUri);
                // Glide.with(CustomerRegisterActivity.this.getApplicationContext()).load(resultUri).centerCrop().circleCrop().into(img);

            }
        }
    }
    //.........................................................................................
    // dy el method el mas2ola 3n raf3 el sora 3la el database
    private void uploadImage() {
        filePath = FirebaseStorage.getInstance().getReference().child("Users Images/Customers Images/").child(str_email+"_"+str_id+".png");
        filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){
                    requireNonNull(task.getResult()).getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            Uri uriDownUrl = task.getResult();
                            img.setImageURI(resultUri);
                            Map<String, Object> userInfo = new HashMap<>();
                            userInfo.put("Image_URL", requireNonNull(uriDownUrl).toString());
                            reference.updateChildren(userInfo);
                        }
                    });
                }
            }


        });
    }

//.........................................................................

    public void checkButton(View view) {
        if (radiomale.isChecked()) {
            str_gender = "Male";
        } else if (radiofemale.isChecked()) {
            str_gender = "Female";
        }
    }


//Code for user authentication in database

    public void register (final String name, final String email, final String id,final String phone, final String password, final String rc,final String gender,final String line) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener
                (CustomerRegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            userid = firebaseUser.getUid();
                            reference = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(userid);
                            HashMap<String, Object> hashMap = new HashMap<>();
                            //put user data in database
                            hashMap.put("Reference_ID",userid);
                            hashMap.put("Name",name);
                            hashMap.put("Email",email);
                            hashMap.put("ID",id);
                            hashMap.put("Phone",phone);
                            hashMap.put("Password",password);
                            hashMap.put("Receipt Number",rc);
                            hashMap.put("Gender",gender);
                            hashMap.put("Line_Name",line);
                            //hashMap.put("Image_URL","");
                            //hashMap.put("Complaint", "");
                            hashMap.put("Login Type","Customer");
                            uploadImage();

                            reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {


                                        reference1 = FirebaseDatabase.getInstance().getReference().child("Attendance").child(userid);
                                        HashMap<String, Object> hashMap1 = new HashMap<>();
                                        //put user data in database
                                        hashMap1.put("Name",name);
                                        hashMap1.put("Email",email);
                                        hashMap1.put("ID",id);
                                        hashMap1.put("Line_Name",line);
                                        hashMap1.put("Status","0");
                                        reference1.setValue(hashMap1).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {

                                                    Toast.makeText(CustomerRegisterActivity.this, "Attendance Done "+name, Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }else {

                                        Toast.makeText(CustomerRegisterActivity.this, "Connection Failed", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });

                            Intent intent = new Intent(CustomerRegisterActivity.this, NavbarActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            Toast.makeText(CustomerRegisterActivity.this, "Welcome Mr/Ms : "+name, Toast.LENGTH_SHORT).show();
                        }else {
                            pd.dismiss();
                            Toast.makeText(CustomerRegisterActivity.this, "Connection Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }
}
