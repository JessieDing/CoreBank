package busi.doSql;

import db.ConnectMySql;

import java.sql.Date;
import java.sql.ResultSet;
import java.util.Calendar;

/**
 * Created by dell on 2017/4/24.
 */
public class SubAcct {
    private String acct_no;// 账号（主账号）
    private String sub_acct_no;// 子账号
    private String sub_Id_type;//子账户类型001活期002整存整取003通知
    private double sub_acct_balance;//子账户余额
    private Date open_date;//起存日
    private int fix_deposit_period;//定存期限
    private Date last_Inters_date;//上次结息日
    private int aggregate;//积数
    private int acct_status;// 状态 1-正常 2-销户 3-挂失 4-冻结
    private int call_day;//通知天数
    private Date due_date;//到期日
    private ConnectMySql dbhelper;

    public SubAcct() {
    }

    /**
     * 子账户
     *
     * @return
     */
    public String regSubAcct() {
        StringBuilder strSQL = new StringBuilder();
        strSQL.append("insert into t_sub_acct values('");
        strSQL.append(acct_no).append("','");
        strSQL.append(sub_acct_no).append("','");
        strSQL.append(sub_Id_type).append("',");
        strSQL.append(sub_acct_balance).append(",'");
        strSQL.append(open_date).append("',");
        strSQL.append(fix_deposit_period).append(",");
        strSQL.append(last_Inters_date).append(",");
        strSQL.append(aggregate).append(",");
        strSQL.append(acct_status).append(",");
        strSQL.append(call_day).append(",'");
        strSQL.append(due_date).append("')");
        System.out.println("SQL[" + strSQL + "]");
        return strSQL.toString();
    }

    public String openFixedAcct(String acctNo,
                                String Trans_no,
                                String sub_Id_type,
                                double deposit_amount,
                                Date openDate,
                                int days) {
        this.acct_no = acctNo;
        this.sub_acct_no = "2000" + Trans_no;
        this.sub_Id_type = sub_Id_type;
        this.sub_acct_balance = deposit_amount;
        this.open_date = openDate;
        this.due_date = addDay(openDate, days);
        this.fix_deposit_period = days;//定存天数

        return regSubAcct();
    }

    private static Date addDay(Date date, long day) {
        long time = date.getTime(); // 得到指定日期的毫秒数
        day = day * 24 * 60 * 60 * 1000; // 要加上的天数转换成毫秒数
        time += day; // 相加得到新的毫秒数
        return new Date(time); // 将毫秒数转换成日期
    }


    public String openCallAcct(String acctNo,
                               String Trans_no,
                               String sub_Id_type,
                               double deposit_amount,
                               Date openDate,
                               int callDay,Date dueDate) {

        setdbhelper(dbhelper);
        setAcct_no(acctNo);
        setSub_acct_no("3000" + Trans_no);
        setSub_Id_type(sub_Id_type);
        setSub_acct_balance(deposit_amount);
        setOpen_date(openDate);
        setCall_day(callDay);//通知期限（1天、7天）
        setDue_date(dueDate);

        return regSubAcct();
    }


    public String getAcct_no() {
        return acct_no;
    }

    public void setAcct_no(String acct_no) {
        this.acct_no = acct_no;
    }

    public String getSub_acct_no() {
        return sub_acct_no;
    }

    public void setSub_acct_no(String sub_acct_no) {
        this.sub_acct_no = sub_acct_no;
    }

    public String getSub_Id_type() {
        return sub_Id_type;
    }

    public void setSub_Id_type(String sub_Id_type) {
        this.sub_Id_type = sub_Id_type;
    }

    public double getSub_acct_balance() {
        return sub_acct_balance;
    }

    public void setSub_acct_balance(double sub_acct_balance) {
        this.sub_acct_balance = sub_acct_balance;
    }

    public Date getOpen_date() {
        return open_date;
    }

    public void setOpen_date(Date open_date) {
        this.open_date = open_date;
    }

    public int getFix_deposit_period() {
        return fix_deposit_period;
    }

    public void setFix_deposit_period(int fix_deposit_period) {
        this.fix_deposit_period = fix_deposit_period;
    }

    public Date getLast_Inters_date() {
        return last_Inters_date;
    }

    public void setLast_Inters_date(Date last_Inters_date) {
        this.last_Inters_date = last_Inters_date;
    }

    public int getAggregate() {
        return aggregate;
    }

    public void setAggregate(int aggregate) {
        this.aggregate = aggregate;
    }

    public int getCall_day() {
        return call_day;
    }

    public void setCall_day(int call_day) {
        this.call_day = call_day;
    }

    public int getAcct_status() {
        return acct_status;
    }

    public void setAcct_status(int acct_status) {
        this.acct_status = acct_status;
    }

    public ConnectMySql getdbhelper() {
        return dbhelper;
    }

    public void setdbhelper(ConnectMySql dbhelper) {
        this.dbhelper = dbhelper;
    }

    public Date getDue_date() {
        return due_date;
    }

    public void setDue_date(Date due_date) {
        this.due_date = due_date;
    }

    /**
     * 验证子账户类型
     *
     * @throws Exception
     */
    public boolean isSubAcctTypeExist(String subAcctType) throws Exception {
        StringBuilder strSQL = new StringBuilder();
        strSQL.append("select * from t_sub_acct");
        strSQL.append(" where Sub_Id_type='");
        strSQL.append(subAcctType);
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

    public String getSubAcctNo(String subAcctType) {
        String tmp = "";
        StringBuilder strSQL1 = new StringBuilder();
        strSQL1.append("select * from t_sub_acct");
        strSQL1.append(" where Sub_Id_type = '");
        strSQL1.append(subAcctType);
        strSQL1.append("'");
        try {
            ResultSet rs1 = dbhelper.doQuery(strSQL1.toString());
            while (rs1.next()) {
                tmp = rs1.getString("Sub_acct_no");
                break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tmp;
    }

    public String insertIntoCallAcct(String acctNo,
                                     String subAcctNo,
                                     String sub_Id_type,
                                     double deposit_amount,
                                     Date openDate,
                                     int callDay) {

        setdbhelper(dbhelper);
        setAcct_no(acctNo);
        setSub_acct_no(subAcctNo);
        setSub_Id_type(sub_Id_type);
        setSub_acct_balance(deposit_amount);
        setOpen_date(openDate);
        setCall_day(callDay);

        return regSubAcct();
    }

    /*插入001类（活期）子账户数据表*/
    public String insertIntoDemandAcct(String acctNo,
                                       String subAcctNo,
                                       String sub_Id_type,
                                       double balance,
                                       Date openDate) {
        setdbhelper(dbhelper);
        setAcct_no(acctNo);
        setSub_acct_no(subAcctNo);
        setSub_Id_type(sub_Id_type);
        setSub_acct_balance(balance);
        setOpen_date(openDate);

        return regSubAcct();

    }

    //活期
    // 活期Sub_acct_no规则为1000+4位流水号
    public String openSubAcct(String acctNo,
                              String Trans_no,
                              String sub_Id_type,
                              double sub_acct_balance,
                              Date openDate, Date dueDate) {
        SubAcct subAcct = new SubAcct();
        subAcct.setdbhelper(dbhelper);
        subAcct.setAcct_no(acctNo);
        subAcct.setSub_acct_no("1000" + Trans_no);//默认活期子账户
        subAcct.setSub_Id_type(sub_Id_type);
        subAcct.setSub_acct_balance(sub_acct_balance);
        subAcct.setOpen_date(openDate);
        subAcct.setFix_deposit_period(0);
        subAcct.setDue_date(dueDate);//活期如何设置到期日？？？

        return subAcct.regSubAcct();
    }


    public String depositSubAcct() {
        StringBuffer strSQL = new StringBuffer();
        strSQL.append("update t_sub_acct set Sub_Acct_balance =");
        strSQL.append(sub_acct_balance);
        strSQL.append(" where Sub_acct_no ='");
        strSQL.append(sub_acct_no);
        strSQL.append("'");
        System.out.println("SQL [" + strSQL + "]");
        return strSQL.toString();
    }

    public double gainBalance() {
        double tmp = 0.00;
        StringBuffer strSQL = new StringBuffer();
        strSQL.append("select * from t_sub_acct");
        strSQL.append(" where Sub_acct_no = '");
        strSQL.append(sub_acct_no);
        strSQL.append("'");
        // System.out.println("SQL [" + strSQL + "]");
        try {
            ResultSet rs = dbhelper.doQuery(strSQL.toString());
            while (rs.next()) {
                tmp = rs.getDouble("Sub_Acct_balance");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tmp;
    }

    public int gainCallDay(){
        int callDay =0;
        StringBuffer strSQL = new StringBuffer();
        strSQL.append("select * from t_sub_acct");
        strSQL.append(" where Sub_acct_no = '");
        strSQL.append(sub_acct_no);
        strSQL.append("'");
        try {
            ResultSet rs = dbhelper.doQuery(strSQL.toString());
            while (rs.next()) {
                callDay = rs.getInt("call_day");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return callDay;
    }


     public Date setDate() {//设置活期到期日为0000-00-00
        Calendar calendar = Calendar.getInstance();
        calendar.set(0000, 00, 00);
        java.util.Date date = calendar.getTime();
        java.sql.Date date1 = new java.sql.Date(date.getTime());
        return date1;
    }
}
