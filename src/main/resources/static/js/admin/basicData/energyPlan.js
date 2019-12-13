/*
 * @Author: jpp
 * @Date: 2019-11-18 10:38:00
 * @Last Modified by: xzl
 * @Last Modified time: 2019-11-26 15:08:58
 */

layui.use(['form', 'element', 'layer', 'table','laydate', 'jquery','laypage'], function () {
    var form = layui.form,
        layer = layui.layer,
        $ = layui.jquery,
        laydate = layui.laydate,
        table = layui.table,
        laypage = layui.laypage,
        element = layui.element;

    var siteObj = JSON.parse(sessionStorage.getItem('parkId'));
    var energyTypeSelect;
    var pageNum =1;
    var pageLimit;

    var date = new Date();
    $("#timeInput1").val(date.getFullYear() + '-01');
    $("#timeInput2").val(date.getFullYear()+1 + '-12');
    laydate.render({
        elem: '#timeInput1'
        ,type: 'month'
        ,lang: 'cn'
        ,value: date.getFullYear() + '-01'
        ,btns: ['now', 'confirm']
    });
    laydate.render({
        elem: '#timeInput2'
        ,type: 'month'
        ,lang: 'cn'
        ,value: date.getFullYear()+1 + '-12'
        ,btns: ['now', 'confirm']
    });

    getEnergyType();

    //查询点击事件
    $(".query").on("click", function () {
        getTableData();
    });

    //新增点击事件
    $(".add").on('click',function(){
        $(".edit_energyTypeId").addClass('layui-hide');
        $(".add_energyTypeId").removeClass('layui-hide');
        $(".edit_yearMonth").addClass('layui-hide');
        $(".add_yearMonth").removeClass('layui-hide');

        $("#energyTypeId").val('');
        form.val("plan_add", {
            "energyTypeId": $("#energyTypeId option:first-child").val()
        });
        var date = new Date();
        var nextMonth = formateDate(date);
        $("#yearMonth").val(nextMonth);//年月默认下一月
        laydate.render({
            elem: '#yearMonth'
            ,type: 'month'
            ,lang: 'cn'
            ,value: nextMonth
            ,btns: ['now', 'confirm']
        });
        $("#usage").val('');
        $("#memo").val('');

        showModel("新增用能计划")
    });

    //修改点击事件
    $(".edit").on('click',function(){
        $(".edit_energyTypeId").removeClass('layui-hide');
        $(".add_energyTypeId").addClass('layui-hide');
        $(".edit_yearMonth").removeClass('layui-hide');
        $(".add_yearMonth").addClass('layui-hide');

        var tableSelect = table.checkStatus('table1').data;
        if(tableSelect.length === 0){
            return layer.msg("请选择需要修改的数据！")
        }
        if(tableSelect.length > 1){
            return layer.msg("只能选择单条数据！")
        }
        var editData = tableSelect[0];
        $("#edit_energyTypeId").html(editData.energyTypeName);
        form.val("plan_add", {
            "energyTypeId": editData.energyTypeId
        });
        var M = editData.month < 10?'0'+editData.month:editData.month;
        $("#edit_yearMonth").html(editData.year+'-'+M);
        $("#usage").val(editData.usage);
        $("#memo").val(editData.memo);

        showModel("修改用能计划",editData.id)
    });

    //删除点击事件
    $('.delete').click(function () {
        var tableSelect = table.checkStatus('table1').data;
        if(tableSelect.length == 0){
            return layer.msg('请选择需要删除的数据')
        }
        if(tableSelect.length > 1){
            return layer.msg("只能选择单条数据！")
        }
        $("#delete_energyTypeId").html(tableSelect[0].energyTypeName);
        var M = tableSelect[0].month < 10?'0'+tableSelect[0].month:tableSelect[0].month;
        $("#delete_yearMonth").html(tableSelect[0].year+'-'+M);
        $("#delete_usage").html(tableSelect[0].usage);

        layer.open({
            type: 1,
            title: '删除用能计划',
            closeBtn: 1,
            shade: 0,
            anim: 1,
            maxmin: true, //开启最大化最小化按钮
            area: ['400px', '350px'],
            content: $('#plan_delete'),
            btn: ['确定', '取消'],
            success: function () {
                $("#plan_delete").removeClass('layui-hide');
            },
            yes: function (index) {
                load();
                var tableSelect = table.checkStatus('table1').data;
                request.service({
                    method: 'get',
                    url: '/energyMonthlyUsagePlan/delete/'+tableSelect[0].id
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
                $("#plan_delete").addClass('layui-hide');
            }
        });
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
        var time = $("#timeInput1").val();
        var start = moment(time,'YYYY-MM').valueOf();
        var time2 = $("#timeInput2").val();
        var end = moment(time2,'YYYY-MM').valueOf();

        var tnum = parseInt(($(".plan_table").height() - 90) / 42);  //动态生成表格展示条数
        load();
        request.service({
            method: 'post',
            url: '/energyMonthlyUsagePlan/query',
            data: {
                'limit': pageLimit ? pageLimit : tnum,
                'page': pageNum,
                'key':{
                    'objType': siteObj.type,
                    'objId': siteObj.id,
                    'energyTypeIds':arr,
                    'start': start,
                    'end': end
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
                    type: "radio",
                    width: 50,
                    fixed: "left"
                }, {
                    type: "numbers",
                    width: 50,
                    title: '序号'
                }, {
                    field: "energyTypeName",
                    width: 100,
                    align:"center",
                    title: '能源种类'
                }, {
                    field: 'year',
                    align:"center",
                    title: '年'
                }, {
                    field: 'month',
                    align:"center",
                    title: '月'
                }, {
                    field: 'usage',
                    align:"center",
                    title: '计划用量'
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

    function showModel(title,editId) {//模态框调用事件
        layer.open({
            type: 1,
            title: title,
            closeBtn: 1,
            shade: 0.3,
            maxmin: true,
            anim: 1,
            area: ['450px', '400px'],
            content: $('#plan_add'),
            btn: ['确定', '取消'],
            success: function () {
                $('#plan_add').removeClass('layui-hide').addClass('layui-show');
            },
            yes: function (index) {
                var url = '';
                if(editId){
                    var tableSelect = table.checkStatus('table1').data;
                    var data = {
                        "objType": siteObj.type,
                        "objId": siteObj.id,
                        "id": tableSelect[0].id,
                        "energyTypeId": tableSelect[0].energyTypeId,
                        "year": tableSelect[0].year,
                        "month":tableSelect[0].month,
                        'usage':$("#usage").val(),
                        "memo":$("#memo").val()
                    };
                    url = '/energyMonthlyUsagePlan/edit';
                }else{
                    var time = $("#yearMonth").val();
                    var date = new Date(moment(time,'YYYY-MM').valueOf());
                    var year = date.getFullYear();
                    var month = date.getMonth()+1;

                    var data = {
                        "objType": siteObj.type,
                        "objId": siteObj.id,
                        "energyTypeId": $("#energyTypeId").val(),
                        "year": year,
                        "month":month,
                        'usage':$("#usage").val(),
                        "memo":$("#memo").val()
                    };
                    url = '/energyMonthlyUsagePlan/add';

                    if(data.energyTypeId == '') return layer.msg('能源种类不能为空！');
                    if(data.year == '') return layer.msg('年不能为空！');
                    if(data.month == '') return layer.msg('月不能为空！');
                }
                if(data.usage == '') return layer.msg('计划用量不能为空！');

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
                $('#plan_add').removeClass('layui-show').addClass('layui-hide');
            }
        });
    }

    function formateDate(date) {
        var y = date.getFullYear();
        var M = date.getMonth() + 1;
        M = M+1;
        if(M > 0){
            M = M < 10 ? ('0' + M) : M;
            return y+'-'+M;
        }else{
            return (y+1)+'-12';
        }
    }

    var indexLoading;
    function load(){
        indexLoading =layer.load(1, {shade: [0.3, '#fff']});
    }
    function disLoad(){
        layer.close(indexLoading);
    }

});