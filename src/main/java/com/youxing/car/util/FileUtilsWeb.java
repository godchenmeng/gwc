package com.youxing.car.util;

import java.io.File;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author mars
 * @date 2017年3月24日 上午10:11:07
 */
public class FileUtilsWeb {

	public static String getFileType(String fileName) {
		if (StringUtils.isBlank(fileName)) {
			return null;
		}
		String name = fileName.indexOf(".") != -1 ? fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length()) : null;
		return name;

	}
	/**
	 * 
	* @author mars   
	* @date 2017年3月24日 上午10:25:26 
	* @Description: TODO(保存上传文件到某个文件夹) 
	* @param @param file
	* @param @param tar
	* @param @return
	* @param @throws Exception    设定文件 
	* @return String    返回类型 
	* @throws
	 */
	public static String saveFiles(MultipartFile file,String tar) throws Exception{
		String name = file.getOriginalFilename();
		String fileType = getFileType(name);
		if (fileType != null) {
			if ("PNG".equals(fileType.toUpperCase()) || "JPG".equals(fileType.toUpperCase()) || "JPEG".equals(fileType.toUpperCase())) {
				String trueFileName = String.valueOf(System.nanoTime()) + name;
				// 设置存放图片文件的路径
				String path = Constant.IMAGE_STORE_CAR + "/" + tar + "/" + trueFileName.replaceAll("-", "");
				File outputFile = new File(path);
				if (!outputFile.exists()) {
					outputFile.mkdirs();
				}
				file.transferTo(outputFile);
				return path;
			} else {
				return null;
			}
		}
		return null;
	}

}
