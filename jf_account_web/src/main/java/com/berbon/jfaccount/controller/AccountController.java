package com.berbon.jfaccount.controller;

import com.alibaba.fastjson.JSONObject;
import com.berbon.jfaccount.Service.SysService;
import com.berbon.jfaccount.commen.*;
import com.berbon.jfaccount.facade.AccountFacade;
import com.berbon.jfaccount.facade.common.PageResult;
import com.berbon.jfaccount.facade.pojo.ChargeOrderInfo;
import com.berbon.jfaccount.facade.pojo.NotifyOrderType;
import com.berbon.jfaccount.facade.pojo.TransferOrderInfo;
import com.berbon.jfaccount.facade.pojo.ValNotifyRsp;
import com.berbon.jfaccount.pojo.BankInfoDetail;
import com.berbon.jfaccount.pojo.CashRecord;
import com.berbon.jfaccount.pojo.ChargeRecord;
import com.berbon.jfaccount.pojo.TransferData;
import com.berbon.jfaccount.utils.MyUtils;
import com.berbon.user.pojo.Users;
import com.pay1pay.hsf.common.logger.Logger;
import com.pay1pay.hsf.common.logger.LoggerFactory;
import com.sztx.pay.center.rpc.api.domain.*;

import com.sztx.pay.center.rpc.api.domain.request.QueryOrderRequest;
import com.sztx.pay.center.rpc.api.domain.response.OrderRecharge;
import com.sztx.pay.center.rpc.api.domain.response.OrderRechargeList;
import com.sztx.pay.center.rpc.api.service.*;
import com.sztx.usercenter.rpc.api.domain.out.UserBaseInfoVO;
import com.sztx.usercenter.rpc.api.domain.out.UserVO;
import com.sztx.usercenter.rpc.api.service.QueryPayUserInfoService;
import com.sztx.usercenter.rpc.api.service.QueryUserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import com.sztx.pay.center.rpc.api.service.BankPayLimitRpcService;
/**
 * Created by chj on 2016/8/2.
 */
@Controller
@RequestMapping("account")
public class AccountController {

    private static Logger logger = LoggerFactory.getLogger(AccountController.class);

    @Autowired
    private com.sztx.se.rpc.dubbo.source.DynamicDubboClient dubboClient;

    private TradeRpcService tradeRpcService;

    private AccountRpcService accountRpcService;

    private QueryUserInfoService queryUserInfoService;

    private QueryService queryService;

    private BankRpcService bankRpcService;

    private BankPayLimitRpcService  bankPayLimitRpcService;

    private SettleRpcService settleRpcService;

    private AccountFacade accountFacade;

    private UserActFlowRpcService userActFlowRpcService;

    @Autowired
    private InitBean initBean;

    @Autowired
    private SysService sysService;

    @ModelAttribute
    public  void init(){
        try {
            tradeRpcService = dubboClient.getDubboClient("tradeRpcService");
            accountRpcService = dubboClient.getDubboClient("accountRpcService");
            queryUserInfoService = dubboClient.getDubboClient("queryUserInfoService");
            queryService = dubboClient.getDubboClient("queryService");
            bankRpcService = dubboClient.getDubboClient("bankRpcService");
            bankPayLimitRpcService = dubboClient.getDubboClient("bankPayLimitRpcService");
            settleRpcService = dubboClient.getDubboClient("settleRpcService");
            accountFacade = dubboClient.getDubboClient("accountFacade");
            userActFlowRpcService = dubboClient.getDubboClient("userActFlowRpcService");

        }catch (Exception e){
            logger.error("发生异常"+e);
        }
    }


    @RequestMapping(value = "rechargeResult" , method = { RequestMethod.POST, RequestMethod.GET})
     public String chargeSucc(HttpServletRequest request, ModelMap map){


        String tradeOrderNo = request.getParameter("tradeOrderNo");

        //查询订单
        ChargeOrderInfo order = accountFacade.queryChargeOrderInfo(tradeOrderNo);

        if(order!=null){

            int state  = order.getState();

            if(state==2){
                map.put("tradeAmount", MyUtils.fen2yuan(order.getAmount()).toString());
                return "/account/rechargeResult";
            }else if(state==1 || state==0 ){
                map.put("errorCode", "-1");
                map.put("errorMsg","订单未支付");
                return ConstStr.error_page;
            }else {
                map.put("errorCode", "-1");
                map.put("errorMsg","充值失败,请稍后再试，或联系客服");
                return ConstStr.error_page;
            }
        }else{
            map.put("errorCode","-1");
            map.put("errorMsg","未找到订单");
            return ConstStr.error_page;
        }

    }

    @RequestMapping(value = "rechargeResultNotify" , method = { RequestMethod.POST, RequestMethod.GET})
    public String chargeSuccNotify(HttpServletRequest request,ModelMap map){

        Map params = request.getParameterMap();

       //通知验签
        ValNotifyRsp rsp = accountFacade.valFrontNotify(params, NotifyOrderType.charge_notify);

        if(rsp.getCode().equals(ValNotifyRsp.CODE.succ)){
            map.put("tradeAmount", MyUtils.fen2yuan(rsp.getAmount()).toString());
         }else if(rsp.getCode() == ValNotifyRsp.CODE.fail || rsp.getCode() == ValNotifyRsp.CODE.exception){
            map.put("errorCode", "-1");
            map.put("errorMsg","充值失败,请稍后再试，或联系客服");
            return ConstStr.error_page;
        }

        return "/account/rechargeEBankResult";
    }

    @RequestMapping(value = "transferResult" , method = { RequestMethod.POST, RequestMethod.GET})
    public String transferResult(HttpServletRequest request , ModelMap map){

        String tradeOrderNo = request.getParameter("tradeOrderNo");
        TransferOrderInfo order = accountFacade.queryTransferOrderInfo(tradeOrderNo);

        if(order!=null){
            String realName = order.getRealName();
            if(realName==null || realName.trim().isEmpty()==true){
                realName = order.getToUserCode();
            }else{
                realName = MyUtils.markName(order.getRealName());
            }

            map.put("payeeName", realName);
            map.put("tradeAmount",  MyUtils.fen2yuan(order.getAmount()).toString());
            return "/account/transferResult";
        }else{
            map.put("errorCode", "-1");
            map.put("errorMsg","未找到订单");
            return ConstStr.error_page;
        }

    }


    @RequestMapping(value = "transferResultNotify" , method = { RequestMethod.POST, RequestMethod.GET})
    public String transferResultNotify(HttpServletRequest request,ModelMap map){


        Map<String, String[]> params = request.getParameterMap();

        //通知验签
        ValNotifyRsp rsp = accountFacade.valFrontNotify(params, NotifyOrderType.transfer_notify);

        if(rsp.getCode().equals(ValNotifyRsp.CODE.succ)){
            map.put("tradeAmount", MyUtils.fen2yuan(rsp.getAmount()).toString());
            map.put("payeeName",rsp.getPayeeName());
        }else if(rsp.getCode() == ValNotifyRsp.CODE.fail || rsp.getCode() == ValNotifyRsp.CODE.exception){
            map.put("errorCode", "-1");
            map.put("errorMsg","充值失败,请稍后再试，或联系客服");
            return ConstStr.error_page;
        }

        return "/account/transferEBankResult";
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
        List<BindCardInfo> cards = accountRpcService.queryBindCardList(user.getUserCode(), 1);

//        //筛选出快捷支付的银行卡
//        List<BindCardInfo> qPayCards = new ArrayList<>();
//        for(BindCardInfo card:cards){
//            //快捷支付
//            if(card.getBindType()==1){
//                qPayCards.add(card);
//            }
//        }
        if(cards!=null)
            pageMap.put("cardCount", cards.size());
        else
            pageMap.put("cardCount", 0);


        //fixme 账户余额
        int balance = accountRpcService.queryBalance(user.getUserCode(), 8);
        logger.info(user.getUserCode()+",余额"+balance);
        int freezeBalance = 0;
        //账户余额
        pageMap.put("actBalance", MyUtils.fen2yuan((balance + freezeBalance)).toString());
        pageMap.put("leftBalance", MyUtils.fen2yuan((balance)).toString() );
        pageMap.put("freezeBalance", MyUtils.fen2yuan(freezeBalance).toString());

        //用户最后登录信息,用户姓名
        UserVO userVO = queryUserInfoService.getUserInfo(user.getUserCode());

        pageMap.put("realName", userVO.getRealname());
        pageMap.put("userCode", userVO.getUserCode());
        pageMap.put("lastLoginTime", userVO.getLastLoginTime());
            //pageMap.put("isNeedDigitalCert",userVO);
        map.put("map",pageMap);

        return "/account/index";
    }

    /**
     * 充值
     */
    @RequestMapping(value = "/recharge")
    public String getpage2(HttpServletRequest request,ModelMap map){
        logger.info("recv  request account/recharge");

        Users user = CheckLoginInterceptor.getUsers(request.getSession());

        int balance = accountRpcService.queryBalance(user.getUserCode(), 8);

        String bindNo = request.getParameter("bindNo");

        UserVO userVO = queryUserInfoService.getUserInfo(user.getUserCode());
        map.put("balance", MyUtils.fen2yuan(balance));
        map.put("realName",userVO.getRealname());
        map.put("loginName",userVO.getUserCode());


        String makeIdNo =  userVO.getIdentityid().substring(0, 4)+"***********"+userVO.getIdentityid().substring(userVO.getIdentityid().length() - 3, userVO.getIdentityid().length());

        map.put("identityNo", makeIdNo);

        map.put("bindNo", bindNo);

        return "account/recharge";
    }


    /**
     * 转账
     * @return
     */
    @RequestMapping(value = "/transferInit" , method = RequestMethod.GET)
    public String getPage3() {
        logger.info("recv  request /account/transferInit");
        return "/account/transferInit";
    }


    /**
     * testJson
     * @return
     */
    @RequestMapping(value = "/testJson" , method = RequestMethod.GET)
    @ResponseBody
    public JsonResult testJson(HttpServletResponse  response) throws IOException {
        JsonResult result = new JsonResult();

        result.setResult(ResultAck.succ.getCode());
        result.setRetinfo(ResultAck.succ.getDesc());

        return result;
    }


    @RequestMapping(value = "/getSalt" , method = { RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public JsonResult getSalt(HttpServletRequest request){
        JsonResult result = new JsonResult();

        try {
            Users user = CheckLoginInterceptor.getUsers(request.getSession());
            QueryPayUserInfoService queryPayUserInfoService = dubboClient.getDubboClient("queryPayUserInfoService");
            UserBaseInfoVO vo = queryPayUserInfoService.getUser(user.getUserCode());
            String salt = vo.getSalt();

            result.setResult(ResultAck.succ);
            result.setData(salt);
        }catch (Exception e){
            result.setResult(ResultAck.fail);
            logger.error("发生异常:"+e);
        }

        return  result;
    }


    /**
     * 获取银行卡列表
     */
    @RequestMapping(value = "/bindList" ,method = { RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public JsonResult getBankList(HttpServletRequest request){

        JsonResult result = new JsonResult();

        String bindType = request.getParameter("bindType");

        Integer bindTypeI = 0;
        if( bindType!=null && bindType.trim().isEmpty()==false ){
           bindTypeI = Integer.parseInt(bindType);
        }

        try {
            Users user = CheckLoginInterceptor.getUsers(request.getSession());
            List<BindCardInfo> cards = accountRpcService.queryBindCardList(user.getUserCode(), bindTypeI);
            if(cards!=null){
                for(BindCardInfo card:cards){
                    String cardNo = card.getCardNo();
                    if(card.getCardType()==1 || card.getCardType()==2){
                        cardNo = "****"+cardNo.substring(cardNo.length()-4,cardNo.length());
                    }else {
                        int start = cardNo.length()-4;
                        if(start<0){
                            start = 0;
                        }
                        cardNo = "****"+cardNo.substring(start,cardNo.length());
                    }
                    card.setCardNo(cardNo);
                }
            }else{
                cards = new ArrayList<>();
            }
            result.setResult(ResultAck.succ.getCode());
            result.setRetinfo(ResultAck.succ.getDesc());
            result.setData(cards);
        }
        catch (Exception e){
            logger.error("发生异常" + e);
            e.printStackTrace();
            result.setResult(ResultAck.fail.getCode());
            result.setRetinfo(ResultAck.fail.getDesc());
        }
        return result;
    }

    /**
     * 查询转账记录
     */

    @RequestMapping(value = "/querytransRecord" ,method = { RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public JsonResult getTransferRecord(HttpServletRequest request){
        JsonResult result = new JsonResult();

        try {
            String pageNo = request.getParameter("pageNo");
            String pageSize = request.getParameter("pageSize");
            String startDate = request.getParameter("startTime");
            String endDate = request.getParameter("endTime");
            String transferOrderId =request.getParameter("transferOrderId");

            Users user = CheckLoginInterceptor.getUsers(request.getSession());

            int IpageNo = Integer.parseInt(pageNo);
            int IpageSize  = Integer.parseInt(pageSize);
            Date startD = null;
            Date endD = null;

            if(startDate!=null && startDate.trim().isEmpty()==false) {
                startD =  new SimpleDateFormat("yyyy-MM-dd").parse(startDate);
            }

            if(endDate!=null && endDate.trim().isEmpty()==false){
                endD = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(endDate+" 23:59:59");
            }



            if(transferOrderId!=null && transferOrderId.trim().isEmpty()==false){
                startD = null;
                endD = null;
            }

            PageResult<TransferOrderInfo> records = accountFacade.queryTransferOrder(IpageNo,IpageSize,startD,endD,transferOrderId,user.getUserCode());

            JSONObject json = new JSONObject();

            json.put("pageNo",pageNo);
            json.put("pageSize",pageSize);
            json.put("total",records.total);

            List<TransferData> f_records = new ArrayList<>();
            if(records.listData!=null){
                for(TransferOrderInfo t:records.listData){
                    TransferData transferData = new TransferData();
                    transferData.setAddTime(t.getCreateTime());
                    transferData.setTransferOrderId(t.getOrderId());

                    if(t.getPayType()==1){
                        transferData.setTransferTypeStr("余额转账");
                    }else if(t.getPayType()==2){
                        transferData.setTransferTypeStr("快捷支付");
                    }else {
                        transferData.setTransferTypeStr("");
                    }


                    String otherAccount;
                    String intOrOut;

                    if(t.getToUserCode().equals(user.getUserCode())){
                        otherAccount = t.getFromUserCode();
                        intOrOut = "in";
                    }else {
                        otherAccount = t.getToUserCode();
                        intOrOut = "out";
                    }

                    transferData.setOtherAccount(otherAccount);
                    transferData.setType(intOrOut);
                    transferData.setRemark(t.getAttach());
                    transferData.setTransferAmount(MyUtils.fen2yuan(t.getAmount()).toString());
                    transferData.setTransferStatusStr(t.getOrderStateDesc());

                    f_records.add(transferData);
                }
            }

            json.put("rows",f_records);
            result.setData(json);
            result.setResult(ResultAck.succ.getCode());
            result.setRetinfo(ResultAck.succ.getDesc());
        }
        catch (Exception e){
            logger.error("发生异常" + e);
            e.printStackTrace();
            result.setResult(ResultAck.fail.getCode());
            result.setRetinfo(ResultAck.fail.getDesc());
        }

        return result;
    }


    /**
     * 查询充值记录
     */

    @RequestMapping(value = "/chargeRecord" ,method = { RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public JsonResult getChargeRecord(HttpServletRequest request){

        JsonResult result = new JsonResult();

        try {
            String pageNo = request.getParameter("pageNo");
            String pageSize = request.getParameter("pageSize");
            String startDate = request.getParameter("startTime");
            String endDate = request.getParameter("endTime");

            Date startD = null;
            Date endD = null;

            if(startDate!=null && startDate.trim().isEmpty()==false) {
                startD =  new SimpleDateFormat("yyyy-MM-dd").parse(startDate);
            }

            if(endDate!=null && endDate.trim().isEmpty()==false){
                endD = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(endDate+" 23:59:59");
            }

            QueryOrderRequest queryReq = new QueryOrderRequest();
            queryReq.setPlatChannelId(initBean.channelId);
            Users user = CheckLoginInterceptor.getUsers(request.getSession());
            queryReq.setPayerAccountId(user.getUserCode());
            if(startD!=null)
                queryReq.setStartTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(startD));
            if(endD != null)
                queryReq.setEndTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(endD));


            OrderRechargeList records = queryService.findRecharge(queryReq, Integer.parseInt(pageNo), Integer.parseInt(pageSize));

            JSONObject json = new JSONObject();
            /**
             * pageNo:1,
             pageSize:10,
             total:100,
             rows
             */
            json.put("pageNo",pageNo);
            json.put("pageSize", pageSize);
            json.put("total", records.getTotal());

            List<ChargeRecord> f_records = new ArrayList<>();
            for(OrderRecharge t:records.getRows()){
                ChargeRecord  record = new ChargeRecord();

                record.setAddTime(t.getAddTime());
                record.setRechargeOrderId(t.getOrderId());
                record.setRechargeChannelStr(t.getRechargeChannelStr());
                record.setRechargeAmount(t.getRechargeAmount().doubleValue()+"");
                record.setRechargeStatusStr(t.getRechargeStatusStr());

                f_records.add(record);
            }
            json.put("rows",f_records);
            result.setData(json);
            result.setResult(ResultAck.succ.getCode());
            result.setRetinfo(ResultAck.succ.getDesc());
        }
        catch (Exception e){
            logger.error("发生异常" + e);
            e.printStackTrace();
            result.setResult(ResultAck.fail.getCode());
            result.setRetinfo(ResultAck.fail.getDesc());
        }

        return result;

    }

    /**
     * 查询资金流水
     * @return
     */
    @RequestMapping(value = "/cashRecord" ,method ={ RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public JsonResult querycashRecord(HttpServletRequest request){
        JsonResult result = new JsonResult();

        try {
            String pageNo = request.getParameter("pageNo");
            String pageSize = request.getParameter("pageSize");
            String startDate = request.getParameter("startTime");
            String endDate = request.getParameter("endTime");
            String tradeOrderNo = request.getParameter("tradeOrderNo");
            String type = request.getParameter("type");
            Date startD = null;
            Date endD = null;

            if(startDate!=null && startDate.trim().isEmpty()==false) {
                startD =  new SimpleDateFormat("yyyy-MM-dd").parse(startDate);
            }else{
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.YEAR, -1);
                startD = cal.getTime();
            }
            if(endDate!=null && endDate.trim().isEmpty()==false){
                endD = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(endDate+" 23:59:59");
            }else{
                Calendar cal2 = Calendar.getInstance();
                cal2.add(Calendar.DATE,1);
                endD = cal2.getTime();
            }

            UserActFlowRequest queryReq = new UserActFlowRequest();
            Users user = CheckLoginInterceptor.getUsers(request.getSession());
            queryReq.setUserId(user.getUserCode());
            queryReq.setPageNo(Integer.parseInt(pageNo));
            queryReq.setPageSize(Integer.parseInt(pageSize));
            queryReq.setStartDate(startD);
            queryReq.setEndDate(endD);
            queryReq.setChannelId(initBean.channelId);

            if(tradeOrderNo!=null && tradeOrderNo.trim().isEmpty()==false){
                queryReq.setOriginOrderNo(tradeOrderNo);

                queryReq.setStartDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2016-01-01 23:59:59"));

                Calendar cal2 = Calendar.getInstance();
                cal2.add(Calendar.DATE, 1);
                cal2.getTime();

                queryReq.setEndDate(cal2.getTime());
            }

            if(type!=null && type.trim().isEmpty()==false){
                if(type.equals("in")){

                }else if(type.equals("out")){

                }
            }

            UserActFlowResponse records = userActFlowRpcService.findUserActFlow(queryReq);


            JSONObject json = new JSONObject();

            json.put("pageNo", pageNo);
            json.put("pageSize", pageSize);
            if(records!=null){
                json.put("total", records.getTotal());
            }else{
                json.put("total", 0);
            }


            List<CashRecord> f_records = new ArrayList<>();
            if(records.getUserActFlowS()!=null) {
                for (UserActFlowResponse.UserActFlow t : records.getUserActFlowS()) {
                    CashRecord record = new CashRecord();
                    record.setPayDate(t.getTradeTime());
                    record.setTradeOrderNo(t.getOriginOrderNo());
                    record.setTradeOrderType(t.getTradeType());

                    String otherAccount = "";
                    String intOrOut = "";

                    record.setOtherAccount(otherAccount);

                    record.setAmount(MyUtils.fen2yuan(Math.abs(t.getTranAmount())).toString());
                    record.setBalance(MyUtils.fen2yuan(t.getActBalance()).toString());


                    if (t.getTranAmount() < 0) {
                        intOrOut = "out";
                    } else {
                        intOrOut = "in";
                    }
                    record.setType(intOrOut);
                    f_records.add(record);
                }
            }

            json.put("payFlows",f_records);

            result.setData(json);
            result.setResult(ResultAck.succ.getCode());
            result.setRetinfo(ResultAck.succ.getDesc());
        } catch (Exception e){
            logger.error("发生异常" + e);
            e.printStackTrace();
            result.setResult(ResultAck.fail.getCode());
            result.setRetinfo(ResultAck.fail.getDesc());
        }

        return result;
    }

    /**
     * 获取支持的银行卡列表
     */
    @RequestMapping(value = "/dcBankList" , method ={ RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public JsonResult  getDcBankList(HttpServletRequest request){

        JsonResult result = new JsonResult();

        try {

            //网银支付
            List<BankInfo> ebankDCSeq =null ;
            try {
                ebankDCSeq = bankRpcService.findBankList(1);
            }
            catch (Exception e){
                logger.error("异常"+e.getMessage());
            }
            List<BankInfoDetail> ebankDeail = new ArrayList<>();

            if(ebankDCSeq!=null){
                for(BankInfo info:ebankDCSeq){
                    if(info.getBankId().equals("1100000")){
                        continue;
                    }
                    BankPayLimitInfo limit = bankPayLimitRpcService.getPayLimitInfo(info.getBankId(),1);
                    ebankDeail.add(new BankInfoDetail(info,limit));
                }
            }

            //快捷支付
            List<BankInfo> expressSeq =null ;
            try {
                expressSeq = bankRpcService.findBankList(2);
            }catch (Exception e){
                logger.error("异常"+e.getMessage());
            }
            List<BankInfoDetail> expressSeqDetail = new ArrayList<>();
            if(expressSeq!=null) {
                for (BankInfo info : expressSeq) {
                    BankPayLimitInfo limit = bankPayLimitRpcService.getPayLimitInfo(info.getBankId(), 2);
                    expressSeqDetail.add(new BankInfoDetail(info, limit));
                }
            }

            List<BankInfoDetail>  expressCCSeq = new ArrayList<>();
            List<BankInfoDetail>  expressDCSeq = new ArrayList<>();
            for(BankInfoDetail bankinfo:expressSeqDetail){

                if(bankinfo.getCardType()!=null) {
                    switch (bankinfo.getCardType()) {
                        case 1:
                            expressDCSeq.add(new BankInfoDetail(bankinfo));
                            break;

                        case 2:
                            expressCCSeq.add(new BankInfoDetail(bankinfo));
                            break;

                        case 3:
                            expressDCSeq.add(new BankInfoDetail(bankinfo));
                            expressCCSeq.add(new BankInfoDetail(bankinfo));
                            break;

                        default:
                            break;
                    }
                }
            }

            Map<String,Object> banks = new HashMap<>();

            if(sysService.useCreditCard()==false){
                expressCCSeq = new ArrayList<>();
            }

            List<BankInfoDetail> unionPay = new ArrayList<>();
            {

                BankInfoDetail infoDetail = new BankInfoDetail();

                infoDetail.setBankId("1100000");
                infoDetail.setCardType(3);
                BankPayLimitInfo info = bankPayLimitRpcService.getPayLimitInfo(infoDetail.getBankId(),1);

                if(info!=null){
                    infoDetail.setBankName(info.getBankName());
                    if(info.getLimitInfos()!=null){
                        for(BankPayLimitInfo.LimitInfo t:info.getLimitInfos()){
                            infoDetail.limitInfo.add(new BankInfoDetail.LimitInfo(t));
                        }
                    }
                }
                unionPay.add(infoDetail);
            }

            banks.put("ebankDCSeq",ebankDeail);
            banks.put("expressCCSeq",expressCCSeq);
            banks.put("expressDCSeq",expressDCSeq);
            banks.put("unionpay",unionPay);
            banks.put("thirdPay",new ArrayList<>());

            result.setData(banks);
            result.setResult(ResultAck.succ.getCode());
            result.setRetinfo(ResultAck.succ.getDesc());
        }
        catch (Exception e){
            logger.error("发生异常" + e);
            e.printStackTrace();
            result.setResult(ResultAck.fail.getCode());
            result.setRetinfo(ResultAck.fail.getDesc());
        }
        return result;
    }

    /**
     * 获取联系人
     */
    @RequestMapping(value = "/getContact" , method ={ RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public JsonResult getContact(HttpServletRequest request){

        JsonResult result = new JsonResult();

        String loginName = request.getParameter("loginName");

        if(loginName==null || loginName.trim().isEmpty()==true){
            result.setResult(ResultAck.para_error.getCode());
            result.setRetinfo(ResultAck.para_error.getDesc());
            return result;
        }

        UserVO user = queryUserInfoService.getUserInfo(loginName);
        result.setResult(ResultAck.succ.getCode());
        result.setRetinfo(ResultAck.succ.getDesc());

        if(user!=null){
            JSONObject json = new JSONObject();
            json.put("loginName", user.getUserCode());

            String realName = MyUtils.markName(user.getRealname());

            json.put("realName",realName);
            json.put("isOk",true);
            result.setData(json);
        }else{

            JSONObject json = new JSONObject();

            json.put("isOk",false);
            result.setData(json);
        }
        return result;
    }

    /**
     *检查银行卡信息
     */
    @RequestMapping(value = "/cardBinCheck" , method ={ RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public JsonResult cardBinCheck(HttpServletRequest request){
        JsonResult result = new JsonResult();

        result.setResult(ResultAck.succ.getCode());
        result.setRetinfo(ResultAck.succ.getDesc());

        String cardno = request.getParameter("cardno");
        String swiftCode = request.getParameter("swiftCode");

        BankInfo info;
        try{
             info = bankRpcService.queryBankCardBin(cardno);
        }catch (Exception e){
            result.setResult(ResultAck.fail.getCode());
            result.setRetinfo(ResultAck.fail.getDesc());
            return result;
        }

        JSONObject json = new JSONObject();

        if(info!=null ){
            if(info.getSwiftCode().equals(swiftCode)){
                json.put("cardCanUse",true);
                json.put("errorMsg", "可用");
            }else{
                json.put("cardCanUse",false);
                json.put("errorMsg","卡号与所选银行不符");
                result.setData(json);
                return result;
            }
        }else{
            json.put("cardCanUse",false);
            json.put("errorMsg", "未找到卡信息");
            result.setData(json);
            return result;
        }

        Users user = CheckLoginInterceptor.getUsers(request.getSession());

        List<BindCardInfo> cards = accountRpcService.queryBindCardList(user.getUserCode(), 1);


        if(cards!=null) {
            for (BindCardInfo cardInfo : cards) {
                if (cardInfo.getCardNo().equals(cardno)) {
                    json.put("cardCanUse", false);
                    json.put("errorMsg", "此卡已绑定快捷支付,请换其它卡!");
                    result.setData(json);
                    return result;
                }
            }
        }
        json.put("cardCanUse",true);
        json.put("errorMsg", "可用");

        result.setData(json);

        return result;
    }

    /**
     *
     */
    @RequestMapping(value = "/queryFee" , method ={ RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public JsonResult queryFee(HttpServletRequest request){
        JsonResult result = new JsonResult();
        long amount;
        int type;
        int cardType;
        String bankId;
        try {
             type =Integer.parseInt(request.getParameter("type"));
             amount = Long.parseLong(request.getParameter("amount"));
             cardType = Integer.parseInt(request.getParameter("cardType"));
             bankId = request.getParameter("bankId");
        }catch (Exception e){
            result.setResult(ResultAck.para_error.getCode());
            result.setRetinfo(ResultAck.para_error.getDesc());
            return  result;
        }

        if( type < 1 || type >3){
            result.setResult(ResultAck.para_error.getCode());
            result.setRetinfo(ResultAck.para_error.getDesc());
            return  result;
        }

        Users user = CheckLoginInterceptor.getUsers(request.getSession());

        int tradeType = 0;

        if(type==1){
            tradeType = 2;
        }else  if(type==2){
            tradeType = 8 ;
        }else if(type==3){
            tradeType = 10;
        }

        long fee = settleRpcService.calculateHandlingFee(tradeType,1,user.getUserCode(),amount,initBean.channelId,bankId,cardType);
        JSONObject json = new JSONObject();
        json.put("fee",fee);

        result.setResult(ResultAck.succ);
        result.setData(json);

        return result;
    }

}
