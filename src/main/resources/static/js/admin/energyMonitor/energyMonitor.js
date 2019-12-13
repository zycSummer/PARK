/*
 * @Author: jpp
 * @Date: 2019-10-22 17:07:34
 * @Last Modified by: jpp
 * @Last Modified time: 2019-11-05 09:37:35
 */

layui.use(['request','form', 'element', 'layer', 'jquery','laydate'], function () {
    var form = layui.form,
        layer = layui.layer,
        laydate = layui.laydate,
        $ = layui.jquery,
        element = layui.element;

    var siteObj = JSON.parse(sessionStorage.getItem('parkId'));
    var tree1 = '';
    var htData = {},setTimeInterval1 = null;
    var task;
    var opnameDia,interval = 5,chart1,xData=[],seriesData=[];//弹框

    // $(document).not('#monitor_content').on("click",function(){
    //     disconnect();
    // });
    $(parent.window.document).not('#monitor_content').on("click",function(){
        disconnect();
    });

    // window.onunload = function (ev) {console.log(ev)
    //     // var n = window.event.screenX - window.screenLeft;
    //     // var b = n > document.documentElement.scrollWidth-20;
    //     // if(b && window.event.clientY < 0 || window.event.altKey) {
    //     //     alert("是关闭而非刷新");
    //     //     window.event.returnValue = ""; //这里可以放置你想做的操作代码    
    //     // }
    //     disconnect();
    // };

    funcsAuthority();

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
        var dialog = new ht.widget.Dialog();
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

    function funcsAuthority(){
        getTree();

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

    function getTree(){
        if(siteObj){
            $("#tree").html('');
            load();
            request.service({
                method: 'post',
                url: '/energyMonitoring/queryLeftHtImg',
                data: {
                    'objType': siteObj.type,
                    'objId': siteObj.id
                }
            }).then(function (res) {
                disLoad();
                var list = res.data?res.data:[];

                var setting = {
                    view: {
                        addDiyDom:null,//用于在节点上固定显示用户自定义控件
                        autoCancelSelected: false,
                        dblClickExpand: true,//双击节点时，是否自动展开父节点的标识
                        showLine: true,//设置 zTree 是否显示节点之间的连线。
                        selectedMulti: false//设置是否允许同时选中多个节点。
                    },
                    data: {
                        keep:{
                            leaf:false,//zTree 的节点叶子节点属性锁，是否始终保持 isParent = false
                            parent:false//zTree 的节点父节点属性锁，是否始终保持 isParent = true
                        },
                        key:{
                            children:"children",//节点数据中保存子节点数据的属性名称。
                            name:"htImgName",//节点数据保存节点名称的属性名称。
                            title:"htImgId"//节点数据保存节点提示信息的属性名称
                        },
                        simpleData: {
                            enable: true,//是否采用简单数据模式 (Array)
                            idKey: "htImgId",//节点数据中保存唯一标识的属性名称。
                            pIdKey: "parentId",//节点数据中保存其父节点唯一标识的属性名称
                            rootPId: ""//用于修正根节点父节点数据，即 pIdKey 指定的属性值。
                        }
                    },
                    callback: {
                        onClick:function(event,treeId,treeNode){
                            getData();
                        }
                    }
                };
                tree1 = $.fn.zTree.init($("#tree"), setting, list);
                tree1.expandAll(true);
                var nodes = tree1.getNodes();
                if (nodes.length>0) {
                    tree1.selectNode(nodes[0]);
                    getData();
                }else{
                    $("#tree").html('');
                    return layer.msg('没有树结构');
                }
            }).catch(function(err){
                disLoad();
                console.log(err)
            });
        }
    }

    //获取 ht
    function getData() {
        $('#chart1').html('');
        var val1 = tree1.getSelectedNodes();
        if(siteObj){
            load();
            request.service({
                method: 'post',
                url: '/energyMonitoring/queryRightHtImg',
                data: {
                    'objType': siteObj.type,
                    'objId': siteObj.id,
                    'htImgId': val1[0].htImgId
                }
            }).then(function (res) {
                disLoad();
                if(res.one){
                    htData = res.one?res.one.cfgPic:{
                        "v": "7.0.1",
                        "p": {
                            "autoAdjustIndex": true,
                            "hierarchicalRendering": true
                        },
                        "a": {
                            "connectActionType": null,
                            "rotateAsClock": false
                        },
                        "d": []
                    };
                    init();
                }
            }).catch(function(err){
                disLoad();
                console.log(err);
            });
        }
    }

    function init() {
        var dataModel = new ht.DataModel();//数据模型类，作为data数据的容器和管理者
        var gv = new ht.graph.GraphView(dataModel);//GraphView:2D图形组件，
        gv.addToDOM($('#chart1')[0]);
        window.addEventListener('resize', function (e) {
            gv.fitContent(true, 0);
        });

        var json = htData;
        var dm = gv.dm();//获取或设置数据模型
        var json2 = ht.Default.parse(json);//Default:js对象，包含HT系统模型的参数信息和工具函数
        dm.clear();
        dm.deserialize(json2);//反序列化
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
            var animationObj = dm.toDatas(function(data) {return data.a('animation');});
            if (animationObj.length > 0) {
                animationObj.each(function(data) {
                    var animation = data.a('animation');
                    animation(data);
                });
            }

            var opnameData = dm.toDatas(function(data){//过滤函数构建新的元素集合并返回
                return data.a('query')?data.a('query'):data.a('opname');
            });

            var opArr = [];
            if(opnameData.length>0){
                opnameData.each(function(data){
                    var op = data.a('query')?data.a('query'):data.a('opname');
                    var vt = data.a('valueType');
                    var obj = {'opName':op,'valueType':vt};

                    if(opArr.length>0){
                        var unique = true;
                        $.each(opArr,function(m,n){
                            if(op == n.opName && vt == n.valueType){
                                unique = false;
                            }
                        });
                        if(unique) opArr.push(obj);
                    }else{
                        opArr.push(obj);
                    }
                });
                // changePumpData(opnameData,opArr,dataModel);
                disconnect();
                connect(opnameData,opArr,dataModel);
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

    //实时监测的测点数据
    function changePumpData(opnameData,opArr,dataModel){
        if(siteObj){
            load();
            request.service({
                method: 'post',
                url: '/energyMonitoring/queryActualMonitorData',
                data: {"opNameList":opArr,"objType": siteObj.type,"objId":siteObj.id}
            }).then(function (res) {
                disLoad();
                if(res.one){
                    var obj = res.one;
                    opnameData.each(function(data) {
                        var op = data.a('query')?data.a('query'):data.a('opname');
                        var opAndValueType = op + data.a('valueType');

                        var animation = data.a('animation');
                        if(animation) animation(data,obj[opAndValueType]);

                        var update = data.a('update');
                        if(update) update(data,obj[opAndValueType]);
                    });

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

                    dataModel.removeScheduleTask(task);//删除调度任务，其中task为以前添加过的调度任务对象。
                    dataModel.addScheduleTask(task);
                }
            }).catch(function(err){
                disLoad();
                console.log(err)
            });
        }
    }

    var stompClient = null;
    var CONNECT_COUNT = 3;
    var isClose = false;
    var connectCount = 1;
    function connect(opnameData,opArr,dataModel) {
        var sock = new SockJS('/jet-stomp-websocket');
        // 获取 STOMP 子协议的客户端对象
        stompClient = Stomp.over(sock);

        // 向服务器发起websocket连接并发送CONNECT帧
        stompClient.connect({}, function (frame) {
            var data = {
                'opNameList': opArr,
                'objType': siteObj.type,
                'ObjId': siteObj.id
            };
            stompClient.send("/app/queryActualMonitorData", {}, JSON.stringify(data));
            setTimeInterval1 = setInterval(function() {
                stompClient.send("/app/queryActualMonitorData", {}, JSON.stringify(data));
            }, 300*1000);

            /*STOMP 客户端要想接收来自服务器推送的消息，必须先订阅相应的URL，即发送一个 SUBSCRIBE 帧，
            然后才能不断接收来自服务器的推送消息；订阅和接收消息通过 subscribe() 方法实现
            */
            var subscription = stompClient.subscribe('/serverStatus/queryActualMonitorData', function (response) {
                if(response.body){
                    var obj = JSON.parse(response.body);
                    opnameData.each(function(data) {
                        var op = data.a('query')?data.a('query'):data.a('opname');
                        var opAndValueType = op + data.a('valueType');

                        var animation = data.a('animation');
                        if(animation) animation(data,obj[opAndValueType]);

                        var update = data.a('update');
                        if(update) update(data,obj[opAndValueType]);
                    });

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

                    dataModel.removeScheduleTask(task);//删除调度任务，其中task为以前添加过的调度任务对象。
                    dataModel.addScheduleTask(task);
                }
                response.ack();//通知服务端它已经接收了消息
            },{ack: 'stompClient'});
            // subscription.unsubscribe();//取消订阅
        },function (error){
            //连接失败时（服务器响应 ERROR 帧）的回调方法
            console.log('连接失败【' + error + '】');
        });

        stompClient.onopen = function () {
            connectCount = 1;   // 重置重连次数
            this.checkHeartBeat();  // 心跳检测
        };

        stompClient.onclose = function (ev) {
            console.log("closed");
            // 已经关闭的情况，不重连
            if (isClose) {
                return;
            }
            // 小于重连次数
            if (connectCount < CONNECT_COUNT) {
                setTimeout(function() {
                    connect();
                }, (Math.random() * 3 + 1 )* 1000);
            } else {
                isClose = true;
            }
        };
    }

    function disconnect() {
        if (stompClient !== null) {
            //从客户端主动断开连接，可调用 disconnect() 方法,异步进行，因此包含了回调参数，操作完成时自动回调；
            stompClient.disconnect();
        }
        if(setTimeInterval1) clearInterval(setTimeInterval1);
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
            url: '/common/queryMonitorHistoryInfoData',
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