package com.csii.batch.sett.file;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.csii.ibs.action.IbsQueryAction;
import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;
import com.csii.pe.service.id.IdFactory;
import com.csii.pp.dict.Constants;
import com.csii.pp.dict.ErrorConstants;
import com.csii.pp.entity.ClearingEntity;
import com.csii.pp.entity.DepartmentEntity;
import com.csii.pp.merchant.Merchant;
import com.csii.pp.util.MiscUtil;

/**
 * 上海银行 - 生成资金结算文件
 */
public class BOSHSettFileAction extends IbsQueryAction {

	private static final Logger logger = LoggerFactory.getLogger(BOSHSettFileAction.class);

	private IdFactory fileSeqNoGenerator;
	
	private IdFactory seqNoGenerator;

	@Override
	public void execute(Context context) throws PeException {
		ClearingEntity clearingEntity = (ClearingEntity) context.getData("ClearingEntity");
		DepartmentEntity departmentEntity = (DepartmentEntity) context.getData("DepartmentEntity");

		Map para = new HashMap();
		para.put("bankId", context.getData("BankId"));
		para.put("ClearDate",clearingEntity.getClearDate());
		para.put("DepartmentId", departmentEntity.getDepartmentId());
		para.put("SettlementStatus", "0");
		// 查询商户结算记录(结算账号是本行的)
		List settList = this.getSqlMap().queryForList("pp.batch.queryMerchantSettForInner0", para);

		String innerFileBatchNo = null;

		if ((null != settList) && (settList.size() > 0)) {
			innerFileBatchNo = (String) fileSeqNoGenerator.generate();

			Iterator it = settList.iterator();

			while (it.hasNext()) {
				Map data = (Map) it.next();
				data.put("DepartmentId", departmentEntity.getDepartmentId());
				if(((BigDecimal)data.get("totalAmt")).compareTo(Constants.ZERO)!=0){
					Merchant mer = (Merchant) this.getSqlMap().queryForObject("pp.core.queryMerchant", data.get("id"));
					Map agtPara = new HashMap();
					agtPara.put("agentId",data.get("id"));
					Map  agentMap = (Map)this.getSqlMap().queryForObject("pp.core.queryAgentInfoById",agtPara);
					//signum返回 的是 -1, 0, 1，分别表示 负数、零、正数
					boolean isNegative =false;
					if(((BigDecimal) data.get("totalAmt")).signum() == -1 ){
						isNegative =true;
					}
					//briefCode 00：交易结算资金,01：交易结算资金返还,02:分润金额结算,03:分润金额返还,04:金服分润结算，05：金服分润结算返还
					if(!MiscUtil.isNullOrEmpty(mer)){
						if(isNegative){
							data.put("briefCode", "01");
						}else{
							data.put("briefCode", "00");
						}
						data.put("merName", mer.getName());
						data.put("settAcctName", mer.getMerSettName());	
						data.put("bankId", mer.getMerBankId()); 
					}else if(!MiscUtil.isNullOrEmpty(agentMap)){
						if(isNegative){
							data.put("briefCode", "03");
						}else{
							data.put("briefCode", "02");
						}
						data.put("merName", agentMap.get("agtName"));     
						data.put("settAcctName",agentMap.get("agtSettacctName"));	
						data.put("bankId", agentMap.get("agtBankId"));
					}else{
						if(isNegative){
							data.put("briefCode", "05");
						}else{
							data.put("briefCode", "04");
						}
						Map innerInfo = (Map)this.getSqlMap().queryForObject("pp.core.queryInnerInfo",context.getData("BankId"));
						data.put("merName", innerInfo.get("merName"));
						data.put("settAcctName",innerInfo.get("acctname"));	
						data.put("bankId", innerInfo.get("bankid"));
					}
					data.put("settSeqNo",seqNoGenerator.generate());
					data.put("settAmount", data.get("totalAmt"));
					data.put("batchNo", innerFileBatchNo);
					data.put("transDate", clearingEntity.getClearDate());
					data.put("settDate", MiscUtil.getCurrentDate());
					data.put("status", "0");

					this.getSqlMap().insert("pp.batch.insertfileDetail", data);
				}
				
			}

			String settDate = MiscUtil.dateToString(clearingEntity.getClearDate());

			String innerFileName = settDate + "_" + innerFileBatchNo + ".xls";

			Map filePara = new HashMap();
			filePara.put("batchNo", innerFileBatchNo);
			filePara.put("transDate", clearingEntity.getClearDate());
			filePara.put("settDate",MiscUtil.getCurrentDate());
			filePara.put("fileName", innerFileName);
			filePara.put("status", "0");
			filePara.put("bankId", context.getData("BankId"));
			filePara.put("departmentId", departmentEntity.getDepartmentId());
			filePara.put("fileType", "0");

			this.getSqlMap().insert("pp.batch.insertfileInfo", filePara);
		} else {
			log.info("今日无本行结算文件");
		}

		// 查询商户结算记录(结算账号是他行的)
		List outSettList = this.getSqlMap().queryForList("pp.batch.queryMerchantSettForOut", para);

		String outFileBatchNo = (String) fileSeqNoGenerator.generate();

		if ((null != outSettList) && (outSettList.size() > 0)) {
			Iterator it = outSettList.iterator();

			while (it.hasNext()) {
				Map data = (Map) it.next();
				data.put("DepartmentId", departmentEntity.getDepartmentId());
				if(((BigDecimal)data.get("totalAmt")).compareTo(Constants.ZERO)!=0){
					Merchant mer = (Merchant) this.getSqlMap().queryForObject("pp.core.queryMerchant", data.get("id"));
					Map agtPara = new HashMap();
					agtPara.put("agentId",data.get("id"));
					Map  agentMap = (Map)this.getSqlMap().queryForObject("pp.core.queryAgentInfoById",agtPara);
					//signum返回 的是 -1, 0, 1，分别表示 负数、零、正数
					boolean isNegative =false;
					if(((BigDecimal) data.get("totalAmt")).signum() == -1 ){
						isNegative =true;
					}
					//briefCode 00：交易结算资金,01：交易结算资金返还,02:分润金额结算,03:分润金额返还,04:金服分润结算，05：金服分润结算返还
					if(!MiscUtil.isNullOrEmpty(mer)){
						if(isNegative){
							data.put("briefCode", "01");
						}else{
							data.put("briefCode", "00");
						}
						data.put("merName", mer.getName());
						data.put("settAcctName", mer.getMerSettName());	
						data.put("bankId", mer.getMerBankId()); 
					}else if(!MiscUtil.isNullOrEmpty(agentMap)){
						if(isNegative){
							data.put("briefCode", "03");
						}else{
							data.put("briefCode", "02");
						}
						data.put("merName", agentMap.get("agtName"));     
						data.put("settAcctName",agentMap.get("agtSettacctName"));	
						data.put("bankId", agentMap.get("agtBankId"));
					}else{
						if(isNegative){
							data.put("briefCode", "05");
						}else{
							data.put("briefCode", "04");
						}
						Map innerInfo = (Map)this.getSqlMap().queryForObject("pp.core.queryInnerInfo",context.getData("BankId"));
						data.put("merName", innerInfo.get("merName"));
						data.put("settAcctName",innerInfo.get("acctname"));	
						data.put("bankId", innerInfo.get("bankid"));
					}
					

					data.put("settSeqNo",seqNoGenerator.generate());
					data.put("settAmount", data.get("totalAmt"));
					data.put("batchNo", outFileBatchNo);
					data.put("transDate", clearingEntity.getClearDate());
					data.put("settDate", MiscUtil.getCurrentDate());
					data.put("status", "0");

					this.getSqlMap().insert("pp.batch.insertfileDetail", data);
				}
			}

			String settDate = MiscUtil.dateToString(clearingEntity.getClearDate());

			String outFileName = settDate + "_" + outFileBatchNo + ".xls";

			Map filePara = new HashMap();
			filePara.put("batchNo", outFileBatchNo);
			filePara.put("transDate", settDate);
			filePara.put("settDate", MiscUtil.getCurrentDate());
			filePara.put("fileName", outFileName);
			filePara.put("status", "0");
			filePara.put("bankId", context.getData("BankId"));
			filePara.put("departmentId", departmentEntity.getDepartmentId());
			filePara.put("fileType", "1");

			this.getSqlMap().insert("pp.batch.insertfileInfo", filePara);
		} else {
			log.info("今日无他行结算文件");
		}
	}

	public void setFileSeqNoGenerator(IdFactory fileSeqNoGenerator) {
		this.fileSeqNoGenerator = fileSeqNoGenerator;
	}

	public void setSeqNoGenerator(IdFactory seqNoGenerator) {
		this.seqNoGenerator = seqNoGenerator;
	}
	
	
}
