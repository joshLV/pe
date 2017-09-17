/*jadclipse*/// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.

package com.csii.pe.service.comm;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.csii.pe.core.PeException;
import com.csii.pe.transform.Parser;
import com.csii.pe.transform.TransformException;
import com.csii.pe.transform.Transformer;
import com.csii.pe.transform.TransformerFactoryInterface;
import com.csii.pp.signature.XmlSignature;
import com.csii.pp.util.MiscUtil;

public class TransformTransport
    implements Transport
{
	protected Log log;
    public TransformTransport()
    {
    	log = LogFactory.getLog(getClass());
        _fldcase = "FormatOutbound";
        _fldchar = "ParseOutbound";
        _fldif = false;
        _fldnew = "/tmp";
        _fldint = 0L;
    }

    public static Map getInputData()
    {
        List list = (List)_fldtry.get();
        int i = list.size();
        return i != 0 ? (Map)list.get(i - 1) : null;
    }

    public static void setInputData(Map map)
    {
        List list = (List)_fldtry.get();
        list.add(map);
    }

    public static void unsetInputData()
    {
        List list = (List)_fldtry.get();
        if(list.size() > 0)
            list.remove(list.size() - 1);
    }

    public Object submit(Object obj)
        throws CommunicationException
    {
        setInputData((Map)obj);
        long l;
        Object obj2 = null;
        Transformer transformer = _flddo.getTransformer(_fldcase);
        Object obj1 = null;
        String mybankJfPriKey = MiscUtil.toStringAndTrim(((Map)obj).get("mybankJfPriKey"));
        String mybankPubKey = MiscUtil.toStringAndTrim(((Map)obj).get("mybankPubKey"));
        String submitURL = MiscUtil.toStringAndTrim(((Map)obj).get("submitURL"));
		try {
			obj1 = transformer.format(obj, (Map)obj);
		} catch (TransformException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String signedData="";
		try {
			String xmlData = new String((byte[])obj1,"utf-8");
			signedData = XmlSignature.signRequestMessage(xmlData, XmlSignature.getPrivateKey(mybankJfPriKey.getBytes()));
			log.info("\n发送网商银行报文：\n"+signedData);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error("数据签名异常",e);
			throw new CommunicationException(e);
		}
        l = _fldint++;
      //设置的transport 是否是debug模式 如果是 则打印报文
        if(_fldif)
            try
            {	//把报文内容写入文件中
                FileOutputStream fileoutputstream = new FileOutputStream(_fldnew + '/' + _fldcase + l + (new Time(System.currentTimeMillis())).toString().replace(':', '_'));
                fileoutputstream.write(signedData.getBytes());
                fileoutputstream.flush();
                fileoutputstream.close();
            }
            catch(Exception exception)
            {
                exception.printStackTrace();
            }
            //从tcp协议发送转换成http协议
//        obj2 = transport.submit(obj1);
            HttpClientMessageSender sender = new HttpClientMessageSender();
    		String respesXml = "";
			try {
				respesXml = sender.send(signedData,submitURL);
				log.info("\n接收网商银行报文：\n"+respesXml);
			} catch (PeException e) {
				// TODO Auto-generated catch block
				log.info("发送网商银行报文失败，",e);
				throw new CommunicationException("E00050");
			}
			if(MiscUtil.isNullOrEmpty(respesXml)){
				throw new CommunicationException("E00049");
			}else if(!respesXml.startsWith("<document")){
				throw new CommunicationException(respesXml);
			}
			
		try {
			if(!XmlSignature.verifyXmlMessage(respesXml, XmlSignature.getPublicKey(mybankPubKey.getBytes()))){
				log.error("网商银行返回报文验证签名失败");
				throw new PeException("000002");
			}else{
				log.info("网商银行返回报文验证签名成功");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error("网商银行返回报文验证签名异常",e);
			throw new CommunicationException("000002");
		}
        obj2 = getResult(respesXml);
        if(obj2 == null)
        {
            unsetInputData();
            return null;
        }
        ByteArrayInputStream bytearrayinputstream = null;
        Transformer transformer1;
        Object obj6;
        //设置的transport 是否是debug模式 如果是 则打印报文
        if(_fldif)
            try
            {
            	//把报文内容写入文件中
                FileOutputStream fileoutputstream1 = new FileOutputStream(_fldnew + '/' + _fldchar + l + (new Time(System.currentTimeMillis())).toString().replace(':', '_'));
                fileoutputstream1.write(obj2.toString().getBytes());
                fileoutputstream1.flush();
                fileoutputstream1.close();
            }
            catch(Exception exception1)
            {
                exception1.printStackTrace();
            }
        if(_fldelse != null)
			try {
				obj2 = _fldelse.parse(obj2, (Map)obj);
			} catch (TransformException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//xiugai 2 hang
//			(byte[])obj2;
		byte[] dist =respesXml.getBytes();
		bytearrayinputstream = new ByteArrayInputStream(dist);
		
			
//        bytearrayinputstream = new ByteArrayInputStream((byte[])obj2);
          transformer1 = _flddo.getTransformer(_fldchar);
//        if(!_fldif)
//        if(_fldbyte == null)
//        Object obj3 = transformer1.parse(bytearrayinputstream, (Map)obj);
//        obj3 = _fldbyte.parse(obj3, (Map)obj);
//        System.err.println((new StringBuilder("result:")).append(obj3).toString());
//        obj6 = obj3;
//        unsetInputData();
//        return obj6;
        Exception exception2;
        Object obj5;
        Object obj7;
        try
        {
            Object obj4 = transformer1.parse(bytearrayinputstream, (Map)obj);
            System.err.println((new StringBuilder("result:")).append(obj4).toString());
            obj7 = obj4;
        }
        catch(TransformException transformexception)
        {
            throw new CommunicationException(transformexception.getMessageKey(), transformexception);
        }
        finally
        {
            unsetInputData();
        }
        unsetInputData();
        return obj7;
//        if(_fldbyte == null)
//            break MISSING_BLOCK_LABEL_544;
//        obj5 = transformer1.parse(bytearrayinputstream, (Map)obj);
//        obj7 = _fldbyte.parse(obj5, (Map)obj);
//        unsetInputData();
//        return obj7;
//        obj7 = transformer1.parse(bytearrayinputstream, (Map)obj);
//        unsetInputData();
//        return obj7;
//        throw exception2;
    }

    protected Object getResult(Object obj)
        throws CommunicationException
    {
        return obj;
    }

    public void setTransformerFactory(TransformerFactoryInterface transformerfactoryinterface)
    {
        _flddo = transformerfactoryinterface;
    }

    public void setTransport(Transport transport1)
    {
        transport = transport1;
    }

    public void setFormatName(String s)
    {
        _fldcase = s;
    }

    public void setParseName(String s)
    {
        _fldchar = s;
    }

    public void setDebug(boolean flag)
    {
        _fldif = flag;
    }

    public void setDumpPath(String s)
    {
        _fldnew = s;
    }

    public void setHeadFormatter(Formatter formatter)
    {
        _fldfor = formatter;
    }

    public void setStreamFormatter(Formatter formatter)
    {
        a = formatter;
    }

    public void setStreamParser(Parser parser)
    {
        _fldelse = parser;
    }

    public void setAfterParser(Parser parser)
    {
        _fldbyte = parser;
    }
    private TransformerFactoryInterface _flddo;
    private String _fldcase;
    private String _fldchar;
    protected Transport transport;
    private boolean _fldif;
    private String _fldnew;
    private Formatter _fldfor;
    private Formatter a;
    private Parser _fldelse;
    private Parser _fldbyte;
    private long _fldint;
	private static ThreadLocal _fldtry = new ThreadLocal() {

        protected Object initialValue()
        {
            return new ArrayList();
        }

    };

}