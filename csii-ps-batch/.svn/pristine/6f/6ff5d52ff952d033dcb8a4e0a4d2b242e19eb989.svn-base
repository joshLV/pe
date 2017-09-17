/*
 * @(#)StepTrsTemplate.java	1.0 2011-12-8 下午06:31:22
 *
 * Copyright 2004-2010 Client Server International, Inc. All rights reserved.
 * CSII PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.csii.batch.template;

import java.util.Locale;

import com.csii.pe.action.Action;
import com.csii.pe.action.Executable;
import com.csii.pe.action.PlaceholderAction;
import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;
import com.csii.pe.template.AbstractApplicationObjectTemplate;
import com.csii.pp.dict.Constants;
import com.csii.pp.entity.ClearingEntity;
import com.csii.pp.util.MiscUtil;

/**
 * StepTrsTemplate.java
 *
 * @author Cuiyi
 * <p>
 *   Created on 2011-12-8
 *   Modification history
 * </p>
 * <p>
 *   IBS Product Expert Group, CSII
 *   Powered by CSII PowerEngine 6.0
 * </p>
 * @version 1.0
 * @since 1.0
 */
public class StepTrsTemplate extends AbstractApplicationObjectTemplate {

	/* (non-Javadoc)
	 * @see com.csii.pe.template.Template#execute(com.csii.pe.core.Context)
	 */
	public void execute(Context context) throws PeException {
		ClearingEntity clearingEntity = (ClearingEntity) context.getData("ClearingEntity");
		
		String prePhase = (String) context.getTransactionConfig().getSetting().get(Constants.PREPHASE);

		String currentPhase = (String) context.getTransactionConfig().getSetting().get(Constants.CURRENTPHASE);

		int retryTimes = Integer.parseInt((String) context.getTransactionConfig().getSetting().get(Constants.RETRYTIMES));

		int delayTime = Integer.parseInt((String) context.getTransactionConfig().getSetting().get(Constants.RETRYTIMES));
		
		// 前一阶段处理成功或者当前阶段处理失败，进行该阶段的处理
		if ((prePhase.equals(clearingEntity.getClearPhase()) && clearingEntity.getClearStatus().equals(ClearingEntity.CLEAR_STATUS_OK))
				|| (currentPhase.equals(clearingEntity.getClearPhase()) && clearingEntity.getClearStatus().equals(ClearingEntity.CLEAR_STATUS_ERROR))) {
			
			clearingEntity.setClearPhase(currentPhase);
			clearingEntity.setClearStatus(ClearingEntity.CLEAR_STATUS_ERROR);
			clearingEntity.update();

			// 具体交易处理的action
			Action action = getAction(Constants.ACTION, context);
			if (action instanceof PlaceholderAction) {
				throw new PeException(com.csii.pe.core.Constants.PLACEHOLDER_ERROR,
						new Object[] { this.getClass().getName(), context.getTransactionId() });
			}
			
			int retry = 0;

			while (true) {
				retry++;
				log.info(getApplicationContext().getMessage("collector_step_" + currentPhase, null, Locale.getDefault())
						+ getApplicationContext().getMessage("collector_proc_begin", new Object[] { new Integer(retry) }, Locale.getDefault()));
				
				try {
					// 交易的具体任务执行
					((Executable) action).execute(context);

					// 更新状态
					clearingEntity.setClearStatus(ClearingEntity.CLEAR_STATUS_OK);
					clearingEntity.setEndDate(MiscUtil.getCurrentDate());
					clearingEntity.update();
					
					log.info(getApplicationContext().getMessage("collector_step_" + currentPhase, null, Locale.getDefault())
							+ getApplicationContext().getMessage("collector_proc_success", new Object[] { new Integer(retry) }, Locale.getDefault()));
					break;
				} catch (Exception ex) {
					log.error(getApplicationContext().getMessage("collector_step_" + currentPhase, null, Locale.getDefault())
							+ getApplicationContext().getMessage("collector_proc_error", new Object[] { new Integer(retry) }, Locale.getDefault()), ex);
					
					if (retry >= retryTimes) {
						// 更新状态
						clearingEntity.setClearStatus(ClearingEntity.CLEAR_STATUS_ERROR);
						clearingEntity.setEndDate(MiscUtil.getCurrentDate());
						clearingEntity.update();

						throw new PeException(getApplicationContext().getMessage("collector_step_" + currentPhase, null, Locale.getDefault())
								+ getApplicationContext().getMessage("collector_proc_error", new Object[] { new Integer(retry) },
										Locale.getDefault()), ex);
					} else {
						try {
							Thread.sleep(delayTime * 1000);
						} catch (Exception exception) {
							//do nothing
						}
						continue;
					}
				}
			}
		}
	}

}
