package com.csii.batch.sett.file;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.csii.ibs.action.IbsQueryAction;
import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;
import com.csii.pe.service.id.IdFactory;
import com.csii.pe.transform.TransformException;
import com.csii.pe.transform.Transformer;
import com.csii.pe.transform.TransformerFactory;
import com.csii.pp.entity.ClearingEntity;
import com.csii.pp.entity.DepartmentEntity;
import com.csii.pp.merchant.Merchant;
import com.csii.pp.sftp.SFTPFileTransfer;
import com.csii.pp.transport.HttpRequest;
import com.csii.pp.util.MiscUtil;
import com.csii.pp.util.RSAUtil;

/**
 * 渤海银行 - 生成资金结算文件
 */
public class CBHBSettFileAction extends IbsQueryAction {

	private static final Logger logger = LoggerFactory.getLogger(CBHBSettFileAction.class);

	private String settFilePath;

	private IdFactory fileSeqNoGenerator;
	
	private TransformerFactory fileTransformerFactory;
	
	private SFTPFileTransfer sftpFileTransfer;
	
	private String platformNo;
	
	private String url;
	
	private IdFactory seqNoGenerator;

	@Override
	public void execute(Context context) throws PeException {
		ClearingEntity clearingEntity = (ClearingEntity) context.getData("ClearingEntity");
		DepartmentEntity departmentEntity = (DepartmentEntity) context.getData("DepartmentEntity");

		String bankId = (String) context.getData("BankId");

		Map para = new HashMap();
		para.put("bankId", bankId);
		para.put("ClearDate", clearingEntity.getClearDate());
		para.put("DepartmentId", departmentEntity.getDepartmentId());
		para.put("SettlementStatus", "0");
		// 查询商户结算记录(结算账号是本行的)
		List settList = this.getSqlMap().queryForList("pp.batch.queryMerchantSett", para);

		String innerFileBatchNo = null;

		if ((null != settList) && (settList.size() > 0)) {
			innerFileBatchNo = (String) fileSeqNoGenerator.generate();

			Iterator it = settList.iterator();
            List fileDetailList = new ArrayList();
            String clearDate = MiscUtil.dateToString(clearingEntity.getClearDate());
            //文件名的长度不能动，回盘文件会根据此长度来取日期。
			String innerFileName = "RQ_"+platformNo+"_batchpay_"+clearDate+"_0001.CSV";
			while (it.hasNext()) {
				Map data = (Map) it.next();
				data.put("DepartmentId", departmentEntity.getDepartmentId());
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
					data.put("bankName", mer.getMerSettAcctBankName());
				}else if(!MiscUtil.isNullOrEmpty(agentMap)){
					if(isNegative){
						data.put("briefCode", "03");
					}else{
						data.put("briefCode", "02");
					}
					data.put("merName", agentMap.get("agtName"));     
					data.put("settAcctName",agentMap.get("agtSettacctName"));	
					data.put("bankId", agentMap.get("agtBankId"));
					data.put("bankName", agentMap.get("agtSettAcctBankNm"));
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
					data.put("bankName", innerInfo.get("bankName"));
				}

				//BigDecimal settAmt = ((BigDecimal) data.get("totalAmt")).subtract((BigDecimal) data.get("totalFeeAmt"));
				data.put("settAmount", data.get("totalAmt"));
				data.put("settSeqNo",seqNoGenerator.generate());
				data.put("batchNo", innerFileBatchNo);
				data.put("transDate", clearingEntity.getClearDate());
				data.put("settDate", MiscUtil.getCurrentDate());
				data.put("status", "0");
				data.put("fileName", innerFileName);
				data.put("platformNo", platformNo);
				fileDetailList.add(data);
				this.getSqlMap().insert("pp.batch.insertfileDetail", data);
			}

			//将状态为失败、未明的更新为0：未发送。
			List failList = this.getSqlMap().queryForList("pp.batch.querySettfileDetail", para);
			if ((null != failList) && (failList.size() > 0)) {
				Iterator iter = failList.iterator();
				while (iter.hasNext()) {
					Map data = (Map) iter.next();
					data.put("settStatus", "0");
					fileDetailList.add(data);
					this.getSqlMap().update("pp.batch.updateSettfileDetailByDate", data);
				}
			}
			
			Map filePara = new HashMap();
			filePara.put("batchNo", innerFileBatchNo);
			filePara.put("transDate", clearingEntity.getClearDate());
			filePara.put("settDate", MiscUtil.getCurrentDate());
			filePara.put("fileName", innerFileName);
			filePara.put("status", "0");
			filePara.put("bankId", bankId);
			filePara.put("departmentId", departmentEntity.getDepartmentId());
			filePara.put("fileType", "0");
			
			uploadSettFile(fileDetailList,innerFileName);
			notify(filePara);
			//上传成功后将明细记录更新为9发送中（0：未发送 成功1，失败2，状态未名3，处理中4 ,发送中9）
			Map data = new HashMap();
			data.put("settStatus", "9");
			data.put("settStatus1", "0");
			this.getSqlMap().update("pp.batch.updateSettfileDetailByStatus", data);
			
			//文件上传表
			this.getSqlMap().insert("pp.batch.insertfileInfo", filePara);
			
			
		} else {
			log.info("今日无本行结算文件");
		}
	}

	/**
	 * 对账文件上传以及通知
	 * @throws PeException 
	 */
	private void uploadSettFile(List list,String fileName) throws PeException {
		Transformer transformer = fileTransformerFactory.getTransformer("cbhb");
		Map map =new HashMap();
		map.put("LoopResult", list);
		try {
			byte[] fileData = (byte[]) transformer.format(map, null);
			FileUtils.writeByteArrayToFile(new File(settFilePath  + "/" + fileName), fileData);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error("生成渤海银行结算文件异常：",e);
			throw new PeException("OF0003");
		}
		try {
			sftpFileTransfer.connect();
			sftpFileTransfer.upload(settFilePath, fileName, "/home/csiisftp/");
		} catch (PeException e) {
			log.info("文件"+settFilePath  + "/" + fileName+"上传失败:"+e);
			throw new PeException("OF0003");
		}finally{
			sftpFileTransfer.disconnect();
		}
	}
	
	private void notify(Map filePara){
		String plain = "{" +
				  "\"_TransactionId\":\"payecm.MCFileImportNoticeTrs\"," +
				  "\"MCHTimestamp\":"+MiscUtil.getCurrentDateTime()+"," +
				  "\"_MCHJnlNo\":\""+filePara.get("batchNo")+"\"," +
				  "\"NoticeType\":\"2\"," +
				  "\"FileName\":"+filePara.get("fileName")+"," +
				 "}";
		String plainData = RSAUtil.RSAencryptByPublicKey(plain.getBytes(),RSAUtil.RSA_PUBLICKEY);
		String sign = RSAUtil.sign(plainData, RSAUtil.SIGN_PRIVATEKEY);
		HttpRequest.sendGet(url, "PlainData="+plainData+"&Signatures="+sign);
	}

	public void setFileSeqNoGenerator(IdFactory fileSeqNoGenerator) {
		this.fileSeqNoGenerator = fileSeqNoGenerator;
	}

	public TransformerFactory getFileTransformerFactory() {
		return fileTransformerFactory;
	}

	public void setFileTransformerFactory(TransformerFactory fileTransformerFactory) {
		this.fileTransformerFactory = fileTransformerFactory;
	}

	public void setSettFilePath(String settFilePath) {
		this.settFilePath = settFilePath;
	}

	public void setSftpFileTransfer(SFTPFileTransfer sftpFileTransfer) {
		this.sftpFileTransfer = sftpFileTransfer;
	}

	public void setPlatformNo(String platformNo) {
		this.platformNo = platformNo;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setSeqNoGenerator(IdFactory seqNoGenerator) {
		this.seqNoGenerator = seqNoGenerator;
	}
}
