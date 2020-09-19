package com.example.mybus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class Test_LineActivity extends AppCompatActivity {

private RecyclerView recyclerView;
private ArrayList <Customers> C_List;
private FirebaseRecyclerOptions <Customers> options;
private FirebaseRecyclerAdapter<Customers,FirebaseViewHolder> adapter;
private DatabaseReference databaseReference;
TextView txt_clist;

    FirebaseAuth mAuth;
    private String mProfileImageUrl;
    private String dProfileImageUrl;
    private String userID;


    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test__line);

        recyclerView = findViewById(R.id.recyclerView_Student_List);

        mAuth = FirebaseAuth.getInstance();
        userID =Objects.requireNonNull(mAuth.getCurrentUser().getUid());
        txt_clist=findViewById(R.id.txt_clist1);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        C_List = new ArrayList<Customers>();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers");
        databaseReference.keepSynced(true);

        options = new  FirebaseRecyclerOptions.Builder<Customers>().setQuery(databaseReference,Customers.class).build();

        adapter = new FirebaseRecyclerAdapter<Customers, FirebaseViewHolder>(options) {
        @Override
        protected void onBindViewHolder(@NonNull final FirebaseViewHolder holder, final int position, @NonNull final Customers model) {
        query ();

        holder.txt_Cline.setText(model.getLine_Name());

            holder.txt_Cname.setText(model.getName());

            txt_clist.setText(model.getLine_Name());

            mProfileImageUrl = model.getImage_URL();
             Glide.with(Test_LineActivity.this.getApplicationContext()).load(mProfileImageUrl).into(holder.image);
             dProfileImageUrl = model.getImage_URL();
             Glide.with(Test_LineActivity.this.getApplicationContext()).load(dProfileImageUrl).into(holder.image);

        holder.imageCall.setImageResource(R.drawable.ic_phone_call);

        holder.ic_exit.setVisibility(View.GONE);

        holder.imageCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent go = new Intent(Intent.ACTION_DIAL);
                go.setData(Uri.parse("tel:" + model.getPhone()));
                Test_LineActivity.this.startActivity(go);

            }
        });

        holder.ic_enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.ic_enter.setVisibility(View.GONE);
                holder.ic_exit.setVisibility(View.VISIBLE);
            }
        });

        holder.ic_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.ic_exit.setVisibility(View.GONE);
                holder.ic_enter.setVisibility(View.VISIBLE);
            }
        });
    }

        public void query (){

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers");
            Query query = reference.orderByChild("Line_Name").equalTo("El-Sheikh Zayed City");


            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Customers c = snapshot.getValue(Customers.class);
                            // Log.v("get name", c.getName());
                            //  Log.v("get line", c.getLine_Name());
                            // Log.v("get phone", c.getPhone());

                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });



        }


    @NonNull
    @Override
    public FirebaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FirebaseViewHolder(LayoutInflater.from(Test_LineActivity.this).inflate(R.layout.row_design,parent,false));
    }
};


recyclerView.setAdapter(adapter);

    }



}
