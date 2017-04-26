package busi.doSql;

import db.ConnectMySql;

import java.sql.Date;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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
     * sql语句 - 用于插入子账户数据表
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

    /*建立 定期存款 子账户*/
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

    /*日期计算 - 计算定存到期日：dueDate=openDate + fixedDepositDays*/
    private static Date addDay(Date date, long day) {
        long time = date.getTime(); // 得到指定日期的毫秒数
        day = day * 24 * 60 * 60 * 1000; // 要加上的天数转换成毫秒数
        time += day; // 相加得到新的毫秒数
        return new Date(time); // 将毫秒数转换成日期
    }

    /*建立 通知存款 子账户*/
    public String openCallAcct(String acctNo,
                               String Trans_no,
                               String sub_Id_type,
                               double deposit_amount,
                               Date openDate,
                               int callDay, Date dueDate) {

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


    /**
     * 验证子账户是否存在
     * 用于活期存款和通知存款
     *
     * @throws Exception
     */
    public boolean isSubAcctTypeExist(String subAcctType, String acctNo) throws Exception {
        StringBuilder strSQL = new StringBuilder();
        strSQL.append("select * from t_sub_acct");
        strSQL.append(" where Sub_Id_type='");
        strSQL.append(subAcctType);
        strSQL.append("'");
        strSQL.append("and ACCT_NO ='");
        strSQL.append(acctNo);
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

    /*取得除定存账户外，活期或通知账户的子账户账号*/
    public String getSubAcctNo(String subAcctType) {
        String tmp = "";
        StringBuilder strSQL1 = new StringBuilder();
        strSQL1.append("select * from t_sub_acct");
        strSQL1.append(" where Sub_Id_type = '");
        strSQL1.append(subAcctType);
        strSQL1.append("'");
        strSQL1.append("and ACCT_NO ='");
        strSQL1.append(acct_no);
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


    /*建立 活期存款  子账户 - 开户时一并开设活期子账户*/
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
        subAcct.setDue_date(dueDate);//活期到期日统一标记为0002-12-31

        return subAcct.regSubAcct();
    }

    /*sql语句- 改变某个子账户余额*/
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

    /*取得该条子账户余额信息*/
    public double gainBalance() {
        double tmp = 0.00;
        StringBuffer strSQL = new StringBuffer();
        strSQL.append("select * from t_sub_acct");
        strSQL.append(" where Sub_acct_no = '");
        strSQL.append(sub_acct_no);
        strSQL.append("'");
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

    /*取得通知存款的通知期限*/
    public int gainCallDay() {
        int callDay = 0;
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

    /*手动设置日期，返回sql Date类型*/
    public Date setDate(int aaaa, int bb, int cc) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(aaaa, bb, cc);
        java.util.Date date = calendar.getTime();
        java.sql.Date date1 = new java.sql.Date(date.getTime());
        return date1;
    }

    public Date setDate() {//设置活期到期日为0000-00-00
        Calendar calendar = Calendar.getInstance();
        calendar.set(0, 0, 0);
        java.util.Date date = calendar.getTime();
        java.sql.Date date1 = new java.sql.Date(date.getTime());
        return date1;
    }

    /*撤销所有子账户，余额归零，状态转2*/
    public void revokeAllSubBalance() {
        List<String> acctNoList = new ArrayList<>();
        StringBuffer strSQL1 = new StringBuffer();
        strSQL1.append("select * from t_sub_acct");//sub_acct_status
        strSQL1.append(" where acct_no = '");
        strSQL1.append(acct_no);
        strSQL1.append("'");

        try {
            ResultSet rs1 = dbhelper.doQuery(strSQL1.toString());
            while (rs1.next()) {
                String tmp = "";
                tmp = rs1.getString("Sub_acct_no");
                acctNoList.add(tmp);
            }
            for (String s : acctNoList) {
                //遍历拿到的子账户，修改（update）balance为0和status为2
                StringBuffer strSQL = new StringBuffer();
                strSQL.append("update t_sub_acct set sub_acct_status =");
                strSQL.append(2);
                strSQL.append(",");
                strSQL.append("Sub_Acct_balance =");
                strSQL.append(0.00);
                strSQL.append(" where Sub_acct_no ='");
                strSQL.append(s);
                strSQL.append("'");
                System.out.println("SQL [" + strSQL + "]");
                String sqlUpdate = strSQL.toString();
                dbhelper.doUpdate(sqlUpdate);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*sql语句- 用于撤销子账户，整存，余额归零，状态转2*/
    public String revokeSubAcct() {
        StringBuffer strSQL = new StringBuffer();
        strSQL.append("update t_sub_acct set Sub_Acct_balance =");
        strSQL.append(sub_acct_balance);
        strSQL.append(",sub_acct_status =");
        strSQL.append(2);
        strSQL.append(" where Sub_acct_no ='");
        strSQL.append(sub_acct_no);
        strSQL.append("'");
        System.out.println("SQL [" + strSQL + "]");
        return strSQL.toString();
    }

    /*用于查找活期、通知存款的openDate*/
    public Date findDate(String sub_Id_type, String dateName) {
        Date date = null;
        StringBuffer strSQL = new StringBuffer();
        strSQL.append("select * from t_sub_acct");
        strSQL.append(" where acct_no = '");
        strSQL.append(acct_no);
        strSQL.append("'");
        strSQL.append("and Sub_Id_type ='");
        strSQL.append(sub_Id_type);
        strSQL.append("'");
        try {
            ResultSet rs1 = dbhelper.doQuery(strSQL.toString());
            while (rs1.next()) {
                date = rs1.getDate(dateName);//传入数据表字段名
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return date;
    }

    /*查找定存openDate*/
    public Date findDate(String subAcctNo) {
        Date date = null;
        StringBuffer strSQL = new StringBuffer();
        strSQL.append("select * from t_sub_acct");
        strSQL.append(" where Sub_acct_no = '");
        strSQL.append(subAcctNo);
        strSQL.append("'");

        try {
            ResultSet rs1 = dbhelper.doQuery(strSQL.toString());
            while (rs1.next()) {
                date = rs1.getDate("Open_date");//传入数据表字段名
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return date;
    }

    /*查找定存期限*/
    public int findFixedDepositPeriod(String subAcctNo) {
        int days = 0;
        StringBuffer strSQL = new StringBuffer();
        strSQL.append("select * from t_sub_acct");
        strSQL.append(" where Sub_acct_no = '");
        strSQL.append(subAcctNo);
        strSQL.append("'");

        try {
            ResultSet rs1 = dbhelper.doQuery(strSQL.toString());
            while (rs1.next()) {
                days = rs1.getInt("fix_deposit_period");
                break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return days;
    }

    /*查找定存金额*/
    public double findFixedDepositAmount(String subAcctNo) {
        double amount = 0;
        StringBuffer strSQL = new StringBuffer();
        strSQL.append("select * from t_sub_acct");
        strSQL.append(" where Sub_acct_no = '");
        strSQL.append(subAcctNo);
        strSQL.append("'");

        try {
            ResultSet rs1 = dbhelper.doQuery(strSQL.toString());
            while (rs1.next()) {
                amount = rs1.getDouble("Sub_Acct_balance");
                break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return amount;
    }

    /*查找活期子账户 账号，依据acctNo + 001*/
    public String findDemandAcctNo(String acctNo) {
        String demandAcctNo = "";
        StringBuffer strSQL = new StringBuffer();
        strSQL.append("select * from t_sub_acct");
        strSQL.append(" where Acct_No = '");
        strSQL.append(acctNo);
        strSQL.append("'");
        strSQL.append("and Sub_Id_type ='001'");


        try {
            ResultSet rsl = dbhelper.doQuery(strSQL.toString());
            while (rsl.next()) {
                rsl.getString("Sub_acct_no");
                break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return demandAcctNo;
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

}
