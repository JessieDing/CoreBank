package busi.trans;

import java.sql.Date;
import java.sql.Time;
import java.util.Calendar;
import java.util.Scanner;

import busi.BankTrans;
import busi.doSql.Acct;
import busi.doSql.AcctDetail;
import busi.doSql.AcctOperation;
import busi.doSql.SubAcct;
import busi.validate.DataValidate;
import db.ConnectMySql;

public class AccountCancellation extends BankTrans {
    private String id_type;// 证件类型111-身份证 112-临时身份证113-户口簿 114-军官证
    // 115-警官证133-学生证 999-其它
    private String id_no;// 证件号码
    private String acct_no;// 账号
    private String acct_pwd;// 账户密码
    private ConnectMySql dbhelper;
    private final static int REVOKED_STATUS = 2;// 状态 1-正常 2-销户 3-挂失 4-冻结

    @Override
    public void prtPrompt() {
        System.out.println("》》》销户《《《");
        System.out.println("请输入证件类型、证件号、账号、密码,格式如下:");
        System.out.println("@id_type@id_no@acct_no@acct_pwd");
    }

    @Override
    public int getInPut() {
        id_type = scn.next();
        if (id_type == null) {
            setTrans_result("获取账号类型失败");
            return -1;
        }
        id_no = scn.next();
        if (id_no == null) {
            setTrans_result("获取证件号码失败");
            return -1;
        }
        acct_no = scn.next();
        if (acct_no == null) {
            setTrans_result("获取账号失败");
            return -1;
        }
        acct_pwd = scn.next();
        if (acct_pwd == null) {
            setTrans_result("获取账户密码失败");
            return -1;
        }
        // 数据有效性校验
        if (dataInvalidate() != 0) {
            setTrans_result("输入数据合法性有误!");
            return -1;
        }
        return 0;
    }

    /**
     * 数据有效性验证
     *
     * @return
     */
    private int dataInvalidate() {
        DataValidate dv = new DataValidate();
        if (!dv.id_type_validate(id_type)) {
            return -1;
        }
        if (!dv.id_no_validate(id_no)) {
            return -1;
        }
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
        // 验证账户是否存在
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
            setTrans_result("当前账户状态不能销户");
            return -1;
        }
        if (acct.FreezeStatus()) {
            setTrans_result("当前账户状态不能销户");
            return -1;
        }
        // 密码验证
        int count = 1;
        while (!acct.gainPassWord()) {
            count++;
            System.out.println("账号或密码错误,请重新输入");
            acct_no = scn.next();
            if (acct_no == null) {
                setTrans_result("获取账号失败");
                return -1;
            }
            acct_pwd = scn.next();
            if (acct_pwd == null) {
                setTrans_result("获取账户密码失败");
                continue;
            }
            acct.setAcct_no(acct_no);
            acct.setAcct_pwd(acct_pwd);
            if (count >= 3) {
                setTrans_result("账号或密码错误3次,不能销户!");
                return -1;
            }
        }

        // 将这次操作写入账户操作表
        AcctOperation acctOpr = new AcctOperation();
        setOperationForAcctCancel(acctOpr);

        // 写入交易明细
        AcctDetail acctDetail = new AcctDetail();
        setDetail(acct, acctOpr, acctDetail);

        SubAcct subAcct = new SubAcct();
        subAcct.setAcct_no(acct_no);
        subAcct.setdbhelper(dbhelper);

        System.out.println("请输入需要撤销的账户类型：");
        System.out.println("001-活期账户（总账户）/ 002-整存整取账户 / 003-通知存款账户");
        Scanner scanner = new Scanner(System.in);
        String revokeType = scanner.next();
        //销户 - 总账户（活期账户）
        if (revokeType.equals("001")) {

            /*所有子账户balance归零，status转为2*/
            subAcct.revokeAllSubBalance();

            /*主账户balance归零*/
            acct.setAcct_status(REVOKED_STATUS);
            acct.setBalance(0.00);
            acctDetail.setBalance_after(0.0);
            if (dbhelper.insertIntoDBO(
                    dbhelper, acct.revokeAcct(), acctOpr.addAcctOperation(), acctDetail.addAcctDetail()
            ) < 0) {
                setTrans_result("销户失败!");
            }
            setTrans_result("销户成功");
        }

        //销户 - 定期账户
        if (revokeType.equals("002")) {
            revokeFixedAcct(subAcct);

            //跑一次总账户余额变化
            double acctTotalBal = acct.calcTotalBalance();
            acct.setBalance(acctTotalBal);
            String changeAcctBalance = acct.changeAcctBalance();
//            dbhelper.doUpdate(changeAcctBalance);

            acctDetail.setBalance_after(acctTotalBal);
            if (dbhelper.insertIntoDBO(
                    dbhelper, changeAcctBalance, acctOpr.addAcctOperation(), acctDetail.addAcctDetail()
            ) < 0) {
                setTrans_result("定存账户销户失败!");
            }
            setTrans_result("定存账户销户成功");
        }

        //销户 - 通知账户
        if (revokeType.equals("003")) {
            revokeCallAcct(subAcct);
            //跑一次总账户余额变化
            double acctTotalBal = acct.calcTotalBalance();
            acct.setBalance(acctTotalBal);
            String changeAcctBalance = acct.changeAcctBalance();
//            dbhelper.doUpdate(changeAcctBalance);

            acctDetail.setBalance_after(acctTotalBal);
            if (dbhelper.insertIntoDBO(
                    dbhelper, changeAcctBalance, acctOpr.addAcctOperation(), acctDetail.addAcctDetail()
            ) < 0) {
                setTrans_result("通知账户销户失败!");
            }
            setTrans_result("通知账户销户成功");
        }

//		**原方法**
        //销户将账户状态改为销户状态写入数据库
//		acct.setAcct_status(REVOKED_STATUS);
//		acct.setBalance(0.00);
//		if (dbhelper.insertIntoDBO(dbhelper,acct.revokeAcct(), acctOpr.addAcctOperation(), detail.addAcctDetail()) < 0) {
//			setTrans_result("取钱失败!");
//		}
//		setTrans_result("销户成功");
        return 0;
    }


    private void setDetail(Acct acct, AcctOperation acctOpr, AcctDetail acctDetail) {
        acctDetail.setdbhelper(dbhelper);
        acctDetail.setTrans_no(acctDetail.createDetailNo());
        acctDetail.setAcct_no(acct_no);
        acctDetail.setBalance_before(acct.gainBalance());
        acctDetail.setTrans_type(acctOpr.getOpr_type());
        acctDetail.setTrans_amt(acct.gainBalance());
        Date date = new Date(new java.util.Date().getTime());
        acctDetail.setTrans_date(date);
        acctDetail.setTrans_time(new Time(new java.util.Date().getTime()));
        acctDetail.setOperator_id("001");
    }

    public void setOperationForAcctCancel(AcctOperation acctOperation) {
        acctOperation.setDbhelper(dbhelper);
        Date date = new Date(new java.util.Date().getTime());
        acctOperation.setAcct_no(acct_no);
        acctOperation.setOpr_type("008");
        acctOperation.setOpr_date(date);
        acctOperation.setOpr_time(new Time(new java.util.Date().getTime()));
    }

    public void revokeFixedAcct(SubAcct subAcct) {
        Scanner scann = new Scanner(System.in);
        System.out.println("请输入要撤销的子账户账号：");//整存整取账户有多个，需要输入子账号进行销户
        String subAcctNo = scann.next();
        System.out.println("整存整取账户" + subAcctNo + "开始销户...");
        System.out.println("请输入销户时间，年月日以空格隔开，例如：2017 07 25");
        int year = scann.nextInt();
        int month = scann.nextInt();
        int day = scann.nextInt();
        Date closeDate = subAcct.setDate(year, month, day);//设置销户时间（转出定存款）
//            subAcct.setDate(2017,07,25);//设置销户时间
        int fixDepositdays = subAcct.findFixedDepositPeriod(subAcctNo);//取得定存期限
        Date openDate = subAcct.findDate(subAcctNo);//取得该笔定存开户日
        double amount = subAcct.findSubAcctAmount(subAcctNo);//取得该笔定存本金
        double interest = calcInterestsForRevokeFixedAcct(openDate, closeDate, fixDepositdays, amount);//计算该笔定存利息

        //该定存子账户的 利息+本金 转入 相应 活期子账户balance字段（活期子账户只有一个，用acctNo + 001找）
        String demandAcctNo = subAcct.findDemandAcctNo(acct_no);
        subAcct.setSub_acct_no(demandAcctNo);
        double demandAcctBalance = subAcct.gainBalance();//取得相应活期子账户余额
        double tmpMoney = demandAcctBalance + amount + interest;//将撤销的定存账户金额转移到活期子账户上
        subAcct.setSub_acct_balance(tmpMoney);
        String changeDemandAcctBalanceSQL = subAcct.depositSubAcct();
        dbhelper.doUpdate(changeDemandAcctBalanceSQL);

        //更改 该定存子账户balance=0,status=2
        subAcct.setSub_acct_no(subAcctNo);
        subAcct.setSub_acct_balance(0.00);
        subAcct.setAcct_status(2);
        String revokeFixAcctSQL = subAcct.revokeSubAcct();
        dbhelper.doUpdate(revokeFixAcctSQL);
    }

    public void revokeCallAcct(SubAcct subAcct) {
        String callAcctNo = subAcct.findCallAcctNo(acct_no);
        subAcct.setSub_acct_no(callAcctNo);
        System.out.println("通知存款账户" + callAcctNo + "开始销户...");
        Scanner scann = new Scanner(System.in);
        System.out.println("请输入销户时间，年月日以空格隔开，例如：2017 07 25");
        int year = scann.nextInt();
        int month = scann.nextInt();
        int day = scann.nextInt();
        Date closeDate = subAcct.setDate(year, month, day);//设置销户时间（转出通知存款）
//      subAcct.setDate(2017,07,25);//设置销户时间

        //取得通知存款开户日(上一计息
        Date openDate = subAcct.findDate(callAcctNo);

        //通知期限
        int callDay = subAcct.gainCallDay();

        //取得该笔通知本金
        double callAcctAmount = subAcct.findSubAcctAmount(callAcctNo);

        //计算该笔通知利息
        double interest = calcInterForCallAcct(openDate, closeDate, callDay, callAcctAmount);

        //该通知子账户的 利息+本金 转入 相应 活期子账户balance字段（活期子账户只有一个，用acctNo + 001找）
        String demandAcctNo = subAcct.findDemandAcctNo(acct_no);
        subAcct.setSub_acct_no(demandAcctNo);
        double demandAcctBalance = subAcct.gainBalance();//取得相应活期子账户余额
        double tmpMoney = demandAcctBalance + callAcctAmount + interest;//将撤销的定存账户金额转移到活期子账户上
        subAcct.setSub_acct_balance(tmpMoney);
        String changeDemandAcctBalanceSQL = subAcct.depositSubAcct();
        dbhelper.doUpdate(changeDemandAcctBalanceSQL);

        //更改 该通知子账户balance=0,status=2
        subAcct.setSub_acct_no(callAcctNo);
        subAcct.setSub_acct_balance(0.00);
        subAcct.setAcct_status(2);
        String revokeCallAcctSQL = subAcct.revokeSubAcct();
        dbhelper.doUpdate(revokeCallAcctSQL);
    }

    public double calcInterestsForRevokeFixedAcct(Date openDate, Date closeDate, int fixDepositTerm, double amount) {
        double inter = 0.00;
        Calendar calendar = Calendar.getInstance();
        long time1 = openDate.getTime();
        long time2 = closeDate.getTime();
        int actualDepositDays = new Long((time2 - time1) / (24 * 3600 * 1000)).intValue();//实际存款天数
        InterestCalculation interestCalculator = new InterestCalculation();

        //销户时定存期限刚好满足
        if (actualDepositDays == fixDepositTerm) {
            if (fixDepositTerm == 90) {
                inter = interestCalculator.calcThreeMonInter(amount);
//                return inter;
            }
            if (fixDepositTerm == 180) {
                inter = interestCalculator.calcSixMonInter(amount);
//                return inter;
            }
            if (fixDepositTerm == 360) {
                inter = interestCalculator.calcOneYearInter(amount);
//                return inter;
            }
            if (fixDepositTerm == 720) {
                inter = interestCalculator.calcTwoYearInter(amount);
//                return inter;
            }
            if (fixDepositTerm == 1080) {
                inter = interestCalculator.calcThreeYearInter(amount);
//                return inter;
            }
        }

        //销户时定存未到期，按活期利率计算
        if (actualDepositDays < fixDepositTerm) {
//......

        }

        //销户时已超过定存期限，超出时间按活期利率计算（积数计息）
        if (actualDepositDays < fixDepositTerm) {
//......

        }
        return inter;
    }

    public double calcInterForCallAcct(Date openDate, Date closeDate, int callDay, double amount) {
        double inter = 0.00;
        Calendar calendar = Calendar.getInstance();
        long time1 = openDate.getTime();
        long time2 = closeDate.getTime();
        int actualDepositDays = new Long((time2 - time1) / (24 * 3600 * 1000)).intValue();//实际存款天数
        InterestCalculation interestCalculator = new InterestCalculation();
        int calcAsDemandDays = actualDepositDays % callDay;
        int periodCount_7 = actualDepositDays / callDay;

        //销户时正好满足7天周期
        if (calcAsDemandDays == 0) {
            inter = interestCalculator.calc7DayCallInter(amount, periodCount_7);
        } else {
            //销户时不足7天周期
            double asDemandInter = interestCalculator.calcDemandInterPerDay(amount, calcAsDemandDays);
            double asCallInter = interestCalculator.calc7DayCallInter(amount, periodCount_7);
            inter = asCallInter + asDemandInter;
        }
        return inter;
    }

    @Override
    public void setDbhelper(ConnectMySql dbhelper) {
        this.dbhelper = dbhelper;
    }
}
