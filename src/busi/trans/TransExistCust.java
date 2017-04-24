package busi.trans;

import busi.BankTrans;
import busi.doSql.Custinfo;
import busi.validate.DataValidate;
import db.ConnectMySql;

public class TransExistCust extends BankTrans {
	private String id_type;// 证件类型
	private String id_no;// 证件号码
	private ConnectMySql dbhelper;

	@Override
	public void prtPrompt() {
		System.out.println("》》》查询客户信息《《《");
		System.out.println("查询请输入:@证件类型@证件号码");

	}

	@Override
	public int getInPut() {
		id_type = scn.next();
		if (id_type == null) {
			setTrans_result("获取证件类型失败");
			return -1;
		}
		id_no = scn.next();
		if (id_no == null) {
			setTrans_result("获取证件号码失败");
			return -1;
		}
		// 数据有效性校验
		if (dataInvalidate() != 0) {
			System.out.println("输入数据合法性有误!");
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
		return 0;
	}

	@Override
	public int doTrans() throws Exception {
		Custinfo cust = new Custinfo();
		cust.setdbhelper(dbhelper);
		cust.setId_type(id_type);
		cust.setId_no(id_no);
		try {
			if (!cust.custExist()) {
				setTrans_result("客户不存在");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (cust.prtTicket()) {
			setTrans_result("客户信息获取成功");
		} else {
			setTrans_result("查询客户不存在");
		}
		return 0;
	}

	public void setDbhelper(ConnectMySql dbhelper) {
		this.dbhelper = dbhelper;
	}
}
