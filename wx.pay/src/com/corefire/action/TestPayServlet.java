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
 * <一句话功能简述>
 * <功能详细描述>测试支付
 * 
 * @author Administrator
 * @version [版本号, 2014-8-28]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
public class TestPayServlet extends HttpServlet {
	// 用来存储订单的交易状态(key:订单号，value:状态(0:未支付，1：已支付)) ---- 这里可以根据需要存储在数据库中
	public static Map<String, String> orderResult;

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
		map.put("trans_id", "APMP");
		map.put("sub_mch_id", CorefireConfig.sub_mch_id);

		Map<String, String> params = SignUtils.paraFilter(map);
		StringBuilder buf = new StringBuilder((params.size() + 1) * 10);
		SignUtils.buildPayParams(buf, params, false);
		String preStr = buf.toString();

		String sign = MD5.sign(preStr, "&key=" + CorefireConfig.key, "utf-8").toUpperCase();

		map.put("sign", sign);

		String reqUrl = CorefireConfig.micropay_req_url;
		String reqJson = XmlUtils.parseJSON(map);
		req.setAttribute("req_message", reqJson);

		System.out.println("===========================================");
		System.out.println("reqUrl：" + reqUrl);
		System.out.println("请求数据:" + reqJson);
		System.out.println("===========================================");

		try {
			String res = CorefireHttpPost.connect(reqUrl, reqJson);

			Map<String, String> resultMap = XmlUtils.json2map(res);

			System.out.println("===========================================");
			System.out.println("返回数据：" + res);
			System.out.println("===========================================");

			req.setAttribute("res_message", res);

			String result = "通信状态:" + resultMap.get("return_code");

			if ("FAIL".equals(resultMap.get("return_code"))) {
				result = result + "<br>" + "错误描述:" + resultMap.get("return_msg");
			} else if ("SUCCESS".equals(resultMap.get("return_code"))) {
				if (resultMap.containsKey("sign")) {
					if (!SignUtils.checkParam(resultMap, CorefireConfig.key)) {
						result = result + "<br>错误描述:" + "验证签名不通过";
					} else {
						result = result + "<br>" + "业务状态:" + resultMap.get("result_code");
						if ("FAIL".equals(resultMap.get("result_code"))) {
							result = result + "<br>" + "错误代码:" + resultMap.get("err_code");
							result = result + "<br>" + "错误描述:" + resultMap.get("err_code_des");
						}
					}
				} else {
					result = result + "<br>错误描述:" + "没有签名信息";
				}
			}
			req.setAttribute("result", result);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		req.getRequestDispatcher("index-result.jsp").forward(req, resp);
	}
}
