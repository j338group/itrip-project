package cn.itrip.controller;

import cn.itrip.beans.dto.Dto;
import cn.itrip.beans.pojo.ItripComment;
import cn.itrip.beans.pojo.ItripImage;
import cn.itrip.beans.pojo.ItripUser;
import cn.itrip.beans.vo.ItripImageVO;
import cn.itrip.beans.vo.comment.ItripAddCommentVO;
import cn.itrip.beans.vo.comment.ItripListCommentVO;
import cn.itrip.beans.vo.comment.ItripSearchCommentVO;
import cn.itrip.common.DtoUtil;
import cn.itrip.common.Page;
import cn.itrip.common.SystemConfig;
import cn.itrip.common.ValidationToken;
import cn.itrip.service.itripComment.ItripCommentService;
import cn.itrip.service.itripImage.ItripImageService;
import org.springframework.beans.BeanUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * description:
 * Created by Ray on 2019-11-11
 */
@RestController
@RequestMapping("/api/comment")
public class SystemCommentController {
    @Resource
    private ItripImageService itripImageService;
    @Resource
    private ItripCommentService itripCommentService;
    @Resource
    private ValidationToken validationToken;
    @Resource
    private SystemConfig systemConfig;
    @RequestMapping("/getimg/{targetId}")
    public Dto<ItripImageVO> getImg(@PathVariable Long targetId ){
        if (targetId == null) {
            return DtoUtil.returnFail("评论id不能为空", "100013");
        }

        Map<String, Object> param = new HashMap<>();
        param.put("targetId", targetId);
        param.put("type", "2");
        try {
            List<ItripImage> imageList = itripImageService.getItripImageListByMap(param);
            List<ItripImageVO> imageVOS = new ArrayList<>();
            for (ItripImage image : imageList) {
                ItripImageVO imageVO = new ItripImageVO();
                BeanUtils.copyProperties(image, imageVO);
                imageVOS.add(imageVO);
            }
            return DtoUtil.returnDataSuccess(imageVOS);
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.returnFail("获取评论图片失败", "100012");
        }
    }
    @RequestMapping(value = "/getcommentlist",method = RequestMethod.POST)
    public Dto getCommentList(@RequestBody ItripSearchCommentVO commentVO){
        Integer isHavingImg = commentVO.getIsHavingImg();
        Integer isOk = commentVO.getIsOk();
        Long hotelId = commentVO.getHotelId();
        Integer pageSize = commentVO.getPageSize();
        Integer pageNo = commentVO.getPageNo();
        if (hotelId ==null|| isHavingImg ==null
        || isOk ==null|| pageSize ==null|| pageNo ==null) {
            return DtoUtil.returnFail("请求参数不完整", "100014");
        }
        //判断是否获取所有评论
        if (isHavingImg ==-1) {
            isHavingImg=null;
        }
        if (isOk ==-1) {
            isOk=null;
        }
        //获取评论列表（分页）
        Map<String, Object> param = new HashMap<>();
        param.put("hotelId", hotelId);
        param.put("isHavingImg", isHavingImg);
        param.put("isOk", isOk);
        try {
            Page<ItripListCommentVO> itripCommentPage = itripCommentService.queryItripCommentPageByMap(param, pageNo, pageSize);
            return DtoUtil.returnDataSuccess(itripCommentPage);
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.returnFail("获取评论详情列表失败", "100015");
        }
    }
/*    @RequestMapping("/upload")//<input type="file" name="uploadFile">
    public Dto<String> uploadPic(@RequestParam("uploadFile")MultipartFile multipartFile){
        String filename = multipartFile.getOriginalFilename();
        System.out.println(filename+"--------------------");
        try {
            multipartFile.transferTo(new File("F:/", filename));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }*/

    @RequestMapping("/upload")//<input type="file" name="uploadFile">
    public Dto<String> uploadPic(HttpServletRequest request){
        CommonsMultipartResolver resolver = new CommonsMultipartResolver(request.getSession().getServletContext());
//        resolver.setServletContext(request.getSession().getServletContext());
//        resolver.setResolveLazily(true);
        //判断是否是文件上传请求
        if (resolver.isMultipart(request)) {
            //token认证
            String token = request.getHeader("token");
            ItripUser currentUser = validationToken.getCurrentUser(token);
            if (currentUser == null) {
                return DtoUtil.returnFail("token认证失败！", "100016");
            }
            //把http请求转换成文件上传的请求
            MultipartHttpServletRequest multipartRequest = resolver.resolveMultipart(request);
            MultiValueMap<String, MultipartFile> multiFileMap = multipartRequest.getMultiFileMap();//uploadFile,
            List<MultipartFile> multipartFiles = multiFileMap.get("uploadFile");
            List<String> filePathUrls = new ArrayList<>();
            for (MultipartFile multipartFile : multipartFiles) {
                //处理文件
                //判断文件类型
                String filename = multipartFile.getOriginalFilename();
                String suffix = filename.substring(filename.lastIndexOf("."));
                if (!(".jpg".equals(suffix)||".jpeg".equals(suffix)||".png".equals(suffix))) {
                    return DtoUtil.returnFail("上传文件类型不合法", "100017");
                }
                //新文件名：userId-毫秒值-随机数
                String newFilename=currentUser.getId()+"-"+System.currentTimeMillis()+"-"+(int)(Math.random()*100000)+suffix;
                System.out.println(filename+"-------------");
                try {
                    //保存文件
                    multipartFile.transferTo(new File(systemConfig.getFileUploadPathString(),newFilename));
                    filePathUrls.add(systemConfig.getVisitImgUrlString()+newFilename);
                } catch (IOException e) {
                    e.printStackTrace();
                    return DtoUtil.returnFail("保存文件失败", "100018");
                }

            }
            return DtoUtil.returnDataSuccess(filePathUrls);
        }else{
            return DtoUtil.returnFail("不是文件上传请求", "100019");
        }
    }
    @RequestMapping(value = "/delpic",method = RequestMethod.POST)
    public Dto delPic(@RequestParam String imgName,HttpServletRequest request){
        //token认证
        String token = request.getHeader("token");
        ItripUser currentUser = validationToken.getCurrentUser(token);
        if (currentUser == null) {
            return DtoUtil.returnFail("token认证失败！", "100016");
        }
        //
        File file = new File(systemConfig.getFileUploadPathString(), imgName);
        if (!file.exists()) {
            return DtoUtil.returnFail("文件不存在", "100010");
        }
        file.delete();
        return DtoUtil.returnSuccess("删除成功！");
    }
    @RequestMapping(value = "/add",method = RequestMethod.POST)
    public Dto addComment(@RequestBody ItripAddCommentVO commentVO,HttpServletRequest request){
        //token认证
        String token = request.getHeader("token");
        ItripUser currentUser = validationToken.getCurrentUser(token);
        if (currentUser == null) {
            return DtoUtil.returnFail("token认证失败！", "100016");
        }
        //封装comment-pojo
        ItripComment comment = new ItripComment();
        BeanUtils.copyProperties(commentVO, comment);
        comment.setUserId(currentUser.getId());
        comment.setCreatedBy(currentUser.getId());
        try {
            itripCommentService.itriptxAddItripComment(comment, commentVO.getItripImages());
            return DtoUtil.returnSuccess("添加评论成功！");
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.returnFail("添加评论失败","100020");
        }
    }
}
