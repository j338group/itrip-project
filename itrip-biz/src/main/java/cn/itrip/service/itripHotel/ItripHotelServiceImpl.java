package cn.itrip.service.itripHotel;
import cn.itrip.beans.pojo.ItripAreaDic;
import cn.itrip.beans.pojo.ItripLabelDic;
import cn.itrip.beans.vo.hotel.HotelVideoDescVO;
import cn.itrip.mapper.itripAreaDic.ItripAreaDicMapper;
import cn.itrip.mapper.itripHotel.ItripHotelMapper;
import cn.itrip.beans.pojo.ItripHotel;
import cn.itrip.common.EmptyUtils;
import cn.itrip.common.Page;
import cn.itrip.mapper.itripLabelDic.ItripLabelDicMapper;
import cn.itrip.service.itripAreaDic.ItripAreaDicService;
import cn.itrip.service.itripLabelDic.ItripLabelDicService;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import cn.itrip.common.Constants;
@Service
public class ItripHotelServiceImpl implements ItripHotelService {

    @Resource
    private ItripHotelMapper itripHotelMapper;
    @Resource
    private ItripAreaDicMapper itripAreaDicMapper;
    @Resource
    private ItripLabelDicMapper itripLabelDicMapper;

    public ItripHotel getItripHotelById(Long id)throws Exception{
        return itripHotelMapper.getItripHotelById(id);
    }

    public List<ItripHotel>	getItripHotelListByMap(Map<String,Object> param)throws Exception{
        return itripHotelMapper.getItripHotelListByMap(param);
    }

    public Integer getItripHotelCountByMap(Map<String,Object> param)throws Exception{
        return itripHotelMapper.getItripHotelCountByMap(param);
    }

    public Integer itriptxAddItripHotel(ItripHotel itripHotel)throws Exception{
            itripHotel.setCreationDate(new Date());
            return itripHotelMapper.insertItripHotel(itripHotel);
    }

    public Integer itriptxModifyItripHotel(ItripHotel itripHotel)throws Exception{
        itripHotel.setModifyDate(new Date());
        return itripHotelMapper.updateItripHotel(itripHotel);
    }

    public Integer itriptxDeleteItripHotelById(Long id)throws Exception{
        return itripHotelMapper.deleteItripHotelById(id);
    }

    public Page<ItripHotel> queryItripHotelPageByMap(Map<String,Object> param,Integer pageNo,Integer pageSize)throws Exception{
        Integer total = itripHotelMapper.getItripHotelCountByMap(param);
        pageNo = EmptyUtils.isEmpty(pageNo) ? Constants.DEFAULT_PAGE_NO : pageNo;
        pageSize = EmptyUtils.isEmpty(pageSize) ? Constants.DEFAULT_PAGE_SIZE : pageSize;
        Page page = new Page(pageNo, pageSize, total);
        param.put("beginPos", page.getBeginPos());
        param.put("pageSize", page.getPageSize());
        List<ItripHotel> itripHotelList = itripHotelMapper.getItripHotelListByMap(param);
        page.setRows(itripHotelList);
        return page;
    }

    /**
     * 根据酒店id查询视频描述信息
     * @param hotelId
     * @return
     * @throws Exception
     */
    @Override
    public HotelVideoDescVO getItripHotelVideoByHotelId(Long hotelId) throws Exception {
        HotelVideoDescVO videoDescVO = new HotelVideoDescVO();
        //获取酒店名称
        ItripHotel itripHotel = itripHotelMapper.getItripHotelById(hotelId);
        videoDescVO.setHotelName(itripHotel.getHotelName());

        //获取酒店所处的商圈名称列表
        List<ItripAreaDic> list=itripAreaDicMapper.getItripAreaDicListByHotelId(hotelId);
        List<String> areaNameList = new ArrayList<>();
        for (ItripAreaDic areaDic : list) {
            areaNameList.add(areaDic.getName());
        }
        videoDescVO.setTradingAreaNameList(areaNameList);

        //获取酒店特色名称列表
        List<ItripLabelDic>  labelDics=itripLabelDicMapper.getItripLableDicListByHotelId(hotelId);
        List<String> featureNameList = new ArrayList<>();
        for (ItripLabelDic labelDic : labelDics) {
            featureNameList.add(labelDic.getName());
        }
        videoDescVO.setHotelFeatureList(featureNameList);


        return videoDescVO;
    }

}
