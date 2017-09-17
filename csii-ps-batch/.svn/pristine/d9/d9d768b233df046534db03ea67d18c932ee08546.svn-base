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
import com.csii.pp.dict.Dict;
import com.csii.pp.entity.BusinessEntity;
import com.csii.pp.entity.ClearingEntity;
import com.csii.pp.entity.DepartmentEntity;
import com.csii.pp.entity.ProcessEntity;
import com.csii.pp.util.MiscUtil;

/**
 * @author db2admin
 *
 */
public class TransferJob extends AbstractJob implements SqlMapAware {

	protected Log log = LogFactory.getLog(getClass());
	
	/**
	 * 处理子进程流的线程池
	 */
	private Executor executor;
	
	protected SqlMapClientOperations sqlMap;
	
	private List transactions;
	
	private String allowedHost; //允许运行的服务器
	
    private int retryTimes;
	
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
			Map para = new HashMap();
			para.put(Dict.PPHOST, MiscUtil.getLocalHostName());
			para.put(Dict.PPQUERYNUMBER, new Integer(1));
			List<ProcessEntity> processList = sqlMap.queryForList(Constants.QUERY_PROCESS_LIST_BY_CONDITION_SQL, para);
			for(ProcessEntity procs : processList){
				final String deptId = (String) procs.getDepartmentId();
				try {
					
					executor.execute(new Runnable() {
						public void run() {
							executeTransaction(deptId);
						}
					});
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			
		}else{
			log.info(getLocalHostName()+ "no allow  start job");
		}
		
	}
	
	private void executeTransaction(String deptId){
		
		ClearingEntity clearingEntity = getClearingEntity(deptId);
		BusinessEntity businessEntity = getBusinessEntity(deptId);
		DepartmentEntity departmentEntity = getDepartmentEntity(deptId);
		
		businessEntity.setPreClearDate(MiscUtil.rolDate(departmentEntity.getDepartmentDate(), 1));
		businessEntity.update();
		
		Map map = new HashMap();
		final Context context = new ScheduleContext("", map);
		
		context.setData("ClearingEntity", clearingEntity);
		context.setData("BusinessEntity", businessEntity);
		context.setData("DepartmentEntity", departmentEntity);
		try {
		int retry = 0;
		while (businessEntity.getPreClearDate().compareTo(clearingEntity.getClearDate()) > 0
				|| !clearingEntity.getClearStatus().equals(ClearingEntity.CLEAR_STATUS_OK)) {
			
			retry++;
			if (transactions != null && !transactions.isEmpty()) {
				for (Iterator it = transactions.iterator(); it.hasNext();) {
					String transactionId = (String) it.next();
					context.setTransactionId(transactionId);
					coreController.execute(context);
				}
			}
			
			businessEntity.setClearDate(departmentEntity.getDepartmentDate());
			businessEntity.update();
			
			log.info("[" + MiscUtil.dateToString(clearingEntity.getClearDate()) + "]清算完成");
//			if (retry >= retryTimes) {
//				break;
//			}
		}
	} catch (PeException ex) {
		log.error(deptId+"执行失败", ex);
		throw new PeRuntimeException(ex);
	} 
	}
	
	private BusinessEntity getBusinessEntity(String departmentId) {
		BusinessEntity businessEntity = (BusinessEntity) sqlMap.queryForObject(Constants.QUERY_BUSINESS_ENTITY_SQL, departmentId);
		businessEntity.setSqlMap(sqlMap);
		return businessEntity;
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


	public Executor getExecutor() {
		return executor;
	}
	public void setExecutor(Executor executor) {
		this.executor = executor;
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
	public void setRetryTimes(int retryTimes) {
		this.retryTimes = retryTimes;
	}
	public void setCoreController(CoreController coreController) {
		this.coreController = coreController;
	}
	
	
	
}
