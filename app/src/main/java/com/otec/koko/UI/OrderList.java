package com.otec.koko.UI;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.otec.koko.Adapter.Orders;
import com.otec.koko.R;
import com.otec.koko.utils.util;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class OrderList extends Fragment {


    private RecyclerView recyclerView;
    private TextView total;
    private Orders orders;
    private ProgressBar progressBar;


    private String TAG = "OrderList";
    private Map<String,Object> map;
    private List<Map<String, Object>> list;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_order_list, container, false);

        recyclerView = view.findViewById(R.id.recyclerview);
        progressBar = view.findViewById(R.id.progressBar);
        total = view.findViewById(R.id.total);

        list = new ArrayList<>();

        if(getArguments().getInt("view") == 1) {
            
            FirebaseFirestore.getInstance().collection("Notification").orderBy("timestamp", Query.Direction.DESCENDING).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot doc : task.getResult().getDocuments()) {
                                map = new HashMap<>();
                                map.put("email", doc.get("email"));
                                map.put("order_id", doc.get("order_id"));
                                map.put("status", doc.get("status"));
                                map.put("timestamp", doc.get("timestamp"));
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

            FirebaseFirestore.getInstance().collection("Paid Orders").document("orders").collection(getArguments().getString("user_email")).whereEqualTo("cartSessionId", getArguments().getString("order_id")).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot doc : task.getResult().getDocuments()) {
                                map = new HashMap<>();
                                map.put("cartSessionId", doc.get("cartSessionId"));
                                map.put("doc_id", doc.get("doc_id"));
                                map.put("img_url", doc.get("img_url"));
                                map.put("item_id", doc.get("item_id"));
                                map.put("name", doc.get("name"));
                                map.put("price", doc.get("price"));
                                map.put("quantity", doc.get("quantity"));
                                Sum(Integer.parseInt(doc.get("quantity").toString()),Integer.parseInt(doc.get("price").toString()));
                                list.add(map);
                                set_layout(list,getArguments().getInt("view"));
                            }
                        } else
                            Log.d(TAG, "onError: " + task.getException());
                    });
        }


        return view;
    }

    private void Sum(int quantity, int price) {
        long subtotal = 0;
        if(!total.getText().toString().trim().isEmpty()) {
            subtotal = Long.parseLong(total.getText().toString().replace("Total:","").trim());
            subtotal += quantity * price;
        }else
             subtotal = quantity * price;

        total.setText("Total: "+ subtotal);
    }


    public void set_layout(List<Map<String, Object>> options,int call) {
        LinearLayoutManager manager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        orders = new Orders(options, getContext(),call);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(orders);
        progressBar.setVisibility(View.INVISIBLE);
    }

}