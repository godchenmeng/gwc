package com.youxing.car.util;

/**
 * @author mars
 * @date 2017年3月24日 上午9:47:51
 */
public class Constant {
	// session中属性的key
	public final static String SESSION_KEY = "yx_user";
	public final static String IMAGE_STORE_CAR = "/home/upload/car";
	public final static String IMAGE_STORE_USER = "/home/upload/user";

	// 数据状态
	public final static String ADD_STATUS = "1";
	public final static String REMOVE_STATUS = "2";

	// 汽车是否绑定设备
	public final static String BIND_OBD = "1";
	public final static String UNBIND_OBD = "0";

	// 汽车审核状态和报废
	public final static String CHECK_STATUS = "1";
	public final static String UN_CHECK_STATUS = "2";
	// 驾驶员工作状态-1-在岗 2 公休 3 长期事假 4 长期病假 5待岗
	public final static String DRIVER_STATE_1 = "1";
	// 用车申请提交
	public final static String APPLY_1 = "1";
	// 用车申请审批通过 调度通过
	public final static String APPLY_2 = "2";
	// 用车申请审批驳回 调度驳回
	public final static String APPLY_3 = "3";
	
	public final static String CAR_TYPE_JSON="["
			+ "{\"id\":\"101\",\"text\":\"阿尔法罗密欧\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
			+ "{\"id\":\"102\",\"text\":\"阿斯顿·马丁\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
			+ "{\"id\":\"103\",\"text\":\"安凯客车\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
			+ "{\"id\":\"104\",\"text\":\"奥迪\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
			+ "{\"id\":\"105\",\"text\":\"巴博斯\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
			+ "{\"id\":\"106\",\"text\":\"霸凌\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
			+ "{\"id\":\"107\",\"text\":\"宝骏\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
			+ "{\"id\":\"108\",\"text\":\"宝马\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
			+ "{\"id\":\"109\",\"text\":\"保时捷\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
			+ "{\"id\":\"110\",\"text\":\"北京\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
			+ "{\"id\":\"111\",\"text\":\"北汽幻速\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
			+ "{\"id\":\"112\",\"text\":\"北汽绅宝\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
			+ "{\"id\":\"113\",\"text\":\"北汽威旺\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
			+ "{\"id\":\"114\",\"text\":\"北汽新能源\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
			+ "{\"id\":\"115\",\"text\":\"北汽制造\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
			+ "{\"id\":\"116\",\"text\":\"奔驰\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
			+ "{\"id\":\"117\",\"text\":\"奔腾\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
			+ "{\"id\":\"118\",\"text\":\"本田\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
			+ "{\"id\":\"119\",\"text\":\"比亚迪\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
			+ "{\"id\":\"120\",\"text\":\"标致\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
			+ "{\"id\":\"121\",\"text\":\"别克\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
			+ "{\"id\":\"122\",\"text\":\"宾利\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
			+ "{\"id\":\"123\",\"text\":\"布加迪\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
			+ "{\"id\":\"124\",\"text\":\"昌河\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
			+ "{\"id\":\"125\",\"text\":\"成功汽车\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
			+ "{\"id\":\"126\",\"text\":\"楚胜\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
			+ "{\"id\":\"127\",\"text\":\"大发\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
			+ "{\"id\":\"128\",\"text\":\"大众\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
			+ "{\"id\":\"129\",\"text\":\"道奇\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
			+ "{\"id\":\"130\",\"text\":\"东风\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
				+ "{\"id\":\"131\",\"text\":\"东风风度\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
				+ "{\"id\":\"132\",\"text\":\"东风风行\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
				+ "{\"id\":\"133\",\"text\":\"东风风神\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
				+ "{\"id\":\"134\",\"text\":\"东风小康\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
				+ "{\"id\":\"135\",\"text\":\"东南\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
				+ "{\"id\":\"136\",\"text\":\"东岳\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
				+ "{\"id\":\"137\",\"text\":\"DS\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
				+ "{\"id\":\"138\",\"text\":\"法拉利\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
				+ "{\"id\":\"139\",\"text\":\"菲亚特\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
				+ "{\"id\":\"140\",\"text\":\"丰田\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
				+ "{\"id\":\"141\",\"text\":\"福迪\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
				+ "{\"id\":\"142\",\"text\":\"福汽启腾\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
				+ "{\"id\":\"143\",\"text\":\"福狮\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
				+ "{\"id\":\"144\",\"text\":\"福特\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
				+ "{\"id\":\"145\",\"text\":\"福田\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
				+ "{\"id\":\"146\",\"text\":\"观致\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
				+ "{\"id\":\"147\",\"text\":\"光冈\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
				+ "{\"id\":\"148\",\"text\":\"广汽传祺\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
				+ "{\"id\":\"149\",\"text\":\"广汽吉奥\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
				+ "{\"id\":\"150\",\"text\":\"GMC\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
			+ "{\"id\":\"151\",\"text\":\"哈飞\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
			+ "{\"id\":\"152\",\"text\":\"哈佛\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
			+ "{\"id\":\"153\",\"text\":\"海格\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
			+ "{\"id\":\"154\",\"text\":\"海马\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
			+ "{\"id\":\"156\",\"text\":\"悍马\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
			+ "{\"id\":\"157\",\"text\":\"豪沃\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
			+ "{\"id\":\"158\",\"text\":\"恒天\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
			+ "{\"id\":\"159\",\"text\":\"红旗\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
			+ "{\"id\":\"160\",\"text\":\"红岩\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
				+ "{\"id\":\"161\",\"text\":\"华驰\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
				+ "{\"id\":\"162\",\"text\":\"华利\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
				+ "{\"id\":\"163\",\"text\":\"华普\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
				+ "{\"id\":\"164\",\"text\":\"华颂\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
				+ "{\"id\":\"165\",\"text\":\"华泰\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
				+ "{\"id\":\"166\",\"text\":\"华威驰乐\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
				+ "{\"id\":\"167\",\"text\":\"黄海\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
				+ "{\"id\":\"168\",\"text\":\"吉利汽车\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
				+ "{\"id\":\"169\",\"text\":\"江淮\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
				+ "{\"id\":\"170\",\"text\":\"江铃\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
				+ "{\"id\":\"171\",\"text\":\"江铃集团轻汽\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
				+ "{\"id\":\"172\",\"text\":\"捷豹\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
				+ "{\"id\":\"173\",\"text\":\"解放\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
			+ "{\"id\":\"174\",\"text\":\"金杯\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
			+ "{\"id\":\"175\",\"text\":\"金龙\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
			+ "{\"id\":\"176\",\"text\":\"金旅\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
				+ "{\"id\":\"177\",\"text\":\"九龙\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
				+ "{\"id\":\"178\",\"text\":\"Jeep\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
				+ "{\"id\":\"179\",\"text\":\"卡尔森\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
				+ "{\"id\":\"180\",\"text\":\"卡升\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
				+ "{\"id\":\"181\",\"text\":\"卡威\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
				+ "{\"id\":\"182\",\"text\":\"开瑞\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
				+ "{\"id\":\"183\",\"text\":\"凯迪拉克\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
				+ "{\"id\":\"184\",\"text\":\"凯翼\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
				+ "{\"id\":\"185\",\"text\":\"康迪\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
				+ "{\"id\":\"186\",\"text\":\"科尼塞克\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
				+ "{\"id\":\"187\",\"text\":\"克莱斯勒\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
				+ "{\"id\":\"188\",\"text\":\"KTM\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
				+ "{\"id\":\"189\",\"text\":\"兰博基尼\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
				+ "{\"id\":\"190\",\"text\":\"劳伦士\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
				+ "{\"id\":\"191\",\"text\":\"劳斯莱斯\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
				+ "{\"id\":\"192\",\"text\":\"雷丁\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
				+ "{\"id\":\"193\",\"text\":\"雷克萨斯\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
				+ "{\"id\":\"194\",\"text\":\"雷诺\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
				+ "{\"id\":\"195\",\"text\":\"理念\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
				+ "{\"id\":\"196\",\"text\":\"力帆\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
				+ "{\"id\":\"197\",\"text\":\"利达\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
				+ "{\"id\":\"198\",\"text\":\"莲花汽车\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
				+ "{\"id\":\"199\",\"text\":\"猎豹汽车\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
				+ "{\"id\":\"200\",\"text\":\"林肯\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
			+ "{\"id\":\"201\",\"text\":\"铃木\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
			+ "{\"id\":\"202\",\"text\":\"凌宇\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
			+ "{\"id\":\"203\",\"text\":\"柳工\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
			+ "{\"id\":\"204\",\"text\":\"陆霸\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
			+ "{\"id\":\"205\",\"text\":\"陆地方舟\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
			+ "{\"id\":\"206\",\"text\":\"陆风\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
			+ "{\"id\":\"207\",\"text\":\"路虎\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
			+ "{\"id\":\"208\",\"text\":\"路特斯\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
			+ "{\"id\":\"209\",\"text\":\"LOCAL MOTORS\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
			+ "{\"id\":\"210\",\"text\":\"马自达\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
			+ "{\"id\":\"211\",\"text\":\"玛莎拉蒂\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
			+ "{\"id\":\"212\",\"text\":\"迈巴赫\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
			+ "{\"id\":\"213\",\"text\":\"迈凯伦\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
			+ "{\"id\":\"214\",\"text\":\"摩根\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
			+ "{\"id\":\"215\",\"text\":\"MG\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
			+ "{\"id\":\"216\",\"text\":\"MINI\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
			+ "{\"id\":\"217\",\"text\":\"纳智捷\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
				+ "{\"id\":\"218\",\"text\":\"南京金龙\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
				+ "{\"id\":\"219\",\"text\":\"讴歌\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
				+ "{\"id\":\"220\",\"text\":\"欧宝\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
				+ "{\"id\":\"221\",\"text\":\"欧朗\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
				+ "{\"id\":\"222\",\"text\":\"欧曼\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
				+ "{\"id\":\"280\",\"text\":\"奇瑞\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
				+ "{\"id\":\"223\",\"text\":\"启辰\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
				+ "{\"id\":\"224\",\"text\":\"起亚\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
				+ "{\"id\":\"225\",\"text\":\"前途\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
				+ "{\"id\":\"226\",\"text\":\"日产\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
				+ "{\"id\":\"227\",\"text\":\"荣威\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
				+ "{\"id\":\"228\",\"text\":\"如虎\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
				+ "{\"id\":\"229\",\"text\":\"瑞琪\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
				+ "{\"id\":\"230\",\"text\":\"萨博\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
				+ "{\"id\":\"231\",\"text\":\"三菱\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
				+ "{\"id\":\"232\",\"text\":\"三一\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
				+ "{\"id\":\"233\",\"text\":\"陕汽通家\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
				+ "{\"id\":\"234\",\"text\":\"上汽大通\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
				+ "{\"id\":\"235\",\"text\":\"世爵\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
				+ "{\"id\":\"236\",\"text\":\"双环\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
				+ "{\"id\":\"237\",\"text\":\"双龙\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
				+ "{\"id\":\"238\",\"text\":\"思铭\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
				+ "{\"id\":\"239\",\"text\":\"斯巴鲁\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
				+ "{\"id\":\"240\",\"text\":\"斯达-斯太尔\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
				+ "{\"id\":\"241\",\"text\":\"斯达泰克\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
				+ "{\"id\":\"242\",\"text\":\"斯柯达\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
				+ "{\"id\":\"243\",\"text\":\"smart\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
				+ "{\"id\":\"244\",\"text\":\"泰卡特\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
				+ "{\"id\":\"245\",\"text\":\"特斯拉\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
				+ "{\"id\":\"247\",\"text\":\"威麟\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
				+ "{\"id\":\"248\",\"text\":\"威兹曼\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
				+ "{\"id\":\"249\",\"text\":\"潍柴英致\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
				+ "{\"id\":\"250\",\"text\":\"沃尔沃\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
				+ "{\"id\":\"251\",\"text\":\"五菱汽车\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
				+ "{\"id\":\"252\",\"text\":\"五十铃\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
			+ "{\"id\":\"253\",\"text\":\"西雅特\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
			+ "{\"id\":\"254\",\"text\":\"仙达\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
			+ "{\"id\":\"255\",\"text\":\"现代\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
			+ "{\"id\":\"256\",\"text\":\"新凯\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
			+ "{\"id\":\"257\",\"text\":\"星马\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
			+ "{\"id\":\"258\",\"text\":\"雪佛兰\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
			+ "{\"id\":\"259\",\"text\":\"雪铁龙\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
			+ "{\"id\":\"260\",\"text\":\"亚特重工\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
			+ "{\"id\":\"261\",\"text\":\"野马汽车\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
			+ "{\"id\":\"269\",\"text\":\"一汽\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
			+ "{\"id\":\"270\",\"text\":\"依维柯\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
			+ "{\"id\":\"271\",\"text\":\"银盾\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
			+ "{\"id\":\"272\",\"text\":\"英菲尼迪\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
			+ "{\"id\":\"273\",\"text\":\"永源\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
			+ "{\"id\":\"274\",\"text\":\"长安\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
			+ "{\"id\":\"275\",\"text\":\"长安商用\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
			+ "{\"id\":\"276\",\"text\":\"长城\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
			+ "{\"id\":\"277\",\"text\":\"知豆\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
			+ "{\"id\":\"278\",\"text\":\"中华\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
			+ "{\"id\":\"262\",\"text\":\"中集\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
			+ "{\"id\":\"263\",\"text\":\"中联\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
			+ "{\"id\":\"264\",\"text\":\"中兴\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
			+ "{\"id\":\"265\",\"text\":\"众泰\",\"leaf\":false,\"type\":0,\"nodeType\":0},"
			+ "{\"id\":\"279\",\"text\":\"其他\",\"leaf\":false,\"type\":0,\"nodeType\":0}]";;

	//调度任务状态 1-未签收 2 签收 3执行中  5 已执行 6已归队
	public final static String QS="2";
	public final static String KS="3";
	public final static String JS="5";
	public final static String GD="6";
	public final static String ZF="7";
	

	public final static String NOTICE_TYPE_SP="1";
	public final static String NOTICE_TYPE_DD="2";
	
	public final static String PASS="1";

	public final static String GET_OVERTIME="请求超时，已断开连接！";
	public final static String GET_EXCEPTION="代码请求异常";
	public final static String GET_INTERRUPT="请求断开连接";


	public final static String EXPORT_FILE_TYPE=".xls";

	//汽车熄火点火状态
	public final static String IGNITION = "0001";
	public final static String FLAMEOUT = "0002";

	//百度地图绘制类型
	public final static String BMAP_DRAWING_CIRCLE = "circle"; //画圆

	//电子围栏类型
	public final static String BARS_RULE_TYPE_1 = "1";//驶入
	public final static String BARS_RULE_TYPE_2 = "2";//驶出
	public final static String BARS_RULE_TYPE_3 = "3";//驶入或驶出

	//百度地图栅栏监控状态
	public final static String MONITORED_STATUS_IN = "in";//在栅栏内
	public final static String MONITORED_STATUS_OUT = "out";//在栅栏外
	public final static String MONITORED_STATUS_UNKNOWN = "unknown";//未知

	//Redis存储KEY
	public final static String REDIS_LOGIN_CODE = "LOGIN.CODE";//登录验证码
	public final static String REDIS_LOGIN_APP_INFO = "LOGIN.APP.INFO";//APP登录信息
	public final static String REDIS_LOGIN_WEB_INFO = "LOGIN.WEB.INFO";//WEB登录信息
}
