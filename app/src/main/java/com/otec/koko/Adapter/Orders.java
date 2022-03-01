package com.otec.koko.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.otec.koko.R;
import com.otec.koko.UI.OrderList;
import com.otec.koko.utils.Constant;
import com.otec.koko.utils.util;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Orders extends RecyclerView.Adapter<Orders.UI_holder> {


    private List<Map<String, Object>> orders;
    private Context context;
    int call;

    public Orders(List<Map<String, Object>> orders, Context context, int call) {
        this.orders = orders;
        this.context = context;
        this.call = call;
    }

    @NonNull
    @NotNull
    @Override
    public Orders.UI_holder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ordercard, parent, false);
        return new UI_holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull Orders.UI_holder holder, int position) {

        if (call == 1) {
            ChangeView(call, holder);
            holder.email.setText(" "+decide(position));
            holder.order_id.setText(" "+orders.get(position).get("order_id").toString());
            holder.status.setText("Status: "+(!Boolean.valueOf(orders.get(position).get("status").toString()) ? Color(holder): "Seen "));
            holder.timestamp.setText(" "+new util().TIME_FORMAT(orders.get(position).get("timestamp").toString()));
            holder.order_entry.setOnClickListener(e->{
                Navigate_and_update(orders.get(position).get("email").toString(),holder,orders.get(position).get("order_id").toString());
            });
        } else {
            ChangeView(call, holder);
            new util().IMG(holder.Order_img, Constant.BASEURL_S3 + orders.get(position).get("img_url").toString(), holder.progress);
            holder.price.setText("Price: " + orders.get(position).get("price").toString());
            holder.name.setText("Item: " + orders.get(position).get("name").toString());
            holder.quantity.setText("Quantity " + orders.get(position).get("quantity").toString());
        }
    }

    private String decide(int pos) {
        String user;
        if(!orders.get(pos).get("email").toString().contains("@"))
                user = "Anonymous";
        else
              user = orders.get(pos).get("email").toString();

        return  user;
    }

    private String Color(UI_holder holder) {
        holder.status.setTextColor(Color.parseColor("#00FF0A"));
        return "New Order";
    }

    private void Navigate_and_update(String email, UI_holder holder,String order_id) {
        new util().open_Fragment(new OrderList(),"OrderList",holder.itemView,BUNDLE(new Bundle(),2,email,order_id),R.id.frameLayout);
        Map<String,Object> status = new HashMap<>();
        status.put("status","true");
        FirebaseFirestore.getInstance().collection("Notification").document(order_id).update(status);
    }


    private Bundle BUNDLE(Bundle bundle, int a,String email, String order_id) {
        bundle.putInt("view",a);
        bundle.putString("user_email",email);
        bundle.putString("order_id",order_id);
        return  bundle;
    }


    private void ChangeView(int i, UI_holder holder) {
        if (i == 1) {
            holder.order_entry.setVisibility(View.VISIBLE);
            holder.orders.setVisibility(View.INVISIBLE);
            holder.Order_img.setVisibility(View.GONE);
        } else {
            holder.order_entry.setVisibility(View.INVISIBLE);
            holder.orders.setVisibility(View.VISIBLE);
            holder.Order_img.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }


    class UI_holder extends RecyclerView.ViewHolder {

        private ImageView Order_img;
        private TextView name, price, quantity,email,order_id,status,timestamp;
        private ProgressBar progress;
        private RelativeLayout order_entry, orders;

        public UI_holder(@NonNull @NotNull View itemView) {
            super(itemView);
            Order_img = itemView.findViewById(R.id.Order_img);
            price = itemView.findViewById(R.id.Price);
            quantity = itemView.findViewById(R.id.Quantity);
            name = itemView.findViewById(R.id.Name);
            progress = itemView.findViewById(R.id.progress);
            orders = itemView.findViewById(R.id.orders);
            order_entry = itemView.findViewById(R.id.order_entry);
            email = itemView.findViewById(R.id.email);
            order_id = itemView.findViewById(R.id.order_id);
            status = itemView.findViewById(R.id.status);
            timestamp = itemView.findViewById(R.id.timestamp);
        }
    }
}
