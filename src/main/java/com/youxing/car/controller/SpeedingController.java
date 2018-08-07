package com.youxing.car.controller;

import com.youxing.car.aop.LogAnnotation;
import com.youxing.car.entity.Organization;
import com.youxing.car.entity.Speeding;
import com.youxing.car.entity.User;
import com.youxing.car.page.Page;
import com.youxing.car.response.FailedResponse;
import com.youxing.car.response.Response;
import com.youxing.car.response.SuccessResponse;
import com.youxing.car.service.OrganizationService;
import com.youxing.car.service.SpeedingService;
import com.youxing.car.service.UserService;
import com.youxing.car.util.Constant;
import com.youxing.car.util.DateUtils;
import com.youxing.car.util.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Api(value = "speeding", description = "超速设置")
@Controller
public class SpeedingController {
    @Resource
    private SpeedingService speedingService;

    @Resource
    private UserService userService;
    @Resource
    private OrganizationService organizationService;
    /**
     *
     * @author CM
     * @date 2017年9月18日 下午16:32:12
     * @Description: TODO(分页显示超速信息)
     * @param @param limit
     * @param @param pageIndex
     * @param @param request
     * @param @return    设定文件
     * @return Page    返回类型
     * @throws
     */
	@LogAnnotation(description="分页显示超速信息" )
    @ApiOperation(value = "分页显示超速信息", httpMethod = "POST", response = Result.class, notes = "必须的参数： limit:15,pageIndex:0")
    @SuppressWarnings("rawtypes")
    @RequestMapping(value="web/speeding/page",method= RequestMethod.POST)
    @ResponseBody
    public Page speeding(int limit, int pageIndex, HttpServletRequest request) {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("startIdx", pageIndex*limit);
        map.put("limit", limit);
        User user =new User();
        String id1 = request.getParameter("keyid");
        user.setId(Long.parseLong(id1));
        user = userService.findBy(user);
        Long orgId = user.getOrg();
        Organization userOrg = organizationService.findById(orgId);
        if (userOrg.getParent() != null) {
            List<Long> org_list = organizationService.getOrganizationUser(userOrg, orgId);
            if (!CollectionUtils.isEmpty(org_list)) {
                org_list.add(orgId);
                map.put("list", org_list);
            } else {
                map.put("org", orgId);
            }
        }
        List<Speeding> list = speedingService.pageBy(map);
        int recodes = speedingService.countBy(map);
        return new Page<Speeding>(list, recodes, limit);
    }

    /**
     *
     * @author CM
     * @date 2017年9月18日
     * @Description: TODO(添加超速设置)
     * @param @param speeding
     * @return Response    返回类型
     * @throws
     */
	@LogAnnotation(description="添加超速设置" )
    @ApiOperation(value = "添加超速设置", httpMethod = "POST", response = Result.class, notes = "必须的参数：speeding:{超速设置对象}")
    @RequiresPermissions(value = "web/speeding/add")
    @RequestMapping(value="web/speeding/add",method=RequestMethod.POST)
    @ResponseBody
    public Response speedingAdd(Speeding speeding, HttpServletRequest request) {
        List<Speeding> allName = speedingService.findName();
        for(Speeding tmpSpeeding : allName){
            if(tmpSpeeding.getSpeeding_name().equals(speeding.getSpeeding_name())){
                return new FailedResponse("该超速设置已经存在！");
            }
        }
        try{
            User user =new User();
            String id1 = request.getParameter("keyid");
            user.setId(Long.parseLong(id1));
            user = userService.findBy(user);
            speeding.setSpeeding_createby(user.getId());
            speeding.setSpeeding_org(user.getOrg());
            speeding.setSpeeding_status(Integer.parseInt(Constant.REMOVE_STATUS));
            speedingService.add(speeding);
            return new SuccessResponse();
        }catch (Exception ex){
            ex.printStackTrace();
            return new FailedResponse("服务器错误");
        }
    }

    /**
     *
     * @author CM
     * @date 2017年9月10日
     * @Description: TODO(修改超速设置)
     * @param @param speeding
     * @return Response    返回类型
     * @throws
     */
	@LogAnnotation(description="修改超速设置" )
    @ApiOperation(value = "修改超速设置", httpMethod = "POST", response = Result.class, notes = "必须的参数：speeding:{超速设置对象}")
    @RequiresPermissions(value = "web/speeding/modify")
    @RequestMapping(value="web/speeding/modify",method=RequestMethod.POST)
    @ResponseBody
    public Response speedingModify(Speeding speeding, HttpServletRequest request) {
        List<Speeding> allName = speedingService.findName();
        for(Speeding tmpSpeeding : allName){
            if(tmpSpeeding.getSpeeding_name().equals(speeding.getSpeeding_name()) && !tmpSpeeding.getId().equals(speeding.getId())){
                return new FailedResponse("该超速设置已经存在！");
            }
        }
        try{
        	speeding.setUpdatedate(DateUtils.formatDateTime(new Date(), "yyyy-MM-dd HH:mm:ss"));
            speedingService.modify(speeding);
            return new SuccessResponse();
        }catch (Exception ex){
            ex.printStackTrace();
            return new FailedResponse("服务器错误");
        }
    }

    /**
     *
     * @author CM
     * @date 2017年9月10日
     * @Description: TODO(删除超速设置)
     * @param @param speeding
     * @return Response    返回类型
     * @throws
     */
	@LogAnnotation(description="删除超速设置" )
    @ApiOperation(value = "删除超速设置", httpMethod = "POST", response = Result.class, notes = "必须的参数：speeding:{超速设置对象}")
    @RequiresPermissions(value = "web/speeding/delete")
    @RequestMapping(value="web/speeding/delete",method=RequestMethod.POST)
    @ResponseBody
    public Response speedingDelete(Speeding speeding, HttpServletRequest request) {
        try{
            speedingService.removeById(speeding.getId());
            return new SuccessResponse();
        }catch (Exception ex){
            ex.printStackTrace();
            return new FailedResponse("服务器错误");
        }
    }
}
