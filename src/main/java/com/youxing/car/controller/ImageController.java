package com.youxing.car.controller;

import com.youxing.car.util.*;

import io.swagger.annotations.ApiOperation;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Random;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.youxing.car.aop.LogAnnotation;
import com.youxing.car.entity.Attachment;
import com.youxing.car.entity.Car;
import com.youxing.car.entity.Driver;
import com.youxing.car.service.CarService;
import com.youxing.car.service.DriverService;

/**
 * @author mars
 * @date 2017年3月24日 下午1:47:20
 */
@Controller
public class ImageController {
	
	@Resource
	private CarService carService;
	@Resource
	private DriverService driverService;
	/**
	 * 
	* @author mars   
	* @date 2017年3月24日 下午1:47:33 
	* @Description: TODO(以流的方式返回图片) 
	* @param @param file
	* @param @param response
	* @param @param request
	* @param @throws IOException    设定文件 
	* @return void    返回类型 
	* @throws
	 */
	@LogAnnotation(description="image图片" )
	@RequestMapping(value="web/get/image", method = RequestMethod.GET)
	public void load(String file, HttpServletResponse response, HttpServletRequest request) throws IOException {
		if (file.startsWith("static")) {
			file = request.getSession().getServletContext().getRealPath("/") + file;
		}
		ByteArrayOutputStream outputStream = null;
		response.setHeader("Cache-Control", "no-store");
		response.setHeader("Pragma", "no-cache");
		response.setDateHeader("Expires", 0);
		String imgFormat = FileUtilsWeb.getFileType(file);
		response.setContentType("image/" + imgFormat);
		File img = new File(file);
		if (!img.exists()) {
			file = request.getSession().getServletContext().getRealPath("/").concat("static/assets/img/default.jpg");
		}
		ServletOutputStream responseOutputStream = null;
		try {
			outputStream = new ByteArrayOutputStream();
			BufferedImage image = ImageIO.read(new File(file));
			ImageIO.write(image, imgFormat, outputStream);
			responseOutputStream = response.getOutputStream();
			responseOutputStream.write(outputStream.toByteArray());
			responseOutputStream.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(outputStream!=null){				
				outputStream.close();
			}
			if(responseOutputStream!=null){				
				responseOutputStream.close();
			}
		}
	}
	
	/**
	 * @throws Exception 
	 * @throws Exception
	 * 
	 * @author mars
	 * @date 2017年3月24日 上午11:14:49
	 * @Description: TODO(文件上传)
	 * @param @param file
	 * @param @param tar 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	@LogAnnotation(description="image图片" )
	@RequestMapping(value = "web/upload/image", method = RequestMethod.POST)
	@ResponseBody
	public Attachment uploadImgae(MultipartFile Filedata, String tar) throws Exception {
		Attachment attache = new Attachment();
		attache.setIsSuccess(false);
		try {
			if (Filedata == null || Filedata.isEmpty()) {
				attache.setMsg("文件为空");
			}
			if (StringUtils.isBlank(tar)||"null".equals(tar)) {
				tar = String.valueOf(System.nanoTime());
			}
			String name = Filedata.getOriginalFilename();
			String fileType = FileUtilsWeb.getFileType(name);
			String car_imag_path = FileUtils.uploadTmpFile(Filedata, tar);
			if (StringUtils.isBlank(fileType) || !("PNG".equals(fileType.toUpperCase()) || "JPG".equals(fileType.toUpperCase()) || "JPEG".equals(fileType.toUpperCase()))) {
				attache.setMsg("文件类型不合法");
			} else {
				attache.setTar(tar);
				attache.setName(name);
				attache.setExt(fileType);
				attache.setUrl(car_imag_path);
				attache.setIsSuccess(true);
			}

		} catch (Exception e) {
			attache.setMsg(e.getMessage());
			e.printStackTrace();
		}
		return attache;
	}
	
	@LogAnnotation(description="证件信息上传" )
	@ApiOperation(value = "证件信息上传", httpMethod = "POST", response = Result.class)
	@RequestMapping(value = "app/upload/image",method = RequestMethod.POST)
	@ResponseBody
	public Result appUploadImage(HttpServletRequest request,String  type,String deputy, String car_id,String driver_id){
		
		Result res_error = Result.instance(ResponseCode.user_upload_error.getCode(), ResponseCode.user_upload_error.getMsg());
		
		if (request != null) {
			//把request对象强转成部件对象
			MultipartHttpServletRequest req = (MultipartHttpServletRequest)request;
			Iterator<String> names = req.getFileNames();
			String fileName = "";
			while(names.hasNext()){
				fileName = names.next();
			}
			//根据文件名获取文件对象
			CommonsMultipartFile  cm = (CommonsMultipartFile) req.getFile(fileName);
			
			//文件名在服务器有可能重复
			String newFileName = "";
			Random ran = new Random();
			for(int i=0;i<3;i++){
				newFileName = System.currentTimeMillis()+ran.nextInt(10)+"";
			}
			
			//获取文件的扩展名
			String originalFilename = cm.getOriginalFilename();
			String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
			
			//把文件关联到服务器
			String folderPath = request.getSession().getServletContext().getRealPath("");
			File targetFile = new File(folderPath+Constant.IMAGE_STORE_USER+newFileName+suffix);
			if(!targetFile.exists()){
				targetFile.mkdirs();
			}
			try {
				//上传
				cm.transferTo(targetFile);
			} catch (IllegalStateException e) {
				return res_error;
			} catch (IOException e) {
				return res_error;
			}
			
			//图片回显到客户端
			String fullPath = folderPath+Constant.IMAGE_STORE_USER+newFileName+suffix;//绝对路径
			String relativePath = Constant.IMAGE_STORE_USER+newFileName+suffix;//相对路径
			
			Car car = new Car();
			Driver driver = new Driver();
			if("1".equals(type) || "2".equals(type) ){
			if(StringUtils.isNotBlank(car_id)){
				car.setId(Long.parseLong(car_id));
			}			//type =1  行驶证
			if("1".equals(type)){
				
				//正副本  1正本  2 副本
				if("1".equals(deputy)){
					car.setDriver_image_1(relativePath);
				}
				if("2".equals(deputy)){
					car.setDriver_image_2(relativePath);
				}
				//type =2  货运资格证
			}else if("2".equals(type)){
				if("1".equals(deputy)){
					car.setShipping_image_1(relativePath);
				}
				if("2".equals(deputy)){
					car.setShipping_image_2(relativePath);
				}
				
			}
				carService.modify(car);
			}
			
			
			if("3".equals(type) || "4".equals(type) ){
				if(StringUtils.isNotBlank(driver_id)){
					driver.setId(Long.parseLong(driver_id));
				}
				if("3".equals(type)){
					//正副本  1正本  2 副本
					if("1".equals(deputy)){
						driver.setD_inmage_1(relativePath);
					}
					if("2".equals(deputy)){
						driver.setD_inmage_2(relativePath);
					}
				}else if("4".equals(type)){
					if("1".equals(deputy)){
						driver.setT_image_1(relativePath);
					}
					if("2".equals(deputy)){
						driver.setT_image_2(relativePath);
					}
					
				}
				driverService.modifyDriver(driver);
				}
				if("5".equals(type) ){
					if("1".equals(deputy)){
						driver.setId_image_1(relativePath);
					}
					if("2".equals(deputy)){
						driver.setId_image_2(relativePath);
					}
					driverService.modifyDriver(driver);
				}
			return Result.success(relativePath);
		}else{
			return res_error;
		}
		
	}
	

	
	
}
