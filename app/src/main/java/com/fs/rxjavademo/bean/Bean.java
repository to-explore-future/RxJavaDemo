package com.fs.rxjavademo.bean;

/**
 * Created by 张强 869518570@qq.com on 2020/6/12
 */
public class Bean {


    /**
     * StatusCode : 200
     * AccessKeyId : STS.NURUrn7jWNjVffT57L2JXfoaN
     * AccessKeySecret : BNEvUu6V7vinLNBAFe8SKH2mnEfEHRkHJGBjL1kKMpbp
     * SecurityToken : CAIS4gJ1q6Ft5B2yfSjIr5bnHsja2rV2+ai9ZEDl0TcZPsV0iarKrDz2IHpIdHVtCeofsvowmmtU5vwSlqB6T55OSAmcNZIoRgbkJMHnMeT7oMWQweEuSPTHcDHhwXeZsebWZ+LmNuS/Ht6md1HDkAJq3LL+bk/Mdle5MJqP+7EFC9MMRVuXYCZhDtVbLRcAzcgBLinpKOqKOBzniXayaU1zoVhYiHhj0a2l3tb+mh3Flw/TwOgPu6HsJoSld8B2IKpnV9C80JYmFMz73TVX9gJB+YpvkaVA4k2nhNyGBERL6Bj0R4im+9Z0fghiffp4SewWovHnieF1ofCUno78jFRvRbgPD3SOG935kJWeQrL2Z48DGOylayiX4LemLYLotg4oW3UfOT5RdsApQn0KUkZwGm2HevX+pA6VPFz/G/LVytI/1Ztk0lPv5sGWIFuCRbqU1ysCM4M7dVkvMxMGKvAyiDyTgGYagAF5YAYmi84BDwRvowU7Hsqn4kXzRwPkAmQGlKsSke8X0kxV57JtQvwS61pLDL5KY0boODddUKnaomvZOVkouChvZL/GyfxLg5n1oMnyDcedOTJFBIf0CdQTedbOmFk+t356t4DSbfIuYcpzc3HccGGA0aPNi1wb6OLi9q2p6GxshQ==
     * Expiration : 2020-06-12T08:48:06Z
     */

    private String StatusCode;
    private String AccessKeyId;
    private String AccessKeySecret;
    private String SecurityToken;
    private String Expiration;

    public String getStatusCode() {
        return StatusCode;
    }

    public void setStatusCode(String StatusCode) {
        this.StatusCode = StatusCode;
    }

    public String getAccessKeyId() {
        return AccessKeyId;
    }

    public void setAccessKeyId(String AccessKeyId) {
        this.AccessKeyId = AccessKeyId;
    }

    public String getAccessKeySecret() {
        return AccessKeySecret;
    }

    public void setAccessKeySecret(String AccessKeySecret) {
        this.AccessKeySecret = AccessKeySecret;
    }

    public String getSecurityToken() {
        return SecurityToken;
    }

    public void setSecurityToken(String SecurityToken) {
        this.SecurityToken = SecurityToken;
    }

    public String getExpiration() {
        return Expiration;
    }

    public void setExpiration(String Expiration) {
        this.Expiration = Expiration;
    }
}
