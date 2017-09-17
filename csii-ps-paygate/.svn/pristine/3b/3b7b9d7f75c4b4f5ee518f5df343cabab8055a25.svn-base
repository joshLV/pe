package com.csii.weixin.template;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.csii.pe.action.Action;
import com.csii.pe.action.Executable;
import com.csii.pe.action.PlaceholderAction;
import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;
import com.csii.pe.template.AbstractTemplate;
import com.csii.pp.common.ErrorConstants;
import com.csii.pp.dict.Constants;
import com.csii.pp.util.MiscUtil;

/**
 * 
 * 微信类交易模版
 * 
 * @author xudong
 * 
 */

public class PayTrsTemplate extends AbstractTemplate implements ApplicationContextAware {

	private ApplicationContext appCtx;

	public PayTrsTemplate() {
		super();
	}

	@Override
	public void execute(Context context) throws PeException {
		// 准备订单的action
		Action prepareAction = getAction("prepareAction", context);
		// 插入订单交易的action
		Action addOrderAction = getAction("addOrderAction", context);
		// 交易前期检查的action
		Action checkAction = getAction("preCheckAction", context);
		// 具体交易处理的action
		Action action = this.getAction("action", context);
		// 交易前期检查的action
		Action aftCheckAction = getAction("aftCheckAction", context);
		// 修改交易记录的action
		Action aftAction = getAction("aftAction", context);

		if (action instanceof PlaceholderAction) {
			throw new PeException(com.csii.pe.core.Constants.PLACEHOLDER_ERROR, new Object[] { this.getClass().getName(), context.getTransactionId() });
		}

		PeException exception = null;

		try {
			try {
				// 准备订单
				((Executable) prepareAction).execute(context);
			} catch (Exception e) {
				log.error(new StringBuffer().append("网关流水[").append(context.getData("TransSeqNo")).append("]").append("******准备订单失败******").append("]").toString(), e);

				if (e instanceof PeException) {
					throw (PeException) e;
				} else {
					throw new PeException(ErrorConstants.SYSTEM_INNER_ERROR, e);
				}
			}

			try {
				// 记录交易
				((Executable) addOrderAction).execute(context);
			} catch (Exception e) {
				log.fatal("******记录交易失败，请检查数据库******", e);
				if (e instanceof PeException) {
					throw (PeException) e;
				} else {
					throw new PeException(ErrorConstants.SYSTEM_INNER_ERROR, e);
				}
			}

			try {
				// 交易的前期检查和控制
				((Executable) checkAction).execute(context);
			} catch (Exception e) {
				log.fatal("******交易前期检查和控制出错******", e);

				if (e instanceof PeException) {
					throw (PeException) e;
				} else {
					throw new PeException(ErrorConstants.SYSTEM_INNER_ERROR, e);
				}
			}

			((Executable) action).execute(context);
		} catch(PeException e){
//			if (e instanceof PeException) {
//				exception=(PeException) e;
//			} else {
//				exception = new PeException(ErrorConstants.SYSTEM_INNER_ERROR);
//				log.error(e);
//			}
			log.error("error", e);
			exception = e;
		} catch (Exception e) {
			log.error("error", e);
			exception = new PeException(ErrorConstants.SYSTEM_INNER_ERROR, e);
		} finally {
			// 交易结果数据准备
			this.post(context, exception);

			resolverMessages(context);

			if ("FAIL".equals(context.getData("ReturnCode")) && !MiscUtil.isNullOrEmpty(context.getData("return_msg"))) {
				context.setData("ReturnMsg", context.getData("return_msg"));
			} else if ("E00012".equals(context.getData("ReturnCode"))) {
				String msg = appCtx
						.getMessage((String) context.getData("ReturnCode"), new Object[] { context.getData("out_refund_no") }, (String) context.getData("err_code_des"), context.getLocale());
				context.setData("ReturnMsg", msg);
			}

			try {
				// 交易的后期控制
				((Executable) aftCheckAction).execute(context);
			} catch (Exception e) {
				log.fatal("******交易后期控制处理失败，请检查******", e);
				throw new PeException(e);
			}

			// 更新交易
			try {
				((Executable) aftAction).execute(context);
			} catch (Exception e) {
				// 更新日志错误，需要根据打印的具体内容进行手工更新不然可能影响到清算
				log.fatal("******更新交易失败，请手工修改******", e);
				throw new PeException(e);
			}

			if (exception != null) {
				throw exception;
			}
		}

	}

	private void post(Context ctx, PeException pe) throws PeException {
		// 支付平台响应码
		String responseCode = null;

		// 如果出现异常,获取相应的响应码
		if (pe != null) {
			// 支付平台错误码
			responseCode = pe.getMessageKey();
		} else {
			responseCode = Constants.AAAAAAA;
		}

		ctx.setData("ReturnCode", responseCode);
	}

	private void resolverMessages(Context context) {
		String messageCode = (String) context.getData("ReturnCode");

		String message = "";

		try {
			String defaultMsg = (String) context.getData("err_code_des");
			message = appCtx.getMessage(messageCode, null, defaultMsg, context.getLocale());
		} catch (Exception ee) {
			log.error("", ee);
			message = "交易失败";
		}
		context.setData("ReturnMsg", message);
	}

	@Override
	public void setApplicationContext(ApplicationContext appCtx) throws BeansException {
		this.appCtx = appCtx;
	}

}
