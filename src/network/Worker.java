package network;

import busi.doSql.Acct;
import busi.doSql.AcctDetail;
import busi.doSql.SubAcct;
import busi.model.KerlAcct;
import db.ConnectMySql;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

class Worker implements Runnable {
    private Socket client;
    private ConnectMySql dbhelper;
    private List<KerlAcct> kerlAccts;

    void setClient(Socket client) {
        this.client = client;
    }

    public void run() {
        try {
            DataInputStream dataInputStream = new DataInputStream(client.getInputStream());
            DataOutputStream dataOutputStream = new DataOutputStream(client.getOutputStream());

            while (true) {
                if (client.isClosed())
                    break;
                String message = dataInputStream.readUTF();
                System.out.println("strMsg:" + message);
                kerlAccts = parseKerlAcctXML(message);
                dataOutputStream.writeUTF("Your message[" + message + "] is received."); // 向客户端回消息
                dataOutputStream.flush();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.getStackTrace();
        }

      /*  Acct acct = new Acct();
        SubAcct subAcct = new SubAcct();
        AcctDetail acctDetail = new AcctDetail();
        acct.setdbhelper(dbhelper);
        subAcct.setdbhelper(dbhelper);
        acctDetail.setdbhelper(dbhelper);
        for (KerlAcct kerlAcct : kerlAccts) {//遍历拿到的KerlAcct List，向类中注入收到的数据
            setDataForPayer(kerlAcct, acct, subAcct, acctDetail);
        }*/
        //update 数据表：subAcct、acctDetail
    }

    /*解析xml*/
    private static List<KerlAcct> parseKerlAcctXML(String xml) {
        SAXReader saxReader = new SAXReader();
        StringReader reader = new StringReader(xml);
        Document document;
        List<KerlAcct> results = new ArrayList<>();
        try {
            document = saxReader.read(reader);
            Element root = document.getRootElement();
            List<Element> kerlAccts = root.elements("KerlAcct");
            for (Element kerlAcct : kerlAccts) {
                String dbtrAcct = kerlAcct.element("DbtrAcct").getText();
                String dbtrName = kerlAcct.element("DbtrName").getText();
                String cdtrAcct = kerlAcct.element("CdtrAcct").getText();
                String cdtrName = kerlAcct.element("CdtrName").getText();
                String amt = kerlAcct.element("Amt").getText();
                String remark = kerlAcct.element("Remark").getText();
                KerlAcct newKerlAcct = new KerlAcct();
                newKerlAcct.setDbtrAcct(dbtrAcct);
                newKerlAcct.setDbtrName(dbtrName);
                newKerlAcct.setCdtrAcct(cdtrAcct);
                newKerlAcct.setCdtrName(cdtrName);
                newKerlAcct.setAmt(amt);
                newKerlAcct.setRemark(remark);
                results.add(newKerlAcct);

//                newKerlAcct.doTrans()
            }
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return results;
    }

    /*付款方，流出，记借debit*/
    public void setDataForPayer(KerlAcct kerlAcct, Acct acct, SubAcct subAcct, AcctDetail acctDetail) {
        String dbtrAcct = kerlAcct.getDbtrAcct();
        String dbtrName = kerlAcct.getDbtrName();
        String cdtrAcct = kerlAcct.getCdtrAcct();
        String cdtrName = kerlAcct.getCdtrName();
        String strAmt = kerlAcct.getAmt();
        double amount = Double.parseDouble(strAmt);
        String remark = kerlAcct.getRemark();

        acct.setAcct_no(dbtrAcct);
        subAcct.setAcct_no(dbtrAcct);
        String demandAcctNo = subAcct.findDemandAcctNo(dbtrAcct);//找到活期子账号
        double totalBalanceAfter = acct.gainBalance();//记账前 总账余额
        double demandBalance = subAcct.findSubAcctAmount(demandAcctNo);//取得活期余额
        //活期余额应大于等于转账发生额，否则转账失败
        //verifyBalance();
        double demandBalanceAfter = demandBalance - amount;
        subAcct.setSub_acct_balance(demandBalanceAfter);

    }

    /*插入数据表，付款方子账户、总账户、明细表（ACCT_NO为付款方账号）*/
    public void insertDataForPayer() {
    }

    /*收款方，流入，记贷credit*/
    public void setDataForPayee() {
    }

    /*插入数据表，收款方子账户、总账户、明细表（ACCT_NO为收款方账号）*/
    public void insertDataForPayee() {
    }


    public ConnectMySql getDbhelper() {
        return dbhelper;
    }

    public void setDbhelper(ConnectMySql dbhelper) {
        this.dbhelper = dbhelper;
    }
}
