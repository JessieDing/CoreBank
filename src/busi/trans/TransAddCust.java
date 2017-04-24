package busi.trans;

import busi.BankTrans;
import busi.doSql.Custinfo;
import busi.validate.DataValidate;
import db.ConnectMySql;

public class TransAddCust extends BankTrans {
	private String id_type;// 证件类型
	private String id_no;// 证件号码
	private String cust_name;// 客户姓名
	private String cust_tel;// 电话号码
	private String cust_addr;// 地址
	private ConnectMySql dbhelper;

	public TransAddCust() {
		id_type = "";
		id_no = "";
		cust_name = "";
		cust_tel = "";
		cust_addr = "";
		setTrans_code("001");
		setTrans_name("新建客户信息");
	}

	public void prtPrompt() {
		System.out.println("》》》建立客户信息《《《");
		System.out.println("请输入客户信息，格式如下:");
		System.out.println("@id_type @id_no @cust_name @cust_tel @cust_addr");
	}

	/**
	 * 获取输入数据
	 */
	public int getInPut() {
		id_type = scn.next();// 证件类型
		if (id_type == null) {
			setTrans_result("获取证件类型错误，请检查");
			return -1;
		}
		id_no = scn.next();// 证件号码
		if (id_no == null) {
			setTrans_result("获取证件号码错误，请检查");
			return -1;
		}
		cust_name = scn.next();
		if (cust_name == null) {
			setTrans_result("获取客户姓名错误，请检查");
			return -1;
		}
		cust_tel = scn.next();// 电话号码
		if (cust_tel == null) {
			setTrans_result("获取电话号码错误，请检查");
			return -1;
		}
		cust_addr = scn.next();// 地址
		if (cust_addr == null) {
			setTrans_result("获取客户地址错误，请检查");
			return -1;
		}
		// 数据有效性校验
		if (dataInvalidate() != 0) {
			setTrans_result("输入数据合法性有误!");
			return -1;
		}
		return 0;
	}

	/**
	 * 处理交易
	 * 
	 * @throws Exception
	 */
	public int doTrans() throws Exception {
		// 实例化对象
		Custinfo cust = new Custinfo();
		cust.setdbhelper(dbhelper);
		cust.setId_type(id_type);
		cust.setId_no(id_no);
		cust.setCust_name(cust_name);
		cust.setCust_tel(cust_tel);
		cust.setCust_addr(cust_addr);
		// 判断是否存在
		// System.out.println(cust.custExist());
		if (cust.custExist()) {
			setTrans_result("客户已经存在");
			return -1;
		}
		// 不存在新增
		cust.setCust_no(cust.createCustNo());// 产生新的客户编号
		cust.setCust_status(1);
		if (cust.regCust() < 0) {
			setTrans_result("新增客户失败");
			return -1;
		} else {
			setTrans_result("新增客户成功");
			return 0;
		}
	}

	private int dataInvalidate() {
		DataValidate dv = new DataValidate();
		if (!dv.id_type_validate(id_type)) {
			return -1;
		}
		if (!dv.id_no_validate(id_no)) {
			return -1;
		}
		if (!dv.cust_name_validate(cust_name)) {
			return -1;
		}
		if (!dv.tel_no_validate(cust_tel)) {
			return -1;
		}
		if (!dv.addr_validate(cust_addr)) {
			return -1;
		}
		return 0;
	}

	public void setDbhelper(ConnectMySql dbhelper) {
		this.dbhelper = dbhelper;
	}
}
