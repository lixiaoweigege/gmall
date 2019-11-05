package com.atguigu.gmall.order.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.anotations.LoginRequired;
import com.atguigu.gmall.bean.*;
import com.atguigu.gmall.service.CartService;
import com.atguigu.gmall.service.OrderService;
import com.atguigu.gmall.service.SkuService;
import com.atguigu.gmall.service.UserService;
import com.atguigu.gmall.util.PriceUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
public class OrderController {
    @Reference
    UserService userService;
    @Reference
    CartService cartService;
    @Reference
    OrderService orderService;
    @Reference
    SkuService skuService;


    //跳转到结算页面
    @LoginRequired(isNeededSuccess = true)
    @RequestMapping("/toTrade")
    public String toTrade(HttpServletRequest request, ModelMap modelMap) {
        String userId = (String) request.getAttribute("userId");
        String nickName = (String) request.getAttribute("nickName");
        //1.根据用户id获取用户地址
        // 从缓存中获取用户信息，包括收货地址
        UmsMember umsMember = userService.getUserFromCacheById(userId);
        modelMap.put("userAddressList", umsMember.getUmsMemberReceiveAddresses());
        //从数据库获得购物车信息
        List<OmsCartItem> omsCartItems = cartService.getCartListByUserId(userId);
        if (omsCartItems != null) {
            //创建一个商品订单列表用于保存需要结算的商品
            List<OmsOrderItem> omsOrderItems = new ArrayList<>();
            for (OmsCartItem omsCartItem : omsCartItems) {
                if (omsCartItem.getIsChecked().equals("1")) {
                    OmsOrderItem omsOrderItem = new OmsOrderItem();
                    omsOrderItem.setProductId(omsCartItem.getProductId());
                    omsOrderItem.setProductSkuId(omsCartItem.getProductSkuId());
                    omsOrderItem.setProductName(omsCartItem.getProductName());
                    omsOrderItem.setProductPic(omsCartItem.getProductPic());
                    omsOrderItem.setProductPrice(omsCartItem.getPrice());
                    omsOrderItem.setProductQuantity(omsCartItem.getQuantity());
                    omsOrderItems.add(omsOrderItem);
                }
            }
            //将订单信息保存到域对象中，用于在结算页面显示
            modelMap.put("orderDetailList", omsOrderItems);
            // 生成交易码
            String tradeCode = UUID.randomUUID().toString();
            orderService.saveTradeCode(userId, tradeCode);
            modelMap.put("tradeCode", tradeCode);
        }

        return "trade";
    }

    //提交订单
    @LoginRequired(isNeededSuccess = true)
    @RequestMapping("/submitOrder")
    public String submitOrder(HttpServletRequest request, String addressId, String tradeCode) {
        String userId = (String) request.getAttribute("userId");
        String nickName = (String) request.getAttribute("nickName");
        //校验交易码
        Boolean check = orderService.checkTradeCode(userId, tradeCode);
        if (check) {
            UmsMemberReceiveAddress umsMemberReceiveAddress = userService.getAddressByAddressId(addressId);
            List<OmsCartItem> cartListByUserId = cartService.getCartListByUserId(userId);
            //封装订单信息,包括收货地址、联系方式等
            OmsOrder omsOrder = new OmsOrder();
            omsOrder.setStatus("0");
            String orderSn = "gmall0615order";
            SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmmss");
            String format = sdf.format(new Date());
            long l = System.currentTimeMillis();
            orderSn = orderSn + format + l;
            omsOrder.setOrderSn(orderSn);//订单编号
            omsOrder.setPayType(2);//支付方式
            omsOrder.setReceiverCity(umsMemberReceiveAddress.getCity());//收货城市
            omsOrder.setReceiverDetailAddress(umsMemberReceiveAddress.getDetailAddress());
            omsOrder.setReceiverName(umsMemberReceiveAddress.getName());
            omsOrder.setReceiverPhone(umsMemberReceiveAddress.getPhoneNumber());
            omsOrder.setReceiverPostCode(umsMemberReceiveAddress.getPostCode());
            omsOrder.setReceiverProvince(umsMemberReceiveAddress.getProvince());
            omsOrder.setReceiverRegion(umsMemberReceiveAddress.getRegion());
            //三天后到货
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE, 3);
            Date time = calendar.getTime();
            omsOrder.setReceiveTime(time);
            omsOrder.setCreateTime(new Date());
            omsOrder.setMemberId(userId);
            omsOrder.setMemberUsername(nickName);
            omsOrder.setPayAmount(PriceUtil.getTotalAmount(cartListByUserId));
            omsOrder.setTotalAmount(PriceUtil.getTotalAmount(cartListByUserId));

            List<OmsOrderItem> omsOrderItems = new ArrayList<>();
            List<String> delSkuIds = new ArrayList<>();
            for (OmsCartItem omsCartItem : cartListByUserId) {
                // 生成订单详情，主要是商品信息
                if (omsCartItem.getIsChecked().equals("1")) {
                    //当要生成订单时，还需要再次确定该商品当前价格是否与数据库中的一致,不一致则支付失败
                    boolean b_price = skuService.checkPrice(omsCartItem.getPrice(),omsCartItem.getProductSkuId());
                    if (b_price==false){ return "tradeFail";}
                            OmsOrderItem omsOrderItem = new OmsOrderItem();
                    omsOrderItem.setProductId(omsCartItem.getProductId());
                    omsOrderItem.setProductSkuId(omsCartItem.getProductSkuId());
                    omsOrderItem.setProductCategoryId(omsCartItem.getProductCategoryId());
                    omsOrderItem.setProductName(omsCartItem.getProductName());
                    omsOrderItem.setProductPic(omsCartItem.getProductPic());
                    omsOrderItem.setProductPrice(omsCartItem.getPrice());
                    omsOrderItem.setProductQuantity(omsCartItem.getQuantity());
                    omsOrderItem.setOrderSn(orderSn);
                    delSkuIds.add(omsCartItem.getProductSkuId());
                    omsOrderItems.add(omsOrderItem);
                }
            }
            omsOrder.setOmsOrderItems(omsOrderItems);
            orderService.saveOrder(omsOrder);
            //删除购物车数据
            cartService.delCartList(delSkuIds, userId);
            // 重定向到支付系统
            return "redirect:http://payment.gmall.com:8087/index?orderSn="+orderSn;
        }
        return "tradeFail";
    }
}
