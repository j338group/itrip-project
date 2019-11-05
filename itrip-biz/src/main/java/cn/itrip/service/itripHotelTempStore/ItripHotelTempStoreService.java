package cn.itrip.service.itripHotelTempStore;
import cn.itrip.beans.pojo.ItripHotelTempStore;
import java.util.List;
import java.util.Map;

import cn.itrip.common.Page;
/**
* Created by shang-pc on 2015/11/7.
*/
public interface ItripHotelTempStoreService {

    public ItripHotelTempStore getItripHotelTempStoreById(Long id)throws Exception;

    public List<ItripHotelTempStore>	getItripHotelTempStoreListByMap(Map<String,Object> param)throws Exception;

    public Integer getItripHotelTempStoreCountByMap(Map<String,Object> param)throws Exception;

    public Integer itriptxAddItripHotelTempStore(ItripHotelTempStore itripHotelTempStore)throws Exception;

    public Integer itriptxModifyItripHotelTempStore(ItripHotelTempStore itripHotelTempStore)throws Exception;

    public Integer itriptxDeleteItripHotelTempStoreById(Long id)throws Exception;

    public Page<ItripHotelTempStore> queryItripHotelTempStorePageByMap(Map<String,Object> param,Integer pageNo,Integer pageSize)throws Exception;

    /**
     * 获取指定房型，指定日期的剩余库存
     * @param param
     * @return
     * @throws Exception
     */
    List<ItripHotelTempStore> getItripHotelTempStoresByMap(Map<String, Object> param) throws Exception;

    Boolean validateTempStore(Map<String, Object> param) throws Exception;
}
