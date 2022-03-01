package com.otec.koko.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.otec.koko.R;
import com.otec.koko.UI.Editor;
import com.otec.koko.UI.OrderList;
import com.otec.koko.utils.Constant;
import com.otec.koko.utils.util;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Posts extends RecyclerView.Adapter<Posts.UI_holder> {


    private List<Map<String, Object>> orders;
    private Context context;
    int call;

    public Posts(List<Map<String, Object>> orders, Context context, int call) {
        this.orders = orders;
        this.context = context;
        this.call = call;
    }

    @NonNull
    @NotNull
    @Override
    public Posts.UI_holder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.editor, parent, false);
        return new UI_holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull Posts.UI_holder holder, int position) {

        if (call == 1) {
            ChangeView(call, holder);
            holder.description.setText(" " + orders.get(position).get("description").toString());
            holder.gender.setText(" " + orders.get(position).get("gender").toString());
            holder.PostName.setText(" " + orders.get(position).get("name").toString());
            holder.PostPrice.setText(" " + (orders.get(position).get("price").toString()));
            new util().IMG(holder.post_img, Constant.BASEURL_S3 + orders.get(position).get("img_url").toString(), holder.progress);



            holder.update.setOnClickListener(e->{
                UPDATE(orders.get(position).get("doc_id").toString(),holder);
            });

            holder.reviewing.setOnClickListener(s->{
                Navigate(holder, orders.get(position).get("doc_id").toString());
            });


        } else {
            ChangeView(call, holder);
            holder.review1.setText(" " + orders.get(position).get("review").toString());
            holder.user.setText(" " + orders.get(position).get("user").toString());

        }
    }


    private void UPDATE(String doc_id, UI_holder holder) {
        Map<String,Object> map = new HashMap<>();
        map.put("description", holder.description.getText().toString());
        map.put("gender", holder.gender.getText().toString());
        map.put("name", holder.PostName.getText().toString());
        map.put("price",holder.PostPrice.getText().toString());
        map.put("doc_id", doc_id);
        FirebaseFirestore.getInstance().collection("Webflystore1").document(doc_id).update(map)
                .addOnCompleteListener(e->{
                    if(e.isSuccessful())
                        util.message("Updated", holder.description.getContext());
                    else
                        util.message("Error occurred "+e.getException(),holder.description.getContext());
                });
    }


    private void Navigate( UI_holder holder, String order_id) {
        new util().open_Fragment(new Editor(), "Editor", holder.itemView, BUNDLE(new Bundle(), 2, order_id), R.id.frameLayout);
         }


    private Bundle BUNDLE(Bundle bundle, int a,  String order_id) {
        bundle.putInt("view", a);
        bundle.putString("doc_id", order_id);
        return bundle;
    }


    private void ChangeView(int i, UI_holder holder) {
        if (i == 1) {
            holder.posts.setVisibility(View.VISIBLE);
            holder.reviews.setVisibility(View.INVISIBLE);
            holder.post_img.setVisibility(View.VISIBLE);
            holder.reviewing.setVisibility(View.VISIBLE);
            holder.update.setVisibility(View.VISIBLE);
        } else {
            holder.posts.setVisibility(View.GONE);
            holder.reviews.setVisibility(View.VISIBLE);
            holder.post_img.setVisibility(View.GONE);
            holder.reviewing.setVisibility(View.GONE);
            holder.update.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }


    class UI_holder extends RecyclerView.ViewHolder {

        private ImageView post_img;
        private EditText PostName, PostPrice, gender, description;
        private TextView user, review1;
        private ProgressBar progress;
        private RelativeLayout posts, reviews;
        private Button update, reviewing;

        public UI_holder(@NonNull @NotNull View itemView) {
            super(itemView);

            post_img = itemView.findViewById(R.id.post_img);
            PostPrice = itemView.findViewById(R.id.Postprice);
            PostName = itemView.findViewById(R.id.PostName);
            progress = itemView.findViewById(R.id.progress);
            description = itemView.findViewById(R.id.description);
            gender = itemView.findViewById(R.id.gender);


            user = itemView.findViewById(R.id.username);
            review1 = itemView.findViewById(R.id.reviews1);

            update = itemView.findViewById(R.id.update);
            reviewing = itemView.findViewById(R.id.reviewing);

            posts = itemView.findViewById(R.id.posts);
            reviews = itemView.findViewById(R.id.reviews);
        }
    }
}
