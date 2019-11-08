package cn.itrip.trade.controller;

import cn.itrip.beans.dto.Dto;
import cn.itrip.beans.pojo.ItripHotelOrder;
import cn.itrip.common.DtoUtil;
import cn.itrip.trade.config.MyWXPayConfig;
import cn.itrip.trade.service.OrderService;
import com.github.wxpay.sdk.WXPay;
import com.github.wxpay.sdk.WXPayUtil;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * description:
 * Created by Ray on 2019-11-08
 */
@RestController
@RequestMapping("/api/wxpay")
public class WxPayController {
    @Resource(name ="wxPayConfig")
    private MyWXPayConfig config;
    @Resource
    private OrderService orderService;
    @RequestMapping(value = "/createqccode/{orderNo}",method = RequestMethod.GET)
    public Dto createQcCode(@PathVariable String orderNo){
        //验证订单号
        if (orderNo == null) {
            return DtoUtil.returnFail("订单号不能为空","400101");
        }
        //
        try {
            WXPay wxpay = new WXPay(config);
            Map<String, String> data = new HashMap<>();
            data.put("body", "爱旅行酒店商品");
            data.put("out_trade_no", orderNo);
            data.put("device_info", "");
            data.put("fee_type", "CNY");
            data.put("total_fee", "1");
            data.put("spbill_create_ip", "123.12.12.123");
            data.put("notify_url", config.getNotifyUrl());
            data.put("trade_type", "NATIVE");  // 此处指定为扫码支付
            data.put("product_id", "12");
            Map<String, String> resp = wxpay.unifiedOrder(data);
            System.out.println(resp);
            if (resp.get("return_code").equals("SUCCESS")) {
                if (resp.get("result_code").equals("SUCCESS")) {
                    String code_url = resp.get("code_url");
                    Map<String, Object> returnData = new HashMap<>();
                    returnData.put("codeUrl", code_url);
                    return DtoUtil.returnDataSuccess(returnData);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }
    @RequestMapping(value = "/notify",method = RequestMethod.POST)
    public void getNotify(HttpServletRequest request, HttpServletResponse response){
        try {
            ServletInputStream is = request.getInputStream();
            BufferedReader br=new BufferedReader(new InputStreamReader(is,"UTF-8"));
            StringBuffer notifyData = new StringBuffer();
            String line=null;
            while((line=br.readLine())!=null){
                notifyData.append(line);
            }
            br.close();
            is.close();
            WXPay wxPay = new WXPay(config);
            Map<String, String> notifyMap = WXPayUtil.xmlToMap(notifyData.toString());  // 转换成map

            if (wxPay.isPayResultNotifySignatureValid(notifyMap)) {
                // 签名正确
                // 进行处理。
                ItripHotelOrder order = new ItripHotelOrder();
                order.setOrderNo(notifyMap.get("out_trade_no"));
                order.setTradeNo(notifyMap.get("transaction_id"));
                order.setPayType(2);
                order.setOrderStatus(2);
                orderService.processUpdateOrderStatus(order);
                // 注意特殊情况：订单已经退款，但收到了支付结果成功的通知，不应把商户侧订单状态从退款改成支付成功
            }
            else {
                // 签名错误，如果数据里没有sign字段，也认为是签名错误
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            //返回数据给微信支付系统，说明我们收到了支付成功的通知
            Map<String, String> returnData = new HashMap<>();
            returnData.put("return_code", "SUCCESS");
            returnData.put("return_msg", "OK");
            try {
                String xmlData = WXPayUtil.mapToXml(returnData);
                response.getWriter().write(xmlData);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
