package cn.itrip.search.service;

import cn.itrip.beans.vo.hotel.ItripHotelVO;
import cn.itrip.beans.vo.hotel.SearchHotelVO;
import cn.itrip.common.EmptyUtils;
import cn.itrip.common.Page;
import cn.itrip.search.dao.BaseQuery;
import org.apache.solr.client.solrj.SolrQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * description:
 * Created by Ray on 2019-10-29
 */
@Service
public class SearchHotelServiceImpl implements SearchHotelService {
    private Logger logger = LoggerFactory.getLogger(SearchHotelServiceImpl.class);
    @Resource
    private BaseQuery baseQuery;
    @Override
    public Page<ItripHotelVO> getItripHotelListByPage(SearchHotelVO hotelVO) throws Exception {
        //封装solr查询条件
        SolrQuery query = new SolrQuery("*:*");
        //封装全文检索（目的地和关键字）
        //destination:北京 AND keyword:首都|人民大会堂
        StringBuffer sb = new StringBuffer();
        sb.append("destination:" + hotelVO.getDestination());
        String keywords = hotelVO.getKeywords();
        if(EmptyUtils.isNotEmpty(keywords)){
            sb.append(" AND keyword:" + keywords.replace(" ", "|"));
        }
        logger.debug("-------------全文检索条件-------{}",sb);
        query.setQuery(sb.toString());
        //封装价格条件
        //150-300
        Double minPrice = hotelVO.getMinPrice();
        if(EmptyUtils.isNotEmpty(minPrice)){
            query.addFilterQuery("maxPrice:["+ minPrice +" TO *]");
        }
        Double maxPrice = hotelVO.getMaxPrice();
        if(EmptyUtils.isNotEmpty(maxPrice)){
            query.addFilterQuery("minPrice:[* TO "+maxPrice+"]");
        }
        //封装商圈条件
        //前端过来的格式 3665,3620
        //tradingAreaIds:(*,3665,* OR *,3619,*)
        String areaIds = hotelVO.getTradeAreaIds();
        if(EmptyUtils.isNotEmpty(areaIds)){
            StringBuffer sb1 = new StringBuffer();
            sb1.append("tradingAreaIds:(");
            String[] ids = areaIds.split(",");
            for (int i = 0; i < ids.length; i++) {
                if(i==0){
                    sb1.append("*," + ids[i] + ",*");
                }else{
                    sb1.append(" OR *," + ids[i] + ",*");
                }
            }
            sb1.append(")");
            logger.debug("---------商圈条件-----{}",sb1);
            query.addFilterQuery(sb1.toString());
        }
        //星级条件
        Integer hotelLevel = hotelVO.getHotelLevel();
        if(EmptyUtils.isNotEmpty(hotelLevel)){
            query.addFilterQuery("hotelLevel:" + hotelLevel);
        }
        //酒店特色
        //前端数据格式 ：17,116,117
        String featureIds = hotelVO.getFeatureIds();
        if(EmptyUtils.isNotEmpty(featureIds)){
            StringBuffer sb1 = new StringBuffer();
            sb1.append("featureIds:(");
            String[] ids = featureIds.split(",");
            for (int i = 0; i < ids.length; i++) {
                if(i==0){
                    sb1.append("*," + ids[i] + ",*");
                }else{
                    sb1.append(" OR *," + ids[i] + ",*");
                }
            }
            sb1.append(")");
            logger.debug("--------特色条件-----{}",sb1);
            query.addFilterQuery(sb1.toString());
        }
        //排序,前端数据：排序的字段
        String ascSort = hotelVO.getAscSort();
        if(EmptyUtils.isNotEmpty(ascSort)){
            query.setSort(ascSort, SolrQuery.ORDER.asc);
        }
        String descSort = hotelVO.getDescSort();
        if(EmptyUtils.isNotEmpty(descSort)){
            query.setSort(descSort, SolrQuery.ORDER.desc);
        }
        //调用dao层代码进行查询
       Page<ItripHotelVO> page= baseQuery.searchHotelListByPage(query,hotelVO.getPageNo(),hotelVO.getPageSize(),ItripHotelVO.class);
        //返回结果
        return page;
    }

    @Override
    public List<ItripHotelVO> getItripHotelListByCity(Integer cityId,Integer count) throws Exception {
        SolrQuery query = new SolrQuery("*:*");
        query.setQuery("cityId:"+cityId);

        Page<ItripHotelVO> page = baseQuery.searchHotelListByPage(query, 1, count, ItripHotelVO.class);
        List<ItripHotelVO> rows = page.getRows();
        return rows;
    }
}
