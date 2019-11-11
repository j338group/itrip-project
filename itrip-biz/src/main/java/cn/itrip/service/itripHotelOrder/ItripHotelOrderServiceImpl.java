package cn.itrip.service.itripHotelOrder;
import cn.itrip.beans.pojo.*;
import cn.itrip.common.BigDecimalUtil;
import cn.itrip.mapper.itripHotelOrder.ItripHotelOrderMapper;
import cn.itrip.common.EmptyUtils;
import cn.itrip.common.Page;
import cn.itrip.mapper.itripHotelRoom.ItripHotelRoomMapper;
import cn.itrip.mapper.itripHotelTempStore.ItripHotelTempStoreMapper;
import cn.itrip.mapper.itripOrderLinkUser.ItripOrderLinkUserMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import cn.itrip.common.Constants;
@Service
public class ItripHotelOrderServiceImpl implements ItripHotelOrderService {

    @Resource
    private ItripHotelOrderMapper itripHotelOrderMapper;
    @Resource
    private ItripOrderLinkUserMapper itripOrderLinkUserMapper;
    @Resource
    private ItripHotelRoomMapper itripHotelRoomMapper;
    @Resource
    private ItripHotelTempStoreMapper itripHotelTempStoreMapper;

    public ItripHotelOrder getItripHotelOrderById(Long id)throws Exception{
        return itripHotelOrderMapper.getItripHotelOrderById(id);
    }

    public List<ItripHotelOrder>	getItripHotelOrderListByMap(Map<String,Object> param)throws Exception{
        return itripHotelOrderMapper.getItripHotelOrderListByMap(param);
    }

    public Integer getItripHotelOrderCountByMap(Map<String,Object> param)throws Exception{
        return itripHotelOrderMapper.getItripHotelOrderCountByMap(param);
    }

    public Integer itriptxAddItripHotelOrder(ItripHotelOrder itripHotelOrder)throws Exception{
            itripHotelOrder.setCreationDate(new Date());
            return itripHotelOrderMapper.insertItripHotelOrder(itripHotelOrder);
    }

    public Integer itriptxModifyItripHotelOrder(ItripHotelOrder itripHotelOrder)throws Exception{
        itripHotelOrder.setModifyDate(new Date());
        return itripHotelOrderMapper.updateItripHotelOrder(itripHotelOrder);
    }

    public Integer itriptxDeleteItripHotelOrderById(Long id)throws Exception{
        return itripHotelOrderMapper.deleteItripHotelOrderById(id);
    }

    public Page<ItripHotelOrder> queryItripHotelOrderPageByMap(Map<String,Object> param,Integer pageNo,Integer pageSize)throws Exception{
        Integer total = itripHotelOrderMapper.getItripHotelOrderCountByMap(param);
        pageNo = EmptyUtils.isEmpty(pageNo) ? Constants.DEFAULT_PAGE_NO : pageNo;
        pageSize = EmptyUtils.isEmpty(pageSize) ? Constants.DEFAULT_PAGE_SIZE : pageSize;
        Page page = new Page(pageNo, pageSize, total);
        param.put("beginPos", page.getBeginPos());
        param.put("pageSize", page.getPageSize());
        List<ItripHotelOrder> itripHotelOrderList = itripHotelOrderMapper.getItripHotelOrderListByMap(param);
        page.setRows(itripHotelOrderList);
        return page;
    }

    @Override
    public BigDecimal getPayAmount(Integer count, int bookingDays, Double roomPrice) throws Exception {
        return BigDecimalUtil.OperationASMD(count * bookingDays, roomPrice, BigDecimalUtil.BigDecimalOprations.multiply, 2, BigDecimal.ROUND_DOWN);
    }

    @Override
    public Long itriptxAddItripHotelOrder(ItripHotelOrder order, List<ItripUserLinkUser> linkUser) throws Exception {
        Long orderId = order.getId();
        if (orderId == null) {
            //插入订单记录
            order.setCreationDate(new Date());
            itripHotelOrderMapper.insertItripHotelOrder(order);
            orderId=order.getId();
        }else{
            //删除联系人
            itripOrderLinkUserMapper.deleteItripOrderLinkUserByOrderId(orderId);
            //修改订单
            itripHotelOrderMapper.updateItripHotelOrder(order);
        }
        //插入订单联系人
        for (ItripUserLinkUser user : linkUser) {
            ItripOrderLinkUser orderUser = new ItripOrderLinkUser();
            orderUser.setOrderId(orderId);
            orderUser.setLinkUserId(user.getId());
            orderUser.setLinkUserName(user.getLinkUserName());
            orderUser.setCreationDate(new Date());
            orderUser.setCreatedBy(order.getUserId());
            itripOrderLinkUserMapper.insertItripOrderLinkUser(orderUser);
        }
        return orderId;
    }

    /**
     * 验证支付类型是否支持（线上、线下）
     * @param hotelOrder
     * @param payType
     * @return
     * @throws Exception
     */
    @Override
    public Boolean getSupportPayType(ItripHotelOrder hotelOrder, Integer payType) throws Exception {
        ItripHotelRoom hotelRoom = itripHotelRoomMapper.getItripHotelRoomById(hotelOrder.getRoomId());
        Integer oldPayType = hotelRoom.getPayType();
        // 11       01     10
        // 01,10     01     10
        return (oldPayType&payType)!=0;
    }

    /**
     * 修改订单状态并刷新库存
     * @param hotelOrder
     * @param payType
     * @throws Exception
     */
    @Override
    public void itriptxModifyItripHotelOrderAndTempRoomStore(ItripHotelOrder hotelOrder, Integer payType) throws Exception {
        //减库存
        Map<String, Object> param = new HashMap<>();
        param.put("count", hotelOrder.getCount());
        param.put("roomId", hotelOrder.getRoomId());
        param.put("checkInDate", hotelOrder.getCheckInDate());
        param.put("checkOutDate", hotelOrder.getCheckOutDate());

        itripHotelTempStoreMapper.updateTempStore(param);
        //修改订单状态
        hotelOrder.setPayType(payType);
        hotelOrder.setOrderStatus(2);
        itripHotelOrderMapper.updateItripHotelOrder(hotelOrder);

    }

    @Scheduled(cron = "0 0/10 * * * ?")
    public void updateOrderStatusTimeOutPay(){
        //扫描订单表，（未支付的）查看订单生成时间跟当前时间的差，是否大于2小时
        //如果大于2小时，修改订单状态
        //1分钟  效率低
        //2小时  12:00  2：00 误差大
        System.out.println("定时修改超时未支付的订单。。。");
        try {
            itripHotelOrderMapper.updateOrderStatus();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
