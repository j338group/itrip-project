package cn.itrip.service.itripHotelRoom;
import cn.itrip.beans.vo.hotelroom.ItripHotelRoomVO;
import cn.itrip.beans.vo.hotelroom.SearchHotelRoomVO;
import cn.itrip.common.DateUtil;
import cn.itrip.mapper.itripHotelRoom.ItripHotelRoomMapper;
import cn.itrip.beans.pojo.ItripHotelRoom;
import cn.itrip.common.EmptyUtils;
import cn.itrip.common.Page;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

import cn.itrip.common.Constants;
@Service
public class ItripHotelRoomServiceImpl implements ItripHotelRoomService {

    @Resource
    private ItripHotelRoomMapper itripHotelRoomMapper;

    public ItripHotelRoom getItripHotelRoomById(Long id)throws Exception{
        return itripHotelRoomMapper.getItripHotelRoomById(id);
    }

    public List<ItripHotelRoom>	getItripHotelRoomListByMap(Map<String,Object> param)throws Exception{
        return itripHotelRoomMapper.getItripHotelRoomListByMap(param);
    }

    public Integer getItripHotelRoomCountByMap(Map<String,Object> param)throws Exception{
        return itripHotelRoomMapper.getItripHotelRoomCountByMap(param);
    }

    public Integer itriptxAddItripHotelRoom(ItripHotelRoom itripHotelRoom)throws Exception{
            itripHotelRoom.setCreationDate(new Date());
            return itripHotelRoomMapper.insertItripHotelRoom(itripHotelRoom);
    }

    public Integer itriptxModifyItripHotelRoom(ItripHotelRoom itripHotelRoom)throws Exception{
        itripHotelRoom.setModifyDate(new Date());
        return itripHotelRoomMapper.updateItripHotelRoom(itripHotelRoom);
    }

    public Integer itriptxDeleteItripHotelRoomById(Long id)throws Exception{
        return itripHotelRoomMapper.deleteItripHotelRoomById(id);
    }

    public Page<ItripHotelRoom> queryItripHotelRoomPageByMap(Map<String,Object> param,Integer pageNo,Integer pageSize)throws Exception{
        Integer total = itripHotelRoomMapper.getItripHotelRoomCountByMap(param);
        pageNo = EmptyUtils.isEmpty(pageNo) ? Constants.DEFAULT_PAGE_NO : pageNo;
        pageSize = EmptyUtils.isEmpty(pageSize) ? Constants.DEFAULT_PAGE_SIZE : pageSize;
        Page page = new Page(pageNo, pageSize, total);
        param.put("beginPos", page.getBeginPos());
        param.put("pageSize", page.getPageSize());
        List<ItripHotelRoom> itripHotelRoomList = itripHotelRoomMapper.getItripHotelRoomListByMap(param);
        page.setRows(itripHotelRoomList);
        return page;
    }

    @Override
    public List<ItripHotelRoomVO> getItripHotelRoomListBySearchRoomVO(SearchHotelRoomVO roomVO) throws Exception {
        //封装查询条件
        Map<String, Object> param = new HashMap<>();
        param.put("hotelId", roomVO.getHotelId());
        param.put("startDate", "");
        param.put("endDate", "");
        param.put("isBook", roomVO.getIsBook());
        param.put("isHavingBreakfast", roomVO.getIsHavingBreakfast());
        param.put("isTimelyResponse", roomVO.getIsTimelyResponse());
        param.put("roomBedTypeId", roomVO.getRoomBedTypeId());
        param.put("isCancel", roomVO.getIsCancel());
        param.put("payType", roomVO.getPayType());

        List<Date> dateList = DateUtil.getBetweenDates(roomVO.getStartDate(), roomVO.getEndDate());

        param.put("dateList",dateList);

        List<ItripHotelRoom> roomList = itripHotelRoomMapper.getItripHotelRoomListByMap(param);
        List<ItripHotelRoomVO> roomVOList = new ArrayList<>();
        for (ItripHotelRoom room : roomList) {
            ItripHotelRoomVO roomVO1 = new ItripHotelRoomVO();
            BeanUtils.copyProperties(room, roomVO1);
            roomVO1.setRoomPrice(BigDecimal.valueOf(room.getRoomPrice()));
            roomVOList.add(roomVO1);
        }
        return roomVOList;
    }

}
