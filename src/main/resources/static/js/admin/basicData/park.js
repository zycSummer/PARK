/*
 * @Author: xzl 
 * @Date: 2019-11-15 14:42:23 
 * @Last Modified by: xzl
 * @Last Modified time: 2019-12-02 15:21:40
 */

layui.use(['form', 'element', 'layer', 'table', 'jquery', 'laypage', 'tree'], function () {
    var form = layui.form,
        layer = layui.layer,
        $ = layui.jquery,
        table = layui.table,
        laypage = layui.laypage,
        tree = layui.tree,
        element = layui.element;
    
    var indexLoading;
    function load() { //加载事件
        indexLoading = layer.load(1, {
            shade: [0.3, '#fff']
        });
    }

    function disLoad() { //取消加载事件
        layer.close(indexLoading);
    }
    //查询点击事件
    $("#search_btn").on("click", function () {
        getParkDataTab()
    })

    //初始函数
    function initFun() {
        getParkDataTab();
    }

    function getParkDataTab() { //ajax获取 数据
       
        var parkId = $("#search_parkId").val();
        var parkName = $("#search_parkName").val();
   
        load();
        request.service({
                method: 'post',
                url: '/park/queryPark',
                data:{
                      'parkId':  parkId,
                    'parkName':parkName
                }
            })
            .then(function (res) {
                disLoad();
                var data = res.data;
                renderTableContent(data);
            })
            .catch(err => {
                console.log(err)
            })

    }

    //渲染表格数据
    function renderTableContent(data) {
        table.render({
            elem: '#park_table',
            height: 'full-155',
            cols: [
                [ {
                    type: 'radio',
                    fixed: 'left'
                },
                    {
                    type: 'numbers',
                    title: '序号'
                },{
                        field: 'parkId',
                        align: "center",
                        title: '园区标识'
                    },
                    {
                        field: 'parkName',
                        align: "center",
                        title: '园区名称'

                    },

                    {
                        field: 'rtdbTenantId',
                        align: "center",
                        title: '实时库租户标识'

                    },
                    {
                        field: 'longitude',
                        align: "center",
                        title: '百度地图-中心坐标经度'

                    },
                    {
                        field: 'latitude',
                        align: "center",
                        title: '百度地图-中心坐标纬度'

                    },
                    {
                        field: 'scale',
                        align: "center",
                        title: '百度地图-默认缩放级别'

                    },
                    {
                        field: 'memo',
                        align: "center",
                        title: '备注'
                    }, {
                        field: 'createUserId',
                        align: "center",
                        title: '创建者',


                    }, {
                        field: 'createTime',
                        align: "center",
                        title: '创建时间',

                    }, {
                        field: 'updateTime',
                        align: "center",
                        title: '修改者',
                        width: 135

                    }, {
                        field: 'updateUserId',
                        align: "center",
                        title: '修改时间',
                        width: 135

                    }
                ]
            ],
            data: data,
            page: false,
            limit: 100
        })

    }
 
    $("#add_btn").on('click',function(){ //新增点击事件
        request.service({
            method: 'get',
            url: '/park/isExistPark'
        })
        .then(function (res) {
            $('#addParkId').attr("disabled", false);
            $("#addParkId").val('') //园区标识
            $("#addParkName").val(''); //园区名称
            $("#addRtdbTenantId").val('');//实时库租户标识
            $("#addLongitude").val('');//百度地图 中心经度
            $("#addLatitude").val('');//百度地图 -中心经度
            $("#addScale").val(''); //百度地图 -缩放级别
            $("#addMemo").val(''); //memo
            showModelIndexBox(null, '新增园区');
        })
        .catch(function(err) {
            console.log(err)
        })
       
    })
 

       $("#edit_btn").on("click",function(){ //修改点击事件
        var tableSelect = table.checkStatus('park_table').data;
           if(tableSelect.length == 0){
               return layer.msg("请选择需要修改的园区")
           }
           $('#addParkId').attr("disabled", true);
           $("#addParkId").val(tableSelect[0].parkId) //园区标识
           $("#addParkName").val(tableSelect[0].parkName); //园区名称
           $("#addRtdbTenantId").val(tableSelect[0].rtdbTenantId);//实时库租户标识
           $("#addLongitude").val(tableSelect[0].longitude);//百度地图 中心经度
           $("#addLatitude").val(tableSelect[0].latitude);//百度地图 -中心经度
           $("#addScale").val(tableSelect[0].scale); //百度地图 -缩放级别
           $("#addMemo").val(tableSelect[0].memo); //memo
        
     
        showModelIndexBox(tableSelect[0].id, '编辑园区');
       })


       function showModelIndexBox(editId, title) { //模态框调用事件
        layer.open({
            type: 1,
            title: title,
            closeBtn: 1,
            shade: 0.3,
            maxmin: true,
            anim: 1,
            area: ['650px', '700px'],
            content: $('#park_add'),
            btn: ['保存', '关闭'],
            success: function () {
                $('#park_add').removeClass('layui-hide').addClass('layui-show');
            },
            yes: function (index) {
                var parkId = $("#addParkId").val() //园区标识
                var parkName = $("#addParkName").val(); //园区名称
                var rtdbTenantId =$("#addRtdbTenantId").val();//实时库租户标识
                var longitude  = $("#addLongitude").val();//百度地图 中心经度
                var latitude = $("#addLatitude").val();//百度地图 -中心经度
                var  scale = $("#addScale").val(); //百度地图 -缩放级别
                var  memo = $("#addMemo").val(); //memo
                if (!parkId) {
                    return layer.msg('请输入园区标识！')
                }
                if (!parkName) {
                    return layer.msg('请输入用户名！')
                }
                var formData = {};
                formData.parkId = parkId;
                formData.parkName = parkName;
                formData.rtdbTenantId = rtdbTenantId;
                formData.longitude =longitude;
                formData.latitude = latitude;
                formData.id = editId;
                formData.scale =scale;
                formData.memo =memo;
                var  url = '/park/add';
                if(editId){
                    url = '/park/edit';
                }
                load();
                request.service({
                        method: 'post',
                        url: url,
                        data: formData
                    })
                    .then(function (res) {
                        disLoad();
                        layer.close(index);
                        getParkDataTab();
                    })
                    .catch(err => {
                        console.log(err)
                    })
            },
            end: function (index) { // 模态框关闭事件
                $('#park_add').removeClass('layui-show').addClass('layui-hide');
            }
        });
    }
      $('#del_btn').click(function () { //删除点击事件
        var tableSelect = table.checkStatus('park_table').data;
        if(tableSelect.length == 0){
            return layer.msg("请选择需要删除的园区")
        }
        layer.open({
            type: 1,
            title: "删除园区",
            shade: 0.3,
            btn: ['确定', '取消'],
            area: ['320px', '300px'], //宽高
            content: '<div id="park_select_tree"> <form class="layui-form" style="margin:30px;">' +
                '   <div class="layui-form-item "><label>园区标识：</label>' +
                '<div class="layui-inline">' + tableSelect[0].parkId + '</div></div>' +
                '   <div class="layui-form-item "><label>园区名称：</label>' +
                '<div class="layui-inline" >' + tableSelect[0].parkName + '</div></div>' +
                '   <div class="layui-form-item " ><span style="color:red;">确定要删除此园区吗？</span>   <i class="fa fa-exclamation-circle tip_icon  " id="deleteParkTip"></i></div>' +

                '</form></div>',
            success: function () {

            },
            yes: function (index) {      
                load();
                request.service({
                        method: 'get',
                        url: '/park/delete/'+ tableSelect[0].parkId,
                    })
                    .then(function (res) {
                        disLoad();
                        layer.close(index);
                        getParkDataTab();
                    })
                    .catch(function (err) {
                        console.log(err)
                    })
            },
            end: function (index) { // 模态框关闭事件
                layer.close(index);
            }
        })
    });

         //提示弹框点击-园区标识
         $("#addParkIdTip").on("click", function () {
            layer.tips('字母、数字组合，长度不超过20 ', '#addParkIdTip');
        })
        //提示弹框点击 
        $("#addParkNameTip").on("click", function () {
            layer.tips('长度不超过30', '#addParkNameTip');
        })
        $("#addRtdbTenantIdTip").on("click", function () {
            layer.tips('此园区在实时库中对应的租户标识， 字母数字组合  长度不超过20', '#addRtdbTenantIdTip');
        })
        $("#addParkMapTip").on("click", function () {
            layer.tips('在能耗地图模块需要用到此处百度地图的相关配置', '#addParkMapTip');
        })

        $(document).on("click", '#deleteParkTip', function () { 
            layer.tips('只删除园区信息表中的信息，不删除其他关联信息。', '#deleteParkTip');
        });
     
        
    initFun(); //页面加载时执行初始函数
});