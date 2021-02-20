package com.fs.rxjavademo.RxRetrofit;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

/**
 * RxJava 调度器
 */
public class RxSchedulers {

    /**
     * 转到主线程
     */
    public static ObservableTransformer toMain() {

        return new ObservableTransformer() {
            @Override
            public ObservableSource apply(Observable observable) {
                return observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
            }
        };
    }

    public static ObservableTransformer dealTransformerDirect() {
        return new ObservableTransformer() {
            @Override
            public ObservableSource apply(Observable upstream) {
                return upstream.onErrorResumeNext(new HttpResponseFunc());
            }
        };
    }

    public static ObservableTransformer dealTransformer(final Type type) {

        return new ObservableTransformer() {
            @Override
            public ObservableSource apply(Observable upstream) {
                return upstream
                        .map(new ApiResultFunc(type)) //解析数据
                        .onErrorResumeNext(new HttpResponseFunc());
            }
        };
    }


    public static ObservableTransformer dealErrorTransformer(final Type type) {

        return new ObservableTransformer() {
            @Override
            public ObservableSource apply(Observable upstream) {
                return upstream
                        .map(new ApiResultFunc(type))  //解析数据
                        .map(new HandleFuc())
                        .onErrorResumeNext(new HttpResponseFunc());
            }
        };
    }


    public static class HttpResponseFunc<T> implements Function<Throwable, Observable<T>> {
        @Override
        public Observable<T> apply(Throwable throwable) throws Exception {
            return Observable.error(ExceptionHandle.handleException(throwable));
        }
    }

    public static class HandleFuc<T> implements Function<BaseResponse<T>, T> {
        @Override
        public T apply(BaseResponse<T> response) throws Exception {
            if (!response.isOk()) {
                ExceptionHandle.ServerException serverException = new ExceptionHandle.ServerException(response.getC(), response.getM());
                serverException.setData(response.getOriginDataS());
                throw serverException;  //这里谁处理呢 ？？？
            }
            return response.getData();
        }
    }

    public static class ApiResultFunc<T> implements Function<ResponseBody, BaseResponse<T>> {
        Type type;

        public ApiResultFunc(Type type) {
            this.type = type;
        }

        @Override
        public BaseResponse<T> apply(ResponseBody responseBody) throws Exception {
            String json = responseBody.string();
            BaseResponse result = parseApiResult(json); //把返回的json 里面的关键数据，封装到BaseResponse中
            if (result != null) {
                if (result.getData() != null) {
                    //再用Gson把剩下的date 变成model 封装到 baseResponse里面去
                    T data = (T) (new Gson().fromJson(result.getData().toString(), type));
                    result.setData(data);
                }
            }
            return result;
        }

        /**
         * 把数据中的 c data m 单独提出来 方便使用
         */
        private BaseResponse parseApiResult(String json) throws JSONException {
            if (json == null || json.length() == 0) {
                return null;
            }
            BaseResponse apiResult = new BaseResponse();
            JSONObject jsonObject = new JSONObject(json);
            if (jsonObject.has("c")) {
                apiResult.setC(jsonObject.getInt("c"));
            }
            if (jsonObject.has("data")) {
                apiResult.setData(jsonObject.getString("data"));
            }
            if (jsonObject.has("m")) {
                apiResult.setM(jsonObject.getString("m"));
            }
            if (!apiResult.isOk()) {
                apiResult.setOriginDataS(jsonObject.getString("data"));
            }
            return apiResult;
        }

    }

}