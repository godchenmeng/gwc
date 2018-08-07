package com.youxing.car.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.youxing.car.entity.Car;

public class CarXlsxReader {
    public List<Car> read(File file) throws InvalidFormatException, IOException {  
    	XSSFWorkbook workbook = new XSSFWorkbook(file);  
    	XSSFSheet sheet = workbook.getSheetAt(0);  
          
        List<Car> result = new ArrayList<Car>();  
          
        int rowStart = sheet.getFirstRowNum() + 1;  
        int rowEnd = sheet.getLastRowNum();  
          
        for(int i = rowStart; i <= rowEnd; i++) {  
        	XSSFRow row = sheet.getRow(i);        
            Car car = this.getUserFromRow(row);             
            if(car != null) {
            	result.add(car);
            }else{
            	return null;
            }  
        }  
        workbook.close();  
        return result;  
    }  
      
    protected Car getUserFromRow(XSSFRow row) {  
        if(row == null) return null;  
        int current = row.getFirstCellNum();
        XSSFCell cell = row.getCell(current);  
        if(null != cell) {  
            Car car = new Car();
            car.setCar_no(cell.getStringCellValue());  
            current++;  
              
            cell = row.getCell(current);  
            if(null != cell){
                car.setBrand(cell.getStringCellValue());  
                current++; 
            }else{
            	return null;
            } 
              
            cell = row.getCell(current);  
            if(null != cell){
                car.setIntime(cell.getStringCellValue());  
                current++; 
            }else{
            	return null;
            }
            
            cell = row.getCell(current);
            if(null != cell){
                car.setDriver_no(cell.getStringCellValue());  
                current++; 
            }else{
            	return null;
            }
            
            cell = row.getCell(current);
            if(null != cell){
                car.setVin(cell.getStringCellValue());
            }else{
            	return null;
            }
              
            return car;  
        } else {
        	return null;
        }
    } 
}
