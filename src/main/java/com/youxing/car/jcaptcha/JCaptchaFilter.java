package com.youxing.car.jcaptcha;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * 用于生成验证码图片的过滤器。  
 * CaptchaService使用当前会话ID当作key获取相应的验证码图片；另外需要设置响应内容不进行浏览缓存
 * @author Administrator
 *
 */
public class JCaptchaFilter extends HttpServlet {

    /** 
	* @Fields serialVersionUID : 
	*/ 
	private static final long serialVersionUID = 1L;
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {   
    	res.setDateHeader("Expires", 0L);  
	    res.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");  
	    res.addHeader("Cache-Control", "post-check=0, pre-check=0");  
	    res.setHeader("Pragma", "no-cache");  
	    res.setContentType("image/jpeg");  
        String id = req.getRequestedSessionId();  
        BufferedImage bi = JCaptcha.captchaService.getImageChallengeForID(id); 
        ServletOutputStream out = res.getOutputStream();  
        ImageIO.write(bi, "jpg", out);  
        try {  
            out.flush();  
        } finally {  
            out.close();  
        }   
	} 

}
