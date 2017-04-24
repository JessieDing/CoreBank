package busi.trans;

import busi.BankTrans;
import busi.doSql.Custinfo;
import busi.validate.DataValidate;
import db.ConnectMySql;

public class TransEditCust extends BankTrans {
	private String id_type;// 证件类型
	private String id_no;// 证件号码
	private String cust_tel;// 电话号码
	private String cust_addr;// 地址
	private ConnectMySql dbhelper;

	public void prtPrompt() {
		System.out.println("》》》修改客户信息《《《");
		System.out.println("输入@证件类型@证件号码@新电话号码@新家庭地址");
	}

	public int getInPut() {
		id_type = scn.next();
		if (id_type == null) {
			setTrans_result("获取客户编号失败");
		}
		id_no = scn.next();
		if (id_no == null) {
			setTrans_result("获取客户编号失败");
		}
		cust_tel = scn.next();
		if (cust_tel == null) {
			setTrans_result("获取客户电话号码失败");
		}
		cust_addr = scn.next();
		if (cust_addr == null) {
			setTrans_result("获取客户地址失败");
		}
		// 数据有效性校验
		if (dataInvalidate() != 0) {
			setTrans_result("输入数据合法性有误!");
			return -1;
		}
		return 0;
	}

	private int dataInvalidate() {
		DataValidate dv = new DataValidate();
		if (!dv.id_type_validate(id_type)) {
			return -1;
		}
		if (!dv.id_no_validate(id_no)) {
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

	@Override
	public int doTrans() throws Exception {
		Custinfo cust = new Custinfo();
		cust.setdbhelper(dbhelper);
		cust.setId_type(id_type);
		cust.setId_no(id_no);
		cust.setCust_tel(cust_tel);
		cust.setCust_addr(cust_addr);
		// 判断客户是否存在
		if (!cust.custExist()) {
			setTrans_result("客户不存在");
			return -1;
		}
		if (!cust.custStatusExist()) {
			setTrans_result("客户已经被注销");
			return -1;
		}
		// 客户已经存在进行修改
		if (cust.editCust() < 0) {
			setTrans_result("修改客户信息失败");
			return -1;
		} else {
			setTrans_result("修改客户信息成功");
			cust.prtTicket();
			return 0;
		}
	}

	@Override
	public void setDbhelper(ConnectMySql dbhelper) {
		this.dbhelper = dbhelper;
	}

}
