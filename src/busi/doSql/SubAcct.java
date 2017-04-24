package busi.doSql;

import db.ConnectMySql;

import java.sql.Date;

/**
 * Created by dell on 2017/4/24.
 */
public class SubAcct {
    private String acct_no;// 账号（主账号）
    private String Sub_acct_no;// 子账号
    private String Sub_Id_type;//子账户类型001活期002整存整取003通知
    private double Sub_Acct_balance;//子账户余额
    private Date Open_date;//起存日
    private Date Due_date_for_Fixed;//到期日
    private Date Last_Inters_date;//上次结息日
    private int aggregate;//积数

    public SubAcct() {
    }

public String regDemandAcct(){
        StringBuffer strSQL = new StringBuffer();
        strSQL.append("insert into subacct_demand values('");
}
}
