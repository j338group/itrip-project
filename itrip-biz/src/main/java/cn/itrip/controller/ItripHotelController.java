package cn.itrip.controller;

import cn.itrip.beans.dto.Dto;
import cn.itrip.beans.pojo.ItripAreaDic;
import cn.itrip.beans.pojo.ItripImage;
import cn.itrip.beans.vo.ItripAreaDicVO;
import cn.itrip.beans.vo.ItripImageVO;
import cn.itrip.beans.vo.hotel.HotelVideoDescVO;
import cn.itrip.common.DtoUtil;
import cn.itrip.service.itripAreaDic.ItripAreaDicService;
import cn.itrip.service.itripHotel.ItripHotelService;
import cn.itrip.service.itripImage.ItripImageService;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * description:
 * Created by Ray on 2019-10-31
 */
@RestController
@RequestMapping("/api/hotel")
public class ItripHotelController {
    @Resource
    private ItripAreaDicService itripAreaDicService;
    @Resource
    private ItripImageService itripImageService;
    @Resource
    private ItripHotelService itripHotelService;
    /**
     * 查询热门城市列表
     * @param type
     * @return
     */
    @RequestMapping(value = "/queryhotcity/{type}",method = RequestMethod.GET)
    public Dto queryHotCity(@PathVariable Integer type){
        if (type == null) {
            return DtoUtil.returnFail("国家type不能为空", "100202");
        }
        //封装查询参数
        Map<String, Object> param = new HashMap<>();
        param.put("isChina", type);
        param.put("isHot", 1);
        try {
            List<ItripAreaDic> areaDicList = itripAreaDicService.getItripAreaDicListByMap(param);
            List<ItripAreaDicVO> areaDicVOList = new ArrayList<>();
            for (ItripAreaDic areaDic : areaDicList) {
                ItripAreaDicVO areaDicVO = new ItripAreaDicVO();
                BeanUtils.copyProperties(areaDic, areaDicVO);
                areaDicVOList.add(areaDicVO);
            }
            return DtoUtil.returnDataSuccess(areaDicVOList);
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.returnFail("获取热门城市列表失败", "100201");

        }

    }

    /**
     * 查询城市商圈列表
     * @param cityId
     * @return
     */
    @RequestMapping(value = "/querytradearea/{cityId}",method = RequestMethod.GET)
    public Dto queryTradeArea(@PathVariable Integer cityId){
        if (cityId == null) {
            return DtoUtil.returnFail("城市id不能为空", "100203");
        }

        Map<String, Object> param = new HashMap<>();
        param.put("parent", cityId);
        param.put("isTradingArea", 1);
        try {
            List<ItripAreaDic> areaDicList = itripAreaDicService.getItripAreaDicListByMap(param);
            List<ItripAreaDicVO> areaDicVOList = new ArrayList<>();
            for (ItripAreaDic areaDic : areaDicList) {
                ItripAreaDicVO areaDicVO = new ItripAreaDicVO();
                BeanUtils.copyProperties(areaDic, areaDicVO);
                areaDicVOList.add(areaDicVO);
            }
            return DtoUtil.returnDataSuccess(areaDicVOList);
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.returnFail("获取城市商圈失败", "100204");
        }
    }

    @RequestMapping(value = "/getimg/{targetId}",method = RequestMethod.GET)
    public Dto getImg(@PathVariable Long targetId){
        if (targetId == null) {
            return DtoUtil.returnFail("酒店id不能为空", "100213");
        }

        Map<String, Object> param = new HashMap<>();
        param.put("targetId", targetId);
        param.put("type","0");
        try {
            List<ItripImage> imageList = itripImageService.getItripImageListByMap(param);
            List<ItripImageVO> imageVOList = new ArrayList<>();
            for (ItripImage image : imageList) {
                ItripImageVO imageVO = new ItripImageVO();
                BeanUtils.copyProperties(image, imageVO);
                imageVOList.add(imageVO);
            }
            return DtoUtil.returnDataSuccess(imageVOList);
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.returnFail("获取酒店图片失败", "100214");
        }

    }
    @RequestMapping(value = "/getvideodesc/{hotelId}",method = RequestMethod.GET)
    public Dto getVedioDesc(@PathVariable("hotelId") Long hotelId){
        if (hotelId == null) {
            return DtoUtil.returnFail("酒店id不能为空", "100215");
        }

        try {
            HotelVideoDescVO videoDescVO=itripHotelService.getItripHotelVideoByHotelId(hotelId);
            return DtoUtil.returnDataSuccess(videoDescVO);
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.returnFail("获取视频文字信息失败", "100214");
        }
    }
}
