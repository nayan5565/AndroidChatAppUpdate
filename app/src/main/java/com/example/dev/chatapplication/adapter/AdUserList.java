package com.example.dev.chatapplication.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.dev.chatapplication.R;
import com.example.dev.chatapplication.activity.MainActivity;

import java.util.ArrayList;

/**
 * Created by Dev on 1/20/2018.
 */

public class AdUserList extends RecyclerView.Adapter<AdUserList.MyViewHolder> {
    private ArrayList<String> userList;
    private Context context;
    private LayoutInflater inflater;

    public AdUserList(Context context) {
        userList = new ArrayList<>();
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    public void addData(ArrayList<String> recipes) {
        this.userList = recipes;
        notifyDataSetChanged();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.row_user_list, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        holder.tvFullName.setText(userList.get(position).split(",")[1]
                .replace('}', ' ')
                .split("=")[1]);
//        holder.tvFullName.setText(userList.get(position).replaceAll("Email=leo@gmail.com, Name="," ")
// .replace('{',' ')
// .replace('}',' '));

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tvFullName;

        private MyViewHolder(View itemView) {
            super(itemView);

            tvFullName = (TextView) itemView.findViewById(R.id.tvUser);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivity(new Intent(context, MainActivity.class));
                    MainActivity.userPos = getAdapterPosition();
                }
            });
        }
    }
}
