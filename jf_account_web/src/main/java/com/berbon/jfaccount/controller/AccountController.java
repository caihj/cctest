package com.berbon.jfaccount.controller;

import com.alibaba.fastjson.JSONObject;
import com.berbon.jfaccount.facade.AccountFacade;
import com.berbon.jfaccount.facade.common.PageResult;
import com.berbon.jfaccount.facade.pojo.TransferOrderInfo;
import com.berbon.jfaccount.pojo.BankInfoDetail;
import com.berbon.jfaccount.commen.CheckLoginInterceptor;
import com.berbon.jfaccount.commen.JsonResult;
import com.berbon.jfaccount.commen.ResultAck;
import com.berbon.jfaccount.pojo.CashRecord;
import com.berbon.jfaccount.pojo.ChargeRecord;
import com.berbon.jfaccount.pojo.TransferData;
import com.berbon.user.pojo.Users;
import com.berbon.util.String.StringUtil;
import com.pay1pay.hsf.common.logger.Logger;
import com.pay1pay.hsf.common.logger.LoggerFactory;
import com.sztx.pay.center.rpc.api.domain.*;

import com.sztx.pay.center.rpc.api.domain.request.QueryOrderRequest;
import com.sztx.pay.center.rpc.api.domain.response.OrderRecharge;
import com.sztx.pay.center.rpc.api.domain.response.OrderRechargeList;
import com.sztx.pay.center.rpc.api.domain.response.OrderTransfer;
import com.sztx.pay.center.rpc.api.domain.response.OrderTransferList;
import com.sztx.pay.center.rpc.api.service.*;
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

    private PayFlowRpcService payFlowRpcService;

    private BankRpcService bankRpcService;

    private BankPayLimitRpcService  bankPayLimitRpcService;

    private SettleRpcService settleRpcService;

    private AccountFacade accountFacade;

    @ModelAttribute
    public  void init(){
        tradeRpcService = dubboClient.getDubboClient("tradeRpcService");
        accountRpcService = dubboClient.getDubboClient("accountRpcService");
        queryUserInfoService = dubboClient.getDubboClient("queryUserInfoService");
        queryService = dubboClient.getDubboClient("queryService");
        payFlowRpcService = dubboClient.getDubboClient("payFlowRpcService");
        bankRpcService = dubboClient.getDubboClient("bankRpcService");
        bankPayLimitRpcService = dubboClient.getDubboClient("bankPayLimitRpcService");
        settleRpcService = dubboClient.getDubboClient("settleRpcService");
        accountFacade = dubboClient.getDubboClient("accountFacade");
    }

    /**
     * session test
     * @return
     */
    @RequestMapping(value = "/sessionTest" , method ={ RequestMethod.POST, RequestMethod.GET},produces="application/json;charset=UTF-8")
    @ResponseBody
    public JsonResult sessionTest(HttpServletRequest request){
        JsonResult result = new JsonResult();
        Users user = CheckLoginInterceptor.getUsers(request.getSession());
        result.setResult(ResultAck.succ.getCode());
        result.setRetinfo(ResultAck.succ.getDesc());
        result.setData(user);
        return result;
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
        pageMap.put("actBalance", (balance + freezeBalance+0.0)/100.0);
        pageMap.put("leftBalance", (balance+0.0)/100.0 );
        pageMap.put("freezeBalance", (freezeBalance+0.0)/100.0);

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

        int balance = accountRpcService.queryBalance(user.getUserCode(), 1);

        String bindNo = request.getParameter("bindNo");

        UserVO userVO = queryUserInfoService.getUserInfo(user.getUserCode());
        map.put("balance",balance);
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

            Users user = CheckLoginInterceptor.getUsers(request.getSession());

            if(transferOrderId!=null && transferOrderId.trim().isEmpty()==false){
                startD = null;
                endD = null;
            }

            PageResult<TransferOrderInfo> records = accountFacade.queryTransferOrder(IpageNo,IpageSize,startD,endD,transferOrderId);

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
                    //transferData.setTransferTypeStr(t.gett);

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
                    transferData.setTransferAmount(t.getAmount()/100.0+"");
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
                record.setRechargeOrderId(t.getRechargeOrderId());
                record.setRechargeChannelStr(t.getRechargeChannelStr());
                record.setRechargeAmount(t.getRechargeAmount().longValue()/100.0+"");
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
                endD = new Date();
            }

            PayFlowRequest queryReq = new PayFlowRequest();
            Users user = CheckLoginInterceptor.getUsers(request.getSession());
            queryReq.setUserId(user.getUserCode());
            queryReq.setPageNo(Integer.parseInt(pageNo));
            queryReq.setPageSize(Integer.parseInt(pageSize));
            queryReq.setStartDate(startD);
            queryReq.setEndDate(endD);

            if(tradeOrderNo!=null && tradeOrderNo.trim().isEmpty()==false){
                queryReq.setTradeOrderNo(tradeOrderNo);
            }
            if(type!=null && type.trim().isEmpty()==false){
                if(type.equals("in")){
                   // queryReq.setTradeOrderType();
                }else if(type.equals("out")){
                   // queryReq.setTradeOrderType();
                }
            }

            PayFlowResponse records = payFlowRpcService.findUserPayFlow(queryReq);


            JSONObject json = new JSONObject();
            /**
             * pageNo:1,
             pageSize:10,
             total:100,
             rows
             */
            json.put("inAmount", records.getInAmount());
            json.put("outAmount", records.getOutAmount());
            json.put("pageNo", pageNo);
            json.put("pageSize", pageSize);
            json.put("total", records.getTotal());

            List<CashRecord> f_records = new ArrayList<>();
            for(PayFlowResponse.PayFlow  t :records.getPayFlows()){
                CashRecord record = new CashRecord();
                record.setPayDate(t.getPayDate());
                record.setTradeOrderNo(t.getTradeOrderNo());
                record.setTradeOrderType(t.getTradeOrderType());


                String otherAccount;
                String intOrOut;

                if(t.getPayeeUserId().equals(user.getUserCode())){
                    otherAccount = t.getPayerUserId();
                    intOrOut = "in";
                }else {
                    otherAccount = t.getPayeeUserId();
                    intOrOut = "out";
                }

                record.setOtherAccount(otherAccount);
                record.setType(intOrOut);
                record.setAmount((t.getAmount() + 0.0f) / 100.0 + "");
                //record.setBalance();

                f_records.add(record);
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
            List<BankInfo> ebankDCSeq = bankRpcService.findBankList(1);
            List<BankInfoDetail> ebankDeail = new ArrayList<>();

            for(BankInfo info:ebankDCSeq){
                BankPayLimitInfo limit = bankPayLimitRpcService.getPayLimitInfoBySwiftCode(info.getSwiftCode(),1);
                ebankDeail.add(new BankInfoDetail(info,limit));
            }

            //快捷支付
            List<BankInfo> expressDCSeq = bankRpcService.findBankList(2);
            List<BankInfoDetail> expressDCSeqDetail = new ArrayList<>();
            for(BankInfo info:expressDCSeq){
                BankPayLimitInfo limit = bankPayLimitRpcService.getPayLimitInfoBySwiftCode(info.getSwiftCode(),2);
                expressDCSeqDetail.add(new BankInfoDetail(info, limit));
            }

            Map<String,Object> banks = new HashMap<>();

            banks.put("ebankDCSeq",ebankDeail);
            banks.put("expressDCSeq",expressDCSeqDetail);
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
            json.put("loginName",user.getUserCode());

            String realName =user.getRealname();
            if(realName!=null && realName.length()>0){
                if(realName.length()==2){
                    realName = "*"+realName.substring(1,2);
                }else  if(realName.length()==3){
                    realName = "*"+realName.substring(1,3);
                }else if(realName.length()==4){
                    realName = "**"+realName.substring(2,4);
                }else{
                    realName = "**"+realName.substring(2,realName.length());
                }
            }
            json.put("realName",realName);
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

        List<BindCardInfo> cards = accountRpcService.queryBindCardList(user.getUserCode(), 0);

        for(BindCardInfo cardInfo:cards){
            if(cardInfo.getCardNo().equals(cardno)){
                json.put("cardCanUse",false);
                json.put("errorMsg", "此卡已绑定快捷支付,请换其它卡!");
                result.setData(json);
                return result;
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
        try {
             type =Integer.parseInt(request.getParameter("type"));
             amount = Long.parseLong(request.getParameter("amount"));
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

        int settleType = 0;

        if(type==1){
            settleType = 2;
        }else  if(type==2){
            settleType = 8 ;
        }else if(type==3){
            settleType = 10;
        }

        long fee = settleRpcService.calculateHandlingFee(1,settleType,user.getUserCode(),amount);
        JSONObject json = new JSONObject();
        json.put("fee",fee);

        result.setResult(ResultAck.succ);
        result.setData(json);

        return result;
    }

}
