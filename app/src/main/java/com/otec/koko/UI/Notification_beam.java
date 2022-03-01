package com.otec.koko.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.otec.koko.R;
import com.otec.koko.utils.util;

public class Notification_beam extends AppCompatActivity {

    private BottomNavigationView bottom;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_beam);
        bottom = findViewById(R.id.bottomNav);
        new util().bottom_nav(bottom,this);
        new util().openFragment(new OrderList(),"OrderList",1,this);
    }
}