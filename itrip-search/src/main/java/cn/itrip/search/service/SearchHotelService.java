package cn.itrip.search.service;

import cn.itrip.beans.vo.hotel.ItripHotelVO;
import cn.itrip.beans.vo.hotel.SearchHotelVO;
import cn.itrip.common.Page;

import java.util.List;

/**
 * description:
 * Created by Ray on 2019-10-29
 */
public interface SearchHotelService {
    Page<ItripHotelVO> getItripHotelListByPage(SearchHotelVO searchHotelVO) throws Exception;

    List<ItripHotelVO> getItripHotelListByCity(Integer cityId,Integer count)throws Exception;
}
