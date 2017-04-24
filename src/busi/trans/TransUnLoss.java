package busi.trans;

import busi.BankTrans;
import busi.doSql.Acct;
import busi.doSql.Custinfo;
import busi.validate.DataValidate;
import db.ConnectMySql;

public class TransUnLoss extends BankTrans {

	private String id_type;// 证件类型
	private String id_no;// 证件号码
	private String acct_no;// 账号
	private String acct_pwd;// 账户密码
	private ConnectMySql dbhelper;

	public void prtPrompt() {
		System.out.println("》》》账户解挂《《《");
		System.out.println("请输入证件类型、证件号码、挂失账号、账户密码");
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
		acct_pwd = scn.next();
		if (acct_pwd == null) {
			setTrans_result("获取密码失败");
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
		Custinfo cust = new Custinfo();
		cust.setdbhelper(dbhelper);
		cust.setId_type(id_type);
		cust.setId_no(id_no);
		if (!cust.custExist()) {
			setTrans_result("客户不存在");
			return -1;
		}
		if (!cust.custStatusExist()) {
			setTrans_result("客户已注销");
			return -1;
		}
		Acct acct = new Acct();
		acct.setdbhelper(dbhelper);
		acct.setAcct_no(acct_no);
		acct.setAcct_pwd(acct_pwd);
		if (!acct.acctNoVerify()) {
			setTrans_result("账号不存在");
			return -1;
		}
		if(acct.revokeStatus()){
			setTrans_result("账户已结被注销!");
			return -1;
		}
		// 验证密码
		int count = 1;
		while (!acct.gainPassWord()) {
			count++;
			System.out.println("账号或密码错误,请重新输入账号，密码");
			acct_no = scn.next();
			if (acct_no == null) {
				System.out.println("获取账号失败");
				continue;
			}
			acct_pwd = scn.next();
			if (acct_pwd == null) {
				setTrans_result("获取账户密码失败");
				continue;
			}
			acct.setAcct_no(acct_no);
			acct.setAcct_pwd(acct_pwd);
			if (count >= 3) {
				setTrans_result("账号或密码错误3次,不能取款!");
				return -1;
			}
		}
		if (!acct.lossStatus()) {
			setTrans_result("账户没有被挂失!不能解挂");
			return -1;
		}
		System.out.println("输入解挂原因:");
		String cause = scn.next();
		// 解挂
		if(acct.gainFreezeAmt()-0.0>0.0000001){
			acct.setAcct_status(4);
		}else{
			acct.setAcct_status(1);
		}
		if (acct.unreaLoss() < 0) {
			setTrans_result("解挂失败!");
			return -1;
		}
		setTrans_result("解挂成功");
		System.out.println("解挂原因:" + cause);
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
		if (!dv.acct_pwd_validate(acct_pwd)) {
			return -1;
		}
		return 0;
	}

}
