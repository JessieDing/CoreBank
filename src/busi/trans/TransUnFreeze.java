package busi.trans;

import busi.BankTrans;
import busi.doSql.Acct;
import busi.validate.DataValidate;
import db.ConnectMySql;

public class TransUnFreeze extends BankTrans {
	private String id_type;// 证件类型
	private String id_no;// 证件号码
	private String acct_no;// 账号
	private ConnectMySql dbhelper;

	public void prtPrompt() {
		System.out.println("》》》账户解除冻结《《《");
		System.out.println("请输入证件类型、证件号码、冻结账号");
	}

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
		acct_no = scn.next();
		if (acct_no == null) {
			setTrans_result("获取账号失败");
			return -1;
		}
		// 数据有效性校验
		if (dataInvalidate() != 0) {
			setTrans_result("输入数据合法性有误!");
			return -1;
		}
		return 0;
	}

	public int doTrans() throws Exception {
		Acct acct = new Acct();
		acct.setdbhelper(dbhelper);
		acct.setAcct_no(acct_no);
		if (!acct.FreezeStatus()) {
			setTrans_result("账户没有被冻结!");
			return -1;
		}
		if(acct.revokeStatus()){
			setTrans_result("账户已结被注销!");
			return -1;
		}
		System.out.println("请输入解冻结金额!");
		double unfreezeAmt = scn.nextDouble();
		if (unfreezeAmt - 0.0 < 0.0000001) {
			setTrans_result("解冻金额有误!解冻失败!");
			return -1;
		}
		double freezeAmt = acct.gainFreezeAmt() - unfreezeAmt;
		if (freezeAmt-0.0 < 0.0) {
			setTrans_result("解冻金额大于冻结金额!解冻失败!");
			return -1;
		}
		acct.setFreeze_amt(freezeAmt);
		if (freezeAmt == 0.00 ) {
			acct.setAcct_status(1);
		} else {
			acct.setAcct_status(4);
		}
		// System.out.println("请输入解冻结原因!");
		// String unfreezeCause = scn.next();
		if (acct.unFreeze() < 0) {
			setTrans_result("解冻失败");
			return -1;
		}
		// System.out.println("冻结原因:");
		// System.out.println(unfreezeCause);
		setTrans_result("解冻成功");
		return 0;
	}

	public void setDbhelper(ConnectMySql dbhelper) {
		this.dbhelper = dbhelper;
	}

	/**
	 * 数据有效性验证
	 * 
	 * @return
	 */
	private int dataInvalidate() {
		DataValidate dv = new DataValidate();
		if (!dv.id_type_validate(id_type)) {
			return -1;
		}
		if (!dv.id_no_validate(id_no)) {
			return -1;
		}
		if (!dv.acct_no_validate(acct_no)) {
			return -1;
		}
		return 0;
	}
}
