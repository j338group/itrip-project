package cn.itrip.search.controller;

import cn.itrip.beans.dto.Dto;
import cn.itrip.beans.vo.hotel.ItripHotelVO;
import cn.itrip.beans.vo.hotel.SearchHotCityVO;
import cn.itrip.beans.vo.hotel.SearchHotelVO;
import cn.itrip.common.DtoUtil;
import cn.itrip.common.ErrorCode;
import cn.itrip.common.Page;
import cn.itrip.search.service.SearchHotelService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * description:
 * Created by Ray on 2019-10-29
 */
@RestController
@RequestMapping("/api/hotellist")
public class ItripSearchController {
    @Resource
    private SearchHotelService searchHotelService;

    @RequestMapping(value = "/searchItripHotelPage",method = RequestMethod.POST)
    public Dto searchItripHotelPage(@RequestBody SearchHotelVO searchHotelVO){
        if(searchHotelVO==null||searchHotelVO.getDestination()==null){
            return DtoUtil.returnFail("目的地不能为空", "20002");
        }

        try {
            Page<ItripHotelVO> page= searchHotelService.getItripHotelListByPage(searchHotelVO);
            return DtoUtil.returnDataSuccess(page);

        } catch (Exception e) {
//            e.printStackTrace();
            return DtoUtil.returnFail(e.getMessage(),"20001");
        }

    }

    @RequestMapping(value = "/searchItripHotelListByHotCity",method = RequestMethod.POST)
    public Dto searchItripHotelListByHotCity(@RequestBody SearchHotCityVO searchHotCityVO){
        if(searchHotCityVO==null||searchHotCityVO.getCityId()==null){
            return DtoUtil.returnFail("城市id不能为空","20004");
        }

        try {
            List<ItripHotelVO> list=
                    searchHotelService.getItripHotelListByCity(searchHotCityVO.getCityId(),searchHotCityVO.getCount());
            return DtoUtil.returnDataSuccess(list);
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.returnFail("查询热门城市酒店列表失败","20003");
        }
    }
}
