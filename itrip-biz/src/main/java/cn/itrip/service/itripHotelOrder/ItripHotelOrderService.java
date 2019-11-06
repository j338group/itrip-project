package cn.itrip.service.itripHotelOrder;
import cn.itrip.beans.pojo.ItripHotelOrder;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import cn.itrip.beans.pojo.ItripUserLinkUser;
import cn.itrip.common.Page;
/**
* Created by shang-pc on 2015/11/7.
*/
public interface ItripHotelOrderService {

    public ItripHotelOrder getItripHotelOrderById(Long id)throws Exception;

    public List<ItripHotelOrder>	getItripHotelOrderListByMap(Map<String,Object> param)throws Exception;

    public Integer getItripHotelOrderCountByMap(Map<String,Object> param)throws Exception;

    public Integer itriptxAddItripHotelOrder(ItripHotelOrder itripHotelOrder)throws Exception;

    public Integer itriptxModifyItripHotelOrder(ItripHotelOrder itripHotelOrder)throws Exception;

    public Integer itriptxDeleteItripHotelOrderById(Long id)throws Exception;

    public Page<ItripHotelOrder> queryItripHotelOrderPageByMap(Map<String,Object> param,Integer pageNo,Integer pageSize)throws Exception;

    BigDecimal getPayAmount(Integer count, int bookingDays, Double roomPrice) throws Exception;

    Long itriptxAddItripHotelOrder(ItripHotelOrder order, List<ItripUserLinkUser> linkUser) throws Exception;

    Boolean getSupportPayType(ItripHotelOrder hotelOrder, Integer payType) throws Exception;

    void itriptxModifyItripHotelOrderAndTempRoomStore(ItripHotelOrder hotelOrder, Integer payType) throws Exception;

}
