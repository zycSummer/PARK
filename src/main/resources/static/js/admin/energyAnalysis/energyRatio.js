/*
 * @Author: jpp
 * @Date: 2019-10-29 17:07:34
 * @Last Modified by: jpp
 * @Last Modified time: 2019-11-07 15:37:35
 */
layui.use(['request','form', 'element', 'layer', 'jquery','tree','laydate','table'], function () {
    var form = layui.form,
        layer = layui.layer,
        tree = layui.tree,
        table = layui.table,
        laydate = layui.laydate,
        $ = layui.jquery,
        element = layui.element;

    var siteObj = JSON.parse(sessionStorage.getItem('parkId'));
    var energyTypeId = 'electricity';//能源种类
    var timeType = 'day';//年、月、日
    var interval = 5;//时间间隔
    var dataType = 'first';//数值类型
    var paramType = '';//参数类别 单选
    var paramItem = [];//参数选项 多选
    var unit = '';//单位
    var chart1, chartData1 = [], series1 = [],xAxis = [],nameArr = [];//折线图数据
    var tableData1 = [];//底部表格数据
    var cols = [],tableData2 = [],tableData2Html = '';

    funcsAuthority();

    form.on('submit(*)', function(data){
        return false; //阻止表单跳转。如果需要表单跳转，去掉这段即可。
    });

    //点击能源种类
    $(document).on("click", ".e_item", function () {
        $(this).siblings().removeClass('e_active');
        $(this).addClass('e_active');

        energyTypeId = $(this).attr('data-id');
        initParamType();//参数类别
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
        initInterval();
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

    //点击 参数类别
    $(document).on('click',".paramType a",function(){
        $(this).siblings().removeClass('layui-this');
        $(this).addClass('layui-this');

        var idx = $(this).attr('data-id');
        if (idx !== paramType) {
            paramType = idx;
        }
        initParamItem();
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
        var treeId = tree1.getSelectedNodes();

        if(treeId){
            showHigh1();
        }else{
            layer.msg('树结构未选中');
        }
        return false;
    });

    $('.energy_img').on('click', function () {
        $(this).siblings().removeClass('imgs');
        $(this).addClass('imgs');
        var idx = $(this).index();
        if (idx == 0) {
            $(this).attr('src', '/public/images/common/contrast.png');
            $(this).next('.energy_img').attr('src', '/public/images/common/table_dark.png');

            $('.chartMain').removeClass('layui-hide');
            $('.table2Box').addClass('layui-hide');

            init1();
            showTable1();
        } else {
            $(this).attr('src', '/public/images/common/table.png');
            $(this).prev('.energy_img').attr('src', '/public/images/common/contrast_dark.png');

            $('.chartMain').addClass('layui-hide');
            $('.table2Box').removeClass('layui-hide');

            showTable2();
        }
        return false;
    });

    $('#energy_export').on('click', function () {
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
        var treeId = tree1.getSelectedNodes();
        if(treeId.length > 0){
            var unit = '';
            var paramItemArr = $('input[type="checkbox"]:checked');
            if(paramItemArr.length > 0){
                $.each(paramItemArr,function(){
                    var val = $(this).val();

                    if(val == 'Ep_imp' || val == 'Ep_exp'){
                        if(unit.indexOf('kWh') == -1) unit += '有功kWh ';
                    }else if(val == 'Eq_imp' || val == 'Eq_exp'){
                        if(unit.indexOf('kVarh') == -1) unit += '无功kVarh';
                    }
                });
            }
            if(unit == ''){
                unit = $('.paramType .layui-this').attr('data-unit');
            }

            var name = siteObj.type == 'PARK'?siteObj.name:siteObj.title;
            var title = name+' - '+treeId[0].nodeName+' - 能耗时比 - '+$('.paramType .layui-this').text()+'('+unit+')';

            if($('.energy_img:nth-child(1)').hasClass('imgs')){
                exportReportTemplet(title,name);
            }else{
                // 1. 数组头部新增表头
                var tableHeader = {};
                $.each(cols,function(j,k){
                    tableHeader[k.field] = k.title;
                });
                var data = [];
                $.each(tableData2,function(jj,kk){
                    data.push(kk);
                });
                data.unshift(tableHeader);
                layui.excel.exportExcel({
                    sheet1:data
                }, name+'.xlsx', 'xlsx');
            }
        }else{
            layer.msg('树结构未选中');
        }
    });
    /*----------------------------------函数------------------------------------*/
    function exportReportTemplet(title,name) {
        var element = $(".chartMain");    // 这个dom元素是要导出pdf的div容器
        $(".chartMain").append('<h2 style="display: none">'+title+'</h2>');
        var w = element.width();    // 获得该容器的宽
        var h = element.height();    // 获得该容器的高
        var offsetTop = element.offset().top;    // 获得该容器到文档顶部的距离
        var offsetLeft = element.offset().left;    // 获得该容器到文档最左的距离
        var canvas = document.createElement("canvas");
        canvas.width = w * 2;    // 将画布宽&&高放大两倍
        canvas.height = h * 2.1;
        var context = canvas.getContext("2d");
        var scale = 2;
        context.scale(2, 2.1);
        context.translate(-offsetLeft, -offsetTop);

        var opts = {
            scale: scale,
            canvas: canvas,
            width: w,
            height: h,
            useCORS: true,
            background: '#FFF'
        };

        html2canvas(element, opts).then(function (canvas) {
            allowTaint: false;
            taintTest: false;
            var contentWidth = canvas.width;
            var contentHeight = canvas.height;
            //一页pdf显示html页面生成的canvas高度;
            var pageHeight = contentWidth / 592.28 * 841.89;
            //未生成pdf的html页面高度
            var leftHeight = contentHeight;
            //页面偏移
            var position = 0;
            //a4纸的尺寸[595.28,841.89]，html页面生成的canvas在pdf中图片的宽高
            var imgWidth = 595.28-20;
            var imgHeight = 592.28 / contentWidth * contentHeight-50;

            var pageData = canvas.toDataURL('image/jpeg', 1.0);

            var pdf = new jsPDF('', 'pt', 'a4');
            pdf.setFont('simhei').setFontSize(12).splitTextToSize(title, 2.5);
            pdf.setFontType("normal");
            pdf.text(imgWidth/2, 20, title,'center');

            //有两个高度需要区分，一个是html页面的实际高度，和生成pdf的页面高度(841.89)
            //当内容未超过pdf一页显示的范围，无需分页
            if (leftHeight < pageHeight) {
                pdf.addImage(pageData, 'JPEG', 10, 40, imgWidth, imgHeight);
            } else {    // 分页
                while (leftHeight > 0) {
                    pdf.addImage(pageData, 'JPEG', 10, position, imgWidth, imgHeight);
                    leftHeight -= pageHeight;
                    position -= 841.89;
                    //避免添加空白页
                    if (leftHeight > 0) {
                        pdf.addPage();
                    }
                }
            }
            pdf.save(name+'.pdf');
        })

    }

    function funcsAuthority(){
        var date = new Date();
        $("#timeInput").val(formateDate(date.valueOf() - 24*60*60*1000));
        $("#timeInput2").val(formateDate(date.valueOf()));
        laydate.render({
            elem: '#timeInput'
            ,type: 'date'
            ,lang: 'cn'
            ,value: new Date(date.valueOf() - 24*60*60*1000)
            ,btns: ['now', 'confirm']
        });
        laydate.render({
            elem: '#timeInput2'
            ,type: 'date'
            ,lang: 'cn'
            ,value: new Date()
            ,btns: ['now', 'confirm']
        });

        $(".timeIntervel").html('<a href="" class="inter layui-this" data-id="5">5分钟</a>' +
            '<a href="" class="inter" data-id="15">15分钟</a>' +
            '<a href="" class="inter" data-id="60">1小时</a>');
        element.render('breadcrumb');

        getMenu();
    }

    function initInterval(){
        $("#timeDiv").html('<input type="text" class="layui-input" id="timeInput"> VS' +
            '   <input type="text" class="layui-input" id="timeInput2" value="">');
        var date = new Date(),timeDefault;
        var typeTime = 'date';
        if(timeType == 'day'){
            typeTime = 'date';
            interval = 5;
            timeDefault = new Date(date.valueOf() - 24*60*60*1000);

            $(".timeIntervel").html('<a href="" class="inter layui-this" data-id="5">5分钟</a>' +
                '<a href="" class="inter" data-id="15">15分钟</a>' +
                '<a href="" class="inter" data-id="60">1小时</a>');
        }else if(timeType == 'month'){
            typeTime = 'month';
            interval = 15;

            var M = date.getMonth()+1;
            var Y = date.getFullYear();
            if((M-1) <= 0){
                timeDefault = (Y-1) + '-12';
            }else{
                M = (M - 1)<10?'0'+(M - 1):(M - 1);
                timeDefault = Y + '-' + M;
            }

            $(".timeIntervel").html('<a href="" class="inter layui-this" data-id="15">15分钟</a>' +
                '<a href="" class="inter" data-id="30">30分钟</a>' +
                '<a href="" class="inter" data-id="60">1小时</a>');
        }else{
            typeTime = 'year';
            interval = 60;
            timeDefault = date.getFullYear() - 1;

            $(".timeIntervel").html('<a href="" class="inter layui-this" data-id="60">1小时</a>' +
                '<a href="" class="inter" data-id="180">3小时</a>' +
                '<a href="" class="inter" data-id="3600">6小时</a>');
        }
        laydate.render({
            elem: '#timeInput'
            ,type: typeTime
            ,lang: 'cn'
            ,value: timeDefault
            ,btns: ['now', 'confirm']
        });
        laydate.render({
            elem: '#timeInput2'
            ,type: typeTime
            ,lang: 'cn'
            ,value: new Date()
            ,btns: ['now', 'confirm']
        });
        element.render('breadcrumb');
    }
    //参数类别
    function initParamType(){
        switch(energyTypeId){
            case 'electricity'://当最左侧能源种类选择的是电(electricity)时
                paramType = 'kW';
                $(".paramType").html('<a href="javascript:void(0)" data-id="kW" data-unit="kW" class="breadcrumb layui-this">有功功率</a>' +
                    '<a href="javascript:void(0)" data-id="V" data-unit="V" class="breadcrumb">电压</a>' +
                    '<a href="javascript:void(0)" data-id="A" data-unit="A" class="breadcrumb">电流</a>' +
                    '<a href="javascript:void(0)" data-id="Ep" data-unit="" class="breadcrumb">电度</a>');
                break;
            case 'water':
                paramType = 'Flowrate';
                $(".paramType").html('<a href="javascript:void(0)" data-id="Flowrate" data-unit="t/h" class="breadcrumb layui-this">瞬时流量</a>' +
                    '<a href="javascript:void(0)" data-id="Totalflow" data-unit="t" class="breadcrumb">累积流量</a>' +
                    '<a href="javascript:void(0)" data-id="Temp" data-unit="℃" class="breadcrumb">温度</a>');
                break;
            case 'steam':
                paramType = 'Flowrate';
                $(".paramType").html('<a href="javascript:void(0)" data-id="Flowrate" data-unit="t/h" class="breadcrumb layui-this">瞬时流量</a>' +
                    '<a href="javascript:void(0)" data-id="Totalflow" data-unit="t" class="breadcrumb">累积流量</a>' +
                    '<a href="javascript:void(0)" data-id="Temp" data-unit="℃" class="breadcrumb">温度</a>' +
                    '<a href="javascript:void(0)" data-id="Press" data-unit="MPa" class="breadcrumb">压力</a>');
                break;
            case 'oil':
            case 'coal':
                paramType = 'Flowrate';
                $(".paramType").html('<a href="javascript:void(0)" data-id="Flowrate" data-unit="t/h" class="breadcrumb layui-this">瞬时用量</a>' +
                    '<a href="javascript:void(0)" data-id="Totalflow" data-unit="t" class="breadcrumb">累积用量</a>');
                break;
            case 'std_coal':
                paramType = 'Usagerate';
                $(".paramType").html('<a href="javascript:void(0)" data-id="Usagerate" data-unit="tce/h" class="breadcrumb layui-this">瞬时用量</a>' +
                    '<a href="javascript:void(0)" data-id="Usage" data-unit="tce" class="breadcrumb">累积用量</a>');
                break;
        }
        initParamItem();
        element.render('breadcrumb');
    }
    //参数选项
    function initParamItem(){
        $(".parameterItem").show();
        if(energyTypeId == 'electricity'){
            switch(paramType) {
                case 'kW':
                    $(".paramItem").html('<a href="javascript:void(0)" class="breadcrumb"><input type="checkbox" name="paramItem[Pa]" value="Pa" title="A相" checked lay-skin="primary"></a>' +
                        '<a href="javascript:void(0)" class="breadcrumb"><input type="checkbox" name="paramItem[Pb]" value="Pb" title="B相" lay-skin="primary"></a>' +
                        '<a href="javascript:void(0)" class="breadcrumb"><input type="checkbox" name="paramItem[Pc]" value="Pc" title="C相" lay-skin="primary"></a>' +
                        '<a href="javascript:void(0)" class="breadcrumb"><input type="checkbox" name="paramItem[P]" value="P" title="总" lay-skin="primary"></a>');
                    break;
                case 'V':
                    $(".paramItem").html('<a href="javascript:void(0)" class="breadcrumb"><input type="checkbox" name="paramItem[Ua]" value="Ua" title="A相" checked lay-skin="primary"></a>' +
                        '<a href="javascript:void(0)" class="breadcrumb"><input type="checkbox" name="paramItem[Ub]" value="Ub" title="B相" lay-skin="primary"></a>' +
                        '<a href="javascript:void(0)" class="breadcrumb"><input type="checkbox" name="paramItem[Uc]" value="Uc" title="C相" lay-skin="primary"></a>' +
                        '<a href="javascript:void(0)" class="breadcrumb"><input type="checkbox" name="paramItem[Uab]" value="Uab" title="AB线" lay-skin="primary"></a>' +
                        '<a href="javascript:void(0)" class="breadcrumb"><input type="checkbox" name="paramItem[Ubc]" value="Ubc" title="BC线" lay-skin="primary"></a>' +
                        '<a href="javascript:void(0)" class="breadcrumb"><input type="checkbox" name="paramItem[Uca]" value="Uca" title="CA线" lay-skin="primary"></a>');
                    break;
                case 'A':
                    $(".paramItem").html('<a href="javascript:void(0)" class="breadcrumb"><input type="checkbox" name="paramItem[Ia]" value="Ia" title="A相" checked lay-skin="primary"></a>' +
                        '<a href="javascript:void(0)" class="breadcrumb"><input type="checkbox" name="paramItem[Ib]" value="Ib" title="B相" lay-skin="primary"></a>' +
                        '<a href="javascript:void(0)" class="breadcrumb"><input type="checkbox" name="paramItem[Ic]" value="Ic" title="C相" lay-skin="primary"></a>');
                    break;
                case 'Ep':
                    $(".paramItem").html('<a href="javascript:void(0)" class="breadcrumb"><input type="checkbox" name="paramItem[Ep_imp]" value="Ep_imp" title="正向有功" checked lay-skin="primary"></a>' +
                        '<a href="javascript:void(0)" class="breadcrumb"><input type="checkbox" name="paramItem[Ep_exp]" value="Ep_exp" title="反向有功" lay-skin="primary"></a>' +
                        '<a href="javascript:void(0)" class="breadcrumb"><input type="checkbox" name="paramItem[Eq_imp]" value="Eq_imp" title="正向无功" lay-skin="primary"></a>' +
                        '<a href="javascript:void(0)" class="breadcrumb"><input type="checkbox" name="paramItem[Eq_exp]" value="Eq_exp" title="反向无功" lay-skin="primary"></a>');
                    break;
            }
        }else if(energyTypeId == 'steam'){
            if(paramType == 'Flowrate'){
                $(".paramItem").html('<a href="javascript:void(0)" class="breadcrumb"><input type="checkbox" name="paramItem[Flowrate_work]" value="Flowrate_work" title="工况" checked lay-skin="primary"></a>' +
                    '<a href="javascript:void(0)" class="breadcrumb"><input type="checkbox" name="paramItem[Flowrate_std]" value="Flowrate_std" title="标况" lay-skin="primary"></a>');
            }else if(paramType == 'Totalflow'){
                $(".paramItem").html('<a href="javascript:void(0)" class="breadcrumb"><input type="checkbox" name="paramItem[Totalflow_work]" value="Totalflow_work" title="工况" checked lay-skin="primary"></a>' +
                    '<a href="javascript:void(0)" class="breadcrumb"><input type="checkbox" name="paramItem[Totalflow_std]" value="Totalflow_std" title="标况" lay-skin="primary"></a>');
            }else{
                $(".paramItem").html('');
                $(".parameterItem").hide();
            }
        }else{
            $(".paramItem").html('');
            $(".parameterItem").hide();
        }
        form.render('checkbox');
        element.render('breadcrumb');

        form.on('checkbox',function(data){
            if(data.elem.checked == false){
                var paramItemArr = $('input[type="checkbox"]:checked');
                if(paramItemArr.length == 0){
                    //这里实现勾选
                    var option = {};
                    option["paramItem["+data.value+"]"] = true;
                    form.val('searchIf',option);
                }
            }
        })
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
                        str += '<li data-id="'+v0['energyTypeId']+'" class="e_item e_active">';
                    }else{
                        str += '<li data-id="'+v0['energyTypeId']+'" class="e_item">';
                    }
                    str += '<a  href="javascript:void(0)">'+v0['energyTypeName']+'</a>';
                    str += '</li>';
                });

                $(".energy_nav").html(str);

                initParamType();
                getTree();
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
                $(".energy_tree .layui-collapse").html('');
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

                    $(".energy_tree .layui-collapse").append(str);
                    element.render('collapse');//重新对折叠面板进行渲染。
                    treeCreate(ii,vv.children,res.data.length);
                });

                if($('.energy_img:nth-child(1)').hasClass('imgs')){
                    showHigh1();
                    showTable1();
                }else{
                    showTable2();
                }

                element.on('collapse(tree)', function(data){
                    if(data.show == false){
                        var idStr = data.content.find('.ztree').attr('id');
                        var tree1 = $.fn.zTree.getZTreeObj(idStr);
                        if(tree1) tree1.cancelSelectedNode();
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

                }
            }
        };
        var idStr = '#tree'+index;
        $.fn.zTree.init($(idStr), setting, zNodes);
        var tree1 = $.fn.zTree.getZTreeObj('tree'+index);
        tree1.expandAll(true);
        if(n == 1){
            $('.ztree').css('height',$(".energy_tree").height()-66);
        }else{
            $('.ztree').css('height',$(".energy_tree").height()-55*n);
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

    function showHigh1(){
        var idStr = $('.layui-show .ztree').attr('id');
        var tree1 = $.fn.zTree.getZTreeObj(idStr);
        var treeId = tree1.getSelectedNodes();

        var param = [],unit = '';
        var paramItemArr = $('input[type="checkbox"]:checked');
        if(paramItemArr.length > 0){
            $.each(paramItemArr,function(){
                var val = $(this).val();

                if(val == 'Ep_imp' || val == 'Ep_exp'){
                    if(unit.indexOf('kWh') == -1) unit += '有功kWh ';
                }else if(val == 'Eq_imp' || val == 'Eq_exp'){
                    if(unit.indexOf('kVarh') == -1) unit += '无功kVarh';
                }

                param.push(val);
            });
        }else{
            if(paramType != 'Ep'){
                param.push(paramType);
            }
        }
        if(unit == ''){
            unit = $('.paramType .layui-this').attr('data-unit');
        }

        var name = siteObj.type == 'PARK'?siteObj.name:siteObj.title;
        if(treeId[0]) var title = name+' - '+treeId[0].nodeName+' - 能耗时比 - '+$('.paramType .layui-this').text()+'('+unit+')';
        $('.titleText').text(title);

        var beginTime = $("#timeInput").val();
        var endTime = $("#timeInput2").val();
        var data = {
            "energyTypeId":energyTypeId,
            "nodeInfo":treeId[0],
            "start":beginTime,
            "end":endTime,
            "timeType":timeType,
            "interval":interval,
            "type":dataType,
            "energyParaIds":param?param:[paramType]
        };
        load();
        request.service({
            method: 'post',
            url: '/energyAnalysis/queryPageInfoTimeData',
            data: data
        }).then(function (res) {
            disLoad();
            chartData1 = [];
            tableData2Html = '';
            var tableHeaderHtml = '',tableBodyHtml = '<tbody>';
            if (res.data.length>0) {
                chartData1 = res.data;

                // 时刻值、平均值、最大值、最小值 用折线图展示，差值用柱状图展示。
                var type = dataType == 'diff'?'bar':'line';
                series1 = [];
                xAxis = [];
                nameArr = [];
                tableData1 = [];
                cols = [{field: 'nodeNameAll', title: '对象', width:250, fixed: 'left',align:'center'}];
                tableData2 = [];

                $.each(chartData1,function(i,val){
                    var dd = val.nodeVOs;
                    $.each(dd,function(ii,vv){
                        if(xAxis.length == 0){
                            tableHeaderHtml += '<thead><tr>';
                            tableHeaderHtml += '<th lay-data="{field:\'nodeNameAll\',width:250,align:\'center\'}">对象</th>';
                            $.each(vv.times,function(m,n){
                                xAxis.push(formateDate2(n));
                                cols.push({
                                    field: 'index'+m,
                                    title: formateDate2(n),
                                    width:80,
                                    align:'center'
                                });

                                tableHeaderHtml += '<th lay-data="{field:\'index'+m+'\', width:80,align:\'center\'}">'+formateDate2(n)+'</th>';
                            });
                            tableHeaderHtml += '</tr></thead>';
                        }

                        var paramItemArr = $('input[type="checkbox"]:checked');
                        var str = '';
                        if(paramItemArr.length > 0){
                            $.each(paramItemArr,function(){
                                var value = $(this).val();
                                if(value == val.energyParaId){
                                    str = $(this).attr('title');
                                }
                            });
                        }else{
                            str = $('.paramType .layui-this').text();
                        }

                        var lineName = vv.nodeNameAll + ' ' +str;
                        nameArr.push(lineName);

                        var lineData = [];
                        var showAllSymbol = 'auto',index = 0,next = 0;
                        var obj = {'nodeNameAll':lineName};
                        tableBodyHtml += '<tr>';
                        tableBodyHtml += '<td>'+lineName+'</td>';
                        $.each(vv.values,function(mm,nn){
                            var field = 'index'+mm;
                            if(nn){
                                lineData.push(parseFloat(nn.toFixed(2)));
                                obj[field] = parseFloat(nn.toFixed(2));
                                tableBodyHtml += '<td>'+obj[field]+'</td>';
                            }else{
                                lineData.push(nn);
                                obj[field] = nn;
                                tableBodyHtml += '<td></td>';
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
                        tableBodyHtml += '</tr>';

                        series1.push({
                            name:lineName,
                            type:type,
                            // symbol:'circle',
                            symbolSize: 8,
                            showSymbol:false,//是否显示 symbol
                            connectNulls:false,//是否连接空数据
                            showAllSymbol: showAllSymbol,//false:随主轴标签间隔隐藏策略,'auto'：默认，如果有足够空间则显示标志图形，否则随主轴标签间隔隐藏策略
                            data:lineData
                        });

                        tableData1.push({
                            "nodeNameAll":lineName,
                            "average":vv.average,
                            "max":vv.max,
                            "maxTime":vv.maxTime,
                            "min":vv.min,
                            "minTime":vv.minTime
                        });

                        tableData2.push(obj);

                    });
                });
                tableData2Html += tableHeaderHtml +tableBodyHtml+'</tbody>';
                if($('.energy_img:nth-child(1)').hasClass('imgs')){
                    init1();
                    showTable1();
                }else{
                    showTable2();
                }
            }else{
                $("#chart1").html('');
                return layer.msg('没有数据');
            }
        }).catch(function(err){
            disLoad();
            console.log(err)
        });
    }

    function init1(){
        if (chart1 != null && chart1 != "" && chart1 != undefined) {
            chart1.dispose();
        }
        var option = {
            grid:{   //绘图区调整
                x:30,  //左留白
                y:10,   //上留白
                x2:40,  //右留白
                y2:30,   //下留白
                containLabel: true,//grid 区域是否包含坐标轴的刻度标签。
                borderColor: '#ccc',
                borderWidth: 1
            },
            tooltip : {
                trigger: 'axis',
                axisPointer: {
                    type: 'line'
                },
                position:function (pos, params, dom, rect, size) {
                    var obj = {top: '1%'};
                    if(size){
                        if(pos[0] < size.viewSize[0] / 2){
                            obj['left'] = pos[0];
                        }else{
                            obj['right'] = size.viewSize[0] - pos[0];
                        }
                    }
                    return obj;
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
                data:nameArr,
                left: 'center',
                bottom: 0,
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
                    data : xAxis,
                    name : '时间',
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
                    name : unit,
                    nameGap: 30,
                    nameLocation: 'middle',
                    scale: true,
                    axisLabel: {//坐标轴刻度标签的相关设置。
                        textStyle:{
                            color:"#ccc"
                        }
                    }
                }
            ],
            series : series1
        };
        chart1 = echarts.init(document.getElementById('chart1')).setOption(option, true);
    }

    function showTable2(){
        $('#table2').html(tableData2Html);
        table.init('parse-table2', { //转化静态表格
            height: $('.table2Box').height()-20
            ,page: false
            ,limit:999999
            ,id:'energy_table'
        });
        // table.render({
        //     elem: '#table2'
        //     ,height: $('.table2Box').height()-20
        //     ,data: tableData2
        //     ,page: false //开启分页
        //     ,id:'energy_table'
        //     ,cols: [cols]
        // });
    }
    //底部表格
    function showTable1(){
        table.render({
            elem: '#table1'
            ,height: $('.table1Box').height()-20
            ,data: tableData1
            ,page: false //开启分页
            ,limit:999999
            ,cols: [[ //表头
                {field: 'nodeNameAll', title: '对象', width:250, fixed: 'left',rowspan:2,align:'center'}
                ,{field: 'average', title: '平均值', width:180,rowspan:2,align:'center'}
                ,{field: 'max', title: '最大值', colspan:2,align:'center'}
                ,{field: 'min', title: '最小值', colspan:2,align:'center'}
            ],[
                {field: 'max', title: '数值', width: 180,align:'center'}
                ,{field: 'maxTime', title: '时间', width: 200,align:'center'}
                ,{field: 'min', title: '数值', width: 180,align:'center'}
                ,{field: 'minTime', title: '时间', width: 200,align:'center'}
            ]]
        });
    }

    var indexLoading;
    function load(){
        indexLoading =layer.load(1, {shade: [0.3, '#fff']});
    }
    function disLoad(){
        layer.close(indexLoading);
    }
});