<%@ page contentType="text/html; charset=utf-8" pageEncoding="UTF-8" %>
<%@ page isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ page import="java.util.Enumeration"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title></title>
<style type="text/css">
.loadingDiv{
	background:url(images/load.gif)  no-repeat;
	background-color:#ffffff;
	height:60px;
	width:160px;
	font-family:'宋体',"Times New Roman", Times, serif;
	font-size:15px;
	color:#1E90FF;
	text-align:left;
	padding-top:20px;
	padding-left:60px;
	padding-right:15px;
	font-weight:bold;
	border:0px;
}
</style>
<script language="JavaScript" type="text/javascript">
		window.onload = function() {
		   document.getElementById('mainForm').submit();
		}
</script>
</head>

<body>
	<div id="loading" class="loadingDiv" style="position:absolute;left:38%;top:200px; display:block;z-index:1000">请稍候 .....</div>

	<form name="formMain" id="mainForm" method="post" action="${path_url}">		    
<%
	Enumeration<String> requestAttributeNames = request.getAttributeNames();
	//商户发送的报文key值
	String params = "version;encoding;certId;signature;signMethod;txnType;txnSubType;bizType;channelType;frontUrl;backUrl;accessType;acqInsCode;merCatCode;merId;merName;merAbbr;orderId;txnTime;accType;accNo;customerInfo;reqReserved;reserved;riskRateInfo;encryptCertId;userMac;bindId;relTxnType;payCardType;issInsCode";
	while(requestAttributeNames.hasMoreElements()){
		String name = requestAttributeNames.nextElement();
		if(params.contains(name)){
		String value = (String)request.getAttribute(name);
%>	
		<input type="hidden" name = '<%=name %>' value ='<%=value%>'/>
<%		}	
	}
%>

	</form>

</body>
</html>