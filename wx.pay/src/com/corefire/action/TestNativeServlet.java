package com.corefire.action;

import java.io.IOException;
import java.util.Map;
import java.util.SortedMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.corefire.config.CorefireConfig;
import com.corefire.util.CorefireHttpPost;
import com.corefire.util.MD5;
import com.corefire.util.SignUtils;
import com.corefire.util.XmlUtils;

/**
 * <一句话功能简述> <功能详细描述>测试支付
 * 
 * @author Administrator
 * @version [版本号, 2014-8-28]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
public class TestNativeServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	public static Map<String, String> orderResult; // 用来存储订单的交易状态(key:订单号，value:状态(0:未支付，1：已支付))
													// ---- 这里可以根据需要存储在数据库中

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setCharacterEncoding("utf-8");
		resp.setCharacterEncoding("utf-8");

		@SuppressWarnings("unchecked")
		SortedMap<String, String> map = XmlUtils.getParameterMap(req);
		map.put("sub_mch_id", CorefireConfig.sub_mch_id);
		map.put("appid", CorefireConfig.appid);
		map.put("notify_url", CorefireConfig.notify_url);
		map.put("trans_id", "TPCP"); // 二维码生成交易

		Map<String, String> params = SignUtils.paraFilter(map);
		StringBuilder buf = new StringBuilder((params.size() + 1) * 10);
		SignUtils.buildPayParams(buf, params, false);
		String preStr = buf.toString();
		String sign = MD5.sign(preStr, "&key=" + CorefireConfig.key, "utf-8").toUpperCase();

		map.put("sign", sign);

		String reqUrl = CorefireConfig.native_req_url;
		String reqJson = XmlUtils.parseJSON(map);
		req.setAttribute("req_message", reqJson);

		System.out.println("===========================================");
		System.out.println("请求Url：" + reqUrl);
		System.out.println("请求数据:" + reqJson);
		System.out.println("===========================================");

		boolean forward = false;
		String result = "";
		try {
			String res = CorefireHttpPost.connect(reqUrl, reqJson);
			req.setAttribute("res_message", res);

			System.out.println("===========================================");
			System.out.println("返回数据：" + res);
			System.out.println("===========================================");

			Map<String, String> resultMap = XmlUtils.json2map(res);

			if (!"000000".equals(resultMap.get("ReturnCode"))) {
				result = result + "<br>" + "错误描述:" + resultMap.get("ReturnMsg");
			} else if ("000000".equals(resultMap.get("ReturnCode"))) {
				result = result + "<br>" + "业务状态:" + resultMap.get("ReturnCode");
				if (!"000000".equals(resultMap.get("ReturnCode"))) {
					result = result + "<br>" + "错误代码:" + resultMap.get("ReturnCode");
					result = result + "<br>" + "错误描述:" + resultMap.get("ReturnMsg");
				} else if ("000000".equals(resultMap.get("ReturnCode"))) {
					forward = true;
					String code_url = resultMap.get("code_url");
					req.setAttribute("code_url", code_url);
					req.setAttribute("out_trade_no", resultMap.get("out_trade_no"));
					req.setAttribute("total_fee", resultMap.get("total_fee"));
					req.setAttribute("body", map.get("body"));
					req.getRequestDispatcher("index-native-result.jsp").forward(req, resp);
				} else {
					result = result + "<br>错误描述:" + "没有签名信息";
				}
			}
			req.setAttribute("result", result);
		} catch (Exception e1) {
			e1.printStackTrace();
			result = "系统异常";
			req.setAttribute("result", result);
		}
		if (!forward) {
			req.getRequestDispatcher("index-result.jsp").forward(req, resp);
		}
	}
}
