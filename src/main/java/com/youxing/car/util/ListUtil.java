package com.youxing.car.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
* @author jianlu
* @version 创建时间：2018年3月20日 下午2:13:30
* @ClassName 类名称
* @Description List工具栏
*/
public class ListUtil {
	
    /**
     * @方法描述：获取两个ArrayList的差集
     * @param firstArrayList 第一个ArrayList
     * @param secondArrayList 第二个ArrayList
     * @return resultList 差集ArrayList
     */
    public static List<Long> receiveDefectList(List<Long> firstArrayList, List<Long> secondArrayList) {
        List<Long> resultList = new ArrayList<Long>();
        LinkedList<Long> result = new LinkedList<Long>(firstArrayList);// 大集合用linkedlist  
        HashSet<Long> othHash = new HashSet<Long>(secondArrayList);// 小集合用hashset  
        Iterator<Long> iter = result.iterator();// 采用Iterator迭代器进行数据的操作  
        while(iter.hasNext()){  
            if(othHash.contains(iter.next())){  
                iter.remove();            
            }     
        }  
        resultList = new ArrayList<Long>(result);
        return resultList;
    }
    
    public static void main(String[] args) {
		List<Long> a = new ArrayList<>();
		a.add(1L);
		a.add(2L);
		a.add(3L);
		
		List<Long> b = new ArrayList<>();
    	b.add(1L);
    	List<Long> receiveDefectList = receiveDefectList(a, b);
    	Iterator<Long> iterator = receiveDefectList.iterator();
    	while (iterator.hasNext()) {
			System.out.println(iterator.next());
		}
    	
    	
	}

}
