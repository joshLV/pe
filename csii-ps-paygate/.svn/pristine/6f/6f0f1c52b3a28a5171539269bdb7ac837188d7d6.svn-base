require.config({
    //基础路径
    baseUrl: "js/",
    //requirejs配置路径（paths）
    paths: {
        "jquery": "min/jquery.min",
        "jmd5": "min/jquery.md5.min",
        "vx": "min/vx.min",
        "vxlocale": "min/vx-locale_zh-cn.min",
        "main": "main"
    },
    //requirejs针对不符合AMD规范的情况进行配置（shim）
    shim: {
        "jquery": {
            deps: [],
            exports: "jquery"
        },
        "jmd5": {
            deps: ["jquery"],
            exports: "jmd5"
        },
        "vx": {
            deps: ["jquery"],
            exports: "vx"
        },
        "vxlocale": {
            deps: ["vx"],
            exports: "vxlocale"
        },
        "main": {
            deps: ["vx"],
            exports: "main"
        }
    }
});

require(["jquery", "jmd5", "vx", "vxlocale", "main"],
    function(jquery, jmd5, vx, vxlocale, main) {

        //初始化方法
        var init = function() {
        	
        	
            //启动vx并获取要使用的service
            vx.bootstrap(document.body, ["mapp"]);
            var $http = vx.element("body").injector().get("$http");
            var $rootScope = vx.element("body").injector().get("$rootScope");
            $rootScope.flag=2;	
            //获取url参数的value值
            var getQueryString = function(name) {
                var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i");
                var r = window.location.search.substr(1).match(reg);
                if (r != null)
                    return unescape(r[2]);
                return "";
            };
            
            if (window.NativeCall) {
                var url = "html/KeyPos/KeyPos.html";
                window.NativeCall.loadTrasfer('content', url);
            }

            //浏览器类型
            $rootScope.BrowserType = getQueryString("browsertype");
            //商户token
            $rootScope.MerchantToken = getQueryString("merchanttoken");

            
            //微信浏览器
            if ($rootScope.BrowserType == "tenpay") {
                $rootScope.CODEVALUE = getQueryString("code");
//                $rootScope.AppIdTenpay = "123";
                $("head").append('<link rel="stylesheet" type="text/css" href="css/wechatpay.css">');

                //查看微信版本号，微信5.0以上才有支付模块
                var HAVEPAYAPI = navigator.userAgent.match(/MicroMessenger\/([\d.]+)/);

                //android和ios可以查到微信版本号
                HAVEPAYAPI = HAVEPAYAPI[1].split(".")[0] >= 5 ? true : false;

                //如果微信版本号小于5.0关闭窗口
                if (!HAVEPAYAPI) {
                    WeixinJSBridge.call('closeWindow');
                    return;
                }
                
            }

            //支付宝浏览器
            if ($rootScope.BrowserType == "alipay") {
                $rootScope.CODEVALUE = getQueryString("auth_code");
//                $rootScope.AppIdAlipay = "2016062001534900";
                $("head").append('<link rel="stylesheet" type="text/css" href="css/alipay.css">');
            }
            var params = {
            		"merToken": $rootScope.MerchantToken,
                    "trans_id": "QAPPID",
                };
                $http.post('/paygate/weixin', $rootScope.getPostData(params)).success(function(data, status, header, config) {
                	 $rootScope.APPID = data.APPID;
                	 $rootScope.ALIPAYAPPID = data.ALIPAYAPPID;
                	 $rootScope.departmentId = data.departmentId;
                	//默认商户支付密钥（签名用）
                     $rootScope.MERCHANTKEY = "1234567890abcdef";

                     
                     //获取商户信息+用户id+商户支付密钥
                     var params = {
                         "mch_token": $rootScope.MerchantToken,
                         "appid": $rootScope.APPID,
                         "alipay_appid": $rootScope.ALIPAYAPPID,
                         "departmentId": $rootScope.departmentId,
                         "trans_id": "PTQM",
                         "auth_channel": $rootScope.BrowserType,
                         "auth_code": $rootScope.CODEVALUE
                     };
                     
                     
                     $http.post('/paygate/weixin', $rootScope.getPostData(params)).success(function(data, status, header, config) {
                     	
                         window.NONCE_STR = data.nonce_str;

                         //成功情况
                         if (data.ReturnCode == "000000") {
                         	
                             //商户基本信息
                             $rootScope.merchantLogo = "img/icon-logo.png";
                             $rootScope.merchantId = data.mch_id;
                             $rootScope.merchantName = data.mch_name;
                             $rootScope.merchantAdd = data.mch_add;
                             $rootScope.merchantPhone = data.mch_phone;
                             $rootScope.agentId = data.mch_agentId;
                             if($rootScope.agentId=="0100000010"){
                            	 $rootScope.flag=1;
                             }else{
                            	 $rootScope.flag=2;
                             }

                             //用户id
                             $rootScope.UserId = data.openid;
                             $rootScope.AccessToken = data.accessToken;

                             //设置title名称为商家名称
                             var titleContent = $rootScope.merchantName;
                             $("title").text(titleContent);
                             document.getElementsByTagName("title")[0].innerText = titleContent;
                             document.title = titleContent;

                             //页面跳转至下单页
                             if (window.NativeCall) {
                                 var url = "html/KeyPos/KeyPos.html";
                                 window.NativeCall.loadTrasfer('content', url);
                             }
                         }
                     });
                });
            //最终的appid
//            $rootScope.APPID = $rootScope.AppIdTenpay != undefined ? $rootScope.AppIdTenpay : $rootScope.AppIdAlipay;
            
        };

        //调用init方法
        init();
    });
