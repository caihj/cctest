package com.berbon.jfaccount.controller;

import com.berbon.jfaccount.commen.*;
import com.berbon.jfaccount.facade.pojo.*;
import com.berbon.jfaccount.utils.IpTool;
import com.berbon.jfaccount.utils.MyUtils;
import com.berbon.user.pojo.Users;
import com.berbon.util.String.StringUtil;
import com.pay1pay.hsf.common.logger.Logger;
import com.pay1pay.hsf.common.logger.LoggerFactory;
import com.sztx.pay.center.rpc.api.service.AccountRpcService;
import com.sztx.usercenter.rpc.api.domain.out.UserVO;
import com.sztx.usercenter.rpc.api.service.QueryUserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by chj on 2016/8/9.
 */

@Controller
@RequestMapping("transfer")
public class TransferController {


    private static Logger logger = LoggerFactory.getLogger(TransferController.class);

    private com.berbon.jfaccount.facade.AccountFacade accountFacade;

    @Autowired
    private com.sztx.se.rpc.dubbo.source.DynamicDubboClient dubboClient;

    private AccountRpcService accountRpcService;

    private QueryUserInfoService queryUserInfoService;

    @ModelAttribute
    public  void init() {
        accountFacade = dubboClient.getDubboClient("accountFacade");
        accountRpcService = dubboClient.getDubboClient("accountRpcService");
        queryUserInfoService = dubboClient.getDubboClient("queryUserInfoService");
    }


    @RequestMapping(value = "/checkUser" , method ={ RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public JsonResult checkUser(HttpServletRequest request){
        JsonResult result = new JsonResult();

        String userCode = request.getParameter("userCode");
        String amount = request.getParameter("amount");
        String note = request.getParameter("note");
        String phone = request.getParameter("phone");
        String realName = request.getParameter("realName");

        TransferCheckReq req = new TransferCheckReq();

        Users user = CheckLoginInterceptor.getUsers(request.getSession());
        if(user==null){
            return null;
        }

        req.setFromUserCode(user.getUserCode());
        req.setToUserCode(userCode);
        req.setAmount(Long.parseLong(amount));
        req.setPhone(phone);
        req.setRealName(realName);

        TransferCheckRsp rsp = accountFacade.checkCanTransferTo(req);

        result.setData(rsp);
        result.setResult(ResultAck.succ);

        return result;
    }

    /**
     * 创建转账订单
     * @return
     */
    @RequestMapping(value = "/transfer" , method = {RequestMethod.GET,RequestMethod.POST})
    public String CreateTransferOrder(HttpServletRequest request,ModelMap map,HttpServletRequest response) {

        logger.info("recv  request /account/transferInit");

        String userCode = request.getParameter("payee");
        String amount = request.getParameter("amount");
        String note = request.getParameter("remark");
        String phone = request.getParameter("payeeMob");
        String realName = request.getParameter("realName");

        String orderId = request.getParameter("orderId");

        TransferOrderInfo order;

        Users user = CheckLoginInterceptor.getUsers(request.getSession());
        if (user == null) {
            logger.error("系统错误");
            return null;
        }

        if(orderId==null || orderId.trim().isEmpty()==true) {

            TransferOrderCrtReq req = new TransferOrderCrtReq();


            req.setReference(IpTool.getIp(request));
            req.setFromUserCode(user.getUserCode());
            req.setToUserCode(userCode);
            req.setAmount(Long.parseLong(amount));
            req.setNote(note);
            req.setPhone(phone);
            req.setRealName(realName);

            TransferOrderCrtRsp rsp = accountFacade.createTransferOrder(req);
            if (rsp == null || rsp.getOrderInfo() == null) {
                logger.error("创建订单失败");
                map.put("errorCode", MyErrorCodeEnum.system_error.getErrCode());
                if(rsp!=null)
                    map.put("errorMsg",rsp.getMsg());
                else
                    map.put("errorMsg",MyErrorCodeEnum.system_error.getOutDesc());
                return ConstStr.error_page;
            }
            order = rsp.getOrderInfo();
            orderId = order.getOrderId();
        }else if(orderId!=null && orderId.trim().isEmpty()==false){
            order = accountFacade.queryTransferDetail(orderId);
            //判断状态，已支付跳到转账成功页
            if(order.getOrderState()==3){
               return "redirect:/account/transferResult.htm?tradeOrderNo="+order.getTradeOrderId();
            }
        }else{
            map.put("errorCode", MyErrorCodeEnum.param_eror.getErrCode());
            map.put("errorMsg",MyErrorCodeEnum.param_eror.getOutDesc());
            logger.error("参数错误");
            return ConstStr.error_page;
        }

        int balance = accountRpcService.queryBalance(user.getUserCode(), 8);

        map.put("tradeNo",order.getOrderId());
        map.put("payee",order.getRealName());
        map.put("remark", order.getAttach());
        map.put("totalFee", MyUtils.fen2yuan(order.getAmount()));
        map.put("balance",MyUtils.fen2yuan(balance));

        String hasMoney="0";

        if(balance >= order.getAmount()){
            hasMoney="1";
        }

        map.put("hasMoney", hasMoney);
        map.put("orderId", orderId);
        logger.info("转账订单号:" + orderId);

        return "/account/transfer";
    }


    /**
     * 支付转账订单
     * @return
     */
    @RequestMapping(value = "payTransfer")
    @ResponseBody
    public JsonResult payTransfer(HttpServletRequest request){

        JsonResult result = new JsonResult();

        String orderId = request.getParameter("orderId");
        String type = request.getParameter("type");
        String bindNo = request.getParameter("bindNo");
        String paypwd = request.getParameter("paypwd");


        if(StringUtil.isNull(orderId,type,paypwd)){
            result.setResult(ResultAck.para_error);
            return result;
        }

        if(!type.equals("1") && !type.equals("2")){
            result.setResult(ResultAck.para_error);
            return result;
        }

        if(type.equals("2") && (bindNo==null || bindNo.trim().isEmpty())){
            result.setResult(ResultAck.para_error);
            return result;
        }


        TransferOrderPayReq req =  new TransferOrderPayReq();

        req.setOrderId(orderId);
        req.setType(Integer.parseInt(type));
        req.setBindNo(bindNo);
        req.setPaypwd(paypwd);
        req.setIp(IpTool.getIp(request));

        TransferOrderPayResp resp = accountFacade.payTransferOrder(req);

        result.setResult(ResultAck.succ);
        result.setData(resp);

        return  result;
    }

    @RequestMapping(value = "/resendMsg", method ={ RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public JsonResult reSendMsg(HttpServletRequest request){

        String tradeOrderId = request.getParameter("tradeOrderId");

        JsonResult result  = new JsonResult();

        ReSendChargeValMsgRsp rsp = accountFacade.reSendQuickValMsg(tradeOrderId, BusOrderType.charge_order, IpTool.getIp(request));

        result.setResult(ResultAck.succ);
        result.setData(rsp);

        return result;
    }

}
