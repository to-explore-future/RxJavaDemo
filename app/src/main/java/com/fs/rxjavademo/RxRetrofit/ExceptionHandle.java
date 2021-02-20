package com.fs.rxjavademo.RxRetrofit;

import android.net.ParseException;

import com.google.gson.JsonParseException;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONException;

import java.net.ConnectException;

import retrofit2.HttpException;

/**
 * 异常处理的类
 */
public class ExceptionHandle {

    private static final int UNAUTHORIZED = 401;            // unauthorized                 : 未经授权的
    private static final int FORBIDDEN = 403;               // forbidden                    : 被禁止的，被禁用的
    private static final int NOT_FOUND = 404;               //                              : 未找到
    private static final int REQUEST_TIMEOUT = 408;         //                              : 请求超时
    private static final int INTERNAL_SERVER_ERROR = 500;   //  internal server error       : 服务器内部出错
    private static final int BAD_GATEWAY = 502;             //  bad gateway                 : 网关问题 ？？
    private static final int SERVICE_UNAVAILABLE = 503;     //  service unavailable         : 服务不可用
    private static final int GATEWAY_TIMEOUT = 504;         //                              : 网关超时

    /**
     * 把错误封装成 ResponeThrowable
     *
     * @param e
     * @return
     */
    public static ResponeThrowable handleException(Throwable e) {
        ResponeThrowable ex;
        if (e instanceof HttpException) {
            HttpException httpException = (HttpException) e;
            ex = new ResponeThrowable(e, ERROR.HTTP_ERROR);

//            Logger.w("ExceptionHandle : " + httpException.code());

            switch (httpException.code()) {
                case UNAUTHORIZED:
                case FORBIDDEN:
                case NOT_FOUND:
                case REQUEST_TIMEOUT:
                case GATEWAY_TIMEOUT:
                case INTERNAL_SERVER_ERROR:
                case BAD_GATEWAY:
                case SERVICE_UNAVAILABLE:
                default:
                    ex.message = "网络错误";
                    break;
            }
            return ex;
        } else if (e instanceof ServerException) {
            ServerException resultException = (ServerException) e;
            ex = new ResponeThrowable(resultException, resultException.code);
            ex.message = resultException.message;
            ex.data = resultException.data;
            return ex;
        } else if (e instanceof JsonParseException || e instanceof JSONException || e instanceof ParseException) {
            ex = new ResponeThrowable(e, ERROR.PARSE_ERROR);
            ex.message = "解析错误";
            return ex;
        } else if (e instanceof ConnectException) {
            ex = new ResponeThrowable(e, ERROR.NETWORD_ERROR);
            ex.message = "连接失败";
            return ex;
        } else if (e instanceof javax.net.ssl.SSLHandshakeException) {
            ex = new ResponeThrowable(e, ERROR.SSL_ERROR);
            ex.message = "证书验证失败";
            return ex;
        } else if (e instanceof ConnectTimeoutException) {
            ex = new ResponeThrowable(e, ERROR.TIMEOUT_ERROR);
            ex.message = "连接超时";
            return ex;
        } else if (e instanceof java.net.SocketTimeoutException) {
            ex = new ResponeThrowable(e, ERROR.TIMEOUT_ERROR);
            ex.message = "连接超时";
            return ex;
        } else {
            ex = new ResponeThrowable(e, ERROR.UNKNOWN);
            ex.message = "未知错误";
            return ex;
        }
    }


    /**
     * 约定异常
     */
    class ERROR {
        /**
         * 未知错误
         */
        public static final int UNKNOWN = 1000;
        /**
         * 解析错误
         */
        public static final int PARSE_ERROR = 1001;
        /**
         * 网络错误
         */
        public static final int NETWORD_ERROR = 1002;
        /**
         * 协议出错
         */
        public static final int HTTP_ERROR = 1003;

        /**
         * 证书出错
         */
        public static final int SSL_ERROR = 1005;

        /**
         * 连接超时
         */
        public static final int TIMEOUT_ERROR = 1006;
    }


    ////////////////Exception/////////////////

    public static class ResponeThrowable extends Exception {
        public int code;
        public String message;
        public String data;

        public ResponeThrowable(Throwable throwable, int code) {
            super(throwable);
            this.code = code;
        }

        public String toString() {
            return "code:" + code + " message:" + message + " data:" + data;
        }
    }

    public static class ServerException extends RuntimeException {
        public int code;
        public String message;
        public String data;

        public ServerException(int code, String msg) {
            this.code = code;
            this.message = msg;
        }

        public void setData(String data) {
            this.data = data;
        }
    }
}

