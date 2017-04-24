package busi.validate;

/**
 * 校验类
 * 
 * @author 作者：邹斌
 * @2017年3月6日
 */
public class DataValidate {
	String[] id_type = new String[] { "111", // 身份证
			"112", // -临时身份证
			"113", // -户口簿
			"114", // -军官证
			"115", // -警官证
			"133", // -学生证
			"999"// -其它
	};

	/**
	 * 校验证件类型
	 * 
	 * @param id_type
	 * @return
	 */
	public boolean id_type_validate(String id_type) {
		for (int i = 0; i < id_type.length(); i++) {
			if (this.id_type[i].equals(id_type)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 证件号码校验
	 * 
	 * @param id_no
	 * @return
	 */
	public boolean id_no_validate(String id_no) {
		int len = 0;
		// 长度校验
		if ((id_no.length() == 18) || (id_no.length() == 15)) {
			// 校验非法字符
			char[] tmp = id_no.toCharArray();
			for (char c : tmp) {
				if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z')) {
					len++;
				}
			}
		}
		if (len == id_no.length()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 校验电话号码
	 * 
	 * @param tel_no
	 * @return
	 */
	public boolean tel_no_validate(String tel_no) {
		if (tel_no.length() >= 12) {
			return false;
		}
		char[] tmp = tel_no.toCharArray();
		for (char c : tmp) {
			if (c > '9' || c < '0') {
				return false;
			}
		}
		return true;
	}

	/**
	 * 校验地址
	 * 
	 * @param addr
	 * @return
	 */
	public boolean addr_validate(String addr) {
		if (addr.length() > 128) {
			return false;
		}
		return true;
	}

	/**
	 * 姓名验证
	 * 
	 * @param cust_name
	 * @return
	 */
	public boolean cust_name_validate(String cust_name) {
		if (cust_name.length() > 32) {
			return false;
		}
		return true;
	}

	/**
	 * 客户编号校验
	 * 
	 * @param cust_no
	 * @return
	 */
	public boolean cust_no_validate(String cust_no) {
		if (cust_no.length() != 8) {
			return false;
		}
		char[] tmp = cust_no.toCharArray();
		for (char c : tmp) {
			if (c > '9' || c < '0') {
				return false;
			}
		}
		return true;
	}

	/**
	 * 密码校验
	 * 
	 * @param acct_pwd
	 * @return
	 */
	public boolean acct_pwd_validate(String acct_pwd) {
		if (acct_pwd.length() != 6) {
			return false;
		}
		return true;
	}

	/**
	 * 账号校验
	 */
	public boolean acct_no_validate(String acct_no) {
		if (acct_no.length() > 24) {
			return false;
		}
		char[] tmp = acct_no.toCharArray();
		for (char c : tmp) {
			if (c > '9' || c < '0') {
				return false;
			}
		}
		return true;
	}

	/**
	 * 存款金额校验
	 */
	public boolean money_validate(double money) {
		if (money - 0.0 <= 0.000001) {
			return false;
		} else if (money - 100000000000.00 > 0.0000001) {
			return false;
		} else {
			return true;
		}
	}
}
