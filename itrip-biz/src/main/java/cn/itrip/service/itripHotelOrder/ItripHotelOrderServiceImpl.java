package cn.itrip.service.itripHotelOrder;
import cn.itrip.beans.pojo.ItripOrderLinkUser;
import cn.itrip.beans.pojo.ItripUserLinkUser;
import cn.itrip.common.BigDecimalUtil;
import cn.itrip.mapper.itripHotelOrder.ItripHotelOrderMapper;
import cn.itrip.beans.pojo.ItripHotelOrder;
import cn.itrip.common.EmptyUtils;
import cn.itrip.common.Page;
import cn.itrip.mapper.itripOrderLinkUser.ItripOrderLinkUserMapper;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import cn.itrip.common.Constants;
@Service
public class ItripHotelOrderServiceImpl implements ItripHotelOrderService {

    @Resource
    private ItripHotelOrderMapper itripHotelOrderMapper;
    @Resource
    private ItripOrderLinkUserMapper itripOrderLinkUserMapper;

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

}
