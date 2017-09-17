package com.csii.batch.job;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.orm.ibatis.SqlMapClientOperations;

import com.csii.ext.schedule.JobContext;
import com.csii.ext.schedule.JobException;
import com.csii.ext.schedule.impl.AbstractJob;
import com.csii.pe.action.Executable;
import com.csii.pe.config.support.SqlMapAware;
import com.csii.pe.core.Context;
import com.csii.pe.core.CoreController;
import com.csii.pe.core.PeException;
import com.csii.pe.core.PeRuntimeException;
import com.csii.pe.service.executor.Executor;
import com.csii.pe.service.schedule.ScheduleContext;
import com.csii.pe.transform.TransformerFactory;
import com.csii.pp.dict.Constants;
import com.csii.pp.entity.BusinessEntity;
import com.csii.pp.entity.ClearingEntity;
import com.csii.pp.entity.DepartmentEntity;
import com.csii.pp.entity.ProcessEntity;
import com.csii.pp.util.MiscUtil;

/**
 * @author db2admin
 *
 */
public class DayTransferJob extends AbstractJob implements SqlMapAware {

	protected Log log = LogFactory.getLog(getClass());
	
	protected SqlMapClientOperations sqlMap;
	
	private List transactions;
	
	private String allowedHost; //允许运行的服务器
	
	
	private CoreController coreController;
	
	public static String getLocalHostName() {
        try {
            InetAddress inetaddress = InetAddress.getLocalHost();
            return inetaddress.getHostName();
        } catch(UnknownHostException ex) {
        	throw new RuntimeException(ex);
        }
	}
	/* (non-Javadoc)
	 * @see com.csii.ext.schedule.impl.AbstractJob#exec(com.csii.ext.schedule.JobContext)
	 */
	@Override
	protected void exec(JobContext jobcontext) throws JobException {
		
		if(allowedHost == null ||  allowedHost.trim().length()==0 ||  allowedHost.equals(getLocalHostName())){
			log.info("start job");
			ClearingEntity clearingEntity = getClearingEntity("daytrans");
			DepartmentEntity departmentEntity = getDepartmentEntity("daytrans");
			
			Map map = new HashMap();
			Context context = new ScheduleContext("", map);
			
			context.setData("ClearingEntity", clearingEntity);
			context.setData("DepartmentEntity", departmentEntity);
			
			try {
					
					if (transactions != null && !transactions.isEmpty()) {
						for (Iterator it = transactions.iterator(); it.hasNext();) {
							String transactionId = (String) it.next();
							context.setTransactionId(transactionId);
							coreController.execute(context);
						}
					}
					
					
					log.info("清算完成");
			} catch (PeException ex) {
				throw new PeRuntimeException(ex);
			} 
			
		}else{
			log.info(getLocalHostName()+ "no allow  start job");
		}
		
	}
	
	
	private ClearingEntity getClearingEntity(String departmentId) {
		ClearingEntity clearingEntity = (ClearingEntity) sqlMap.queryForObject(Constants.QUERY_CLEARING_ENTITY_SQL, departmentId);
		clearingEntity.setSqlMap(sqlMap);
		return clearingEntity;
	}
	
	private DepartmentEntity getDepartmentEntity(String departmentId) {
		DepartmentEntity departmentEntity = (DepartmentEntity) sqlMap.queryForObject(Constants.QUERY_DEPARTMENT_ENTITY_SQL, departmentId);
		departmentEntity.setSqlMap(sqlMap);
		return departmentEntity;
	}


	public void setTransactions(List transactions) {
		this.transactions = transactions;
	}
	public void setAllowedHost(String allowedHost) {
		this.allowedHost = allowedHost;
	}
	public void setSqlMap(SqlMapClientOperations sqlMap) {
		// TODO Auto-generated method stub
		this.sqlMap = sqlMap;
	}
	
	public SqlMapClientOperations getSqlMap() {
		return sqlMap;
	}
	public void setCoreController(CoreController coreController) {
		this.coreController = coreController;
	}
	
	
	
}
