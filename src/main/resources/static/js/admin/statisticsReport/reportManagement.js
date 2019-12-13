/*
 * @Author: jpp
 * @Date: 2019-11-11 10:07:34
 * @Last Modified by: jpp
 * @Last Modified time: 2019-12-02 15:30:20
 */

layui.use(['request','form', 'element', 'layer', 'jquery','table','tree'], function () {
    var form = layui.form,
        layer = layui.layer,
        tree = layui.tree,
        $ = layui.jquery,
        table = layui.table,
        treeGrid = layui.treeGrid,
        element = layui.element;

    var siteObj = JSON.parse(sessionStorage.getItem('parkId'));
    var tree1;//左侧具体节点
    var display = 'param';
    var paramsSelect;//参数 多选
    var paramTableData = [];
    var objTableData = [];
    var parentSelect;//弹窗 父节点 单选
    var objNavThis;//弹窗 企业/仪表
    var objIdMeter,objTypeMeter;//弹窗 企业选中的
    var parkRtdbId = sessionStorage.getItem('parkRtdb'); //园区标识对应的实时库租户标识
    var siteRtdbId; //仪表所属的企业对应的实时库项目标识

    funcsAuthority();

    //点击 展示参数/展示对象
    $('.nav-item').on('click',function(){
        $(this).siblings().removeClass('nav-this');
        $(this).addClass('nav-this');

        display = $(this).attr('data-id');

        if(display == 'param'){
            $('.param_title,.param_table').removeClass('layui-hide');
            $('.obj_title,.obj_table').addClass('layui-hide');
            getParamTable();
        }else{
            $('.param_title,.param_table').addClass('layui-hide');
            $('.obj_title,.obj_table').removeClass('layui-hide');
            getObjTable();
        }

        return false;
    });

    //左侧列表 查询
    $('.left_query').on('click',function(){
        getTree1();
    });
    //左侧 新增报表
    $('.add').on('click',function(){
        $(".add_report").removeClass('layui-hide');
        $(".edit_report").addClass('layui-hide');

        $("#report_id").val('');
        $("#report_name").val('');
        $("#report_energyTypeId").val($("#report_energyTypeId option:first-child").val());
        $("#report_isUse").attr('checked',false);
        $("#report_sortId").val('');
        $("#report_memo").val('');
        add('add','新增报表');
    });
    //左侧 修改报表
    $('.edit').on('click',function(){
        var val = tree1.getSelectedNodes();
        if(val.length == 0) return layer.msg('请选择一条报表');
        $(".edit_report").removeClass('layui-hide');
        $(".add_report").addClass('layui-hide');

        $("#edit_report_id").html(val[0].reportId);
        $("#report_name").val(val[0].reportName);
        $("#report_energyTypeId").val(val[0].energyTypeId);
        if(val[0].isUse == 'Y'){
            $("#report_isUse").attr('checked',true);
        }else{
            $("#report_isUse").attr('checked',false);
        }
        $("#report_sortId").val(val[0].sortId);
        $("#report_memo").val(val[0].memo);
        add('edit','修改报表');
    });
    //左侧 删除报表
    $('.delete').on('click',function(){
        var val = tree1.getSelectedNodes();
        if(val.length == 0) return layer.msg('请选择要删除的报表');
        deletes();
    });
    //左侧 启用或停用报表
    $('.startOrStop').on('click',function(){
        var val = tree1.getSelectedNodes();
        if(val.length == 0) return layer.msg('请选择要启用或停用的报表');
        startOrStop();
    });
    //左侧 导入报表配置
    $('.import').on('click',function(){
        var val = tree1.getSelectedNodes();
        if(val.length == 0) return layer.msg('请选择要导入的报表配置');
        importConf();
    });
    //左侧 导入报表配置 模板文件下载
    $("#download_demo").on('click',function(){
        var url = '/reportManage/download';
        window.location.href = encodeURI(url);
    });
    //左侧 导出报表
    $(".export").on('click',function(){
        var val = tree1.getSelectedNodes();
        if(val.length == 0) return layer.msg('请选择要导出的报表');
        var url = '/reportManage/exportExcel?fileName='+val[0].showName+'&reportId='+val[0].reportId+'&objType='+siteObj.type+'&objId='+siteObj.id;
        window.location.href = encodeURI(url);
    });

    //右侧 展示参数 查询
    $('.param_query').on('click',function(){
        getParamTable();
    });
    //右侧 展示参数 新增
    $('.param_add').on('click',function(){
        var val = tree1.getSelectedNodes();
        if(val.length == 0) return layer.msg('请选择一条报表');
        addParam('add','新增展示参数');
    });
    //右侧 展示参数 修改
    $('.param_edit').on('click',function(){
        var tableSelect = table.checkStatus('param_table').data;
        if(tableSelect.length === 0){
            return layer.msg("请选择需要修改的数据！")
        }
        if(tableSelect.length > 1){
            return layer.msg("只能选择单条数据！")
        }
        addParam('edit','修改展示参数');
    });
    //右侧 展示参数 删除
    $('.param_delete').on('click',function(){
        var tableSelect = table.checkStatus('param_table').data;
        if(tableSelect.length == 0){
            return layer.msg('请选择需要删除的数据')
        }
        if(tableSelect.length > 1){
            return layer.msg("只能选择单条数据！")
        }
        var val = tree1.getSelectedNodes();
        $('#param_delete_reportId').html(val[0].showName);
        $('#param_delete_paramId').html('['+tableSelect[0].energyParaId+']'+tableSelect[0].energyParaName);
        $('#param_delete_name').html(tableSelect[0].displayName);
        // 数值类型
        form.val("param_delete", {
            "dataType[timeValue]": tableSelect[0].timeValue == 'Y'?true:false
            ,"dataType[maxValue]": tableSelect[0].maxValue == 'Y'?true:false
            ,"dataType[minValue]": tableSelect[0].minValue == 'Y'?true:false
            ,"dataType[avgValue]": tableSelect[0].avgValue == 'Y'?true:false
            ,"dataType[diffValue]": tableSelect[0].diffValue == 'Y'?true:false
        });
        form.render('checkbox');

        deletesParam();
    });

    //右侧 展示对象 新增
    $('.obj_add').on('click',function(){
        var val = tree1.getSelectedNodes();
        if(val.length == 0) return layer.msg('请选择一条报表');

        $(".add_obj_nodeId").removeClass('layui-hide');
        $(".edit_obj_nodeId").addClass('layui-hide');

        $("#obj_nodeId").val('');
        var radioSelect = treeGrid.radioStatus('object_table');
        if(!radioSelect.nodeId){
            parentSelect.setValue([ ]);
        }else{
            parentSelect.update({
                radio: true,
                clickClose: true,
                prop: {
                    name: 'nodeName',
                    value: 'nodeId'
                },
                data: [{"nodeName":radioSelect.title,"nodeId":radioSelect.nodeId}]
            });
            parentSelect.setValue([radioSelect.nodeId]);
        }
        $("#obj_nodeName").val('');
        $("#obj_sortId").val('');
        $("#obj_memo").val('');
        $("#ds_data").html('');//仪表列表 数据源
        $("#formula").val('');//公式

        addObject('add','新增展示对象');
    });
    //右侧 展示对象 修改
    $('.obj_edit').on('click',function(){
        var radioSelect = treeGrid.radioStatus('object_table');
        if(!radioSelect.nodeId){
            return layer.msg("请选择需要修改的数据！");
        }

        $(".edit_obj_nodeId").removeClass('layui-hide');
        $(".add_obj_nodeId").addClass('layui-hide');

        $("#edit_obj_nodeId").html(radioSelect.nodeId);
        if(radioSelect.parentId){
            parentSelect.update({
                radio: true,
                clickClose: true,
                prop: {
                    name: 'nodeName',
                    value: 'nodeId'
                },
                data: [{"nodeName":radioSelect.parentTitle,"nodeId":radioSelect.parentId}]
            });
            parentSelect.setValue([radioSelect.parentId]);
        }else{
            parentSelect.update({
                radio: true,
                clickClose: true,
                prop: {
                    name: 'nodeName',
                    value: 'nodeId'
                },
                data: []
            });
            parentSelect.setValue([]);
        }
        $("#obj_nodeName").val(radioSelect.nodeName);
        $("#obj_sortId").val(radioSelect.sortId);
        $("#obj_memo").val(radioSelect.memo);
        renderDataSource(radioSelect.dataSource);//仪表列表 数据源

        addObject('edit','修改展示对象');
    });
    //右侧 展示对象 删除
    $('.obj_delete').on('click',function(){
        var radioSelect = treeGrid.radioStatus('object_table');
        if(!radioSelect.nodeId){
            return layer.msg('请选择需要删除的数据')
        }
        var val = tree1.getSelectedNodes();
        $('#obj_delete_report').html(val[0].showName);
        $('#obj_delete_object').html('['+radioSelect.nodeId+']'+radioSelect.nodeName);

        deleteObject();
    });
    //右侧 展示对象 导入
    $('.obj_import').on('click',function(){
        var val = tree1.getSelectedNodes();
        $('#obj_import_report').html(val[0].showName);
        importObject();
    });
    //右侧 展示对象导入 模板文件下载
    $("#obj_download_demo").on('click',function(){
        var url = '/reportObjDetail/download';
        window.location.href = encodeURI(url);
    });
    //右侧 展示对象 导出
    $('.obj_export').on('click',function(){
        var val = tree1.getSelectedNodes();
        var url = '/reportObjDetail/exportExcel?fileName='+val[0].showName+'&reportId='+val[0].reportId+'&objType='+siteObj.type+'&objId='+siteObj.id;
        window.location.href = encodeURI(url);
    });

    //点击 展示对象弹框 企业/仪表
    $('.obj_nav-item').on('click',function(){
        $(this).siblings().removeClass('obj_nav-this');
        $(this).addClass('obj_nav-this');

        objNavThis = $(this).attr('data-id');

        if(objNavThis == 'park'){
            $('#parkSiteTableContent').removeClass('layui-hide');
            $('#meterTableContent,.meter_btnGroup,.meterTap').addClass('layui-hide');
        }else{
            $('#parkSiteTableContent').addClass('layui-hide');
            $('#meterTableContent,.meter_btnGroup,.meterTap').removeClass('layui-hide');
        }
        return false;
    });

    //点击 展示对象弹框 企业查询
    $('.obj_parkSiteQuery').on('click',function(){
        getParkOrSite();
    });
    //点击 展示对象弹框 仪表查询
    $('.obj_meterQuery').on('click',function(){
        getMeterTable();
    });
    //点击 展示对象弹框 企业列表
    $(document).on("click", '#parkSiteTbody tr', function () {
        $(this).addClass('layui-this');
        $(this).siblings().removeClass('layui-this');

        objIdMeter = $(this).attr('data-id');
        objTypeMeter = $(this).attr('data-type');
        siteRtdbId = $(this).data("rtdbprojectid");//弹窗 企业选中的
        getMeterTable();

        //点击某一具体企业，切换到仪表标签页
        $('.obj_nav-item').removeClass('obj_nav-this');
        $('.obj_nav-item:nth-child(2)').addClass('obj_nav-this');
        $('#parkSiteTableContent').addClass('layui-hide');
        $('#meterTableContent,.meter_btnGroup,.meterTap').removeClass('layui-hide');
    });
    //双击 展示对象弹框 仪表列表
    $(document).on("dblclick", '#meterTbody tr', function () {
        var meterId = $(this).attr('data-id');
        var dataSocureText = parkRtdbId + '.' + siteRtdbId + '.' + meterId; //拼接数据源
        // 园区标识对应的实时库租户标识（SELECT rtdb_tenant_id FROM `tb_park` LIMIT 1;）+ "."
        // + 仪表所属的企业对应的实时库项目标识（SELECT rtdb_project_id FROM `tb_site` WHERE site_id = ?;）+ "."
        // +  仪表标识
        var str = '<tr data-socure="' + dataSocureText + '"><td>' +
            '<input type="checkbox" name="dataSocurecheckbox['+dataSocureText+']" value="'+dataSocureText+'" title="'+dataSocureText+'" lay-skin="primary"></td></tr>';
        $('#ds_data').append(str);
        form.render('checkbox', 'ds_list');
    });
    //点击 展示对象弹框 仪表列表 添加至所选之前
    $('#addSelectedBefore').on('click',function(){
        var checkboxArr = $("#ds_data input:checkbox");
        var checkedArr = $("#ds_data input:checkbox:checked");
        if(checkboxArr.length > 0 && checkedArr.length == 0) return layer.msg('请选择仪表列表');

        $("#meterTbody input:checkbox:checked").each(function(i){
            var meterId = $(this).val();
            var dataSocureText = parkRtdbId + '.' + siteRtdbId + '.' + meterId; //拼接数据源
            var str = '<tr data-socure="' + dataSocureText + '"><td>' +
                '<input type="checkbox" name="dataSocurecheckbox['+dataSocureText+']" value="'+dataSocureText+'" title="'+dataSocureText+'" lay-skin="primary"></td></tr>';

            if(checkboxArr.length == 0){
                $("#ds_data").append(str);
            }else{
                $("#ds_data input:checkbox:checked").eq(0).parent().parent().before(str);
            }
        });
        form.render('checkbox', 'ds_list');
    });
    //点击 展示对象弹框 仪表列表 添加至末尾
    $('#addSelectedAfter').on('click',function(){
        $("#meterTbody input:checkbox:checked").each(function(i){
            var meterId = $(this).val();
            var dataSocureText = parkRtdbId + '.' + siteRtdbId + '.' + meterId; //拼接数据源
            var str = '<tr data-socure="' + dataSocureText + '"><td>' +
                '<input type="checkbox" name="dataSocurecheckbox['+dataSocureText+']" value="'+dataSocureText+'" title="'+dataSocureText+'" lay-skin="primary"></td></tr>';
            $("#ds_data").append(str);
        });
        form.render('checkbox', 'ds_list');
    });
    //点击 展示对象弹框 仪表列表 全选
    $('#allSelect').on('click',function(){
        var option = {};
        $("#meterTbody input:checkbox").each(function(i){
            option["meterId["+$(this).val()+"]"] = true;
        });
        form.val("meter_list", option);
    });
    //点击 展示对象弹框 仪表列表 反选
    $('#allUnSelect').on('click',function(){
        var option = {};
        $("#meterTbody input:checkbox").each(function(i){
            option["meterId["+$(this).val()+"]"] = $(this)[0].checked == true?false:true;
        });
        form.val("meter_list", option);
    });
    //点击 展示对象弹框 仪表列表 取消
    $('#cancel').on('click',function(){
        var option = {};
        $("#meterTbody input:checkbox").each(function(i){
            option["meterId["+$(this).val()+"]"] = false;
        });
        form.val("meter_list", option);
    });
    //点击 展示对象弹框 数据源 删除所选
    $('#removeSelected').on('click',function(){
        $("#ds_data input:checkbox:checked").each(function(i){
            $(this).parent().parent('tr').remove();
        });
    });
    //点击 展示对象弹框 数据源 清空
    $('#clearSelected').on('click',function(){
        $("#ds_data").html('');
    });
    //点击 展示对象弹框 数据源公式 检查
    $('#checkFormula').on('click',function(){
        var dataSocureLength = $('#ds_data tr').length; //数据源添加的数据源长度
        var formulaVal = $("#formula").val();
        var selectValArr = formulaVal?formulaVal.split('+'):[];

        if (dataSocureLength == 0) {
            return layer.msg('数据源仪表列表列表为空');
        }else if (dataSocureLength == 1) {
            if(selectValArr.length == 1){
                return layer.msg('公式中的问号个数不能为一个');
            }else if(selectValArr.length > 1){
                return layer.msg('数据源-仪表参数列表的个数 和 公式中的问号个数不一致');
            }
        }else{
            if (formulaVal.length > 0) {
                if (selectValArr.length != dataSocureLength) { //检查仪表参数列表的个数和公式中的问号个数是否一致，不一致时提醒用户
                    return layer.msg('数据源-仪表参数列表的个数 和 公式中的问号个数不一致');
                }
            }else{
                return layer.msg('未填写公式');
            }
        }
    });
    //点击 展示对象弹框 数据源公式 所有仪表相加
    $('#addAllMeter').on('click',function(){
        // 点击 所有仪表相加 按钮，则将上面仪表列表中的所有仪表相加，即?+?+?....有多少个仪表就有多少个?+，
        // 最后将最后一个加号删除。如果只有一个仪表，则公式结果为空。
        var dataSocureLength = $('#ds_data tr').length; //数据源添加的数据源长度
        if (dataSocureLength.length == 0) {
            return layer.msg('未添加仪表参数');
        }
        var addFormulaVal = '';
        if (dataSocureLength == 1) { //如果只有一个仪表参数，则公式结果为空。
            addFormulaVal = '';
        }else{
            for (var index = 0; index < dataSocureLength; index++) {
                if (index != dataSocureLength - 1) {
                    addFormulaVal += '?+'
                } else {
                    addFormulaVal += '?'
                }
            }
        }
        $("#formula").val(addFormulaVal);
    });
    //点击 展示对象弹框 数据源公式 清空
    $('#clearFormula').on('click',function(){
        $("#formula").val('');
    });

    //报表弹框 提示点击 - 报表标识
    $("#report_id_tip").on("click", function () {
        layer.tips('字母、数字组合，长度不超过20,在园区/当前企业内唯一。', '#report_id_tip');
    });
    //报表弹框 提示点击 - 报表名称
    $("#report_name_tip").on("click", function () {
        layer.tips('长度不超过30。', '#report_name_tip');
    });
    //报表弹框 提示点击 - 排序标识
    $("#report_sortId_tip").on("click", function () {
        layer.tips('字母、数字组合，长度不超过10。', '#report_sortId_tip');
    });

    //展示参数弹框 提示点击 - 展示名称
    $("#param_name_tip").on("click", function () {
        layer.tips('长度不超过20。', '#param_name_tip');
    });
    //展示参数弹框 提示点击 - 排序标识
    $("#param_sortId_tip").on("click", function () {
        layer.tips('字母、数字组合，长度不超过10。', '#param_sortId_tip');
    });

    //展示对象弹框 提示点击 - 节点ID
    $("#obj_nodeId_tip").on("click", function () {
        layer.tips('字母、数字组合，长度不超过20,在当前报表内唯一。', '#obj_nodeId_tip');
    });
    //展示对象弹框 提示点击 - 节点名称
    $("#obj_nodeName_tip").on("click", function () {
        layer.tips('长度不超过30。', '#obj_nodeName_tip');
    });
    //展示对象弹框 提示点击 - 排序标识
    $("#obj_sortId_tip").on("click", function () {
        layer.tips('字母、数字组合，长度不超过10。', '#obj_sortId_tip');
    });

    //监听提交
    form.on('submit(*)', function (data) {
        return false;//阻止表单跳转
    });
    /*----------------------------------函数------------------------------------*/
    // 新增修改报表
    function add(type,title){
        layui.layer.open({
            type: 1,
            title: title,
            closeBtn: 1,
            shade: 0,
            anim: 1,
            maxmin: true, //开启最大化最小化按钮
            area: ['400px', '450px'],
            content: $('#report_add'),
            btn: ['确定', '取消'],
            success: function () {
                $("#report_add").removeClass('layui-hide');
                form.render();
            },
            yes: function (index) {
                var isMainPageArr = $('#report_isUse:checked');
                var isUse = 'N';
                if(isMainPageArr.length > 0){
                    isUse = 'Y';
                }
                var val = tree1.getSelectedNodes();
                if(type == 'edit'){
                    var data = {
                        "objType": siteObj.type,
                        "objId": siteObj.id,
                        "id": val[0].id,
                        "reportId": val[0].reportId,
                        "reportName": $("#report_name").val(),
                        "energyTypeId": $("#report_energyTypeId").val(),
                        'isUse':isUse,
                        "sortId": $("#report_sortId").val(),
                        "memo":$("#report_memo").val()
                    }
                }else if(type == 'add'){
                    var data = {
                        "objType": siteObj.type,
                        "objId": siteObj.id,
                        "reportId": $("#report_id").val(),
                        "reportName": $("#report_name").val(),
                        "energyTypeId": $("#report_energyTypeId").val(),
                        'isUse':isUse,
                        "sortId": $("#report_sortId").val(),
                        "memo":$("#report_memo").val()
                    };
                    if(data.reportId == '') return layer.msg('报表标识不能为空！');
                    if(data.reportId.length > 20) return layer.msg('报表标识长度不能超过20！');
                    var re = /^[0-9a-zA-Z]+$/g;
                    if(!(re.test(data.reportId))){
                        return layer.msg('报表标识为字母、数字组合！');
                    }
                }

                if(data.reportName == '') return layer.msg('报表名称不能为空！');
                if(data.reportName.length > 30) return layer.msg('报表名称长度不能超过30！');
                if(data.sortId.length > 10) return layer.msg('排序标识长度不能超过10！');

                var re2 = /^[0-9a-zA-Z]+$/g;
                if(data.sortId.length > 0 && !(re2.test(data.sortId))){
                    return layer.msg('排序标识为字母、数字组合！');
                }
                var  url ='/reportManage/add'
                 if(type =='edit'){
                    url ='/reportManage/edit'
                 }
                load();
                request.service({
                    method: 'post',
                    url: url,
                    data: data
                }).then(function (res) {
                    disLoad();
                    getTree1();
                    layer.close(index);
                }).catch(function(err){
                    disLoad();
                    console.log(err)
                });
            },
            end: function(index){
                $("#report_add").addClass('layui-hide');
            }
        });
    }

    // 删除报表
    function deletes(){
        layer.open({
            type: 1,
            title: '删除报表',
            closeBtn: 1,
            shade: 0,
            anim: 1,
            maxmin: true, //开启最大化最小化按钮
            area: ['400px', '350px'],
            content: $('#report_delete'),
            btn: ['确定', '取消'],
            success: function () {
                $("#report_delete").removeClass('layui-hide');

                var val = tree1.getSelectedNodes();
                $("#delete_report_id").html(val[0].reportId);
                $("#delete_report_name").html(val[0].reportName);
                $("#delete_energyTypeId").html(val[0].energyTypeName);
                form.render();
            },
            yes: function (index) {
                var val = tree1.getSelectedNodes();
                load();
                request.service({
                    method: 'post',
                    url: '/reportManage/delete',
                    data:{
                        "objType": siteObj.type,
                        "objId": siteObj.id,
                        "reportId":val[0].reportId
                    }
                }).then(function (res) {
                    disLoad();
                    getTree1();
                    layer.close(index);
                }).catch(function(err){
                    disLoad();
                    console.log(err)
                });
            },
            end: function(index){
                $("#report_delete").addClass('layui-hide');
            }
        });
    }

    // 启用或停用报表
    function startOrStop(){
        var val = tree1.getSelectedNodes();
        if(siteObj){
            load();
            request.service({
                method: 'post',
                url: '/reportManage/enableOrDisableReport',
                data: {
                    'objType': siteObj.type,
                    'objId': siteObj.id,
                    'reportId':val[0].reportId,
                    'isUse':val[0].isUse
                }
            }).then(function (res) {
                disLoad();
                getTree1();
            }).catch(function(err){
                disLoad();
                console.log(err)
            });
        }
    }

    // 导入报表配置
    function importConf(){
        layer.open({
            type: 1,
            title: '导入报表配置',
            closeBtn: 1,
            shade: 0,
            anim: 1,
            maxmin: true, //开启最大化最小化按钮
            area: ['500px', '450px'],
            content: $('#report_import'),
            btn: ['确定', '取消'],
            success: function () {
                $("#report_import").removeClass('layui-hide');

                var val = tree1.getSelectedNodes();
                $("#import_report_id").html(val[0].reportId);
                $("#import_report_name").html(val[0].reportName);
                $("#import_energyTypeId").html(val[0].energyTypeName);
                form.render();
            },
            yes: function (index) {
                var val = tree1.getSelectedNodes();
                var formData = new FormData($("#report_import .layui-form")[0]);

                var file = formData.get('file');
                if(file.size == 0){
                    return layer.msg('请上传文件！');
                }

                formData.append("objType", siteObj.type);
                formData.append("objId", siteObj.id);
                formData.append("reportId", val[0].reportId);
                formData.append("energyTypeId", val[0].energyTypeId);

                load();
                request.service({
                    method: 'post',
                    url: '/reportManage/importExcel',
                    data:formData
                }).then(function (res) {
                    disLoad();
                    getTree1();
                    layer.msg(res.msg);
                    layer.close(index);
                }).catch(function(err){
                    disLoad();
                    console.log(err)
                });
            },
            end: function(index){
                $("#report_import").addClass('layui-hide');
            }
        });
    }

    // 新增修改展示参数
    function addParam(type,title){
        layer.open({
            type: 1,
            title: title,
            closeBtn: 1,
            shade: 0,
            anim: 1,
            maxmin: true, //开启最大化最小化按钮
            area: ['450px', '530px'],
            content: $('#param_add'),
            btn: ['确定', '取消'],
            success: function () {
                $("#param_add").removeClass('layui-hide');

                var val = tree1.getSelectedNodes();
                if(val.length == 0) return layer.msg('请选择一条报表');
                $('#param_add_report_id').html(val[0].showName);

                if(type == 'edit'){
                    $(".edit_param").removeClass('layui-hide');
                    $(".add_param").addClass('layui-hide');

                    var tableSelect = table.checkStatus('param_table').data;
                    $("#param_edit_parameter").html('['+tableSelect[0].energyParaId+']'+tableSelect[0].energyParaName);
                    $("#param_name").val(tableSelect[0].displayName);
                    // 数值类型
                    form.val("param_add", {
                        "dataType[timeValue]": tableSelect[0].timeValue == 'Y'?true:false
                        ,"dataType[maxValue]": tableSelect[0].maxValue == 'Y'?true:false
                        ,"dataType[minValue]": tableSelect[0].minValue == 'Y'?true:false
                        ,"dataType[avgValue]": tableSelect[0].avgValue == 'Y'?true:false
                        ,"dataType[diffValue]": tableSelect[0].diffValue == 'Y'?true:false
                    });

                    $("#param_sortId").val(tableSelect[0].sortId);
                    $("#param_memo").val(tableSelect[0].memo);
                }else{
                    $(".add_param").removeClass('layui-hide');
                    $(".edit_param").addClass('layui-hide');

                    $("#param_add_parameter").val($("#param_add_parameter option:first-child").val());
                    $("#param_name").val('');
                    // 数值类型
                    form.val("param_add", {
                        "dataType[timeValue]": false
                        ,"dataType[maxValue]": false
                        ,"dataType[minValue]": false
                        ,"dataType[avgValue]": false
                        ,"dataType[diffValue]": false
                    });

                    $("#param_sortId").val('');
                    $("#param_memo").val('');
                }
                form.render();
            },
            yes: function (index) {
                var tableSelect = table.checkStatus('param_table').data;
                var val = tree1.getSelectedNodes();
                // 数值类型
                var timeValue = 'N';
                var maxValue = 'N';
                var minValue = 'N';
                var avgValue = 'N';
                var diffValue = 'N';
                $("#param_add input:checkbox:checked").each(function(i){
                    var val = $(this).val();
                    switch(val){
                        case 'timeValue':
                            timeValue = 'Y';
                            break;
                        case 'maxValue':
                            maxValue = 'Y';
                            break;
                        case 'minValue':
                            minValue = 'Y';
                            break;
                        case 'avgValue':
                            avgValue = 'Y';
                            break;
                        case 'diffValue':
                            diffValue = 'Y';
                            break;
                    }
                });

                if(type == 'edit'){
                    var data = {
                        "objType": siteObj.type,
                        "objId": siteObj.id,
                        "id": tableSelect[0].id,
                        "reportId": val[0].reportId,
                        "energyParaId": tableSelect[0].energyParaId,
                        'displayName':$("#param_name").val(),
                        // 数值类型
                        'timeValue':timeValue,
                        'maxValue':maxValue,
                        'minValue':minValue,
                        'avgValue':avgValue,
                        'diffValue':diffValue,
                        "sortId": $("#param_sortId").val(),
                        "memo":$("#param_memo").val()
                    }
                }else if(type == 'add'){
                    var data = {
                        "objType": siteObj.type,
                        "objId": siteObj.id,
                        "reportId": val[0].reportId,
                        "energyParaId": $("#param_add_parameter").val(),
                        'displayName':$("#param_name").val(),
                        // 数值类型
                        'timeValue':timeValue,
                        'maxValue':maxValue,
                        'minValue':minValue,
                        'avgValue':avgValue,
                        'diffValue':diffValue,
                        "sortId": $("#param_sortId").val(),
                        "memo":$("#param_memo").val()
                    }
                }

                if(data.displayName == '') return layer.msg('展示名称不能为空！');
                if(data.displayName.length > 20) return layer.msg('展示名称长度不能超过20！');
                if(timeValue == 'N' && maxValue == 'N' && minValue == 'N' && avgValue == 'N' && diffValue == 'N'){
                    return layer.msg('数值类型不能为空! ');
                }
                if(data.sortId.length > 10) return layer.msg('排序标识长度不能超过10！');

                var re3 = /^[0-9a-zA-Z]+$/g;
                if(data.sortId.length > 0 && !(re3.test(data.sortId))){
                    return layer.msg('排序标识为字母、数字组合！');
                }
                var url ='/reportParaDetail/add'
                 if(type == 'edit'){
                    url ='/reportParaDetail/edit'
                 }
                load();
                request.service({
                    method: 'post',
                    url: url,
                    data: data
                }).then(function (res) {
                    disLoad();
                    getParamTable();
                    layer.close(index);
                }).catch(function(err){
                    disLoad();
                    console.log(err)
                });
            },
            end: function(index){
                $("#param_add").addClass('layui-hide');
            }
        });
    }

    // 删除展示参数
    function deletesParam(){
        layer.open({
            type: 1,
            title: '删除报表',
            closeBtn: 1,
            shade: 0,
            anim: 1,
            maxmin: true, //开启最大化最小化按钮
            area: ['400px', '420px'],
            content: $('#param_delete'),
            btn: ['确定', '取消'],
            success: function () {
                $("#param_delete").removeClass('layui-hide');
            },
            yes: function (index) {
                var val1 = tree1.getSelectedNodes();
                var tableSelect = table.checkStatus('param_table').data;
                load();
                request.service({
                    method: 'post',
                    url: '/reportParaDetail/delete',
                    data:{
                        'objType':siteObj.type,
                        'objId':siteObj.id,
                        'reportId':val1[0].reportId,
                        'energyParaId':tableSelect[0].energyParaId
                    }
                }).then(function (res) {
                    disLoad();
                    getParamTable();
                    layer.close(index);
                }).catch(function(err){
                    disLoad();
                    console.log(err)
                });
            },
            end: function(index){
                $("#param_delete").addClass('layui-hide');
            }
        });
    }

    // 新增修改展示对象
    function addObject(type,title) {
        layer.open({
            type: 1,
            title: title,
            closeBtn: 1,
            shade: 0,
            anim: 1,
            maxmin: true, //开启最大化最小化按钮
            area: ['1450px', '700px'],
            content: $('#obj_add'),
            btn: ['确定', '取消'],
            success: function () {
                $("#obj_add").removeClass('layui-hide');
                var val = tree1.getSelectedNodes();
                $('#obj_report_id').html(val[0].showName);
                form.render();
                if (siteObj.type == 'PARK') { //当为园区时请求获取当前园区下的企业
                    $('.obj_nav-item').removeClass('obj_nav-this');
                    $('.obj_nav-item:nth-child(1)').addClass('obj_nav-this');
                    $('#parkSiteTableContent').removeClass('layui-hide');
                    $('#meterTableContent,.meter_btnGroup,.meterTap').addClass('layui-hide');
                }else{//当不为园区时 选择为某个企业时
                    getMeterTable();

                    $('.obj_nav-item').removeClass('obj_nav-this');
                    $('.obj_nav-item:nth-child(2)').addClass('obj_nav-this');
                    $('#parkSiteTableContent').addClass('layui-hide');
                    $('#meterTableContent,.meter_btnGroup,.meterTap').removeClass('layui-hide');
                }
            },
            yes: function (index) {
                var radioSelect = treeGrid.radioStatus('object_table');
                var val = tree1.getSelectedNodes();

                var selectData = parentSelect.getValue();
                selectData = selectData.length>0?selectData[0].nodeId:'';

                //获取 数据源
                var dataSource;
                var dataSourceArr = [];
                $('#ds_data tr').each(function (i) {
                    dataSourceArr.push($(this).attr('data-socure'));
                });
                if (dataSourceArr.length === 0) { //当为数据源时 值为null
                    dataSource = null;
                } else {
                    var formulaVal = $("#formula").val();//公式
                    if (formulaVal.length > 0) {
                        var selectValArr = formulaVal.split('+');
                        if (dataSourceArr.length == 1) { //如果仪表参数列表中只有一个仪表参数，公式可以为空，但不能只有1个问号
                            if (selectValArr.length == 1) {
                                return layer.msg('公式检查失败')
                            }
                        }
                        if (selectValArr.length != dataSourceArr.length) { //检查数据源-仪表参数列表的个数 和 公式中的问号个数是否一致，不一致时提醒用户
                            return layer.msg('公式检查失败')
                        }

                        var str = dataSourceArr.join(',');
                        dataSource = str + '#' + formulaVal;
                    }else{
                        if(dataSourceArr.length == 1){
                            dataSource = dataSourceArr[0];
                        }else{
                            return layer.msg('公式检查失败');
                        }
                    }
                }

                if(type == 'edit'){
                    var data = {
                        "objType": siteObj.type,
                        "objId": siteObj.id,
                        "id": radioSelect.id,
                        "reportId": radioSelect.reportId,
                        "nodeId": radioSelect.nodeId,
                        "parentId":selectData,
                        'nodeName':$("#obj_nodeName").val(),
                        "sortId": $("#obj_sortId").val(),
                        "memo":$("#obj_memo").val(),
                        "dataSource":dataSource
                    }
                }else if(type == 'add'){
                    var data = {
                        "objType": siteObj.type,
                        "objId": siteObj.id,
                        "reportId": val[0].reportId,
                        "nodeId": $("#obj_nodeId").val(),
                        "parentId":selectData,
                        'nodeName':$("#obj_nodeName").val(),
                        "sortId": $("#obj_sortId").val(),
                        "memo":$("#obj_memo").val(),
                        "dataSource":dataSource
                    };
                    if(data.nodeId == '') return layer.msg('节点ID不能为空！');
                    if(data.nodeId.length > 20) return layer.msg('节点ID长度不能超过20！');

                    var re4 = /^[0-9a-zA-Z]+$/g;
                    if(!(re4.test(data.nodeId))){
                        return layer.msg('节点ID为字母、数字组合！');
                    }
                }

                if(data.nodeName == '') return layer.msg('节点名称不能为空！');
                if(data.nodeName.length > 30) return layer.msg('节点名称长度不能超过30！');
                if(data.sortId.length > 10) return layer.msg('排序标识长度不能超过10！');

                var re5 = /^[0-9a-zA-Z]+$/g;
                if(!(re5.test(data.sortId))){
                    return layer.msg('排序标识为字母、数字组合！');
                }
                var url ='/reportObjDetail/add';
                 if(type == 'edit'){
                    url ='/reportObjDetail/edit';
                 }
                load();
                request.service({
                    method: 'post',
                    url: url,
                    data: data
                }).then(function (res) {
                    disLoad();
                    getObjTable();
                    layer.close(index);
                }).catch(function(err){
                    disLoad();
                    console.log(err)
                });
            },
            end: function(index){
                $("#obj_add").addClass('layui-hide');
            }
        });
    }

    //修改时的数据源
    function renderDataSource(dataSource){
        $("#ds_data").html('');
        $("#formula").val('');
        if(dataSource && dataSource.length>0){
            if(dataSource.indexOf('#') > 0){
                var arr = dataSource.split('#');
                var dataSourceArr = arr[0].split(',');
                var formulaVal = arr[1];
                $("#formula").val(formulaVal);//公式
                $.each(dataSourceArr,function(ii,vv){
                    var str = '<tr data-socure="' + vv + '"><td>' +
                        '<input type="checkbox" name="dataSocurecheckbox['+vv+']" value="'+vv+'" title="'+vv+'" lay-skin="primary"></td></tr>';
                    $("#ds_data").append(str);
                });
            }else{
                var str = '<tr data-socure="' + dataSource + '"><td>' +
                    '<input type="checkbox" name="dataSocurecheckbox['+dataSource+']" value="'+dataSource+'" title="'+dataSource+'" lay-skin="primary"></td></tr>';
                $("#ds_data").append(str);
                $("#formula").val('');
            }

            form.render('checkbox', 'ds_list');
        }
    }

    //仪表选择 企业
    function getParkOrSite(){
        var key = $("#obj_parkSiteName").val(); // 查询key值
        var keyUrl = '';
        if (key) {
            keyUrl = key //判断是否存在key  存在则拼接url
        }
        if (siteObj && siteObj.type == 'PARK') { //当为园区时请求获取当前园区下的企业
            load();
            request.service({
                method: 'get',
                url: '/common/getAllCurrentSite/' + keyUrl
            }).then(function (res) {
                disLoad();
                var str = '';
                $.each(res.data,function(kk,jj){
                    str += '<tr data-rtdbprojectid="' + jj.rtdbProjectId + '" data-id="'+jj.objId+'" data-type="'+jj.objType+'"><td>['+jj.objId+']'+jj.siteName+'</td></tr>';
                });
                $('#parkSiteTbody').html(str);
            }).catch(function(err){
                disLoad();
                console.log(err)
            });
        } else { //当不为园区时 选择为某个企业时
            var str = '<tr data-rtdbprojectid="' + siteObj.rtdbProjectId + '" data-id="'+siteObj.id+'" data-type="'+siteObj.type+'"><td>['+siteObj.id+']'+siteObj.title+'</td></tr>';
            $('#parkSiteTbody').html(str);

            objIdMeter = siteObj.id;
            objTypeMeter = siteObj.type;
            siteRtdbId = siteObj.rtdbProjectId;//弹窗 企业选中的
            getMeterTable();

            $('.obj_nav-item').removeClass('obj_nav-this');
            $('.obj_nav-item:nth-child(2)').addClass('obj_nav-this');
            $('#parkSiteTableContent').addClass('layui-hide');
            $('#meterTableContent,.meter_btnGroup,.meterTap').removeClass('layui-hide');
        }
    }
    //根据所选企业获取仪表数据
    function getMeterTable() {
        var val1 = tree1?tree1.getSelectedNodes():[];
        if(siteObj && val1.length>0) {
            load();
            request.service({
                method: 'post',
                url: '/meter/getCurrentSiteByEnergyTypeId',
                data: {
                    "objType": objTypeMeter,
                    "objId": objIdMeter,
                    "energyTypeId": val1[0].energyTypeId,
                    "key": $("#obj_meterName").val()
                }
            }).then(function (res) {
                disLoad();
                var str = '';
                $.each(res.data, function (m, n) {
                    str += '<tr data-id="' + n.meterId + '"><td ><input type="checkbox" name="meterId['+n.meterId+']" value="' + n.meterId + '" title="['+n.meterId+']'+n.meterName+'" lay-skin="primary"></td></tr>';
                });
                $('#meterTbody').html(str);
                form.render('checkbox', 'meter_list');
            }).catch(function (err) {
                disLoad();
                console.log(err)
            });
        }else{
            $('#meterTbody').html('');
        }
    }

    //新增修改 弹窗 父节点
    function renderSelect(data){
        parentSelect = xmSelect.render({
            el: '#obj_parentNode',
            radio: true,
            clickClose: true,
            prop: {
                name: 'nodeName',
                value: 'nodeId'
            },
            content: '<div id="ele1" lay-filter="ele1"></div>'
        });

        //渲染自定义内容
        tree.render({
            elem: '#ele1'  //绑定元素
            ,data: data
            ,id:'ele1Id'
            ,showLine: false  //是否开启连接线
            ,onlyIconControl:true
            ,click: function(obj){
                // console.log(obj.data); //得到当前点击的节点数据
                parentSelect.closed();
                parentSelect.update({
                    radio: true,
                    clickClose: true,
                    prop: {
                        name: 'nodeName',
                        value: 'nodeId'
                    },
                    data: [{"nodeName":obj.data.title,"nodeId":obj.data.nodeId}]
                });
                parentSelect.setValue([obj.data.nodeId]);
            }
        });
    }

    // 删除展示对象
    function deleteObject() {
        layer.open({
            type: 1,
            title: '删除展示对象',
            closeBtn: 1,
            shade: 0,
            anim: 1,
            maxmin: true, //开启最大化最小化按钮
            area: ['400px', '350px'],
            content: $('#obj_delete'),
            btn: ['确定', '取消'],
            success: function () {
                $("#obj_delete").removeClass('layui-hide');
            },
            yes: function (index) {
                var val = tree1.getSelectedNodes();
                var radioSelect = treeGrid.radioStatus('object_table');
                load();
                var data = {
                    "objType": siteObj.type,
                    "objId": siteObj.id,
                    "reportId":val[0].reportId,
                    "nodeId":radioSelect.nodeId
                };
                request.service({
                    method: 'post',
                    url: '/reportObjDetail/delete',
                    data:data
                }).then(function (res) {
                    disLoad();
                    getObjTable();
                    layer.close(index);
                }).catch(function(err){
                    disLoad();
                    console.log(err)
                });
            },
            end: function(index){
                $("#obj_delete").addClass('layui-hide');
            }
        });
    }

    // 导入展示对象
    function importObject() {
        layer.open({
            type: 1,
            title: '导入展示对象',
            closeBtn: 1,
            shade: 0,
            anim: 1,
            maxmin: true, //开启最大化最小化按钮
            area: ['550px', '350px'],
            content: $('#obj_import'),
            btn: ['确定', '取消'],
            success: function () {
                $("#obj_import").removeClass('layui-hide');
            },
            yes: function (index) {
                var val = tree1.getSelectedNodes();
                var formData = new FormData($("#obj_import .layui-form")[0]);

                var file = formData.get('file');
                if(file.size == 0){
                    return layer.msg('请上传文件！');
                }

                formData.append("objType", siteObj.type);
                formData.append("objId", siteObj.id);
                formData.append("reportId", val[0].reportId);
                load();
                request.service({
                    method: 'post',
                    url: '/reportObjDetail/importExcel',
                    data:formData
                }).then(function (res) {
                    disLoad();
                    layer.close(index);
                    getObjTable();
                }).catch(function(err){
                    disLoad();
                    console.log(err)
                });
            },
            end: function(index){
                $("#obj_import").addClass('layui-hide');
            }
        });
    }

    // 获取能源种类
    function getEnergyType(){
        load();
        request.service({
            method: 'get',
            url: '/common/queryEnergyTypes'
        }).then(function (res) {
            disLoad();
            var str = '';
            $.each(res.data,function(ii,vv){
                str += '<option value="'+vv.energyTypeId+'" >'+vv.energyTypeName+'</option>';
            });
            $("#report_energyTypeId").html(str);
            form.render('select');
        }).catch(function(err){
            disLoad();
            console.log(err)
        });
    }

    // 获取参数下拉框
    function getSelectParams() {
        var val1 = tree1.getSelectedNodes();
        if(siteObj && val1.length>0){
            load();
            request.service({
                method: 'get',
                url: '/common/queryParameter/'+val1[0].energyTypeId
            }).then(function (res) {
                disLoad();
                paramsSelect = xmSelect.render({
                    el: '#paramId',
                    prop: {
                        name: 'energyParaName',
                        value: 'energyParaId'
                    },
                    model: {
                        label: {
                            type: 'block',
                            block: {
                                //最大显示数量, 0:不限制
                                showCount: 2,
                                //是否显示删除图标
                                showIcon: true
                            }
                        }
                    },
                    data: res.data
                });

                var str = '';
                $.each(res.data,function(ii,vv){
                    str += '<option value="'+vv.energyParaId+'" >'+vv.energyParaName+'</option>';
                });
                $("#param_add_parameter").html(str);
                form.render('select');
            }).catch(function(err){
                disLoad();
                console.log(err)
            });
        }
    }

    //报表列表
    function getTree1(){
        if(siteObj){
            load();
            request.service({
                method: 'post',
                url: '/reportManage/query',
                data: {
                    'objType': siteObj.type,
                    'objId': siteObj.id,
                    'reportName':$("#reportName").val()
                }
            }).then(function (res) {
                disLoad();
                if(res.data){
                    var list = res.data?res.data:[];
                    var setting = {
                        view: {
                            addDiyDom:null,//用于在节点上固定显示用户自定义控件
                            autoCancelSelected: false,
                            dblClickExpand: true,//双击节点时，是否自动展开父节点的标识
                            showLine: false,//设置 zTree 是否显示节点之间的连线。
                            selectedMulti: false,
                            fontCss: function (treeId, treeNode) {
                                return treeNode.isUse == "Y" ? {} : {color:"#bbb"};
                            },
                            showIcon: false
                        },
                        data: {
                            keep:{
                                leaf:false,//zTree 的节点叶子节点属性锁，是否始终保持 isParent = false
                                parent:false//zTree 的节点父节点属性锁，是否始终保持 isParent = true
                            },
                            key:{
                                children:"children",//节点数据中保存子节点数据的属性名称。
                                name:"showName",//节点数据保存节点名称的属性名称。
                                title:"id"//节点数据保存节点提示信息的属性名称
                            },
                            simpleData: {
                                enable: true,//是否采用简单数据模式 (Array)
                                idKey: "id",//节点数据中保存唯一标识的属性名称。
                                pIdKey: "parentId",//节点数据中保存其父节点唯一标识的属性名称
                                rootPId: ""//用于修正根节点父节点数据，即 pIdKey 指定的属性值。
                            }
                        },
                        callback: {
                            onClick:function(event,treeId,treeNode){
                                if(display == 'param'){
                                    getSelectParams();
                                    getParamTable();
                                }else{
                                    getObjTable();
                                }
                            }
                        }
                    };
                    tree1 = $.fn.zTree.init($("#tree"), setting, list);
                    tree1.expandAll(true);
                    var nodes = tree1.getNodes();
                    if (nodes.length>0) {
                        tree1.selectNode(nodes[0]);
                        if(display == 'param'){
                            getSelectParams();
                            getParamTable();
                        }else{
                            getObjTable();
                        }
                    }else{
                        $("#tree").html('');
                    }
                }
            }).catch(function(err){
                disLoad();
                console.log(err)
            });
        }
    }

    // 展示参数表格
    function getParamTable(){
        var paraArr = [];
        if(paramsSelect){
            var para = paramsSelect.getValue();
            $.each(para,function(i2,v2){
                paraArr.push(v2.energyParaId);
            });
        }

        var val1 = tree1.getSelectedNodes();
        if(siteObj && val1.length>0){
            load();
            request.service({
                method: 'post',
                url: '/reportParaDetail/query',
                data: {
                    'objType': siteObj.type,
                    'objId': siteObj.id,
                    'reportId': val1[0].reportId,
                    'energyParaIds':paraArr,
                    'displayName':$("#paramName").val(),
                    'energyTypeId': val1[0].energyTypeId
                }
            }).then(function (res) {
                disLoad();
                if(res.data){
                    paramTableData = res.data;
                    table.render({
                        elem: '#param_table'
                        ,height: $('.param_table').height()-20
                        ,data: paramTableData
                        ,page: false //开启分页
                        ,limit:999999
                        ,cols: [[ //表头
                            {type: "radio", width: 50, fixed: "left"}
                            ,{type: "numbers", title: '序号', width:80, align:'center'}
                            ,{field: 'energyParaName', title: '参数', align:'center',
                                templet:function(d){
                                    return '['+d.energyParaId+']'+d.energyParaName;
                                }
                            }
                            ,{field: 'displayName', title: '展示名称', align:'center'}
                            ,{field: 'timeValue', title: '时刻值', width: 100,align:'center',
                                templet:function(d){
                                    if(d.timeValue == 'Y'){
                                        return '<input type="checkbox" checked lay-skin="primary">';
                                    }else{
                                        return '<input type="checkbox" lay-skin="primary">';
                                    }
                                }}
                            ,{field: 'maxValue', title: '最大值', width: 100,align:'center',
                                templet:function(d){
                                    if(d.maxValue == 'Y'){
                                        return '<input type="checkbox" checked lay-skin="primary">';
                                    }else{
                                        return '<input type="checkbox" lay-skin="primary">';
                                    }
                                }}
                            ,{field: 'minValue', title: '最小值', width: 100,align:'center',
                                templet:function(d){
                                    if(d.minValue == 'Y'){
                                        return '<input type="checkbox" checked lay-skin="primary">';
                                    }else{
                                        return '<input type="checkbox" lay-skin="primary">';
                                    }
                                }}
                            ,{field: 'avgValue', title: '平均值', width: 100,align:'center',
                                templet:function(d){
                                    if(d.avgValue == 'Y'){
                                        return '<input type="checkbox" checked lay-skin="primary">';
                                    }else{
                                        return '<input type="checkbox" lay-skin="primary">';
                                    }
                                }}
                            ,{field: 'diffValue', title: '差值', width: 100,align:'center',
                                templet:function(d){
                                    if(d.diffValue == 'Y'){
                                        return '<input type="checkbox" checked lay-skin="primary">';
                                    }else{
                                        return '<input type="checkbox" lay-skin="primary">';
                                    }
                                }}
                            ,{field: 'sortId', title: '排序标识', width: 100,align:'center'}
                            ,{field: 'memo', title: '备注', width: 100,align:'center'}
                        ]]
                    });
                }
            }).catch(function(err){
                disLoad();
                console.log(err)
            });
        }
    }

    // 展示对象表格
    function getObjTable(){
        var val1 = tree1.getSelectedNodes();
        if(siteObj && val1.length>0){
            load();
            request.service({
                method: 'post',
                url: '/reportObjDetail/query',
                data: {
                    'objType': siteObj.type,
                    'objId': siteObj.id,
                    'reportId': val1[0].reportId
                }
            }).then(function (res) {
                disLoad();
                if(res.data){
                    renderSelect(renderObjList(res.data));
                    objTableData = [];
                    renderObjTableData(res.data);
                    treeGrid.render({
                        id:'object_table'
                        ,elem: '#object_table'
                        ,height: $('.obj_table').height()-20
                        ,idField:'nodeId'
                        ,cellMinWidth: 100
                        ,iconOpen:false//是否显示图标【默认显示】
                        ,treeId:'nodeId'//树形id字段名称
                        ,treeUpId:'parentId'//树形父id字段名称
                        ,treeShowName:'nodeName'//以树形式显示的字段
                        ,isFilter:false
                        ,isOpenDefault:true//节点默认是展开还是折叠【默认展开】
                        ,loading:true
                        ,isPage:false
                        ,cols: [[
                            {type: "radio", width: 50, fixed: "left"}
                            ,{type: "numbers", title: '序号', width:80, fixed: 'left',align:'center'}
                            ,{field: 'nodeName', title: '节点名称', width:200}
                            ,{field: 'nodeId', title: '节点ID', width:80,align:'center'}
                            ,{field: 'parentId', title: '父节点ID', width:180,align:'center'}
                            ,{field: 'sortId', title: '排序', width:80,align:'center'}
                            ,{field: 'dataSource', title: '数据源', width: 450,align:'center'}
                            ,{field: 'memo', title: '备注', width: 100,align:'center'}
                            ,{field: 'createUserId', title: '创建者', width: 120,align:'center'}
                            ,{field: 'createTime', title: '创建时间', width: 180,align:'center'}
                            ,{field: 'updateUserId', title: '修改者', width: 120,align:'center'}
                            ,{field: 'updateTime', title: '修改时间', width: 180,align:'center'}
                        ]]
                        ,data:objTableData
                        ,page:false
                        ,limit:999999
                    });
                }
            }).catch(function(err){
                disLoad();
                console.log(err)
            });
        }
    }

    function renderObjList(arr){
        $.each(arr,function(i,v){
            v.title = '['+v.nodeId+']'+v.nodeName;
            v.parentTitle = '['+v.parentId+']'+v.parentName;
            v.spread = true;
            if(v.children && v.children.length>0){
                renderObjList(v.children);
            }
        });
        return arr;
    }

    function renderObjTableData(data){
        $.each(data,function(i2,v2){
            v2.parentTitle = '['+v2.parentId+']'+v2.parentName;
            v2.title = '['+v2.nodeId+']'+v2.nodeName;
            objTableData.push(v2);
            if(v2.children && v2.children.length>0){
                renderObjTableData(v2.children);
            }
        });
    }

    function funcsAuthority(){
        getEnergyType();
        getParkOrSite();
        getTree1();
    }

    var indexLoading;
    function load(){
        indexLoading =layer.load(1, {shade: [0.3, '#fff']});
    }
    function disLoad(){
        layer.close(indexLoading);
    }
});