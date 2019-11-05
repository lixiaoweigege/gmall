package com.atguigu.gmall.payment.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.atguigu.gmall.anotations.LoginRequired;
import com.atguigu.gmall.bean.OmsOrder;
import com.atguigu.gmall.bean.PaymentInfo;
import com.atguigu.gmall.payment.config.AlipayConfig;
import com.atguigu.gmall.payment.util.HttpClient;
import com.atguigu.gmall.service.OrderService;
import com.atguigu.gmall.service.PaymentService;
import com.github.wxpay.sdk.WXPayUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Controller
public class PaymentController {
    @Reference
    OrderService orderService;
    @Autowired
    AlipayClient alipayClient;
    @Autowired
    PaymentService paymentService;
    // 服务号Id
    @Value("${appid}")
    private String appid;
    // 商户号Id
    @Value("${partner}")
    private String partner;
    // 密钥
    @Value("${partnerkey}")
    private String partnerkey;

    @RequestMapping("/index")
    @LoginRequired
    public String index(HttpServletRequest request, String orderSn, ModelMap modelMap) {
        String userId = (String) request.getAttribute("userId");
        String nickName = (String) request.getAttribute("nickName");

        OmsOrder omsOrder = orderService.getOrderByUserId(userId, orderSn);

        BigDecimal totalAmount = omsOrder.getTotalAmount();

        modelMap.put("orderSn", orderSn);
        modelMap.put("totalAmount", totalAmount);
        return "index";
    }

    //调用支付接口
    @LoginRequired
    @RequestMapping("alipay/submit")
    @ResponseBody
    public String alipay(HttpServletRequest request, String orderSn, ModelMap modelMap) {
        //根据订单号获取订单信息
        OmsOrder omsOrder = orderService.getOrderByOrderSn(orderSn);
        //把订单信息保存到支付信息中
        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setPaymentStatus("未支付");
        paymentInfo.setOrderSn(omsOrder.getOrderSn());
        paymentInfo.setTotalAmount(omsOrder.getTotalAmount());
        paymentInfo.setSubject(omsOrder.getOmsOrderItems().get(0).getProductName());
        paymentService.savePaymentInfo(paymentInfo);

        //请求支付接口
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        //在公共参数中设置回跳和通知地址
        alipayRequest.setReturnUrl(AlipayConfig.return_payment_url);
        alipayRequest.setNotifyUrl(AlipayConfig.notify_payment_url);
        Map<String, Object> map = new HashMap<>();
        map.put("out_trade_no", orderSn);
        map.put("product_code", "FAST_INSTANT_TRADE_PAY");
        //map.put("total_amount", omsOrder.getTotalAmount());
        map.put("total_amount", 0.01);
        map.put("subject", omsOrder.getOmsOrderItems().get(0).getProductName());
        alipayRequest.setBizContent(JSON.toJSONString(map));
        String form = "";
        try {
            form = alipayClient.pageExecute(alipayRequest).getBody();
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        // 设置延迟检查订单支付状态的定时任务
        // 发送一个消息队列的支付检查延迟队列，PAY_CHECK_QUEUE
        paymentService.sendPayCheckQueue(paymentInfo, 8L);
        return form;
    }

    @RequestMapping("alipay/callback/return")
    @LoginRequired
    @ResponseBody
    public String callbackReturn(HttpServletRequest request, ModelMap modelMap) {
        String trade_no = request.getParameter("trade_no");//支付宝交易号
        String out_trade_no = request.getParameter("out_trade_no");//商户订单号
        String total_amount = request.getParameter("total_amount");//交易金额
        String sign = request.getParameter("sign");//签名
        Map<String, String> paramsMap = new HashMap<>(); //将异步通知中收到的所有参数都存放到 map 中
        paramsMap.put("sign", sign);
        //此处异常需要开会讨论，因为没有使用内网穿透，因此此处不做验签
        try {
            boolean signVerified = AlipaySignature.rsaCheckV1(paramsMap, AlipayConfig.alipay_public_key, AlipayConfig.charset, AlipayConfig.sign_type); //调用SDK验证签名
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
// 更新支付信息业务
        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setOrderSn(out_trade_no);
        paymentInfo.setPaymentStatus("已支付");
        paymentInfo.setAlipayTradeNo(trade_no);
        paymentInfo.setCallbackContent(request.getQueryString());
        paymentInfo.setCallbackTime(new Date());
        paymentService.update(paymentInfo);
        // 更新订单信息业务等其他系统业务
        // 发送系统消息队列，通知gmall系统某outTradeNo已经支付成功
        paymentService.sendPaySuccessQueue(paymentInfo);

        return "redirect:/finish.html";
    }
    @RequestMapping("wx/submit")
    @ResponseBody
    public Map createNative(String orderSn) {
        // 做一个判断：支付日志中的订单支付状态 如果是已支付，则不生成二维码直接重定向到消息提示页面！
        // 调用服务层数据
        // 第一个参数是订单Id ，第二个参数是多少钱，单位是分
        if(orderSn.length()>32){
            orderSn = orderSn.substring(30);
        }
        Map map = createNative(orderSn + "", "1");
        System.out.println(map.get("code_url"));
        // data = map
        return map;
    }
    //创建支付订单并生成二维码
    public Map createNative(String orderId, String total_fee) {
        //1.创建参数
        Map<String, String> param = new HashMap();//创建参数
        param.put("appid", appid);//公众号
        param.put("mch_id", partner);//商户号
        param.put("nonce_str", WXPayUtil.generateNonceStr());//随机字符串
        param.put("body", "尚硅谷");//商品描述
        param.put("out_trade_no", orderId);//商户订单号
        param.put("total_fee", total_fee);//总金额（分）
        param.put("spbill_create_ip", "127.0.0.1");//IP
        param.put("notify_url", " http://2z72m78296.wicp.vip/wx/callback/notify");//回调地址(随便写)
        param.put("trade_type", "NATIVE");//交易类型
        try {
            //2.生成要发送的xml
            String xmlParam = WXPayUtil.generateSignedXml(param, partnerkey);
            System.out.println(xmlParam);
            HttpClient client = new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");
            client.setHttps(true);
            client.setXmlParam(xmlParam);
            client.post();
            //3.获得结果
            String result = client.getContent();
            System.out.println(result);
            Map<String, String> resultMap = WXPayUtil.xmlToMap(result);
            Map<String, String> map = new HashMap<>();
            map.put("code_url", resultMap.get("code_url"));//支付地址
            map.put("total_fee", total_fee);//总金额
            map.put("out_trade_no", orderId);//订单号
            return map;
        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }

}
