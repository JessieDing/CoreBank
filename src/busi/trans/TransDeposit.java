package busi.trans;

import java.sql.Date;
import java.sql.Time;
import java.util.Scanner;

import busi.BankTrans;
import busi.doSql.Acct;
import busi.doSql.AcctDetail;
import busi.doSql.SubAcct;
import busi.validate.DataValidate;
import db.ConnectMySql;

public class TransDeposit extends BankTrans {
    private String acct_no;// 账号
    private String acct_pwd;// 账户密码
    private double money;// 存款金额
    private double tmpMoney;
    private Date date;
    private Time time;
    private String depositType;// 001-整存整取 002-整存零取 003-定活两变 004-定期存款
    private ConnectMySql dbhelper;


    @Override
    public void prtPrompt() {
        System.out.println("》》》存款《《《");
        System.out.println("请输入账号、密码、存款类型（001-活期 002-整存整取 003通知存款）");
        System.out.println("@acct_no @acct_pwd @deposit_type");
    }

    @Override
    public int getInPut() {
        acct_no = scn.next();
        if (acct_no == null) {
            setTrans_result("获取账号失败");
            return -1;
        }
        acct_pwd = scn.next();
        if (acct_pwd == null) {
            setTrans_result("获取密码失败");
            return -1;
        }

        //存款类型
        depositType = scn.next();
        /*if (depositType == null) {
            setTrans_result("获取存款类型失败，默认存入活期");
			return 0;
		}*/


        // 数据有效性校验
        if (dataInvalidate() != 0) {
            setTrans_result("输入数据合法性有误!");
            return -1;
        }
        return 0;
    }

    private int dataInvalidate() {
        DataValidate dv = new DataValidate();
        if (!dv.acct_no_validate(acct_no)) {
            return -1;
        }
        if (!dv.acct_pwd_validate(acct_pwd)) {
            return -1;
        }
        return 0;
    }

    @Override
    public int doTrans() throws Exception {
        // 账户是否存在
        Acct acct = new Acct();
        acct.setdbhelper(dbhelper);
        acct.setAcct_no(acct_no);
        acct.setAcct_pwd(acct_pwd);
        if (!acct.acctNoVerify()) {
            setTrans_result("账户不存在");
            return -1;
        }
        // 验证账户状态是否为正常
        if (!acct.acctStatusVerify()) {
            setTrans_result("当前账户状态不能存款");
            return -1;
        }
        // 密码验证
        int count = 1;
        while (!acct.gainPassWord()) {
            count++;
            System.out.println("账号或密码错误,请重新输入账号、密码");
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
                setTrans_result("账号或密码错误3次,不能存款!");
                return -1;
            }
        }


        // 输入存款金额
        DataValidate dv = new DataValidate();
        boolean flag;
        do {
            flag = false;
            System.out.println("请输入存款金额");
            money = scn.nextDouble();
            // 存款金额判断
            if (!dv.money_validate(money)) {
                flag = true;
            }
        } while (flag);
        // 存款

        SubAcct subAcct = new SubAcct();
        AcctDetail detail = new AcctDetail();
        OpenAcct openAcct = new OpenAcct();
        double banlanceBefore = acct.calcTotalBalance();
//活期
        if (depositType.equals("001")) {

            return 0;
        }
        //整存整取
        if (depositType.equals("002")) {
            date = new Date(new java.util.Date().getTime());
            detail.setdbhelper(dbhelper);
            String detailNo = detail.createDetailNo();
            detail.setTrans_no(detailNo);
            detail.setAcct_no(acct_no);
            detail.setBalance_before(banlanceBefore);
            detail.setTrans_type("006");
            detail.setTrans_amt(money);
            detail.setTrans_date(date);
            time = new Time(new java.util.Date().getTime());
            detail.setTrans_time(time);
            detail.setOperator_id("001");

            System.out.println("请输入定存期限（90/ 180/ 360/ 720/ 1080）：");
            Scanner scanner = new Scanner(System.in);
            int day = scanner.nextInt();
            String addfixedAcct = subAcct.openFixedAcct(acct_no, detail.getTrans_no(), "002", money, date, day);
            if (dbhelper.insertIntoDBO(dbhelper, addfixedAcct) < 0) {
                setTrans_result("写入子账户失败!");
            }
            setTrans_result("成功写入子账户");
            double banlanceAfter = acct.calcTotalBalance();
            acct.setBalance(banlanceAfter);
            detail.setBalance_after(acct.getBalance());//交易明细拿到定存后总账余额
            if (dbhelper.insertIntoDBO(dbhelper, acct.deposit(), detail.addAcctDetail()) < 0) {
                setTrans_result("存款失败!");
            }
            setTrans_result("存款成功");


            // 打印凭条
            setTrans_code(detailNo);
            setTrans_name("定存" + day + "天");
            prtTransInfo(acct);
            return 0;
        }
        //通知存款
        if (depositType.equals("003")) {
            if (!subAcct.isSubAcctTypeExist("003")){
                date = new Date(new java.util.Date().getTime());
                detail.setdbhelper(dbhelper);
                String detailNo = detail.createDetailNo();
                detail.setTrans_no(detailNo);
                detail.setAcct_no(acct_no);
                detail.setBalance_before(banlanceBefore);
                detail.setTrans_type("006");
                detail.setTrans_amt(money);
                detail.setTrans_date(date);
                time = new Time(new java.util.Date().getTime());
                detail.setTrans_time(time);
                detail.setOperator_id("001");

                System.out.println("请输入通知期限：1：1天通知；7：7天通知");
                Scanner scanner = new Scanner(System.in);
                int callDay = scanner.nextInt();
                if (!dv.callDayValidate(callDay)){
                    setTrans_result("通知期限输入有误!");
                    return -1;
                }
                String addCalldAcct = subAcct.openCallAcct(acct_no, detail.getTrans_no(), "003", money, date, callDay);
                if (dbhelper.insertIntoDBO(dbhelper, addCalldAcct) < 0) {
                    setTrans_result("写入子账户失败!");
                }
                setTrans_result("成功写入子账户");
                double banlanceAfter = acct.calcTotalBalance();
                acct.setBalance(banlanceAfter);
                detail.setBalance_after(acct.getBalance());//交易明细拿到定存后总账余额
                if (dbhelper.insertIntoDBO(dbhelper, acct.deposit(), detail.addAcctDetail()) < 0) {
                    setTrans_result("存款失败!");
                }
                setTrans_result("存款成功");


                // 打印凭条
                setTrans_code(detailNo);
                setTrans_name(callDay + "天"+ "通知存款" );
                prtTransInfo(acct);
                return 0;
            }else{
                //已存在通知存款子账户，默认存入活期
                System.out.println("已有通知存款子账户,请选择存为活期或整存整取");
                return -1;
            }

        }


        return 0;
    }

    /*public String openFixedAcct( SubAcct subAcct,String acctNo,
                                String Trans_no,
                                String sub_Id_type,
                                double deposit_amount,
                                Date openDate,
                                int days) {
        subAcct.setdbhelper(dbhelper);
        subAcct.setAcct_no(acctNo);
        subAcct.setSub_acct_no("2000" + Trans_no);
        subAcct.setSub_Id_type(sub_Id_type);
        subAcct.setSub_acct_balance(deposit_amount);
        subAcct.setOpen_date(openDate);
        subAcct.setDue_date_for_Fixed(days);//定存天数

        return subAcct.regSubAcct();
    }*/


//==================*******=============================
//		date = new Date(new java.util.Date().getTime());
//		tmpMoney = money + acct.gainBalance();
//		acct.setBalance(tmpMoney);

		/*AcctDetail detail = new AcctDetail();
        detail.setdbhelper(dbhelper);
		String detailNo = detail.createDetailNo();
		detail.setTrans_no(detailNo);
		detail.setAcct_no(acct_no);
		detail.setBalance_before(acct.gainBalance());
		detail.setBalance_after(acct.getBalance());
		detail.setTrans_type("006");
		detail.setTrans_amt(money);
		detail.setTrans_date(date);
		time = new Time(new java.util.Date().getTime());
		detail.setTrans_time(time);
		detail.setOperator_id("001");*/

//==================*******=============================

		/*if (dbhelper.insertInto(dbhelper, acct.deposit(), detail.addAcctDetail()) < 0) {
            setTrans_result("存钱失败!");
		}
		setTrans_result("存款成功");
		// 打印凭条
		setTrans_code(detailNo);
		setTrans_name("存钱");
		prtTransInfo(acct);*/


    @Override
    public void setDbhelper(ConnectMySql dbhelper) {
        this.dbhelper = dbhelper;
    }

    /**
     * 打印凭证
     */
    public void prtTransInfo(Acct acct) {
        System.out.println("交  易  码[" + trans_code + "]");
        System.out.println("交易名称[" + trans_name + "]");
        System.out.println("交易账户[" + acct_no + "]");
        System.out.println("交易金额[" + money + "]");
        System.out.println("账户余额[" + acct.gainBalance() + "]");
        System.out.println("交易时间[" + date + " " + time + "]");
    }
}
