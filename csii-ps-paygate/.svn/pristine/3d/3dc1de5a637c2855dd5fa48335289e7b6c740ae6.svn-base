package com.csii.pe.service.comm;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.csii.pe.core.PeException;
import com.csii.pp.transport.ssl.EasySSLProtocolSocketFactory;
import com.csii.pp.util.WxUtil;


/**
 *
 * 这个例子用来测试报文的发送。
 * 报文发送事例代码，这里使用了MultiThreadedHttpConnectionManager 在发送大量数据时，可以保证资源的回收利用。
 *
 *

 */
public class HttpClientMessageSender {

    private HttpConnectionManager connectionManager;
    private static Logger log = LoggerFactory.getLogger(HttpClientMessageSender.class);
    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        // TODO 根据自己需要填写reqXml，这里的reqXml不是文件名，而是文件的内容。
        String reqXml = "FOR TEST";
//        Init.init();

        HttpClientMessageSender messageSender = new HttpClientMessageSender();
        String resultXml = messageSender.send(reqXml,
            "http://121.0.26.34/message/gateway.do");
//        String resultXml = messageSender.send(reqXml,
//        "http://localhost:9089/katong/connect.do");
        System.out.println("收到的报文是:"+resultXml);
//        System.out.println("校验签名的结果是:" + SignUtil.check(resultXml, KeyUtil.getAlipayPubKey()));

    }

    public String send(String reqXml, String postUrl) throws PeException {

        // 发送报文
        HttpClient httpClient = new HttpClient(connectionManager);

        PostMethod method = new PostMethod(postUrl);

        method.addRequestHeader("Content-Type", "text/xml; charset=utf-8");
        try {
            method.setRequestEntity(new StringRequestEntity(reqXml, null, "utf-8"));

            httpClient.executeMethod(method);


            String requestcharset = method.getRequestCharSet();

            int statuscode  = method.getStatusCode();
            if(200 !=statuscode){
            	throw new PeException("E00050");
            }
            System.err.print("statuscode========/n"+statuscode);
            System.err.print("requestcharset========"+requestcharset);
            // 获得返回报文
            String resXml = method.getResponseBodyAsString();

            return resXml;
        } catch (Exception e) {
            //根据需要自行处理日志
           throw new PeException(e);
        } finally {
            method.releaseConnection();
        }
    }

    public HttpClientMessageSender() {
        super();
        
        ProtocolSocketFactory pSF = new EasySSLProtocolSocketFactory();
		Protocol.registerProtocol("https", new Protocol("https", pSF, 443));
        // 创建一个线程安全的HTTP连接池
        connectionManager = new MultiThreadedHttpConnectionManager();

        HttpConnectionManagerParams params = new HttpConnectionManagerParams();
        // 连接建立超时
        params.setConnectionTimeout(10000);
        // 数据等待超时
        params.setSoTimeout(30000);
        // 默认每个Host最多10个连接
        params.setDefaultMaxConnectionsPerHost(10);
        // 最大连接数（所有Host加起来）
        params.setMaxTotalConnections(200);

        connectionManager.setParams(params);
    }

}