package cn.itrip.trade.config;

import com.github.wxpay.sdk.IWXPayDomain;
import com.github.wxpay.sdk.WXPayConfig;

import java.io.InputStream;

/**
 * description:
 * Created by Ray on 2019-11-08
 */
public class MyWXPayConfig  extends WXPayConfig {
    private String appID;
    private String mchID;
    private String key;
    private String notifyUrl;
    private String successUrl;
    private String failUrl;

    public void setAppID(String appID) {
        this.appID = appID;
    }

    public void setMchID(String mchID) {
        this.mchID = mchID;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getNotifyUrl() {
        return notifyUrl;
    }

    public void setNotifyUrl(String notifyUrl) {
        this.notifyUrl = notifyUrl;
    }

    public String getSuccessUrl() {
        return successUrl;
    }

    public void setSuccessUrl(String successUrl) {
        this.successUrl = successUrl;
    }

    public String getFailUrl() {
        return failUrl;
    }

    public void setFailUrl(String failUrl) {
        this.failUrl = failUrl;
    }

    @Override
    public String getAppID() {
        return this.appID;
    }

    @Override
    public String getMchID() {
        return this.mchID;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public InputStream getCertStream() {
        //获取证书
        return null;
    }

    @Override
    public IWXPayDomain getWXPayDomain() {
        //获取有效域名（接口地址）
        return WXPayDomainSimpleImpl.instance();
    }
}
