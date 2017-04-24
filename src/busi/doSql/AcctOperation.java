package busi.doSql;

import java.sql.Time;
import java.sql.Date;

import db.ConnectMySql;

public class AcctOperation {
	private String acct_no;// 账号
	private String opr_type;// 操作类型
	private Date opr_date;// 操作日期
	private Time opr_time;// 操作日期
	private ConnectMySql dbhelper;

	public AcctOperation() {
		super();
	}

	/**
	 * 开户操作添加到数据库账户操作表中
	 */
	public String addAcctOperation() {
		StringBuffer strSQL = new StringBuffer();
		strSQL.append("insert into T_ACCT_OPERATION values('");
		strSQL.append(acct_no).append("','");
		strSQL.append(opr_type).append("','");
		strSQL.append(opr_date).append("','");
		strSQL.append(opr_time).append("')");
		System.out.println("SQL[" + strSQL + "]");
		
		return strSQL.toString();
	}

	/**
	 * 修改
	 */
	public int editAcctOperation() {
		return 0;
	}

	public String getAcct_no() {
		return acct_no;
	}

	public void setAcct_no(String acct_no) {
		this.acct_no = acct_no;
	}

	public String getOpr_type() {
		return opr_type;
	}

	public void setOpr_type(String string) {
		this.opr_type = string;
	}

	public Date getOpr_date() {
		return opr_date;
	}

	public void setOpr_date(Date opr_date) {
		this.opr_date = opr_date;
	}

	public Time getOpr_time() {
		return opr_time;
	}

	public void setOpr_time(Time opr_time) {
		this.opr_time = opr_time;
	}

	public ConnectMySql getDbhelper() {
		return dbhelper;
	}

	public void setDbhelper(ConnectMySql dbhelper) {
		this.dbhelper = dbhelper;
	}

}
