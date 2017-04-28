package busi.doSql;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Time;
import java.text.NumberFormat;

import db.ConnectMySql;

public class AcctDetail {
    private String trans_no;// 交易流水
    private String acct_no;// 账号
    private double balance_before;// 交易前金额
    private double balance_after;// 交易后金额
    private String trans_type;// 交易类型
    private double trans_amt;// 交易金额
    private Date trans_date;// 交易时间
    private Time trans_time;
    private String operator_id;// 操作员
    private String credit_debit;//借贷
    private String counterparty_Acct;//交易对手账号
    private String remark;//摘要
    private ConnectMySql dbhelper;
    private Date startDate;
    private Date endDate;

    public ConnectMySql getdbhelper() {
        return dbhelper;
    }

    public void setdbhelper(ConnectMySql dbhelper) {
        this.dbhelper = dbhelper;
    }

    /**
     * 构造方法
     */
    public AcctDetail() {
        super();
    }

    /**
     * 新增交易记录
     */
    public String addAcctDetail() {
        StringBuffer strSQL = new StringBuffer();
        strSQL.append("insert into t_acct_detail values('");//insert插入新的一行
        strSQL.append(trans_no);
        strSQL.append("','");
        strSQL.append(acct_no);
        strSQL.append("',");
        strSQL.append(balance_before);
        strSQL.append(",");
        strSQL.append(balance_after);
        strSQL.append(",'");
        strSQL.append(trans_type);
        strSQL.append("',");
        strSQL.append(trans_amt);
        strSQL.append(",'");
        strSQL.append(trans_date);
        strSQL.append("','");
        strSQL.append(trans_time);
        strSQL.append("','");
        strSQL.append(operator_id);
        strSQL.append("','");
        strSQL.append(credit_debit);
        strSQL.append("','");
        strSQL.append(counterparty_Acct);
        strSQL.append("','");
        strSQL.append(remark);
        strSQL.append("')");
        System.out.println("SQL [" + strSQL + "]");
        return strSQL.toString();
    }

    /**
     * 查询交易明细
     */
    public int queryAcctDetail() {
        StringBuffer strSQL1 = new StringBuffer();
        strSQL1.append("select * from t_acct_detail group by trans_no having acct_no='");
        strSQL1.append(acct_no);
        strSQL1.append("' and trans_date>='");
        strSQL1.append(startDate);
        strSQL1.append("' and trans_date<='");
        strSQL1.append(endDate);
        strSQL1.append("'");
        System.out.println("SQL [" + strSQL1 + "]");
        System.out.println("交易流水\t交易账号\t交易前金额\t交易后金额\t交易类型\t交易金额\t交易日期\t交易时间\t操作员");
        try {
            ResultSet rs1 = dbhelper.doQuery(strSQL1.toString());
            while (rs1.next()) {
                System.out.println(rs1.getString(1) + "\t"

                        + rs1.getString(2) + "\t"

                        + rs1.getDouble(3) + "\t"

                        + rs1.getDouble(4) + "\t"

                        + rs1.getString(5) + "\t"

                        + rs1.getDouble(6) + "\t"

                        + rs1.getDate(7) + "\t"

                        + rs1.getTime(8) + "\t"

                        + rs1.getString(9));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
        return 0;
    }

    /**
     * 修改
     */
    public int editAcctDetail() {
        return 0;
    }

    public String getTrans_no() {
        return trans_no;
    }

    public void setTrans_no(String trans_no) {
        this.trans_no = trans_no;
    }

    public String getAcct_no() {
        return acct_no;
    }

    public void setAcct_no(String acct_no) {
        this.acct_no = acct_no;
    }

    public double getBalance_before() {
        return balance_before;
    }

    public void setBalance_before(double balance_before) {
        this.balance_before = balance_before;
    }

    public double getBalance_after() {
        return balance_after;
    }

    public void setBalance_after(double balance_after) {
        this.balance_after = balance_after;
    }

    public String getTrans_type() {
        return trans_type;
    }

    public void setTrans_type(String trans_type) {
        this.trans_type = trans_type;
    }

    public double getTrans_amt() {
        return trans_amt;
    }

    public void setTrans_amt(double trans_amt) {
        this.trans_amt = trans_amt;
    }

    public Date getTrans_date() {
        return trans_date;
    }

    public void setTrans_date(Date trans_date) {
        this.trans_date = trans_date;
    }

    public String getOperator_id() {
        return operator_id;
    }

    public void setOperator_id(String operator_id) {
        this.operator_id = operator_id;
    }

    public Time getTrans_time() {
        return trans_time;
    }

    public void setTrans_time(Time trans_time) {
        this.trans_time = trans_time;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getCredit_debit() {
        return credit_debit;
    }

    public void setCredit_debit(String credit_debit) {
        this.credit_debit = credit_debit;
    }

    public String getCounterparty_Acct() {
        return counterparty_Acct;
    }

    public void setCounterparty_Acct(String counterparty_Acct) {
        this.counterparty_Acct = counterparty_Acct;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    /**
     * 产生4位流水号
     */
    public String createDetailNo() {
        String strNewCustNo = null;
        String strSQL = "select ifnull(max(trans_no),'0000') from t_acct_detail";
        try {
            ResultSet rs = dbhelper.doQuery(strSQL);
            if (rs.next()) {
                String strCustNo = rs.getString(1);
                Integer nCustNo = Integer.valueOf(strCustNo);
                nCustNo = nCustNo + 1;
                // 格式化成4位
                NumberFormat nf = NumberFormat.getInstance();
                nf.setMinimumIntegerDigits(4);
                strNewCustNo = nf.format(nCustNo).replace(",", "");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return strNewCustNo;
    }

    /**
     * 获取交易时间
     */
    public long getTransTime() {
        long time = 0;
        StringBuffer strSQL = new StringBuffer();
        strSQL.append("select * from t_acct_detail where acct_no='");
        strSQL.append(acct_no);
        strSQL.append("'");
        try {
            ResultSet rs = dbhelper.doQuery(strSQL.toString());
            while (rs.next()) {
                time = rs.getDate("ttrans_date").getTime();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return time;
    }

    /**
     * 获取当天取款金额
     */
    public double getTotalBalance() {
        double totalAmt = 0.0;
        StringBuilder strSQL = new StringBuilder();
        strSQL.append("select sum(trans_amt) from t_acct_detail where acct_no='");
        strSQL.append(acct_no);
        strSQL.append("' and trans_type='");
        strSQL.append(trans_type);
        strSQL.append("' and trans_date='");
        strSQL.append(trans_date);
        strSQL.append("'");
        try {
            ResultSet rs = dbhelper.doQuery(strSQL.toString());
            while (rs.next()) {
                totalAmt = rs.getDouble("trans_amt");
            }
        } catch (Exception e) {
            return totalAmt;
        }
        return totalAmt;
    }
}
