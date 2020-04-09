package com.yss.fsip.common.util;//package com.yss.fsip.util;
//
//import com.yss.fsip.common.util.DateUtil;
//import com.yss.fsip.common.util.id.IDGenerator;
//import com.yss.fsip.exception.FSIPRuntimeException;
//import org.apache.commons.lang.StringUtils;
//
//import java.util.Date;
//
//
//
///**
// * 流水号生成器
// *
// * @author lenglinyong
// * @version 1.0, 2013-3-25
// * @since 1.0, 2013-3-25
// */
//public class SerialNoIDGenerator implements IDGenerator {
//
//	private String prefix = "DEFAULT";
//	private String format = FORMAT_PREFIXYYYYMMDD00000000000;
//	private Date date = DateUtil.stringtoDate(DateUtil.getCurrent(), "yyyy-MM-dd");
//
//	private int cacheSize = 1;
//
//	/**
//	 * yyyyMMdd+000000000000
//	 */
//	public static final String FORMAT_YYYYMMDD000000000000 = "%2$tY%2$tm%2$td%3$012d";
//
//	/**
//	 * prefix+yyyyMMdd+00000000000
//	 */
//	public static final String FORMAT_PREFIXYYYYMMDD00000000000 = "%1$s%2$tY%2$tm%2$td%3$011d";
//
//	public SerialNoIDGenerator(Date date, String prefix, String format) {
//
//		this.prefix = StringUtils.isEmpty(prefix) ? this.prefix : prefix;
//		this.date = (date == null) ? this.date : date;
//		this.format = StringUtils.isEmpty(format) ? this.format : format;
//	}
//
//	public SerialNoIDGenerator(Date date, String prefix) {
//
//		this.prefix = StringUtils.isEmpty(prefix) ? this.prefix : prefix;
//		this.date = (date == null) ? this.date : date;
//	}
//
//	public SerialNoIDGenerator(String prefix, String format) {
//
//		this.prefix = StringUtils.isEmpty(prefix) ? this.prefix : prefix;
//		this.format = StringUtils.isEmpty(format) ? this.format : format;
//	}
//
//	public SerialNoIDGenerator(String prefix) {
//
//		this.prefix = StringUtils.isEmpty(prefix) ? this.prefix : prefix;
//	}
//
//	public SerialNoIDGenerator(Date date) {
//
//		this.date = (date == null) ? this.date : date;
//	}
//
//	public SerialNoIDGenerator() {
//
//	}
//
//	public String nextId() throws FSIPRuntimeException {
//
//		return this.generate(this.date, this.prefix, this.cacheSize, this.format)[0];
//	}
//
//	public String[] nextId(int size) throws FSIPRuntimeException {
//
//		return this.generate(this.date, this.prefix, size, this.format);
//	}
//
//	private String[] generate(Date date, String prefix, int size, String format) throws FSIPRuntimeException {
//
//		if (prefix.length() >= 20) {
//			throw new FSIPRuntimeException("前缀长度超出限定长度20个字符.");
//		}
//		SerialNoService serialNoService = SerialNoService.getInstance();
//		String[] ids = serialNoService.generate(date, prefix, size, format);
//		return ids;
//	}
//}
