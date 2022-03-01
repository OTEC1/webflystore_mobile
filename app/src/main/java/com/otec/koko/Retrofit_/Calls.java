package com.otec.koko.Retrofit_;


import java.util.Map;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface Calls {



    @GET("getTimeStamp")
    Call<Map<String,Object>> getTimesTamp();



    @POST("https://us-central1-grelots-ad690.cloudfunctions.net/ImgResize")
    Call<Object>  addimg(@Body Map<String,Object> objectMap);




}
