package cn.itrip.trade.service;

import cn.itrip.beans.pojo.ItripHotelOrder;

/**
 * description:
 * Created by Ray on 2019-11-08
 */
public interface OrderService {

    ItripHotelOrder getOrderByOrderNo(String orderNo)throws Exception;
    void processUpdateOrderStatus(ItripHotelOrder order) throws Exception;
}
