package busi.trans;

import java.sql.Date;
import java.sql.Time;

import busi.BankTrans;
import busi.doSql.Acct;
import busi.doSql.AcctDetail;
import busi.doSql.AcctOperation;
import busi.validate.DataValidate;
import db.ConnectMySql;

public class AccountCancellation extends BankTrans {
	private String id_type;// 证件类型111-身份证 112-临时身份证113-户口簿 114-军官证
	// 115-警官证133-学生证 999-其它
	private String id_no;// 证件号码
	private String acct_no;// 账号
	private String acct_pwd;// 账户密码
	private ConnectMySql dbhelper;
	private int acct_status = 2;// 状态 1-正常 2-销户 3-挂失 4-冻结

	@Override
	public void prtPrompt() {
		System.out.println("》》》销户《《《");
		System.out.println("请输入证件类型、证件号、账号、密码,格式如下:");
		System.out.println("@id_type@id_no@acct_no@acct_pwd");
	}

	@Override
	public int getInPut() {
		id_type = scn.next();
		if (id_type == null) {
			setTrans_result("获取账号类型失败");
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
			setTrans_result("获取账户密码失败");
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

	@Override
	public int doTrans() throws Exception {
		// 验证账户是否存在
		Acct acct = new Acct();
		acct.setdbhelper(dbhelper);
		acct.setAcct_no(acct_no);
		acct.setAcct_pwd(acct_pwd);
		if (!acct.acctNoVerify()) {
			setTrans_result("账户不存在");
			return -1;
		}
		// 验证账户状态是否为正常
		if (!acct.acctStatusVerify()) {
			setTrans_result("当前账户状态不能销户");
			return -1;
		}
		if (acct.FreezeStatus()) {
			setTrans_result("当前账户状态不能销户");
			return -1;
		}
		// 密码验证
		int count = 1;
		while (!acct.gainPassWord()) {
			count++;
			System.out.println("账号或密码错误,请重新输入");
			acct_no = scn.next();
			if (acct_no == null) {
				setTrans_result("获取账号失败");
				return -1;
			}
			acct_pwd = scn.next();
			if (acct_pwd == null) {
				setTrans_result("获取账户密码失败");
				continue;
			}
			acct.setAcct_no(acct_no);
			acct.setAcct_pwd(acct_pwd);
			if (count >= 3) {
				setTrans_result("账号或密码错误3次,不能销户!");
				return -1;
			}
		}
		// 将这次操作写入账户操作表
		AcctOperation acctOpr = new AcctOperation();
		acctOpr.setDbhelper(dbhelper);
		Date date = new Date(new java.util.Date().getTime());
		acctOpr.setAcct_no(acct_no);
		acctOpr.setOpr_type("008");
		acctOpr.setOpr_date(date);
		acctOpr.setOpr_time(new Time(new java.util.Date().getTime()));

		AcctDetail detail = new AcctDetail();
		detail.setdbhelper(dbhelper);
		detail.setTrans_no(detail.createDetailNo());
		detail.setAcct_no(acct_no);
		detail.setBalance_before(acct.gainBalance());
		detail.setBalance_after(0.0);
		detail.setTrans_type(acctOpr.getOpr_type());
		detail.setTrans_amt(acct.gainBalance());
		detail.setTrans_date(date);
		detail.setTrans_time(new Time(new java.util.Date().getTime()));
		detail.setOperator_id("001");

		// 销户将账户状态改为销户状态写入数据库
		acct.setAcct_status(acct_status);
		acct.setBalance(0.00);
		if (dbhelper.insertIntoDBO(dbhelper,acct.revokeAcct(), acctOpr.addAcctOperation(), detail.addAcctDetail()) < 0) {
			setTrans_result("取钱失败!");
		}
		setTrans_result("销户成功");
		return 0;
	}

	@Override
	public void setDbhelper(ConnectMySql dbhelper) {
		this.dbhelper = dbhelper;
	}
}