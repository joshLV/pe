package com.csii.pp.order;

/**
 * sql.Date - util.Date 反转
 */
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.orm.ibatis.SqlMapClientOperations;

import com.csii.pe.config.support.SqlMapAware;
import com.csii.pp.dict.Dict;
import com.csii.pp.util.MiscUtil;

//import java.util.Date;

/**
 * 订单管理类，所有关于订单的操作通过此类进行
 * 
 * @author X
 */
public class OrderManager implements SqlMapAware {

	private SqlMapClientOperations sqlMap;

	public OrderManager() {
		super();
	}

	// 查询清分订单
	public Order getBtClearTransByTransSeqNo(String transSeqNo) {
		Map para = new HashMap();
		para.put(Dict.PPTRANSSEQNO, transSeqNo);
		return (Order) this.getSqlMap().queryForObject("pp.core.queryClearOrderByTransSeqNo", para);
	}

	public Order getBtClearTransByOrgTransSeqNo(String transSeqNo) {
		Map para = new HashMap();
		para.put(Dict.PPORIGTRANSSEQNO, transSeqNo);
		return (Order) this.getSqlMap().queryForObject("pp.core.queryClearOrderByOrgTransSeqNo", para);
	}

	// 查询原订单
	public Order getOriginalOrder(Order order) {
		Order origOrder = this.getOrgOrder(order.getOrgTransSeqNo());
		if (origOrder == null) {
			origOrder = this.getHisOrgOrder(order.getOrgTransSeqNo());
		}
		return origOrder;
	}

	// 查询原订单by merseqno merdate
	public Order getOriginalOrderByMerInfo(Order order) {
		
		Order origOrder = this.getOrgOrderByMer(order.getOrgMerSeqNo(),order.getOrgMerTransDate());
		if (origOrder == null) {
			origOrder = this.getHisOrgOrderByMer(order.getOrgMerSeqNo(),order.getOrgMerTransDate());
		}
		return origOrder;
	}
		
	// 交易表中查询原交易-商户号、清算日期、平台流水号
	public Order getOrgOrder(String origTransSeqNo) {
		Map para = new HashMap();
		para.put(Dict.PPORIGTRANSSEQNO, origTransSeqNo);

		return (Order) this.getSqlMap().queryForObject("pp.core.queryOrgOrder", para);
	}

	// 交易历史表中查询原交易，by merseqno merdate
	public Order getHisOrgOrderByMer(String orgmerseqno,Date orgmerdate) {
		Map para = new HashMap();
		para.put("orgmerseqno", orgmerseqno);
		para.put("orgmerdate", orgmerdate);
		return (Order) this.getSqlMap().queryForObject("pp.core.queryHisOrgOrderByMer", para);
	}

	// 交易表中查询原交易-商户号、清算日期、平台流水号 by merseqno merdate
	public Order getOrgOrderByMer(String orgmerseqno,Date orgmerdate) {
		Map para = new HashMap();
		para.put("orgmerseqno", orgmerseqno);
		para.put("orgmerdate", orgmerdate);
		return (Order) this.getSqlMap().queryForObject("pp.core.queryOrgOrderByMer", para);
	}

	// 交易历史表中查询原交易
	public Order getHisOrgOrder(String origTransSeqNo) {
		Map para = new HashMap();
		para.put(Dict.PPORIGTRANSSEQNO, origTransSeqNo);
		return (Order) this.getSqlMap().queryForObject("pp.core.queryHisOrgOrder", para);
	}

	public int updateClearOrder(Order order) {
		return this.getSqlMap().update("pp.core.updateClearOrder", order);
	}
	
	public int updateDayClearOrder(Order order) {
		return this.getSqlMap().update("pp.core.updateDayClearOrder", order);
	}

	// 更新订单信息
	public int updateOrder(Order order) {
		return getSqlMap().update("pp.core.updateOrder", order);
	}

	// 更新历史订单信息
	public int updateHistoryOrder(Order order) {
		return getSqlMap().update("pp.core.updateHistoryOrder", order);
	}

	// 查询历史订单
	public Order getHistoryOrder(String transSeqNo, Date transDate) {
		Map para = new HashMap();
		para.put(Dict.PPTRANSSEQNO, transSeqNo);
		para.put(Dict.PPTRANSDATE, transDate);

		return (Order) this.getSqlMap().queryForObject("pp.core.queryHistoryOrder", para);
	}

	// 查询历史订单
		public Order getHistoryOrderByOrgTransSeqNo(String transSeqNo, Date transDate) {
			Map para = new HashMap();
			para.put(Dict.PPORIGTRANSSEQNO, transSeqNo);
			para.put(Dict.PPTRANSDATE, transDate);

			return (Order) this.getSqlMap().queryForObject("pp.core.queryHistoryOrderByOrgTransSeqNo", para);
		}
		
	public Order getOtOrder(String transSeqNo) {
		Map dataMap = new HashMap();
		dataMap.put("TransSeqNo", transSeqNo);
		return (Order) this.getSqlMap().queryForObject("pp.core.queryOtByTransSeqNo", dataMap);
	}

	public Order getHtOrder(String transSeqNo) {
		Map dataMap = new HashMap();
		dataMap.put("TransSeqNo", transSeqNo);
		return (Order) this.getSqlMap().queryForObject("pp.core.queryHtByTransSeqNo", dataMap);
	}

	public Order getOrder(String transSeqNo) {
		Order order = this.getOtOrder(transSeqNo);
		if (order == null) {
			order = this.getHtOrder(transSeqNo);
		}
		return order;
	}
	public Order getOrderBySeqNoMerDate(String transSeqNo,String merDate) {
		Order order = this.getOtOrderBySeqNoMerDate(transSeqNo,merDate);
		if (order == null) {
			order = this.getHtOrderBySeqNoMerDate(transSeqNo,merDate);
		}
		return order;
	}
	
	//new  add
	public Order getHtOrderBySeqNoMerDate(String transSeqNo,String merDate) {
		Map dataMap = new HashMap();
		dataMap.put("TransSeqNo", transSeqNo);
		dataMap.put("merDate", merDate);
		return (Order) this.getSqlMap().queryForObject("pp.core.queryHtByTransSeqNoMerDate", dataMap);
	}
	//new  add
	public Order getOtOrderBySeqNoMerDate(String transSeqNo,String merDate) {
		Map dataMap = new HashMap();
		dataMap.put("TransSeqNo", transSeqNo);
		dataMap.put("merDate", merDate);
		return (Order) this.getSqlMap().queryForObject("pp.core.queryOtByTransSeqNoMerDate", dataMap);
	}
	public int updateOrderInOtAndHt(Order order) {
		int ret = this.updateOrder(order);
		if (ret < 1) {
			ret = this.updateHistoryOrder(order);
		}
		return ret;
	}
	
	//获取清分订单
	public List getOrderForClear(Map para) {
		return getSqlMap().queryForList("pp.core.queryOrderForClear", para);
	}
	// 查询重复订单
	public List getDuplicateOrder(Order order) {
		List ret = getSqlMap().queryForList(
				"pp.core.queryMerchantDuplicateOrder", order);
		if (ret == null) {
			ret = new ArrayList();
		}
		return ret;
	}
	@Override
	public void setSqlMap(SqlMapClientOperations sqlMap) {
		this.sqlMap = sqlMap;
	}

	public SqlMapClientOperations getSqlMap() {
		return sqlMap;
	}
	

	public List getOrderForDayClear(Map para) {
		return getSqlMap().queryForList("pp.core.queryOrderForDayClear", para);
	}

}
