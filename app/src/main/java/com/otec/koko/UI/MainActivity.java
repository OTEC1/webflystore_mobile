package com.otec.koko.UI;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.otec.koko.BuildConfig;
import com.otec.koko.R;
import com.otec.koko.Retrofit_.Base_config;
import com.otec.koko.Retrofit_.Calls;
import com.otec.koko.Running_service.Keep_alive;
import com.otec.koko.Running_service.RegisterUser;
import com.otec.koko.utils.Constant;
import com.otec.koko.utils.Find;
import com.otec.koko.utils.util;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.security.Security;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import me.pushy.sdk.Pushy;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.otec.koko.utils.Constant.READ_STORAGE_PERMISSION_REQUEST_CODE;
import static com.otec.koko.utils.Constant.SELECT_VIDEO;

public class MainActivity extends AppCompatActivity {

    private EditText name,price,description;
    private ProgressBar  mainProgressbar;
    private TextView progressCount;
    private Spinner section;
    private Button button,choose;
    private Uri uri;
    private Keep_alive keep_alive;
    private Intent intent;
    private BottomNavigationView bottom;
    private static FirebaseApp INSTANCE;



    private String section1="",TAG="MainActivity",exif,endpoint;
    private DocumentReference documentReference;
    private List<String> sections;
    private ArrayAdapter adapter1;
    private boolean started_payload;
    private double timestamp;
    private long Time_lapsed = 2000, back_pressed;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);





         //util.message(decrypt("1234567812345678",BuildConfig.APPCIRCLE_API_URL) ,getApplicationContext());

        NOTIFICATION_LISTER();
        WriteDB();
        STRICT_POLICY();
        CHECK_POLICY();
        TimStamp();
        mapping();
        populateSpinner();
        new util().bottom_nav(bottom,this);


        choose.setOnClickListener(e-> {
            filePicker("IMG");
        });



        button.setOnClickListener(e-> {
            if(!name.getText().toString().trim().isEmpty() && !price.getText().toString().trim().isEmpty() && !description.getText().toString().trim().isEmpty()) {
                if (!started_payload && uri != null) {
                    mainProgressbar.setVisibility(View.VISIBLE);

                    Task<DocumentSnapshot> doc = FirebaseFirestore.getInstance(getInstance(getApplicationContext())).collection("east").document("lab").get();
                    doc.addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            started_payload = true;
                            AWS(uri, String.valueOf(task.getResult().get("p1")), String.valueOf(task.getResult().get("p2")), "kokocraft");
                        } else
                            Log.d(TAG, "onError ! " + task.getException());
                    });


                } else
                    util.message("Pls add Image file !", getApplicationContext());
            }else
                util.message("Pls fill out all fields !", getApplicationContext());
        });


        section.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                section1 = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        GET_SELECT(section,2);
    }





    private void WriteDB() {

        Map<String,Object> token = new HashMap<>();
        token.put("token", new util().init(getApplicationContext()).getString(getString(R.string.DEVICE_TOKEN), null));

        Task<DocumentSnapshot> df = DOC().get();
        df.addOnCompleteListener(e->{

            if(Objects.requireNonNull(df.getResult()).exists())
                DOC().update(token);
            else
                DOC().set(token);

        });

    }

    private DocumentReference DOC() {
        return  FirebaseFirestore.getInstance().collection("admin").document(getString(R.string.ADMIN));
    }


    public String generate_name() {
        long x = System.currentTimeMillis();
        long q = System.nanoTime();
        return String.valueOf(x).concat(String.valueOf(q))+".png";
    }


    private void filePicker(String url) {
        Intent intent;
        if (url.equals("IMG")) {
            intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
            intent.setDataAndType(uri, "image/*");
            startActivityForResult(intent, SELECT_VIDEO);
        }
    }




    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == SELECT_VIDEO) {
            assert data != null;
            uri = data.getData();
            started_payload = false;
            choose.setText("File Added");

        }
    }


    public int getCameraRotation(Context context, Uri uri, String path) {
        int rotate = 0;
        try {
            context.getContentResolver().notifyChange(uri, null);
            File file = new File(path);

            ExifInterface exif = new ExifInterface(file.getAbsolutePath());
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
            }

        } catch (Exception e) {
            Log.d(TAG, "getCameraRotation: " + e.getLocalizedMessage());

        }
        return rotate;
    }


    private void mapping() {
        name = findViewById(R.id.name);
        price = findViewById(R.id.price);
        section = findViewById(R.id.section);
        progressCount = findViewById(R.id.progressCount);
        button = findViewById(R.id.button);
        description = findViewById(R.id.description);
        mainProgressbar = findViewById(R.id.mainProgressbar);
        choose = findViewById(R.id.choose);
        bottom = findViewById(R.id.bottomNav);
        sections = new ArrayList<>();
    }

    private void TimStamp() {

        Calls call = Base_config.getConnection().create(Calls.class);
        Call<Map<String,Object>> object  = call.getTimesTamp();
        object.enqueue(new Callback<Map<String,Object>>() {
            @Override
            public void onResponse(Call<Map<String,Object>> call, retrofit2.Response<Map<String,Object>> response) {
                Map<String,Object> map = response.body();
                timestamp = Double.parseDouble(Objects.requireNonNull(map.get("timestamp")).toString());
            }

            @Override
            public void onFailure(Call<Map<String,Object>> call, Throwable t) {

            }
        });
    }

    private void GET_SELECT(Spinner category,int y) {

        category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                section1 = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void populateSpinner() {
        sections.add("Select option");
        sections.add("Male");
        sections.add("Female");
        sections.add("unisex");
        sections.add("kids");

        adapter1 = new ArrayAdapter(getApplicationContext(),android.R.layout.simple_spinner_item, sections);
        adapter1.notifyDataSetChanged();
        section.setAdapter(adapter1);

    }

    private Map<String,Object> map(DocumentReference documentReference,String url){

        Map<String,Object> objectMap = new HashMap<>();
        objectMap.put("name",name.getText().toString().toLowerCase());
        objectMap.put("price",price.getText().toString());
        objectMap.put("img_url", url);
        objectMap.put("gender",section1);
        objectMap.put("categories",exif);
        objectMap.put("doc_id", documentReference.getId());
        objectMap.put("description",description.getText().toString());
        objectMap.put("timestamp",timestamp);


        return  objectMap;
    }

    private void AWS(Uri path,  String p1, String p2, String p3){

        try {
            AWSCredentials credentials = new BasicAWSCredentials(p1, p2);
            AmazonS3 s3 = new AmazonS3Client(credentials);
            Security.setProperty("networkaddress.cache.ttl", "60");
            s3.setRegion(Region.getRegion(Regions.EU_WEST_3));
            TransferUtility transferUtility = new TransferUtility(s3, this);
            endpoint =  generate_name();
            TransferObserver trans = transferUtility.upload(p3, endpoint, new File(Find.get_file_selected_path(path, this)));
            trans.setTransferListener(new TransferListener() {
                @Override
                public void onStateChanged(int id, TransferState state) {

                }

                @Override
                public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                    float percentDone = ((float) bytesCurrent / (float) bytesTotal) * 100;
                    int p = (int) percentDone;
                    progressCount.setText("Uploading... " + p + "%");
                    if (p == 100 && started_payload) {
                        Firebase(endpoint);
                        CLOUDINARY(endpoint);
                        choose.setText("Choose + ");
                       reset();
                    }
                }



                @Override
                public void onError(int id, Exception ex) {
                    util.message("S3= " + ex.getLocalizedMessage(), getApplicationContext());
                    reset();
                    if (s3.doesObjectExist(p3, endpoint))
                        s3.deleteObject(p3, endpoint);
                }
            });
        }catch (Exception e){util.message("Error "+e.getLocalizedMessage(), getApplicationContext());}
    }

    private void reset() {
        mainProgressbar.setVisibility(View.GONE);
        started_payload = false;
    }


    private void CLOUDINARY(String url) {
        Calls calls = Base_config.getConnection().create(Calls.class);
        Map<String,Object> pic = new HashMap<>();
        pic.put("url",Constant.BASEURL_S3 + url);
        pic.put("publicface","Kokocarft/"+ url);

        Call<Object> data = calls.addimg(pic);
        data.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                Log.d(TAG, "onResponse: "+ response.body());
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                Log.d(TAG, "onFailure: "+ t.getLocalizedMessage());
            }
        });
    }


    private void Firebase(String  url) {
        if(!name.getText().toString().isEmpty() && !price.getText().toString().isEmpty())
            documentReference = FirebaseFirestore.getInstance().collection("Webflystore1").document();
            documentReference.set(map(documentReference,url)).addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                util.message("Added", getApplicationContext());
                name.setText(""); description.setText("");  price.setText("");
            }else
                util.message("Error "+task.getException(),getApplicationContext());
        });
    }


    private void NOTIFICATION_LISTER() {

        keep_alive = new Keep_alive();
        intent = new Intent(this , keep_alive.getClass());
        if (!is_running(keep_alive.getClass()))
            startService(intent);

        if (!Pushy.isRegistered(getApplicationContext()))
            new RegisterUser(this).execute();
        Pushy.listen(this);
    }


    private boolean is_running(Class<? extends Keep_alive> aClass) {

        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo serviceInfo : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (aClass.getName().equals(serviceInfo.service.getClassName())) {
                Log.d(TAG, " Service Already Running");
                return true;
            }
            Log.d(TAG, " Service Not Running");
        }
        return false;
    }


    //Step 1
    public void STRICT_POLICY() {
        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
    }


    //Step 2
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void CHECK_POLICY() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
        } else
            request_permission();
    }


    //Step 3
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void request_permission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            new AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("This Permission is needed for file sharing")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ActivityCompat.requestPermissions((Activity) getApplicationContext(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_STORAGE_PERMISSION_REQUEST_CODE);
                        }
                    }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            }).create().show();
        } else {
            ActivityCompat.requestPermissions(Objects.requireNonNull(this), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_STORAGE_PERMISSION_REQUEST_CODE);

        }
    }


    //Step 4
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == READ_STORAGE_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                util.message("Permission Granted", this);
            else
                util.message("Permission Denied", this);
        }

    }
    //----------------------------------------------End of file sharing ---------------------------------------------//



    @Override
    public void onBackPressed() {
            util.message("Press twice to exit", getApplicationContext());
            if (back_pressed + Time_lapsed > System.currentTimeMillis()) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                free_memory();
            }
                back_pressed = System.currentTimeMillis();

    }


    public void free_memory() {
        FragmentManager fm = getSupportFragmentManager();
        for (int x = 0; x < fm.getBackStackEntryCount(); ++x) {
            fm.popBackStack();
        }
    }





    private  FirebaseApp getInstance(Context context){
        if(INSTANCE == null)
            INSTANCE = getSecondProject(context);

        return  INSTANCE;
    }




    private  static  FirebaseApp getSecondProject(Context context) {
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setApiKey(BuildConfig.Firebase_key)
                .setApplicationId(BuildConfig.Firebase_APP_ID)
                .setProjectId(BuildConfig.Firebase_PRO_ID)
                .build();
        FirebaseApp.initializeApp(context,options,"chau02");
        return  FirebaseApp.getInstance("chau02");
    }




}