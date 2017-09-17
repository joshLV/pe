KeyPosCtrl.$inject = ["$scope", "$targets", "$remote", "$rootScope"];

function KeyPosCtrl($scope, $targets, $remote, $rootScope) {

    //初始化方法
    $scope.init = function() {};

    //关闭窗口
    $scope.Close = function() {
        if ($rootScope.BrowserType == "tenpay") {
            WeixinJSBridge.call('closeWindow');
        } else if ($rootScope.BrowserType == "alipay") {
            AlipayJSBridge.call('closeWebview');
        }
    };

    //创建订单
    $scope.ToResult = function() {
    	 var recommendationList = 
             [{'img':'img/goldenCard.jpeg', 'url':'https://mbank.bankofshanghai.com/pweixin/static/index.html?_TransactionId=CreditCardApply&_CardType=0300001560&YLLink=430056'},
             {'img':'img/taobaoCard.jpeg', 'url':'https://mbank.bankofshanghai.com/pweixin/static/index.html?_TransactionId=CreditCardApply&_CardType=0300001616&YLLink=430056'}]
    	 $scope.randomObj = recommendationList[Math.round(Math.random())];
    	if($rootScope.flag==1){
    		if(typeof($scope.MemId)=="undefined"){ 
    			alert("请输入创富港会员号");
    			return;
    		}
    		if(!$scope.MemId){
    			alert("请输入创富港会员号");
    			return;
    		}
    	}else{
    		$scope.MemId="00000000";
//    		alert("MemId=="+$scope.MemId);
    	}
    	if(typeof($scope.Amount)=="undefined"){
    		alert("请输入金额");
    		return;
    	}
    	if(!$scope.Amount){
			alert("请输入金额");
			return;
		}
        //2秒之后才可操作
        $("#SUBMITBTN").attr("disabled",'true');
        setTimeout(function(){
            $("#SUBMITBTN").removeAttr("disabled");
        },2000);

        if ($rootScope.BrowserType == "tenpay") {
            $scope.wechatPay();
        } else if ($rootScope.BrowserType == "alipay") {
           
            if($rootScope.departmentId == "mybank"){
            	$scope.mybank();
            }else if($rootScope.departmentId == "alipay"){
            	 $scope.aliPay();
            }
        }
    };
    
    function accMul(arg1,arg2)
    {
        var m=0,s1=arg1.toString(),s2=arg2.toString();
        try{m+=s1.split(".")[1].length}catch(e){}
        try{m+=s2.split(".")[1].length}catch(e){}
        return Number(s1.replace(".",""))*Number(s2.replace(".",""))/Math.pow(10,m);
    }

    //微信支付
    $scope.wechatPay = function() {

        var params = {
            "trans_id": "TPCP",
            "mch_token": $rootScope.MerchantToken,
            "sub_mch_id":$rootScope.merchantId,
            "openid": $rootScope.UserId,
            "trade_type":"JSAPI",
            "total_fee": "" + accMul($scope.Amount,100),
            "nonce_str": window.NONCE_STR,
            "MemId":$scope.MemId
        };
        

        $remote.post("/paygate/weixin", $rootScope.getPostData(params),
            function(data) {
        	
                if (data.ReturnCode == "000000") {
                    //订单号
                    $scope.TRADE_NO = data.out_trade_no;
                    
                    //发起微信支付
                    $scope.GoTenPay(data);
                }
            });
    };
    
    //网商支付
    $scope.mybank = function() {

        var params = {
        		"trans_id": "ALZS",
                "mch_token": $rootScope.MerchantToken,
                "sub_mch_id":$rootScope.merchantId,
                "openid": $rootScope.UserId,
                "trade_type":"JSAPI",
                "total_fee": "" + accMul($scope.Amount,100),
                "nonce_str": window.NONCE_STR,
                "ChannelType": "ALI"
        };

        $remote.post("/paygate/weixin", $rootScope.getPostData(params),
            function(data) {
        	
                if (data.ReturnCode == "000000") {

                    //订单号
                    $scope.TRADE_NO = data.TRADE_NO;
                    
                    //订单创建成功，发起支付
                    AlipayJSBridge.call("tradePay", {
                        tradeNO: $scope.TRADE_NO
                    }, function(result) {
                    	 //订单号
                        $scope.TRADE_NO = data.out_trade_no;
                    	$scope.payDepartment = "2";
                        $scope.QryPayStatus();
                    });
                }
            });
    };

    //支付宝支付
    $scope.aliPay = function() {

        var params = {
        	"trans_id": "AGZH",
            "sub_mch_id":$rootScope.merchantId,
            "trade_type":"JSAPI",
            "mch_token": $rootScope.MerchantToken,
            "openid": $rootScope.UserId,
            "total_fee": "" + accMul($scope.Amount,100),
            "nonce_str": window.NONCE_STR,
        };

        $remote.post("/paygate/weixin", $rootScope.getPostData(params),
            function(data) {
        	
                if (data.ReturnCode == "000000") {

                    //订单号
                    $scope.TRADE_NO = data.prepay_id;
                    
                    //订单创建成功，发起支付
                    AlipayJSBridge.call("tradePay", {
                        tradeNO: $scope.TRADE_NO
                    }, function(result) {
                    	$scope.TRADE_NO = data.out_trade_no;
                    	$scope.payDepartment = "2";
                        $scope.QryPayStatus();
                    });
                }
            });
    };

    //支付状态查询方法
    $scope.QryPayStatus = function() {
        var params = {
            "trans_id": "TPCX",
            "auth_channel": $rootScope.BrowserType,
            "mch_token": $rootScope.MerchantToken,
            "sub_mch_id":$rootScope.merchantId,
            "out_trade_no": $scope.TRADE_NO,
            "nonce_str": window.NONCE_STR
        };

        //查询支付结果
        $remote.post("/paygate/weixin", $rootScope.getPostData(params),
            function(data) {
                if (data.ReturnCode == "000000") {
                    //交易结果
                    $scope.TradeResult = data;
                    switch (data.trade_state) {
                        case "SUCCESS":
                            //支付成功
                            $targets("content", "#2");
                            break;
                        case "PAYERROR":
                            //支付失败
                            $targets("content", "#4");
                            break;
                        default:
                            //处理中
                            $targets("content", "#3");
                            break;
                    }
                }
            });
    };
    //调起微信支付控件
    function onBridgeReady(data) {
    	
        WeixinJSBridge.invoke( 
            'getBrandWCPayRequest', {
                "appId": data.appId,
                "timeStamp": data.timeStamp,
                "nonceStr": data.nonce_str,
                "package": data.package,
                "signType": data.signType,
                "paySign": data.paySign
            },
            function(res) {
                if (res.err_msg == "get_brand_wcpay_request:ok") {
                	$scope.payDepartment = "1";
                    $scope.QryPayStatus();
                }
            }
        );
    };

    //发起微信支付
    $scope.GoTenPay = function(data) {
    	
        if (typeof WeixinJSBridge == "undefined") {
            if (document.addEventListener) {
                document.addEventListener('WeixinJSBridgeReady', onBridgeReady, false);
            } else if (document.attachEvent) {
                document.attachEvent('WeixinJSBridgeReady', onBridgeReady);
                document.attachEvent('onWeixinJSBridgeReady', onBridgeReady);
            }
        } else {
            onBridgeReady(data);
        }
    };
    //广告推荐
    $scope.recommend = function(url) {
        window.open(url);
    };
}
