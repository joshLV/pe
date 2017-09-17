package com.csii.weixin.common.action;

import java.util.List;

import com.csii.ibs.action.AbstractAction;
import com.csii.pe.action.Executable;
import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;

/**
 * 
 *
 */
public class CheckAction extends AbstractAction{

	private List checkers;

	/**
	 * 
	 */
	public CheckAction() {
		super();
	}

	/* (non-Javadoc)
	 * @see com.csii.pe.action.Executable#execute(com.csii.pe.core.Context)
	 */
	public void execute(Context ctx) throws PeException {

		if (checkers != null && !checkers.isEmpty()) {

			for (int i = 0; i < checkers.size(); i++) {

				((Executable) checkers.get(i)).execute(ctx);
			}

		}

	}

	/**
	 * @param list
	 */
	public void setCheckers(List list) {
		checkers = list;
	}

}
