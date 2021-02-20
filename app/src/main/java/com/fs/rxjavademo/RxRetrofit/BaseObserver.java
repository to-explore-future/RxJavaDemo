package com.fs.rxjavademo.RxRetrofit;

import android.content.Context;
import android.util.Log;


import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import retrofit2.HttpException;

/**
 * 一般只用处理三个方法
 *
 * @Override public void onNext()
 * @Override public void onServerException(ExceptionHandle.ResponeThrowable e)
 * @Override public void onNetworkException()
 */
public abstract class BaseObserver<T> implements Observer<T> {

    private Context context;

    public BaseObserver(Context context) {
        this.context = context;
    }

    @Override
    public void onError(Throwable e) {

        if (e instanceof ExceptionHandle.ResponeThrowable) {
            ExceptionHandle.ResponeThrowable responeThrowable = (ExceptionHandle.ResponeThrowable) e;
            if (responeThrowable.getCause() instanceof ExceptionHandle.ServerException) { //服务器数据错误
                onServerException((ExceptionHandle.ResponeThrowable) e);
            } else if (responeThrowable.getCause() instanceof HttpException) {
                onHttpException();
            } else if (((ExceptionHandle.ResponeThrowable) e).code == ExceptionHandle.ERROR.NETWORD_ERROR) {
                onConnectException();
            } else if (((ExceptionHandle.ResponeThrowable) e).code == ExceptionHandle.ERROR.TIMEOUT_ERROR) {
                onSocketTimeoutException();
            } else {
                onUnKnowHTTPException();//一般是返回字段Data格式错误,解析错误，还可能网络假连接
            }
        } else {
            onUnKnowException();
        }
    }

    @Override
    public void onSubscribe(Disposable d) {
    }

    @Override
    public void onComplete() {
    }

    /**
     * 连接错误
     */
    public void onConnectException() {
//        HQTool.toastNoNet();
        onNetworkException();
    }

    /**
     * 超时错误
     */
    public void onSocketTimeoutException() {
//        HQTool.toastNoNet();
        onNetworkException();
    }

    public void onUnKnowHTTPException() {
//        HQTool.toastNoNet();
    }

    public void onUnKnowException() {
//        HQTool.toastUnknownError();
    }

    /**
     * HTTP错误
     */
    public void onHttpException() {
//        HQTool.toastNoNet();
        onNetworkException();
    }

    /**
     * 请求正常，服务器返回码错误，最好处理
     */
    public void onServerException(ExceptionHandle.ResponeThrowable e) {
        if (e.code == 20206) {
            Log.d("loginpresent", "onServerException");
//            AccountManager.getInstance().logout(context);
//            HQTool.toast(context, e.message);   //账户失效，请重新登录
//            AccountManager.getInstance().toSignInupActivity(context);
        }
//        }else if(e.code == 20204){
//            AccountManager.getInstance().logout(context);
//            HQTool.toast(context, "账户异常");
//            AccountManager.getInstance().toSignInupActivity(context);
//            return;
//        }
    }

    /**
     * 网络错误，集中处理的地方，避免处理函数太多
     */
    public void onNetworkException() {
//        Logger.w("onNetworkException()");
    }
}
