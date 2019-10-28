package cn.itrip.search.dao;

import cn.itrip.common.PropertiesUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrRequest;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

import java.io.IOException;

/**
 * description:
 * Created by Ray on 2019-10-28
 */
public class Test {
    public static void main(String[] args) throws IOException, SolrServerException {
        HttpSolrClient client = new HttpSolrClient.Builder(PropertiesUtils.get("database.properties", "baseUrl"))
                .withConnectionTimeout(10000)
                .withSocketTimeout(60000)
                .build();

        SolrQuery query = new SolrQuery("*:*");
        query.setQuery("hotelName:*");
        //用户想搜索150-300的酒店
        //酒店的价格范围：最低价<300,最高>150
        //fq
        query.addFilterQuery("minPrice:[* TO 300]");
        query.addFilterQuery("maxPrice:[150 TO *]");
        //start
        query.setStart(0);
        //rows
        query.setRows(100);
        //sort
        query.setSort("id", SolrQuery.ORDER.desc);
        //fl
        query.setFields("id","hotelName");
        QueryResponse response = client.query(query, SolrRequest.METHOD.GET);
        SolrDocumentList results = response.getResults();
        System.out.println("size=="+results.getNumFound());
        for (SolrDocument result : results) {
            System.out.println(result.get("id")+"---"+result.get("hotelName")+"--"+result.get("minPrice")+"--"+result.get("maxPrice"));
        }

    }
}
