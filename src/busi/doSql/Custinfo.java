package busi.doSql;

import java.sql.ResultSet;
import java.text.NumberFormat;

import db.ConnectMySql;

public class Custinfo {
	private String cust_no;// 客户编号
	private String id_type;// 证件类型111-身份证 112-临时身份证113-户口簿 114-军官证
							// 115-警官证133-学生证 999-其它
	private String id_no;// 证件号码
	private String cust_name;// 客户姓名
	private String cust_tel;// 电话号码
	private String cust_addr;// 地址
	private int cust_status;// 客户状态 1-正常 2-注销
	private ConnectMySql dbhelper;

	public ConnectMySql getdbhelper() {
		return dbhelper;
	}

	public void setdbhelper(ConnectMySql dbhelper) {
		this.dbhelper = dbhelper;
	}

	/**
	 * 构造方法
	 */
	public Custinfo() {
		super();
	}

	/**
	 * 新建客户
	 */
	public int regCust() {
		StringBuffer strSQL = new StringBuffer();
		strSQL.append("insert into t_cust_info values('");
		strSQL.append(cust_no).append("','");
		strSQL.append(id_type).append("','");
		strSQL.append(id_no).append("','");
		strSQL.append(cust_name).append("','");
		strSQL.append(cust_tel).append("','");
		strSQL.append(cust_addr).append("',");
		strSQL.append(cust_status).append(")");
		System.out.println("SQL[" + strSQL + "]");
		int i = dbhelper.doUpdate(strSQL.toString());
		if (i <= 0) {
			System.out.println("插入数据库失败");
			return -1;
		}
		return 0;
	}

	/**
	 * 修改客户信息
	 */
	public int editCust() {
		StringBuffer strSQL = new StringBuffer();
		strSQL.append("update t_cust_info set cust_tel ='");
		strSQL.append(cust_tel);
		strSQL.append("', cust_addr ='");
		strSQL.append(cust_addr);
		strSQL.append("' where id_type = '");
		strSQL.append(id_type);
		strSQL.append("' and id_no = '");
		strSQL.append(id_no);
		strSQL.append("'");
		System.out.println("SQL[" + strSQL + "]");
		int i = dbhelper.doUpdate(strSQL.toString());
		if (i <= 0) {
			System.out.println("修改信息失败");
			return -1;
		}
		return 0;
	}

	/**
	 * 注销客户客户
	 */
	public int delCust() {
		StringBuffer strSQL = new StringBuffer();
		strSQL.append("update t_cust_info set cust_status ='");
		strSQL.append(cust_status);
		strSQL.append("' where id_type = '");
		strSQL.append(id_type);
		strSQL.append("' and id_no = '");
		strSQL.append(id_no);
		strSQL.append("'");
		System.out.println("SQL[" + strSQL + "]");
		int i = dbhelper.doUpdate(strSQL.toString());
		if (i <= 0) {
			System.out.println("修改信息失败");
			return -1;
		}
		return 0;
	}

	public String getCust_no() {
		return cust_no;
	}

	public void setCust_no(String cust_no) {
		this.cust_no = cust_no;
	}

	public String getId_type() {
		return id_type;
	}

	public void setId_type(String id_type) {
		this.id_type = id_type;
	}

	public String getId_no() {
		return id_no;
	}

	public void setId_no(String id_no) {
		this.id_no = id_no;
	}

	public String getCust_name() {
		return cust_name;
	}

	public void setCust_name(String cust_name) {
		this.cust_name = cust_name;
	}

	public String getCust_tel() {
		return cust_tel;
	}

	public void setCust_tel(String cust_tel) {
		this.cust_tel = cust_tel;
	}

	public String getCust_addr() {
		return cust_addr;
	}

	public void setCust_addr(String cust_addr) {
		this.cust_addr = cust_addr;
	}

	public int getCust_status() {
		return cust_status;
	}

	public void setCust_status(int cust_status) {
		this.cust_status = cust_status;
	}

	/**
	 * 判断客户是否存在
	 * 
	 * @return
	 * @throws Exception
	 */

	public boolean custExist() throws Exception {
		StringBuffer strSQL = new StringBuffer();
		strSQL.append("select * from t_cust_info");
		strSQL.append(" where id_type = '");
		strSQL.append(id_type);
		strSQL.append("' and id_no = '");
		strSQL.append(id_no);
		strSQL.append("'");
		System.out.println("SQL [" + strSQL + "]");
		ResultSet rs = dbhelper.doQuery(strSQL.toString());
		int num = 0;
		try {
			while (rs.next()) {
				num++;
				break;
			}
			if (num > 0) {
				return true;
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return true;
		}
		return false;
	}

	/**
	 * 创建客户编号
	 * 
	 * @return
	 * @throws Exception
	 */
	public String createCustNo() throws Exception {
		String strNewCustNo = null;
		String strSQL = "select ifnull(max(cust_no),'00000000') from t_cust_info";
		ResultSet rs = dbhelper.doQuery(strSQL);
		try {
			if (rs.next()) {
				String strCustNo = rs.getString(1);
				Integer nCustNo = Integer.valueOf(strCustNo);
				nCustNo = nCustNo + 1;
				// 格式化成8位
				NumberFormat nf = NumberFormat.getInstance();
				nf.setMinimumIntegerDigits(8);
				strNewCustNo = nf.format(nCustNo).replace(",", "");
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return strNewCustNo;
	}

	/**
	 * 打印客户信息
	 * 
	 * @return
	 */
	public boolean prtTicket() {
		StringBuffer strSQL = new StringBuffer();
		strSQL.append("select * from t_cust_info");
		strSQL.append(" where id_type = '");
		strSQL.append(id_type);
		strSQL.append("' and id_no = '");
		strSQL.append(id_no);
		strSQL.append("'");
		// System.out.println("SQL [" + strSQL + "]");
		int i = 0;
		try {
			ResultSet rs = dbhelper.doQuery(strSQL.toString());
			System.out.println("客户编号\t\t证件类型\t\t证件号码\t\t客户姓名\t联系方式\t\t家庭地址\t客户状态");
				while (rs.next()) {
					i++;
					System.out.println(rs.getString("cust_no") + "\t" + rs.getString("id_type")

							+ "\t" + rs.getString("id_no") + "\t" + rs.getString("cust_name") + "\t"

							+ rs.getString("cust_tel") + "\t" + rs.getString("cust_addr") + "   "

							+ rs.getInt("cust_status"));
				}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(i>0){
			return true;
		}
		return false;
	}

	/**
	 * 判断客户状态
	 * 
	 * @throws Exception
	 */
	public boolean custStatusExist() {
		StringBuffer strSQL = new StringBuffer();
		strSQL.append("select cust_status from t_cust_info");
		strSQL.append(" where id_type = '");
		strSQL.append(id_type);
		strSQL.append("' and id_no = '");
		strSQL.append(id_no);
		strSQL.append("'");
		System.out.println("SQL [" + strSQL + "]");
		try {
			ResultSet rs = dbhelper.doQuery(strSQL.toString());
			while (rs.next()) {
				int tmp = rs.getInt("cust_status");
				// System.out.println(tmp);
				if (tmp == 2) {
					return false;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}
}
