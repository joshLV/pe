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
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.csii.ext.schedule.JobContext;
import com.csii.ext.schedule.JobException;
import com.csii.ext.schedule.impl.AbstractJob;
import com.csii.pe.action.Executable;
import com.csii.pe.config.support.SqlMapAware;
import com.csii.pe.config.support.TransactionTemplateAware;
import com.csii.pe.core.Context;
import com.csii.pe.core.CoreController;
import com.csii.pe.core.PeException;
import com.csii.pe.core.PeRuntimeException;
import com.csii.pe.service.schedule.ScheduleContext;
import com.csii.pe.transform.TransformerFactory;
import com.csii.pp.dict.Constants;
import com.csii.pp.entity.BusinessEntity;
import com.csii.pp.entity.ClearingEntity;
import com.csii.pp.entity.DepartmentEntity;
import com.csii.pp.util.MiscUtil;

/**
 * @author xueyu
 *
 */
public class CutOffJob extends AbstractJob implements SqlMapAware,TransactionTemplateAware {

	protected Log log = LogFactory.getLog(getClass());
	
	protected SqlMapClientOperations sqlMap;
	
	protected TransactionTemplate transactionTemplate;
	
	private String allowedHost; //允许运行的服务器
	
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
		log.info("*********");
		if(allowedHost == null ||  allowedHost.trim().length()==0 ||  allowedHost.equals(getLocalHostName())){
			log.info("start cutoff job");
			List<DepartmentEntity> deptList = getDepartmentEntityList();
			for(final DepartmentEntity dept : deptList) {  
	            dept.setDepartmentDate(MiscUtil.rolDate(dept.getDepartmentDate(), -1)); 
	            this.sqlMap.update("pp.core.updateDepartmentEntity", dept);
//	            final HashMap para = new HashMap();
//	            para.put("departmentId", dept.getDepartmentId());
//	            para.put("status", "0");
//	        	transactionTemplate.execute(new TransactionCallback() {
//					public Object doInTransaction(TransactionStatus arg0) {
//						dept.update();
//						sqlMap.update(Constants.UPDATE_PROCESS_ENTITY_STATUS_SQL, para);
//						return null;
//					}
//				});
	        }  
			
		}else{
			log.info(getLocalHostName()+ "no allow  start job");
		}
		log.info("cut off job end");
		
	}
	
	private List getDepartmentEntityList() {
		List deptList = sqlMap.queryForList(Constants.QUERY_DEPARTMENT_LIST_SQL);
		return deptList;
	}


	public void setAllowedHost(String allowedHost) {
		this.allowedHost = allowedHost;
	}
	public void setSqlMap(SqlMapClientOperations sqlMap) {
		// TODO Auto-generated method stub
		this.sqlMap = sqlMap;
	}
	public void setTransactionTemplate(TransactionTemplate paramTransactionTemplate) {
		// TODO Auto-generated method stub
		this.transactionTemplate = paramTransactionTemplate;
	}
	
}
