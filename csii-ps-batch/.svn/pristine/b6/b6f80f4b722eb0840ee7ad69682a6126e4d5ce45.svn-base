package com.csii.batch.check;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.csii.pe.core.PeException;
import com.csii.pp.order.Order;

public class SequenceChecker implements Checker {

	private List checkers;

	/* (non-Javadoc)
	 * @see com.csii.pp.check.Checker#check(java.util.Map)
	 */
	public boolean check(Map data) throws PeException {
		boolean result = false;
		if (checkers != null && !checkers.isEmpty()) {
			for (Iterator it = checkers.iterator(); it.hasNext();) {
				Checker checker = (Checker) it.next();
				if (checker.check(data)) {
					break;
				}
			}
		}
		return result;
	}

	/**
	 * @param list
	 */
	public void setCheckers(List list) {
		checkers = list;
	}

//	public void check(Order order) throws PeException {
//		//boolean result = false;
//		if (checkers != null && !checkers.isEmpty()) {
//			for (Iterator it = checkers.iterator(); it.hasNext();) {
//				Checker checker = (Checker) it.next();
//				checker.check(order);
//			}
//		}
//	}
}
