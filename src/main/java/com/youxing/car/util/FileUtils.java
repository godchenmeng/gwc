package com.youxing.car.util;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Random;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.ImageOutputStream;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Element;

/**
 * @author mars
 * @date 2017年3月24日 上午10:11:07
 */
public class FileUtils {

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
			if ("PNG".equals(fileType.toUpperCase()) || "JPG".equals(fileType.toUpperCase())) {
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
	/**
	 * Description: 向FTP服务器上传文件
	 * @param tar FTP服务器保存目录
	 * @param file 输入流
	 * @return 成功返回true，否则返回false
	 */
	public static String uploadTmpFile(MultipartFile file, String tar) {
		Properties p = ConfigUtils.getConfig();
		FTPClient ftp = new FTPClient();

		try{
			String ip = p.getProperty("ftp.ip");
			String username = p.getProperty("ftp.user");
			String password = p.getProperty("ftp.pwd");
			String name = file.getOriginalFilename();
			String fileType = getFileType(name);
			if (fileType != null) {
				if ("PNG".equals(fileType.toUpperCase()) || "JPG".equals(fileType.toUpperCase()) || "JPEG".equals(fileType.toUpperCase()) || "BMP".equals(fileType.toUpperCase())) {
					// 设置存放图片文件的路径
					String fileName = String.valueOf(System.nanoTime()) + String.valueOf(new Random().nextInt(1000)) + "." + fileType;
					String fileNameTemp = fileName + ".tmp";
					ftp.connect(ip);//连接FTP服务器


					boolean reply = ftp.login(username, password);//登录
					if (!reply) {
						ftp.disconnect();
						return null;
					}
					ftp.enterLocalPassiveMode();
					ftp.changeWorkingDirectory("tmp");
					ftp.makeDirectory(tar);
					ftp.changeWorkingDirectory(tar);
					ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
					ftp.setControlEncoding("UTF-8");
					boolean isSuccess = false;
					if(file.getSize() > 3145728){
					InputStream input = compressPicVersion(file);
					isSuccess = ftp.storeFile(fileNameTemp, input);
					}else{
						isSuccess = ftp.storeFile(fileNameTemp, file.getInputStream());
					}
					if(isSuccess){
						ftp.rename(fileNameTemp,fileName);
					}else{
						return  null;
					}
					ftp.logout();
					return "tmp/" + tar + "/" + fileName;
				} else {
					return null;
				}
			}
		}catch (Exception ex){
			ex.printStackTrace();
		} finally {
			if (ftp.isConnected()) {
				try {
					ftp.disconnect();
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
			}
		}
		return null;
	}
	/**
	 * Description: 向FTP服务器上传文件
	 * @param fromSrc FTP服务器临时保存目录
	 * @return 成功返回true，否则返回false
	 */
	public static String uploadFile(String fromSrc) {
		Properties p = ConfigUtils.getConfig();
		FTPClient ftp = new FTPClient();
		try{
			String ip = p.getProperty("ftp.ip");
			String username = p.getProperty("ftp.user");
			String password = p.getProperty("ftp.pwd");

			int reply;
			ftp.connect(ip, 21);//连接FTP服务器
			ftp.login(username, password);//登录
			reply = ftp.getReplyCode();
			if (!FTPReply.isPositiveCompletion(reply)) {
				ftp.disconnect();
				return null;
			}
			String [] path = fromSrc.split("/");
			ftp.makeDirectory(path[1]);
			ftp.changeWorkingDirectory(path[0]);
			ftp.changeWorkingDirectory(path[1]);
			ftp.rename(path[2],"/" + path[1] + "/" + path[2]);
			if(ftp.listFiles("/" + path[0] + "/" + path[1]).length == 0) {
				ftp.removeDirectory("/" + path[0] + "/" + path[1]);
			}
			ftp.logout();
			return path[1] + "/" + path[2];
		}catch (Exception ex){
			ex.printStackTrace();
		} finally {
			if (ftp.isConnected()) {
				try {
					ftp.disconnect();
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
			}
		}
		return null;
	}
	/**
	 * Description: 验证图片是否是缓存文件夹中的文件，是则 保存到正式文件夹，否则不变
	 * @param url
	 * @return
	 */
	public static String checkUploadFile(String url){
		if(StringUtils.isBlank(url)){
			return null;
		}
		if(url.startsWith("tmp")){
			return uploadFile(url);
		}else{
			return url;
		}
	}
	
	//压缩大小不压缩尺寸
	public static InputStream compressPicVersion(MultipartFile file) throws IOException {
		Image img = ImageIO.read(file.getInputStream());
		int newWidth = img.getWidth(null);
		int newHeight = img.getHeight(null);
		BufferedImage tag = new BufferedImage((int) newWidth, (int) newHeight, BufferedImage.TYPE_INT_RGB);
		tag.getGraphics().drawImage(img.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH), 0, 0, null);
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		saveAsJPEG(100, tag, 0.5f, os); 
		InputStream is = new ByteArrayInputStream(os.toByteArray());
		return is;
	}
	
	
	   /** 
     * 以JPEG编码保存图片 
     * @param dpi  分辨率 
     * @param image_to_save  要处理的图像图片 
     * @param JPEGcompression  压缩比 
     * @param fos 文件输出流 
     * @throws IOException 
     */  
    public static void saveAsJPEG(Integer dpi ,BufferedImage image_to_save, float JPEGcompression, ByteArrayOutputStream fos) throws IOException {  
            
        //useful documentation at http://docs.oracle.com/javase/7/docs/api/javax/imageio/metadata/doc-files/jpeg_metadata.html  
        //useful example program at http://johnbokma.com/java/obtaining-image-metadata.html to output JPEG data  
        
        //old jpeg class  
        //com.sun.image.codec.jpeg.JPEGImageEncoder jpegEncoder  =  com.sun.image.codec.jpeg.JPEGCodec.createJPEGEncoder(fos);  
        //com.sun.image.codec.jpeg.JPEGEncodeParam jpegEncodeParam  =  jpegEncoder.getDefaultJPEGEncodeParam(image_to_save);  
        
        // Image writer  
//      JPEGImageWriter imageWriter = (JPEGImageWriter) ImageIO.getImageWritersBySuffix("jpeg").next();  
        ImageWriter imageWriter  =   ImageIO.getImageWritersBySuffix("jpg").next();  
        ImageOutputStream ios  =  ImageIO.createImageOutputStream(fos);  
        imageWriter.setOutput(ios);  
        //and metadata  
        IIOMetadata imageMetaData  =  imageWriter.getDefaultImageMetadata(new ImageTypeSpecifier(image_to_save), null);  
           
           
        if(dpi !=  null && !dpi.equals("")){  
               
             //old metadata  
            //jpegEncodeParam.setDensityUnit(com.sun.image.codec.jpeg.JPEGEncodeParam.DENSITY_UNIT_DOTS_INCH);  
            //jpegEncodeParam.setXDensity(dpi);  
            //jpegEncodeParam.setYDensity(dpi);  
        
            //new metadata  
            Element tree  =  (Element) imageMetaData.getAsTree("javax_imageio_jpeg_image_1.0");  
            Element jfif  =  (Element)tree.getElementsByTagName("app0JFIF").item(0);  
            jfif.setAttribute("Xdensity", Integer.toString(dpi) );  
            jfif.setAttribute("Ydensity", Integer.toString(dpi));  
               
        }  
        
        
        if(JPEGcompression >= 0 && JPEGcompression <= 1f){  
        
            //old compression  
            //jpegEncodeParam.setQuality(JPEGcompression,false);  
        
            // new Compression  
            JPEGImageWriteParam jpegParams  =  (JPEGImageWriteParam) imageWriter.getDefaultWriteParam();  
            jpegParams.setCompressionMode(JPEGImageWriteParam.MODE_EXPLICIT);  
            jpegParams.setCompressionQuality(JPEGcompression);  
        
        }  
        
        //old write and clean  
        //jpegEncoder.encode(image_to_save, jpegEncodeParam);  
        
        //new Write and clean up  
        imageWriter.write(imageMetaData, new IIOImage(image_to_save, null, null), null);  
        ios.close();  
        imageWriter.dispose();  
        
    }  
}
