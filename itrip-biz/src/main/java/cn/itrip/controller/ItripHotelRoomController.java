package cn.itrip.controller;

import cn.itrip.beans.dto.Dto;
import cn.itrip.beans.pojo.ItripHotelRoom;
import cn.itrip.beans.pojo.ItripImage;
import cn.itrip.beans.pojo.ItripLabelDic;
import cn.itrip.beans.vo.ItripImageVO;
import cn.itrip.beans.vo.ItripLabelDicVO;
import cn.itrip.beans.vo.hotelroom.ItripHotelRoomVO;
import cn.itrip.beans.vo.hotelroom.SearchHotelRoomVO;
import cn.itrip.common.DtoUtil;
import cn.itrip.service.itripHotelRoom.ItripHotelRoomService;
import cn.itrip.service.itripImage.ItripImageService;
import cn.itrip.service.itripLabelDic.ItripLabelDicService;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;

/**
 * description:
 * Created by Ray on 2019-11-01
 */
@RestController
@RequestMapping("/api/hotelroom")
public class ItripHotelRoomController {
    @Resource
    private ItripImageService itripImageService;
    @Resource
    private ItripHotelRoomService itripHotelRoomService;
    @Resource
    private ItripLabelDicService itripLabelDicService;
    /**
     * 根据房型id查询图片
     * @param targetId
     * @return
     */
    @RequestMapping(value = "/getimg/{targetId}",method = RequestMethod.GET)
    public Dto getImg(@PathVariable Long targetId){
        if (targetId == null) {
            return DtoUtil.returnFail("酒店房型id不能为空", "100302");
        }
        Map<String, Object> param = new HashMap<>();
        param.put("targetId", targetId);
        param.put("type", "1");
        try {
            List<ItripImage> imageList = itripImageService.getItripImageListByMap(param);
            List<ItripImageVO> imageVOList = new ArrayList<>();
            for (ItripImage image : imageList) {
                ItripImageVO imageVO = new ItripImageVO();
                BeanUtils.copyProperties(image, imageVO);
                imageVOList.add(imageVO);
            }
            return DtoUtil.returnDataSuccess(imageVOList);
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.returnFail("获取酒店房型图片失败", "100301");
        }
    }
    @RequestMapping("/queryhotelroombed")
    public Dto<ItripLabelDicVO> queryHotelRoomBed(){

        Map<String, Object> param = new HashMap<>();
        param.put("parentId", 1);
        try {
            List<ItripLabelDic> labelDicList = itripLabelDicService.getItripLabelDicListByMap(param);
            List<ItripLabelDicVO> labelDicVOList = new ArrayList<>();
            for (ItripLabelDic labelDic : labelDicList) {
                ItripLabelDicVO labelDicVO = new ItripLabelDicVO();
                BeanUtils.copyProperties(labelDic, labelDicVO);
                labelDicVOList.add(labelDicVO);
            }
            return DtoUtil.returnDataSuccess(labelDicVOList);
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.returnFail("获取酒店房间床型失败", "100305");
        }

    }
    @RequestMapping("/queryhotelroombyhotel")
    public Dto queryHotelRoomByHotel(@RequestBody SearchHotelRoomVO roomVO){
        if (roomVO == null||roomVO.getHotelId()==null) {
            return DtoUtil.returnFail("酒店id不能为空", "100305");
        }
        Date startDate = roomVO.getStartDate();
        Date endDate = roomVO.getEndDate();
        if(startDate ==null|| endDate ==null){
            return DtoUtil.returnFail("入住和退房日期不能为空", "100306");
        }
        if(startDate.getTime()>endDate.getTime()){
            return DtoUtil.returnFail("退房日期不能早于入住日期", "100307");
        }
        try {
            List<ItripHotelRoomVO> roomVOList= itripHotelRoomService.getItripHotelRoomListBySearchRoomVO(roomVO);
            //以下格式配合前端的需求????????
            List<List<ItripHotelRoomVO>> list = new ArrayList<>();
            for (ItripHotelRoomVO hotelRoomVO : roomVOList) {
                List<ItripHotelRoomVO> tempList = new ArrayList<>();
                tempList.add(hotelRoomVO);
                list.add(tempList);
            }
            return DtoUtil.returnDataSuccess(list);
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.returnFail("查询房型列表失败", "100304");
        }


    }
}
