package busi.doSql;

import db.ConnectMySql;

import java.sql.Date;

/**
 * Created by dell on 2017/4/24.
 */
public class SubAcct {
    private String acct_no;// 账号（主账号）
    private String sub_acct_no;// 子账号
    private String sub_Id_type;//子账户类型001活期002整存整取003通知
    private double sub_acct_balance;//子账户余额
    private Date open_date;//起存日
    private Date due_date_for_Fixed;//到期日
    private Date last_Inters_date;//上次结息日
    private int aggregate;//积数
    private int acct_status;// 状态 1-正常 2-销户 3-挂失 4-冻结
    private int call_day;//通知天数
    private ConnectMySql dbhelper;

    public SubAcct() {
    }


    /**
     * 子账户
     *
     * @return
     */
    public String regSubAcct() {
        StringBuffer strSQL = new StringBuffer();
        strSQL.append("insert into t_sub_acct values('");
        strSQL.append(acct_no).append("','");
        strSQL.append(sub_acct_no).append("','");
        strSQL.append(sub_Id_type).append("',");
        strSQL.append(sub_acct_balance).append(",'");
        strSQL.append(open_date).append("',");
        strSQL.append(due_date_for_Fixed).append(",");
        strSQL.append(last_Inters_date).append(",");
        strSQL.append(aggregate).append(",");
        strSQL.append(acct_status).append(",");
        strSQL.append(call_day).append(")");
        System.out.println("SQL[" + strSQL + "]");
        return strSQL.toString();
    }

    public String openDemandAcct(){
        StringBuffer strSQL = new StringBuffer();
        strSQL.append("insert into t_sub_acct " +
                "(Acct_No,Sub_acct_no,Sub_Id_type,Sub_Acct_balance,Open_date,sub_acct_status)" +
                " values('");
        strSQL.append(acct_no).append("','");
        strSQL.append(sub_acct_no).append("','");
        strSQL.append(sub_Id_type).append("',");
        strSQL.append(sub_acct_balance).append(",'");
        strSQL.append(open_date).append("',");
//        strSQL.append(aggregate).append(",");
        strSQL.append(acct_status).append(",");
        return strSQL.toString();
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

    public Date getDue_date_for_Fixed() {
        return due_date_for_Fixed;
    }

    public void setDue_date_for_Fixed(Date due_date_for_Fixed) {
        this.due_date_for_Fixed = due_date_for_Fixed;
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
/*
public String regDemandAcct(){
        StringBuffer strSQL = new StringBuffer();
        strSQL.append("insert into subacct_demand values('");
}
*/
}
