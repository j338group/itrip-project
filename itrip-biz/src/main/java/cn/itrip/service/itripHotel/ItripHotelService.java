package cn.itrip.service.itripHotel;
import cn.itrip.beans.pojo.ItripHotel;
import java.util.List;
import java.util.Map;

import cn.itrip.beans.vo.hotel.HotelVideoDescVO;
import cn.itrip.common.Page;
/**
* Created by shang-pc on 2015/11/7.
*/
public interface ItripHotelService {

    public ItripHotel getItripHotelById(Long id)throws Exception;

    public List<ItripHotel>	getItripHotelListByMap(Map<String,Object> param)throws Exception;

    public Integer getItripHotelCountByMap(Map<String,Object> param)throws Exception;

    public Integer itriptxAddItripHotel(ItripHotel itripHotel)throws Exception;

    public Integer itriptxModifyItripHotel(ItripHotel itripHotel)throws Exception;

    public Integer itriptxDeleteItripHotelById(Long id)throws Exception;

    public Page<ItripHotel> queryItripHotelPageByMap(Map<String,Object> param,Integer pageNo,Integer pageSize)throws Exception;

    HotelVideoDescVO getItripHotelVideoByHotelId(Long hotelId) throws Exception;
}
