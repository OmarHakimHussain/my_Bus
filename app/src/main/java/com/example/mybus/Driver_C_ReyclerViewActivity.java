package com.example.mybus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Driver_C_ReyclerViewActivity extends AppCompatActivity {
    RecyclerView recyclerView_clist;
    ArrayList<Customers> c_list =new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver__c__reycler_view);

        recyclerView_clist=findViewById(R.id.recyclerView_clist);

        //Actionbar and its title
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Customer List");
        query();


      /*  Customer c1 = new Customer("Mohamed Ahmed","Mostafa Mahmoud Square","+201141024562",R.drawable.ic_account,R.drawable.ic_phone_call);
        c_list.add(c1);
        Customer c2 = new Customer("Amr Khaled","Mostafa Mahmoud Square","+201000122659",R.drawable.ic_account,R.drawable.ic_phone_call);
        c_list.add(c2);
        Customer c3 = new Customer("Omar Hakim","Shooting Club","+201033389739",R.drawable.ic_account,R.drawable.ic_phone_call);
        c_list.add(c3);
        Customer c4 = new Customer("Jouhny George","Lebanon Square","+201211252558",R.drawable.ic_account,R.drawable.ic_phone_call);
        c_list.add(c4);
        Customer c5 = new Customer("Karim Omar","Lebanon Square","+201203776514",R.drawable.ic_account,R.drawable.ic_phone_call);
        c_list.add(c5);*/

        driver_Adapter d1 = new driver_Adapter(Driver_C_ReyclerViewActivity.this,c_list);
        recyclerView_clist.setAdapter(d1);
        recyclerView_clist.setLayoutManager(new LinearLayoutManager(Driver_C_ReyclerViewActivity.this));


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
                        c_list.add(c);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }
}


