package cn.itrip.mapper.itripComment;
import cn.itrip.beans.pojo.ItripComment;
import cn.itrip.beans.vo.comment.ItripListCommentVO;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;

public interface ItripCommentMapper {

	public ItripComment getItripCommentById(@Param(value = "id") Long id)throws Exception;

	public List<ItripComment>	getItripCommentListByMap(Map<String,Object> param)throws Exception;

	public Integer getItripCommentCountByMap(Map<String,Object> param)throws Exception;

	public Integer insertItripComment(ItripComment itripComment)throws Exception;

	public Integer updateItripComment(ItripComment itripComment)throws Exception;

	public Integer deleteItripCommentById(@Param(value = "id") Long id)throws Exception;

	/**
	 * 查询评论详情列表
	 * @param param
	 * @return
	 * @throws Exception
	 */
	List<ItripListCommentVO> getItripCommentListWithDetailByMap(Map<String, Object> param)throws Exception;
}
