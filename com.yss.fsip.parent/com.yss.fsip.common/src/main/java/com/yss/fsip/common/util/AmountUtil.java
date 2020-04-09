package com.yss.fsip.common.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * 金额数字转换帮助类
 * 
 * @see
 * @author zhanglq
 * @version 1.0,2013-8-27
 * @since 1.0,2013-8-27
 */
public class AmountUtil {

	/*
	 * modify by liuzhongying 2017-6-7
	 * 运算过程中如果产生了科学计数法，导致最后一位也就是17为会丢失，如果17位为大于等于5的数，会导致计算的尾差
	 */
	private static final int DEF_DIV_SCALE = 17; // 使用double默认精度和vb相同，应该能满足要求

	/**
	 * 功能描述：人民币转成大写
	 * 
	 * @param value 需要转换的金额
	 * @return String 转换后的字符串
	 */
	public static String rmbToBigString(BigDecimal value) {
		char[] hunit = { '拾', '佰', '仟' }; // 段内位置表示
		char[] vunit = { '万', '亿' }; // 段名表示
		char[] decimal = { '角', '分', '厘', '毫', '丝' };
		char[] digit = { '零', '壹', '贰', '叁', '肆', '伍', '陆', '柒', '捌', '玖' }; // 数字表示
		String valStr = value.toPlainString(); // 转化成字符串
		String[] valueArr = valStr.split("\\.");
		String head = ""; // 整数部分
		String rail = "00"; // 小数部分
		if (valueArr.length > 1) {
			head = valueArr[0];
			rail = valueArr[1];
		} else {
			head = valueArr[0];
		}
		String prefix = ""; // 整数部分转化的结果
		String suffix = ""; // 小数部分转化的结果
		StringBuffer stringBuffer=new StringBuffer();
		// 处理小数部分（如果有）
		if (!rail.equals("00")) {
			int railZeroCount = 0;
			char[] chRail = rail.toCharArray();
			for (int i = 0; i < chRail.length; i++) {
				if (chRail[i] == '0') {
					railZeroCount++;
				} else {
					if (railZeroCount > 0) {
						//suffix += digit[0];
						stringBuffer.append(digit[0]);
					}
					railZeroCount = 0;
					//suffix += digit[chRail[i] - '0'];
					stringBuffer.append(digit[chRail[i] - '0']);
					//suffix += decimal[i];
					stringBuffer.append(decimal[i]);
				}
			}
		}
		suffix = stringBuffer.toString();
		if (!"0".equals(head)) {
			// 处理小数点前面的数
			char[] chDig = head.toCharArray(); // 把整数部分转化成字符数组
			byte zeroSerNum = 0; // 连续出现0的次数
			for (int i = 0; i < chDig.length; i++) { // 循环处理每个数字
				int idx = (chDig.length - i - 1) % Integer.parseInt("4"); // 取段内位置
				int vidx = (chDig.length - i - 1) / Integer.parseInt("4"); // 取段位置
				if (chDig[i] == '0') { // 如果当前字符是0
					zeroSerNum++; // 连续0次数递增
				} else {
					if (zeroSerNum > 0) {
						prefix += digit[0];
					}
					zeroSerNum = 0;
					prefix += digit[chDig[i] - '0'];
					if (idx > 0) {
						prefix += hunit[idx - 1];
					}
				}

				if (idx == 0 && zeroSerNum < Integer.parseInt("4")) {
					if (vidx > 0) {
						prefix += vunit[vidx - 1];
					}
				}
			}
			prefix += "元";
		}

		String resAIW = prefix + suffix;

		// 处理结果
		if (resAIW.equals("")) { // 零元
			resAIW = "零" + "元";
		}
		if (suffix.equals("")) { // ...元整
			resAIW += "整";
		}
		return resAIW; // 返回正确表示
	}

	/**
	 * 
	 * 数字格式化 //注意不一定四舍五入，有时候（偶数后的5）舍5！alex20041124解决这个问题！ 相当于vb中的format，不过只能是数字
	 * 
	 * @param number 数字
	 * @param format 格式
	 * @return String
	 * @author yaojie
	 * @date 2014-11-27
	 */
	public static final String formatNumber(BigDecimal number, String format) {
		int decs = 0;// 要保留的小数位数
		String format1 = format;
		if (format1.trim().length() == 0) {
			format1 = "#0.00";
		}
		int i = format1.indexOf(".");
		// alex20041124，先按照format中指定的小数位数对number进行round，再格式化
		if (i >= 0) {
			for (decs = ++i; decs < format1.length(); decs++) {
				if (format1.charAt(decs) != '0' && format1.charAt(decs) != '#') {
					break;
				}
			}
			decs -= i;
		}
		if (format1.indexOf("%") >= 0) {
			decs += Integer.parseInt("2");// 百分比
		}

		return (new DecimalFormat(format1)).format(round(number, decs));
	}
    /**
     * 数字格式化 //注意不一定四舍五入，有时候（偶数后的5）舍5！alex20041124解决这个问题！ 相当于vb中的format，不过只能是数字
     */
    public static final String formatNumber(double number,String format){
        int decs = 0;// 要保留的小数位数

        if(format.trim().length() == 0)
            format = "#0.00";
        int i = format.indexOf(".");
        // alex20041124，先按照format中指定的小数位数对number进行round，再格式化
        if(i >= 0){
            for(decs = ++i; decs < format.length(); decs++){
                if(format.charAt(decs) != '0' && format.charAt(decs) != '#')
                    break;
            }
            decs -= i;
        }
        if(format.indexOf("%") >= 0)
            decs += 2;// 百分比

        return (new DecimalFormat(format)).format(round(number,decs));
    }

	/**
	 * 格式化金额字符串
	 * 
	 * @param amount 金额字符串
	 * @return (-)#,###.00格式的金额字符串
	 * @author wxy
	 * @date 2015-2-12
	 */
	public static String thousandPart(String amount) {
		if (amount == null || "".equals(amount)) {
			return "";
		}
		if (amount.indexOf(".") < 0) {
			amount += ".00";
		} else if (amount.indexOf(".") > 0) {
			if (amount.substring(amount.indexOf(".") + 1, amount.length()).length() == 1) {
				amount += "0";
			}
		}
		String flag = "";

		if (amount.startsWith("-")) {
			amount = amount.substring(1);
			flag = "-";
		}
		int index = amount.indexOf(".");
		String deci = "";
		if (index > -1) {
			deci = amount.substring(index);
			amount = amount.substring(0, index);

		}

		char[] c = amount.toCharArray();
		amount = "";
		StringBuffer strBuffer=new StringBuffer();
		int j = 1;
		for (int i = c.length - 1; i >= 0; i--) {
			//amount += c[i];
			strBuffer.append(c[i]);
			if (j == 3) {
				//amount += ",";
				strBuffer.append(",");
				j = 1;
				continue;
			}
			j++;
		}
		amount = strBuffer.toString();
		if (amount.endsWith(",")) {
			amount = amount.substring(0, amount.length() - 1);
		}

		c = amount.toCharArray();
		amount = "";
		for (int i = c.length - 1; i >= 0; i--) {
			amount += c[i];
		}

		amount = flag + amount + deci;
		return amount;
	}

	/**
	 * 提供精确的小数位四舍五入处理。
	 * 
	 * @param v 需要四舍五入的数字
	 * @param lDecs 小数点后保留几位
	 * @param bTrunc 是否四舍五入，true：截位 false：四舍五入
	 * @return 四舍五入后的结果
	 */
	public static final BigDecimal round(BigDecimal v, int lDecs, boolean bTrunc) {
		return round(v, lDecs, bTrunc ? BigDecimal.ROUND_DOWN : BigDecimal.ROUND_HALF_UP);
	}

	/**
	 * 提供精确的小数位四舍五入处理。重载，支持BigDecimal所有保留位数方式
	 * @param v 需要四舍五入的数字
	 * @param lDecs 小数点后保留几位
	 * @param roundingMode 注意使用BigDecimal常量：ROUND_UP进位、ROUND_DOWN截位、ROUND_HALF_UP四舍五入 等
	 * @author wuyang
	 * @return
	 */
	public static final BigDecimal round(BigDecimal v, int lDecs, int roundingMode) {
		if (lDecs < 0) {
			return v;
		}
		return v.divide(BigDecimal.ONE, lDecs, roundingMode);
	}
	/**
	 * 
	 * 保留小数点后多少位
	 * 
	 * @param v 数值
	 * @param lDecs 小数点位数
	 * @return double
	 * @author yaojie
	 * @date 2014-11-27
	 */
	public static final BigDecimal round(BigDecimal v, int lDecs) {
		return round(v, lDecs, BigDecimal.ROUND_HALF_UP);
	}
	
	/**
	 * 提供向上调整小位
	 * @param v 数值
	 * @param lDecs 小数点位数
	 * @param bTrunc
	 * @return
	 */
	public static final BigDecimal roundup(BigDecimal v, int lDecs) {
		if (lDecs < 0) {
			return v;
		}
		BigDecimal one = new BigDecimal("1");
		return v.divide(one, lDecs,  BigDecimal.ROUND_UP);
	}

	/**
	 * 
	 * 自动分析sNum格式，解析成double类型返回 支持千分符，科学计数，百分数...前后空格，前导的+号和0 遇其它非法格式报错
	 * 
	 * @param sNum 格式
	 * @return BigDecimal
	 * @throws BizException 异常
	 * @author yaojie
	 * @date 2014-11-27
	 */
	public static BigDecimal toNumber(String sNum){
		int i = 0;
		String sNumFormate = sNum;
		DecimalFormat df = new DecimalFormat(sNumFormate.indexOf("%") > 0 ? "#,###.#%"
				: "#,###.#E0");

		try {
			sNumFormate = StringUtil.trim(sNumFormate);
			if (sNumFormate.length() > 1) {
				if (sNumFormate.charAt(0) == '0') { // 去前导连续0，注意只有一个0的情况
					if (sNumFormate.charAt(1) != '.') {
						for (i = 0; i < sNumFormate.length() - 1; i++) {
							if (sNumFormate.charAt(i) != '0') {
								break;
							}
						}
					}
				} else if (sNumFormate.charAt(0) == '+') {
					i = 1;
				}

			}
			return new BigDecimal(df
					.parse(i == 0 || sNumFormate.indexOf("%") > 0 ? sNumFormate : sNumFormate
							.substring(i)).doubleValue());

		} catch (Exception pe) {
			throw new RuntimeException("非法数值格式！", pe);
		}
	}

	/**
	 * 功能说明：提供精确的加法运算。
	 * 
	 * @param v1 被加数
	 * @param v2 加数
	 * @return 两个参数的和
	 */
	public static final BigDecimal add(BigDecimal v1, BigDecimal v2) {
		return add(v1, v2, new BigDecimal(0), new BigDecimal(0), new BigDecimal(0), new BigDecimal(
				0), 2);
	}

	/**
	 * 功能说明：提供精确的加法运算。
	 * 
	 * @param v1 被加数
	 * @param v2 加数
	 * @param v3 加数
	 * @return 三个参数的和
	 */
	public static final BigDecimal add(BigDecimal v1, BigDecimal v2, BigDecimal v3) {
		return add(v1, v2, v3, new BigDecimal(0), new BigDecimal(0), new BigDecimal(0), 3);
	}

	/**
	 * 功能说明：提供精确的加法运算。
	 * 
	 * @param v1 被加数
	 * @param v2 加数
	 * @param v3 加数
	 * @param v4 加数
	 * @return 四个参数的和
	 */
	public static final BigDecimal add(BigDecimal v1, BigDecimal v2, BigDecimal v3, BigDecimal v4) {
		return add(v1, v2, v3, v4, new BigDecimal(0), new BigDecimal(0), 4);
	}

	/**
	 * 功能说明：提供精确的加法运算。
	 * 
	 * @param v1 被加数
	 * @param v2 加数
	 * @param v3 加数
	 * @param v4 加数
	 * @param v5 加数
	 * @return 五个参数的和
	 */
	public static final BigDecimal add(BigDecimal v1, BigDecimal v2, BigDecimal v3, BigDecimal v4,
			BigDecimal v5) {
		return add(v1, v2, v3, v4, v5, new BigDecimal(0), 5);
	}

	/**
	 * 功能说明：提供精确的加法运算。
	 * 
	 * @param v1 被加数
	 * @param v2 加数
	 * @param v3 加数
	 * @param v4 加数
	 * @param v5 加数
	 * @param v6 加数
	 * @return 六个参数的和
	 */
	public static final BigDecimal add(BigDecimal v1, BigDecimal v2, BigDecimal v3, BigDecimal v4,
			BigDecimal v5, BigDecimal v6) {
		return add(v1, v2, v3, v4, v5, v6, 6);
	}

	/**
	 * 功能说明：提供精确的加法运算(此方法为底层调用方法)。
	 * 
	 * @param v1 被加数
	 * @param v2 加数
	 * @param v3 加数
	 * @param v4 加数
	 * @param v5 加数
	 * @param v6 加数
	 * @param n 参数个数
	 * @return n个参数的和
	 */
	private static final BigDecimal add(BigDecimal v1, BigDecimal v2, BigDecimal v3, BigDecimal v4,
			BigDecimal v5, BigDecimal v6, int n) {
		switch (n) {
			case 2:
				return v1.add(v2);
		}
		switch (n) {
			case 3:
				return v1.add(v2).add(v3);
			case 4:
				return v1.add(v2).add(v3).add(v4);
			case 5:
				return v1.add(v2).add(v3).add(v4).add(v5);
			case 6:
				return v1.add(v2).add(v3).add(v4).add(v5).add(v6);
			default:
				return BigDecimal.ZERO;
		}
	}

	/**
	 * 功能说明：提供精确的减法运算。
	 * 
	 * @param v1 被减数
	 * @param v2 减数
	 * @return 两个参数的差
	 */
	public static final BigDecimal sub(BigDecimal v1, BigDecimal v2) {
		return add(v1, mul(v2, new BigDecimal(-1)), new BigDecimal(0), new BigDecimal(0),
				new BigDecimal(0), new BigDecimal(0), 2);
	}

//	public static final double sub(double v1, double v2) {
//		return add(DBUtil.getBigDecimal(v1), mul(DBUtil.getBigDecimal(v2), new BigDecimal(-1)),
//				new BigDecimal(0), new BigDecimal(0), new BigDecimal(0), new BigDecimal(0), 2)
//				.doubleValue();
//	}

	/**
	 * 功能说明：提供精确的减法运算。
	 * 
	 * @param v1 被减数
	 * @param v2 减数
	 * @param v3 减数
	 * @return 三个参数的差
	 */
	public static final BigDecimal sub(BigDecimal v1, BigDecimal v2, BigDecimal v3) {
		return add(v1, mul(v2, new BigDecimal(-1)), mul(v3, new BigDecimal(-1)), new BigDecimal(0),
				new BigDecimal(0), new BigDecimal(0), 3);
	}

	/**
	 * 功能说明：提供精确的减法运算。
	 * 
	 * @param v1 被减数
	 * @param v2 减数
	 * @param v3 减数
	 * @param v4 减数
	 * @return 四个参数的差
	 */
	public static final BigDecimal sub(BigDecimal v1, BigDecimal v2, BigDecimal v3, BigDecimal v4) {
		return add(v1, mul(v2, new BigDecimal(-1)), mul(v3, new BigDecimal(-1)),
				mul(v4, new BigDecimal(-1)), new BigDecimal(0), new BigDecimal(0), 4);
	}

	/**
	 * 功能说明：提供精确的减法运算。
	 * 
	 * @param v1 被减数
	 * @param v2 减数
	 * @param v3 减数
	 * @param v4 减数
	 * @param v5 减数
	 * @return 五个参数的差
	 */
	public static final BigDecimal sub(BigDecimal v1, BigDecimal v2, BigDecimal v3, BigDecimal v4,
			BigDecimal v5) {
		return add(v1, mul(v2, new BigDecimal(-1)), mul(v3, new BigDecimal(-1)),
				mul(v4, new BigDecimal(-1)), mul(v5, new BigDecimal(-1)), new BigDecimal(0), 5);
	}

	/**
	 * 功能说明：提供精确的减法运算。
	 * 
	 * @param v1 被减数
	 * @param v2 减数
	 * @param v3 减数
	 * @param v4 减数
	 * @param v5 减数
	 * @param v6 减数
	 * @return 六个参数的差
	 */
	public static final BigDecimal sub(BigDecimal v1, BigDecimal v2, BigDecimal v3, BigDecimal v4,
			BigDecimal v5, BigDecimal v6) {
		return add(v1, mul(v2, new BigDecimal(-1)), mul(v3, new BigDecimal(-1)),
				mul(v4, new BigDecimal(-1)), mul(v5, new BigDecimal(-1)),
				mul(v6, new BigDecimal(-1)), 6);
	}
	/**
	 * 提供精确的乘法运算.
	 */
	public static BigDecimal mul(BigDecimal v1, BigDecimal v2) {
		return adjustDouble(v1.multiply(v2));
	}

	/**
	 * 提供精确的乘法运算，并对运算结果截位.
	 * 
	 * @param scale 运算结果小数后精确的位数
	 */
	public static BigDecimal mul(BigDecimal v1, BigDecimal v2, int scale) {
		if (scale < 0) {
			throw new IllegalArgumentException("The scale must be a positive integer or zero");
		}
		return round(adjustDouble(v1.multiply(v2)), scale); // add
															// by
															// hyr
															// 20120626
	}

	public static final BigDecimal mul(BigDecimal v1, BigDecimal v2, BigDecimal v3) {
		return mul(v1, v2, v3, new BigDecimal(1), new BigDecimal(1), new BigDecimal(1), 3);
	}
	/**
	 * 
	 * 功能说明：提供精确的乘法运算(此方法为底层调用方法)。
	* @param v1 被乘数
	 * @param v2 乘数
	 * @param v3 乘数
	 * @param v4 乘数
	 * @return 参数的积
	 * @author  luotaiping
	 * @date    2015-8-6
	 */
	public static final BigDecimal mul(BigDecimal v1, BigDecimal v2, BigDecimal v3, BigDecimal v4) {
		return mul(v1, v2, v3, v4, new BigDecimal(1), new BigDecimal(1), 4);
	}
	/**
	 * 
	 * 功能说明：提供精确的乘法运算(此方法为底层调用方法)。
	 * @param v1 被乘数
	 * @param v2 乘数
	 * @param v3 乘数
	 * @param v4 乘数
	 * @param v5 乘数
	 * @return 参数的积
	 * @author  luotaiping
	 * @date    2015-8-6
	 */
	public static final BigDecimal mul(BigDecimal v1, BigDecimal v2, BigDecimal v3, BigDecimal v4, BigDecimal v5) {
		return mul(v1, v2, v3, v4, v5, new BigDecimal(1), 5);
	}


	/**
	 * 功能说明：提供精确的乘法运算(此方法为底层调用方法)。
	 * 
	 * @param v1 被乘数
	 * @param v2 乘数
	 * @param v3 乘数
	 * @param v4 乘数
	 * @param v5 乘数
	 * @param v6 乘数
	 * @param n 参数个数
	 * @return n个参数的积
	 */
	private static final BigDecimal mul(BigDecimal v1, BigDecimal v2, BigDecimal v3, BigDecimal v4,
			BigDecimal v5, BigDecimal v6, int n) {
		BigDecimal result = null;

		if(n==2){
			result = v1.multiply(v2);
		}

		switch (n) {
			case 6:
				result = v1.multiply(v2).multiply(v3).multiply(v4).multiply(v5).multiply(v6);
				break;
			case 5:
				result = v1.multiply(v2).multiply(v3).multiply(v4).multiply(v5);
				break;
			case 4:
				result = v1.multiply(v2).multiply(v3).multiply(v4);
				break;
			case 3:
				result = v1.multiply(v2).multiply(v3);
			default:
				break;
		}
		return adjustDouble(result);
	}

	/**
	 * 提供（相对）精确的除法运算.
	 * 
	 * @see #div(double, double, int)
	 */
	public static BigDecimal div(BigDecimal v1, BigDecimal v2) {
		return div(v1, v2, DEF_DIV_SCALE);
	}

	/**
	 * 提供（相对）精确的除法运算. 由scale参数指定精度，以后的数字四舍五入.
	 * 
	 * @param v1 被除数
	 * @param v2 除数
	 * @param scale 表示表示需要精确到小数点以后几位
	 */
	public static BigDecimal div(BigDecimal v1, BigDecimal v2, int scale) {
		if (scale < 0) {
			throw new IllegalArgumentException("The scale must be a positive integer or zero");
		}

		return v1.divide(v2, scale, BigDecimal.ROUND_HALF_UP);
	}

	/**
	 * 功能说明：Math.pow的结果最多可以有17位有效数字，本函数控制最多15位，和VB的double兼容
	 * 
	 * @param v1 double
	 * @param v2 double
	 * @return double
	 */
	public static final BigDecimal pow(BigDecimal v1, BigDecimal v2) {
		return adjustDouble(new BigDecimal(Math.pow(v1.doubleValue(), v2.doubleValue())));
	}

	/**
	 * 功能说明：把java产生的double值转换成15位最多有效数字的值，多于小数四舍五入，整数不处理
	 * 
	 * @param v1 double 要转换的参数值
	 * @return double
	 */
	public static final BigDecimal adjustDouble(BigDecimal v1) { // todo 考虑
		// E多少的问题还有div也可能存在问题

		String strRes = String.valueOf(v1).toUpperCase();
		int i, len = strRes.length();
		int e = strRes.lastIndexOf('E'); // 考虑科学记数法!
		int d = strRes.indexOf('.');

		if (len <= DEF_DIV_SCALE || d < 0)
			return v1;

		for (i = 0; i < len; i++)
			if (strRes.charAt(i) >= '1' && strRes.charAt(i) <= '9')
				break;
		i = len - (d > i ? i + 1 : i) - (e >= 0 ? len - e : 0);// 得出有效位数
		if (i >= DEF_DIV_SCALE) {
			if (e < 0)
				v1 = round(v1, len - d - 2 - i + DEF_DIV_SCALE);
			else
				// 带科学记数法的处理: 先处理E前面的部分，再附加E部分，再转换成double
				v1 = round(v1, e - d - 1 - Integer.parseInt(strRes.substring(e + 1))
						- (i - DEF_DIV_SCALE));
		}

		return v1;
	}

	/**
	 * 提供精确的小数位四舍五入处理.
	 * 
	 * @param v 需要四舍五入的数字
	 * @param scale 小数点后保留几位
	 */
	public static BigDecimal round(String v, int scale) {
		if (scale < 0) {
			throw new IllegalArgumentException("The scale must be a positive integer or zero");
		}
		BigDecimal b = new BigDecimal(v);
		BigDecimal one = new BigDecimal("1");
		return b.divide(one, scale, BigDecimal.ROUND_HALF_UP);
	}

	/**
	 * @description 考虑有些地方会用到多位相乘法，原方法采用多次调用二位数乘法，这里写一公用方法
	 *              适用场景：多位double数字相乘(采用累计相成方法)
	 * @param num 保留的小数位数
	 * @param dNum 待处理数据
	 * @return 多位相乘以后的结果 Double类型
	 */
	public static BigDecimal mul(int num, Object... dNum) {

		BigDecimal fDecimal = new BigDecimal("0");// 初始化一个空参
		BigDecimal sDecimal = null;
		for (int i = 0; i < dNum.length; i++) {
			if (i == 0) {
				fDecimal = new BigDecimal(String.valueOf(dNum[i]));// 对参数进行第一次赋值
			} else {
				sDecimal = new BigDecimal(String.valueOf(dNum[i]));
				fDecimal = fDecimal.multiply(sDecimal);// 记录累积值
			}
		}
		return adjustDouble(num == 0 ? fDecimal : fDecimal.setScale(num, BigDecimal.ROUND_HALF_UP));
	}

	/**
	 * 移动加权平均算法
	 * 
	 * @param dKcMoney 库存金额
	 * @param dKcAmout 库存数量
	 * @param dSaleAmount 卖出数量
	 * @param iCb8ws 中间小数位 ,一般为12位
	 * @return
	 */
	@Deprecated
	public static double curOutPrice(double dKcMoney, double dKcAmout, double dSaleAmount,
			int iCb8ws) {
		//BigDecimal dSalecb = new BigDecimal(0d);
		BigDecimal dSalecb = AmountUtil.mul(
				AmountUtil.div(new BigDecimal(dKcMoney), new BigDecimal(dKcAmout), iCb8ws),
				new BigDecimal(dSaleAmount), 2);
		return dSalecb.doubleValue();
	}

	/**
	 * 
	 * 返回接收double类型的参数，返回double的返回值
	 * 
	 * @param dKcMoney
	 * @param dKcAmout
	 * @param dSaleAmount
	 * @param paraMap
	 * @return
	 * @author chenwentao
	 * @date 2015-7-31
	 */
//	public static double curOutPrice(double dKcMoney, double dKcAmout, double dSaleAmount,
//			Map<String, String> paraMap) {
//		return curOutPrice(DBUtil.getBigDecimal(dKcMoney), DBUtil.getBigDecimal(dKcAmout),
//				DBUtil.getBigDecimal(dSaleAmount), paraMap).doubleValue();
//	}

	/**
	 * 
	 * 返回接收BigDecimal类型的参数，返回BigDecimal的返回值
	 * 
	 * @param dKcMoney
	 * @param dKcAmout
	 * @param dSaleAmount
	 * @param paraMap
	 * @return
	 * @author chenwentao
	 * @date 2015-7-31
	 */
//	public static BigDecimal curOutPrice(BigDecimal dKcMoney, BigDecimal dKcAmout,
//			BigDecimal dSaleAmount, Map<String, String> paraMap) {
//		if (dKcAmout.compareTo(dSaleAmount) == 0) {
//			return dKcMoney;
//		}
//		// 库存小于卖出,返回-1
//		if (dKcAmout.compareTo(dSaleAmount) == -1) {
//			return DBUtil.getBigDecimal(-1);
//		}
//		BigDecimal dSalecb = null;//new BigDecimal(0d);
//		String ydjqgs = paraMap.get(ParameterProgramKey.CODE_YDJQGS);
//		String ydjqblws = paraMap.get(ParameterProgramKey.CODE_YDJQBLWS);
//		int digit = 8;
//		if(StringUtil.isNotEmpty(ydjqblws)){
//			digit  = Integer.parseInt(paraMap.get(ParameterProgramKey.CODE_YDJQBLWS));
//		}
//		if ("true".equalsIgnoreCase(ydjqgs)) {
//			// 卖出成本=round(库存金额*round(卖出数量/库存数量,N),2) N为保留位数，默认8位
//						dSalecb = AmountUtil.mul(
//								dKcMoney,
//								AmountUtil.div(dSaleAmount, dKcAmout,
//								               digit), 2);
//		} else {
//			// 卖出成本=round(round(库存金额/库存数量,N)*卖出数量,2) N为保留位数，默认8位
//						dSalecb = AmountUtil.mul(
//								AmountUtil.div(dKcMoney, dKcAmout,digit),
//								dSaleAmount, 2);
//		}
//
//		return dSalecb;
//	}
	
	/**
	 * 
	 * 币种转换
	 * 注释根据code获取币种名称的方法
	 * 不能通过此方法进行转换.需要根据币种code查询币种信息表获取对应的名称
	 * 
	 * @param dKcMoney
	 * @param dKcAmout
	 * @param dSaleAmount
	 * @param paraMap
	 * @return
	 * 
	 * @date 2015-7-31
	 */
	/*
	public static String bzzh(String bz) {
		//bz=bz.trim();
		String bzxx="";
		if("HKD".equals(bz)){		
			bzxx="港币";
		}else if("USD".equals(bz)){
			bzxx="美元";
		}else if("CNY".equals(bz)){
			bzxx="人民币";
		}else if("AUD".equals(bz)){
			bzxx="澳币";
		}else if("CAD".equals(bz)){
			bzxx="加币";
		}else if("JPY".equals(bz)){
			bzxx="日圆";
		}else if("GBP".equals(bz)){
			bzxx="英镑";
		}else if("EUR".equals(bz)){
			bzxx="欧元";
		}else if("SGD".equals(bz)){
			bzxx="新加坡币";
		}
		return bzxx;
	}
	*/ 
	 
	public static String bzjc(String bz) {
		if(StringUtil.isNotEmpty(bz)){
			bz = bz.trim();
			//系统里存的是国际币种代码，除了CNY需要转换，其余都不需处理 2018-07-06
			if("CNY".equals(bz)){
				bz = "RMB";
			}
		}
		return bz;
	}
	
    public static final double round(double v,int lDecs){
        return round(v,lDecs,false);
    }
    /**
     * 提供精确的小数位四舍五入处理。
     * 
     * @param v 需要四舍五入的数字
     * @param lDecs 小数点后保留几位
     * @return 四舍五入后的结果
     */
    public static final double round(double v,int lDecs,boolean bTrunc){
        if(lDecs < 0)
            return v;

        BigDecimal b = new BigDecimal(Double.toString(v));
        BigDecimal one = new BigDecimal("1");

        return b.divide(one,lDecs,bTrunc?BigDecimal.ROUND_DOWN:BigDecimal.ROUND_HALF_UP).doubleValue();
    }
    
    /**
     * 将decimal转换为字符串，最后i位为小数位
     * @param decimal
     * @param i 小数位数
     * @author guochaolong
     * @return
     */
    public static String bigDecimalToString(BigDecimal decimal,int i){
        String str = decimal.toPlainString();
        int pointIndex = str.indexOf(".");
        if(pointIndex == -1){
            return str+"00";
        }else if(str.length() > pointIndex+i+1){
            throw new NumberFormatException("所给数字不符合要求，小数位数大于"+i);
        }else if(str.length() < pointIndex+i+1){
            str += "0";
        }
        return new StringBuffer(str).deleteCharAt(pointIndex).toString();
    }
    /**
     * 根据传进去的数字进行自增后返回字符串
     * @param num
     * @author LiKai 2018-05-22
     * @return 
     */
  //根据传进去的数字进行自增后返回字符串(用于广发出函中的函件编号)待实现
  	public String getOrderNumber(int num) {
  		String orderNumber = "000" + num;
  		if (orderNumber.length() < 4) {
  			orderNumber = orderNumber.substring(orderNumber.length() - 4);
  		} else {
  			orderNumber = orderNumber.substring(3);
  		}
  		num++;
  		return orderNumber;
  	}
    
	/**
   	 * 先处理为百位单位，再四舍五入
   	 * 
   	 * @param amount
   	 * @param values
   	 * @return
   	 */
   	public static BigDecimal rundToMillion(BigDecimal amount) {
   		return amount.divide(new BigDecimal(1000000)).setScale(0,
   				BigDecimal.ROUND_HALF_UP);
   	}
}
