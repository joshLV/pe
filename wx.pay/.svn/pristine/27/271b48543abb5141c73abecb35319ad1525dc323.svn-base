package com.corefire.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


/**
 * <一句话功能简述>
 * <功能详细描述>配置信息
 * 
 * @author  Administrator
 * @version  [版本号, 2014-8-29]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public class CorefireConfig {
    
    /**
     * 交易密钥
     */
    public static String key ;
    
    /**
     * 商户号
     */
    public static String mch_id;
    
    /**
     * 芯火子商户号
     */
    public static String sub_mch_id;
    
    /**
     * 芯火appid
     * appid
     */
    public static String appid;
    
    /**
     * 微信刷卡支付请求url
     */
    public static String micropay_req_url;
    
    /**
    * 微信扫码支付请求url
    */
   public static String native_req_url;
    
    /**
     * 订单查询请求url
     */
    public static String query_req_url;
    
    /**
     * 退款请求url
     */
    public static String refund_req_url;
    
    /**
     * 退款查询请求url
     */
    public static String refund_query_req_url;
    
    /**
     * 撤销订单请求url
     */
    public static String reverse_req_url;
    
    /**
     * 下载账单请求url
     */
    public static String bill_req_url;
    
    /**
     * 通知url
     */
    public static String notify_url;
    
    /**
     * 通用url
     */
    public static String common_req_url;
    
    
   


	static{
        Properties prop = new Properties();   
        InputStream in = CorefireConfig.class.getResourceAsStream("/config.properties");   
        try {   
        	
            prop.load(in);   
            key = prop.getProperty("key").trim();   
            mch_id = prop.getProperty("mch_id").trim();
            sub_mch_id = prop.getProperty("sub_mch_id").trim();
            appid = prop.getProperty("appid").trim();
            micropay_req_url = prop.getProperty("micropay_req_url").trim();
            native_req_url = prop.getProperty("native_req_url").trim();
            query_req_url = prop.getProperty("query_req_url").trim();
            refund_req_url = prop.getProperty("refund_req_url").trim();
            refund_query_req_url = prop.getProperty("refund_query_req_url").trim();
            reverse_req_url = prop.getProperty("reverse_req_url").trim();
            bill_req_url = prop.getProperty("bill_req_url").trim();
            notify_url =prop.getProperty("notify_url").trim();   
            common_req_url =prop.getProperty("common_req_url").trim();   
        } catch (IOException e) {   
            e.printStackTrace();   
        } 
    }
}
