package ui;

import java.util.Scanner;

import busi.BankTransFatory;
import busi.BankTransInterFace;
import db.ConnectMySql;

public class CoreBankMain {

	@SuppressWarnings({ "resource", "unused" })
	public static void main(String[] args) throws Exception {

		while (true) {
			prtMain();// 显示主界面
			Scanner s = new Scanner(System.in);
			String strTransCode = s.next();// 输入选择功能
			if (strTransCode.equalsIgnoreCase("q")) {// 结束功能
				System.out.println("结束");
				break;
			}
			BankTransInterFace currentTrans = null;// 接口
			BankTransFatory btf = new BankTransFatory();
			currentTrans = btf.creatBankTrans(strTransCode);// 选择功能
			if (currentTrans != null) {
				boolean flag;// 标记
				do {
					currentTrans.prtPrompt();// 打印提示信息
					flag = false;
					if (currentTrans.getInPut() != 0) {// 传入数据验证
						flag = true;
						System.out.println(currentTrans.getTrans_result());// 打印失败原因
					}
				} while (flag);
				ConnectMySql dbhelper = new ConnectMySql();// 数据库连接
				// 需要关闭自动提交事务功能?
				if (dbhelper == null) {
					System.out.println("打开数据库连接错误！请检查");
					continue;
				}
				currentTrans.setDbhelper(dbhelper); // 传入数据库连接
				if (currentTrans.doTrans() != 0) {// 调用执行方法
					System.out.println(currentTrans.getTrans_result());// 打印错误信息
				} else {
					System.out.println(currentTrans.getTrans_result());// 打印成功信息
				}
			}
		}
	}

	/**
	 * 界面
	 */
	public static void prtMain() {
		System.out.println("*********************************************************");
		System.out.println("*\t\t\t十一项目组银行核心系统\t\t\t*");
		System.out.println("*********************************************************");
		System.out.println("*客户管理>>\t\t\t\t\t\t*");
		System.out.println("*\t\t1-新建客户信息\t2-修改客户信息\t\t*");
		System.out.println("*\t\t3-注销客户\t\t4-客户信息查询\t\t*");
		System.out.println("*********************************************************");
		System.out.println("*账户交易>>\t\t\t\t\t\t*");
		System.out.println("*\t\t5-开户\t\t6-存款\t\t\t*");
		System.out.println("*\t\t7-取款\t\t8-销户\t\t\t*");
		System.out.println("*********************************************************");
		System.out.println("*账户管理>>\t\t\t\t\t\t*");
		System.out.println("*\t\t9-冻结\t\t10-解冻\t\t\t*");
		System.out.println("*********************************************************");
		System.out.println("*其他>>\t\t\t\t\t\t\t*");
		System.out.println("*\t\t11-挂失\t\t12-解挂\t\t\t*");
		System.out.println("*\t\t13-结息\t\t14-交易明细查询\t\t*");
		System.out.println("*********************************************************");
		System.out.println("*请输入您的交易码 ,按q退出<<*");
	}
}
