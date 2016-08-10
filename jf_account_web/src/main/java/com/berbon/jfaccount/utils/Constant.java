package com.berbon.jfaccount.utils;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;


import com.berbon.util.yhbf.YhbfUtil;
import com.pay1pay.hsf.common.logger.Logger;
import com.pay1pay.hsf.common.logger.LoggerFactory;

@Component
public class Constant implements InitializingBean {

	private static Logger logger = LoggerFactory.getLogger(Constant.class);
	public static Map<Integer, Integer> orderStateMap = new HashMap<Integer, Integer>();
	@Autowired
	private MessageSource messageSource;// 得到spring的配置参数对象，可以通过此对象获取到一些参数

	@Value("${yhbfpath}")
	private String yhbfpath;
	@Value("${encKey}")
	private String encKey;
	@Value("${merId}")
	private String merId;
	@Value("${serviceId}")
	private String serviceId;
	@Value("${goodsType}")
	private Integer goodsType;
	@Value("${redictUrl}")
	private String redictUrl;
	@Value("${berbonsessionId}")
	private String berbonsessionId;
	@Value("${mobDataMerId}")
	private String mobDataMerId;
	@Value("${mobDataEncKey}")
	private String mobDataEncKey;
	@Value("${mobDataServiceId}")
	private String mobDataServiceId;
	@Value("${mobDatagoodsType}")
	private Integer mobDatagoodsType;
	@Value("${broadBandMerId}")
	private String broadBandMerId;
	@Value("${broadBandEncKey}")
	private String broadBandEncKey;
	@Value("${broadBandServiceId}")
	private String broadBandServiceId;
	@Value("${broadBandgoodsType}")
	private Integer broadBandgoodsType;
	@Value("${reportFtpHost}")
	private String reportFtpHost;
	@Value("${reportPort}")
	private Integer reportFtpPort;
	@Value("${reportFtpUsername}")
	private String reportFtpUsername;
	@Value("${reportFtpPwd}")
	private String reportFtpPwd;
	@Value("${reportRemotePath}")
	private String reportRemotePath;
	@Value("${reportLocalPath}")
	private String reportLocalPath;
	public String decKey;
	@Value("${emptyReportRedirectUrl}")
	public String emptyReportRedirectUrl;
	@Value("${mobMerId}")
	public String mobMerId;
	@Value("${mobBatchFilePath}")
	public String mobBatchFilePath;
	@Value("${etcIdCardPicPath}")
	public String etcIdCardPicPath;
	@Value("${etcdrivePicPath}")
	public String etcdrivePicPath;

	@Value("${etcLocalPath}")
	public String etcLocalPath;
	@Value("${fileSize}")
	public long fileSize;
	@Value("${sztLocalPath}")
	public String sztLocalPath;
	@Value("${sztCachePath}")
	public String sztCachePath;
	
	
	public static int platform = 20; // 平台web端

	/***
	 * 从资源文件中获取信息
	 * 
	 * @Description:
	 */
	public void init() {
		logger.info("yhbfpath=" + yhbfpath);
		decKey = YhbfUtil.decode(encKey, yhbfpath);
		mobDataEncKey = YhbfUtil.decode(mobDataEncKey, yhbfpath);
		broadBandEncKey = YhbfUtil.decode(broadBandEncKey, yhbfpath);
		initOrderStateMap();
		/*
		 * String path =Constant.class.getResource("/").getPath();
		 * msgSrvLongConnector = new MsgSrvLongConnector(path+"msgsrv.xml");
		 * msgSrvLongConnector.start();
		 */
	}

	/**
	 * public static int CHARGE_SUCCESS=10; //充值成功 public static int
	 * CHARGEING=11; //充值中 public static int NOTPAY=20; //未支付 public static int
	 * PAYING=21; //支付中 public static int CHARGE_FAILD=12; //充值失败 public static
	 * int REFUNDING=30; //退款中 public static int REFUND_SUCCESS=31; //退款成功
	 * public static int REFUND_FAILD=32; //退款失败 public static int
	 * CORRECTING=40; //冲正中 public static int CORRECT_SUCCESS=41; //冲正成功 public
	 * static int CORRECT_FAILD=42; //冲正失败 public static int CANEL=50; //撤销
	 */

	public void initOrderStateMap() {


	}

	/**
	 * 根据键名从资源文件中获取参数信息
	 * 
	 * @param key
	 * @return
	 * @Description:
	 */
	public String get(String key) {
		return messageSource.getMessage(key, null, null);
	}

	public String getYhbfpath() {
		return yhbfpath;
	}

	public void setYhbfpath(String yhbfpath) {
		this.yhbfpath = yhbfpath;
	}

	public String getEncKey() {
		return encKey;
	}

	public void setEncKey(String encKey) {
		this.encKey = encKey;
	}

	public String getMerId() {
		return merId;
	}

	public void setMerId(String merId) {
		this.merId = merId;
	}

	public String getDecKey() {
		return decKey;
	}

	public void setDecKey(String decKey) {
		this.decKey = decKey;
	}

	public String getServiceId() {
		return serviceId;
	}

	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}

	public Integer getGoodsType() {
		return goodsType;
	}

	public void setGoodsType(Integer goodsType) {
		this.goodsType = goodsType;
	}

	public String getRedictUrl() {
		return redictUrl;
	}

	public void setRedictUrl(String redictUrl) {
		this.redictUrl = redictUrl;
	}

	public String getBerbonsessionId() {
		return berbonsessionId;
	}

	public void setBerbonsessionId(String berbonsessionId) {
		this.berbonsessionId = berbonsessionId;
	}

	public String getMobDataMerId() {
		return mobDataMerId;
	}

	public void setMobDataMerId(String mobDataMerId) {
		this.mobDataMerId = mobDataMerId;
	}

	public String getMobDataEncKey() {
		return mobDataEncKey;
	}

	public void setMobDataEncKey(String mobDataEncKey) {
		this.mobDataEncKey = mobDataEncKey;
	}

	public String getMobDataServiceId() {
		return mobDataServiceId;
	}

	public void setMobDataServiceId(String mobDataServiceId) {
		this.mobDataServiceId = mobDataServiceId;
	}

	public Integer getMobDatagoodsType() {
		return mobDatagoodsType;
	}

	public void setMobDatagoodsType(Integer mobDatagoodsType) {
		this.mobDatagoodsType = mobDatagoodsType;
	}

	public String getBroadBandMerId() {
		return broadBandMerId;
	}

	public void setBroadBandMerId(String broadBandMerId) {
		this.broadBandMerId = broadBandMerId;
	}

	public String getBroadBandEncKey() {
		return broadBandEncKey;
	}

	public void setBroadBandEncKey(String broadBandEncKey) {
		this.broadBandEncKey = broadBandEncKey;
	}

	public String getBroadBandServiceId() {
		return broadBandServiceId;
	}

	public void setBroadBandServiceId(String broadBandServiceId) {
		this.broadBandServiceId = broadBandServiceId;
	}

	public Integer getBroadBandgoodsType() {
		return broadBandgoodsType;
	}

	public void setBroadBandgoodsType(Integer broadBandgoodsType) {
		this.broadBandgoodsType = broadBandgoodsType;
	}

	public String getReportFtpHost() {
		return reportFtpHost;
	}

	public void setReportFtpHost(String reportFtpHost) {
		this.reportFtpHost = reportFtpHost;
	}

	public Integer getReportFtpPort() {
		return reportFtpPort;
	}

	public void setReportFtpPort(Integer reportFtpPort) {
		this.reportFtpPort = reportFtpPort;
	}

	public String getReportFtpUsername() {
		return reportFtpUsername;
	}

	public void setReportFtpUsername(String reportFtpUsername) {
		this.reportFtpUsername = reportFtpUsername;
	}

	public String getReportFtpPwd() {
		return reportFtpPwd;
	}

	public void setReportFtpPwd(String reportFtpPwd) {
		this.reportFtpPwd = reportFtpPwd;
	}

	public String getReportRemotePath() {
		return reportRemotePath;
	}

	public void setReportRemotePath(String reportRemotePath) {
		this.reportRemotePath = reportRemotePath;
	}

	public String getReportLocalPath() {
		return reportLocalPath;
	}

	public void setReportLocalPath(String reportLocalPath) {
		this.reportLocalPath = reportLocalPath;
	}


	public String getEmptyReportRedirectUrl() {
		return emptyReportRedirectUrl;
	}

	public void setEmptyReportRedirectUrl(String emptyReportRedirectUrl) {
		this.emptyReportRedirectUrl = emptyReportRedirectUrl;
	}


	public String getMobMerId() {
		return mobMerId;
	}

	public void setMobMerId(String mobMerId) {
		this.mobMerId = mobMerId;
	}


	public String getMobBatchFilePath() {
		return mobBatchFilePath;
	}

	public void setMobBatchFilePath(String mobBatchFilePath) {
		this.mobBatchFilePath = mobBatchFilePath;
	}


	public String getEtcIdCardPicPath() {
		return etcIdCardPicPath;
	}

	public void setEtcIdCardPicPath(String etcIdCardPicPath) {
		this.etcIdCardPicPath = etcIdCardPicPath;
	}

	public String getEtcdrivePicPath() {
		return etcdrivePicPath;
	}

	public void setEtcdrivePicPath(String etcdrivePicPath) {
		this.etcdrivePicPath = etcdrivePicPath;
	}


	public String getEtcLocalPath() {
		return etcLocalPath;
	}

	public void setEtcLocalPath(String etcLocalPath) {
		this.etcLocalPath = etcLocalPath;
	}


	public long getFileSize() {
		return fileSize;
	}

	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}



	public String getSztLocalPath() {
		return sztLocalPath;
	}

	public void setSztLocalPath(String sztLocalPath) {
		this.sztLocalPath = sztLocalPath;
	}


	public String getSztCachePath() {
		return sztCachePath;
	}

	public void setSztCachePath(String sztCachePath) {
		this.sztCachePath = sztCachePath;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		init();
	}
}
