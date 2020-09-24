package com.fs.rxjavademo;

import android.os.Bundle;
import android.util.Log;

import com.fs.rxjavademo.bean.Bean;
import com.google.gson.Gson;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import androidx.appcompat.app.AppCompatActivity;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Notification;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Action;
import io.reactivex.rxjava3.functions.BiFunction;
import io.reactivex.rxjava3.functions.BiPredicate;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.functions.Predicate;
import io.reactivex.rxjava3.functions.Supplier;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy-mm-dd hh-mm-ss", Locale.CHINESE);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        create();
        switchThread();
        demo3();
        just();
        fromArray();
        defer();
        timer();
        interval();
        intervalRange();
        range();
        map();
        flatMap();
        concatMap();
        buffer();
        concat();
        concatArray();
        merger();
        mergeArray();
        concatDelayError();
        zip();
        combineLatest();
        reduce();
        startWith();
        count();
        delay();
        doIt();
        onErrorReturn();
        onErrorResumeNext();
        retry();
        retry(3);
        retry(new Predicate() {
            @Override
            public boolean test(Object o) throws Throwable {
                return false;
            }
        });
        retry(new BiPredicate() {
            @Override
            public boolean test(Object o, Object o2) throws Throwable {
                return false;
            }
        });
    }

    /**
     * 最基本的使用
     */
    void create() {
        ObservableOnSubscribe<Integer> source = new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> emitter) throws Throwable {
                Log.d(TAG, "Observable emit 1" + "\n");
                emitter.onNext(1);
                Log.d(TAG, "Observable emit 2" + "\n");
                emitter.onNext(2);
                Log.d(TAG, "Observable emit 3" + "\n");
                emitter.onNext(3);
                Log.d(TAG, "Observable emit 4" + "\n");
                emitter.onNext(4);
            }
        };

        Subscriber<Integer> subscriber = new Subscriber<Integer>() {

            @Override
            public void onSubscribe(Subscription s) {

            }

            @Override
            public void onNext(Integer integer) {

            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onComplete() {

            }
        };
        Observer<Integer> observer = new Observer<Integer>() {

            private Disposable mDisposable;

            @Override
            public void onSubscribe(@NonNull Disposable disposable) {
                mDisposable = disposable;
            }

            @Override
            public void onNext(@NonNull Integer integer) {
                Log.d(TAG, "onNext:param:" + integer);
                if (integer == 2) {
                    mDisposable.dispose();  //解除订阅关系
                }
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };
        Observable.create(source).subscribe(observer);
    }

    /**
     * subscribeOn():指发射事件的线程,多次指定只有第一次是有效的
     * observerOn():订阅者接受事件的线程,每次指定都有效
     */
    void switchThread() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> emitter) throws Throwable {
                Log.e(TAG, "Observable thread is :" + Thread.currentThread().getName());
                emitter.onNext(1);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.newThread())  //在Schedulers.newThread()线程发射
                .subscribeOn(Schedulers.io())   //在Schedulers.io()线程发射
                .observeOn(AndroidSchedulers.mainThread())  //在Schedulers.newThread()线程接收
                .doOnNext(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Throwable {
                        Log.d(TAG, "After observeOn(AndroidSchedulers.mainThread()),current thread is :" + Thread.currentThread().getName());
                    }
                })
                .observeOn(Schedulers.io())                 //在Schedulers.io()线程接收
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Throwable {
                        Log.d(TAG, "After observeOn(Schedulers.io()),current thread is :" + Thread.currentThread().getName());
                    }
                });
    }

    void demo3() {
        Observable.create(new ObservableOnSubscribe<Response>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Response> emitter) throws Throwable {
                Log.d(TAG, "action:\tcreate" + "\tcurrent thread:" + Thread.currentThread().getName());
                Request.Builder builder = new Request.Builder()
                        .url("http://49.234.116.125:7080/")
                        .get();
                Request request = builder.build();
                Call call = new OkHttpClient().newCall(request);
                Response response = call.execute();
                emitter.onNext(response);
            }
        }).map(response -> {
            Log.d(TAG, "action:\tmap" + "\tcurrent thread:" + Thread.currentThread().getName());
            if (response.isSuccessful()) {
                ResponseBody body = response.body();
                if (body != null) {
                    Log.d(TAG, "map:转换前:" + response.body().toString());
                    return new Gson().fromJson(body.toString(), Bean.class);
                }
            }
            return null;
        }).observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<Bean>() {
                    @Override
                    public void accept(Bean bean) throws Throwable {
                        Log.d(TAG, "action:\tdoOnNext" + "\tcurrent thread:" + Thread.currentThread().getName());
                        Log.d(TAG, "对bean进行一系列的操作 ...");

                    }
                }).observeOn(AndroidSchedulers.mainThread())

                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<Bean>() {
                    @Override
                    public void accept(Bean bean) throws Throwable {

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Throwable {

                    }
                });
    }

    /**
     * just的使用
     * 快速创建被观察者对象,并且自动发射
     * just方法会帮我们自动创建Observable,并且自动调用onNext方法,并且所有的都发射完毕之后,还会调用onComplete()
     */
    void just() {
        Observable.just(1, 2, 3, 4).subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(@NonNull Disposable disposable) {
                Log.d(TAG, "just:-->\t\tonSubscribe:" + disposable);
            }

            @Override
            public void onNext(@NonNull Integer integer) {
                Log.d(TAG, "just:-->\t\tonNext:" + integer);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.d(TAG, "just:-->\t\tonError:" + e.toString());
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "just:-->\t\tonComplete:");
            }
        });
    }

    /**
     * fromArray()比just更高级
     * just把里面的一个个元素发射出来
     * fromArray如果你传入的是一个数组或者集合,被观察者会把这个数组或者集合中的元素一个一个发射出来
     * 如果你传入的是一组数组或者集合,被观察者会把这组数组或者集合中的每个数组或者集合发射出来.
     */
    void fromArray() {
        String array1[] = new String[]{"a", "b", "c", "d", "e"};
        String array2[] = new String[]{"a", "b", "c", "d", "e"};

        Observable.fromArray(array1).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(@NonNull String s) {
                Log.d(TAG, "fromArray-->" + s);
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });

        Observable.fromArray(array1, array2).subscribe(new Observer<String[]>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(String @NonNull [] strings) {

            }

            @Override
            public void onError(@NonNull Throwable e) {

            }

            @Override
            public void onComplete() {

            }

        });


        List<String> list = new ArrayList<>();
        list.add("a");
        list.add("b");
        list.add("c");
        list.add("d");
        list.add("e");
        Observable.fromArray(list).subscribe(new Observer<List<String>>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(@NonNull List<String> strings) {

            }

            @Override
            public void onError(@NonNull Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    /**
     * fromIterator:可以迭代的
     */
    void fromItarable() {
        List<String> list = new ArrayList<>();
        list.add("a");
        list.add("b");
        list.add("c");
        list.add("d");
        list.add("e");
        Observable.fromIterable(list).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(@NonNull String s) {

            }

            @Override
            public void onError(@NonNull Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    /**
     * 一些测试方法:empty(),error(),never();
     */
    void emptyErrorNever() {
        /**
         * empty():只发射onComplete()事件
         */
        Observable.empty().subscribe(new Observer<Object>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(@NonNull Object o) {

            }

            @Override
            public void onError(@NonNull Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });

        /**
         * error():只发射onError事件,支持自定义Error
         */
        Observable.error(new RuntimeException()).subscribe(new Observer<Object>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(@NonNull Object o) {

            }

            @Override
            public void onError(@NonNull Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });

        /**
         * 什么事件都不发射
         */
        Observable.never().subscribe(new Observer<Object>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(@NonNull Object o) {

            }

            @Override
            public void onError(@NonNull Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });

    }


    Integer i = 10;
    Integer j = 100;

    /**
     * 延迟方法
     * defer:不太明白
     */
    void defer() {

        /**
         * defer:只有订阅的时候才创建被观察者
         */
        @NonNull Observable<Integer> observable = Observable.defer(new Supplier<ObservableSource<Integer>>() {
            @Override
            public ObservableSource<Integer> get() throws Throwable {
                @NonNull Observable<Integer> just = Observable.just(i);
                i = 30;
                return just;
            }
        });

        observable.subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(@NonNull Integer integer) {
                Log.d(TAG, "接收到的数据:" + integer);
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });

        /**
         * 注意:如果上面使用的不是defer()方法,那么结果就不是30,而是10
         *
         */
        @NonNull Observable<Integer> observable1 = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> emitter) throws Throwable {
                emitter.onNext(j);
                j = 200;
            }
        });

        observable1.subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(@NonNull Integer integer) {
                Log.d(TAG, "非defer方法发射,接收到的数据:" + integer);
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    /**
     * 过一段时间做 ...
     */
    void timer() {
        Observable.timer(1, TimeUnit.SECONDS).subscribe(new Observer<Long>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(@NonNull Long aLong) {
                Log.d(TAG, "long:" + aLong);
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    /**
     * 每隔一段时间,发送事件
     * ? : 怎么停止发射呢
     */
    void interval() {
        Observable.interval(3, 1, TimeUnit.SECONDS).subscribe(new Observer<Long>() {

            private Disposable mDisposable;

            @Override
            public void onSubscribe(@NonNull Disposable d) {
                mDisposable = d;
            }

            @Override
            public void onNext(@NonNull Long aLong) {

                Log.d(TAG, "onNext:第" + aLong + "次:\t" + getCurrentDateStr());
                if (aLong == 5 && !mDisposable.isDisposed()) {
                    mDisposable.dispose(); //接触订阅关系 ? 但是被观察者应该还在发射
                }
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }

            @Override
            public void onComplete() {
                Log.d(TAG, "停止了");
            }
        });
    }


    String getCurrentDateStr() {
        return mSimpleDateFormat.format(new Date());
    }

    /**
     * 由于interval会一直发送下去,除非解除订阅关系
     * intervalRange会指定一个发送的范围,超出范围不再发送
     */
    void intervalRange() {
        Observable.intervalRange(3, 5, 3, 1, TimeUnit.SECONDS).subscribe(new Observer<Long>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(@NonNull Long aLong) {
                Log.d(TAG, "intervalRange:onNext:第" + aLong + "次:\t" + getCurrentDateStr());
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }

            @Override
            public void onComplete() {
                Log.d(TAG, "停止了");
            }
        });
    }

    /**
     * 这个没有时间间隔,会快速发射,
     */
    void range() {
        Observable.range(3, 10).subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(@NonNull Integer integer) {
                Log.d(TAG, "range:onNext:第" + integer + "次:\t" + getCurrentDateStr());
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }

            @Override
            public void onComplete() {
                Log.d(TAG, "range:onComplete:");

            }
        });
    }

    void map() {
        Observable.just(1, 3, 5, 7).map(new Function<Integer, String>() {
            @Override
            public String apply(Integer integer) throws Throwable {
                return "integer:" + String.valueOf(integer);
            }

        }).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(@NonNull String s) {
                Log.d(TAG, "map:onNext:" + s);
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }

            @Override
            public void onComplete() {
                Log.d(TAG, "map:onComplete:");
            }
        });
    }

    /**
     * 教程说flatMap是无序的,我做出的测试无法验证
     */
    void flatMap() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> emitter) throws Throwable {
                emitter.onNext(2);
                emitter.onNext(1);
                emitter.onNext(3);
            }
        }).flatMap(new Function<Integer, ObservableSource<String>>() {
            @Override
            public ObservableSource<String> apply(Integer integer) throws Throwable {
                ArrayList<String> list = new ArrayList<>();
                for (int k = 0; k < 3; k++) {
                    Thread.sleep((int) (Math.random() * 3) * 1000);
                    list.add("我是事件" + integer + "产生的子事件" + k);
                }
                return Observable.fromIterable(list);
            }
        }).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(@NonNull String s) {
                Log.d(TAG, "flatMap:onNext" + s);
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }

            @Override
            public void onComplete() {
                Log.d(TAG, "flatMap:onComplete");
            }
        });
    }

    /**
     * 这个是有序的
     */
    void concatMap() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> emitter) throws Throwable {
                emitter.onNext(2);
                emitter.onNext(1);
                emitter.onNext(3);
            }
        }).concatMap(new Function<Integer, ObservableSource<String>>() {
            @Override
            public ObservableSource<String> apply(Integer integer) throws Throwable {
                ArrayList<String> list = new ArrayList<>();
                for (int k = 0; k < 3; k++) {
                    if (k == 1) {
                        Thread.sleep(1000);
                    }
                    list.add("我是事件" + integer + "产生的子事件" + k);
                }
                return Observable.fromIterable(list);
            }
        }).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(@NonNull String s) {
                Log.d(TAG, "concatMap:onNext" + s);
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }

            @Override
            public void onComplete() {
                Log.d(TAG, "flatMap:onComplete");
            }
        });
    }

    /**
     * 设置缓冲区
     */
    void buffer() {
        Observable.just(1, 3, 3, 4, 5).buffer(3, 1).subscribe(new Observer<List<Integer>>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(@NonNull List<Integer> integers) {
                Log.d(TAG, "缓冲区的长度:" + integers.size());
                Log.d(TAG, "缓冲区中的元素:" + integers.toString());
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    /**
     *
     */
    void concat() {
        Observable.concat(Observable.just(1, 2, 3)
                , Observable.just(4, 5, 6)
                , Observable.just(7, 8, 9)
                , Observable.just(10, 11, 12))
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull Integer integer) {
                        Log.d(TAG, "concat:onNext:" + integer);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "concat:onComplete:");
                    }
                });


    }

    /**
     *
     */
    void concatArray() {
        Observable.concatArray(Observable.just(1, 2, 3)
                , Observable.just(4, 5, 6)
                , Observable.just(7, 8, 9)
                , Observable.just(10, 11, 12)
                , Observable.just(13, 14, 15))
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull Integer integer) {
                        Log.d(TAG, "concat:onNext:" + integer);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "concat:onComplete:");
                    }
                });
    }

    void merger() {
        Observable.merge(Observable.intervalRange(0, 3, 1, 1, TimeUnit.SECONDS)
                , Observable.intervalRange(3, 3, 1, 1, TimeUnit.SECONDS))
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull Long aLong) {
                        Log.d(TAG, "merge:onNext:" + aLong);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete");
                    }
                });
    }

    /**
     * 谁抢在在前面就先发送谁
     */
    void mergeArray() {
        Observable.mergeArray(Observable.intervalRange(0, 3, 1, 1, TimeUnit.SECONDS)
                , Observable.intervalRange(3, 3, 1, 1, TimeUnit.SECONDS)
                , Observable.intervalRange(6, 3, 1, 1, TimeUnit.SECONDS)
                , Observable.intervalRange(9, 3, 1, 1, TimeUnit.SECONDS))
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull Long aLong) {
                        Log.d(TAG, "mergeArray:onNext:" + aLong);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete");
                    }
                });
    }

    /**
     * 延时处理error
     */
    void concatDelayError() {

        Observable.concatArrayDelayError(
                Observable.create(new ObservableOnSubscribe<Integer>() {
                    @Override
                    public void subscribe(@NonNull ObservableEmitter<Integer> emitter) throws Throwable {
                        emitter.onNext(1);
                        emitter.onNext(2);
                        emitter.onNext(3);
                        emitter.onError(new RuntimeException("自制error"));
                    }
                }), Observable.just(4, 5, 6)
        ).subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(@NonNull Integer integer) {
                Log.d(TAG, "concatDelayError:onNext:" + integer);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.d(TAG, "接收到error:" + e.toString());
            }

            @Override
            public void onComplete() {

            }
        });
    }


    void zip() {
        @NonNull Observable<Integer> observable2 = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> emitter) throws Throwable {
                emitter.onNext(1);
                //zip:合并是一一对应的,不管这里耗时多长,都会一一对应
                Thread.sleep(1000);
                emitter.onNext(2);
                emitter.onNext(3);
                emitter.onNext(4);

                emitter.onNext(5);
            }
        });

        @NonNull Observable<String> observable1 = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> emitter) throws Throwable {
                emitter.onNext("5");
                emitter.onNext("6");
                emitter.onNext("7");
                emitter.onNext("8");
            }
        });
        Observable.zip(observable1, observable2, new BiFunction<String, Integer, String>() {
            @Override
            public String apply(String s, Integer integer) throws Throwable {
                return s + "====" + integer;
            }
        }).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(@NonNull String s) {
                Log.d(TAG, "zip:onNext:" + s);
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    /**
     *
     */
    void combineLatest() {
        @NonNull Observable<String> observable1 = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> emitter) throws Throwable {
                emitter.onNext("1");
                emitter.onNext("2");
                emitter.onNext("3");
            }
        });

        @NonNull Observable<String> observable2 = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> emitter) throws Throwable {
                emitter.onNext("tom");
                emitter.onNext("jack");
                emitter.onNext("juli");
            }
        });

        Observable.combineLatest(observable1, observable2, new BiFunction<String, String, String>() {
            @Override
            public String apply(String s, String s2) throws Throwable {
                return s + "========" + s2;
            }
        }).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(@NonNull String s) {
                Log.d(TAG, s);
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    /**
     *
     */
    void reduce() {
        Observable.just(1, 2, 3, 4).reduce(new BiFunction<Integer, Integer, Integer>() {
            @Override
            public Integer apply(Integer integer, Integer integer2) throws Throwable {
                Log.d(TAG, "本次要合并的数据:" + integer + "===" + integer2);
                return integer * integer2;
            }
        }).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Throwable {
                Log.d(TAG, "接收到的数据:" + integer);
            }
        });
    }

    void collect() {
        // todo
    }

    void startWith() {
        Observable.just(5, 6, 7)
                .startWithItem(0)
                .startWithArray(1, 2, 3)
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull Integer integer) {
                        Log.d(TAG, " " + integer);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**
     * 同级发送的数量
     */
    void count() {
        Observable.just(1, 2, 3, 4).count().subscribe(new SingleObserver<Long>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onSuccess(@NonNull Long aLong) {
                Log.d(TAG, "数量:" + aLong);

            }

            @Override
            public void onError(@NonNull Throwable e) {

            }
        });
    }

    /**
     * 延迟发射
     */
    void delay() {
        Observable.just(1, 2, 3)
                .delay(3, TimeUnit.SECONDS)
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull Integer integer) {
                        Log.d(TAG, "延迟发射" + integer);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**
     * 操作符
     */
    void doIt() {
        Observable.just(1, 2, 3, 4)
                //当Observable每发送一次数据事件就会调用一次
                .doOnEach(new Consumer<Notification<Integer>>() {
                    @Override
                    public void accept(Notification<Integer> integerNotification) throws Throwable {
                        Log.d(TAG, "doOnEach():每次发送数据事件都会调用");
                    }
                })
                //执行onNext事件之前调用
                .doOnNext(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Throwable {
                        Log.d(TAG, "doOnNext():执行onNext事件之前调用");
                    }
                })
                //执行onNext事件之后执行
                .doAfterNext(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Throwable {
                        Log.d(TAG, "doAfterNext():执行onNext事件之后执行");
                    }
                })
                //Observable正常发送事件完毕后调用
                .doOnComplete(new Action() {
                    @Override
                    public void run() throws Throwable {
                        Log.d(TAG, "doOnComplete():Observable正常发送事件完毕后调用");
                    }
                })
                //Observable发送错误事件的时候调用
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Throwable {
                        Log.d(TAG, "doOnError():Observable发送错误事件的时候调用");
                    }
                })
                //观察者订阅时调用
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Throwable {
                        Log.d(TAG, "doOnSubscribe():观察者订阅时调用");
                    }
                })
                //Observable发送事件完毕之后调用,无论正常发送完毕 / 异常终止
                .doOnTerminate(new Action() {
                    @Override
                    public void run() throws Throwable {
                        Log.d(TAG, "doOnTerminate():Observable发送事件完毕之后调用,无论正常发送完毕 / 异常终止");
                    }
                })
                .doFinally(new Action() {
                    @Override
                    public void run() throws Throwable {
                        Log.d(TAG, "doFinally():");
                    }
                })
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull Integer integer) {
                        Log.d(TAG, "onNext:" + integer);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**
     * 遇到错误发送一个正常的事件,然后结束
     */
    void onErrorReturn() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> emitter) throws Throwable {
                emitter.onNext(1);
                emitter.onNext(2);
                emitter.onError(new Throwable("发生错误了"));
                //后面的事件停止发射
                emitter.onNext(3);
            }
        }).onErrorReturn(new Function<Throwable, Integer>() {
            @Override
            public Integer apply(Throwable throwable) throws Throwable {
                Log.e(TAG, "发生错误拦截到了,这样观察者就不至于走onError,在这里重新发射一个正常的事件");
                return 555;
            }
        }).subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(@NonNull Integer integer) {
                Log.d(TAG, "onNext:" + integer);
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    /**
     * 发生错误生成一个新的Observable,接着发射
     */
    void onErrorResumeNext() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> emitter) throws Throwable {
                emitter.onNext(1);
                emitter.onNext(2);
                emitter.onError(new Throwable("制造了一个错误"));
            }
        }).onErrorResumeNext(new Function<Throwable, ObservableSource<? extends Integer>>() {
            @Override
            public ObservableSource<? extends Integer> apply(Throwable throwable) throws Throwable {
                Log.e(TAG, "错误被拦截,然后发送一个新的Observable");
                return Observable.just(6, 7, 8);
            }
        }).subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(@NonNull Integer integer) {
                Log.d(TAG, "onErrorResumeNext:onNext:" + integer);
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }


    void retry() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> emitter) throws Throwable {
                emitter.onNext(1);
                emitter.onNext(2);
                emitter.onError(new Throwable("人造错误"));
                emitter.onNext(3);
            }
        }).retry().subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(@NonNull Integer integer) {
                Log.d(TAG, "retry:onNext:" + integer);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.e(TAG, "发现一次错误");
            }

            @Override
            public void onComplete() {

            }
        });
    }

    /**
     * 显示重试的次数
     *
     * @param time
     */
    void retry(long time) {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> emitter) throws Throwable {
                emitter.onNext(1);
                emitter.onNext(2);
                emitter.onError(new Throwable("人造错误"));
                emitter.onNext(3);
            }
        }).retry(time).subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(@NonNull Integer integer) {
                Log.d(TAG, "retry:onNext:" + integer);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.e(TAG, "发现一次错误");
            }

            @Override
            public void onComplete() {

            }
        });
    }

    int b = 0;

    void retry(Predicate predicate) {

        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> emitter) throws Throwable {
                emitter.onNext(1);
                emitter.onNext(2);
                emitter.onError(new Error("自定义Error"));
                emitter.onNext(3);
            }
        }).retry(new Predicate<Throwable>() {
            @Override
            public boolean test(Throwable throwable) throws Throwable {
                //通过判断这个异常 ,决定是否重新发送事件
                if (throwable.getMessage().equals("自定义Error") && b < 3) {
                    b++;
                    return true;
                }
                return false;
            }
        }).subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(@NonNull Integer integer) {
                Log.d(TAG, "retry:Predicat:onNext:" + integer);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.e(TAG, "retry:Predicat:发现一次错误");
            }

            @Override
            public void onComplete() {

            }
        });
    }

    /**
     * 这个会把重试的次数回调到 test中
     *
     * @param biPredicate
     */
    void retry(BiPredicate biPredicate) {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> emitter) throws Throwable {
                emitter.onNext(1);
                emitter.onNext(2);
                emitter.onError(new Error("自定义Error"));
                emitter.onNext(3);
            }
        }).retry(new BiPredicate<Integer, Throwable>() {
                     @Override
                     public boolean test(@NonNull Integer integer, @NonNull Throwable throwable) throws Throwable {
                         return integer <= 3;
                     }
                 }
        ).subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(@NonNull Integer integer) {
                Log.d(TAG, "retry:BiPredicate:onNext:" + integer);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.e(TAG, "retry:BiPredicate:发现一次错误");
            }

            @Override
            public void onComplete() {

            }
        });
    }


}