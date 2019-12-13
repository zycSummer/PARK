/*
 * @Author: xzl 
 * @Date: 2019-10-17 14:38:00 
 * @Last Modified by: xzl
 * @Last Modified time: 2019-11-25 09:44:03
 */
layui.use(['form', 'element', 'layer', 'table', 'jquery', 'laypage','colorpicker'], function () {
    var form = layui.form,
        layer = layui.layer,
        $ = layui.jquery,
        table = layui.table,
        colorpicker =layui.colorpicker,
        laypage = layui.laypage,
        element = layui.element;
    var indexLoading; 
    function load() {
        indexLoading = layer.load(1, {
            shade: [0.3, '#fff']
        });
    }

    function disLoad() {
        layer.close(indexLoading);
    }
    //查询点击事件
    $("#search_btn").on("click", function () {
        getEnergyGradeData()
    })

    //初始函数
    function initFun() {
        getEnergyGradeData();
    }
    function getEnergyGradeData() { //ajax获取 数据
        var formData = {};
        formData.energyGradeId =$("#search_energyGradeId").val();
        load();
        request.service({
                method: 'post',
                url: '/sysEnergyGrade/query',
                data: formData
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
            elem: '#role_table',
            height: 'full-155',
            cols: [
                [{
                        type: "radio",
                        width: 50,
                        fixed: "left"
                    },

                    {
                        type: 'numbers',
                        align: "center",
                        title: '序号'

                    },
                    {
                        field: 'energyGradeId',
                        align: "center",
                        title: '能耗强度等级标识'
                    },
                    {
                        field: 'lower',
                        align: "center",
                        title: '能耗强度下限'

                    },
                    {
                        field: 'upper',
                        align: "center",
                        title: '能耗强度上限'

                    },
                    {
                        field: 'color',
                        align: "center",
                        title: '颜色'
                        ,templet: function(d){
                            return '<span style="color:'+d.color+';">'+ d.color +'</span>'
                          }
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
            limit: 1000
        })

    }
    colorpicker.render({
        elem: '#addColor' 
        ,done: function(color){
            $('#addColorVal').val(color);
          } //绑定元素
      });
    $("#add_btn").on('click', function () { //新增点击事件
        $('#energyGradeId').attr("disabled", false);
        $("#energyGradeId").val('')
        $("#lower").val('')
        $("#upper").val('')
        $("#addColorVal").val('')
        $("#addmemo").val('')
        showModelIndexBox(null, "新增能耗强度等级")
    })

    $("#edit_btn").on('click', function () { //修改点击事件
        var tableSelect = table.checkStatus('role_table').data;
        if (tableSelect.length === 0) {
            return layer.msg("请选择需要修改的数据！")
        }
       
        var editData = tableSelect[0];
        $('#energyGradeId').attr("disabled", true);
        $("#energyGradeId").val(editData.energyGradeId)
        $("#lower").val(editData.lower)
        $("#upper").val(editData.upper)
        $("#addColorVal").val(editData.color)
        $("#addmemo").val(editData.memo)
        showModelIndexBox(editData.id, "编辑能耗强度等级")
    })


    function showModelIndexBox(editId, title) { //模态框调用事件
        layer.open({
            type: 1,
            title: title,
            closeBtn: 1,
            shade: 0.3,
            maxmin: true,
            anim: 1,
            area: ['500px', '500px'],
            content: $('#energyGrade_add'),
            btn: ['保存', '关闭'],
            success: function () {
                $('#energyGrade_add').removeClass('layui-hide').addClass('layui-show');
            },
            yes: function (index) {
                var energyGradeId = $("#energyGradeId").val() //能耗强度等级标识
                var lower = $("#lower").val(); //下限
                var upper = $("#upper").val() || null; //上限
                var color  =$("#addColorVal").val();
                var memo   =$("#addmemo").val();
                if (!energyGradeId) {
                    return layer.msg('请输入能耗强度标识')
                }
                if (!lower) {
                    return layer.msg('请输入能耗强度下限')
                }
                if (upper) {
                  if( parseInt(lower) >  parseInt(upper)){
                      return layer.msg("能耗强度下限不能大于能耗强度上限")
                  }
                }
                if (!color) {
                    return layer.msg('请选择颜色')
                }
               

                var formData = {};
                formData.energyGradeId = energyGradeId;
                formData.lower = lower;
                formData.upper = upper;
                formData.color  =color;
                formData.memo  =memo;
                formData.id = editId;
                var  url ='/sysEnergyGrade/add';
                    if(editId){
                    url ='/sysEnergyGrade/edit';
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
                        getEnergyGradeData();
                    })
                    .catch(err => {
                        console.log(err)
                    })
            },
            end: function (index) { // 模态框关闭事件
                $('#energyGrade_add').removeClass('layui-show').addClass('layui-hide');
            }
        });
    }

   
    $('#del_btn').click(function () { //删除点击事件
        var tableSelect = table.checkStatus('role_table').data;
        if (tableSelect.length == 0) {
            return layer.msg('请选择需要删除的能耗强度等级！')
        }
        layer.open({
            type: 1,
            title: "删除能耗强度等级",
            shade: 0.3,
            btn: ['确定', '取消'],
            area: ['320px', '240px'], //宽高
            content: '<div id="park_select_tree"> <form class="layui-form" style="margin:30px;">' +
                '   <div class="layui-form-item "><label>能耗强度等级标识：</label>' +
                '<div class="layui-inline">' + tableSelect[0].energyGradeId + '</div></div>' +
                '   <div class="layui-form-item " style="color:red;">确定要删除此能耗强度等级吗？</div>' +

                '</form></div>',
            success: function () {

            },
            yes: function (index) {
             
                load();
                request.service({
                        method: 'get',
                        url: '/sysEnergyGrade/delete/'+ tableSelect[0].energyGradeId,
                    
                    })
                    .then(function (res) {
                        disLoad();
                        layer.close(index);
                        getEnergyGradeData();
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


    $("#authority_maintenance_btn").on('click', function () { //权限维护事件
        var tableSelect = table.checkStatus('role_table').data;
        if (tableSelect.length === 0) {
            return layer.msg("请选择需要维护的角色！")
        }
        if (tableSelect.length > 1) {
            return layer.msg("只能选择单个角色！")
        }
        showModelAuthBox(tableSelect[0].roleId, '权限维护')

    })


    function showModelAuthBox(roleId, title) { //模态框调用事件
        layer.open({
            type: 1,
            title: title,
            closeBtn: 1,
            shade: 0.3,
            maxmin: true,
            anim: 1,
            area: ['1100px', '650px'],
            content: $('#role_auth'),
            btn: ['保存', '关闭'],
            success: function () {
                $('#role_auth').removeClass('layui-hide').addClass('layui-show');
                //根据角色code 获取权限集
                getRoleMenuTableData(roleId)

            },
            yes: function (index) {
               
                var menuCheck = []; //获取所有菜单被选中的值
                var buttonCheck = []; //获取所有按钮被选中的值
                $('.layui-table-body input[name="menu_role"]:checked').each(function(){ 
                         menuCheck.push($(this).val());
                      });
                  
                $('.layui-table-body input[name="button_role"]:checked').each(function(){    
                            var btnObj = {};
                            btnObj.menuId = $(this).data('parent');
                            btnObj.functionId = $(this).data('functionid');
                            buttonCheck.push(btnObj);
                      });
                var formData = {};
                formData.roleId = roleId;
                formData.menuIdList = menuCheck;
                formData.buttonList = buttonCheck;
                load();
                request.service({
                        method: 'post',
                        url: '/role/editAuth',
                        data: formData
                    })
                    .then(function (res) {
                        disLoad();
                        layer.close(index);
                        getEnergyGradeData();
                    })
                    .catch(err => {
                        console.log(err)
                    })
            },
            end: function (index) { // 模态框关闭事件
                $('#role_auth').removeClass('layui-show').addClass('layui-hide');
            }
        });
    }




    //获取权限数据 
    function getRoleMenuTableData(roleId) {
        load()
        request.service({
            method: "get",
            url: "/role/getMenuAndButtons/" + roleId
        }).then(res => {
            disLoad();
            var roleData = handleRoleTreeData(res.data);
            renderRoleTable(roleData)

        }).catch(function (err) {
            console.log(err)
        })
    }
 
      $("#energyGradeIdTip").on("click", function () {
        layer.tips('字母、数字组合，长度不超过20。', '#energyGradeIdTip');
    })
    $("#lowerTip").on("click", function () {
        layer.tips('只能输入数字', '#lowerTip');
    })
    $("#upperTip").on("click", function () {
        layer.tips('只能输入数字', '#upperTip');
    })
    initFun(); //页面加载时执行初始函数 
});