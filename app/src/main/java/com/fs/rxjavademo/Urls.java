package com.fs.rxjavademo;


public class Urls {
    public static String ROOT_URL;

    public static String getDownloadURL() {
        return ROOT_URL + "/setup/download";
    }

    public static String getAboutUsURL() {
        return ROOT_URL + "/setup/about";
    }

    public static String getCouponRuleURL() {
        return ROOT_URL + "/coupon/coupon_rule";
    }

    public static String getServiceSafeURL() {
        return ROOT_URL + "/setup/service_safe";
    }

    public static String getServiceProvisionURL() {
        return ROOT_URL + "/setup/service_provision";
    }

    public static String getPrivacyDeclareURL() {
        return ROOT_URL + "/setup/privacy_declare";
    }

    public static String getOrderConfirmURL() {
        return ROOT_URL + "/order/confirm";
    }

    public static String getOrderInvoiceURL() {
        return ROOT_URL + "/order/voucher";
    }

    public static String getOrderVoucherURL() {
        return ROOT_URL + "/order/voucher";
    }

    public static String getPointRuleURL() {
        return ROOT_URL + "/points/points_rule";
    }

    public static String getInitURL() {
        return ROOT_URL + "/setup/init";
    }
}
