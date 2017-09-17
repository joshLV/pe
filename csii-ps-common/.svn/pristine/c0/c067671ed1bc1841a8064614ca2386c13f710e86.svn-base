package com.csii.pp.signature;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.codec.binary.Base64;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.transforms.Transforms;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XmlSignature {
	
	    private static final String CHARSET            = "UTF-8";
	    private static final String SIGN_ALGORITHM     = "SHA256withRSA";
	    private static final String XML_ALGORITHM      = "http://www.w3.org/2001/04/xmldsig-more#rsa-sha256";
	    private static final String REQUEST_TAG_NAME   = "request";
	    private static final String RESPONSE_TAG_NAME  = "response";
	    private static final String SIGNATURE_TAG_NAME = "Signature";
	
	/**
     * 读取公钥
     *
     * @param keyData 公钥数据， 注意是-----BEGIN PUBLIC KEY----- 和 -----END PUBLIC KEY----- 中间的内容
     * @return the public key
     * @throws GeneralSecurityException the general security exception
     */
	    public static PublicKey getPublicKey(final byte[] keyData) throws Exception {

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        byte[] encodedKey = Base64.decodeBase64(keyData);
        return keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));
    }

    /**
     * 读取私钥
     * 
     * @param keyData 私钥数据
     * @return
     * @throws Exception
     */
    public static PrivateKey getPrivateKey(final byte[] keyData) throws Exception {

        byte[] encodedKey = Base64.decodeBase64(keyData);
        final PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encodedKey);
        final KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(keySpec);
    }
    
    /**
     * 私钥签名请求报文
     * 
     * @param requestMessage
     * @param privateKey
     * @return
     * @throws Exception 
     */
    public static String signRequestMessage(String requestMessage, PrivateKey privateKey) throws Exception {
        return signXmlMessage(requestMessage, REQUEST_TAG_NAME, privateKey);
    }

    /**
     * 私钥签名应答报文
     * 
     * @param responseMessage
     * @param privateKey
     * @return
     * @throws Exception 
     */
    public static String signResponseMessage(String responseMessage, PrivateKey privateKey)
                                                                                           throws Exception {
        return signXmlMessage(responseMessage, RESPONSE_TAG_NAME, privateKey);
    }

    /**
     * 私钥签名
     * 
     * @param xmlMessageSource 待签名的XML
     * @param elementTagName
     * @param privateKey
     * @return
     * @throws Exception 
     */
    public static String signXmlMessage(String xmlMessageSource, String elementTagName,
                                        PrivateKey privateKey) throws Exception {
    	org.apache.xml.security.Init.init();
        Document xmlDocument = getDocument(xmlMessageSource);
        XMLSignature xmlSignature = new XMLSignature(xmlDocument, xmlDocument.getDocumentURI(),
            XML_ALGORITHM);

        NodeList nodeList = xmlDocument.getElementsByTagName(elementTagName);
        if (nodeList == null || nodeList.getLength() != 1) {
            throw new Exception("Document element with tag name " + elementTagName + " not fount");
        }
        Node elementNode = nodeList.item(0);
        elementNode.getParentNode().appendChild(xmlSignature.getElement());

        Transforms transforms = new Transforms(xmlDocument);
        transforms.addTransform(Transforms.TRANSFORM_ENVELOPED_SIGNATURE);
        xmlSignature.addDocument("", transforms, Constants.ALGO_ID_DIGEST_SHA1);

        xmlSignature.sign(privateKey);

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        XMLUtils.outputDOM(xmlDocument, os);
        return os.toString(CHARSET);
    }

    /**
     * 验证XML签名
     *
     * @param xmlMessage 带有签名的XML
     * @param publicKey 公钥
     * 
     * @return 签名验证结果 boolean
     * @throws Exception the exception
     */
    public static boolean verifyXmlMessage(String xmlMessage, PublicKey publicKey) throws Exception {

        Document xmlDocument = getDocument(xmlMessage);
        NodeList signatureNodes = xmlDocument.getElementsByTagNameNS(Constants.SignatureSpecNS,
            SIGNATURE_TAG_NAME);
        if (signatureNodes == null || signatureNodes.getLength() != 1) {
            throw new Exception("Document element with tag name " + SIGNATURE_TAG_NAME
                                + " not fount");
        }
        Element signElement = (Element) signatureNodes.item(0);
        XMLSignature signature = new XMLSignature(signElement, "");
        return signature.checkSignatureValue(publicKey);
    }
    
    /**
     * 解析XML字符串
     * 
     * @param xmlMessageSource
     * @return
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     * @throws UnsupportedEncodingException
     */
    private static Document getDocument(String xmlMessageSource)
                                                                throws ParserConfigurationException,
                                                                SAXException, IOException,
                                                                UnsupportedEncodingException {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document xmlDocument = builder.parse(new InputSource(new ByteArrayInputStream(
            xmlMessageSource.getBytes(CHARSET))));
        return xmlDocument;
    }

    /**
     * 对字符串进行签名
     *
     * @param source 待签名的数据
     * @param privateKey 私钥
     * @return Base64编码后的签名信息
     * @throws Exception
     */
    public static String sign(String source, PrivateKey privateKey) throws Exception {

        final Signature signatureChecker = Signature.getInstance(SIGN_ALGORITHM);
        signatureChecker.initSign(privateKey);
        signatureChecker.update(source.getBytes(CHARSET));
        return Base64.encodeBase64String(signatureChecker.sign());
    }

    /**
     * 对字符串进行验签
     * 
     * @param source 待验签的数据
     * @param signature Base64编码后的签名信息
     * @param publicKey 公钥
     * @return
     * @throws Exception
     */
    public static boolean verify(String source, String signature, PublicKey publicKey)
                                                                                      throws Exception {
        final Signature signatureChecker = Signature.getInstance(SIGN_ALGORITHM);
        signatureChecker.initVerify(publicKey);
        signatureChecker.update(source.getBytes(CHARSET));
        return signatureChecker.verify(Base64.decodeBase64(signature));
    }
    
}
