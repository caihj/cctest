package com.berbon.jfaccount.Service;

import com.alibaba.fastjson.JSONObject;
import com.berbon.jfaccount.Dao.MasterChildRelateDao;
import com.berbon.jfaccount.Dao.TransferOrderDao;
import com.berbon.jfaccount.comm.BusinessType;
import com.berbon.jfaccount.comm.ErrorCode;
import com.berbon.jfaccount.comm.InitBean;
import com.berbon.jfaccount.facade.pojo.AcquireTransferReq;
import com.berbon.jfaccount.facade.pojo.AcquireTransferRsp;
import com.berbon.jfaccount.facade.pojo.TransferOrderInfo;
import com.berbon.jfaccount.pojo.MasterChildRelate;
import com.berbon.jfaccount.util.UtilTool;
import com.sztx.pay.center.rpc.api.domain.TradeResponse;
import com.sztx.pay.center.rpc.api.domain.TransferRequest;
import com.sztx.pay.center.rpc.api.service.TradeRpcService;
import com.sztx.se.common.exception.BusinessException;
import com.sztx.se.rpc.dubbo.source.DynamicDubboClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by chj on 2016/11/22.
 *
 * 总账户-子账户自动转账service
 */
@Service
public class MCAutoTranserService {

    private static Logger logger = LoggerFactory.getLogger(MCAutoTranserService.class);

    @Autowired
    private MasterChildRelateDao dao;

    @Autowired
    private TransferOrderDao transferOrderDao;

    @Autowired
    private InitBean initBean;

    @Autowired
    private DynamicDubboClient dubboClient;

    public AcquireTransferRsp transfer(AcquireTransferReq req) {

        AcquireTransferRsp rsp = new AcquireTransferRsp();
        MasterChildRelate data = null;

        List<MasterChildRelate> list = dao.get(req.userCode);
        if (list.size() == 0) {
            rsp.ack = AcquireTransferRsp.ACK.NOT_CHILD_ACCOUNT;
            rsp.msg = "不是子账号";
            return rsp;
        }

        if (list.size() > 1) {
            logger.error("数据异常");
            rsp.ack = AcquireTransferRsp.ACK.FAIL;
            rsp.msg = "数据异常";
            return rsp;
        }

        data = list.get(0);

        //统计月限额

        Calendar cal = Calendar.getInstance();


        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        Date begin = cal.getTime();

        cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        Date end = cal.getTime();

        Long totalAmount = transferOrderDao.getTotalAmount(begin, end, data.getMasterUserCode(), data.getChildUserCode(), BusinessType.type_2020.type + "");

        logger.info("当月交易总额:{}", totalAmount);

        if (totalAmount + req.amount > data.getMaxTransMonth()) {
            logger.error("当月最大交易额超限");
            rsp.ack = AcquireTransferRsp.ACK.FAIL;
            rsp.msg = "当月最大交易额超限";
            return rsp;
        }

        if (req.amount > data.getMaxSingleTrans()) {
            logger.error("当月最大交易额超限");
            rsp.ack = AcquireTransferRsp.ACK.FAIL;
            rsp.msg = "单笔交易额超限";
            return rsp;
        }


        //创建转账订单
        TransferOrderInfo orderInfo = new TransferOrderInfo();

        orderInfo.setOrderId(UtilTool.generateTransferOrderId());
        orderInfo.setFromUserCode(data.getMasterUserCode());
        orderInfo.setToUserCode(data.getChildUserCode());
        orderInfo.setAmount(req.amount);

        orderInfo.setReceiverType(1);

        orderInfo.setAttach("业务订单号:" + req.bussOrderId);

        orderInfo.setCreateTime(new Date());

        orderInfo.setChannelId(initBean.channelId);
        orderInfo.setBusinessType(BusinessType.type_2014.type + "");
        //orderInfo.setReference(req.getReference());
        orderInfo.setOrderState(TransferOrderDao.OrderState.wait_pay.state);
        orderInfo.setOrderStateDesc(TransferOrderDao.OrderState.wait_pay.desc);
        //orderInfo.setCreateUserCode(req.getFromUserCode());

        orderInfo.setBusinessType(BusinessType.type_2020.type + "");
        orderInfo.setCreateUserCode(req.userCode);
        orderInfo = transferOrderDao.createOrder(orderInfo);



        //执行转账
        TransferRequest request = new TransferRequest();
        request.setOrderId(orderInfo.getOrderId());
        request.setPayerUserId(orderInfo.getFromUserCode());
        request.setPayeeUserId(orderInfo.getToUserCode());
        request.setPayType(1);
        request.setReceiverType(orderInfo.getReceiverType());
        request.setAmount(orderInfo.getAmount() + "");
        request.setAttach(orderInfo.getAttach());
        request.setSrcChannel("1");
        request.setReturnUrl(initBean.transferNotifyUrl);
        request.setNotifyUrl(initBean.transferbackNotifyUrl);
        request.setOrderTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(orderInfo.getCreateTime()));

        request.setIsUsePwd(orderInfo.getIsUsePwd() + "");
        request.setChannelId(orderInfo.getChannelId());
        request.setBusinessType(BusinessType.type_2014.type + "");
        request.setSignType("MD5");
        request.setSrcIp(req.ip);


        String sign = SignService.CalSign(request, initBean.newPayKey);
        request.setSign(sign);

        TradeRpcService tradeRpcService = dubboClient.getDubboClient("tradeRpcService");
        if (tradeRpcService == null) {
            logger.error("获取TradeRpcService 失败!");
            rsp.ack = AcquireTransferRsp.ACK.FAIL;
            rsp.msg="获取TradeRpcService 失败";
            return rsp;
        }

        try {
            TradeResponse resp = tradeRpcService.transfer(request);

            switch (resp.getResultCode()){
                case "2":
                    throw new BusinessException("不可预知的转账状态");
                case "3":
                    rsp.ack = AcquireTransferRsp.ACK.SUCCESS;
                    rsp.msg = "成功";
                    rsp.transferOrderId = request.getOrderId();
                    rsp.amount = orderInfo.getAmount();
                    break;
                default:
                    rsp.ack = AcquireTransferRsp.ACK.FAIL;
                    rsp.msg = resp.getResultMsg();
            }

            //更新转账订单状态
            TransferOrderDao.OrderState state = TransferOrderDao.GetState(resp.getResultCode());
            transferOrderDao.update(orderInfo.getId(), state.state, state.desc, resp.getTradeOrderId(), request.getPayType(), request.getBindNo());

        } catch (Exception e) {
            logger.error("转账异常");
            rsp.ack = AcquireTransferRsp.ACK.FAIL;
            rsp.msg = e.getMessage();
        }

        return  rsp;
    }
}
