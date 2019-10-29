package cn.itrip.search.dao;

import cn.itrip.beans.pojo.ItripHotel;
import cn.itrip.beans.vo.hotel.ItripHotelVO;
import cn.itrip.common.Constants;
import cn.itrip.common.Page;
import cn.itrip.common.PropertiesUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.List;

/**
 * description:
 * Created by Ray on 2019-10-29
 */
@Repository
public class BaseQuery<T> {
    private HttpSolrClient client=null;
    public BaseQuery() {
        String baseUrl = PropertiesUtils.get("database.properties", "baseUrl");
        client = new HttpSolrClient.Builder(baseUrl)
                .withConnectionTimeout(10000)
                .withSocketTimeout(60000)
                .build();
    }

    /**
     * 分页查询对象列表
     * @param solrQuery  搜索条件
     * @param pageNo 页码
     * @param pageSize  页面大小
     * @param clazz  要封装的实体类
     * @return
     * @throws IOException
     * @throws SolrServerException
     */
    public Page<T> searchHotelListByPage(SolrQuery solrQuery,Integer pageNo,Integer pageSize,Class<T> clazz) throws IOException, SolrServerException {
        //判断分页参数，给默认初始值
        pageNo=pageNo==null? Constants.DEFAULT_PAGE_NO :pageNo;
        pageSize=pageSize==null?Constants.DEFAULT_PAGE_SIZE:pageSize;
        solrQuery.setStart((pageNo-1)*pageSize);
        solrQuery.setRows(pageSize);
        QueryResponse response = client.query(solrQuery);
        List<T> beans = response.getBeans(clazz);
        long numFound = response.getResults().getNumFound();
        //封装实体列表到分页对象
        Page<T> page = new Page<>(pageNo,pageSize,(int)numFound);
        page.setRows(beans);
        return page;
    }

}
