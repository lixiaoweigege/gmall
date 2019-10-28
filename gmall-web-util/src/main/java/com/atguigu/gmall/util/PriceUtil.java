package com.atguigu.gmall.util;

import com.atguigu.gmall.bean.OmsCartItem;

import java.math.BigDecimal;
import java.util.List;

public class PriceUtil {
    public static BigDecimal getTotalAmount(List<OmsCartItem> omsCartItems) {

        BigDecimal amount = new BigDecimal("0");

        for (OmsCartItem omsCartItem : omsCartItems) {
            if(omsCartItem.getIsChecked().equals("1")){
                amount = amount.add(omsCartItem.getPrice().multiply(omsCartItem.getQuantity()));
            }
        }

        return amount;

    }
}
