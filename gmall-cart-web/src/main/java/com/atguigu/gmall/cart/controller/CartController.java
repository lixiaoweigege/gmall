package com.atguigu.gmall.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.anotations.LoginRequired;
import com.atguigu.gmall.bean.OmsCartItem;
import com.atguigu.gmall.bean.PmsSkuInfo;
import com.atguigu.gmall.service.CartService;
import com.atguigu.gmall.service.SkuService;
import com.atguigu.gmall.util.CookieUtil;
import com.atguigu.gmall.util.PriceUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import sun.font.TrueTypeFont;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class CartController {
    @Reference
    SkuService skuService;
    @Reference
    CartService cartService;

    @RequestMapping("checkCart")
    @LoginRequired(isNeededSuccess=false)
    public String checkCart(HttpServletRequest request, HttpServletResponse response, OmsCartItem omsCartItem, ModelMap map) {
        String userId = "1";
        List<OmsCartItem> omsCartItems=new ArrayList<>();
        if (StringUtils.isNotBlank(userId)) {
            //用户已经的登录,操作数据库和redis
            omsCartItem.setMemberId(userId);
            cartService.updateCartByUserId(omsCartItem);
        } else {
            String cartListCookie=CookieUtil.getCookieValue(request,"cartListCookie",true);
            omsCartItems = JSON.parseArray(cartListCookie, OmsCartItem.class);
            for (OmsCartItem cartItem : omsCartItems) {
                cartItem.setIsChecked(omsCartItem.getIsChecked());
            }
            //覆盖原来的cookie

            CookieUtil.setCookie(request,response,"cartListCookie",JSON.toJSONString(omsCartItems),1000 * 60 * 60 * 24, true);
        }
        if(omsCartItems!=null&&omsCartItems.size()>0){
            map.put("cartList",omsCartItems);
            map.put("totalAmount", PriceUtil.getTotalAmount(omsCartItems));

        }
        return "cartListInner";
    }

    @RequestMapping("cartList")
    @LoginRequired(isNeededSuccess=false)
    public String cartList(HttpServletRequest request, HttpServletResponse response, OmsCartItem omsCartItem, ModelMap map) {
        String userId = "1";
        List<OmsCartItem> omsCartItems = new ArrayList<>();
        if (StringUtils.isNotBlank(userId)) {
            // 查缓存
            omsCartItems = cartService.cartList(userId);
        } else {
            // 查cookie
            String cartListCookie = CookieUtil.getCookieValue(request, "cartListCookie", true);
            if (StringUtils.isNotBlank(cartListCookie)) {
                omsCartItems = JSON.parseArray(cartListCookie, OmsCartItem.class);
            }
        }

        map.put("cartList", omsCartItems);
        if (omsCartItems != null && omsCartItems.size() > 0) {
            map.put("totalAmount", PriceUtil.getTotalAmount(omsCartItems));

        }
        return "cartList";
    }

    @RequestMapping("/addToCart")
    @LoginRequired(isNeededSuccess=false)
    public String addCart(HttpServletRequest request, HttpServletResponse response, OmsCartItem omsCartItem) {
        //根据库存id查询商品的库存信息
        PmsSkuInfo pmsSkuInfo = skuService.getSkuInfoById(omsCartItem.getProductSkuId());
        //把库存信息封装到omsCartItem类中
        omsCartItem.setIsChecked("1");
        omsCartItem.setPrice(pmsSkuInfo.getPrice());
        omsCartItem.setTotalPrice(omsCartItem.getPrice().multiply(omsCartItem.getQuantity()));
        omsCartItem.setCreateDate(new Date());
        omsCartItem.setProductCategoryId(pmsSkuInfo.getCatalog3Id());
        omsCartItem.setProductId(pmsSkuInfo.getProductId());
        omsCartItem.setProductName(pmsSkuInfo.getSkuName());
        omsCartItem.setProductPic(pmsSkuInfo.getSkuDefaultImg());
        String userId = "1";
        List<OmsCartItem> omsCartItems = new ArrayList<>();
        if (StringUtils.isNotBlank(userId)) {

            // 用户已经登录，操作db和redis
            omsCartItem.setMemberId(userId);
            omsCartItem.setMemberNickname("用户昵称");

            //omsCartItems = cartService.getCartByUserId(userId);
            OmsCartItem omsCartItemFromDb;//通过这个dao直接判断十添加还是更新
            omsCartItemFromDb = cartService.isCartExists(userId, omsCartItem);

            if (omsCartItemFromDb != null) {
                // 更新
                omsCartItemFromDb.setQuantity(omsCartItemFromDb.getQuantity().add(omsCartItem.getQuantity()));
                omsCartItemFromDb.setTotalPrice(omsCartItemFromDb.getPrice().multiply(omsCartItemFromDb.getQuantity()));
                cartService.updateCart(omsCartItemFromDb);
            } else {
                // 添加
                cartService.addCart(omsCartItem);
            }
        } else {

            // 用户没有登录，操作cookie
            String cartListCookie = CookieUtil.getCookieValue(request, "cartListCookie", true);
            if (StringUtils.isNotBlank(cartListCookie)) {
                omsCartItems = JSON.parseArray(cartListCookie, OmsCartItem.class);
                // 判断是否重复
                boolean b = if_new_cart(omsCartItems, omsCartItem);

                if (b) {
                    // 不重复添加
                    omsCartItems.add(omsCartItem);
                } else {
                    // 重复更新
                    for (OmsCartItem cartItem : omsCartItems) {
                        if (cartItem.getProductSkuId().equals(omsCartItem.getProductSkuId())) {
                            cartItem.setQuantity(cartItem.getQuantity().add(omsCartItem.getQuantity()));
                            cartItem.setTotalPrice(omsCartItem.getPrice().multiply(omsCartItem.getQuantity()));
                        }
                    }
                }

            } else {
                // 直接添加
                omsCartItems.add(omsCartItem);
            }
            // 覆盖cookie
            CookieUtil.setCookie(request, response, "cartListCookie", JSON.toJSONString(omsCartItems), 1000 * 60 * 60 * 24, true);
        }
        return "redirect:/success.html";
    }

    private boolean if_new_cart(List<OmsCartItem> omsCartItems, OmsCartItem omsCartItem) {

        boolean b = true;
        for (OmsCartItem cartItem : omsCartItems) {
            if (cartItem.getProductSkuId().equals(omsCartItem.getProductSkuId())) {
                b = false;
            }
        }
        return b;
    }
}
