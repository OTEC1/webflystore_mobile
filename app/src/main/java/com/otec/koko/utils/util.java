package com.otec.koko.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.otec.koko.R;
import com.otec.koko.UI.Editor;
import com.otec.koko.UI.Login;
import com.otec.koko.UI.MainActivity;
import com.otec.koko.UI.OrderList;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class util {




    private  SharedPreferences sp;
    public SharedPreferences init(Context view) {
        return sp = Objects.requireNonNull(view.getSharedPreferences(view.getString(R.string.app_name), Context.MODE_PRIVATE));
    }


    public  static  void  messageLong(String message, Context constant){
        Toast.makeText(constant, message, Toast.LENGTH_LONG).show();
    }

    public  static  void  message( String massage,Context context){
        Toast.makeText(context, massage, Toast.LENGTH_SHORT).show();
    }



    //Bottom  Nav
    public void bottom_nav(BottomNavigationView bottomNav, AppCompatActivity appCompatActivity) {
        bottomNav.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {


                case R.id.Post:
                    if (FirebaseAuth.getInstance().getUid() == null)
                            appCompatActivity.startActivity(new Intent(appCompatActivity, MainActivity.class));
                     else
                        util.message("Pls Sign in", appCompatActivity);
                    return true;


                case R.id.Orders:
                    if (FirebaseAuth.getInstance().getUid() == null)
                         openFragment(new OrderList(),"OrderList",1,appCompatActivity);
                    else
                        util.message("Pls Sign in", appCompatActivity);;
                    return true;


                case R.id.Edit:
                    if (FirebaseAuth.getInstance().getUid() == null)
                        openFragment(new Editor(),"Editor",1,appCompatActivity);
                    else
                        util.message("Pls Sign in", appCompatActivity);;
                    return true;



                case R.id.Signin:
                    if (FirebaseAuth.getInstance().getUid() == null)
                        appCompatActivity.startActivity(new Intent(appCompatActivity, Login.class));
                    else
                        util.message("Pls Sign in", appCompatActivity);
                    return true;
            }

            return false;
        });
    }



    //Fragment Open from Activity
    public void openFragment(Fragment fragment, String my_fragment, int a, AppCompatActivity context) {
        FragmentTransaction fragmentTransaction = context.getSupportFragmentManager().beginTransaction();
        reuse_fragment(fragmentTransaction, fragment, my_fragment, BUNDLE(new Bundle(), a), R.id.frameLayout);
    }

    private Bundle BUNDLE(Bundle bundle, int a) {
        bundle.putInt("view",a);
        bundle.putString("user_email","");
        return  bundle;
    }


    //Open Fragment from  Adapter Class
    public void open_Fragment(Fragment fragments, String tag, View view, Bundle bundle, int d) {
        AppCompatActivity activity = (AppCompatActivity) view.getContext();
        Fragment myfrag = fragments;
        myfrag.setArguments(bundle);
        activity.getSupportFragmentManager().beginTransaction().replace(d, myfrag, tag).addToBackStack(null).commit();

    }


    //Reuse component
    private void reuse_fragment(FragmentTransaction fragmentTransaction, Fragment fragment, String my_fragment, Bundle b, int frameLayout) {
        fragment.setArguments(b);
        fragmentTransaction.replace(frameLayout, fragment, my_fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }


    //IMG LOAD PROGRESS
    public void IMG(ImageView poster_value, String url, ProgressBar progressBar) {
        RequestOptions requestOptions = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true);

        Glide.with(poster_value.getContext()).load(url)
                .error(R.drawable.ic_baseline_error_outline_24)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        progressBar.setVisibility(View.INVISIBLE);

                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        progressBar.setVisibility(View.INVISIBLE);
                        return false;
                    }
                })
                .apply(requestOptions).into(poster_value);
    }




    public String TIME_FORMAT(String result) {
        double l = Double.parseDouble(result);
        long h = (long) l;
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(h);
        cal.setTimeZone(Calendar.getInstance().getTimeZone());
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:s");
        Date x = cal.getTime();
        return formatter.format(x);

    }

}
