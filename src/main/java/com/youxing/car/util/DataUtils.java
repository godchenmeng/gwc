package com.youxing.car.util;

import java.text.ParseException;
import java.util.Date;

import org.apache.commons.lang.time.DateFormatUtils;

import com.youxing.car.entity.CarNotice;
import com.youxing.car.entity.Gps;

public class DataUtils {
    /**
     *
     * @author mars
     * @date 2017年4月27日 上午10:56:05
     * @Description: TODO(车辆gps信息)
     * @param @param str
     * @param @return
     * @param @throws ParseException 设定文件
     * @return Gps 返回类型
     * @throws
     */
    public static Gps getGps(String[] str) throws ParseException {
        Gps gps = new Gps();
        gps.setAlm_id(str[0]);
        gps.setDevice(str[1]);
        gps.setSerial(str[2]);
        gps.setObd_type(str[3]);
        gps.setData_flow(str[4]);
        gps.setDianya(Double.valueOf(str[5]));
        gps.setMileage_type(str[6]);
        gps.setMileage(Long.valueOf(str[7]));
        gps.setFuel(Long.valueOf(str[8]));
        gps.setEngine_speed(Integer.valueOf(str[9]));
        gps.setCar_speed(Integer.valueOf(str[10]));
        gps.setSky_flow(Double.valueOf(str[11]));
        gps.setInlet(Integer.valueOf(str[12]));
        gps.setInlet_pass(Integer.valueOf(str[13]));
        gps.setFault_status(str[14]);
        gps.setEngine_fault(Integer.valueOf(str[15]));
        gps.setCoolant(Integer.valueOf(str[16]));
        gps.setCar_tempu(Integer.valueOf(str[17]));
        gps.setOil_pass(Integer.valueOf(str[18]));
        gps.setAir_pass(Integer.valueOf(str[19]));
        gps.setAir_sensor(Double.valueOf(str[20]));
        gps.setGas_position(Double.valueOf(str[21]));
        gps.setEngine_time(Integer.valueOf(str[22]));
        gps.setFault_mil(Long.valueOf(str[23]));
        gps.setInnage(str[24]);
        gps.setEngine_load(Integer.valueOf(str[25]));
        gps.setLong_fuel(Double.valueOf(str[26]));
        gps.setAngle_engine(Double.valueOf(str[27]));
        gps.setCar_mileage(Long.valueOf(str[28]));
        gps.setCar_time(Long.valueOf(str[29]));
        gps.setMean1(Integer.valueOf(str[30]));
        gps.setMean2(Integer.valueOf(str[31]));
        gps.setMean3(Integer.valueOf(str[32]));
        gps.setMean4(Integer.valueOf(str[33]));
        gps.setTotal_max(Integer.valueOf(str[34]));
        gps.setSenddate(DateUtils.parseDate(str[35]));
        gps.setCreatedate(new Date());
        gps.setGps_status(str[36]);
        gps.setLatitude(Double.valueOf(str[37]));
        gps.setLongitude(Double.valueOf(str[38]));
        gps.setAltitude(Double.valueOf(str[39]));
        gps.setSatellites(Integer.valueOf(str[40]));
        gps.setGps_speed(Double.valueOf(str[41]));
        gps.setDirection(Double.valueOf(str[42]));
        gps.setPdop(Double.valueOf(str[43]));
        gps.setAcc(str[44]);
        if(str.length > 45) {
            gps.setTurn_state(str[45]);
            if(str.length > 46) gps.setFuel_level(Double.valueOf(str[46]));
            if(str.length > 47) gps.setWater_level(Double.valueOf(str[47]));
        }
        return gps;
    }

    /**
     *
     * @author mars
     * @date 2017年4月27日 上午11:38:55
     * @Description: TODO(车辆报警数据)
     * @param @param str
     * @param @return
     * @param @throws ParseException 设定文件
     * @return CarNotice 返回类型
     * @throws
     */
    public static CarNotice getCarNotice(String[] str) throws ParseException {
        CarNotice car = new CarNotice();
        car.setAlm_id(str[0]);
        car.setDevice(str[1]);
        car.setSerial(str[2]);
        car.setObd_type(str[3]);
        car.setData_flow(str[4]);
        car.setDianya(Double.valueOf(str[5]));
        car.setMileage_type(str[6]);
        car.setMileage(Long.valueOf(str[7]));
        car.setFuel(Long.valueOf(str[8]));
        car.setEngine_speed(Integer.valueOf(str[9]));
        car.setCar_speed(Integer.valueOf(str[10]));
        car.setSky_flow(Double.valueOf(str[11]));
        car.setInlet(Integer.valueOf(str[12]));
        car.setInlet_pass(Integer.valueOf(str[13]));
        car.setFault_status(str[14]);
        car.setEngine_fault(Integer.valueOf(str[15]));
        car.setCoolant(Integer.valueOf(str[16]));
        car.setCar_tempu(Integer.valueOf(str[17]));
        car.setOil_pass(Integer.valueOf(str[18]));
        car.setAir_pass(Integer.valueOf(str[19]));
        car.setAir_sensor(Double.valueOf(str[20]));
        car.setGas_position(Double.valueOf(str[21]));
        car.setEngine_time(Integer.valueOf(str[22]));
        car.setFault_mil(Long.valueOf(str[23]));
        car.setInnage(str[24]);
        car.setEngine_load(Integer.valueOf(str[25]));
        car.setLong_fuel(Double.valueOf(str[26]));
        car.setAngle_engine(Double.valueOf(str[27]));
        car.setCar_mileage(Long.valueOf(str[28]));
        car.setCar_time(Long.valueOf(str[29]));
        car.setMean1(Integer.valueOf(str[30]));
        car.setMean2(Integer.valueOf(str[31]));
        car.setMean3(Integer.valueOf(str[32]));
        car.setMean4(Integer.valueOf(str[33]));
        car.setTotal_max(Integer.valueOf(str[34]));
        car.setSenddate(str[35]);
        car.setGps_status(str[36]);
        car.setLatitude(Double.valueOf(str[37]));
        car.setLongitude(Double.valueOf(str[38]));
        car.setAltitude(Double.valueOf(str[39]));
        car.setSatellites(Integer.valueOf(str[40]));
        car.setGps_speed(Double.valueOf(str[41]));
        car.setDirection(Double.valueOf(str[42]));
        car.setPdop(Double.valueOf(str[43]));
        car.setCreatedate(new Date());
        car.setAcc(str[44]);

        return car;
    }

    /**
     * gps对象转string，方便redis存储
     * @param gps
     * @return
     */
    public static String gpsToString(Gps gps) {
        String result = "[";
        result += gps.getAlm_id() + "," + gps.getDevice() + "," + gps.getSerial() + "," + gps.getObd_type() + "," + gps.getData_flow() + "," +
                gps.getDianya() + "," + gps.getMileage_type() + "," + gps.getMileage() + "," + gps.getFuel() + "," + gps.getEngine_speed() + "," +
                gps.getCar_speed() + "," + gps.getSky_flow() + "," + gps.getInlet() + "," + gps.getInlet_pass() + "," + gps.getFault_status() + "," +
                gps.getEngine_fault() + "," + gps.getCoolant() + "," + gps.getCar_tempu() + "," + gps.getOil_pass() + "," + gps.getAir_pass() + "," +
                gps.getAir_sensor() + "," + gps.getGas_position() + "," + gps.getEngine_time() + "," + gps.getFault_mil() + "," + gps.getInnage() + "," +
                gps.getEngine_load() + "," + gps.getLong_fuel() + "," + gps.getAngle_engine() + "," + gps.getCar_mileage() + "," + gps.getCar_time() + "," +
                gps.getMean1() + "," + gps.getMean2() + "," + gps.getMean3() + "," + gps.getMean4() + "," + gps.getTotal_max() + "," + DateFormatUtils.format(gps.getSenddate(), "yyyy-MM-dd hh:mm:ss") + "," +
                gps.getGps_status() + "," + gps.getLatitude() + "," + gps.getLongitude() + "," + gps.getAltitude() + "," + gps.getSatellites() + "," + gps.getGps_speed() + "," +
                gps.getDirection() + "," + gps.getPdop() + "," + gps.getAcc() + "," + gps.getTurn_state() + "," + gps.getFuel_level() + "," + gps.getWater_level();
        result += "]";
        return  result;
    }
}
