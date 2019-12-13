/*
 * @Author: jpp
 * @Date: 2019-10-30 10:38:00
 * @Last Modified by: xzl
 * @Last Modified time: 2019-11-28 16:36:10
 */

layui.use(['form', 'element', 'layer', 'table', 'jquery','laypage'], function () {
    var form = layui.form,
        layer = layui.layer,
        $ = layui.jquery,
        table = layui.table,
        laypage = layui.laypage,
        element = layui.element;

    var siteObj = JSON.parse(sessionStorage.getItem('parkId'));
    var pageNum =1;
    var pageLimit;

    getFileTableData();

    //查询点击事件
    $("#search_btn").on("click", function () {
        getFileTableData()
    });

    //新增点击事件
    $("#add_btn").on('click',function(){
        $("#add_description").val('');
        $("#memo").val('');
        $("#upload_file").val('');

        $("#add_file_name").parent().parent().addClass('layui-hide');
        $("#upload_file").parent().parent().removeClass('layui-hide');

        showModel("新增")
    });

    //修改点击事件
    $("#edit_btn").on('click',function(){
        var tableSelect = table.checkStatus('file_table').data;
        if(tableSelect.length === 0){
            return layer.msg("请选择需要修改的数据！")
        }
        if(tableSelect.length > 1){
            return layer.msg("只能选择单条数据！")
        }
        var editData = tableSelect[0];
        $("#add_file_name").html(editData.fileName);
        $("#add_description").val(editData.fileDesc);
        $("#memo").val(editData.memo);

        $("#add_file_name").parent().parent().removeClass('layui-hide');
        $("#upload_file").parent().parent().addClass('layui-hide');

        showModel("修改",editData.id)
    });

    //删除点击事件
    $('#del_btn').click(function () {
        var tableSelect = table.checkStatus('file_table').data;
        if(tableSelect.length == 0){
            return layer.msg('请选择需要删除的数据')
        }
        if(tableSelect.length > 1){
            return layer.msg("只能选择单条数据！")
        }

        layer.confirm('确认删除此条记录', {
            icon: 3,
            title: '确认删除？'
        }, function (index) {
            load();
            request.service({
                method: 'get',
                url: '/documentManagement/delete/'+tableSelect[0].id
            }).then(function (res) {
                disLoad();
                layer.close(index);
                layer.msg("删除成功");
                getFileTableData();
            }).catch(function(err){
                disLoad();
                console.log(err);
            });
        });
    });

    //下载
    $("#download_btn").on('click',function(){
        var tableSelect = table.checkStatus('file_table').data;
        if(tableSelect.length == 0){
            return layer.msg('请选择需要下载的数据')
        }
        if(tableSelect.length > 1){
            return layer.msg("只能选择单条数据！")
        }
        load();
        request.service({
            method: 'get',
            url: '/documentManagement/download/'+tableSelect[0].id
        }).then(function (result) {
            disLoad();
            // //处理文件流并下载
            // var blob = new Blob([result], {type: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8"}),
            // Temp = document.createElement("a");
            // Temp.href = window.URL.createObjectURL(blob);
            // Temp.download =tableSelect[0].fileName
            // $('body').append(Temp);
            // Temp.click();
            var url = '/documentManagement/download/'+tableSelect[0].id;
            window.location.href = encodeURI(url);
   
        }).catch(function(err){
            disLoad();
            console.log(err);
        });


       
    });

    //监听提交
    form.on('submit(*)', function (data) {
        return false;//阻止表单跳转
    });

    /*----------------------------------函数------------------------------------*/
    function getFileTableData(){
        var tnum = parseInt(($(".table_content").height() - 90) / 42);  //动态生成表格展示条数
        load();
        request.service({
            method: 'post',
            url: '/documentManagement/query',
            data: {
                'limit': pageLimit ? pageLimit : tnum,
                'page': pageNum,
                'key':{
                    'objType': siteObj.type,
                    'objId': siteObj.id,
                    'fileName': $("#file_name").val()
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
            elem: '#file_table',
            id:'file_table',
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
                    field: 'fileName',
                    align:"center",
                    title: '文件名'
                }, {
                    field: 'fileDesc',
                    align:"center",
                    title: '描述'
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
                    getFileTableData();
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
            area: ['600px', '400px'],
            content: $('#file_add'),
            btn: ['保存', '关闭'],
            success: function () {
                $('#file_add').removeClass('layui-hide').addClass('layui-show');
            },
            yes: function (index) {
                var description = $("#add_description").val();
                var memo = $("#memo").val();

                if(!description){
                    return layer.msg('请输入描述');
                }

                var url = '';
                if(editId){
                    var formData = new FormData();
                    formData.append("fileDesc", description);
                    formData.append("memo", memo);
                    formData.append("id", editId);
                    url = '/documentManagement/edit';
                }else{
                    var formData = new FormData($("#file_add .layui-form")[0]);
                    formData.append("objType", siteObj.type);
                    formData.append("objId", siteObj.id);
                    formData.append("fileDesc", description);
                    formData.append("memo", memo);
                    url = '/documentManagement/add';

                    var file = formData.get('file');
                    if(file.size == 0){
                        return layer.msg('请上传文件！');
                    }
                }

                load();
                request.service({
                    method: 'post',
                    url: url,
                    data: formData
                }).then(function (res) {
                    disLoad();
                    layer.msg(res.msg);
                    layer.close(index);
                    getFileTableData();
                }).catch(function(err){
                    disLoad();
                    console.log(err)
                });
            },
            end: function (index) {
                $('#file_add').removeClass('layui-show').addClass('layui-hide');
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