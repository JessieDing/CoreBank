package busi.doSql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Connection;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import db.ConnectMySql;

public class Acct {
    private String acct_no;// 账号
    private String acct_name;// 账户名
    private double balance;// 账户余额
    private int acct_status;// 状态 1-正常 2-销户 3-挂失 4-冻结
    private String cust_no;// 客户编号
    private Date reg_date;// 开户日期
    private String acct_pwd;// 账户密码
    private double freeze_amt;// 冻结金额
    private Date last_inter_date;// 上次结息日期
    private ConnectMySql dbhelper;
    private String id_type;
    private String id_no;

    public ConnectMySql getdbhelper() {
        return dbhelper;
    }

    public void setdbhelper(ConnectMySql dbhelper) {
        this.dbhelper = dbhelper;
    }

    /**
     * 默认构造方法
     */
    public Acct() {
        super();
    }

    /**
     * 开户
     *
     * @return
     */
    public String regAcct() {
        StringBuffer strSQL = new StringBuffer();
        strSQL.append("insert into t_acct values('");
        strSQL.append(acct_no).append("','");
        strSQL.append(acct_name).append("',");
        strSQL.append(balance).append(",");
        strSQL.append(acct_status).append(",'");
        strSQL.append(cust_no).append("',");
        strSQL.append("curtime() ,'");
        strSQL.append(acct_pwd).append("',");
        strSQL.append(freeze_amt).append(",'");
        strSQL.append(last_inter_date).append("')");
        System.out.println("SQL[" + strSQL + "]");
        return strSQL.toString();
    }

    /**
     * 销户
     *
     * @return
     */
    public String revokeAcct() {
        StringBuffer strSQL = new StringBuffer();
        strSQL.append("update t_acct set acct_status =");
        strSQL.append(acct_status);
        strSQL.append(",");
        strSQL.append("balance =");
        strSQL.append(balance);
        strSQL.append(" where acct_no ='");
        strSQL.append(acct_no);
        strSQL.append("'");
        System.out.println("SQL [" + strSQL + "]");
        return strSQL.toString();
    }

    /**
     * 挂失
     *
     * @return
     */
    public int regLoss() {
        StringBuffer strSQL1 = new StringBuffer();
        strSQL1.append("update t_acct set acct_status='");
        strSQL1.append(acct_status);
        strSQL1.append("' where acct_no='");
        strSQL1.append(acct_no);
        strSQL1.append("'");
        Date date = new Date(new java.util.Date().getTime());
        Time time = new Time(new java.util.Date().getTime());
        StringBuffer strSQL2 = new StringBuffer();
        strSQL2.append("insert into t_acct_operation values('");
        strSQL2.append(acct_no);
        strSQL2.append("','011','");
        strSQL2.append(date);
        strSQL2.append("','");
        strSQL2.append(time);
        strSQL2.append("')");
        System.out.println("SQL1 [" + strSQL1 + "]");
        System.out.println("SQL2 [" + strSQL2 + "]");
        Connection conn = dbhelper.getConnection();
        try {
            conn.setAutoCommit(false);
            int i = dbhelper.doUpdate(strSQL1.toString());
            int j = dbhelper.doUpdate(strSQL2.toString());
            conn.commit();
            if (i <= 0) {
                System.out.println("修改 t_acct 表失败");
                return -1;
            }
            if (j <= 0) {
                System.out.println("insert t_acct_operation 表失败");
                return -1;
            }
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 解挂
     *
     * @return
     */
    public int unreaLoss() {

        StringBuffer strSQL1 = new StringBuffer();
        strSQL1.append("update t_acct set acct_status='");
        strSQL1.append(acct_status);
        strSQL1.append("' where acct_no='");
        strSQL1.append(acct_no);
        strSQL1.append("'");
        Date date = new Date(new java.util.Date().getTime());
        Time time = new Time(new java.util.Date().getTime());
        StringBuffer strSQL2 = new StringBuffer();
        strSQL2.append("insert into t_acct_operation values('");
        strSQL2.append(acct_no);
        strSQL2.append("','012','");
        strSQL2.append(date);
        strSQL2.append("','");
        strSQL2.append(time);
        strSQL2.append("')");
        System.out.println("SQL1 [" + strSQL1 + "]");
        System.out.println("SQL2 [" + strSQL2 + "]");
        Connection conn = dbhelper.getConnection();
        try {
            conn.setAutoCommit(false);//事务
            int i = dbhelper.doUpdate(strSQL1.toString());
            int j = dbhelper.doUpdate(strSQL2.toString());
            conn.commit();
            if (i <= 0) {
                System.out.println("修改 t_acct 表失败");
                return -1;
            }
            if (j <= 0) {
                System.out.println("insert t_acct_operation 表失败");
                return -1;
            }
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
        return 0;

    }

    /**
     * 冻结
     *
     * @return
     */
    public int freeze() {
        StringBuffer strSQL1 = new StringBuffer();
        strSQL1.append("update t_acct set freeze_amt='");
        strSQL1.append(freeze_amt);
        strSQL1.append("',acct_status=");
        strSQL1.append(acct_status);
        strSQL1.append(" where acct_no='");
        strSQL1.append(acct_no);
        strSQL1.append("'");

        // AcctOperation acctOpr = new AcctOperation();
        // acctOpr.setAcct_no(acct_no);
        // acctOpr.setOpr_type("009");
        Date date = new Date(new java.util.Date().getTime());
        Time time = new Time(new java.util.Date().getTime());
        // acctOpr.setOpr_date(date);
        // acctOpr.setOpr_time(time);
        StringBuffer strSQL2 = new StringBuffer();
        strSQL2.append("insert into t_acct_operation values('");
        strSQL2.append(acct_no);
        strSQL2.append("','009','");
        strSQL2.append(date);
        strSQL2.append("','");
        strSQL2.append(time);
        strSQL2.append("')");
        System.out.println("SQL1 [" + strSQL1 + "]");
        System.out.println("SQL2 [" + strSQL2 + "]");
        Connection conn = dbhelper.getConnection();
        try {
            conn.setAutoCommit(false);
            int i = dbhelper.doUpdate(strSQL1.toString());
            int j = dbhelper.doUpdate(strSQL2.toString());
            conn.commit();
            if (i <= 0) {
                System.out.println("修改 t_acct 表失败");
                return -1;
            }
            if (j <= 0) {
                System.out.println("insert t_acct_operation 表失败");
                return -1;
            }
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 解冻
     *
     * @return
     */
    public int unFreeze() {

        StringBuffer strSQL1 = new StringBuffer();
        strSQL1.append("update t_acct set freeze_amt='");
        strSQL1.append(freeze_amt);
        strSQL1.append("',acct_status=");
        strSQL1.append(acct_status);
        strSQL1.append(" where acct_no='");
        strSQL1.append(acct_no);
        strSQL1.append("'");

        Date date = new Date(new java.util.Date().getTime());
        Time time = new Time(new java.util.Date().getTime());
        StringBuffer strSQL2 = new StringBuffer();
        strSQL2.append("insert into t_acct_operation values('");
        strSQL2.append(acct_no);
        strSQL2.append("','010','");
        strSQL2.append(date);
        strSQL2.append("','");
        strSQL2.append(time);
        strSQL2.append("')");
        System.out.println("SQL1 [" + strSQL1 + "]");
        System.out.println("SQL2 [" + strSQL2 + "]");
        Connection conn = dbhelper.getConnection();
        try {
            conn.setAutoCommit(false);
            int i = dbhelper.doUpdate(strSQL1.toString());
            int j = dbhelper.doUpdate(strSQL2.toString());
            conn.commit();
            if (i <= 0) {
                System.out.println("修改 t_acct 表失败");
                return -1;
            }
            if (j <= 0) {
                System.out.println("insert t_acct_operation 表失败");
                return -1;
            }
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
        return 0;

    }

    /**
     * 存款/取款
     *
     * @return
     */
    public String deposit() {
        StringBuffer strSQL = new StringBuffer();
        strSQL.append("update t_acct set balance =");
        strSQL.append(balance);
        strSQL.append(" where acct_no ='");
        strSQL.append(acct_no);
        strSQL.append("'");
        System.out.println("SQL [" + strSQL + "]");
        return strSQL.toString();
    }

    public String getAcct_no() {
        return acct_no;
    }

    public void setAcct_no(String acct_no) {
        this.acct_no = acct_no;
    }

    public String getAcct_name() {
        return acct_name;
    }

    public void setAcct_name(String acct_name) {
        this.acct_name = acct_name;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public int getAcct_status() {
        return acct_status;
    }

    public void setAcct_status(int acct_status) {
        this.acct_status = acct_status;
    }

    public String getCust_no() {
        return cust_no;
    }

    public void setCust_no(String cust_no) {
        this.cust_no = cust_no;
    }

    public Date getReg_date() {
        return reg_date;
    }

    public void setReg_date(Date reg_date) {
        this.reg_date = reg_date;
    }

    public String getAcct_pwd() {
        return acct_pwd;
    }

    public void setAcct_pwd(String acct_pwd) {
        this.acct_pwd = acct_pwd;
    }

    public double getFreeze_amt() {
        return freeze_amt;
    }

    public void setFreeze_amt(double freeze_amt) {
        this.freeze_amt = freeze_amt;
    }

    public Date getLast_inter_date() {
        return last_inter_date;
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

    public void setLast_inter_date(Date last_inter_date) {
        this.last_inter_date = last_inter_date;
    }

    /**
     * 创建账号
     *
     * @return
     */
    public String createAcctNo() {
        Random ran = new Random();
        StringBuffer sb = new StringBuffer();
        sb.append("621081");
        sb.append("436742");
        for (int i = 0; i < 7; i++) {
            sb.append(ran.nextInt(9));
        }
        return sb.toString();
    }

    /**
     * 获取客户姓名
     *
     * @throws Exception
     */
    public String getAcctName() {
        String tmp = null;
        StringBuffer strSQL = new StringBuffer();
        strSQL.append("select * from t_cust_info");
        strSQL.append(" where id_type = '");
        strSQL.append(id_type);
        strSQL.append("' and id_no = '");
        strSQL.append(id_no);
        strSQL.append("'");
        // System.out.println("SQL [" + strSQL + "]");
        try {
            ResultSet rs = dbhelper.doQuery(strSQL.toString());
            while (rs.next()) {
                tmp = rs.getString("cust_name");
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return tmp;
    }

    /**
     * 获取客户编号
     *
     * @throws Exception
     */
    public String getCustNo() {
        String tmp = null;
        StringBuffer strSQL = new StringBuffer();
        strSQL.append("select * from t_cust_info");
        strSQL.append(" where id_type = '");
        strSQL.append(id_type);
        strSQL.append("' and id_no = '");
        strSQL.append(id_no);
        strSQL.append("'");
        System.out.println("SQL [" + strSQL + "]");
        try {
            ResultSet rs = dbhelper.doQuery(strSQL.toString());
            while (rs.next()) {
                tmp = rs.getString("cust_no");
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return tmp;
    }

    /**
     * 判断客户是否已经开户
     */
    public boolean acctExist() throws Exception {
        StringBuffer strSQL = new StringBuffer();
        strSQL.append("select * from t_acct");
        strSQL.append(" where cust_no = '");
        strSQL.append(cust_no);
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
     * 验证账号是否存在
     *
     * @throws Exception
     */
    public boolean acctNoVerify() throws Exception {
        StringBuffer strSQL = new StringBuffer();
        strSQL.append("select * from t_acct");
        strSQL.append(" where acct_no = '");
        strSQL.append(acct_no);
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
     * 存取款/验证账号的状态
     */
    public boolean acctStatusVerify() {
        int tmp;
        StringBuffer strSQL = new StringBuffer();
        strSQL.append("select * from t_acct");
        strSQL.append(" where acct_no = '");
        strSQL.append(acct_no);
        strSQL.append("'");
        System.out.println("SQL [" + strSQL + "]");
        try {
            ResultSet rs = dbhelper.doQuery(strSQL.toString());
            while (rs.next()) {
                tmp = rs.getInt("acct_status");
                if (tmp == 1 || tmp == 4) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 验证密码
     */
    public boolean gainPassWord() {
        String tmp = null;
        StringBuffer strSQL = new StringBuffer();
        strSQL.append("select * from t_acct");
        strSQL.append(" where acct_no = '");
        strSQL.append(acct_no);
        strSQL.append("'");
        System.out.println("SQL [" + strSQL + "]");
        try {
            ResultSet rs = dbhelper.doQuery(strSQL.toString());
            while (rs.next()) {
                tmp = rs.getString("acct_pwd");
                if (tmp.equals(acct_pwd)) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取余额
     */
    public double gainBalance() {
        double tmp = 0.00;
        StringBuffer strSQL = new StringBuffer();
        strSQL.append("select * from t_acct");
        strSQL.append(" where acct_no = '");
        strSQL.append(acct_no);
        strSQL.append("'");
        // System.out.println("SQL [" + strSQL + "]");
        try {
            ResultSet rs = dbhelper.doQuery(strSQL.toString());
            while (rs.next()) {
                tmp = rs.getDouble("balance");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tmp;
    }

    /**
     * 获取冻结金额
     */
    public double gainFreezeAmt() {
        double tmp = 0.00;
        StringBuffer strSQL = new StringBuffer();
        strSQL.append("select * from t_acct");
        strSQL.append(" where acct_no = '");
        strSQL.append(acct_no);
        strSQL.append("'");
        System.out.println("SQL [" + strSQL + "]");
        try {
            ResultSet rs = dbhelper.doQuery(strSQL.toString());
            while (rs.next()) {
                tmp = rs.getDouble("freeze_amt");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tmp;
    }

    /**
     * 验证账号为冻结状态
     */
    public boolean FreezeStatus() {
        int tmp;
        StringBuffer strSQL = new StringBuffer();
        strSQL.append("select * from t_acct");
        strSQL.append(" where acct_no = '");
        strSQL.append(acct_no);
        strSQL.append("'");
        System.out.println("SQL [" + strSQL + "]");
        try {
            ResultSet rs = dbhelper.doQuery(strSQL.toString());
            while (rs.next()) {
                tmp = rs.getInt("acct_status");
                if (tmp == 4) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;

    }

    /**
     * 验证账号为挂失状态
     */
    public boolean lossStatus() {
        int tmp;
        StringBuffer strSQL = new StringBuffer();
        strSQL.append("select * from t_acct");
        strSQL.append(" where acct_no = '");
        strSQL.append(acct_no);
        strSQL.append("'");
        System.out.println("SQL [" + strSQL + "]");
        try {
            ResultSet rs = dbhelper.doQuery(strSQL.toString());
            while (rs.next()) {
                tmp = rs.getInt("acct_status");
                if (tmp == 3) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 判断账户是否被注销
     */
    public boolean revokeStatus() {
        int tmp;
        StringBuffer strSQL = new StringBuffer();
        strSQL.append("select * from t_acct");
        strSQL.append(" where acct_no = '");
        strSQL.append(acct_no);
        strSQL.append("'");
        System.out.println("SQL [" + strSQL + "]");
        try {
            ResultSet rs = dbhelper.doQuery(strSQL.toString());
            while (rs.next()) {
                tmp = rs.getInt("acct_status");
                if (tmp == 2) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /*
    * 加总子账户余额
    * */
    public double calcTotalBalance() {
        double sum = 0.00;
        List<Double> bList = new ArrayList<>();
        StringBuffer strSQL1 = new StringBuffer();
//		strSQL1.append("SELECT Sub_Acct_balance FROM t_sub_acct");
        strSQL1.append("select * from t_sub_acct");
        strSQL1.append(" where acct_no = '");
        strSQL1.append(acct_no);
        strSQL1.append("'");

        try {
            ResultSet rs1 = dbhelper.doQuery(strSQL1.toString());
            while (rs1.next()) {
                double tmp = 0;
                tmp = rs1.getDouble("Sub_Acct_balance");
                bList.add(tmp);
            }
            for (Double d : bList) {
                sum += d;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sum;
    }


}
