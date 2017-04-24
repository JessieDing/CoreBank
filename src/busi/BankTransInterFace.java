package busi;

import db.ConnectMySql;

/**
 * 银行交易接口
 * 
 * @author 作者：邹斌
 * @2017年3月6日
 */
public interface BankTransInterFace {
	public void prtPrompt();// 提示信息

	public int getInPut();// 获取数据

	public int doTrans() throws Exception;// 执行交易

	public String getTrans_result();// 处理结果

	// public void prtTicket();// 打印交易凭条

	public void setDbhelper(ConnectMySql dbhelper);//传入数据库连接

}
