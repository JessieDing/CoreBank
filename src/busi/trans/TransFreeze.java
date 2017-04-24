package busi.trans;

import busi.BankTrans;
import busi.doSql.Acct;
import busi.validate.DataValidate;
import db.ConnectMySql;

public class TransFreeze extends BankTrans {
	private String id_type;// 证件类型
	private String id_no;// 证件号码
	private String acct_no;// 账号
	private ConnectMySql dbhelper;

	@Override
	public void prtPrompt() {
		System.out.println("》》》账户冻结《《《");
		System.out.println("请输入证件类型、证件号码、冻结账号");
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

	@Override
	public int doTrans() throws Exception {
		Acct acct = new Acct();
		acct.setdbhelper(dbhelper);
		acct.setAcct_no(acct_no);
		if (!acct.acctNoVerify()) {
			setTrans_result("账户不存在!");
			return -1;
		}
		if(acct.revokeStatus()){
			setTrans_result("账户已结被注销!");
			return -1;
		}
		if (acct.FreezeStatus()) {
			setTrans_result("账户已经被冻结!");
			return -1;
		}
		System.out.println("请输入冻结金额!");
		double freezeAmt = scn.nextDouble();
		if (freezeAmt - 0.00 < 0.00000001) {
			setTrans_result("冻结金额有误!");
			return -1;
		}
		if ((acct.gainFreezeAmt() + freezeAmt) - acct.gainBalance() > 0.000001) {
			setTrans_result("冻结金额大于余额!冻结失败,请重新冻结!");
			return -1;
		}
		acct.setFreeze_amt(acct.gainFreezeAmt() + freezeAmt);
		acct.setAcct_status(4);
		System.out.println("请输入冻结原因!");
		String freezeCause = scn.next();
		if (acct.freeze() < 0) {
			setTrans_result("冻结失败");
			return -1;
		}
		System.out.println("冻结原因:");
		System.out.println(freezeCause);
		setTrans_result("冻结成功");
		return 0;
	}

	@Override
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
