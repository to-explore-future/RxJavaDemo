package com.fs.rxjavademo;

import com.fs.rxjavademo.RxRetrofit.RetrofitManager;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {

        RetrofitManager.getInstance().get("www.baidu.com", "json", new io.reactivex.Observer<Object>() {
            @Override
            public void onSubscribe(io.reactivex.disposables.Disposable d) {

            }

            @Override
            public void onNext(Object o) {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }


}