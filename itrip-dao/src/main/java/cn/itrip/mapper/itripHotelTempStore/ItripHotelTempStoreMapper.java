package cn.itrip.mapper.itripHotelTempStore;
import cn.itrip.beans.pojo.ItripHotelTempStore;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;

public interface ItripHotelTempStoreMapper {

	public ItripHotelTempStore getItripHotelTempStoreById(@Param(value = "id") Long id)throws Exception;

	public List<ItripHotelTempStore>	getItripHotelTempStoreListByMap(Map<String,Object> param)throws Exception;

	public Integer getItripHotelTempStoreCountByMap(Map<String,Object> param)throws Exception;

	public Integer insertItripHotelTempStore(ItripHotelTempStore itripHotelTempStore)throws Exception;

	public Integer updateItripHotelTempStore(ItripHotelTempStore itripHotelTempStore)throws Exception;

	public Integer deleteItripHotelTempStoreById(@Param(value = "id") Long id)throws Exception;

	/**
	 * 刷新实时库存表
	 * @param param
	 */
    void flushRoomTempStore(Map<String, Object> param) throws Exception;

	List<ItripHotelTempStore> getItripHotelTempStoresByMap(Map<String, Object> param)throws Exception;

    void updateTempStore(Map<String, Object> param) throws Exception;
}
