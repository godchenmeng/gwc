package com.youxing.car.controller;

import com.youxing.car.entity.Fence;
import com.youxing.car.entity.Organization;
import com.youxing.car.entity.User;
import com.youxing.car.page.Page;
import com.youxing.car.response.FailedResponse;
import com.youxing.car.response.Response;
import com.youxing.car.response.SuccessResponse;
import com.youxing.car.service.FenceService;
import com.youxing.car.service.OrganizationService;
import com.youxing.car.service.UserService;
import com.youxing.car.util.Constant;
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
import java.util.HashMap;
import java.util.List;

@Api(value = "fence", description = "fence")
@Controller
public class FenceController {
    @Resource
    private FenceService fenceService;
    @Resource
    private UserService userService;
    @Resource
    private OrganizationService organizationService;
    /**
     *
     * @author CM
     * @date 2017年9月8日 下午16:32:12
     * @Description: TODO(分页显示电子栅栏)
     * @param @param limit
     * @param @param pageIndex
     * @param @param request
     * @param @return    设定文件
     * @return Page    返回类型
     * @throws
     */
    @ApiOperation(value = "栅栏分页显示", httpMethod = "POST", response = Result.class, notes = "必须的参数： limit:15,pageIndex:0")
    @SuppressWarnings("rawtypes")
    @RequestMapping(value="web/fence/page",method= RequestMethod.POST)
    @ResponseBody
    public Page fence(int limit, int pageIndex, HttpServletRequest request) {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("startIdx", pageIndex*limit);
        map.put("limit", limit);
        User u =new User();
        String id1 = request.getParameter("keyid");
        u.setId(Long.parseLong(id1));
        User user = userService.findBy(u);

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
        List<Fence> list = fenceService.pageBy(map);
        int recodes = fenceService.countBy(map);
        return new Page<Fence>(list, recodes, limit);
    }

    /**
     *
     * @author CM
     * @date 2017年9月10日
     * @Description: TODO(添加电子栅栏)
     * @param @param fence
     * @return Response    返回类型
     * @throws
     */
    @ApiOperation(value = "添加电子栅栏", httpMethod = "POST", response = Result.class, notes = "必须的参数：fence:{电子栅栏对象}")
    @RequiresPermissions(value = "web/fence/add")
    @RequestMapping(value="web/fence/add",method=RequestMethod.POST)
    @ResponseBody
    public Response fenceAdd(Fence fence, HttpServletRequest request) {
        List<Fence> allName = fenceService.findName();
        for(Fence tmpFence : allName){
            if(tmpFence.getFence_name().equals(fence.getFence_name())){
                return new FailedResponse("该栅栏名称已经存在！");
            }
        }
        try{
            User u =new User();
            String id1 = request.getParameter("keyid");
            u.setId(Long.parseLong(id1));
            User user = userService.findBy(u);

            fence.setFence_createby(user.getId());
            fence.setFence_org(user.getOrg());
            fence.setFence_status(Integer.parseInt(Constant.REMOVE_STATUS));
            fenceService.add(fence);
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
     * @Description: TODO(修改电子栅栏)
     * @param @param fence
     * @return Response    返回类型
     * @throws
     */
    @ApiOperation(value = "修改电子栅栏", httpMethod = "POST", response = Result.class, notes = "必须的参数：fence:{电子栅栏对象}")
    @RequiresPermissions(value = "web/fence/modify")
    @RequestMapping(value="web/fence/modify",method=RequestMethod.POST)
    @ResponseBody
    public Response fenceModify(Fence fence, HttpServletRequest request) {
        List<Fence> allName = fenceService.findName();
        for(Fence tmpFence : allName){
            if(tmpFence.getFence_name().equals(fence.getFence_name()) && !tmpFence.getId().equals(fence.getId())){
                return new FailedResponse("该栅栏名称已经存在！");
            }
        }
        try{
            fenceService.modify(fence);
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
     * @Description: TODO(删除电子栅栏)
     * @param @param fence
     * @return Response    返回类型
     * @throws
     */
    @ApiOperation(value = "删除电子栅栏", httpMethod = "POST", response = Result.class, notes = "必须的参数：fence:{电子栅栏对象}")
    @RequiresPermissions(value = "web/fence/delete")
    @RequestMapping(value="web/fence/delete",method=RequestMethod.POST)
    @ResponseBody
    public Response fenceDelete(Fence fence, HttpServletRequest request) {
        try{
            fenceService.removeById(fence.getId());
            return new SuccessResponse();
        }catch (Exception ex){
            ex.printStackTrace();
            return new FailedResponse("服务器错误");
        }
    }
}
