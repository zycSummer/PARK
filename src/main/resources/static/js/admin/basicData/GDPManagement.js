/*
 * @Author: jpp
 * @Date: 2019-11-18 10:38:00
 * @Last Modified by: jpp
 * @Last Modified time: 2019-12-11 15:12:30
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
    var pageNum =1;
    var pageLimit;

    var date = new Date();
    $("#timeInput1").val(date.getFullYear() + '-01');
    $("#timeInput2").val(date.getFullYear() + '-12');
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
        ,value: date.getFullYear() + '-12'
        ,btns: ['now', 'confirm']
    });

    getTableData();

    //查询点击事件
    $(".query").on("click", function () {
        getTableData()
    });

    //新增点击事件
    $(".add").on('click',function(){
        $(".edit_yearMonth").addClass('layui-hide');
        $(".add_yearMonth").removeClass('layui-hide');

        var date = new Date();
        var lastMonth = formateDate(date);
        $("#yearMonth").val(lastMonth);//年月默认上一月
        laydate.render({
            elem: '#yearMonth'
            ,type: 'month'
            ,lang: 'cn'
            ,value: lastMonth
            ,btns: ['now', 'confirm']
        });

        $("#GDP").val('');
        $("#addValue").val('');
        $("#memo").val('');

        showModel("新增GDP")
    });

    //修改点击事件
    $(".edit").on('click',function(){
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
        var M = editData.month < 10?'0'+editData.month:editData.month;
        $("#edit_yearMonth").html(editData.year+'-'+M);
        $("#GDP").val(editData.gdp);
        $("#addValue").val(editData.addValue);
        $("#memo").val(editData.memo);

        showModel("修改GDP",editData.id)
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
        var M = tableSelect[0].month < 10?'0'+tableSelect[0].month:tableSelect[0].month;
        $("#delete_yearMonth").html(tableSelect[0].year+'-'+M);
        $("#delete_GDP").html(tableSelect[0].gdp);
        $("#delete_addValue").html(tableSelect[0].addValue);

        layer.open({
            type: 1,
            title: '删除GDP',
            closeBtn: 1,
            shade: 0,
            anim: 1,
            maxmin: true, //开启最大化最小化按钮
            area: ['400px', '350px'],
            content: $('#GDP_delete'),
            btn: ['确定', '取消'],
            success: function () {
                $("#GDP_delete").removeClass('layui-hide');
            },
            yes: function (index) {
                load();
                var tableSelect = table.checkStatus('table1').data;
                request.service({
                    method: 'get',
                    url: '/gdpMonthly/delete/'+tableSelect[0].id
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
                $("#GDP_delete").addClass('layui-hide');
            }
        });
    });

    //新增修改 提示点击 - GDP
    $("#GDP_tip").on("click", function () {
        layer.tips('单位：万元', '#GDP_tip');
    });

    //新增修改 提示点击 - 工业增加值
    $("#addValue_tip").on("click", function () {
        layer.tips('单位：万元', '#addValue_tip');
    });

    //监听提交
    form.on('submit(*)', function (data) {
        return false;//阻止表单跳转
    });

    /*----------------------------------函数------------------------------------*/
    function getTableData(){
        var time = $("#timeInput1").val();
        var start = moment(time,'YYYY-MM').valueOf();
        var time2 = $("#timeInput2").val();
        var end = moment(time2,'YYYY-MM').valueOf();

        var tnum = parseInt(($(".GDP_table").height() - 90) / 42);  //动态生成表格展示条数
        load();
        request.service({
            method: 'post',
            url: '/gdpMonthly/query',
            data: {
                'limit': pageLimit ? pageLimit : tnum,
                'page': pageNum,
                'key':{
                    'objType': siteObj.type,
                    'objId': siteObj.id,
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
                    field: 'year',
                    align:"center",
                    title: '年'
                }, {
                    field: 'month',
                    align:"center",
                    title: '月'
                }, {
                    field: 'gdp',
                    align:"center",
                    title: 'GDP(万元)'
                }, {
                    field: 'addValue',
                    align:"center",
                    title: '工业增加值(万元)'
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
            area: ['450px', '350px'],
            content: $('#GDP_add'),
            btn: ['确定', '取消'],
            success: function () {
                $('#GDP_add').removeClass('layui-hide').addClass('layui-show');
            },
            yes: function (index) {
                var url = '';
                if(editId){
                    var tableSelect = table.checkStatus('table1').data;
                    var data = {
                        "objType": siteObj.type,
                        "objId": siteObj.id,
                        "id": tableSelect[0].id,
                        "year": tableSelect[0].year,
                        "month": tableSelect[0].month,
                        'gdp':$("#GDP").val(),
                        'addValue':$("#addValue").val(),
                        "memo":$("#memo").val()
                    };
                    url = '/gdpMonthly/edit';
                }else{
                    var time = $("#yearMonth").val();
                    var date = new Date(moment(time,'YYYY-MM').valueOf());
                    var year = date.getFullYear();
                    var month = date.getMonth()+1;

                    var data = {
                        "objType": siteObj.type,
                        "objId": siteObj.id,
                        "year": year,
                        "month":month,
                        'gdp':$("#GDP").val(),
                        'addValue':$("#addValue").val(),
                        "memo":$("#memo").val()
                    };
                    url = '/gdpMonthly/add';
                }

                if(data.gdp == ''){
                    return layer.msg('请输入GDP!');
                }
                if(data.addValue == ''){
                    return layer.msg('请输入工业增加值!');
                }

                load();
                request.service({
                    method: 'post',
                    url: url,
                    data: data
                }).then(function (res) {
                    disLoad();
                    layer.msg(res.msg);
                    layer.close(index);
                    getTableData();
                }).catch(function(err){
                    disLoad();
                    console.log(err)
                });
            },
            end: function (index) {
                $('#GDP_add').removeClass('layui-show').addClass('layui-hide');
            }
        });
    }

    function formateDate(date) {
        var y = date.getFullYear();
        var M = date.getMonth() + 1;
        M = M-1;
        if(M > 0){
            M = M < 10 ? ('0' + M) : M;
            return y+'-'+M;
        }else{
            return (y-1)+'-12';
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