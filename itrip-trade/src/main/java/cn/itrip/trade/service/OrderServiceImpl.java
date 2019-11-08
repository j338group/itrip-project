package cn.itrip.trade.service;

import cn.itrip.beans.pojo.ItripHotelOrder;
import cn.itrip.beans.pojo.ItripTradeEnds;
import cn.itrip.mapper.itripHotelOrder.ItripHotelOrderMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * description:
 * Created by Ray on 2019-11-08
 */
@Service
public class OrderServiceImpl implements OrderService {
    @Resource
    private ItripHotelOrderMapper itripHotelOrderMapper;
    @Override
    public ItripHotelOrder getOrderByOrderNo(String orderNo) throws Exception {
        Map<String, Object> param = new HashMap<>();
        param.put("orderNo", orderNo);
        List<ItripHotelOrder> orderList = itripHotelOrderMapper.getItripHotelOrderListByMap(param);
        return orderList.get(0);
    }

    @Override
    public void processUpdateOrderStatus(ItripHotelOrder order) throws Exception {
        //更新订单状态
        ItripHotelOrder hotelOrder = this.getOrderByOrderNo(order.getOrderNo());
        Long id = hotelOrder.getId();
        order.setId(id);
        itripHotelOrderMapper.updateItripHotelOrder(order);
        //更新库存---（插入交易中间表数据）
        ItripTradeEnds tradeEnds = new ItripTradeEnds();
        tradeEnds.setFlag(0);
        tradeEnds.setOrderNo(hotelOrder.getOrderNo());
        tradeEnds.setId(id);

    }
}
