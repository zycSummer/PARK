/*
 * @Author: jpp
 * @Date: 2019-10-22 17:07:34
 * @Last Modified by: jpp
 * @Last Modified time: 2019-11-22 13:37:35
 */

layui.use(['request','form', 'element', 'layer', 'jquery','laydate'], function () {
    var form = layui.form,
        layer = layui.layer,
        laydate = layui.laydate,
        $ = layui.jquery,
        element = layui.element;
    layer = parent.layer === undefined ? layui.layer : top.layer;

    var siteObj = JSON.parse(sessionStorage.getItem('parkId'));
    var htData = {};
    var paraDataList = [],dataModel;//综合展示主图paras
    var timeIntervalArr = [];
    var rankingInterval,loadInterval,dayRankingInterval;
    var task;
    var opnameDia,interval = 5,chart1,xData=[],seriesData=[];//弹框
    var fullScreenIndex;

    funcsAuthority();

    //全屏
    $(".fullScreen").on('click',function(){
        var ww = $(parent.window).width()+'px',hh = $(parent.window).height()+'px';
        layer.open({
            type: 2
            ,title: false
            ,closeBtn: false
            ,area: [ww,hh]
            ,id: 'LAY_layuipro' //设定一个id，防止重复弹出
            ,content: window.location.href
            ,success: function(layero, index){
                fullScreenIndex = index;
                layero.find('iframe').contents().find(".tabBreacrumb").hide();
                layero.find('iframe').contents().find(".fullScreen").hide();
            }
        });
    });
    //退出全屏
    $(parent.window.document).keyup(function(event){
        var theEvent = event || window.event;// 兼容FF和IE和Opera
        var code = theEvent.keyCode || theEvent.which || theEvent.charCode;
        if(code == 27){ // 按 Esc
            var parentHT = parent.window.document.getElementById("chart2");
            if($(parentHT)) $(parentHT).remove();
        }
    });
    //退出全屏
    $(document).keyup(function(event){
        var theEvent = event || window.event;// 兼容FF和IE和Opera
        var code = theEvent.keyCode || theEvent.which || theEvent.charCode;
        if(code == 27){ // 按 Esc
            layer.closeAll('iframe');
        }
    });

    //点击 时间间隔
    $(document).on("click",".inter",function(){
        $(this).siblings().removeClass('layui-this');
        $(this).addClass('layui-this');

        interval = $(this).attr('data-id');
        return false;
    });

    //查询
    $(document).on("click",".query",function(){
        getHisData();
    });

    //历史数据（折线图形式）的功能
    window.openHistory = function(event,data,view){
        var dialog = new ht.widget.Dialog();//模态对话框插件
        opnameDia = data.a('query')?data.a('query'):data.a('opname');
        if(!opnameDia){
            layer.msg('没有配测点！');
            return false;
        }
        dialog.setConfig({
            title: '历史数据',
            titleBackground: '#f8f8f8',//指定标题栏的背景色
            titleColor: '#000',//指定标题栏文字的颜色
            titleIcon: 'node_image',//标题栏图标
            titleAlign: 'left',//标题文本对齐方式，可选值为left/right/center
            content: document.getElementById("historyChart"),//指定对话框的内容，可以是html文本，或DOM对象，或HT组件对象（自动调用其invalidate）
            // content: $("#historyChart").html(),//指定对话框的内容，可以是html文本，或DOM对象，或HT组件对象（自动调用其invalidate）
            contentPadding: 10,//对话框内容区域的内边距
            width: 800,
            height: 400,
            borderWidth:1,//对话框的边框宽度
            draggable: true,//指定对话框是否可拖拽调整位置，可选值为true/false
            // dragModel: 'inside',指定对话框拖拽模式，inside 表示对话框内容包含在父亲容器中，none 或 null 表示随意拖放
            minDragSize: 50,//指定对话框在超出父亲容器的最小可拖动区域大小，默认为 20
            closable: true,//可选值为true/false，表示是否显示关闭按钮
            maximizable: true,//可选值为true/false，表示对话框是否可被最大化
            resizeMode: "wh",//鼠标移动到对话框右下角可改变对话框的大小，此参数控制resize模式：w表示只调整宽度，h表示只调整高度，wh表示宽高都可调整，none表示不可调整宽高
            // position: {x:0,y:0},//指定对话框的的位置，默认为字符串center
            buttons: [{//指定对话框按钮组内容
                label: "Close",
                action: function(button, e) {
                    dialog.hide();
                    // dialog.validate();
                }
            }],
            buttonsAlign: "center",//指定对话框按钮的对齐方式，可选值为left/right/center
            action: function(item, e) {//全局回调函数，当点击按钮时，单个按钮的回调函数执行完成后再执行此全局回调函数，参数为函数，格式为：function(button, event) { }，button为点击的按钮，event为原生的事件对象(一般为MouseEvent)
                // console.log(item, e);
            }
        });
        dialog.show();

        var time1 = dialog.getView().querySelector("#time1").value;
        var time2 = dialog.getView().querySelector("#time2").value;

        getHisData(time1,time2);

        //增加事件监听器，参数为监听函数，派发的事件种类可以查看简单示例中的Demo
        dialog.addEventListener(function(e) {
            if(e.kind == 'hide'){
                $("#dialogCon").html(dialog.getView().querySelector("#historyChart"));
            }
            if(e.kind == 'maximize' || e.kind == 'restore' || e.kind == 'betweenResize'){
                initHis();
            }
            // console.log(e);
        });
    };

    window.openDetail = function(event,data,view){
        var dialog = new ht.widget.Dialog();//模态对话框插件
        var id = data.a('htImgId');
        if(!id){
            layer.msg('没有配置组态画面');
            return false;
        }
        if(siteObj){
            load();
            request.service({
                method: 'post',
                url: '/comprehensiveShow/queryAllSiteImgByHtImgId',
                data: {
                    'objType': siteObj.type,
                    'objId': siteObj.id,
                    'htImgId': id
                }
            }).then(function (res) {
                disLoad();
                if(res.one){
                    var dataModel2 = new ht.DataModel();//数据模型类，作为data数据的容器和管理者
                    var gv2 = new ht.graph.GraphView(dataModel2);//GraphView:2D图形组件

                    window.addEventListener('resize', function(e){
                        gv2.fitContent(true, 0);//缩放平移整个拓扑以展示所有的图元
                    });

                    var json = res.one.cfgPic;
                    var json2 = ht.Default.parse(json);//Default:js对象，包含HT系统模型的参数信息和工具函数
                    dataModel2.deserialize(json2);//反序列化
                    gv2.fitContent(true, 0);
                    setTimeout(function() {
                        gv2.fitContent(true, 0);
                    }, 1000);
                    dataModel2.enableAnimation();

                    var dlt = 5, max = 100000000;
                    task = {
                        interval: 50,
                        action: function(data){
                            if (data instanceof ht.Shape) {
                                if (!data.s('shape.dash')) return;
                                if( data['_tag'] == 'fixed') return;

                                var k = data['_tag'] == '1' ? -1 : 1;
                                var offset = data.s('shape.dash.offset') || 0;
                                data.s('shape.dash.offset', (offset + k * dlt) % max);
                                return;
                            }
                        }
                    };
                    dataModel2.removeScheduleTask(task);
                    dataModel2.addScheduleTask(task);

                    var animationObj = dataModel2.toDatas(function(data) {
                        return data.a('animation'); //如果不放在setTimeout里，无法获取图纸上未修改过自定义属性值的图标属性
                    });
                    if (animationObj.length > 0) {
                        animationObj.each(function(data) {
                            var animation = data.a('animation');
                            if( data['_tag'] == 'fixed') return;
                            animation(data);
                        });
                    }

                    var stretch = dataModel2.toDatas(function(data){ return data.a('stretch');});
                    if (stretch.length > 0) {
                        stretch.each(function(data3) {
                            data3.setAnimation({
                                stretch1: {
                                    property: "stretch",//动画要改变的图元的属性名
                                    accessType:'attr',
                                    from: 'fill',
                                    to: 'uniform',
                                    frames:2,//动画帧数
                                    // duration:50000,//持续时间
                                    interval: 100,
                                    easing:'Elastic.easeOut',//动画方式
                                    onUpdate: function(value){//回调函数，动画的每一帧都会回调此函数
                                        this.a("stretch", 'uniform');
                                    },
                                    next: "stretch2"
                                },
                                stretch2: {
                                    property: "stretch",
                                    accessType:'attr',
                                    from: 'uniform',
                                    to: 'centerUniform',
                                    frames:2,//动画帧数
                                    // duration:50000,//持续时间
                                    interval: 100,
                                    easing:'Quart.easeInOut',//动画方式
                                    onUpdate: function(value){
                                        this.a("stretch", 'centerUniform');
                                    },
                                    next: "stretch3"
                                },
                                stretch3: {
                                    property: "stretch",
                                    accessType:'attr',
                                    from: 'centerUniform',
                                    to: 'fill',
                                    frames:2,//动画帧数
                                    // duration:50000,//持续时间
                                    interval: 100,
                                    easing:'Quart.easeInOut',//动画方式
                                    onUpdate: function(value){
                                        this.a("stretch", 'fill');
                                    },
                                    next: "stretch1"
                                },
                                expandHeight: {
                                    property: "height",
                                    accessType:'attr',
                                    from: 90,
                                    to: 100,
                                    frames:2,//动画帧数
                                    // duration:50000,//持续时间
                                    interval: 100,
                                    next: "collapseHeight"
                                },
                                collapseHeight: {
                                    property: "height",
                                    accessType:'attr',
                                    from: 100,
                                    to: 90,
                                    frames:2,//动画帧数
                                    // duration:50000,//持续时间
                                    interval: 100,
                                    next: "expandHeight"
                                },
                                start: ["stretch1","expandHeight"]
                            });
                        });
                    }

                    var opnameData2 = dataModel2.toDatas(function(data0){//过滤函数构建新的元素集合并返回
                        return data0.a('paras');
                    });
                    if(opnameData2.length>0){
                        disconnect();
                        connect(opnameData2, dataModel2);
                    } else {
                        disconnect();
                    }

                    dialog.setConfig({
                        // title: '',
                        titleBackground: '#fff',//指定标题栏的背景色
                        // titleColor: '#fff',//指定标题栏文字的颜色
                        // titleIcon: 'node_image',//标题栏图标
                        // titleAlign: 'left',//标题文本对齐方式，可选值为left/right/center
                        content: gv2,//指定对话框的内容，可以是html文本，或DOM对象，或HT组件对象（自动调用其invalidate）
                        contentPadding: 0,//对话框内容区域的内边距
                        width: 1600,
                        height: 850,
                        borderWidth:0,//对话框的边框宽度
                        draggable: true,//指定对话框是否可拖拽调整位置，可选值为true/false
                        // dragModel: 'inside',指定对话框拖拽模式，inside 表示对话框内容包含在父亲容器中，none 或 null 表示随意拖放
                        minDragSize: 50,//指定对话框在超出父亲容器的最小可拖动区域大小，默认为 20
                        closable: true,//可选值为true/false，表示是否显示关闭按钮
                        maximizable: true,//可选值为true/false，表示对话框是否可被最大化
                        resizeMode: "wh",//鼠标移动到对话框右下角可改变对话框的大小，此参数控制resize模式：w表示只调整宽度，h表示只调整高度，wh表示宽高都可调整，none表示不可调整宽高
                        // position: {x:0,y:0},//指定对话框的的位置，默认为字符串center
                        // buttons: [{//指定对话框按钮组内容
                        //     label: "Close",
                        //     // className: 'class',
                        //     action: function(button, e) {
                        //         dialog.hide();
                        //     }
                        // }],
                        // buttonsAlign: "center",//指定对话框按钮的对齐方式，可选值为left/right/center
                        action: function(item, e) {//全局回调函数，当点击按钮时，单个按钮的回调函数执行完成后再执行此全局回调函数，参数为函数，格式为：function(button, event) { }，button为点击的按钮，event为原生的事件对象(一般为MouseEvent)
                            // console.log(item, e);
                        }
                    });
                    dialog.show();

                    //增加事件监听器，参数为监听函数，派发的事件种类可以查看简单示例中的Demo
                    dialog.addEventListener(function(e) {
                        if(e.kind == 'hide'){
                            disconnect();
                            connect(paraDataList, dataModel);
                        }
                        if(e.kind == 'maximize' || e.kind == 'restore' || e.kind == 'betweenResize'){
                            // changeData();
                        }
                        // console.log(e);
                    });
                }
            }).catch(function(err){
                disLoad();
                console.log(err)
            });
        }
    };

    window.changeParas = function(event,data){
        var opnameData = dataModel.toDatas(function(data){//过滤函数构建新的元素集合并返回
            return data.a('paras');
        });
        paraDataList = [];
        if(opnameData.length>0){
            paraDataList = opnameData;
            clickSendData(data.getParent());
        } else {
            disconnect();
        }
    };

    function funcsAuthority(){
        getData();

        var date = new Date();
        var now = formateDate(date);
        var time1 = now.y + '-' +now.M+'-'+now.d+' 00:00:00';
        var next = formateDate(new Date(date.getTime()+24*60*60*1000));
        var time2 = next.y + '-' +next.M+'-'+next.d+' 00:00:00';
        $("#time1").val(time1);
        $("#time2").val(time2);
        laydate.render({
            elem: '#time1'
            ,type: 'datetime'
            ,lang: 'cn'
            ,value: time1
        });
        laydate.render({
            elem: '#time2'
            ,type: 'datetime'
            ,lang: 'cn'
            ,value: time2
        });
        element.render('breadcrumb');
    }

    //获取 ht
    function getData() {
        $('#chart1').html('');
        if(siteObj){
            load();
            request.service({
                method: 'post',
                url: '/comprehensiveShow/querySiteImg',
                data: {
                    'objType': siteObj.type,
                    'objId': siteObj.id
                }
            }).then(function (res) {
                disLoad();
                if(res.one){
                    htData = res.one.cfgPic;
                    init($('#chart1')[0]);
                }
            }).catch(function(err){
                disLoad();
                console.log(err)
            });
        }
    }

    function init(obj) {
        dataModel = new ht.DataModel();//数据模型类，作为data数据的容器和管理者
        var gv = new ht.graph.GraphView(dataModel);//GraphView:2D图形组件，
        gv.addToDOM(obj);
        window.addEventListener('resize', function (e) {
            gv.fitContent(true, 0);
        });

        var json = htData;
        dataModel.enableAnimation();//启动全局动画定时器，参数interval指定全局动画间隔

        var json2 = ht.Default.parse(json);//Default:js对象，包含HT系统模型的参数信息和工具函数
        dataModel.clear();
        dataModel.deserialize(json2);//反序列化
        gv.fitContent(true, 0); //第二个参数为缩放后图元区域与拓扑边缘的距离，默认为20

        var dlt = 5, max = 100000000;
        task = {
            interval: 100,
            action: function(data){
                if (data instanceof ht.Shape) {
                    if (!data.s('shape.dash')) return;
                    if( data['_tag'] == 'fixed') return;

                    var k = data.getToolTip();//简化属性-提示
                    k = k == 1?-1:1;
                    var offset = data.s('shape.dash.offset') || 0;
                    data.s('shape.dash.offset', (offset + k * dlt) % max);
                    data.s('shape.corner.radius', 15);
                    return;
                }
            }
        };

        setTimeout(function () {
            var animationObj = dataModel.toDatas(function(data) {return data.a('animation');});
            if (animationObj.length > 0) {
                animationObj.each(function(data) {
                    var animation = data.a('animation');
                    animation(data);
                });
            }

            var opnameData = dataModel.toDatas(function(data){//过滤函数构建新的元素集合并返回
                return data.a('paras');
            });
            paraDataList = [];
            if(opnameData.length>0){
                paraDataList = opnameData;
                disconnect();
                connect(opnameData,dataModel);
            } else {
                disconnect();
            }

            dataModel.each(function(data2){
                var dataBindings = data2.getDataBindings();
                if(dataBindings){
                    for(var name in dataBindings.p){
                        var db = dataBindings.p[name];
                        var value = '';
                        if(db.func){
                            value = db.func(data2,value);
                        }
                        data2.setTag(value);
                    }
                }
            });
            dataModel.removeScheduleTask(task);
            dataModel.addScheduleTask(task);
        }, 100);
    }

    var stompClient = null;
    function connect(opnameData, dm) {
        // 建立连接对象（还未发起连接）
        var sock = new SockJS('/jet-stomp-websocket',null,{'timeout':60000});
        // 获取 STOMP 子协议的客户端对象
        stompClient = Stomp.over(sock);
        // stompClient.heartbeat.outgoing = 10;  // client will send heartbeats every 20000ms
        // stompClient.heartbeat.incoming = 0;  // client does not want to receive heartbeats from the server

        // 向服务器发起websocket连接并发送CONNECT帧
        stompClient.connect({}, function (frame) {
            sendData(opnameData,dm);

            /*STOMP 客户端要想接收来自服务器推送的消息，必须先订阅相应的URL，即发送一个 SUBSCRIBE 帧，
            然后才能不断接收来自服务器的推送消息；订阅和接收消息通过 subscribe() 方法实现
            */
            // var sub1 = stompClient.subscribe("/serverStatus/energyRealTimeValue", function(response){
            //     onmessage(response,opnameData,dm,'energyRealTimeValue');
            // });

            // subscription.unsubscribe();//取消订阅
            /* 如果想让客户端订阅多个目的地，你可以在接收所有信息的时候调用相同的回调函数：
                onmessage = function(message) {
                    // called every time the client receives a message
                }
                var sub1 = client.subscribe("queue/test", onmessage);
                var sub2 = client.subscribe("queue/another", onmessage)
            */
        },function (error){
            //连接失败时（服务器响应 ERROR 帧）的回调方法
            console.log('连接失败【' + error + '】');
        });
    }

    var sub1,sub2,sub3,sub4,sub5;
    function sendData(opnameData,dm){
        timeIntervalArr = [];
        var index = 1;
        opnameData.each(function(data6){
            var paras = data6.a('paras');
            var op = JSON.stringify({'paras':paras,'key':index});
            data6.a('key',index);
            index += 1;

            var vt = data6.a('interval');
            var url = data6.a('url');
            var inter = vt?parseInt(vt)*1000:300000;
            if(op && url && stompClient){
                if(data6.a('interval') == 0){
                    stompClient.send(url, {}, op);
                }else if(parseInt(data6.a('interval')) > 0) {
                    if (url == '/htweb/energyRealTimeLoadRanking') {//实时负荷排名
                        stompClient.send(url, {}, op);
                        rankingInterval = setInterval(function () {
                            stompClient.send(url, {}, op);
                        }, inter);
                    } else if (url == '/htweb/loadTodayHistoryValue') {//实时负荷
                        stompClient.send(url, {}, op);
                        loadInterval = setInterval(function () {
                            stompClient.send(url, {}, op);
                        }, inter);
                    } else if (url == '/htweb/energyTodayUsageRanking') {//当日能耗排名
                        stompClient.send(url, {}, op);
                        dayRankingInterval = setInterval(function () {
                            stompClient.send(url, {}, op);
                        }, inter);
                    } else {
                        stompClient.send(url, {}, op);
                        var indexs = setInterval(function () {
                            stompClient.send(url, {}, op);
                        }, inter);
                        timeIntervalArr.push(indexs);
                    }
                }

                switch(url){
                    case '/htweb/energyTodayDiffValue'://中下角(各种能源或标煤 瞬时量 的当日差值)
                        if(!sub1){
                            sub1 = stompClient.subscribe("/serverStatus/energyTodayDiffValue", function(response){
                                var obj = JSON.parse(response.body);
                                opnameData.each(function(data8){
                                    if(data8.a('key') == obj.key){
                                        onmessage(response,data8,dm);
                                    }
                                });
                            });
                        }
                        break;
                    case '/htweb/energyRealTimeLoadRanking'://实时负荷排名
                        if(!sub2){
                            sub2 = stompClient.subscribe("/serverStatus/energyRealTimeLoadRanking", function(response){
                                var obj = JSON.parse(response.body);
                                opnameData.each(function(data9){
                                    if(data9.a('key') == obj.key){
                                        onmessage(response,data9,dm);
                                    }
                                });
                            });
                        }
                        break;
                    case '/htweb/energyConsumption'://能源环比
                        if(!sub3){
                            sub3 = stompClient.subscribe("/serverStatus/energyConsumption", function(response){
                                var obj = JSON.parse(response.body);
                                opnameData.each(function(data10){
                                    if(data10.a('key') == obj.key){
                                        onmessage(response,data10,dm);
                                    }
                                });
                            });
                        }
                        break;
                    case '/htweb/energyTodayUsageRanking'://当日能耗排名
                        if(!sub5){
                            sub5 = stompClient.subscribe("/serverStatus/energyTodayUsageRanking", function(response){
                                var obj = JSON.parse(response.body);
                                opnameData.each(function(data12){
                                    if(data12.a('key') == obj.key){
                                        onmessage(response,data12,dm);
                                    }
                                });
                            });
                        }
                        break;
                    case '/htweb/loadTodayHistoryValue':// 当天实时负荷
                        if(!sub4){
                            sub4 = stompClient.subscribe("/serverStatus/loadTodayHistoryValue", function(response){
                                var obj = JSON.parse(response.body);
                                opnameData.each(function(data11){
                                    if(data11.a('key') == obj.key){
                                        onmessage(response,data11,dm);
                                    }
                                });
                            });
                        }
                        break;
                    case '/htweb/energyRealTimeValue':// 综合展示最新值查询
                        if(!sub4){
                            sub4 = stompClient.subscribe("/serverStatus/energyRealTimeValue", function(response){
                                var obj = JSON.parse(response.body);
                                opnameData.each(function(data11){
                                    if(data11.a('key') == obj.key){
                                        onmessage(response,data11,dm);
                                    }
                                });
                            });
                        }
                        break;
                }
            }
        });
    }

    function clickSendData(data7){
        var paras = data7.a('paras');
        var op = JSON.stringify({'paras':paras,'key':data7.a('key')});
        var vt = data7.a('interval');
        var url = data7.a('url');
        var inter = vt?parseInt(vt)*1000:300000;
        if(op && url && stompClient){
            if(data7.a('interval') == 0){
                stompClient.send(url, {}, op);
            }else if(parseInt(data7.a('interval')) > 0) {
                clearInterval(rankingInterval);
                clearInterval(dayRankingInterval);
                stompClient.send(url, {}, op);

                if (url == '/htweb/energyRealTimeLoadRanking') {
                    rankingInterval = setInterval(function () {
                        stompClient.send(url, {}, op);
                    }, inter);
                }else if (url == '/htweb/energyTodayUsageRanking') {
                    dayRankingInterval = setInterval(function () {
                        stompClient.send(url, {}, op);
                    }, inter);
                } else{
                    loadInterval = setInterval(function () {
                        stompClient.send(url, {}, op);
                    }, inter);
                }
            }
        }
    }

    function onmessage(response,ajaxData,dm){
        var obj = JSON.parse(response.body);
        var animation = ajaxData.a('animation');
        if(animation) animation(ajaxData,obj['paras']);

        var op = ajaxData.a('paras');
        var url = ajaxData.a('url');
        if(op && url){
            var update = ajaxData.a('update');
            if(update) update(ajaxData,obj['paras']);
        }

        dm.each(function(data2){
            var dataBindings = data2.getDataBindings();
            if(dataBindings){
                for(var name in dataBindings.p){
                    var db = dataBindings.p[name];
                    var value = '';
                    if(db.func){
                        value = db.func(data2,value);
                    }
                    data2.setTag(value);
                }
            }
        });
        dm.removeScheduleTask(task);//删除调度任务，其中task为以前添加过的调度任务对象。
        dm.addScheduleTask(task);

        response.ack();//通知服务端它已经接收了消息
    }

    function disconnect() {
        if (stompClient !== null) {
            //从客户端主动断开连接，可调用 disconnect() 方法,异步进行，因此包含了回调参数，操作完成时自动回调；
            stompClient.disconnect();
        }
        clearInterval(rankingInterval);
        clearInterval(dayRankingInterval);
        clearInterval(loadInterval);
        $.each(timeIntervalArr,function(i,vv){
            if(vv) clearInterval(vv);
        });
        if(sub1) sub1.unsubscribe();//取消订阅
        if(sub2) sub2.unsubscribe();//取消订阅
        if(sub3) sub3.unsubscribe();//取消订阅
        if(sub4) sub4.unsubscribe();//取消订阅
        if(sub5) sub5.unsubscribe();//取消订阅
        sub1 = undefined;
        sub2 = undefined;
        sub3 = undefined;
        sub4 = undefined;
        sub5 = undefined;
        console.log("Disconnected");
    }

    //历史数据
    function getHisData(time1,time2){
        $("#chart-content").html('');
        var data = {
            "start": time1?time1:$("#time1").val(),
            "end": time2?time2:$("#time2").val(),
            "interval": interval,
            "point": opnameDia
        };
        load();
        request.service({
            method: 'post',
            url: '/common/queryComprehensiveHistoryInfoData',
            data: data
        }).then(function (res) {
            disLoad();
            seriesData = [];
            xData = [];
            if(res.one){
                $.each(res.one.timestamps,function(i,v){
                    var date = formateDate(new Date(v));
                    xData.push(date.y+'-'+date.M+'-'+date.d+' '+date.h+':'+date.m);
                });
                $.each(res.one.values,function(i,v){
                    if(v){
                        seriesData.push(parseFloat(v.toFixed(2)));
                    }else{
                        seriesData.push(v);
                    }
                });
                initHis();
            }
        }).catch(function(err){
            disLoad();
            console.log(err);
        });
    }

    function initHis(){
        if (chart1 != null && chart1 != "" && chart1 != undefined) {
            chart1.dispose();
        }
        chart1 = echarts.init(document.getElementById('chart-content'));
        var option = {
            title : {
                text: opnameDia,
                left: 'center',
                subtext: ''
            },
            grid:{   //绘图区调整
                x:30,  //左留白
                y:80,   //上留白
                x2:20,  //右留白
                y2:20,   //下留白
                containLabel: true,//grid 区域是否包含坐标轴的刻度标签。
                borderColor: '#ccc',
                borderWidth: 1
            },
            tooltip : {
                trigger: 'axis',
                axisPointer: {
                    type: 'line'
                }
            },
            dataZoom: [
                {
                    type: 'inside',
                    start: 0,
                    end: 100,
                    xAxisIndex: [0]
                }
            ],
            legend: {
                left: 30,
                top: 28,
                textStyle:{
                    color:'#ccc'
                }
            },
            toolbox: {
                show: false
            },
            calculable : true,
            xAxis : [
                {
                    type : 'category',
                    data : xData,
                    // name : i18next.t('workCalendar.time'),//时间
                    boundaryGap : false,
                    axisTick: {show:true},
                    axisLine: {lineStyle:{color:'#ddd',width:1}},
                    axisLabel: {
                        textStyle:{
                            color:"#ccc"
                        }
                    },
                    splitLine:{//坐标轴在 grid 区域中的分隔线
                        show :false
                    }
                }
            ],
            yAxis : [
                {
                    type : 'value',
                    name : opnameDia,
                    nameGap: 70,
                    nameLocation: 'middle',
                    scale: true,
                    axisLabel: {//坐标轴刻度标签的相关设置。
                        textStyle:{
                            color:"#ccc"
                        }
                    }
                }
            ],
            series : [{
                name:opnameDia,
                type:'line',
                data:seriesData
            }]
        };

        chart1.setOption(option, true);
    }

    function formateDate(date) {
        var y = date.getFullYear();
        var M = date.getMonth() + 1;
        M = M < 10 ? ('0' + M) : M;
        var d = date.getDate();
        d = d < 10 ? ('0' + d) : d;
        var h = date.getHours();
        h = h < 10 ? ('0' + h) : h;
        var m = date.getMinutes();
        m = m < 10 ? ('0' + m) : m;
        var s = date.getSeconds();
        s = s < 10 ? ('0' + s) : s;

        return {'y':y,'M':M,'d':d,'h':h,'m':m,'s':s};
    }

    var indexLoading;
    function load(){
        indexLoading =layer.load(1, {shade: [0.3, '#fff']});
    }
    function disLoad(){
        layer.close(indexLoading);
    }
});