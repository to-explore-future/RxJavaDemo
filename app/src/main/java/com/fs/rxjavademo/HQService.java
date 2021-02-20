package com.fs.rxjavademo;


import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Url;

public interface HQService {
    String ROOT_URL = Urls.ROOT_URL + "/";
    String ROOT_URL_TEST = "http://192.168.1.147/";

    //h5
    String Order_List = "webpk/order/order_list.html";
    String Order_Search = "webpk/order/order_search.html";
    String Signup_h5 = "webpk/register.html";
    String Hoteldetails_h5 = "webpk/hoteldetails/hoteldetails.html";

    //init
    String Init = "setup/init";
    String CheckVersion = "setup/version";
    String ActiveLog = "logs/active_log";
    String adsClickLog = "logs/adclick_log";
    String getRecommendCity = "ajax/recommend_citys";

    String Login = "user/login";
    String forgotPasswordVerify = "user/forgotPasswordVerify";
    String forgotPasswordReset = "user/forgotPasswordReset";
    String SendMailCode = "user/send_mail_code";
    String SignupForC = "user/apply_c";
    String ChangePassword = "user/update_password";
    String deleteBrowser = "user/del_browser";

    //room
    String RoomList = "hotelorder/hotel_price";

    //coupon
    String MyCoupon = "coupon/my_coupon";
    String GetUserList = "coupon/get_user_list";
    String ReleaseCoupon = "coupon/release_coupon";
    String DispatchCoupon = "coupon/dispath_coupon";

    //city
    String CityDetail = "ajax/city_detail";
    String RecommentCity = "ajax/recommend_citys";
    String Country = "ajax/citizenship_list";
    String HotelSearch = "ajax/hotel_search";
    String KeywordSearch = "ajax/search";
    String CityList = "ajax/city_list";
    String RegionList = "ajax/region_list";

    //hotel
    String HotelList = "ajax/hotel_list";
    String HotelHistory = "ajax/browse_history_list";
    String HotelDetail = "ajax/hotel_detail";
    String FavoritesHotelList = "ajax/favorites_list";
    String getSurplusAmount = "user/get_surplus_amout";
    String HotelComment = "ajax/hotel_comment";
    String HotelMoreComment = "ajax/get_more_comment";
    String HotelFavorites = "ajax/favorites";

    //point
    String userSignin = "points/user_signin";
    String GiftList = "points/my_points";
    String ExchangeRecord = "points/exchange_record";
    String ShortMsgExchange = "points/send_verify";
    String DoExchange = "points/do_exchange";
    String PointDetail = "points/points_details";
    String RecordDetail = "points/record_details";


    //Order
    String ORDER_LIST = "order/order_list";
    String VoucherEmail = "order/voucher_email";
    String CheckRate = "hotelorder/check_rate";
    String GetRequireInfor = "hotelorder/get_require_info";
    String OrderDetail = "order/order_detail";
    String RoomPrice = "hotelorder/room_price";
    String OrderUpload = "order/order_attach_upload";
    String CreateOrder = "hotelorder/create_hq_order";
    String DeleteOrder = "order/delete_order";
    String UnDeleteOrder = "order/undelete_order";
    String CashDesk = "cashier/cashier_desk";
    String UpdateOrder = "hotelorder/update_hq_order";
    String PhoneCode = "ajax/phone_code";
    String ChangeHqOrder = "order/change_order";
    String UpdateDistributor = "order/update_distributor_sn";
    String CancleOrder = "order/cancel_order";

    //Pay
    String UnionPayTN = "pay/order_to_unionpay";
    String UnionPayVerifySign = "pay/unionpay_verify_sign";
    String WXPay = "pay/order_to_wxpay";
    String AliPay = "pay/order_to_alipay";
    String OrderPayReturn = "hotelorder/order_pay_return";
    String advancePayReturn = "pay/order_to_cash_balance_pay";
    String remainPay = "pay/order_clearing_pay";
    String advanceChildPayReturn = "child_pay/order_to_cash_balance_pay";

    String PayOrder = "pay/pay_order";
    //test
    String testURL = "test/android.php";

    //setting
    String OrderNotification = "user/set_order_notification";
    String WorkOrderNotification = "user/set_work_order_notification";
    String UnreadMsgList = "user/unread_msg_list";
    String MsgList = "user/msg_list";
    String MsgDetail = "user/msg_detail";
    String ReadAllMsg = "user/read_all_msg";
    String Feedback = "user/feedback";

    @POST("{filePath}")
    Call<ResponseBody> params(@Path(value = "filePath", encoded = true) String filePath, @Body RequestBody json);

    @POST("{filePath}")
    Observable<ResponseBody> rxParams(@Path(value = "filePath", encoded = true) String filePath, @Body RequestBody json);

    @Multipart
    @POST("{filePath}")
    Observable<ResponseBody> rxParamsUpload(@Path(value = "filePath", encoded = true) String filePath, @Part("userinfo") RequestBody jsonBody, @Part MultipartBody.Part[] file);

    @GET
    Observable<ResponseBody> rxGet(@Url String url);

//	@POST("{filePath}")
//	Observable<BaseResponse<OrderListRespBean>> getOrderList(@Path(value = "filePath",encoded = true) String filePath, @Body RequestBody json);
//
//
//	@POST("order/order_list")
//	Observable<BaseResponse<OrderListRespBean>> getOrderList(@Body RequestBody json);

}
