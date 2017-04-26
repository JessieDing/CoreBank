package busi.trans;

import busi.BankTrans;
import busi.doSql.*;
import busi.validate.DataValidate;
import db.ConnectMySql;

import java.sql.Date;
import java.sql.Time;
import java.util.Calendar;

public class OpenAcct extends BankTrans {
    private String id_type;// 证件类型
    private String id_no;// 证件号码
    private String pwd;// 密码
    private String aginPwd;// 再次输入密码
    private ConnectMySql dbhelper;
    private final static double DEFAULT_MIN_AMOUNT = 10.00;

    public void prtPrompt() {
        System.out.println("》》》开户《《《");
        System.out.println("请输入:@证件类型@证件号码");
    }

    public int getInPut() {
        id_type = scn.next();
        if (id_type == null) {
            setTrans_result("获取证件类型失败");
            return -1;
        }
        id_no = scn.next();
        if (id_no == null) {
            setTrans_result("获取证件号码失败");
            return -1;
        }
        // 数据有效性校验
        if (dataInvalidate() != 0) {
            setTrans_result("输入数据合法性有误!");
            return -1;
        }
        return 0;
    }

    // 证件类型、证件号码验证
    private int dataInvalidate() {
        DataValidate dv = new DataValidate();
        if (!dv.id_type_validate(id_type)) {
            return -1;
        }
        if (!dv.id_no_validate(id_no)) {
            return -1;
        }
        return 0;
    }

    public int doTrans() throws Exception {
        Custinfo cust = new Custinfo();
        cust.setdbhelper(dbhelper);
        cust.setId_type(id_type);
        cust.setId_no(id_no);
        // 判断客户是否存在
        if (!cust.custExist()) {
            setTrans_result("客户不存在");
            return -1;
        }
        // 判断客户是否注销
        if (!cust.custStatusExist()) {
            setTrans_result("客户已经注销");
            return -1;
        }

        // 客户存在且没有注销
        Acct acct = new Acct();
        acct.setdbhelper(dbhelper);
        acct.setId_type(id_type);
        acct.setId_no(id_no);
        acct.setCust_no(acct.getCustNo());
        // 判断是否已经开户
        if (acct.acctExist()) {
            setTrans_result("客户已经开户,不能再次开户");
            return -1;
        }
        // 客户没有开户，进行开户
        System.out.println("请输入6位数密码:");
        pwd = scn.next();
        if (pwd == null) {
            setTrans_result("获取密码失败");
            return -1;
        }
        // 密码校验
        DataValidate dv = new DataValidate();
        if (!dv.acct_pwd_validate(pwd)) {
            setTrans_result("输入数据合法性有误!");
            return -1;
        }
        System.out.println("请再次输入密码:");
        aginPwd = scn.next();
        if (aginPwd == null) {
            setTrans_result("获取密码失败");
            return -1;
        }
        if (!pwd.equals(aginPwd)) {
            setTrans_result("两次输入密码不一致");
            return -1;
        }
        //插入数据库acct表
        //变更：此处为总账户
        String acctNo = acct.createAcctNo();//开户，创建银行账号
        acct.setAcct_no(acctNo);
        acct.setAcct_name(acct.getAcctName());
        /*
        * 变更：总balance=各表balance之和
		* 需修改acct的setBalance方法
		* */
        acct.setBalance(DEFAULT_MIN_AMOUNT);//总账户余额（默认开户最低存款额10.00）
        acct.setAcct_status(1);
        acct.setCust_no(acct.getCustNo());
        Date date = new Date(new java.util.Date().getTime());
        acct.setReg_date(date);
        acct.setAcct_pwd(pwd);
        acct.setFreeze_amt(0.00);
        acct.setLast_inter_date(date);

        // 插入数据库T_ACCT_OPERATION表（提方法）
        AcctOperation acctOpr = new AcctOperation();
        acctOpr.setDbhelper(dbhelper);
        acctOpr.setAcct_no(acct.getAcct_no());
        acctOpr.setOpr_type("005");
        acctOpr.setOpr_date(date);
        acctOpr.setOpr_time(new Time(new java.util.Date().getTime()));

        //插入数据库t_acct_detail表（提方法）
        AcctDetail detail = new AcctDetail();
        detail.setdbhelper(dbhelper);
        detail.setTrans_no(detail.createDetailNo());
        detail.setAcct_no(acctNo);
        detail.setBalance_before(0.00);
        detail.setBalance_after(DEFAULT_MIN_AMOUNT);
        detail.setTrans_type(acctOpr.getOpr_type());
        detail.setTrans_amt(DEFAULT_MIN_AMOUNT);
        detail.setTrans_date(date);
        detail.setTrans_time(new Time(new java.util.Date().getTime()));
        detail.setOperator_id("001");

        //插入数据库t_sub_acct表
        SubAcct subAcct = new SubAcct();
        Date date1 = acct.getReg_date();
        //默认活期子账户
        String strRegSubAcct = subAcct.openSubAcct(acctNo, detail.getTrans_no(), "001", DEFAULT_MIN_AMOUNT, date1, subAcct.setDate());
//        double initBalance = acct.calcTotalBalance();
//        acct.setBalance(initBalance);

        if (dbhelper.insertIntoDBO(dbhelper, acct.regAcct(), acctOpr.addAcctOperation(), detail.addAcctDetail(), strRegSubAcct) < 0) {
            setTrans_result("开户失败!");
        }
        setTrans_result("开户成功");
        return 0;

    }








    public void setDbhelper(ConnectMySql dbhelper) {
        this.dbhelper = dbhelper;
    }
}
