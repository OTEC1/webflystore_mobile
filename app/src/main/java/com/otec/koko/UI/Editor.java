package com.otec.koko.UI;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.otec.koko.Adapter.Orders;
import com.otec.koko.Adapter.Posts;
import com.otec.koko.R;
import com.otec.koko.utils.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Editor extends Fragment {

    private Posts posts_and_review;
    private RecyclerView recyclerview;
    private ProgressBar progressBar;


    private Map<String,Object> map;
    private List<Map<String, Object>> list;
    private  String TAG = "Editor";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_editor, container, false);
        recyclerview = view.findViewById(R.id.recyclerview);
        progressBar = view.findViewById(R.id.progressBar);
        list = new ArrayList<>();

        if(getArguments().getInt("view") == 1) {
            FirebaseFirestore.getInstance().collection("Webflystore1").orderBy("timestamp", Query.Direction.DESCENDING).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot doc : task.getResult().getDocuments()) {
                                map = new HashMap<>();
                                map.put("description", doc.get("description"));
                                map.put("gender", doc.get("gender"));
                                map.put("img_url", doc.get("img_url"));
                                map.put("name", doc.get("name"));
                                map.put("price", doc.get("price"));
                                map.put("doc_id", doc.get("doc_id"));
                                list.add(map);

                                if(list.size() == task.getResult().getDocuments().size())
                                    set_layout(list,getArguments().getInt("view"));
                                else if (task.getResult().getDocuments().size() == 0) {
                                    util.message("No Order has been placed !", getContext());
                                    progressBar.setVisibility(View.INVISIBLE);
                                }
                            }
                        } else
                            Log.d(TAG, "onError: " + task.getException());
                    });
        }else {

            FirebaseFirestore.getInstance().collection("Webflystore1").document(getArguments().getString("doc_id")).collection("Review").get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot doc : task.getResult().getDocuments()) {
                                map = new HashMap<>();
                                map.put("review", doc.get("review"));
                                map.put("user", doc.get("user"));
                                list.add(map);
                                set_layout(list,getArguments().getInt("view"));
                            }
                        } else
                            Log.d(TAG, "onError: " + task.getException());
                    });
        }


        return view;
    }



    public void set_layout(List<Map<String, Object>> options,int call) {
        LinearLayoutManager manager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        posts_and_review = new Posts(options, getContext(),call);
        recyclerview.setLayoutManager(manager);
        recyclerview.setAdapter(posts_and_review);
        progressBar.setVisibility(View.INVISIBLE);
    }
}