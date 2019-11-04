package cn.itrip.controller;

import cn.itrip.beans.dto.Dto;
import cn.itrip.beans.pojo.ItripHotel;
import cn.itrip.beans.pojo.ItripHotelRoom;
import cn.itrip.beans.pojo.ItripHotelTempStore;
import cn.itrip.beans.pojo.ItripUser;
import cn.itrip.beans.vo.order.RoomStoreVO;
import cn.itrip.beans.vo.order.ValidateRoomStoreVO;
import cn.itrip.common.DtoUtil;
import cn.itrip.common.ValidationToken;
import cn.itrip.service.itripHotel.ItripHotelService;
import cn.itrip.service.itripHotelRoom.ItripHotelRoomService;
import cn.itrip.service.itripHotelTempStore.ItripHotelTempStoreService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * description:
 * Created by Ray on 2019-11-04
 */
@RestController
@RequestMapping("/api/hotelorder")
public class ItripHotelOrderController {
    @Resource
    private ValidationToken validationToken;
    @Resource
    private ItripHotelService itripHotelService;
    @Resource
    private ItripHotelRoomService itripHotelRoomService;
    @Resource
    private ItripHotelTempStoreService itripHotelTempStoreService;
    @RequestMapping(value = "/getpreorderinfo",method = RequestMethod.POST)
    public Dto<RoomStoreVO> getPreoderInfo(@RequestBody ValidateRoomStoreVO validateRoomStoreVO, HttpServletRequest request){
        //1.登录验证
//        String token = request.getHeader("token");
//        ItripUser currentUser = validationToken.getCurrentUser(token);
//        if (currentUser == null) {
//            return DtoUtil.returnFail("token失效，请重登录", "100000");
//        }
        //2.验证必填项
        if (validateRoomStoreVO == null) {
            return DtoUtil.returnFail("参数不能为空", "100001");
        }
        Long hotelId = validateRoomStoreVO.getHotelId();
        if ( hotelId == null) {
            return DtoUtil.returnFail("hotelId不能为空", "100510");
        }
        Long roomId = validateRoomStoreVO.getRoomId();
        if (roomId == null) {
            return DtoUtil.returnFail("roomId不能为空", "100511");
        }
        Date checkInDate = validateRoomStoreVO.getCheckInDate();
        Date checkOutDate = validateRoomStoreVO.getCheckOutDate();
        if (checkInDate == null|| checkOutDate==null ||checkInDate.getTime()>checkOutDate.getTime()) {
            return DtoUtil.returnFail("入离时间不能为空，入住时间不能晚于离开时间", "100512");
        }
        if (validateRoomStoreVO.getCount() == null) {
            validateRoomStoreVO.setCount(1);
        }
        //3.调用业务层获取数据
        ItripHotel itripHotel;
        ItripHotelRoom hotelRoom;
        List<ItripHotelTempStore> stores;
        try {
            itripHotel = itripHotelService.getItripHotelById(hotelId);
            hotelRoom = itripHotelRoomService.getItripHotelRoomById(roomId);
            Map<String, Object> param = new HashMap<>();
            param.put("startTime", checkInDate);
            param.put("endTime", checkOutDate);
            param.put("hotelId", hotelId);
            param.put("roomId", roomId);
            stores = itripHotelTempStoreService.getItripHotelTempStoresByMap(param);
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.returnFail(e.getMessage(), "100513");
        }
        //4.封装RoomStoreVO数据
        RoomStoreVO roomStoreVO = new RoomStoreVO();
        roomStoreVO.setHotelId(hotelId);
        roomStoreVO.setRoomId(roomId);
        roomStoreVO.setCheckInDate(checkInDate);
        roomStoreVO.setCheckOutDate(checkOutDate);
        roomStoreVO.setCount(validateRoomStoreVO.getCount());
        roomStoreVO.setHotelName(itripHotel.getHotelName());
        roomStoreVO.setPrice(BigDecimal.valueOf(hotelRoom.getRoomPrice()));
        //因为stores已经根据store排序，第一个即为最小库存
        roomStoreVO.setStore(stores.get(0).getStore());

        //5.返回数据
        return DtoUtil.returnDataSuccess(roomStoreVO);
    }
}
