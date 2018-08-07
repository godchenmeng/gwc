package com.youxing.car.util;

import net.sf.jsqlparser.schema.Server;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigUtils {
    public static Properties getConfig(){
        Properties p = new Properties();
        InputStream config = null;
        try {
            config = Server.class.getResourceAsStream("/config.properties");
            p.load(config);
            return p;
        }catch (Exception ex){
            ex.printStackTrace();
        }finally {
            if(null != config){
                try{
                    config.close();
                }catch (IOException ioe){
                    ioe.printStackTrace();
                }
            }
        }
        return null;
    }
}
