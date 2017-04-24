package busi.trans;

import java.sql.Date;
import java.sql.Time;

import busi.BankTrans;
import busi.doSql.Acct;
import busi.doSql.AcctDetail;
import busi.validate.DataValidate;
import db.ConnectMySql;

public class DrawMoney extends BankTrans {
	private String acct_no;// 账号
	private String acct_pwd;// 账户密码
	private double money;// 取款金额
	private Date date;
	private Time time;
	private ConnectMySql dbhelper;

	@Override
	public void prtPrompt() {
		System.out.println("》》》取钱《《《");
		System.out.println("输入账号、账户密码");
		System.out.println("@acct_no@acct_pwd");
	}

	@Override
	public int getInPut() {
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

	private int dataInvalidate() {
		DataValidate dv = new DataValidate();
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
		// 账户是否存在
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
			setTrans_result("当前账户状态不能取款");
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
		// 验证可用余额(balance-freezeamt)
		double availableBalance = acct.gainBalance() - acct.gainFreezeAmt();
		boolean flag;
		do {
			flag = false;
			System.out.println("请输入取款金额");
			money = scn.nextDouble();
			// 取款金额判断
			if ((money - 0.0 < 0.000001) || (money - 1000000.000 > 0.00001)) {
				System.out.println("输入金额不正确或超出最大取款金额1000000");
				flag = true;
			}
			if (availableBalance - money < 0.00000001) {
				System.out.println("余额不足");
				flag = true;
			}
		} while (flag);
		date = new Date(new java.util.Date().getTime());
		AcctDetail detail = new AcctDetail();
		detail.setdbhelper(dbhelper);
		detail.setAcct_no(acct_no);
		detail.setTrans_date(date);
		detail.setTrans_type("007");
		double totalAmt = detail.getTotalBalance();
		if (totalAmt + money > 1000000) {
			setTrans_result("取款金额超过当天最大额度");
			return -1;
		}
		// 取款
		double tmpMoney = acct.gainBalance() - money;
		acct.setBalance(tmpMoney);

		// AcctOperation acctOpr = new AcctOperation();
		// acct.setdbhelper(dbhelper);
		// acctOpr.setAcct_no(acct_no);
		// acctOpr.setOpr_type("007");
		// acctOpr.setOpr_date(date);
		// acctOpr.setOpr_time(new Time(new java.util.Date().getTime()));

		// AcctDetail detail = new AcctDetail();
		// detail.setdbhelper(dbhelper);
		String detailNo = detail.createDetailNo();
		detail.setTrans_no(detailNo);
		detail.setAcct_no(acct_no);
		detail.setBalance_before(acct.gainBalance());
		detail.setBalance_after(acct.getBalance());
		detail.setTrans_type(detail.getTrans_type());
		detail.setTrans_amt(money);
		detail.setTrans_date(date);
		time = new Time(new java.util.Date().getTime());
		detail.setTrans_time(time);
		detail.setOperator_id("001");
		if (dbhelper.insertInto(dbhelper, acct.deposit(), detail.addAcctDetail()) < 0) {
			setTrans_result("取钱失败!");
		}
		setTrans_result("取款成功");

		// 打印凭条、
		setTrans_code(detailNo);
		setTrans_name("取款");
		prtTransInfo(acct);
		return 0;
	}

	@Override
	public void setDbhelper(ConnectMySql dbhelper) {
		this.dbhelper = dbhelper;

	}

	/**
	 * 打印凭证
	 */
	public void prtTransInfo(Acct acct) {
		System.out.println("交  易  码[" + trans_code + "]");
		System.out.println("交易名称[" + trans_name + "]");
		System.out.println("交易账户[" + acct_no + "]");
		System.out.println("交易金额[" + money + "]");
		System.out.println("账户余额[" + acct.gainBalance() + "]");
		System.out.println("交易时间[" + date + " " + time + "]");
	}
}
