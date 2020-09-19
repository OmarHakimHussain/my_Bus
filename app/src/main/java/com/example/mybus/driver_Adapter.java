package com.example.mybus;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Objects;

public class driver_Adapter extends RecyclerView.Adapter<driver_Adapter.driver_Holder> {
    Context context;
    ArrayList<Customers> array_list;
    FirebaseAuth mAuth;
    private String mProfileImageUrl;
    private String dProfileImageUrl;
    private String userID;



    public driver_Adapter(Context context, ArrayList<Customers> array_list) {
        this.context = context;
        this.array_list = array_list;
    }

    @NonNull
    @Override
    public driver_Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.row_design, parent, false);
        driver_Holder b = new driver_Holder(v);
        mAuth = FirebaseAuth.getInstance();
        userID = Objects.requireNonNull(mAuth.getCurrentUser().getUid());
        return b;
    }

    @Override
    public void onBindViewHolder(@NonNull final driver_Holder holder, final int position) {
        holder.txt_Cname.setText(array_list.get(position).getName());
        holder.txt_Cline.setText(array_list.get(position).getLine_Name());

        mProfileImageUrl = array_list.get(position).getImage_URL();
        Glide.with(context.getApplicationContext()).load(mProfileImageUrl).into( holder.image);
        dProfileImageUrl = array_list.get(position).getImage_URL();
        Glide.with(context.getApplicationContext()).load(dProfileImageUrl).into(holder.image);

        holder.imageCall.setImageResource(R.drawable.ic_phone_call);

        holder.ic_exit.setVisibility(View.GONE);

        holder.imageCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent go = new Intent(Intent.ACTION_DIAL);
                go.setData(Uri.parse("tel:"+array_list.get(position).getPhone()));
                context.startActivity(go);

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

    @Override
    public int getItemCount() {
        return array_list.size();
    }

    public class driver_Holder extends RecyclerView.ViewHolder {
        TextView txt_Cname,txt_Cline;
        ImageView image,imageCall,ic_enter,ic_exit;
        public driver_Holder(@NonNull View itemView) {
            super(itemView);
            txt_Cname=itemView.findViewById(R.id.txt_Cname);
            txt_Cline=itemView.findViewById(R.id.txt_Cline);
            image=itemView.findViewById(R.id.image_account);
            imageCall=itemView.findViewById(R.id.imageCall);
            ic_enter=itemView.findViewById(R.id.ic_enter);
            ic_exit=itemView.findViewById(R.id.ic_exit);



        }
    }
}
