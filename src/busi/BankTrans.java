package busi;

import java.util.Scanner;

public abstract class BankTrans implements BankTransInterFace {
	protected String trans_name;// 交易名称
	protected String trans_code;// 交易码
	protected String trans_result;// 处理结果
	protected Scanner scn;// 获取输入数据

	public BankTrans() {
		scn = new Scanner(System.in);
	}

	public void prtTransInfo() {
		System.out.println("交易码[" + trans_code + "]");
		System.out.println("交易名称[" + trans_name + "]");
	}

	/**
	 * 处理结果
	 */
	public String getTransResult() {
		return trans_result;

	}

	public String getTrans_result() {
		return trans_result;
	}

	public void setTrans_result(String trans_result) {
		this.trans_result = trans_result;
	}

	public String getTrans_name() {
		return trans_name;
	}

	public void setTrans_name(String trans_name) {
		this.trans_name = trans_name;
	}

	public String getTrans_code() {
		return trans_code;
	}

	public void setTrans_code(String trans_code) {
		this.trans_code = trans_code;
	}

}
