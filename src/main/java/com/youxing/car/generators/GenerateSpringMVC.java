package com.youxing.car.generators;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

public class GenerateSpringMVC {

	public static void main(String[] args) {
		// multiple*******************************************************
		
		//String[] tableNames = {"app_address","app_user","app_topic","app_label","app_comment"};
		//String[] entityNames = {"Address","User","Topic","Label","Comment"};
		String[] tableNames = {"yx_bars_lat"};
		String[] entityNames = {"BarsLat"};
				
		String pkgPrefix = "com.youxing.car.";
		String pkgPath = "main\\java\\com\\youxing\\car\\";
		String databaseName = "office_cars";
		// single********************************************************
		String tableName = "";
		String entityName = "";
		String key = "id";
	 
		// ********************************************************
		Connection conn = null;
		// MySQL的JDBC URL编写方式：jdbc:mysql://主机名称：连接端口/数据库的名称?参数=值
		// 避免中文乱码要指定useUnicode和characterEncoding
		// 执行数据库操作之前要在数据库管理系统上创建一个数据库，名字自己定，
		// 下面语句之前就要先创建javademo数据库
		String url = "jdbc:mysql://192.168.1.150:3306/" + databaseName + "?" + "user=root&password=yxcl123456&useUnicode=true&characterEncoding=UTF8";

		try {
			// 之所以要使用下面这条语句，是因为要使用MySQL的驱动，所以我们要把它驱动起来，
			// 可以通过Class.forName把它加载进去，也可以通过初始化来驱动起来，下面三种形式都可以
			Class.forName("com.mysql.jdbc.Driver");// 动态加载mysql驱动
			// or:
			// com.mysql.jdbc.Driver driver = new com.mysql.jdbc.Driver();
			// or：
			// new com.mysql.jdbc.Driver();

			// 一个Connection代表一个数据库连接
			conn = DriverManager.getConnection(url);
			// Statement里面带有很多方法，比如executeUpdate可以实现插入，更新和删除等
			// Statement stmt = conn.createStatement();
			
           for(int j=0;j<tableNames.length;j++){
        	   tableName=tableNames[j];
        	   entityName=entityNames[j];
			PreparedStatement pment = conn.prepareStatement("select * from " + tableName + " where 1=2");
			ResultSet rs = pment.executeQuery();
			ResultSetMetaData metaData = rs.getMetaData();
			int colum = metaData.getColumnCount();
			String[] cols = new String[colum];
			String[] colspara = new String[colum];
			String[] coltypeName = new String[colum];
			for (int i = 1; i <= colum; i++) {
				// 获取列名
				String columName = metaData.getColumnName(i);
				cols[i - 1] = columName.toLowerCase();
				colspara[i - 1] = "#{" + columName.toLowerCase() + "}";
				int type = metaData.getColumnType(i);// 获取每一列的数据类型
				coltypeName[i - 1] = JDBCTypesUtils.getJavaTypeName(type);
			}
			rs.close();
			pment.close();
			
			// -
			mapper(pkgPrefix,entityName,cols,key,tableName,colspara);
			entity(pkgPrefix,entityName, coltypeName,pkgPath,cols);
			service(pkgPrefix,entityName,pkgPath);
			serviceImpl( pkgPrefix,entityName,pkgPath) ;
			dao( pkgPrefix,entityName,pkgPath);
			webcontroller(pkgPrefix,entityName,pkgPath);
			// -
			
           }
           conn.close();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	/**
	 * 生成实体类
	 * 
	 * @param pkgPrefix
	 * @param entityName
	 * @param coltypeName
	 * @param pkgPath
	 * @param cols
	 */
	public static void entity(String pkgPrefix, String entityName, String[] coltypeName, String pkgPath, String[] cols) {
		StringBuffer entity = new StringBuffer();
		entity.append("package " + pkgPrefix + "entity;\n\n");
		entity.append("import java.io.Serializable;\n");
		entity.append("import java.util.Date;\n");
		entity.append("@SuppressWarnings(\"serial\")\n");
		entity.append("public class " + entityName + " implements Serializable{\n");
	
		for (int i = 0; i < cols.length; i++) {
			entity.append("	private " + coltypeName[i] + " " + cols[i].toLowerCase() + ";\n");
		}
		entity.append("\n");
		for (int i = 0; i < cols.length; i++) {
			//-getter
			entity.append("	public " + coltypeName[i] + " get" + StringUtil.capitalise(cols[i].toLowerCase()) + "(){\n");
			entity.append("	    return " + cols[i].toLowerCase() + ";\n");
			entity.append("	}\n");
			//-setter
			entity.append("	public void set"+StringUtil.capitalise(cols[i].toLowerCase()) +"("+ coltypeName[i] + " " + cols[i].toLowerCase() + "){\n");
			entity.append("	    this." + cols[i].toLowerCase() + " = "+cols[i].toLowerCase()+";\n");
			entity.append("	}\n");
		}
		
		entity.append("\n");
		entity.append("}\n");
		String entityDir = System.getProperty("user.dir") + "\\src\\" + pkgPath + "entity\\" + entityName + ".java";
		createFile(entityDir, entity.toString());
		// - create dao ---
	
	}

	/**
	 * 生成Mapper.xml文件
	 * 
	 * @param pkgPrefix
	 * @param entityName
	 * @param cols
	 * @param key
	 * @param tableName
	 * @param colspara
	 */
	public static void mapper(String pkgPrefix, String entityName, String[] cols, String key, String tableName, String[] colspara) {

		String mapperDir = System.getProperty("user.dir") + "\\src\\main\\java\\com\\youxing\\car\\mapping\\" + entityName + "Mapper.xml";
		/* FileUtil.makeDir(strDir); */ 

		StringBuffer mapper = new StringBuffer();

		mapper.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n");
		mapper.append("<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">\n");
		mapper.append("<mapper namespace=\"" + pkgPrefix + "dao." + entityName + "Dao\">\n");
		//************************************
		mapper.append("\t<resultMap type=\"" + pkgPrefix + "entity." + entityName + "\" id=\"" + entityName + "RM\">\n");
		for (int i = 0; i < cols.length; i++) {
			if (cols[i].equalsIgnoreCase(key))
				mapper.append("\t\t<id column=\"" + cols[i].toLowerCase() + "\" property=\"" + cols[i].toLowerCase() + "\" />\n");
			else
				mapper.append("\t\t<result column=\"" + cols[i].toLowerCase() + "\" property=\"" + cols[i].toLowerCase() + "\" />\n");
		}
		mapper.append("\t</resultMap>\n");
		
		//************************************
		mapper.append("\t<!-- _____________________________________________________________________________ 全部字段 -->\n");
		mapper.append("\t<sql id=\"COLUMNS\">\n");
		mapper.append("\t\t" + StringUtil.join(cols, ",") + "\n");
		mapper.append("\t</sql>\n");
		mapper.append("\n");
		//1.add ************************************
		mapper.append("\n");
		mapper.append("\t<!-- _____________________________________________________________________________ 1.add -->\n");
		mapper.append("\n");
		mapper.append("\t<insert id=\"add\" parameterType=\"" + pkgPrefix + "entity." + entityName + "\">\n");
		mapper.append("\t\tinsert into " + tableName + " (\n");
		for (int i = 0; i < cols.length; i++) {
			if (!cols[i].equalsIgnoreCase(key))
				if (i < cols.length - 1)
					mapper.append("\t\t\t<if test=\"" + cols[i].toLowerCase() + " != null and "+cols[i].toLowerCase()+"!=''\">" + cols[i].toLowerCase() + " ,</if>\n");
				else {
					mapper.append("\t\t\t<if test=\"" + cols[i].toLowerCase() + " != null and "+cols[i].toLowerCase()+"!=''\">" + cols[i].toLowerCase() + "</if>\n");
				}
		}
		mapper.append("\t\t)\n");
		mapper.append("\t\tvalues (\n");
		for (int i = 0; i < cols.length; i++) {
			if (!cols[i].equalsIgnoreCase(key))
				if (i < cols.length - 1)
					mapper.append("\t\t\t<if test=\"" + cols[i].toLowerCase() + " != null and "+cols[i].toLowerCase()+"!=''\"> #{" + cols[i].toLowerCase() + "},</if>\n");
				else {
					mapper.append("\t\t\t<if test=\"" + cols[i].toLowerCase() + " != null and "+cols[i].toLowerCase()+"!=''\"> #{" + cols[i].toLowerCase() + "}</if>\n");
				}
		}
		mapper.append("\t\t)\n");
		mapper.append("\t</insert>\n");
		
		//2.modify ************************************
		mapper.append("\n");
		mapper.append("\t<!-- ______________________________________________________________________________ 2.modify -->\n");
		mapper.append("\n");
		mapper.append("\t<update id=\"modify\" parameterType=\"" + pkgPrefix + "entity." + entityName + "\">\n");
		mapper.append("\t\tupdate " + tableName + " \n");
		mapper.append("\t\t<set>\n");
		for (int i = 0; i < cols.length; i++) {
			if (!cols[i].equalsIgnoreCase(key))
				if (i < cols.length - 1)
					mapper.append("\t\t\t<if test=\"" + cols[i].toLowerCase() + " != null and "+cols[i].toLowerCase()+"!=''\">" + cols[i].toLowerCase() + " = #{" + cols[i].toLowerCase() + "},</if>\n");
				else {
					mapper.append("\t\t\t<if test=\"" + cols[i].toLowerCase() + " != null and "+cols[i].toLowerCase()+"!=''\">" + cols[i].toLowerCase() + " = #{" + cols[i].toLowerCase() + "}</if>\n");
				}
		}
		mapper.append("\t\t</set>\n");
		mapper.append("\t\twhere " + key.toLowerCase() + " = #{" + key + "}\n");
		mapper.append("\t</update>\n");
		
		//3.removeById ************************************
		mapper.append("\n");
		mapper.append("\t<!-- _____________________________________________________________________________ 3.removeById -->\n");
		mapper.append("\n");
		mapper.append("\t<delete id=\"removeById\" parameterType=\"long\">\n");
		mapper.append("\t\tdelete from " + tableName + " where " + key.toLowerCase() + "=#{" + key.toLowerCase() + "} \n");
		mapper.append("\t</delete>\n");
		
		//4.removeByIds ************************************
		mapper.append("\n");
		mapper.append("\t<!-- _____________________________________________________________________________ 4.removeByIds -->\n");
		mapper.append("\n");
		mapper.append("\t<delete id=\"removeByIds\" parameterType=\"List\">\n");
		mapper.append("\t\tdelete from " + tableName + " where " + key.toLowerCase() + " in \n");
		mapper.append("\t\t<foreach collection = \"list\" index=\"index\"  item=\"id\" open=\"(\" separator=\",\" close=\")\">\n");
		mapper.append("\t\t\t#{" + key.toLowerCase() + "}\n");
		mapper.append("\t\t</foreach>\n");
		mapper.append("\t</delete>\n");
				
		//5.listAll ************************************
		mapper.append("\n");
		mapper.append("\t<!-- _____________________________________________________________________________ 5.listAll -->\n");
		mapper.append("\n");
		mapper.append("\t<select id=\"listAll\" resultMap=\"" + entityName + "RM\">\n");
		mapper.append("\t\tselect <include refid=\"COLUMNS\" /> from " + tableName + "\n");
		mapper.append("\t</select>\n");
		
		//5.listBy ************************************
		mapper.append("\n");
		mapper.append("\t<!-- _____________________________________________________________________________ 6.listBy -->\n");
		mapper.append("\n");
		mapper.append("\t<select id=\"listBy\" parameterType=\"map\" resultMap=\"" + entityName + "RM\">\n");
		mapper.append("\t\tselect <include refid=\"COLUMNS\" /> from " + tableName + "\n");
		mapper.append("\t\t<where>\n");
		for (int i = 0; i < cols.length; i++) {
			mapper.append("\t\t\t<if test=\"" + cols[i].toLowerCase() + " != null\">and " + cols[i].toLowerCase() + " = #{" + cols[i].toLowerCase() + "}</if>\n");
		}
		mapper.append("\t\t</where>\n");
		mapper.append("\t</select>\n");
		
		//7.pageBy ************************************
		mapper.append("\n");
		mapper.append("\t<!-- _____________________________________________________________________________ 7.pageBy -->\n");
		mapper.append("\n");
		mapper.append("\t<select id=\"pageBy\" parameterType=\"map\" resultMap=\"" + entityName + "RM\">\n");
		mapper.append("\t\tselect <include refid=\"COLUMNS\" /> from " + tableName+"\n");
		mapper.append("\t\t<where>\n");
		for (int i = 0; i < cols.length; i++) {
			mapper.append("\t\t\t<if test=\"" + cols[i].toLowerCase() + " != null\">and " + cols[i].toLowerCase() + " = #{" + cols[i].toLowerCase() + "}</if>\n");
		}
		mapper.append("\t\t</where>\n");		
		mapper.append("\t\torder by " + key.toLowerCase() + " desc\n");
		mapper.append("\t\tlimit #{startIdx},#{limit}\n");
		mapper.append("\t</select>\n");
		
		//8.1.findByMap************************************
		mapper.append("\n");
		mapper.append("\t<!-- _____________________________________________________________________________ 8.1.findByMap -->\n");
		mapper.append("\n");
		mapper.append("\t<select id=\"findByMap\" parameterType=\"map\" resultMap=\"" + entityName + "RM\">\n");
		mapper.append("\t\tselect <include refid=\"COLUMNS\" /> from " + tableName + "\n");
		mapper.append("\t\t<where>\n");
		for (int i = 0; i < cols.length; i++) {
			mapper.append("\t\t\t<if test=\"" + cols[i].toLowerCase() + " != null\">and " + cols[i].toLowerCase() + " = #{" + cols[i].toLowerCase() + "}</if>\n");
		}
		mapper.append("\t\t</where>\n");	
		mapper.append("\t</select>\n");
		
		//8.2.findBy************************************
		mapper.append("\n");
		mapper.append("\t<!-- _____________________________________________________________________________ 8.2.findBy -->\n");
		mapper.append("\n");
		mapper.append("\t<select id=\"findBy\" parameterType=\""+pkgPrefix+"entity."+entityName+"\" resultMap=\"" + entityName + "RM\">\n");
		mapper.append("\t\tselect <include refid=\"COLUMNS\" /> from " + tableName + "\n");
		mapper.append("\t\t<where>\n");
		for (int i = 0; i < cols.length; i++) {
			mapper.append("\t\t\t<if test=\"" + cols[i].toLowerCase() + " != null\">and " + cols[i].toLowerCase() + " = #{" + cols[i].toLowerCase() + "}</if>\n");
		}
		mapper.append("\t\t</where>\n");	
		mapper.append("\t</select>\n");
		
		//9.findById************************************
		mapper.append("\n");
		mapper.append("\t<!-- _____________________________________________________________________________ 9.findById -->\n");
		mapper.append("\n");
		mapper.append("\t<select id=\"findById\" parameterType=\"long\" resultMap=\"" + entityName + "RM\">\n");
		mapper.append("\t\tselect <include refid=\"COLUMNS\" /> from " + tableName + " where " + key.toLowerCase() + " = #{" + key + "}\n");
		mapper.append("\t</select>\n");
		
		//10.countAll ************************************
		mapper.append("\n");
		mapper.append("\t<!-- _____________________________________________________________________________ 10.countAll -->\n");
		mapper.append("\n");
		mapper.append("\t<select id=\"countAll\" resultType=\"integer\">\n");
		mapper.append("\t\tselect count(" + key + ") from " + tableName + "\n");
		mapper.append("\t</select>\n");
		
		//11.countBy ************************************
		mapper.append("\n");
	    mapper.append("\t<!-- _____________________________________________________________________________ 11.countBy -->\n");
		mapper.append("\n");
		mapper.append("\t<select id=\"countBy\" parameterType=\"map\" resultType=\"integer\">\n");
		mapper.append("\t\tselect count(" + key + ") from " + tableName + "\n");
		mapper.append("\t\t<where>\n");
		for (int i = 0; i < cols.length; i++) {
			mapper.append("\t\t\t<if test=\"" + cols[i].toLowerCase() + " != null\">and " + cols[i].toLowerCase() + " = #{" + cols[i].toLowerCase() + "}</if>\n");
		}
		mapper.append("\t\t</where>\n");	
		mapper.append("\t</select>\n");
		mapper.append("\t<!-- ******************扩展Mapper****************** -->\n");
		//************************************
		mapper.append("</mapper>\n");
		createFile(mapperDir, mapper.toString());
	}

	/**生成dao数据访问类
	 * @param pkgPrefix
	 * @param entityName
	 * @param pkgPath
	 */
	public static void dao(String pkgPrefix, String entityName, String pkgPath) {
		StringBuffer dao = new StringBuffer();
		dao.append("package " + pkgPrefix + "dao;\n");
		dao.append("import " + pkgPrefix + "dao.base.BaseDao;\n");
		dao.append("import " + pkgPrefix + "entity." + entityName + ";\n");
		dao.append("public interface " + entityName + "Dao extends BaseDao<" + entityName + ">{\n");
		dao.append("}\n");
		String daoDir = System.getProperty("user.dir") + "\\src\\" + pkgPath + "dao\\" + entityName + "Dao.java";
		createFile(daoDir, dao.toString());
	}

	/**生成serviceImpl接口实现类
	 * @param pkgPrefix
	 * @param entityName
	 * @param pkgPath
	 */
	public static void serviceImpl(String pkgPrefix, String entityName, String pkgPath) {
		StringBuffer serviceimpl = new StringBuffer();
		serviceimpl.append("package " + pkgPrefix + "service.impl;\n");
		serviceimpl.append("import javax.annotation.Resource;\n");
	
		serviceimpl.append("import org.springframework.stereotype.Service;\n");
		serviceimpl.append("import " + pkgPrefix + "dao." + entityName + "Dao;\n");
		serviceimpl.append("import " + pkgPrefix + "dao.base.BaseDao;\n");
		serviceimpl.append("import " + pkgPrefix + "entity." + entityName + ";\n");
		serviceimpl.append("import " + pkgPrefix + "service." + entityName + "Service;\n");
		serviceimpl.append("import " + pkgPrefix + "service.base.impl.BaseServiceImpl;\n");
		serviceimpl.append("@Service\n");
		serviceimpl.append("public class " + entityName + "ServiceImpl extends BaseServiceImpl<" + entityName + "> implements " + entityName + "Service{\n");
		serviceimpl.append("	@Resource\n");
		serviceimpl.append("	private " + entityName + "Dao " + entityName.toLowerCase() + "Dao;	\n");
		serviceimpl.append("	public BaseDao<" + entityName + "> getBaseDao() {\n");
		serviceimpl.append("		return this." + entityName.toLowerCase() + "Dao;\n");
		serviceimpl.append("	}\n");
		serviceimpl.append("}\n");
		String serviceimplDir = System.getProperty("user.dir") + "\\src\\" + pkgPath + "service\\impl\\" + entityName + "ServiceImpl.java";
		createFile(serviceimplDir, serviceimpl.toString());
	}

	/**生成service接口
	 * @param pkgPrefix
	 * @param entityName
	 * @param pkgPath
	 */
	public static void service(String pkgPrefix, String entityName, String pkgPath) {

		StringBuffer service = new StringBuffer();
		service.append("package " + pkgPrefix + "service;\n");
		service.append("import " + pkgPrefix + "entity." + entityName + ";\n");
		service.append("import " + pkgPrefix + "service.base.BaseService;\n");
		service.append("public interface " + entityName + "Service extends BaseService<" + entityName + ">{\n");
		service.append("\n");
		service.append("\n");
		service.append("}\n");

		String serviceDir = System.getProperty("user.dir") + "\\src\\" + pkgPath + "service\\" + entityName + "Service.java";
		createFile(serviceDir, service.toString());
	}

	/**生成controller控制器
	 * @param pkgPrefix
	 * @param entityName
	 * @param pkgPath
	 */
	public static void webcontroller(String pkgPrefix, String entityName, String pkgPath) {
		StringBuffer controller = new StringBuffer();
		controller.append("package " + pkgPrefix + "controller;\n\n");

		controller.append("import org.springframework.stereotype.Controller;\n");
		controller.append("import org.springframework.web.bind.annotation.RequestMapping;\n");
		controller.append("import javax.annotation.Resource;\n\n");

		controller.append("import " + pkgPrefix + "service." + entityName + "Service;\n");
		controller.append("@Controller\n");
		controller.append("public class " + entityName + "Controller {\n");
		controller.append("         @Resource\n");
		controller.append("         private " + entityName + "Service " + entityName.toLowerCase() + "Service;\n\n");
		controller.append("}\n");
		String controllerDir = System.getProperty("user.dir") + "\\src\\" + pkgPath + "controller\\" + entityName + "Controller.java";
		createFile(controllerDir, controller.toString());
	}

	/**生成app 端controller控制器
	 * @param pkgPrefix
	 * @param entityName
	 * @param pkgPath
	 */
	public static void appcontroller(String pkgPrefix, String entityName, String pkgPath) {
//		StringBuffer controller = new StringBuffer();
//		controller.append("package " + pkgPrefix + "controller;\n\n");
//
//		controller.append("import org.springframework.stereotype.Controller;\n");
//		controller.append("import org.springframework.web.bind.annotation.RequestMapping;\n");
//		controller.append("import javax.annotation.Resource;\n\n");
//
//		controller.append("import " + pkgPrefix + "service." + entityName + "Service;\n");
//		controller.append("@Controller\n");
//		controller.append("@RequestMapping(\"app\")\n");
//		controller.append("public class " + entityName + "Controller {\n");
//		controller.append("         @Resource\n");
//		controller.append("         private " + entityName + "Service " + entityName.toLowerCase() + "Service;\n\n");
//		controller.append("}\n");
//		String controllerDir = System.getProperty("user.dir") + "\\src\\" + pkgPath + "controller\\app\\" + entityName + "AppController.java";
//		createFile(controllerDir, controller.toString());
	}
	
	public static void createFile(String path, String content) {
		FileOutputStream fos = null;
		BufferedOutputStream bos = null;
		try {
			fos = new FileOutputStream(new File(path));
			bos = new BufferedOutputStream(fos);
			bos.write(content.getBytes("UTF-8"));
			bos.flush();
			bos.close();
			fos.flush();
			fos.close();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

}
