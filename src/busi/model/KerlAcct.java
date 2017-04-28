package busi.model;

public class KerlAcct {
    private String dbtrAcct;//付款人账号
    private String dbtrName;//付款人名称
    private String cdtrAcct;//收款人账号
    private String cdtrName;//收款人名称
    private String amt;//发生额
    private String remark;//摘要

    public void setDbtrAcct(String dbtrAcct) {
        this.dbtrAcct = dbtrAcct;
    }

    public String getDbtrAcct() {
        return dbtrAcct;
    }

    public void setDbtrName(String dbtrName) {
        this.dbtrName = dbtrName;
    }

    public String getDbtrName() {
        return dbtrName;
    }

    public void setCdtrAcct(String cdtrAcct) {
        this.cdtrAcct = cdtrAcct;
    }

    public String getCdtrAcct() {
        return cdtrAcct;
    }

    public void setCdtrName(String cdtrName) {
        this.cdtrName = cdtrName;
    }

    public String getCdtrName() {
        return cdtrName;
    }

    public void setAmt(String amt) {
        this.amt = amt;
    }

    public String getAmt() {
        return amt;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getRemark() {
        return remark;
    }
}
