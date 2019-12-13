/*
 * @Author: jpp
 * @Date: 2019-10-23 13:07:34
 * @Last Modified by: jpp
 * @Last Modified time: 2019-11-12 09:44:26
 */

layui.use(['request','element','layer','request','laydate'], function () {
    var laydate = layui.laydate,
        element = layui.element;

    var siteObj = JSON.parse(sessionStorage.getItem('parkId'));
    var energyTypeId = '';//能源种类
    var timeType = 'days';//年、月、日
    var objType = '';//对象类别
    var chartData, gv, overview, autoLayout,unit = '';

    funcsAuthority();

    //点击 能源种类
    $(document).on("click", ".energyType a", function () {
        $(this).addClass('layui-this');
        $(this).siblings().removeClass('layui-this');

        energyTypeId = $(this).attr('data-id');
        getObjectType();
        return false;
    });

    //点击 对象类别
    $(document).on("click", ".objectType a", function () {
        $(this).siblings().removeClass('layui-this');
        $(this).addClass('layui-this');

        objType = $(this).attr('data-id');
        return false;
    });

    //查询 查询所选对象在所选时间范围内的所选能源的使用量
    $(".search").on('click', function () {
        getChartData();
    });

    //点击 时间类型
    $(".timeType a").on('click', function () {
        $(this).siblings().removeClass('layui-this');
        $(this).addClass('layui-this');

        timeType = $(this).attr('data-id');
        initTime();
        return false;
    });

    /*----------------------------------函数------------------------------------*/
    // 能源种类
    function getEnergyType() {
        $('.energyType').html('');
        if(siteObj){
            load();
            request.service({
                method: 'get',
                url: '/common/queryHistoryLeftData'
            }).then(function (res) {
                disLoad();
                var str = '';
                $.each(res.data, function (i0,v0) {
                    if (i0 == 0) {
                        energyTypeId = v0['energyTypeId'];
                        str += '<a href="javascript:void(0);" class="breadcrumb layui-this" data-id="' + v0['energyTypeId'] + '">' + v0['energyTypeName'] + '</a>';
                    } else {
                        str += '<a href="javascript:void(0);" class="breadcrumb" data-id="' + v0['energyTypeId'] + '">' + v0['energyTypeName'] + '</a>';
                    }
                });
                $('.energyType').html(str);
                element.render('breadcrumb');

                getObjectType();
            }).catch(function(err){
                disLoad();
                console.log(err)
            });
        }
    }

    // 对象类别
    function getObjectType() {
        $('.objectType').html('');
        if(siteObj && energyTypeId){
            load();
            request.service({
                method: 'post',
                url: '/energyBalance/getObjectClassType',
                data: {
                    'objType': siteObj.type,
                    'objId': siteObj.id,
                    'energyTypeId':energyTypeId
                }
            }).then(function (res) {
                disLoad();
                var str = '';
                $.each(res.data,function(ii,vv){
                    if (ii == 0) {
                        objType = vv.orgTreeId;
                        str += '<a href="javascript:void(0);" class="breadcrumb layui-this" data-id="' + vv.orgTreeId + '">' + vv.orgTreeName + '</a>';
                    } else {
                        str += '<a href="javascript:void(0);" class="breadcrumb" data-id="' + vv.orgTreeId + '">' + vv.orgTreeName + '</a>';
                    }
                });
                $('.objectType').html(str);
                element.render('breadcrumb');

                getChartData();
            }).catch(function(err){
                disLoad();
                console.log(err)
            });
        }
    }

    function getChartData() {
        var time = $("#timeInput").val(),timestamp;
        if (timeType == 'years') {
            timestamp = moment(time,'YYYY').valueOf();
        } else if (timeType == 'months') {
            timestamp = moment(time,'YYYY-MM').valueOf();
        } else {
            timestamp = moment(time,'YYYY-MM-DD').valueOf();
        }

        if(siteObj && energyTypeId){
            load();
            request.service({
                method: 'post',
                url: '/energyBalance/getTreeData',
                data: {
                    'objType': siteObj.type,
                    'objId': siteObj.id,
                    'energyTypeId':energyTypeId,
                    'orgTreeId':objType,
                    'timestamp':timestamp,
                    'timeUnit':timeType
                }
            }).then(function (res) {
                disLoad();
                $('#chart1').html('');
                chartData = {};
                if (res.one) {
                    chartData = res.one.node;
                    unit = res.one.unit;
                    init();
                } else {
                    return layer.msg('没有树结构');
                }
            }).catch(function(err){
                disLoad();
                console.log(err)
            });
        }
    }

    var ToggleOverview = function (graphView) {
        var self = this;
        ToggleOverview.superClass.constructor.apply(self, [graphView]);
        self._expand = true;

        var div = document.createElement("div");
        div.style.setProperty("width", "24px", null);
        div.style.setProperty("height", "24px", null);
        div.style.setProperty("position", "absolute", null);
        div.style.setProperty("left", "0", null);
        div.style.setProperty("top", "0", null);
        div.style.setProperty("background", " url(custom/images/shrink.png) no-repeat", null);
        div.style.setProperty("background-position", "center center", null);
        div.style.setProperty("transform", "rotate(-90deg)", null);
        self._view.appendChild(div);

        function handleTransitionEnd(e) {
            if (e.propertyName === "width") {
                self.invalidate();//无效组件，并调用延时刷新
            }
        }

        self._view.addEventListener("webkitTransitionEnd", handleTransitionEnd, false);
        self._view.addEventListener("transitionend", handleTransitionEnd, false);
        var eventName = ht.Default.isTouchable ? "touchstart" : "mousedown";
        div.addEventListener(eventName, function (e) {
            if (self._expand) {
                self._view.style.setProperty("width", "24px", null);
                self._view.style.setProperty("height", "24px", null);
                self._canvas.style.setProperty("opacity", "0", null);
                self._mask.style.setProperty("opacity", "0", null);
                div.style.setProperty("background-image", "url(custom/images/expand.png)", null);
                div.style.setProperty("width", "24px", null);
                div.style.setProperty("height", "24px", null);
                div.style.setProperty("transform", "rotate(-90deg)", null);
                self._expand = false;
            } else {
                self._view.style.setProperty("width", "", null);
                self._view.style.setProperty("height", "", null);
                self._canvas.style.setProperty("opacity", "1", null);
                self._mask.style.setProperty("opacity", "1", null);
                div.style.setProperty("background-image", "url(custom/images/shrink.png)", null);
                div.style.setProperty("width", "24px", null);
                div.style.setProperty("height", "24px", null);
                self._expand = true;
            }
            self.invalidate();
            e.stopPropagation();
        });
        self.setContentBackground("white");
        // 通过getMaskBackground和setMaskBackground获取和设置可见区域遮罩背景颜色
        // 通过getContentBorderColor和setContentBorderColor获取和设置内容边框颜色
        // 通过getContentBackground和setContentBackground获取和设置内容背景颜色
        // 通过isAutoUpdate和setAutoUpdate获取和设置是否自动同步绑定的GraphView组件变化
    };
    ht.Default.def(ToggleOverview, ht.graph.Overview, {});//定义类 类名 要继承的父类 方法和变量声明

    function init() {
        gv = new ht.graph.GraphView();
        gv.getView().className = 'main';
        initModel();//初始化节点

        // var rightView = new ht.widget.SplitView(propertyView, tablePane, 'v', 0.4);//分割组件，v分为上下层，比例为0.4:0.6
        // rightView.getView().style.borderLeft = '1px solid #000';
        var borderPane = new ht.widget.BorderPane();//边框面板组件
        // borderPane.setRightView(rightView, 400);//设置 borderPane 右边组件为 rightView，宽度为400
        borderPane.setCenterView(gv);//设置 borderPane 中间组件为 gv
        borderPane.addToDOM($('#chart1')[0]);//将 borderPane 组件添加进 body 中

        createDirectionForm();//创建左上角的form表单

        // gv.dm().enableDataBindings();
        // gv.dm().enableAnimat();
        // gv.setSelectableFunc(function (data) {
        //     return false
        // });
        // gv.setEditable(false);
        // gv.enableEvent();

        gv.enableFlow();// 启动流动，默认为false，interval表示流动的时间间隔
        // disableFlow() 停止流动
        // flowInterval 通过setFlowInterval(interval)和getFlowInterval()操作，控制流动的时间间隔
        gv.enableDashFlow();// 启动虚线流动，默认为false，interval表示流动的时间间隔
        // gv.disableDashFlow();// 停止虚线流动
        // gv.setDashFlowInterval(100);// 通过setDashFlowInterval(interval)和getDashFlowInterval()操作，控制虚线流动的时间间隔

        //鹰眼组件
        overview = new ToggleOverview(gv);
        overview.getView().className = "overview animation";
        overview.addToDOM($('#chart1')[0]);
        overview.getView().style.left = '';
        overview.getView().style.top = '';
        // window.addEventListener('resize', function(e) {
        //     gv.invalidate();
        // }, false);

//Interactor交互器基类，提供了基础功能函数，如交互事件派发，监听函数添加和清除，拖拽操作封装，自动平移滚动等功能
        gv.addInteractorListener(function (e) {
            if (e.kind === 'doubleClickData') {
                var nodes = e.data;
                if(nodes.a('hasChilds')){//如果有下一级节点
                    var flagsNames = '';
                    if(nodes.s('icons')){
                        flagsNames = nodes.s('icons').flags?nodes.s('icons').flags.names[0]:false;
                    }

                    if(flagsNames == 'expand'){
                        if(!nodes.hasChildren() && nodes.a('taken')){
                            return false;
                            //双击显示该节点的下一级所有节点
                            var objectStatus = $(".option .layui-this").attr('lay');
                            var data = {
                                'status': objectStatus,
                                'parkId': cmid,
                                'taken': nodes.a('taken'),
                                'energyType': energyType,
                                'start': $("#time1").val(),
                                'end': $("#time2").val()
                            };
                            load();
                            request.service({
                                method: 'post',
                                url: '/energyBalance/getTreeData',
                                data: data
                            }).then(function (res) {
                                disLoad();
                                var val = res.one.head.value!=null?parseFloat(res.one.head.value.toFixed(2)):'';
                                var unit = res.one.head.unit!=null?res.one.head.unit:'';
                                nodes.s({
                                    'label2':val+unit
                                });

                                nodes.clearChildren();
                                recursive(res.one.child, nodes);//递归 添加节点和流动

                                nodes.eachChild(function (child) {
                                    child.s('2d.visible', true);
                                    nodes.s('icons',{
                                        flags:{
                                            position:8,
                                            direction:'east',
                                            offsetY:9,
                                            names:['shrink']
                                        }
                                    });
                                    layout('towardsouth', true);
                                });

                                var selectionModel = gv.dm().getSelectionModel();//选择模型
                                selectionModel.removeSelection(nodes);//取消选中对象
                            }).catch(function(err){
                                disLoad();
                                console.log(err)
                            });
                        }else{
                            nodes.eachChild(function (child) {
                                child.s('2d.visible', true);
                                nodes.s('icons',{
                                    flags:{
                                        position:8,
                                        direction:'east',
                                        offsetY:9,
                                        names:['shrink']
                                    }
                                });
                                layout('towardsouth', true);
                            });

                            var selectionModel = gv.dm().getSelectionModel();//选择模型
                            selectionModel.removeSelection(nodes);//取消选中对象
                        }
                    }else{
                        nodes.eachChild(function (child) {
                            child.s('2d.visible', false);
                            nodes.s('icons',{
                                flags:{
                                    position:8,
                                    direction:'east',
                                    offsetY:9,
                                    names:['expand']
                                }
                            });

                            shrink(child);
                            layout('towardsouth', true);
                        });
                    }
                }else{//如果没有下一级节点

                }
            }
        });

        autoLayout = new ht.layout.AutoLayout(gv);
        setTimeout(function () {
            layout('towardsouth', true);//因为图片还没加载出来的时候，自动布局就按照节点的默认大小来布局的
        }, 400);
    }

    //递归收缩
    function shrink(nodes) {
        nodes.eachChild(function (child) {
            if (child.s('2d.visible')) {
                child.s('2d.visible', false);
                nodes.s('icons',{
                    flags:{
                        position:8,
                        direction:'east',
                        offsetY:9,
                        names:['expand']
                    }
                });

                shrink(child);
            }
        });
    }

    function createDirectionForm() {
        var form = new ht.widget.FormPane();
        form.setWidth(200);
        form.setHeight(80);
        $('#chart1')[0].appendChild(form.getView());
        form.getView().style.background = '#fff';
        form.getView().style.boxShadow = '4px 16px 16px rgba(0, 0, 0, 0.1)';
        form.addRow([
            {
                element: '自动布局:',
            }
        ], [0.1]);
        form.addRow([
            {
                button: {
                    icon: './symbols/demo/layout/南布局.json',
                    onClicked: function () {
                        layout('towardsouth', true);
                    },
                    background: null,
                    labelColor: '#fff',
                    groupId: 'btn',
                    toolTip: '朝南布局',
                    borderColor: null
                }
            },
            {
                button: {
                    icon: './symbols/demo/layout/北布局.json',
                    onClicked: function () {
                        layout('towardnorth', true);
                    },
                    background: null,
                    labelColor: '#fff',
                    groupId: 'btn',
                    toolTip: '朝北布局',
                    borderColor: null
                }
            },
            {
                button: {
                    icon: './symbols/demo/layout/西布局.json',
                    onClicked: function () {
                        layout('towardwest', true);
                    },
                    background: null,
                    labelColor: '#fff',
                    groupId: 'btn',
                    toolTip: '朝西布局',
                    borderColor: null
                }
            },
            {
                button: {
                    icon: './symbols/demo/layout/东布局.json',
                    onClicked: function () {
                        layout('towardeast', true);
                    },
                    background: null,
                    labelColor: '#fff',
                    groupId: 'btn',
                    toolTip: '朝东布局',
                    borderColor: null
                }
            },
            {
                button: {
                    icon: './symbols/demo/layout/层次布局.json',
                    onClicked: function () {
                        layout('hierarchical', true);
                    },
                    background: null,
                    labelColor: '#fff',
                    groupId: 'btn',
                    toolTip: '层次布局',
                    borderColor: null
                }
            },
            {
                button: {
                    icon: './symbols/demo/layout/对称布局.json',
                    onClicked: function () {
                        layout('symmetric', true);
                    },
                    background: null,
                    labelColor: '#fff',
                    groupId: 'btn',
                    toolTip: '对称布局',
                    borderColor: null
                }
            },
            {
                button: {
                    icon: './symbols/demo/layout/圆形布局.json',
                    onClicked: function () {
                        layout('circular', true);
                    },
                    background: null,
                    labelColor: '#fff',
                    groupId: 'btn',
                    toolTip: '圆形布局',
                    borderColor: null
                }
            }
        ], [0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1]);
        return form;
    }

    function layout(direction, anim) {
        autoLayout.setAnimate(anim);
        autoLayout.layout(direction, function () {
            gv.fitContent(anim);
        });
    }

    ht.Default.setImage('expand',{
        width:50,
        height:50,
        comps:[{
            type:'text',
            text:'+',
            font:'bold 22px arial, sans-serif',
            rect:[20,2,0,40],
            color:'red'
        }]
    });
    ht.Default.setImage('shrink',{
        width:50,
        height:50,
        comps:[{
            type:'text',
            text:'-',
            font:'bold 22px arial, sans-serif',
            rect:[20,2,0,40],
            color:'red'
        }]
    });

    //ht.Node类型是GraphView和Graph3dview呈现节点图元的基础类，继承于Data类
    function createNode(data, parent,child) {//创建Node节点
        var node = new ht.Node();
        node.setImage(data.icon);
        node.setParent(parent);//设置父元素
        var val = data.value!=null?parseFloat(data.value.toFixed(2)):'';
        node.s({
            'label':data.nodeName,
            'label.position':3,
            'label.color': '#000',
            'label.font':'12px arial, sans-serif',
            'label2':val+' '+unit,
            'label2.position':3,
            'label2.offset.x':0,
            'label2.offset.y':57,
            'label2.background':'rgba(174, 188, 255, 0.5)'
        });

        node.a('per',data.percent);//百分比
        node.a('hasChilds',data.haveChildren);//是不是最后一级节点
        node.setSize(40, 40);
        if (parent && data.nodeName == '损耗') {
            node.s({
                'label':'损耗',
                'opacity': 0.3
            });
        }
        gv.dm().add(node);

        //使用量
        // var textNode = new ht.HtmlNode();
        // textNode.setHtml(document.getElementById("use-template").innerHTML);
        // textNode.setScalable(true);
        // var context = {
        //     id: textNode.getId(),
        //     rows: data
        // };
        // textNode.setContext(context);
        // gv.dm().add(textNode);

        if (parent != null) {
            parent.s({
                'body.color':'',
                'icons':{
                    flags:{
                        position:8,
                        direction:'east',
                        offsetY:9,
                        names:['shrink']
                    }
                }
            });

            if(data.haveChildren){
                node.s({
                    'body.color':'',
                    'icons':{
                        flags:{
                            position:8,
                            direction:'east',
                            offsetY:9,
                            names:['expand']
                        }
                    }
                });
            }else{
                node.s({'body.color': '#fffacf'});
            }
        }else{
            if(child && child.length>0){
                node.s({
                    'body.color':'',
                    'icons':{
                        flags:{
                            position:8,
                            direction:'east',
                            offsetY:9,
                            names:['expand']
                        }
                    }
                });
            }else{
                node.s({'body.color': '#fffacf'});
            }
        }

        node.s('2d.visible', true);//图元是否可见
        // textNode.s('2d.visible', true);

        // textNode.setHost(node);//getHost()和setHost(host)获取和设置吸附宿主对象，当节点吸附上宿主图元时，宿主移动或旋转时会带动所有吸附者
        // textNode.getAttaches();//获取吸附到自身的所有节点的ht.List类型数组
        // textNode.onHostChanged(oldHost, newHost);//当吸附宿主对象发生变化时回调该函数，可重载做后续处理
        // textNode.handleHostPropertyChange(event);//当吸附宿主对象属性发生变化时回调该函数，可重载做后续处理
        // textNode.isHostOn(node);//判断该图元是否吸附到指定图元对象上
        // textNode.isLoopedHostOn(node);//判断是否与指定图元形成环状吸附，如A吸附B，B吸附C，C吸附回A，则A，B和C图元相互环状吸附

        // var nh = node.getHeight();
        // var th = textNode.getHeight();
        // textNode.setPosition({x: node.getPosition().x, y: node.getPosition().y + nh / 2 + th / 2 - 6});

        return node;
    }

    //ht.Edge类型用于连接起始和目标两个Node节点，两个节点间可以有多条Edge存在，也允许起始和目标为同一节点。
    function createEdge(source, target, data, color, group) {
        var edge = new ht.Edge(source, target);
        edge.setStyle('edge.gap', 21);
        if (color) edge.setStyle('edge.color', color);
        if (group) edge.setStyle('edge.group', group);
        gv.dm().add(edge);

        var percent = data ? data.percent ? parseFloat(data.percent) : 0 : 0;//占总的百分比
        var color = ["#0033ff", "#0166ff", "#0099ff", "#00ccff", "#00ffff"];
        var index = 4;
        if (percent <= 20) {
            index = 4;
        } else if (percent <= 40 && percent > 20) {
            index = 3;
        } else if (percent <= 60 && percent > 40) {
            index = 2;
        } else if (percent <= 80 && percent > 60) {
            index = 1;
        } else if (percent <= 100 && percent > 80) {//百分数
            index = 0;
        }

        edge.s({//自定义新连线类型
            '2d.selectable': false,
            'edge.width': 5,
            'edge.color': '#2C3E50',
            'edge.type': 'h.v',//字符串类型的连线类型 boundary ripple h.v v.h ortho flex extend.east
            'edge.corner.radius': 10,//控制拐角曲线弧度
            // 'edge.ripple.size':1,
            // 'edge.ripple.length':80,
            'edge.center': true,//默认值为false代表从矩形边缘开始走向，true则起始和结束于中心
            'edge.flow': true,
            'edge.flow.step': 1,// 控制流动的步进，默认为3
            'edge.flow.reverse': true,//值为true或flase，控制反向流动，默认值为false
            'edge.flow.autoreverse': true, //值为true或flase，正向流动和反向流动是否交替进行，默认值为false
            'edge.flow.count': 1,// 控制流动组的个数，默认为1
            'edge.flow.element.count': 10, //每个流动组中的元素的个数，默认为10
            // 'edge.flow.element.background':'red', //流动组中元素的背景颜色，默认为rgba(255, 255, 114, 0.4)
            'edge.flow.element.max': 7, //流动组中最大元素的尺寸，默认为7
            'edge.flow.element.min': 2, //流动组中最小元素的尺寸，默认为0
            'edge.flow.element.space': 4, //流动组中元素的间隔，默认为3.5
            'edge.flow.element.autorotate': true, //值为true或false，控制流动组中的元素是否与路径的方向保持一致，默认为false
            // 'edge.flow.element.image':, //字符串类型，指定流动组中元素的图片，图片需要提前通过ht.Default.setImage注册
            // 'edge.flow.element.shadow.visible':true, //值为true或false，控制流动组中的元素是否显示渐变阴影，默认为true
            // 'edge.flow.element.shadow.begincolor':'green', //字符串类型，表示流动组中的元素的渐变阴影的中心颜色，默认为rgba(255, 255, 0, 0.3)
            // 'edge.flow.element.shadow.endcolor':'blue', //字符串类型，表示流动组中的元素的渐变阴影的边缘颜色，默认为rgba(255, 255, 0, 0)
            // 'edge.flow.element.shadow.max':22, //表示流动组中的最大元素的渐变阴影的尺寸，默认为22
            // 'edge.flow.element.shadow.min':5, //表示流动组中的最小元素的渐变阴影的尺寸，默认为4

            'edge.dash': true,
            'edge.dash.width': 5,
            'edge.dash.color': color[index],
            // 'edge.dash.pattern': [10],
            'edge.dash.flow': true,
            'edge.dash.flow.step': 3,// ht.Edge虚线流动的步进，默认值为3
            // 'edge.dash.flow.reverse':false,//控制ht.Edge的反向流动，默认值为false
        });

        // edge.getSource();//和setScource(node)获取和设置起始节点
        // edge.getTarget();//和setTarget(node)获取和设置目标节点
        // edge.isLooped();//判断连线的起始和目标是否为同一节点
        // edge.getSourceAgent();//获取图形上连接的起始节点
        // edge.getTargetAgent();//获取图形上连接的目标节点
        // edge.getEdgeGroup();//获取ht.EdgeGroup类型对象，起始和目标节点间有多条连线时才有值
        // edge.toggle();//实现对当前起始和目标节点之间多条连线的展开合并的切换，影响edge.expanded的style属性
        // edge.isEdgeGroupHidden();//判断当前连线是否在连线组中被隐藏
        // edge.getEdgeGroupSize();//返回当前连线所在连线组的连线数
        // edge.getEdgeGroupIndex();//返回当前连线所在连线组的索引
        // edge.isEdgeGroupAgent();//判断当前连线是否为所在连线组的代理
        return edge;
    }

    function initModel() {
        var obj = {
            'nodeName':chartData.nodeName,
            'icon':chartData.iconPath,
            'percent':chartData.percent,
            'value':chartData.val,
            'haveChildren':chartData.children
        };
        var root = createNode(obj,null,chartData.children);
        recursive(chartData.children, root);//递归
        // chartData[0].childs最后一级有childs:[],加上图形"+"，没有不加，如果加了“+”,双击节点时发下级请求，如果没有“+”，不发送请求
    }

    //递归生成ht.Node
    function recursive(arrs, parent) {
        for (var i = 0; i < arrs.length; i++) {
            var obj = {
                'nodeName':arrs[i].nodeName,
                'icon':arrs[i].iconPath,
                'percent':arrs[i].percent,
                'value':arrs[i].val,
                'haveChildren':arrs[i].children
            };

            if (arrs[i].children && arrs[i].children.length>0) {
                var iNode = createNode(obj, parent, arrs[i].children);
                createEdge(parent, iNode, obj);
                recursive(arrs[i].children, iNode);
            } else {
                var iNode = createNode(obj, parent);
                createEdge(parent, iNode, obj);
            }
        }
    }

    function formateDate(date) {
        var y = date.getFullYear();
        var M = date.getMonth() + 1;
        M = M < 10 ? ('0' + M) : M;
        var d = date.getDate();
        d = d < 10 ? ('0' + d) : d;
        if (timeType == 'years') {
            return y;
        } else if (timeType == 'months') {
            return y + '-' + M;
        } else {
            return y + '-' + M + '-' + d;
        }
    }

    //时间类型和时间展示
    function initTime(){
        var time1 = formateDate(new Date());
        var type = 'date';
        if (timeType == 'years') {
            type = 'year';
        } else if (timeType == 'months') {
            type = 'month';
        } else {
            type = 'date';
        }
        $("#timeDiv").html('<input type="text" class="layui-input" id="timeInput" value="' + time1 + '">');

        laydate.render({
            elem: '#timeInput'
            , type: type
            , lang: 'cn'
            , value: new Date()
        });
    }

    function funcsAuthority() {
        initTime();
        element.render('breadcrumb');

        getEnergyType();
    }

    var indexLoading;
    function load(){
        indexLoading =layer.load(1, {shade: [0.3, '#fff']});
    }
    function disLoad(){
        layer.close(indexLoading);
    }
});