package busi.trans;

import java.sql.Date;
import java.text.SimpleDateFormat;

import busi.BankTrans;
import busi.doSql.Acct;
import busi.doSql.AcctDetail;
import busi.doSql.Custinfo;
//import busi.doSql.Custinfo;
import busi.validate.DataValidate;
import db.ConnectMySql;

@SuppressWarnings("unused")
public class QueryAcctDetail extends BankTrans {
	private String id_type;// 证件类型
	private String id_no;// 证件号码
	private String acct_no;// 账号
	private String acct_pwd;// 账户密码
	private String startDate;
	private String endDate;
	private ConnectMySql dbhelper;

	@Override
	public void prtPrompt() {
		System.out.println("》》》查询交易记录!《《《");
		System.out.println("请输入:");
		System.out.println("@证件类型@证件号码@账号@账户密码");
	}

	@Override
	public int getInPut() {
		id_type = scn.next();
		if (id_type == null) {
			setTrans_result("获取证件类型失败!");
			return -1;
		}
		id_no = scn.next();
		if (id_no == null) {
			setTrans_result("获取证件号码失败!");
			return -1;
		}
		acct_no = scn.next();
		if (acct_no == null) {
			setTrans_result("获取账号失败!");
			return -1;
		}
		acct_pwd = scn.next();
		if (acct_pwd == null) {
			setTrans_result("获取密码失败!");
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
		// 判断证件是否存在
		// Custinfo cust = new Custinfo();
		// cust.setdbhelper(dbhelper);
		// cust.setId_type(id_type);
		// cust.setId_no(id_no);
		// if (!cust.custExist()) {
		// setTrans_result("该证件不能查询");
		// return -1;
		// }
		// 判断账户是否存在
		Acct acct = new Acct();
		acct.setdbhelper(dbhelper);
		acct.setId_no(id_no);
		acct.setId_type(id_type);
		acct.setAcct_no(acct_no);
		acct.setAcct_pwd(acct_pwd);
		acct.setCust_no(acct.getCustNo());
		if (!acct.acctExist()) {
			setTrans_result("账号不存在");
			return -1;
		}
		// 验证密码
		int count = 1;
		while (!acct.gainPassWord()) {
			count++;
			System.out.println("账号或密码错误,请重新输入账号,密码");
			acct_no = scn.next();
			if (acct_no == null) {
				System.out.println("获取账号失败");
				continue;
			}
			acct_pwd = scn.next();
			if (acct_pwd == null) {
				System.out.println("获取账户密码失败");
				continue;
			}
			acct.setAcct_no(acct_no);
			acct.setAcct_pwd(acct_pwd);
			if (count >= 3) {
				setTrans_result("账号或密码错误3次,不能取款!");
				return -1;
			}
		}
		// 输入查询日期
		System.out.println("请输入起始日期:");
		System.out.println("日期格式:yyyy-MM-dd");
		startDate = scn.next();
		while (!Date_validate(startDate)) {
			System.out.println("输入日期格式有误，请重新输入!");
			startDate = scn.next();
		}
		java.util.Date stDate = new java.util.Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		stDate = sdf.parse(startDate);
		Date startsqlDate = new Date(stDate.getTime());
		// long startTime =stDate.getTime();
		System.out.println("请输入截止日期:");
		endDate = scn.next();
		while (!Date_validate(endDate)) {
			System.out.println("输入日期格式有误，请重新输入!");
			endDate = scn.next();
		}
		java.util.Date enDate = new java.util.Date();
		enDate = sdf.parse(endDate);
		long time = 365 * 24 * 3600;
		if ((enDate.getTime() / 1000 - stDate.getTime() / 1000) > time) {
			setTrans_result("查询时间段超过一年,请重新查询");
			return -1;
		}
		Date endsqlDate = new Date(enDate.getTime());
		// long enTime =enDate.getTime();
		AcctDetail acctDetail = new AcctDetail();
		acctDetail.setdbhelper(dbhelper);
		acctDetail.setAcct_no(acct_no);
		acctDetail.setStartDate(startsqlDate);
		acctDetail.setEndDate(endsqlDate);
		if (acctDetail.queryAcctDetail() < 0) {
			setTrans_result("查询失败!");
			return -1;
		}
		setTrans_result("查询成功!");
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
		if (!dv.acct_pwd_validate(acct_pwd)) {
			return -1;
		}
		return 0;
	}

	/**
	 * 日期格式校验
	 * 
	 * @param startDate
	 * @return
	 */
	public boolean Date_validate(String startDate) {
		String str = "^((((1[6-9]|[2-9]\\d)\\d{2})-(0?[13578]|1[02])-(0?[1-9]|[12]\\d|3[01]))|"
				+ "(((1[6-9]|[2-9]\\d)\\d{2})-(0?[13456789]|1[012])-(0?[1-9]|[12]\\d|30))|"
				+ "(((1[6-9]|[2-9]\\d)\\d{2})-0?2-(0?[1-9]|1\\d|2[0-8]))|(((1[6-9]|[2-9]\\d)"
				+ "(0[48]|[2468][048]|[13579][26])|((16|[2468][048]|[3579][26])00))-0?2-29-))$";
		if (startDate.matches(str)) {
			return true;
		}
		return false;
	}

}
