package com.csii.batch.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.csii.pe.action.Executable;
import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;
import com.csii.pe.service.id.IdFactory;
import com.csii.pp.dict.Constants;
import com.csii.pp.dict.Dict;
import com.csii.pp.entity.ClearingEntity;
import com.csii.pp.entity.DepartmentEntity;
import com.csii.pp.util.MiscUtil;

public class SettlementAction extends AbstractBatchAction {

	private static final Logger logger = LoggerFactory.getLogger(SettlementAction.class);

	private IdFactory settSeqNoGenerator;

	private Map<String, Executable> generateBankSettFileMap;

	public void execute(Context ctx) throws PeException {

		// TODO Auto-generated method stub
		ClearingEntity clearingEntity = (ClearingEntity) ctx.getData("ClearingEntity");
		DepartmentEntity departmentEntity = (DepartmentEntity) ctx.getData("DepartmentEntity");

		log.info(departmentEntity + "结算开始");
		Map<String, Object> para = new HashMap<String, Object>();
		para.put("DepartmentId", departmentEntity.getDepartmentId());
		para.put("SettStatus", Constants.SETT_SEND_STATUS_NO);
		para.put("SettDate", clearingEntity.getClearDate()); // 增加结算日期参数
		para.put("ClearDate", clearingEntity.getClearDate()); // 增加结算日期参数
		// 删除当日结算表中当日结算日的数据
		getSqlMap().delete("pp.batch.deleteMerchantSettByDepartmentAndDate", para);
		//先汇总商户结算数据 
		List merTransList = getSqlMap().queryForList("pp.batch.queryMerchantTransForSett", para);

		if ((merTransList != null) && !merTransList.isEmpty()) {
			for (int i = 0; i < merTransList.size(); i++) {
				Map<String, Object> merSettData = (Map<String, Object>) merTransList.get(i);
				log.info("商户结算数据：" + merSettData);
//				merSettData.put("SettDate", MiscUtil.rolDate(clearingEntity.getClearDate(), -1));
				merSettData.put("ClearDate", clearingEntity.getClearDate());
				merSettData.put("SettDate", clearingEntity.getClearDate());
				// 只有金额大于零的时候才结算
				// if ((totalAmount.compareTo() > 0)) {
				// log.info("开始处理" + merchant.getId() + " 商户结算");
				processMerchantSett(merSettData);
				// log.info("结束处理" + merchant.getId() + " 商户结算");
				// }
			}
		}
		
		log.info(departmentEntity.getDepartmentName() + "结算结束");
//		log.info(departmentEntity.getDepartmentName() + "结算文件生成开始");
		//生成对账文件
//		MakeMerchantSettFile(ctx);
		
		//结算成功，更新mertrans为1
		if ((merTransList != null) && !merTransList.isEmpty()) {
			for (int i = 0; i < merTransList.size(); i++) {
				Map<String, Object> merSettData = (Map<String, Object>) merTransList.get(i);
				log.info("更新商户结算数据：" + merSettData);
//				merSettData.put("SettDate", MiscUtil.rolDate(clearingEntity.getClearDate(), -1));
				merSettData.put("ClearDate", clearingEntity.getClearDate());
				merSettData.put("SettDate", clearingEntity.getClearDate());
				// 只有金额大于零的时候才结算
				// if ((totalAmount.compareTo() > 0)) {
				// log.info("开始处理" + merchant.getId() + " 商户结算");
				this.getSqlMap().update("pp.batch.updateMerTransStatus", merSettData);
				// log.info("结束处理" + merchant.getId() + " 商户结算");
				// }
			}
		}
	}

	public void processMerchantSett(Map<String, Object> data) {

		// 记录结算交易
		// 商户结算交易流水，这里需要修改！！！！！！！！！！！
		data.put(Dict.PPSETTLEMENTSEQNO, settSeqNoGenerator.generate());
		// 交易日期
		data.put(Dict.PPTRANSDATE, MiscUtil.getDate());
		// 交易时间戳
		data.put(Dict.PPTRANSDATETIME, new java.util.Date());

		data.put(Dict.PPTRANSFERSTATUS, Constants.SETT_SEND_STATUS_NO);

		this.getSqlMap().insert("pp.batch.insertMerchantSett", data);
	}
	
	public void MakeMerchantSettFile(Context ctx) throws PeException {
		ClearingEntity clearingEntity = (ClearingEntity) ctx.getData("ClearingEntity");
		DepartmentEntity departmentEntity = (DepartmentEntity) ctx.getData("DepartmentEntity");

		Map para = new HashMap();
		para.put("SettlementDate", clearingEntity.getClearDate());
		para.put("DepartmentId", departmentEntity.getDepartmentId());
		para.put("SettlementStatus", "0");

		this.getSqlMap().delete("pp.batch.deleteFileDetailByDate", para);

		this.getSqlMap().delete("pp.batch.deleteFileByDate", para);

		try {
			for (Map.Entry<String, Executable> entry : generateBankSettFileMap.entrySet()) {
				String bankId = entry.getKey();
				ctx.setData("BankId", bankId);

				Executable bankSettFileAction = entry.getValue();
				bankSettFileAction.execute(ctx);
			}
		} catch (PeException e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
	}

	public void setSettSeqNoGenerator(IdFactory settSeqNoGenerator) {
		this.settSeqNoGenerator = settSeqNoGenerator;
	}

	public void setGenerateBankSettFileMap(Map<String, Executable> generateBankSettFileMap) {
		this.generateBankSettFileMap = generateBankSettFileMap;
	}
}