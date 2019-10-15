package com.atguigu.gmall.manag.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.bean.*;
import com.atguigu.gmall.manage.util.MyUploadUtil;
import com.atguigu.gmall.service.SpuService;
import org.csource.common.MyException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static java.lang.System.out;

@CrossOrigin
@Controller
public class SpuController {
    @Reference
    private SpuService spuService;
    /**
     * 根据三级分类获取标准产品单元列表
     */
    @ResponseBody
    @RequestMapping("/spuList")
    public List<PmsProductInfo>  spuList(@RequestParam("catalog3Id") String catalog3Id){
        List<PmsProductInfo> pmsProductInfoList=spuService.spuList(catalog3Id);
        return pmsProductInfoList;
    }

    /**
     * 获取该标准产品单元的基础销售属性
     * @return
     */
    @ResponseBody
    @RequestMapping("/baseSaleAttrList")
    public List<PmsBaseSaleAttr> baseSaleAttrList(){
        List<PmsBaseSaleAttr> pmsBaseSaleAttrList=spuService.baseSaleAttrList();
        return pmsBaseSaleAttrList;
    }
    /**
     * 保存图片并返回图片地址给页面
     */
    @ResponseBody
    @RequestMapping("/fileUpload")
    public String fileUpload(@RequestParam("file")MultipartFile multipartFile) throws IOException, MyException {
        String img_url= MyUploadUtil.upload_image(multipartFile);
        return img_url;
    }
    /**
     * 保存标准产品单元信息
     * @param pmsProductInfo
     * @return
     */
    @ResponseBody
    @RequestMapping("/saveSpuInfo")
    public String saveSpuInfo(@RequestBody PmsProductInfo pmsProductInfo){
        spuService.saveSpuInfo(pmsProductInfo);
        return "success";
    }
    @ResponseBody
    @RequestMapping("/spuSaleAttrList")
    public List<PmsProductSaleAttr> spuSaleAttrList(@RequestParam("spuId") String productId){
        List<PmsProductSaleAttr> pmsProductSaleAttrList=spuService.spuSaleAttrList(productId);
        return pmsProductSaleAttrList;
    }

    @ResponseBody
    @RequestMapping("/spuImageList")
    public List<PmsProductImage> spuImageList(@RequestParam("spuId") String productId){
        List<PmsProductImage> pmsProductImageList=spuService.spuImageList(productId);
        return pmsProductImageList;
    }
}
