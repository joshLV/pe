<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
	<head>
		<title>微信刷卡支付退款测试页面</title>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<link href="css/index.css" rel="stylesheet" type="text/css">
		<script type="text/javascript" src="js/jquery-1.4.2.min.js"></script>
		<script type="text/javascript">
			/* $(function(){
				$('.hideClass').hide();
// 				$('input[name=out_refund_no]').val(new Date().getTime());
				$('input[name=mch_id]').blur(function(){
					var value = $.trim($(this).val());
					if(value != ''){
						$('input[name=op_user_id]').val(value); 
					}
				});
			}); */
			
			function checkNum(num){
				var reg = /^\+?[1-9][0-9]*$/;
				return num.match(reg);
			}
			
			function doSubmit(){
				
				var out_trade_no = $.trim($('input[name=out_trade_no]').val());
				if(out_trade_no == ''){
					alert('商户订单号不能为空！');
					return false;
				}
				var total_fee = $.trim($('input[name=total_fee]').val());
				if(total_fee == ''){
					alert('总金额不能为空');
					return false;
				}
				var refund_fee = $.trim($('input[name=refund_fee]').val());
				if(refund_fee == ''){
					alert('退款金额不能为空');
					return false;
				}
				
				if(!checkNum(total_fee) || !checkNum(refund_fee)){
					alert('金额只能是正整数');
					return false;
				}
				
				if(parseInt(total_fee) <= 0 || parseInt(refund_fee) <= 0){
					alert('金额不能小于0');
					return false;
				}
				if(parseInt(total_fee) < parseInt(refund_fee)){
					alert('总金额不能小于退款金额');
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
				<li class="current">微信支付退款</li> 
            </ol>
        </div>
        <form action="testRefund" method="post">
            <div id="body" style="clear:left">
                <dl class="content">
					
                    <dt>商户原支付单号：</dt>
                    <dd>
                        <span class="null-star"></span>
                        <input name="out_trade_no" value="" maxlength="32" size="30"  placeholder="长度32"/>
                        <span>(长度32)</span>
                        <span></span>
                    </dd>
                   
                    <dt>商户退款单号：</dt>
                    <dd>
                        <span class="null-star"></span>
                        <input name="out_refund_no" value="" maxlength="32" size="30"  placeholder="长度32"/>
                        <span>(长度32)</span>
                        <span></span>
                    </dd>
                    <dt>总金额：</dt>
                    <dd>
                        <span class="null-star"></span>
                        <input name="total_fee" value="1" maxlength="32" size="30"  placeholder="单位：分"/>
                        <span class="null-star">*(单位：分)</span>
                        <span></span>
                    </dd>
                    <dt>退款金额：</dt>
                    <dd>
                        <span class="null-star"></span>
                        <input name="refund_fee" value="1" maxlength="32" size="30"  placeholder="单位：分"/>
                        <span class="null-star">*(单位：分)</span>
                        <span></span>
                    </dd>
                     <dt>收银员：</dt>
                    <dd>
                        <span class="null-star"></span>
                        <input name="cashierId" value="test" maxlength="32" size="30"  placeholder="长度32"/>
                        <span class="null-star">(长度32)*</span>
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
				<li><font class="note-help">商户订单号<!-- 、通道订单号和微信订单号至少填一个。 --> </font></li>
			</ul>
		</div>
	</div>
</body>
</html>