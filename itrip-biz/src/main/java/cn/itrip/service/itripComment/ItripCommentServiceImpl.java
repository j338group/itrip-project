package cn.itrip.service.itripComment;
import cn.itrip.beans.pojo.ItripHotelOrder;
import cn.itrip.beans.pojo.ItripImage;
import cn.itrip.beans.vo.comment.ItripListCommentVO;
import cn.itrip.mapper.itripComment.ItripCommentMapper;
import cn.itrip.beans.pojo.ItripComment;
import cn.itrip.common.EmptyUtils;
import cn.itrip.common.Page;
import cn.itrip.mapper.itripHotelOrder.ItripHotelOrderMapper;
import cn.itrip.mapper.itripImage.ItripImageMapper;
import cn.itrip.service.itripImage.ItripImageService;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;
import cn.itrip.common.Constants;
@Service
public class ItripCommentServiceImpl implements ItripCommentService {

    @Resource
    private ItripCommentMapper itripCommentMapper;
    @Resource
    private ItripImageMapper itripImageMapper;
    @Resource
    private ItripHotelOrderMapper itripHotelOrderMapper;
    public ItripComment getItripCommentById(Long id)throws Exception{
        return itripCommentMapper.getItripCommentById(id);
    }

    public List<ItripComment>	getItripCommentListByMap(Map<String,Object> param)throws Exception{
        return itripCommentMapper.getItripCommentListByMap(param);
    }

    public Integer getItripCommentCountByMap(Map<String,Object> param)throws Exception{
        return itripCommentMapper.getItripCommentCountByMap(param);
    }

    public Integer itriptxAddItripComment(ItripComment itripComment)throws Exception{
            itripComment.setCreationDate(new Date());
            return itripCommentMapper.insertItripComment(itripComment);
    }

    public Integer itriptxModifyItripComment(ItripComment itripComment)throws Exception{
        itripComment.setModifyDate(new Date());
        return itripCommentMapper.updateItripComment(itripComment);
    }

    public Integer itriptxDeleteItripCommentById(Long id)throws Exception{
        return itripCommentMapper.deleteItripCommentById(id);
    }

    public Page<ItripListCommentVO> queryItripCommentPageByMap(Map<String,Object> param, Integer pageNo, Integer pageSize)throws Exception{
        Integer total = itripCommentMapper.getItripCommentCountByMap(param);
        pageNo = EmptyUtils.isEmpty(pageNo) ? Constants.DEFAULT_PAGE_NO : pageNo;
        pageSize = EmptyUtils.isEmpty(pageSize) ? Constants.DEFAULT_PAGE_SIZE : pageSize;
        Page page = new Page(pageNo, pageSize, total);
        param.put("beginPos", page.getBeginPos());
        param.put("pageSize", page.getPageSize());
        List<ItripListCommentVO> itripCommentList = itripCommentMapper.getItripCommentListWithDetailByMap(param);
        page.setRows(itripCommentList);
        return page;
    }

    @Override
    public void itriptxAddItripComment(ItripComment comment, ItripImage[] itripImages) throws Exception {
        //添加评论
        comment.setCreationDate(new Date());
        comment.setScore((comment.getFacilitiesScore()+comment.getHygieneScore()+comment.getPositionScore()+comment.getServiceScore())/4);
        itripCommentMapper.insertItripComment(comment);
        Long id = comment.getId();
        //添加图片
        for (ItripImage image : itripImages) {
            image.setTargetId(id);
            image.setCreationDate(new Date());
            image.setType("2");
            itripImageMapper.insertItripImage(image);
        }
        //修改订单状态
        ItripHotelOrder order = new ItripHotelOrder();
        order.setId(comment.getOrderId());
        order.setOrderStatus(4);
        itripHotelOrderMapper.updateItripHotelOrder(order);

    }

}
