package com.corefire.action;

import java.io.IOException;
import java.util.Date;
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
 * <功能详细描述>退款
 * 
 * @author Administrator
 * @version [版本号, 2014-8-29]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
public class TestRefundServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

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
		map.put("op_user_id", CorefireConfig.mch_id);
		map.put("trans_id", "SQTK");
		map.put("nonce_str", String.valueOf(new Date().getTime()));
		

		Map<String, String> params = SignUtils.paraFilter(map);
		StringBuilder buf = new StringBuilder((params.size() + 1) * 10);
		SignUtils.buildPayParams(buf, params, false);
		String preStr = buf.toString();
		String sign = MD5.sign(preStr, "&key=" + CorefireConfig.key, "utf-8").toUpperCase();
		map.put("sign", sign);

		String reqUrl = CorefireConfig.refund_req_url;
		String reqJson = XmlUtils.parseJSON(map);
		req.setAttribute("req_message", reqJson);

		System.out.println("===========================================");
		System.out.println("请求Url:" + reqUrl);
		System.out.println("请求数据:" + reqJson);
		System.out.println("===========================================");

		try {
			String res = CorefireHttpPost.connect(reqUrl, reqJson);
			req.setAttribute("res_message", res);

			System.out.println("===========================================");
			System.out.println("返回数据：" + res);
			System.out.println("===========================================");

			Map<String, String> resultMap = XmlUtils.json2map(res);

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
