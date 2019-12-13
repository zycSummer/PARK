/*
 * @Author: jpp
 * @Date: 2019-11-25 10:38:00
 * @Last Modified by: xzl
 * @Last Modified time: 2019-11-26 15:15:31
 */

layui.use(['form', 'element', 'layer', 'table', 'jquery','laypage'], function () {
    var form = layui.form,
        layer = layui.layer,
        $ = layui.jquery,
        table = layui.table,
        laypage = layui.laypage,
        element = layui.element;

    var siteObj = JSON.parse(sessionStorage.getItem('parkId'));
    var energyTypeSelect;
    var pageNum =1;
    var pageLimit;

    getEnergyType();

    //查询点击事件
    $(".query").on("click", function () {
        getTableData();
    });

    //新增点击事件
    $(".add").on('click',function(){
        $(".edit_meterId").addClass('layui-hide');
        $(".add_meterId").removeClass('layui-hide');

        $("#meterId").val('');
        $("#meterName").val('');
        $("#energyTypeId").val($("#energyTypeId option:first-child").val());
        $("#sortId").val('');
        $("#isRanking").attr('checked',false);
        $("#memo").val('');
        form.val("meter_add", {
            "energyTypeId": $("#energyTypeId option:first-child").val(),
            "isRanking":false
        });

        addModel("新增仪表")
    });

    //修改点击事件
    $(".edit").on('click',function(){
        $(".edit_meterId").removeClass('layui-hide');
        $(".add_meterId").addClass('layui-hide');

        var tableSelect = table.checkStatus('table1').data;
        if(tableSelect.length === 0){
            return layer.msg("请选择需要修改的数据！")
        }
        if(tableSelect.length > 1){
            return layer.msg("只能选择单条数据！")
        }
        var editData = tableSelect[0];
        $("#edit_meterId").html(editData.meterId);
        $("#meterName").val(editData.meterName);
        $("#energyTypeId").val(editData.energyTypeId);
        $("#sortId").val(editData.sortId);
        $("#isRanking").attr('checked',editData.isRanking);
        $("#memo").val(editData.memo);

        form.val("meter_add", {
            "energyTypeId": editData.energyTypeId,
            "isRanking":editData.isRanking
        });

        addModel("修改仪表",editData.id)
    });

    //删除点击事件
    $('.delete').click(function () {
        var checkStatus = table.checkStatus('table1'); //idTest 即为基础参数 id 对应的值
        var tableSelect = checkStatus.data;
        if(tableSelect.length == 0){
            return layer.msg('请选择需要删除的数据')
        }
        deletes();
    });

    //导入
    $(".import").on('click',function(){
        $('#import_object').html(siteObj.title);
        importModel();
    });
    //导入弹框 模板文件下载
    $("#meter_download_demo").on('click',function(){
        var url = '/meter/download';
        window.location.href = encodeURI(url);
    });
    //导出
    $('.export').on('click',function(){
        var arr = [];
        if(energyTypeSelect){
            var para = energyTypeSelect.getValue();
            $.each(para,function(i2,v2){
                arr.push(v2.energyTypeId);
            });
        }

        var url = '/meter/exportExcel?objType='+siteObj.type+ '&objId='+siteObj.id+
            '&energyTypeId='+arr+'&meterId='+$("#search_meterId").val()+
            '&meterName='+$("#search_meterName").val();
        window.location.href = encodeURI(url);
    });

    //新增弹框 提示点击 - 仪表标识
    $("#meterId_tip").on("click", function () {
        layer.tips('字母、数字组合，长度不超过20。\n' + '和实时库中的仪表标识对应。', '#meterId_tip');
    });
    //新增弹框 提示点击 - 仪表名称
    $("#meterName_tip").on("click", function () {
        layer.tips('长度不超过30', '#meterName_tip');
    });
    //新增弹框 提示点击 - 排序标识
    $("#sortId_tip").on("click", function () {
        layer.tips('字母、数字组合，长度不超过10，系统按照字符串升序。', '#sortId_tip');
    });
    //新增弹框 提示点击 - 参与负荷排名
    $("#isRanking_tip").on("click", function () {
        layer.tips('设置为是，将在 项目能耗 >  负荷排名 子模块参与排名。', '#isRanking_tip');
    });
    //删除弹框 提示点击
    $("#delete_tip").on("click", function () {
        layer.tips('只删除仪表信息表中的信息，不删除其他关联信息。', '#delete_tip');
    });

    //监听提交
    form.on('submit(*)', function (data) {
        return false;//阻止表单跳转
    });

    /*----------------------------------函数------------------------------------*/
    //能源种类获取
    function getEnergyType() {
        if(siteObj){
            load();
            request.service({
                method: 'get',
                url: '/common/queryHistoryLeftData'
            }).then(function (res) {
                disLoad();
                energyTypeSelect = xmSelect.render({
                    el: '#searchEnergyTypeId',
                    clickClose: true,
                    prop: {
                        name: 'energyTypeName',
                        value: 'energyTypeId'
                    },
                    data:res.data
                });

                var str = '',arr = [];
                $.each(res.data,function(ii,vv){
                    arr.push(vv.energyTypeId);
                    str += '<option value="'+vv.energyTypeId+'" >'+vv.energyTypeName+'</option>';
                });
                $("#energyTypeId").html(str);
                form.render('select');

                energyTypeSelect.setValue(arr);

                getTableData();
            }).catch(function(err){
                disLoad();
                console.log(err)
            });
        }
    }

    function getTableData(){
        var arr = [];
        if(energyTypeSelect){
            var para = energyTypeSelect.getValue();
            $.each(para,function(i2,v2){
                arr.push(v2.energyTypeId);
            });
        }
        var tnum = parseInt(($(".meter_table").height() - 90) / 42);  //动态生成表格展示条数
        load();
        request.service({
            method: 'post',
            url: '/meter/queryMeter',
            data: {
                'limit': pageLimit ? pageLimit : tnum,
                'page': pageNum,
                'key':{
                    'objType': siteObj.type,
                    'objId': siteObj.id,
                    'energyTypeId':arr,
                    'meterId': $("#search_meterId").val(),
                    'meterName': $("#search_meterName").val()
                }
            }
        }).then(function (res) {
            disLoad();
            renderTableContent(res.data,pageLimit ? pageLimit : tnum);
            renderPage(res.count,pageLimit ? pageLimit : tnum);
        }).catch(function(err){
            disLoad();
            console.log(err)
        });
    }

    //渲染表格数据
    function renderTableContent(data,limit) {
        table.render({
            elem: '#table1',
            id:'table1',
            height:'full-150',
            cols: [
                [{
                    type: "checkbox",
                    width: 50,
                    fixed: "left"
                }, {
                    type: "numbers",
                    width: 50,
                    title: '序号'
                }, {
                    field: "meterId",
                    align:"center",
                    width: 90,
                    title: '仪表标识'
                }, {
                    field: 'meterName',
                    width: 160,
                    align:"center",
                    title: '仪表名称'
                }, {
                    field: 'energyTypeName',
                    align:"center",
                    title: '能源种类'
                }, {
                    field: 'sortId',
                    align:"center",
                    title: '排序标识'
                }, {
                    field: 'isRanking',
                    align:"center",
                    title: '是否参与负荷排名',
                    templet:function(d){
                        if(d.isRanking == true){
                            return '是';
                        }else{
                            return '否';
                        }
                    }
                }, {
                    field: 'memo',
                    align:"center",
                    title: '备注'
                }, {
                    field: 'createUserId',
                    align:"center",
                    title: '创建者'
                }, {
                    field: 'createTime',
                    align:"center",
                    title: '创建时间'
                }, {
                    field: 'updateUserId',
                    align:"center",
                    title: '修改者',
                    width: 135
                }, {
                    field: 'updateTime',
                    align:"center",
                    title: '修改时间',
                    width: 135

                }]
            ],
            data: data,
            page:false,
            limit:limit
        })

    }
    //渲染分页模块
    function renderPage(count,tnum) {
        laypage.render({
            elem: 'table_page',
            count: count, //数据总数，从服务端得到
            curr: pageNum,
            limit: pageLimit ? pageLimit : tnum,
            limits:[tnum,20,30,50],
            layout: ['count', 'prev', 'page', 'next', 'limit', 'skip'],
            jump: function (obj, first) {
                //首次不执行
                if (!first) {
                    pageNum =obj.curr;
                    pageLimit =obj.limit;
                    getTableData();
                }
            }
        });

    }

    function addModel(title,editId) {//模态框调用事件
        layer.open({
            type: 1,
            title: title,
            closeBtn: 1,
            shade: 0.3,
            maxmin: true,
            anim: 1,
            area: ['450px', '500px'],
            content: $('#meter_add'),
            btn: ['确定', '取消'],
            success: function () {
                $('#meter_add').removeClass('layui-hide').addClass('layui-show');
            },
            yes: function (index) {
                var isRankingArr = $('#isRanking:checked');
                var isRanking = false;
                if(isRankingArr.length > 0){
                    isRanking = true;
                }

                var url = '';
                if(editId){
                    var checkStatus = table.checkStatus('table1'); //idTest 即为基础参数 id 对应的值
                    var tableCheckedData = checkStatus.data;
                    var data = {
                        "objType":siteObj.type,
                        "objId":siteObj.id,
                        "id":tableCheckedData[0].id,
                        "meterId":tableCheckedData[0].meterId,
                        "meterName":$("#meterName").val(),
                        "energyTypeId":$("#energyTypeId").val(),
                        "sortId":$("#sortId").val(),
                        "isRanking":isRanking,
                        "memo":$("#memo").val()
                    };
                    url = '/meter/edit';
                }else{
                    var data = {
                        "objType":siteObj.type,
                        "objId":siteObj.id,
                        "meterId":$("#meterId").val(),
                        "meterName":$("#meterName").val(),
                        "energyTypeId":$("#energyTypeId").val(),
                        "sortId":$("#sortId").val(),
                        "isRanking":isRanking,
                        "memo":$("#memo").val()
                    };
                    url = '/meter/add';

                    if(data.meterId == '') return layer.msg('仪表标识不能为空！');
                    if(data.meterId.length > 20) return layer.msg('仪表标识长度不能超过20！');
                    var re = /^[0-9a-zA-Z]+$/g;
                    if(!(re.test(data.meterId))){
                        return layer.msg('仪表标识为字母、数字组合！');
                    }
                }

                if(data.meterName == '') return layer.msg('仪表名称不能为空！');
                if(data.meterName.length > 30) return layer.msg('仪表名称长度不能超过30！');
                if(data.sortId.length > 10) return layer.msg('排序标识长度不能超过10！');
                var re2 = /^[0-9a-zA-Z]+$/g;
                if(data.sortId.length > 0 && !(re2.test(data.sortId))){
                    return layer.msg('排序标识为字母、数字组合！');
                }

                load();
                request.service({
                    method: 'post',
                    url: url,
                    data: data
                }).then(function (res) {
                    disLoad();
                    layer.close(index);
                    getTableData();
                }).catch(function(err){
                    disLoad();
                    console.log(err)
                });
            },
            end: function (index) {
                $('#meter_add').removeClass('layui-show').addClass('layui-hide');
            }
        });
    }

    // 删除仪表
    function deletes(){
        layer.open({
            type: 1,
            title: '删除仪表',
            closeBtn: 1,
            shade: 0,
            anim: 1,
            maxmin: true, //开启最大化最小化按钮
            area: ['400px', '200px'],
            content: $('#meter_delete'),
            btn: ['确定', '取消'],
            success: function () {
                $("#meter_delete").removeClass('layui-hide');
            },
            yes: function (index) {
                var checkStatus = table.checkStatus('table1'); //idTest 即为基础参数 id 对应的值
                var tableCheckedData = checkStatus.data;
                var arr = [];
                $.each(tableCheckedData,function (ii,vv) {
                    arr.push(vv.id);
                });
                load();
                request.service({
                    method: 'post',
                    url: '/meter/delete',
                    data:arr
                }).then(function (res) {
                    disLoad();
                    getTableData();
                    layer.close(index);
                }).catch(function(err){
                    disLoad();
                    console.log(err)
                });
            },
            end: function(index){
                $("#meter_delete").addClass('layui-hide');
            }
        });
    }

    // 导入仪表
    function importModel(){
        layer.open({
            type: 1,
            title: '导入仪表',
            closeBtn: 1,
            shade: 0.3,
            maxmin: true,
            anim: 1,
            area: ['450px', '400px'],
            content: $('#meter_import'),
            btn: ['确定', '取消'],
            success: function () {
                $('#meter_import').removeClass('layui-hide').addClass('layui-show');
            },
            yes: function (index) {
                var formData = new FormData($("#meter_import .layui-form")[0]);

                var file = formData.get('file');
                if(file.size == 0){
                    return layer.msg('请上传文件！');
                }

                formData.append("objType", siteObj.type);
                formData.append("objId", siteObj.id);
                load();
                request.service({
                    method: 'post',
                    url: '/meter/importExcel',
                    data: formData
                }).then(function (res) {
                    disLoad();
                    layer.close(index);
                    getTableData();
                }).catch(function(err){
                    disLoad();
                    console.log(err)
                });
            },
            end: function (index) {
                $('#meter_import').removeClass('layui-show').addClass('layui-hide');
            }
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