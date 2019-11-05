package cn.itrip.controller;

import cn.itrip.beans.dto.Dto;
import cn.itrip.beans.pojo.*;
import cn.itrip.beans.vo.order.ItripAddHotelOrderVO;
import cn.itrip.beans.vo.order.RoomStoreVO;
import cn.itrip.beans.vo.order.ValidateRoomStoreVO;
import cn.itrip.common.*;
import cn.itrip.service.itripHotel.ItripHotelService;
import cn.itrip.service.itripHotelOrder.ItripHotelOrderService;
import cn.itrip.service.itripHotelRoom.ItripHotelRoomService;
import cn.itrip.service.itripHotelTempStore.ItripHotelTempStoreService;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

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
    @Resource
    private SystemConfig systemConfig;
    @Resource
    private ItripHotelOrderService itripHotelOrderService ;


    @RequestMapping(value = "/getpreorderinfo",method = RequestMethod.POST)
    public Dto<RoomStoreVO> getPreoderInfo(@RequestBody ValidateRoomStoreVO validateRoomStoreVO, HttpServletRequest request){
        //1.登录验证
        String token = request.getHeader("token");
        ItripUser currentUser = validationToken.getCurrentUser(token);
        if (currentUser == null) {
            return DtoUtil.returnFail("token失效，请重登录", "100000");
        }
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

    @RequestMapping(value = "/validateroomstore",method = RequestMethod.POST)
    public Dto validateRoomStore(@RequestBody ValidateRoomStoreVO vo,HttpServletRequest request){
//        1.登录验证
        String token = request.getHeader("token");
        ItripUser currentUser = validationToken.getCurrentUser(token);

        if (currentUser == null) {
            return DtoUtil.returnFail("token失效，请重登录", "100000");
        }
        //其他验证
        Long hotelId = vo.getHotelId();
        Long roomId = vo.getRoomId();
        Integer count = vo.getCount();
        Date checkInDate = vo.getCheckInDate();
        Date checkOutDate = vo.getCheckOutDate();
        if (vo == null|| hotelId ==null|| roomId ==null|| count ==null|| checkInDate ==null|| checkOutDate ==null) {
            return DtoUtil.returnFail("参数不能为空", "100515");
        }
        //验证库存
        Map<String, Object> param = new HashMap<>();
        param.put("startTime", checkInDate);
        param.put("endTime", checkOutDate);
        param.put("hotelId", hotelId);
        param.put("roomId", roomId);
        param.put("count", count);
        try {
            Boolean haveStore=itripHotelTempStoreService.validateTempStore(param);
            Map<String, Boolean> returnMap = new HashMap<>();
            returnMap.put("storeFlag",haveStore);
            return DtoUtil.returnDataSuccess(returnMap);
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.returnFail("验证库存失败系统异常", "100516");
        }
    }

    /**
     * 生成订单
     * @param orderVO
     * @param request
     * @return
     */
    @RequestMapping(value = "/addhotelorder",method = RequestMethod.POST)
    public Dto addHotelOrder(@RequestBody ItripAddHotelOrderVO orderVO,HttpServletRequest request){
        //        1.登录验证
        String token = request.getHeader("token");
        ItripUser currentUser = validationToken.getCurrentUser(token);
        if (currentUser == null) {
            return DtoUtil.returnFail("token失效，请重登录", "100000");
        }
        //验证参数
        if (orderVO == null) {
            return DtoUtil.returnFail("不能提交空，请填写订单信息", "100506");
        }
        Long roomId = orderVO.getRoomId();
        Long hotelId = orderVO.getHotelId();
        Date checkInDate = orderVO.getCheckInDate();
        Date checkOutDate = orderVO.getCheckOutDate();
        Integer count = orderVO.getCount();
        //验证库存
        Map<String, Object> param = new HashMap<>();
        param.put("startTime", checkInDate);
        param.put("endTime", checkOutDate);
        param.put("hotelId", hotelId);
        param.put("roomId", roomId);
        param.put("count", count);
        try {
            if(!itripHotelTempStoreService.validateTempStore(param)) {
                return DtoUtil.returnFail("库存不足", "100507");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //封装数据
        ItripHotelOrder order = new ItripHotelOrder();
        BeanUtils.copyProperties(orderVO, order);
        order.setUserId(currentUser.getId());
        order.setOrderStatus(0);
        order.setCreatedBy(currentUser.getId());
        //生成订单号：机器码+时间戳（yyyyMMddHHmmss）+MD5(roomId+毫秒数+6位随机数)6
        StringBuffer orderNo = new StringBuffer();
        orderNo.append(systemConfig.getMachineCode());
        orderNo.append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
        orderNo.append(MD5.getMd5("" + roomId + System.currentTimeMillis() + Math.random() * 900000 + 100000, 6));
        order.setOrderNo(orderNo.toString());
        //预订天数
        int bookingDays = DateUtil.getBetweenDates(checkInDate, checkOutDate).size() - 1;
        order.setBookingDays(bookingDays);
        //订单金额
        ItripHotelRoom hotelRoom = null;
        try {
            hotelRoom = itripHotelRoomService.getItripHotelRoomById(roomId);
            BigDecimal amount=itripHotelOrderService.getPayAmount(count,bookingDays,hotelRoom.getRoomPrice());
            order.setPayAmount(amount.doubleValue());
        } catch (Exception e) {
            e.printStackTrace();
        }
        //客户端
        if (token.startsWith("token:PC")) {
            order.setBookType(0);
        }else
        if (token.startsWith("token:MOBILE")) {
            order.setBookType(1);
        }else {
            order.setBookType(2);
        }
        //订单联系人
        StringBuffer sb = new StringBuffer();
        for (ItripUserLinkUser user : orderVO.getLinkUser()) {
            sb.append(user.getLinkUserName()+",");
        }
        String s = sb.toString();
        String linkUserName = s.substring(0, s.length()-1);
        order.setLinkUserName(linkUserName);

        //调用业务层插入订单记录
        try {
            Long orderId=itripHotelOrderService.itriptxAddItripHotelOrder(order,orderVO.getLinkUser());
            Map<String, Object> data = new HashMap<>();
            data.put("orderId", orderId);
            data.put("orderNo", orderNo);
            return DtoUtil.returnDataSuccess(data);
        } catch (Exception e) {
            e.printStackTrace();
            return  DtoUtil.returnFail("生成订单失败","100505");
        }
    }
}
