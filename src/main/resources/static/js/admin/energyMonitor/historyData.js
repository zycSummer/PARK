/*
 * @Author: jpp
 * @Date: 2019-10-22 17:07:34
 * @Last Modified by: jpp
 * @Last Modified time: 2019-11-05 09:37:35
 */

layui.use(['request','form', 'element', 'layer', 'jquery','tree','laydate'], function () {
    var form = layui.form,
        layer = layui.layer,
        tree = layui.tree,
        laydate = layui.laydate,
        $ = layui.jquery,
        element = layui.element;

    var siteObj = JSON.parse(sessionStorage.getItem('parkId'));
    var energyTypeId = '';//能源种类
    var timeType = 'day';//年、月、日
    var interval = 5;//时间间隔
    var dataType = 'first';//数值类型
    var paramsSelect;//参数 多选
    var chart1, chartData1 = [];

    funcsAuthority();

    //点击能源种类
    $(document).on("click", ".h_item", function () {
        $(this).siblings().removeClass('h_active');
        $(this).addClass('h_active');

        energyTypeId = $(this).attr('data-id');
        getSelectParams();
        getTree();

        return false;
    });

    //点击 年、月、日
    $(".timeType a").on('click',function(){
        $(this).siblings().removeClass('layui-this');
        $(this).addClass('layui-this');

        var idx = $(this).attr('data-id');
        if (idx !== timeType) {
            timeType = idx;
        }
        createInterval();
        return false;
    });

    //点击 时间间隔
    $(document).on("click",".inter",function(){
        $(this).siblings().removeClass('layui-this');
        $(this).addClass('layui-this');

        interval = parseInt($(this).attr('data-id'));
        return false;
    });

    //点击 时刻值、平均值、最大值
    $(".valType a").on('click',function(){
        $(this).siblings().removeClass('layui-this');
        $(this).addClass('layui-this');

        var idx = $(this).attr('data-id');
        if (idx !== dataType) {
            dataType = idx;
        }
        return false;
    });

    //查询
    $(document).on("click",".query",function(){
        var idStr = $('.layui-show .ztree').attr('id');
        if(!idStr){
            layer.msg('没有树结构!');
            return false;
        }
        var tree1 = $.fn.zTree.getZTreeObj(idStr);
        if(!tree1){
            layer.msg('树结构未选中!');
            return false;
        }
        var treeIds = tree1.getCheckedNodes(true);
        var para = paramsSelect.getValue();
        if(treeIds.length > 0){
            if(para.length > 0) {
                getChartData();
            }else{
                layer.msg('没有参数');
            }
        }else{
            layer.msg('树结构未选中');
        }
        return false;
    });
    /*----------------------------------函数------------------------------------*/
    function funcsAuthority(){
        var date = new Date();
        $("#timeInput").val(formateDate(date.valueOf()));
        laydate.render({
            elem: '#timeInput'
            ,type: 'date'
            ,lang: 'cn'
            ,value: new Date()
            ,btns: ['now', 'confirm']
        });
        form.render('select');

        $(".timeIntervel").html('<a href="" class="inter layui-this" data-id="5">5分钟</a>' +
            '<a href="" class="inter" data-id="15">15分钟</a>' +
            '<a href="" class="inter" data-id="60">1小时</a>');
        element.render('breadcrumb');

        getMenu();
    }

    function createInterval(){
        $("#timeDiv").html('<input type="text" class="layui-input" id="timeInput">');
        var typeTime = 'date';
        if(timeType == 'day'){
            typeTime = 'date';
            interval = 5;

            $(".timeIntervel").html('<a href="" class="inter layui-this" data-id="5">5分钟</a>' +
                '<a href="" class="inter" data-id="15">15分钟</a>' +
                '<a href="" class="inter" data-id="60">1小时</a>');
        }else if(timeType == 'month'){
            typeTime = 'month';
            interval = 15;

            $(".timeIntervel").html('<a href="" class="inter layui-this" data-id="15">15分钟</a>' +
                '<a href="" class="inter" data-id="30">30分钟</a>' +
                '<a href="" class="inter" data-id="60">1小时</a>');
        }else{
            typeTime = 'year';
            interval = 60;

            $(".timeIntervel").html('<a href="" class="inter layui-this" data-id="60">1小时</a>' +
                '<a href="" class="inter" data-id="180">3小时</a>' +
                '<a href="" class="inter" data-id="3600">6小时</a>');
        }
        laydate.render({
            elem: '#timeInput'
            ,type: typeTime
            ,lang: 'cn'
            ,value: new Date()
            ,btns: ['now', 'confirm']
        });
        element.render('breadcrumb');
    }

    //左侧能源种类获取
    function getMenu() {
        if(siteObj){
            load();
            request.service({
                method: 'get',
                url: '/common/queryHistoryLeftData'
            }).then(function (res) {
                disLoad();
                var str = '';
                $.each(res.data,function(i0,v0){
                    if(i0 == 0){
                        energyTypeId = v0['energyTypeId'];
                        str += '<li data-id="'+v0['energyTypeId']+'" class="h_item h_active">';
                    }else{
                        str += '<li data-id="'+v0['energyTypeId']+'" class="h_item">';
                    }
                    str += '<a  href="javascript:void(0)">'+v0['energyTypeName']+'</a>';
                    str += '</li>';
                });

                $(".history_nav").html(str);
                if(res.data.length>0){
                    getTree();
                    getSelectParams();
                }else{
                    $("#tree,#params").html('');
                }
            }).catch(function(err){
                disLoad();
                console.log(err)
            });
        }
    }

    function getTree(){
        if(siteObj && energyTypeId){
            load();
            request.service({
                method: 'post',
                url: '/common/queryHistoryLeftTree',
                data: {
                    'objType': siteObj.type,
                    'objId': siteObj.id,
                    'energyTypeId':energyTypeId
                }
            }).then(function (res) {
                disLoad();
                $(".history_tree .layui-collapse").html('');
                $.each(res.data,function(ii,vv){
                    var str = '';
                    if(ii == 0){
                        str += '<div class="layui-colla-item">' +
                            '        <h2 class="layui-colla-title">'+vv.orgTreeName+'</h2>' +
                            '        <div class="layui-colla-content layui-show">' +
                            '              <div class="ztree" id="tree'+ii+'"></div>' +
                            '        </div>' +
                            '   </div>';
                    }else{
                        str += '<div class="layui-colla-item">' +
                            '        <h2 class="layui-colla-title">'+vv.orgTreeName+'</h2>' +
                            '        <div class="layui-colla-content">' +
                            '              <div class="ztree" id="tree'+ii+'"></div>' +
                            '        </div>' +
                            '   </div>';
                    }

                    $(".history_tree .layui-collapse").append(str);
                    element.render('collapse');//重新对折叠面板进行渲染。
                    treeCreate(ii,vv.children,res.data.length);
                });

                if(paramsSelect) getChartData();

                element.on('collapse(tree)', function(data){
                    if(data.show == false){
                        var idStr = data.content.find('.ztree').attr('id');
                        var tree1 = $.fn.zTree.getZTreeObj(idStr);
                        if(tree1) tree1.checkAllNodes(false);
                    }
                });
            }).catch(function(err){
                disLoad();
                console.log(err)
            });
        }
    }

    function treeCreate(index,zNodes,n){
        var setting = {
            view: {
                addDiyDom:null,//用于在节点上固定显示用户自定义控件
                autoCancelSelected: false,
                dblClickExpand: false,//双击节点时，是否自动展开父节点的标识
                showLine: true,//设置 zTree 是否显示节点之间的连线。
                selectedMulti: true//设置是否允许同时选中多个节点。
            },
            check: {
                enable: true,
                chkStyle: "checkbox",
                chkboxType: { "Y": "", "N": "" }
            },
            data: {
                keep:{
                    leaf:false,//zTree 的节点叶子节点属性锁，是否始终保持 isParent = false
                    parent:false//zTree 的节点父节点属性锁，是否始终保持 isParent = true
                },
                key:{
                    children:"children",//节点数据中保存子节点数据的属性名称。
                    name:"nodeName",//节点数据保存节点名称的属性名称。
                    title:"nodeId"//节点数据保存节点提示信息的属性名称
                },
                simpleData: {
                    enable: true,//是否采用简单数据模式 (Array)
                    idKey: "nodeId",//节点数据中保存唯一标识的属性名称。
                    pIdKey: "parentId",//节点数据中保存其父节点唯一标识的属性名称
                    rootPId: ""//用于修正根节点父节点数据，即 pIdKey 指定的属性值。
                }
            },
            callback: {
                onClick:function(event,treeId,treeNode){
                    var tree1 = $.fn.zTree.getZTreeObj(treeId);
                    tree1.checkNode(treeNode);
                }
            }
        };
        var idStr = '#tree'+index;
        $.fn.zTree.init($(idStr), setting, zNodes);
        var tree1 = $.fn.zTree.getZTreeObj('tree'+index);
        tree1.expandAll(true);
        if(n == 1){
            $('.ztree').css('height',$(".history_tree").height()-66);
        }else{
            $('.ztree').css('height',$(".history_tree").height()-55*n);
        }
    }

    function getSelectParams() {
        if(siteObj && energyTypeId){
            load();
            request.service({
                method: 'get',
                url: '/common/queryParameter/'+energyTypeId
            }).then(function (res) {
                disLoad();
                paramsSelect = xmSelect.render({
                    el: '#params',
                    prop: {
                        name: 'energyParaName',
                        value: 'energyParaId'
                    },
                    height: '320px',
                    model: {
                        label: {
                            type: 'block',
                            block: {
                                //最大显示数量, 0:不限制
                                showCount: 3,
                                //是否显示删除图标
                                showIcon: true
                            }
                        }
                    },
                    data: res.data
                });

                var tree1 = $.fn.zTree.getZTreeObj('tree0');
                if(tree1) getChartData();
            }).catch(function(err){
                disLoad();
                console.log(err)
            });
        }
    }

    function formateDate(timestamp) {
        var date = new Date(timestamp);
        var y = date.getFullYear();
        var M = date.getMonth() + 1;
        M = M < 10 ? ('0' + M) : M;
        var d = date.getDate();
        d = d < 10 ? ('0' + d) : d;
        if(timeType == 'year') {
            return y;
        }else if(timeType == 'month') {
            return y+'-'+M;
        }else{
            return y+'-'+M+'-'+d;
        }
    }

    function formateDate2(timestamp) {
        var date = new Date(timestamp);
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
        if(timeType == 'year') {
            return y+'-'+M+'-'+d+' '+h+':'+m;
        }else if(timeType == 'month') {
            return M+'-'+d+' '+h+':'+m;
        }else{
            return h+':'+m;
        }
    }

    function getChartData(){
        var idStr = $('.layui-show .ztree').attr('id');
        var tree1 = $.fn.zTree.getZTreeObj(idStr);
        var treeIds = tree1.getCheckedNodes(true);

        var para = paramsSelect.getValue();
        var paraArr = [];
        $.each(para,function(i2,v2){
            paraArr.push(v2.energyParaId);
        });

        var beginTime = $("#timeInput").val();
        var data = {
            "energyTypeId":energyTypeId,// 能源种类标识
            "nodeInfo":treeIds,// 数据源
            "energyParaIds": paraArr,// 能源参数标识
            "time":beginTime,
            "timeType":timeType,
            "interval":interval,// 时间间隔分钟
            "type":dataType //数值类型
        };

        load();
        request.service({
            method: 'post',
            url: '/common/queryMonitorPageInfoData',
            data: data
        }).then(function (res) {
            disLoad();
            chartData1 = [];
            if (res.data.length>0) {
                chartData1 = res.data;
                init1();
            }else{
                $("#chart1").html('');
                return layer.msg('没有数据');
            }
        }).catch(function(err){
            disLoad();
            console.log(err);
            $("#chart1").html('');
        });
    }

    //直角坐标系内绘图网格
    function makeGrid(top, height, opt) {
        return echarts.util.merge({
            left: 70,
            right: 60,
            top: top,
            height: height
        }, opt || {}, true);
    }

    //X轴生成器
    function makeXAxis(gridIndex,xAxisData, opt) {
        //避免X轴数据显示过于频繁
        var axisLabelFlag = false;
        var name = '';
        //  if (gridIndex % 2 == 0) {
        //      axisLabelFlag = true;
        //  }
        if(gridIndex == (chartData1.length-1)) {
            axisLabelFlag = true;
            name = '时间';
        }

        var boundaryGap = dataType == 'diff'?true:false;

        return echarts.util.merge({
            type: 'category',
            gridIndex: gridIndex,
            //统一时间轴数据
            name : name,
            data: xAxisData,
            axisLabel: {
                show: axisLabelFlag
                /* formatter: function(value) {
                        return echarts.format.formatTime('MM-dd', value);
                }*/
            },
            boundaryGap : boundaryGap,
            axisTick: {show:true},
            axisLine: {lineStyle:{color:'#ddd',width:1}},
            splitLine:{//坐标轴在 grid 区域中的分隔线
                show :false
            }
        }, opt || {}, true);
    }

    //Y轴生成器
    function makeYAxis(gridIndex, opt) {
        return echarts.util.merge({
            type: 'value',
            nameLocation: 'middle',
            nameGap: '40',
            gridIndex: gridIndex,
            nameTextStyle: {
                color: '#333'
            },
            scale:true,
            axisTick: {
                show: false
            },
            axisLabel: {
                show: true
            }
        }, opt || {}, true);
    }

    //数据生成器
    function makeGridData(xAxisIndex, yAxisIndex, chartType, chartName, chartData, opt) {
        return echarts.util.merge({
            type: chartType,
            name: chartName,
            xAxisIndex: xAxisIndex,
            yAxisIndex: yAxisIndex,
            data: chartData
        }, opt || {}, true);
    }

    function init1(){
        //图表定位
        var chartGridTop = 40;
        var chartGridHeight = 150;

        $(".chart-content").html('<div id="chart1"></div>');

        var h = chartGridTop+(chartGridHeight + 25)*(chartData1.length);
        if($("#chart1").height() < h){
            $("#chart1").height(h+40);
        }

        if (chart1 != null && chart1 != "" && chart1 != undefined) {
            chart1.dispose();
        }

        // 时刻值、平均值、最大值、最小值 用折线图展示，差值用柱状图展示。
        // var type = dataType == 'diff'?'bar':'line';
        var yNameArr = [];
        //一个参数一张图（折线图）进行展示。如果勾选的多个节点有同一参数，则这张图上展示多条曲线。
        var series1 = [],yAxis = [],xAxis = [],nameArr = [],gridArr=[],dataZoomArr = [];
        var colors = ['#43CD80','#778899','#FF6A6A','#FFC125','#63B8FF','#BA55D3','#836FFF'];
        var idx = 0;
        $.each(chartData1,function(i,val){
            dataZoomArr.push(i);
            gridArr.push(makeGrid(chartGridTop+(chartGridHeight + 25)*i, chartGridHeight));
            var yName = val.energyParaName;
            var unit = val.unit == null?'':'('+val.unit+')';

            yAxis.push(makeYAxis(i, {
                name: yName+unit,
                splitNumber: 3, //调整间隔
                //  splitLine: {show: false},//去除网格线
            }));
            yNameArr.push({
                name:yName,
                unit:unit
            });

            var xAxisData = [];
            $.each(val.nodeVOs,function(ii,vv){
                if(xAxisData.length == 0){
                    $.each(vv.times,function(m,n){
                        xAxisData.push(formateDate2(n));
                    });
                }

                var lineName = vv.nodeName;
                nameArr.push(lineName);

                var lineData = [];
                var showAllSymbol = 'auto',index = 0,next = 0;
                $.each(vv.values,function(mm,nn){
                    if(nn){
                        lineData.push(parseFloat(nn.toFixed(2)));
                    }else{
                        lineData.push(nn);
                    }

                    //判断是否有独立的点
                    if(nn == null){
                        index = mm+1;
                    }
                    if(mm == index && nn != null){
                        next = mm+1;
                    }
                    if(mm == next && nn == null){
                        showAllSymbol = 'true';
                    }
                });

                if(colors.length <= idx){
                    var r = Math.floor(Math.random()*256);
                    var g = Math.floor(Math.random()*256);
                    var b = Math.floor(Math.random()*256);
                    var color = "rgb("+r+','+g+','+b+")";
                    colors.push(color);
                }

                var obj;
                if(dataType == 'diff'){
                    obj = makeGridData(i, i, 'bar', lineName, lineData, {
                        // barGap: '135%',
                        // barCategoryGap:'20%',
                        // barWidth: 10,
                        // z: 10,
                        itemStyle:{
                            normal:{
                                color:colors[idx]
                            }
                        },
                        symbolSize: 8,
                        showSymbol:false,//是否显示 symbol
                        connectNulls:false,//是否连接空数据
                        showAllSymbol: showAllSymbol
                    })
                }else{
                    obj = makeGridData(i, i, 'line', lineName, lineData, {
                        smooth: true,
                        color:colors[idx],
                        symbolSize: 8,
                        showSymbol:false,//是否显示 symbol
                        connectNulls:false,//是否连接空数据
                        showAllSymbol: showAllSymbol
                    })
                }
                idx += 1;
                series1.push(obj);

                // series1.push({
                //     name:lineName,
                //     type:type,
                //     // symbol:'circle',
                //     symbolSize: 8,
                //     showSymbol:false,//是否显示 symbol
                //     connectNulls:false,//是否连接空数据
                //     showAllSymbol: showAllSymbol,//false:随主轴标签间隔隐藏策略,'auto'：默认，如果有足够空间则显示标志图形，否则随主轴标签间隔隐藏策略
                //     data:lineData
                // });
            });
            xAxis.push(makeXAxis(i,xAxisData));
        });

        var axisPointer = dataType == 'diff'?'shadow':'line';

        var option = {
            animation: false,
            //标题组件，包含主标题和副标题
            title: {
                x: 'center',
                text: ''
            },
            legend: {
                data: nameArr,
                type:'plain',
                left: 30,
                top: 10,
                textStyle:{
                    color:'#ccc'
                }
            },
            toolbox: {
                show: true,
                showTitle: false,
                top:10,
                right:0,
                feature: {
                    dataZoom: {
                        yAxisIndex: 'none'
                    },
                    dataView: {readOnly: false},
                    magicType: {type: ['line', 'bar']},
                    restore: {},
                    saveAsImage: {},
                    brush:{
                        type:'rect',
                        icon:{}
                    }
                }
            },
            tooltip: {
                //移动端展示方式
                trigger: 'axis',
                transitionDuration: 0,
                confine: true,
                borderRadius: 4,
                borderWidth: 1,
                borderColor: '#333',
                backgroundColor: 'rgba(255,255,255,0.9)',
                textStyle: {
                    fontSize: 12,
                    color: '#333'

                },
                formatter:function(params){  //数据单位格式化
                    var relVal = params[0].name;  //x轴名称
                    relVal +="<div style='width:250px'>";
                    for (var i = 0, l = params.length; i < l; i++) {
                        if(params[i].value != null){
                            relVal +=  "<span  style='display:inline-block;margin-right:5px;" +
                                "border-radius:10px;width:10px;height:10px;background-color:"+
                                params[i].color+";'>" +'<span  style="display:block;padding-left:15px;' +
                                'margin-top:-4px">'+ params[i].seriesName +'['+yNameArr[params[i].axisIndex].name+']'+ ' : '
                                + params[i].value+yNameArr[params[i].axisIndex].unit+'</span>'+
                                "</span>"+'<br>';
                        }
                    }
                    relVal +="</div>";
                    return relVal;
                },
                axisPointer: {
                    type: axisPointer
                },
                // position: function (pos, params, dom, rect, size) {
                //     var obj = {top: '1%'};
                //     if(size){
                //         if(pos[0] < size.viewSize[0] / 2){
                //             obj['left'] = pos[0];
                //         }else{
                //             obj['right'] = size.viewSize[0] - pos[0];
                //         }
                //     }
                //     return obj;
                // }
            },

            //坐标轴指示器（axisPointer）的全局公用设置
            axisPointer: {
                type: 'shadow',
                link: {
                    xAxisIndex: 'all'
                }
            },
            //直角坐标系内绘图网格
            grid: gridArr,
            xAxis: xAxis,
            yAxis: yAxis,
            //dataZoom 组件 用于区域缩放
            dataZoom: [{
                type: 'slider',
                xAxisIndex: dataZoomArr,
                realtime: true,
                //移动端展示方式
                handleSize: '140%'
            }],
            //每个系列通过 type 决定自己的图表类型
            series: series1

        };
        chart1 = echarts.init(document.getElementById('chart1')).setOption(option, true);
    }

    var indexLoading;
    function load(){
        indexLoading =layer.load(1, {shade: [0.3, '#fff']});
    }
    function disLoad(){
        layer.close(indexLoading);
    }
});