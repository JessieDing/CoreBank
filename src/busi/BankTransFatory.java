package busi;

import busi.trans.AccountCancellation;
import busi.trans.DrawMoney;
import busi.trans.OpenAcct;
import busi.trans.QueryAcctDetail;
import busi.trans.TransAddCust;
import busi.trans.TransDelCust;
import busi.trans.TransDeposit;
import busi.trans.TransEditCust;
import busi.trans.TransExistCust;
import busi.trans.TransFreeze;
import busi.trans.TransLoss;
import busi.trans.TransUnFreeze;
import busi.trans.TransUnLoss;

public class BankTransFatory {
	public BankTransInterFace creatBankTrans(String strTransCode) {
		if (strTransCode.trim().equals("1")) {// 新建客户
			return new TransAddCust();
		} else if (strTransCode.trim().equals("2")) {// 修改客户信息
			return new TransEditCust();
		} else if (strTransCode.trim().equals("3")) {// 注销客户
			return new TransDelCust();
		} else if (strTransCode.trim().equals("4")) {// 查询客户信息
			return new TransExistCust();
		} else if (strTransCode.trim().equals("5")) {// 查询客户信息
			return new OpenAcct();
		} else if (strTransCode.trim().equals("6")) {// 存款
			return new TransDeposit();
		} else if (strTransCode.trim().equals("7")) {// 取款
			return new DrawMoney();
		} else if (strTransCode.trim().equals("8")) {// 销户
			return new AccountCancellation();
		} else if (strTransCode.trim().equals("9")) {// 冻结
			return new TransFreeze();
		} else if (strTransCode.trim().equals("10")) {// 解冻
			return new TransUnFreeze();
		} else if (strTransCode.trim().equals("11")) {// 挂失
			return new TransLoss();
		} else if (strTransCode.trim().equals("12")) {// 解挂
			return new TransUnLoss();
		} else if (strTransCode.trim().equals("13")) {// 结息
			return null;
		} else if (strTransCode.trim().equals("14")) {// 交易明细查询
			return new QueryAcctDetail();
		}
		return null;
	}
}
