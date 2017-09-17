package com.csii.batch.collector;

import java.util.HashMap;
import java.util.Map;

import com.csii.pe.action.Executable;
import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;
import com.csii.pp.entity.ClearingEntity;
import com.csii.pp.entity.DepartmentEntity;

public class MerchantSettlementAction extends AbstractExecutableAction{
	
    private Map<String, Executable> generateBankSettFileMap;
    
	public void execute(Context ctx) throws PeException {
		// TODO Auto-generated method stub
		MakeMerchantSettFile(ctx);
	}
	
	public void setGenerateBankSettFileMap(Map<String, Executable> generateBankSettFileMap) {
		this.generateBankSettFileMap = generateBankSettFileMap;
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
			log.error(e.getMessage(), e);
			throw e;
		}
	}

}
