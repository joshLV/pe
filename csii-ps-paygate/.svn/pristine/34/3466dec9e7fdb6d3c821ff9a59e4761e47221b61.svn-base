package com.csii.weixin.mgmt.action;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.orm.ibatis.SqlMapClientOperations;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;

import com.csii.ibs.action.AbstractAction;
import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;
import com.csii.pe.core.PeRuntimeException;
import com.csii.pe.service.id.IdFactory;
import com.csii.pe.service.id.JdbcIdFactory;
import com.csii.pp.dict.Constants;
import com.csii.pp.merchant.Merchant;
import com.csii.pp.util.MiscUtil;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class WP06Action extends AbstractAction {
	
	private  IdFactory tellerSeqIdFactory;

	@Override
	public void execute(Context ctx) throws PeException {
		// TODO Auto-generated method stub
		final Map map = ctx.getDataMap();
		
		log.debug("*************商户开户交易开始*************");
		// 商户号和利润流水号的生成
		//String merchantId = merchantIdSeqNoGenenrator.generate().toString();
		//String profitSeqNo = profitSeqNoGenenrator.generate().toString();
		final Object MerId = tellerSeqIdFactory.generate();
		final String merchantId = checkMerId(MerId);
		
		log.debug("商户号["+merchantId+"]");
		map.put("MerchantId", merchantId);
		map.put("merLinkname", MiscUtil.toStringAndTrim(ctx.getString("link_name")));
		map.put("merLinkphoneno", MiscUtil.toStringAndTrim(ctx.getString("link_phone")));
		map.put("merLinkemail", MiscUtil.toStringAndTrim(ctx.getString("link_email")));
		map.put("merName", MiscUtil.toStringAndTrim(ctx.getString("mer_name")));
		map.put("merShortname", MiscUtil.toStringAndTrim(ctx.getString("mer_shortname")));
		map.put("mer_phoneno", MiscUtil.toStringAndTrim(ctx.getString("mer_phoneno")));
		map.put("manageType", MiscUtil.toStringAndTrim(ctx.getString("manage_type")));
		map.put("manageId", MiscUtil.toStringAndTrim(ctx.getString("manage_id")));
		map.put("merMark", MiscUtil.toStringAndTrim(ctx.getString("mer_mark")));
		map.put("merAddr", MiscUtil.toStringAndTrim(ctx.getString("mer_addr")));
		map.put("corporationName", MiscUtil.toStringAndTrim(ctx.getString("corporation_name")));
		map.put("corporationCertno", MiscUtil.toStringAndTrim(ctx.getString("corporation_certno")));
		map.put("merSettaccttype", MiscUtil.toStringAndTrim(ctx.getString("mer_settaccttype")));
		map.put("merSettacctname", MiscUtil.toStringAndTrim(ctx.getString("mer_settacctname")));
		map.put("merSettacctbankname", MiscUtil.toStringAndTrim(ctx.getString("settacct_bankname")));
		map.put("settacctNo", MiscUtil.toStringAndTrim(ctx.getString("settacct_no")));
		map.put("merType", MiscUtil.toStringAndTrim(ctx.getString("mer_type")));
		map.put("agentId", MiscUtil.toStringAndTrim(ctx.getString("agent_id")));
		map.put("merSettacctbankno", MiscUtil.toStringAndTrim(ctx.getString("settacct_bankno")));
		map.put("merStatus", "0");
		map.put("authStatus", "0");//待审核
		map.put("merParentid", MiscUtil.toStringAndTrim(ctx.getString("mer_parentid")));
		map.put("merSubmitdate", ctx.getTimestamp());
		map.put("subAppid",  MiscUtil.toStringAndTrim(ctx.getString("sub_appid")));
		final String keyid = MiscUtil.toStringAndTrim(ctx.getString("key_id"));
		//业务单号
		final Object OrderId = tellerSeqIdFactory.generate();
		map.put("orderId", OrderId);
		Map paramap = new HashMap();
		paramap.put("agentId", MiscUtil.toStringAndTrim(ctx.getString("agent_id")));
		//查询merbankid
		List orderList = this.getSqlMap().queryForList("pp.core.queryAgentBankId", map);
		String agtBankId = "";
		if(orderList==null){
			//抛出异常
			throw new PeException("E00044");
		}else if(orderList.size()>0){
			Map agtMap = (Map)orderList.get(0);
			agtBankId = (String) agtMap.get("agtBankId");
		}
		map.put("merBankid", agtBankId);
		map.put("mchToken", mixString(13));
		//计算金服innerfee，大商户和普通商户则取所属代理，子商户取大商户的innerfee
		String mertype = MiscUtil.toStringAndTrim(ctx.getString("mer_type"));
		if("02".equals(mertype)){//子商户
			Map qyrmap = new HashMap();
			qyrmap.put("mer_id", MiscUtil.toStringAndTrim(ctx.getString("mer_parentid")));
			Merchant mer  = (Merchant) this.getSqlMap().queryForObject("pp.core.qryMerId", qyrmap);
			if(null==mer){
				log.info("无大商户数据");
				throw new PeException("E00045");
			}else{
				BigDecimal parentMerInnerFee = (BigDecimal) mer.getMerInnerFeeAmt();
				map.put("merInnerFeeAmt", parentMerInnerFee);
			}
		}else{//普通商户或大商户找自己的代理
			Map qyrmap = new HashMap();
			qyrmap.put("agentId", MiscUtil.toStringAndTrim(ctx.getString("agent_id")));
			Map amap  = (Map) this.getSqlMap().queryForObject("pp.core.queryAgentInfoById", qyrmap);
			if(null==amap||amap.isEmpty()){
				log.info("无代理数据");
				throw new PeException("E00046");
			}else{
				BigDecimal agentInnerFee = (BigDecimal) amap.get("agtInnerfeeamt");
				map.put("merInnerFeeAmt", agentInnerFee);
			}
		}
			
		final String transctrl = MiscUtil.toStringAndTrim(ctx.getString("mer_transctrl"));
		
		
		final SqlMapClientOperations finalsqlmap=this.getSqlMap();
		try {
			// start transaction
			this.getTransactionTemplate().execute(new TransactionCallback() {
				public Object doInTransaction(TransactionStatus status) {
					//插入商户key关联表
					Map amap = new HashMap();
					amap.put("MerchantId", merchantId);
					amap.put("KeyId", keyid);
					finalsqlmap.insert("pp.core.insertMerKey", amap);
					log.info("商户key关联表新增成功！");
					
					ObjectMapper mapper = new ObjectMapper(); 
					try {
						//List stuList2 = mapper.readValue("[{\"trans_type\":\"NATIVE\",\"channel_id\":\"weixin\",\"fee_amt\":\"0.002\"}]", List.class);
						List stuList2 = mapper.readValue(transctrl, List.class);
						for (int i = 0; i < stuList2.size(); i++) {
							Map mermap = (Map) stuList2.get(i);
							Map<String, String> hmap= new HashMap<String, String>();
							hmap.put("MerchantId",merchantId);
							hmap.put("departmentId", MiscUtil.toStringAndTrim(mermap.get("channel_id")));
							hmap.put("transId", MiscUtil.toStringAndTrim(mermap.get("trans_type")));
							hmap.put("feeAmt", MiscUtil.toStringAndTrim(mermap.get("fee_amt")));
							hmap.put("feeType", MiscUtil.toStringAndTrim(mermap.get("fee_type")));
							hmap.put("settPeriod", MiscUtil.toStringAndTrim(mermap.get("sett_period")));
							finalsqlmap.insert("pp.core.insertMerFeeCtrl", hmap);
							log.info("插入商户手续费表成功"+hmap);
						}
					} catch (JsonParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						log.info("格式化循环域出错"+e.getMessage());
						throw new RuntimeException("格式化循环域出错");
					} catch (JsonMappingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						log.info("商户新增失败"+e.getMessage());
						throw new RuntimeException(e);
						
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						log.info("商户新增失败"+e.getMessage());
						throw new RuntimeException(e);
					}
					
					//插入商户表
					finalsqlmap.insert("pp.core.insertMerchant", map);
					log.info("插入商户表成功");
					return null;
				}
			});
		} catch (PeRuntimeException ptre) {
			throw new PeException(ptre);
		}
		
		
		log.debug("*************商户开户交易结束*************");
		Map json = new HashMap();
		json.put("ReturnCode", Constants.AAAAAAA);
		json.put("ReturnMsg", "交易成功");
		json.put("merchant_id", merchantId);
		json.put("mer_status", "0");
		ctx.setData("json", json);
	}
	
	public static String mixString(int num) {
		char[] letter = new char[] { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H',
				'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
				'U','V','W','X','Y','Z','0','1','2','3','4','5','6','7','8','9'};
		String mixString = "";
		Random ran = new Random();
		for (int i = 0; i < num; i++) {

			int postion = ran.nextInt(36);
			mixString += String.valueOf(letter[postion]);
		}
		return mixString;
	}
	
	private static String checkMerId(Object merid)
	{
		String meridnew=merid+"";
		if (meridnew.length()<12)
		{
			int kk=12-meridnew.length();
			String tmp="";
			for (int i=0;i<kk;i++)
			{
				tmp="0"+tmp;
			}
			meridnew=tmp+meridnew;
		}
		return meridnew;
		
	}
	public void setTellerSeqIdFactory(IdFactory tellerSeqIdFactory) {
		this.tellerSeqIdFactory = tellerSeqIdFactory;
	}

	public static void main(String[] args) {
//		ObjectMapper mapper = new ObjectMapper(); 
//		try {
//			List stuList2 = mapper.readValue("[{\"trans_type\":\"NATIVE\",\"channel_id\":\"weixin\",\"fee_amt\":\"0.002\"}]", List.class);
//			Map asd = (Map) stuList2.get(0);
//			System.out.println("aa:"+stuList2);
//		} catch (JsonParseException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (JsonMappingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		int length = 14;
		String val = "";  
        Random random = new Random();  
          
        //参数length，表示生成几位随机数  
        for(int i = 0; i < length; i++) {  
              
            String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num";  
            //输出字母还是数字  
            if( "char".equalsIgnoreCase(charOrNum) ) {  
                //输出是大写字母还是小写字母  
                int temp = random.nextInt(2) % 2 == 0 ? 65 : 97;  
                val += (char)(random.nextInt(26) + temp);  
            } else if( "num".equalsIgnoreCase(charOrNum) ) {  
                val += String.valueOf(random.nextInt(10));  
            }  
        }  
       System.out.println(val);
	}
}
