/**
 * 配置文件
 */
//mapp
vx.module('mapp.config', []).value('mapp.config', {});
vx.module('mapp.libraries', ['mapp.config']);
vx.module('mapp', ['mapp.libraries', 'ui']);
//ui
vx.module('ui.config', []).value('ui.config', {});
vx.module('ui.libraries', ['ui.config']);
vx.module('ui', ['ui.libraries']);

(function(window, vx, $) {
    'use strict';
    var mod = vx.module('mapp.config');

    //configLog
    configLog.$inject = ['$logProvider'];

    function configLog($logProvider) {
        $logProvider.setLevel('debug');
    }

    //Browser
    configBrowser.$inject = ['$browserProvider'];

    function configBrowser($browserProvider) {
    }

    //Targets
    configTargets.$inject = ['$targetsProvider'];

    function configTargets($targetsProvider) {
        $targetsProvider.useLocation(false);
    }

    //Compile
    configCompile.$inject = ['$compileProvider'];

    function configCompile($compileProvider) {
    }

    //RootScope
    configRootScope.$inject = ['$rootScopeProvider'];

    function configRootScope($rootScopeProvider) {
        $rootScopeProvider.traceDigest(false);
    }

    //Remote
    configRemote.$inject = ['$$remoteProvider'];

    function configRemote($$remoteProvider) {
        $$remoteProvider.setErrorCallback(function(data, status, headers, config) {
            //防重复提交，上传该随机串
            if (data.nonce_str != undefined) {
                window.NONCE_STR = data.nonce_str;
            }
            //当错误信息不为空
            if (data.ReturnCode != "000000") {
                alert(data.ReturnMsg);
            }
        });
    }

    //Http
    configHttp.$inject = ['$httpProvider'];

    function configHttp($httpProvider) {
        $httpProvider.setDefaultCallback(function(data, status, headers, config) {
            return false;
        });
    }

    //HttpBackend
    configHttpBackend.$inject = ['$httpBackendProvider'];

    function configHttpBackend($httpBackendProvider) {

        $httpBackendProvider.setClientMode(false);
        $httpBackendProvider.useAntiCache(0);
        $httpBackendProvider.useExternalAjax(false);

        $httpBackendProvider.config({
            ajaxTimeout: 30000,
            ajaxQueueSize: 5,
            ajaxAborted: false,
            beforeSend: function() {
                //$("#TheMask").show();
            },
            afterReceived: function() {
                //$("#TheMask").hide();
            }
        });
    }

    //Validation
    configValidation.$inject = ['$validationProvider'];

    function configValidation($validationProvider) {
    }

    mod.config(configLog);
    mod.config(configBrowser);
    mod.config(configTargets);
    mod.config(configCompile);
    mod.config(configRootScope);
    mod.config(configRemote);
    mod.config(configHttp);
    mod.config(configHttpBackend);
    mod.config(configValidation);

    //runTargets
    runTargets.$inject = ['$targets', '$rootScope', '$transitions'];

    function runTargets($targets, $rootScope, $transitions) {
        vx.forEach($transitions.types, function(value, key) {
            $targets.transition(value, (function() {
                //var type = $transitions.types[value];
                //默认专场方式
                var type = "default";
                $transitions.setTransitionTime(500);
                return function(oldEl, newEl, remove, back) {
                    $transitions.runTransition(type, oldEl, newEl, remove, back);
                };
            })());
        });
    }

    //runRootScope
    runRootScope.$inject = ["$rootScope", "$nativeCall", "$timeout", "$http", "$targets"];

    function runRootScope($rootScope, $nativeCall, $timeout, $http, $targets) {
        //常量定义
        //创建订单用随机串，防止重复提交
        $rootScope.RandomStr = '';
        //跨域访问地址

        //1.html跳到2.html
        $rootScope.loadPage = function(sourse, targets) {
            $nativeCall.pages.push(sourse);
            $nativeCall.history = [];
            $targets("content", targets);
        };

        //将post数据的字段名按ascii字典序排序(数组sort方法)
        //按照key1=value1&key2=value2…）拼接成字符串 string1
        //拼接上 key=Key( 商户支付密钥)得到 stringSignTemp
        //md5，并大写
        //最终数据json串
        $rootScope.getPostData = function(obj) {
            //json对象
            var PostData = obj;
            //key数组
            var _keySortArr = [];
            //keyvalue数组
            var _keyValueArr = [];
            //组装key数组
            for (var key in obj) {
                if (obj.hasOwnProperty(key)) {
                    _keySortArr.push(key);
                }
            }
            //对key数组排序
            _keySortArr.sort();
            //得到字符串string1
            for (var i = 0; i < _keySortArr.length; i++) {
                _keyValueArr[i] = _keySortArr[i] + "=" + obj[_keySortArr[i]] + '&';
            }

            _keyValueArr.push("key=" + $rootScope.MERCHANTKEY);
            
            //签名值
            PostData.sign = $.md5(_keyValueArr.join("")).toUpperCase();
            return JSON.stringify(PostData);
        };
    }

    runNativeCall.$inject = ['$nativeCall'];

    function runNativeCall($nativeCall) {
        window.NativeCall = $nativeCall;
    }

    runOS.$inject = ['$os'];

    function runOS($os) {
        window.OS = $os;
    }

    mod.run(runTargets);
    mod.run(runOS);
    mod.run(runRootScope);
    mod.run(runNativeCall);

})(window, window.vx, window.jQuery);


/**
 * 服务文件
 */

//$nativeCall
(function(window, vx) {
    'use strict';

    var service = {};
    service.$nativeCall = ["$os", '$rootScope', '$targets',
        function($os, $rootScope, $targets) {
            var tNative = {
                history: [],
                pages: [],
                viewPort: 'content',
                rootScope: $rootScope,
                timer: null
            };
            //设置标题
            tNative.setTitle = function(title) {
                $rootScope.$apply(function() {
                    $rootScope.titleName = title;
                });
            };
            //返回
            tNative.goBack = function() {
                if (tNative.history.length <= 1) {
                    if (tNative.pages.length > 0) {
                        var targets = tNative.pages.pop();
                        $targets("content", targets);
                    }
                } else if (typeof tNative.history[tNative.history.length - 1] !== 'function') {
                    var vLength = tNative.history.length;
                    var vLast = tNative.history[vLength - 1];
                    var vPenult = tNative.history[vLength - 2];
                    $targets(tNative.viewPort, "#" + (vPenult - vLast));
                } else if (typeof tNative.history[tNative.history.length - 1] === 'function') {
                    var callback = tNative.history.pop();
                    callback();
                }
            };
            //转场
            tNative.loadTrasfer = function(viewport, url) {
                $targets(viewport, url);
            };

            return tNative;
        }
    ];

    vx.module('mapp.libraries').service(service);

})(window, window.vx);

//$os
(function(window, vx, userAgent) {
    'use strict';

    var service = {};
    service.$os = [function() {
        var os = {
            webkit: userAgent.match(/WebKit\/([\d.]+)/) ? true : false,
            android: userAgent.match(/(Android)\s+([\d.]+)/) || userAgent.match(/Silk-Accelerated/) ? true : false,
            androidICS: this.android && userAgent.match(/(Android)\s4/) ? true : false,
            ipad: userAgent.match(/(iPad).*OS\s([\d_]+)/) ? true : false,
            iphone: !(userAgent.match(/(iPad).*OS\s([\d_]+)/) ? true : false) && userAgent.match(/(iPhone\sOS)\s([\d_]+)/) ? true : false,
            ios: (userAgent.match(/(iPad).*OS\s([\d_]+)/) ? true : false) || (!(userAgent.match(/(iPad).*OS\s([\d_]+)/) ? true : false) && userAgent.match(/(iPhone\sOS)\s([\d_]+)/) ? true : false),
            ios5: (userAgent.match(/(iPad).*OS\s([5_]+)/) ? true : false) || (!(userAgent.match(/(iPad).*OS\s([5_]+)/) ? true : false) && userAgent.match(/(iPhone\sOS)\s([5_]+)/) ? true : false),
            wphone: userAgent.match(/Windows Phone/i) ? true : false
        };
        return os;
    }];

    vx.module('mapp.libraries').service(service);

})(window, window.vx, navigator.userAgent);

//$transitions
(function(window, vx, userAgent) {
    'use strict';

    var service = {};
    service.$transitions = ["$nativeCall", "$os", "$rootScope",
        function($nativeCall, $os, $rootScope) {
            var transitionProvider = {
                types: {
                    'fadeIn': 'fadeIn',
                    'slide': 'slide',
                    'default': 'default',
                    'none': 'none'
                },
                runTransition: runTransition,
                availableTransitions: {}
            };
            transitionProvider.slideTransitionTime = 500;
            transitionProvider.setTransitionTime = function(time) {
                this.slideTransitionTime = time || 500;
            };

            function runTransition(transition, oldDiv, currWhat, remove, back) {
                if (!transitionProvider.availableTransitions[transition])
                    transition = 'default';
                transitionProvider.availableTransitions[transition].call(this, oldDiv, currWhat, remove, back);
            }

            //slide转场函数
            (function(transitionProvider) {
                function slideTransition(oldEl, newEl, remove, back) {
                    //记录历史部分
                    var controller = newEl.controller("vViewport");
                    var activeIndex = controller.$pages.activeIndex;
                    for (var ii in $nativeCall.history) {
                        if ($nativeCall.history[ii] === activeIndex) {
                            $nativeCall.history = $nativeCall.history.slice(0, ii);
                            break;
                        }
                    }
                    //转场部分
                    var wrapper, minH, oldToLeft, newToLeft, oldInitLeft, newInitLeft;
                    wrapper = $('.viewport-wrapper');
                    if (back) {
                        oldInitLeft = '0';
                        newInitLeft = '-100%';
                        oldToLeft = '100%';
                        newToLeft = '0';
                    } else {
                        oldInitLeft = '0';
                        newInitLeft = '100%';
                        oldToLeft = '-100%';
                        newToLeft = '0';
                    }
                    if (oldEl) {
                        $nativeCall.history.push(activeIndex);
                        oldEl.css({
                            "display": "block",
                            "position": "absolute",
                            "left": oldInitLeft,
                            "width": "100%",
                            "top": "0"
                        });
                    } else {
                        $nativeCall.history.push(activeIndex);
                        newEl.show();
                        window.scrollTo(0, 0);
                        return;
                    }
                    newEl.css({
                        "display": "block",
                        "position": "absolute",
                        "left": newInitLeft,
                        "width": "100%",
                        "top": "0"
                    });
                    oldEl.stop().animate({
                        "left": oldToLeft,
                        "top": '0'
                    }, transitionProvider.slideTransitionTime);

                    newEl.stop().animate({
                        "left": newToLeft,
                        "top": "0"
                    }, transitionProvider.slideTransitionTime, function() {
                        oldEl.css({
                            "display": "none",
                            "position": ""
                        });
                        newEl.css({
                            "top": "0",
                            "position": ""
                        });
                        wrapper.css({
                            "min-height": ""
                        });
                        window.scrollTo(0, 0);
                    });
                }

                transitionProvider.availableTransitions.slide = slideTransition;

            })(transitionProvider);

            //fadeIn转场
            (function(transitionProvider) {
                function fadeInTransition(oldEl, newEl, remove, back) {
                    //记录历史部分
                    var controller = newEl.controller("vViewport");
                    var activeIndex = controller.$pages.activeIndex;
                    for (var ii in $nativeCall.history) {
                        if ($nativeCall.history[ii] === activeIndex) {
                            $nativeCall.history = $nativeCall.history.slice(0, ii);
                            break;
                        }
                    }
                    //转场部分
                    $nativeCall.history.push(activeIndex);
                    if (oldEl) {
                        oldEl.hide();
                        newEl.fadeIn();
                    } else {
                        newEl.show();
                    }
                    window.scrollTo(0, 0);
                }

                transitionProvider.availableTransitions.fadeIn = transitionProvider.availableTransitions["default"] = transitionProvider.availableTransitions.none = fadeInTransition;

            })(transitionProvider);

            return transitionProvider;
        }
    ];

    vx.module('mapp.libraries').service(service);

})(window, window.vx);

/**
 * @author kongxiangxu
 */
(function(window, vx) {
    'use strict';

    var service = {};
    service.Util = ['$filter',
        function($filter) {
            return {
                /**
                 * description:还回时间格式类似为"2012-05-16"字符串, format指定字符串格式， 默认yyyy-MM-dd。
                 * example:getDate()还回当前时间的，getDate("3d")三天前时间，getDate("3w")三周前的，getDate("3m")三个月前的
                 * getDate("-3d")三天后时间，getDate("-3w")三周后的时间，getDate("-3m")三个月后的时间
                 */
                getDate: function(days, format) {
                    format = format || 'yyyy-MM-dd';
                    if (days) {
                        var group = days.match(/(\d+)([dDMmWw])/);
                        var value = group[1],
                            type = group[2].toUpperCase();
                        if (days.match(/^-/) != null) {
                            if (type === 'D')
                                return $filter('date')(new Date(new Date().getTime() + (value * 24 * 3600 * 1000)), format);
                            else if (type === 'W')
                                return $filter('date')(new Date(new Date().getTime() + (value * 7 * 24 * 3600 * 1000)), format);
                            else if (type === 'M') {
                                var date = new Date();
                                date.setMonth(date.getMonth() + parseInt(value));
                                return $filter('date')(date, format);
                            }
                        }
                        if (type === 'D')
                            return $filter('date')(new Date(new Date().getTime() - (value * 24 * 3600 * 1000)), format);
                        else if (type === 'W')
                            return $filter('date')(new Date(new Date().getTime() - (value * 7 * 24 * 3600 * 1000)), format);
                        else if (type === 'M') {
                            var date = new Date();
                            date.setMonth(date.getMonth() - value);
                            return $filter('date')(date, format);
                        }
                    } else
                        return $filter('date')(new Date(), format);
                },
                encryptAcNo: function(acno) {
                    var length = acno.length;
                    return acno.substring(0, 4) + "****" + acno.substring(length - 4, length);
                },
                daysBetween: function(beginDate, endDate) {
                    var OneMonth = beginDate.substring(5, beginDate.lastIndexOf('-'));
                    var OneDay = beginDate.substring(beginDate.length, beginDate.lastIndexOf('-') + 1);
                    var OneYear = beginDate.substring(0, beginDate.indexOf('-'));
                    var TwoMonth = endDate.substring(5, endDate.lastIndexOf('-'));
                    var TwoDay = endDate.substring(endDate.length, endDate.lastIndexOf('-') + 1);
                    var TwoYear = endDate.substring(0, endDate.indexOf('-'));
                    var difference = ((Date.parse(TwoMonth + '/' + TwoDay + '/' + TwoYear) - Date.parse(OneMonth + '/' + OneDay + '/' + OneYear)) / 86400000);
                    return difference;
                },
                //20.45*100
                floatNumber: function(arg1, arg2) {
                    var m = 0,
                        s1 = arg1.toString(),
                        s2 = arg2.toString();
                    try { m += s1.split(".")[1].length } catch (e) {}
                    try { m += s2.split(".")[1].length } catch (e) {}
                    return Number(s1.replace(".", "")) * Number(s2.replace(".", "")) / Math.pow(10, m);
                }
            };
        }
    ];

    vx.module('mapp.libraries').service(service);

})(window, window.vx);



/**
 * 指令文件
 */

/**
 * v-submit="ToConfirm()"
 */
(function(window, vx) {
    'use strict';
    var mod = vx.module("ui.libraries");
    mod.directive("*vSubmit", function() {
        return function(scope, element, attrs) {
            element.bind('submit', function(event) {
                var form = scope[attrs.name];
                var inputCtrls = attrs.$$element[0];
                for (var i = 0; i < inputCtrls.length; i++) {
                    var ctrl = inputCtrls[i];
                    if (['input', 'select'].indexOf(ctrl.tagName.toLowerCase()) !== -1) {
                        ctrl.blur();
                    }
                    var validateAttr = ctrl.getAttribute("validate") || true;
                    //默认原输入域的验证属性
                    if ((ctrl.nodeName === "BUTTON") || (validateAttr === 'false') || ctrl.type === 'checkbox') {
                        continue;
                    }
                    var message = {
                        required: ctrl.getAttribute("required-message") || "不能为空",
                        min: ctrl.getAttribute("min-message") || "最小值:" + ctrl.getAttribute("min"),
                        max: ctrl.getAttribute("max-message") || "最大值:" + ctrl.getAttribute("max"),
                        minlength: ctrl.getAttribute("minlength-message") || "最小长度:" + ctrl.getAttribute("v-minlength"),
                        maxlength: ctrl.getAttribute("maxlength-message") || "最大长度:" + ctrl.getAttribute("v-maxlength"),
                        pattern: ctrl.getAttribute("pattern-message") || "格式不正确"
                    };
                    var ctrlName = ctrl['name'] || ctrl['id'];
                    var ctrlComment = ctrl.parentNode.previousElementSibling ? ctrl.parentNode.previousElementSibling.innerText : ctrl.placeholder;
                    for (var key in form.$error) {
                        for (var j = 0; j < form.$error[key].length; j++) {
                            if (ctrlName == form.$error[key][j].$name) {
                                NativeCall.showLastError(ctrlComment + message[key]);
                                return;
                            }
                        }
                    }
                }
                scope.$apply(attrs.vSubmit);
                event.stopPropagation();
                event.preventDefault();
            });
        };
    });
})(window, window.vx);

/*
 * uiMask
 * ui-Mask='{"Fixed":2}'  设置小数点后尾数，默认为2位小数 ui-Mask
 */
(function(window, vx) {
    'use strict';
    var mod = vx.module("ui.libraries");
    mod.directive("uiMask", [
        function() {
            return {
                require: 'vModel',
                restrict: 'A',
                compile: function uiMaskCompilingFunction() {
                    var maskDefinitions = {};
                    var options = {
                        "Fixed": 2
                    };
                    return function uiMaskLinkingFunction(scope, iElement, iAttrs, controller) {
                        var eventsBound = false,
                            maskPatterns, value, valueMasked, isValid, oldValue, oldValueUnmasked, oldCaretPosition, oldSelectionLength;

                        maskDefinitions = vx.fromJson(iAttrs.uiMask || "{}");
                        maskDefinitions = vx.extend(options, maskDefinitions);

                        function initialize() {
                            initializeElement();
                            bindEventListeners();
                            return true;
                        };

                        function parser(fromViewValue) {
                            value = unmaskValue(fromViewValue || '');
                            //controller.$viewValue = value.length ? maskValue(value) : '';
                            return value;
                        };
                        controller.$parsers.push(parser);

                        function initializeElement() {
                            value = oldValueUnmasked = unmaskValue(controller.$modelValue || '');
                            valueMasked = oldValue = maskValue(value);
                            var viewValue = value.length ? valueMasked : '';
                            iElement.val(viewValue);
                            controller.$viewValue = viewValue;
                        };

                        function bindEventListeners() {
                            if (eventsBound) {
                                return;
                            }
                            iElement.bind('input click focus', eventHandler);
                            //                    iElement.bind('blur', evenouttHandler);
                            eventsBound = true;
                        };

                        function unmaskValue(value) {
                            return value;
                        };

                        function maskValue(unmaskedValue) {
                            var valueMasked = '';
                            vx.forEach(unmaskedValue.split(''), function(chr, i) {
                                if (i < 12) {
                                    if (chr.match(/[0-9]/)) {
                                        var position = valueMasked.indexOf('.');
                                        if (position < 0 || valueMasked.length - position < (maskDefinitions.Fixed + 1))
                                            valueMasked += chr;
                                    } else if (chr == '.' && maskDefinitions.Fixed > 0) {
                                        if (valueMasked.indexOf('.') < 0) {
                                            valueMasked += chr;
                                        }
                                    }
                                }
                            });
                            return valueMasked;
                        };

                        function eventHandler(e) {
                            e = e || {};
                            var eventWhich = e.which,
                                eventType = e.type;
                            var val = iElement.val(),
                                valOld = oldValue,
                                valMasked, valUnmasked = unmaskValue(val),
                                valUnmaskedOld = oldValueUnmasked;

                            var caretPos = getCaretPosition(this) || 0,
                                caretPosOld = oldCaretPosition || 0,
                                caretPosDelta = caretPos - caretPosOld,
                                selectionLenOld = oldSelectionLength || 0,
                                isSelected = getSelectionLength(this) > 0,
                                wasSelected = selectionLenOld > 0,
                                isAddition = (val.length > valOld.length) || (selectionLenOld && val.length > valOld.length - selectionLenOld),
                                isDeletion = (val.length < valOld.length) || (selectionLenOld && val.length === valOld.length - selectionLenOld),
                                isSelection = (eventWhich >= 37 && eventWhich <= 40) && e.shiftKey, // Arrow key codes

                                isKeyLeftArrow = eventWhich === 37,
                                // Necessary due to "input" event not providing a key code
                                isKeyBackspace = eventWhich === 8 || (eventType !== 'keyup' && isDeletion && (caretPosDelta === -1)),
                                isKeyDelete = eventWhich === 46 || (eventType !== 'keyup' && isDeletion && (caretPosDelta === 0) && !wasSelected),

                                // Handles cases where caret is moved and placed in front of invalid maskCaretMap position. Logic below
                                // ensures that, on click or leftward caret placement, caret is moved leftward until directly right of
                                // non-mask character. Also applied to click since users are (arguably) more likely to backspace
                                // a character when clicking within a filled input.
                                caretBumpBack = (isKeyLeftArrow || isKeyBackspace || eventType === 'click') && caretPos > 0;

                            oldSelectionLength = getSelectionLength(this);
                            //These events don`t require any action
                            if (isSelection || (isSelected && (eventType === 'click' || eventType === "keyup")))
                                return;

                            //Update values
                            valMasked = maskValue(valUnmasked);
                            oldValue = valMasked;
                            oldValueUnmasked = valUnmasked;
                            iElement.val(valMasked);
                            if (eventType === "input") {
                                scope.$apply(function() {
                                    controller.$setViewValue(valUnmasked);
                                });
                            }
                            // Caret Repositioning
                            if (isAddition && (caretPos <= 0)) {}
                            if (caretBumpBack) {
                                caretPos--;
                            }
                            if ((caretBumpBack && caretPos < valMasked.length)) {
                                caretPos++;
                            }
                            oldCaretPosition = caretPos;
                            setCaretPosition(this, caretPos);
                        };

                        function getCaretPosition(input) {
                            if (input.selectionStart !== undefined) {
                                return input.selectionStart;
                            } else if (document.selection) {
                                input.focus();
                                var selection = document.selection.createRange();
                                selection.moveStart('character', -input.value.length);
                                return selection.text.length;
                            }
                            return 0;
                        };

                        function setCaretPosition(input, pos) {
                            if (input.offsetWidth === 0 || input.offsetHeight === 0) {
                                return;
                            }
                            if (input.setSelectionRange) {
                                input.focus();
                                input.setSelectionRange(pos, pos);
                            } else if (input.createTextRange) {
                                //Curse you IE
                                var range = input.createTextRange();
                                range.collapse(true);
                                range.moveEnd('character', pos);
                                range.moveStart('character', pos);
                                range.select();
                            }
                        };

                        function getSelectionLength(input) {
                            if (input.selectionStart !== undefined) {
                                return input.selectionEnd - input.selectionStart;
                            }
                            if (document.selection) {
                                return document.selection.createRange().text.length;
                            }
                            return 0;
                        }

                        //init
                        initialize();
                    };
                }
            };
        }
    ]);
})(window, window.vx);

/**
 *  directive   key-allow
 *  value   symbol(default)  允许输入数字、字母、特殊字符
 *  value   number  允许输入数字
 *  value   word    允许输入数字、字母
 *  value   tel     允许输入数字、-
 *  value   amount  允许输入数字和小数点
 *  usage    key-allow   key-allow="number|word|symbol"
 */
(function(window, vx) {
    'use strict';
    var mod = vx.module("ui.libraries");
    mod.directive("keyAllow", function() {
        return {
            link: function(scope, element, attr, ctrl) {
                var keyAllow = attr.keyAllow || "symbol";
                element.bind("input", function(event) {
                    scope.$apply(function() {
                        var value = element.val();
                        if (keyAllow === "number") {
                            value = value.match(/^[0-9]*/);
                        } else if (keyAllow === "word") {
                            value = value.match(/^[0-9a-zA-Z]*/);
                        } else if (keyAllow === "symbol") {
                            value = value.match(/^[!-~]*/);
                        } else if (keyAllow === "tel") {
                            value = value.match(/^[0-9-]*/);
                        } else if (keyAllow === "amount") {
                            value = value.match(/^[0-9]*(\.?[0-9]{0,2})/);
                        }
                        element.val(value ? value[0] : null);
                    });
                });
            }
        };
    });
})(window, window.vx);

/**
 * 过滤文件
 */

/**
 * @author
 * filter template
 */
(function(window, vx) {
    'use strict';

    function accountNo() {
        return function(input) {
            if (input !== undefined)
                input = input.replace(/(.{4})/g, "$1 ");
            return input;
        };
    }

    vx.module('ui.libraries').filter('accountNo', accountNo);

})(window, window.vx);

/**
 * @author
 * filter template
 * 大写金额转换
 */
(function(window, vx) {
    'use strict';

    function amount() {
        return function(input) {
            if (input !== undefined) {
                var strOutput = "",
                    strUnit = '仟佰拾亿仟佰拾万仟佰拾元角分';
                input += "00";
                var intPos = input.indexOf('.');
                if (intPos >= 0) {
                    input = input.substring(0, intPos) + input.substr(intPos + 1, 2);
                }
                strUnit = strUnit.substr(strUnit.length - input.length);
                for (var i = 0; i < input.length; i++) {
                    strOutput += '零壹贰叁肆伍陆柒捌玖'.substr(input.substr(i, 1), 1) + strUnit.substr(i, 1);
                }
                return strOutput.replace(/^零角零分$/, '').replace(/零角零分$/, '整').replace(/^零元零角/, '').replace(/零[仟佰拾]/g, '零').replace(/零{2,}/g, '零').replace(/零([亿|万])/g, '$1').replace(/零+元/, '元').replace(/亿零{0,3}万/, '亿').replace(/^元/, "零元").replace(/零角/, '零').replace(/零元/, '').replace(/零分$/, "");
            }
            return input;
        };
    }

    vx.module('ui.libraries').filter('amount', amount);

})(window, window.vx);

/**
 * @author
 * filter 加密账号    1234****5678
 */
(function(window, vx) {
    'use strict';

    function encryptAcNo() {
        return function(input) {
            if (input !== undefined)
                return input.substring(0, 4) + "****" + input.substring(input.length - 4);
        };
    }

    vx.module('ui.libraries').filter('encryptAcNo', encryptAcNo);

})(window, window.vx);

/**
 * @author
 * filter 时间过滤    20161222192350==》2016-12-22 19:23:50
 */
(function(window, vx) {
    'use strict';

    function timeFormat() {
        return function(input) {
            if (input !== undefined)
                return input.substr(0,4)+"-"+input.substr(4,2)+"-"+input.substr(6,2)+" "+input.substr(8,2)+":"+input.substr(10,2)+":"+input.substr(12,2);
        };
    }

    vx.module('ui.libraries').filter('timeFormat', timeFormat);

})(window, window.vx);
/**
 * @author wbr
 * input只能输入数字
 * <input ui-number='{"intLength":25,"float":true,"dotLength":2,"addZero":false}'/>
 * @param intLength:整数位长度 默认25
 * 		  float:是否可以是小数 默认true
 * 		  dotLength:小数位长度 默认2
 * 		  addZero:是否自动补零 默认false
 */
(function(window, vx, undefined) {
    'use strict';

    function checkKey(code, dot) {
        var aaa = {
            "isAllow": false,
            "checkSpe": checkSpe
        };

        function checkSpe(code, dot) {
            //				    回车 tab 退格 左  右
            var specialAllowKey = [13, 9, 8, 37, 39];
            if (dot) {
                //			  小键盘小数点  大键盘小数点
                specialAllowKey.push(110, 190);
            }
            var allow = false;
            for (var i = 0; i < specialAllowKey.length; i++) {
                if (code == specialAllowKey[i]) {
                    allow = true;
                    break;
                }
            }
            if (allow) {
                return true;
            } else {
                return false;
            }
        };
        if (code) {
            //			大键盘数字							小键盘数字
            if ((code >= 48 && code <= 57) || (code >= 96 && code <= 105) || checkSpe(code, dot)) {
                aaa.isAllow = true;
            } else {
                aaa.isAllow = false;
            }
        }
        return aaa;
    }

    var defaults = {
        "intLength": 25,
        "float": true,
        "dotLength": 2,
        "addZero": false
    };
    var directive = {};
    directive.uiNumber = [
        function() {
            return {
                restrict: 'A',
                link: function(scope, element, attrs) {
                    var params = $.extend({}, defaults, vx.fromJson(attrs.uiNumber || {}));
                    params.intLength = parseInt(params.intLength, 10);
                    params.dotLength = parseInt(params.dotLength, 10);
                    element.bind({
                        'keydown': function(e) {
                            var theEvent = window.event || e,
                                code = theEvent.keyCode || theEvent.which,
                                len = params.intLength;
                            var valueStr = $(this).val().toString();
                            var dotIndex = valueStr.indexOf('.');
                            if (params.float) {
                                len = params.intLength + params.dotLength + 1;
                            }
                            if (valueStr.length >= len) {
                                if (!checkKey().checkSpe(code, false)) {
                                    // theEvent.keyCode = 0;
                                    if (theEvent.preventDefault) {
                                        theEvent.preventDefault();
                                    } else { // ie
                                        theEvent.returnValue = false;
                                    }
                                }
                            } else {
                                //可以是小数
                                if (params.float) {
                                    //已经输入过小数点
                                    if (dotIndex > 0) {
                                        if (!checkKey(code, false).isAllow) {
                                            // theEvent.keyCode = 0;
                                            if (theEvent.preventDefault) {
                                                theEvent.preventDefault();
                                            } else { // ie
                                                theEvent.returnValue = false;
                                            }
                                        }
                                    }
                                    //未输入过小数点
                                    else {
                                        if (valueStr.length >= params.intLength) {
                                            if (!checkKey().checkSpe(code, true)) {
                                                // theEvent.keyCode = 0;
                                                if (theEvent.preventDefault) {
                                                    theEvent.preventDefault();
                                                } else { // ie
                                                    theEvent.returnValue = false;
                                                }
                                            }
                                        } else {
                                            if (!checkKey(code, true).isAllow) {
                                                // theEvent.keyCode = 0;
                                                if (theEvent.preventDefault) {
                                                    theEvent.preventDefault();
                                                } else { // ie
                                                    theEvent.returnValue = false;
                                                }
                                            }
                                        }
                                    }
                                }
                                //只能是整数
                                else {
                                    if (!checkKey(code, false).isAllow) {
                                        // theEvent.keyCode = 0;
                                        if (theEvent.preventDefault) {
                                            theEvent.preventDefault();
                                        } else { // ie
                                            theEvent.returnValue = false;
                                        }
                                    }
                                }
                            }
                        },
                        'keyup': function(e) {
                            var len = params.intLength;
                            var valueStr = $(this).val().toString();
                            var dotIndex = valueStr.indexOf('.');
                            if (params.float) {
                                len = params.intLength + params.dotLength + 1;
                            }
                            if (dotIndex > 0) {
                                element.attr("maxlength", dotIndex + params.dotLength + 1);
                                var dotStr = valueStr.substr(dotIndex + 1);
                                if (dotStr.length > params.dotLength) {
                                    dotStr = dotStr.substr(0, params.dotLength);
                                    valueStr = valueStr.substring(0, dotIndex + 1) + dotStr;
                                    $(this).val(valueStr);
                                }
                            } else {
                                element.attr("maxlength", '');
                            }
                            if (valueStr.length > len) {
                                $(this).val(valueStr.substr(0, len));
                            }
                        },
                        'blur': function(e) {
                            if (params.float && params.addZero) {
                                var len = params.intLength,
                                    zeroLen = params.dotLength;
                                var valueStr = $(this).val().toString();
                                var dotIndex = valueStr.indexOf('.');
                                var zeroStr = function(l) {
                                    var zs = "";
                                    for (var i = 0; i < l; i++) {
                                        zs += "0";
                                    }
                                    return zs;
                                };
                                if (dotIndex < 0) {
                                    if (vx.isEmpty(valueStr)) {
                                        $(this).val("0." + zeroStr(zeroLen));
                                    } else {
                                        $(this).val(valueStr + "." + zeroStr(zeroLen));
                                    }
                                } else if (dotIndex === 0) {
                                    $(this).val("0" + valueStr + zeroStr(zeroLen));
                                } else {
                                    var dotStrLen = valueStr.substr(dotIndex + 1).length;
                                    zeroLen = params.dotLength - dotStrLen;
                                    $(this).val(valueStr + zeroStr(zeroLen));
                                }
                            }
                        }
                    });
                }
            };
        }
    ];

    vx.module('ui.libraries').directive(directive);

})(window, window.vx);

(function(window, vx, undefined) {'use strict';
/**
 * 用户输入的限制
 * @author soda-lu
 * ui-num
 *
 */
vx.module('ui.libraries').directive('uiNum', ["$timeout", "$compile",
function($timeout, $compile) {
	return {
		restrict : 'CA',
		link : function(scope, element, attrs) {
			var defaults = {
				"maxlength" : "12",
				"hasDot" : true
			};
			var params = $.extend({}, defaults, vx.fromJson(attrs.uiNum || {}));
			element.bind({
				'blur' : function(e) {
					if ($(this).val() >= 0 && $(this).val() !== '') {
					} else {
						//输入非数字不清空
						scope[attrs.vModel] = undefined;
						$(this).val("");
						scope.$apply(scope);
					}
				},
				'paste' : function(e) {
					return false;
				},
				'keydown' : function(e, value) {
					// keydown start
					var theEvent = window.event || e;
					// 当整数位数达到maxlength-3时判断下一位是否为.,如果不是“.”则禁止输入
					if ((theEvent.ctrlKey || theEvent.shiftKey || $(this).val().toString().length == (Math.abs(params.maxlength) - 3))) {
						if (theEvent.keyCode != 13 && theEvent.keyCode != 9 && theEvent.keyCode != 8 && theEvent.keyCode !== 190) {
							//element.attr("maxlength",Math.abs(params.maxlength) - 3);
							if (window.event) {
								code = 0;
								theEvent.returnValue = false;
							} else {
								theEvent.preventDefault();
							}
						}
					}
					//释放tab back enter键 与"."
					if ((theEvent.ctrlKey || theEvent.shiftKey || $(this).val().toString().length == Math.abs(params.maxlength))) {
						if (theEvent.keyCode != 13 && theEvent.keyCode != 9 && theEvent.keyCode != 8) {
							//element.attr("maxlength",Math.abs(params.maxlength));
							// 没有小数位达到限制位数
							if (window.event) {
								code = 0;
								theEvent.returnValue = false;
							} else {
								theEvent.preventDefault();
							}
						}
					}
					// hasDot start
					var code = theEvent.keyCode || theEvent.which;
					// 不能输入汉字
					/*if(code == 229){
						if (window.event) {
							code = 0;
							theEvent.returnValue = false;
						} else {
							theEvent.preventDefault();
						}
					}*/
					if (params.hasDot) {
						//如首位是0,其后必跟"."
						if ($(this).val() == '0' && (code != 190 && code !== 13 && code != 9 && code != 8)) {
							if (window.event) {
								code = 0;
								theEvent.returnValue = false;
							} else {
								theEvent.preventDefault();
							}
						}
						//禁止"."在首位
						if ($(this).val() === '' && code === 190) {
							if (window.event) {
								code = 0;
								theEvent.returnValue = false;
							} else {
								theEvent.preventDefault();
							}
						}
						//"."后仅保留两位有效数字
						if ($(this).val()!=="" && $(this).val()!==undefined && $(this).val()!==null) {
							var str = $(this).val() + "";
							var xiaoshudian = str.indexOf(".");
							var valueLength = str.length;
							if (valueLength >= 4 && (xiaoshudian == (valueLength - 3)) && (code != 190 && code !== 13 && code != 9 && code != 8)) {
								if (window.event) {
									code = 0;
									theEvent.returnValue = false;
								} else {
									theEvent.preventDefault();
								}
							}
							//禁止数字外的其他键
							if (code < 48 || (code > 57 && code < 96) || code > 105) {
								if (code == 229 || code == 110 || code == 37 || code == 39 || code == 46 || code == 8 || code == 180 || code == 190 || code == 9) {
									if ((code == 110 || code == 190) && $(this).val().toString().indexOf('.') > 0) {
										if (window.event) {
											code = 0;
											theEvent.returnValue = false;
										} else {
											theEvent.preventDefault();
										}
									}
								} else {
									if (window.event) {
										code = 0;
										theEvent.returnValue = false;
									} else {
										theEvent.preventDefault();
									}
								}
							}
							//END 禁止数字外的其他键
						}
					} else {
						//禁止数字外的其他键 299汉字
						if (code < 48 || (code > 57 && code < 96) || code > 105) {
							if (code == 229 || code == 110 || code == 37 || code == 39 || code == 46 || code == 8 || code == 180 || code == 190 || code == 9) {
								if ((code == 110 || code == 190)) {
									if (window.event) {
										code = 0;
										theEvent.returnValue = false;
									} else {
										theEvent.preventDefault();
									}
								}
							} else {
								if (window.event) {
									code = 0;
									theEvent.returnValue = false;
								} else {
									theEvent.preventDefault();
								}
							}
						}
						//END 禁止数字外的其他键
					}
					// hasDot end
					// keydown end
				}
			});
		}
	};
}]);

})(window, window.vx, window.$);
