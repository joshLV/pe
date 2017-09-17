<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
	<head>
		<title>微信支付退款查询测试页面</title>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<link href="css/index.css" rel="stylesheet" type="text/css">
		<script type="text/javascript" src="js/jquery-1.4.2.min.js"></script>
		<script type="text/javascript">
			$(function(){
				$('.hideClass').hide();
			});
			function doSubmit(){
				
				var out_refund_no = $.trim($('input[name=out_refund_no]').val());
				if(out_refund_no == ''){
					alert('退款单号不能为空！');
					return false;
				}
				$("form").submit();
			}
		</script>
	</head>
<body text=#000000 bgColor="#ffffff" leftMargin=0  topMargin=4>
	<div id="main">
        <div class="cashier-nav">
            <ol>
				<li class="current">微信支付退款查询</li> 
            </ol>
        </div>
        <form action="testRefundQuery" method="post">
            <div id="body" style="clear:left">
                <dl class="content">
					
                    <dt>商户退款单号：</dt>
                    <dd>
                        <span class="null-star"></span>
                        <input name="out_refund_no" value="" maxlength="32" size="30"  placeholder="长度32"/>
                        <span>(长度32)</span>
                        <span></span>
                    </dd>
                    
                    <dt></dt>
                    <dd>
                        <span class="new-btn-login-sp">
                            <button class="new-btn-login" type="button" onclick="doSubmit()" style="text-align:center;">确 认</button>
                        </span>
                    </dd>
                </dl>
            </div>
		</form>
        <div id="foot">
			<ul class="foot-ul">
				<li><font class="note-help">商户订单号、<!-- 通道订单号、微信订单号、 -->商户退款单号<!-- 、通道退款单号、微信退款单号 -->至少填一个。 </font></li>
			</ul>
		</div>
	</div>
</body>
</html>