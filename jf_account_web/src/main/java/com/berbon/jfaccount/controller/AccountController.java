package com.berbon.jfaccount.controller;

import com.alibaba.fastjson.JSONObject;
import com.berbon.jfaccount.commen.CheckLoginInterceptor;
import com.berbon.jfaccount.commen.JsonResult;
import com.berbon.jfaccount.commen.ResultAck;
import com.berbon.user.pojo.Users;
import com.pay1pay.hsf.common.logger.Logger;
import com.pay1pay.hsf.common.logger.LoggerFactory;
import com.sztx.pay.center.rpc.api.domain.BindCardInfo;
import com.sztx.pay.center.rpc.api.domain.PayFlowRequest;
import com.sztx.pay.center.rpc.api.domain.PayFlowResponse;
import com.sztx.pay.center.rpc.api.domain.request.QueryOrderRequest;
import com.sztx.pay.center.rpc.api.domain.response.OrderRechargeList;
import com.sztx.pay.center.rpc.api.domain.response.OrderTransferList;
import com.sztx.pay.center.rpc.api.service.BankRpcService;
import com.sztx.pay.center.rpc.api.service.PayFlowRpcService;
import com.sztx.pay.center.rpc.api.service.QueryService;
import com.sztx.usercenter.rpc.api.domain.out.UserVO;
import com.sztx.usercenter.rpc.api.service.QueryUserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import com.sztx.pay.center.rpc.api.service.AccountRpcService;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by chj on 2016/8/2.
 */
@Controller
@RequestMapping("account")
public class AccountController {

    private static Logger logger = LoggerFactory.getLogger(AccountController.class);

    @Autowired
    private com.sztx.se.rpc.dubbo.source.DynamicDubboClient dubboClient;

    private AccountRpcService accountRpcService;

    private QueryUserInfoService queryUserInfoService;

    private QueryService queryService;

    private PayFlowRpcService payFlowRpcService;

    private BankRpcService bankRpcService;

    @ModelAttribute
    public  void init(){
        accountRpcService = dubboClient.getDubboClient("accountRpcService");
        queryUserInfoService = dubboClient.getDubboClient("queryUserInfoService");
        queryService = dubboClient.getDubboClient("queryService");
        payFlowRpcService = dubboClient.getDubboClient("payFlowRpcService");
        bankRpcService = dubboClient.getDubboClient("bankRpcService");
    }

    /**
     * 账户首页
     * @return
     */
    @RequestMapping(value = "/index" , method = RequestMethod.GET)
    public String getPage(ModelMap map,HttpServletRequest request){
        logger.info("recv  request /account/index");
        Users user = CheckLoginInterceptor.getUsers(request.getSession());

        Map pageMap = new HashMap();


        //查询银行卡列表
        List<BindCardInfo> cards = accountRpcService.queryBindCardList(user.getUserCode(), 0);

//        //筛选出快捷支付的银行卡
//        List<BindCardInfo> qPayCards = new ArrayList<>();
//        for(BindCardInfo card:cards){
//            //快捷支付
//            if(card.getBindType()==1){
//                qPayCards.add(card);
//            }
//        }
        pageMap.put("cardCount", cards.size());


        //fixme 账户余额
        int balance = accountRpcService.queryBalance(user.getUserCode(), 1);
        int freezeBalance = 0;
        //账户余额
        pageMap.put("actBalance", balance + freezeBalance);
        pageMap.put("leftBalance", balance);
        pageMap.put("freezeBalance", freezeBalance);

        //用户最后登录信息,用户姓名
        UserVO userVO = queryUserInfoService.getUserInfo(user.getUserCode());

        pageMap.put("realName", userVO.getRealname());
        pageMap.put("userCode", userVO.getUserCode());
        pageMap.put("lastLoginTime", userVO.getLastLoginTime());
            //pageMap.put("isNeedDigitalCert",userVO);


        return "/account/index";
    }

    /**
     * 充值
     */
    @RequestMapping(value = "/recharge")
    public String getpage2(){
        logger.info("recv  request account/recharge");
        return "account/recharge";
    }


    /**
     * 转账
     * @return
     */
    @RequestMapping(value = "/transferInit" , method = RequestMethod.GET)
    public String getPage3(){
        logger.info("recv  request /account/transferInit");
        return "/account/transferInit";
    }


    /**
     * 获取银行卡列表
     */
    @RequestMapping(value = "/bindList" ,method = RequestMethod.POST)
    @ResponseBody
    public String getBankList(HttpServletRequest request){

        JsonResult result = new JsonResult();

        try {
            Users user = CheckLoginInterceptor.getUsers(request.getSession());
            List<BindCardInfo> cards = accountRpcService.queryBindCardList(user.getUserCode(), 0);
            result.setResult(ResultAck.succ.getCode());
            result.setRetinfo(ResultAck.succ.getDesc());
            result.setData(cards);
        }
        catch (Exception e){
            logger.error("发生异常" + e);
            result.setResult(ResultAck.fail.getCode());
            result.setRetinfo(ResultAck.fail.getDesc());
        }
        return JSONObject.toJSONString(result);
    }

    /**
     * 查询转账记录
     */

    @RequestMapping(value = "/querytransRecord" ,method = RequestMethod.POST)
    @ResponseBody
    public String getTransferRecord(HttpServletRequest request){
        JsonResult result = new JsonResult();

        try {
            String pageNo = request.getParameter("pageNo");
            String pageSize = request.getParameter("pageSize");

            QueryOrderRequest queryReq = new QueryOrderRequest();
            Users user = CheckLoginInterceptor.getUsers(request.getSession());
            queryReq.setPayerAccountId(user.getUserCode());

            OrderTransferList records = queryService.findTransfer(queryReq, Integer.parseInt(pageNo), Integer.parseInt(pageSize));

            result.setData(records);
            result.setResult(ResultAck.succ.getCode());
            result.setRetinfo(ResultAck.succ.getDesc());
        }
        catch (Exception e){
            logger.error("发生异常" + e);
            result.setResult(ResultAck.fail.getCode());
            result.setRetinfo(ResultAck.fail.getDesc());
        }

        return JSONObject.toJSONString(result);
    }


    /**
     * 查询充值记录
     */

    @RequestMapping(value = "/chargeRecord" ,method = RequestMethod.POST)
    @ResponseBody
    public String getChargeRecord(HttpServletRequest request){

        JsonResult result = new JsonResult();

        try {
            String pageNo = request.getParameter("pageNo");
            String pageSize = request.getParameter("pageSize");

            QueryOrderRequest queryReq = new QueryOrderRequest();
            Users user = CheckLoginInterceptor.getUsers(request.getSession());
            queryReq.setPayerAccountId(user.getUserCode());

            OrderRechargeList records = queryService.findRecharge(queryReq, Integer.parseInt(pageNo), Integer.parseInt(pageSize));

            result.setData(records);
            result.setResult(ResultAck.succ.getCode());
            result.setRetinfo(ResultAck.succ.getDesc());
        }
        catch (Exception e){
            logger.error("发生异常" + e);
            result.setResult(ResultAck.fail.getCode());
            result.setRetinfo(ResultAck.fail.getDesc());
        }

        return JSONObject.toJSONString(result);

    }

    /**
     * 查询资金流水
     * @return
     */
    @RequestMapping(value = "/cashRecord" ,method = RequestMethod.POST)
    @ResponseBody
    public String querycashRecord(HttpServletRequest request){
        JsonResult result = new JsonResult();

        try {
            String pageNo = request.getParameter("pageNo");
            String pageSize = request.getParameter("pageSize");
            String startDate = request.getParameter("startTime");
            String endDate = request.getParameter("endTime");

            Date startD =  new SimpleDateFormat("yyyy-MM-dd").parse(startDate);
            Date endD = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(endDate+" 23:59:59");

            PayFlowRequest queryReq = new PayFlowRequest();
            Users user = CheckLoginInterceptor.getUsers(request.getSession());
            queryReq.setUserId(user.getUserCode());
            queryReq.setPageNo(Integer.parseInt(pageNo));
            queryReq.setPageSize(Integer.parseInt(pageSize));
            queryReq.setStartDate(startD);
            queryReq.setEndDate(endD);

            PayFlowResponse records = payFlowRpcService.findUserPayFlow(queryReq);
            result.setData(records);
            result.setResult(ResultAck.succ.getCode());
            result.setRetinfo(ResultAck.succ.getDesc());
        }
        catch (Exception e){
            logger.error("发生异常" + e);
            result.setResult(ResultAck.fail.getCode());
            result.setRetinfo(ResultAck.fail.getDesc());
        }

        return JSONObject.toJSONString(result);
    }

    /**
     * 获取支持的银行卡列表
     */
    @RequestMapping(value = "/dcBankList" , method = RequestMethod.GET)
    @ResponseBody
    public String  getDcBankList(HttpServletRequest request){

        JsonResult result = new JsonResult();

        try {





      //      bankRpcService.findBankList();

           // result.setData(records);
            result.setResult(ResultAck.succ.getCode());
            result.setRetinfo(ResultAck.succ.getDesc());
        }
        catch (Exception e){
            logger.error("发生异常" + e);
            result.setResult(ResultAck.fail.getCode());
            result.setRetinfo(ResultAck.fail.getDesc());
        }

        return JSONObject.toJSONString(result);

    }



}
