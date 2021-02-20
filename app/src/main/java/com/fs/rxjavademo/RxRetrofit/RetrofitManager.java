package com.fs.rxjavademo.RxRetrofit;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


import com.fs.rxjavademo.HQService;

import java.io.File;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observer;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.logging.HttpLoggingInterceptor.Level;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
//import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitManager {

//    public static String baseUrl = HQService.ROOT_URL;
    public static String baseUrl = HQService.ROOT_URL_TEST;

    private static RetrofitManager instance = null;

    private static OkHttpClient.Builder httpClient = null;

    private static Retrofit retrofit;

    private static HQService hqService;

    private RetrofitManager() {

    }


    /**
     * 线程不安全，
     *
     * @return
     */
    public static RetrofitManager getInstance() {
        if (instance == null) {
            instance = new RetrofitManager();
            init();
        }
        return instance;
    }

    private static void init() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        // set your desired log level
        logging.setLevel(Level.BODY);

        httpClient = new OkHttpClient.Builder().connectTimeout(30, TimeUnit.SECONDS).
                readTimeout(30, TimeUnit.SECONDS).
                writeTimeout(30, TimeUnit.SECONDS);

        httpClient.addInterceptor(logging);

        retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
//                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();

        hqService = retrofit.create(HQService.class);
    }

//    public static boolean isNetworkAvailable() {
//        ConnectivityManager manager = (ConnectivityManager) HQHotelApplication.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo info = manager.getActiveNetworkInfo();
//        return (info != null && info.isAvailable());
//    }

    public <T> void get(String url, String json, Observer<T> observer) {
        hqService.rxGet(url)
                .compose(RxSchedulers.toMain())
                .compose(RxSchedulers.dealErrorTransformer(getType(observer.getClass())))
                .subscribe(observer);
    }

    public <T> void post(String url, String json, Observer<T> observer) {
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), json);

        hqService.rxParams(url, body)
                .compose(RxSchedulers.toMain())
                .compose(RxSchedulers.dealErrorTransformer(getType(observer.getClass())))
                .subscribeWith(observer);
    }

    public <T> void postImage(String url, String json, File[] files, Observer<T> observer) {
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), json);

        int fileSize = files.length;
        MultipartBody.Part[] multipartBodyPart = new MultipartBody.Part[fileSize];

        for (int i = 0; i < fileSize; i++) {
            RequestBody requestFile = RequestBody.create(okhttp3.MediaType.parse("image/png"), files[i]);
            multipartBodyPart[i] = MultipartBody.Part.createFormData("Filedata[]", files[i].getName(), requestFile);
        }

        hqService.rxParamsUpload(url, body, multipartBodyPart)
                .compose(RxSchedulers.toMain())
                .compose(RxSchedulers.dealErrorTransformer(getType(observer.getClass())))
                .subscribeWith(observer);

    }

    public <T> void postWithoutParseCode(String url, String json, Observer<T> observer) {
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), json);

        hqService.rxParams(url, body)
                .compose(RxSchedulers.toMain())
                .compose(RxSchedulers.dealTransformer(getType(observer.getClass())))
                .subscribeWith(observer);
    }

    public <T> void postWithResponseBody(String url, String json, Observer<T> observer) {
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), json);

        hqService.rxParams(url, body)
                .compose(RxSchedulers.toMain())
                .compose(RxSchedulers.dealTransformerDirect())
                .subscribeWith(observer);
    }


    private Type getType(Class clazz) {
        Type genType = clazz.getGenericSuperclass();

        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        Type type = params[0];
        Type finalNeedType;
        if (params.length > 1) {
            if (!(type instanceof ParameterizedType)) throw new IllegalStateException("没有填写泛型参数");
            finalNeedType = ((ParameterizedType) type).getActualTypeArguments()[0];
        } else {
            finalNeedType = type;

            //Company<User<Group>>处理类似多层嵌套，取Group
            if (type instanceof ParameterizedType) {
                Type[] innerParams = ((ParameterizedType) type).getActualTypeArguments();
                if (innerParams.length > 0) {
                    finalNeedType = innerParams[0];
                }
            }
        }
        return finalNeedType;
    }


}
