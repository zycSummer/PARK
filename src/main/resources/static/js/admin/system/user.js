/*
 * @Author: xzl 
 * @Date: 2019-10-17 14:38:00 
 * @Last Modified by: xzl
 * @Last Modified time: 2019-12-03 09:38:22
 */
layui.use(['form', 'element', 'layer', 'table', 'jquery', 'laypage','laydate'], function () {
    var form = layui.form,
        layer = layui.layer,
        $ = layui.jquery,
        table = layui.table,
        laypage = layui.laypage,
        laydate =layui.laydate,
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
        getUserData()
    })

    //初始函数
    function initFun() {
        getUserData();
        getUserGroup();//获取所有用户组
        getAllRoleList();//获取所有角色
    }
    var pageNum = 1;
    var pageLimit;

    function getUserData() { //ajax获取 数据
        var tnum = parseInt(($(".tab_content").height() - 60) / 40);  //动态生成表格展示条数
        var formData = {};
        formData.limit = pageLimit ? pageLimit : tnum;
        formData.page = pageNum;
        var key = {};
        key.code = $("#user_code").val();
        key.name = $("#user_name").val();
        formData.key = key;
        load();
        request.service({
                method: 'post',
                url: '/user/query',
                data: formData
            })
            .then(function (res) {
                disLoad();
                var tableData =res.data;
                if(tableData.length>0){
                    tableData.forEach(element => {
                      switch (element.enabled) { //是否启用
                          case true:
                            element.isEnabledText ='是'  
                              break;
                      
                          default:
                            element.isEnabledText ='否'      
                              break;
                      }
                      switch (element.locked) { //是否锁定
                        case true:
                          element.isLockedText ='是'  
                            break;
                    
                        default:
                          element.isLockedText ='否'      
                            break;
                    } 
                    var roleTextArr =[];   
                    if(element.roleList.length>0){
                     element.roleList.forEach(role=>{
                        roleTextArr.push(role.roleName)
                     })   
                    }
                    element.roleText =roleTextArr.join(',');
                    });
                }
                renderTableContent(res.data, formData.limit);
                renderPage(res.count, tnum);
            })
            .catch(err => {
                console.log(err)
            })

    }

    //渲染表格数据
    function renderTableContent(data, limit) {
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
                        field: 'userId',
                        align: "center",
                        title: '用户标识'
                    },
                    {
                        field: 'userName',
                        align: "center",
                        title: '用户名'

                    },
                    {
                        field: 'userGroupName',
                        align: "center",
                        title: '用户组'
                    },
                    {
                        field: 'roleText',
                        align: "center",
                        title: '角色'
                    },
                    {
                        field: 'isEnabledText',
                        align: "center",
                        title: '是否启用'
                    },
                    {
                        field: 'isLockedText',
                        align: "center",
                        title: '是否锁定'
                    },
                    {
                        field: 'expireDate',
                        align: "center",
                        title: '到期时间'
                    },
                    {
                        field: 'lastLoginIp',
                        align: "center",
                        title: '最后登录IP'
                    },
                    {
                        field: 'lastLoginTime',
                        align: "center",
                        title: '最后登录时间'
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
                        field: 'updateUserId',
                        align: "center",
                        title: '修改者',
                        width: 135

                    }, {
                        field: 'updateTime',
                        align: "center",
                        title: '修改时间',
                        width: 135

                    }
                ]
            ],
            data: data,
            page: false,
            limit: limit
        })

    }
    //渲染分页模块
    function renderPage(count, tnum) {
        laypage.render({
            elem: 'table_page',
            count: count, //数据总数，从服务端得到
            curr: pageNum,
            limit: pageLimit ? pageLimit : tnum,
            limits: [tnum, 20, 30, 50],
            layout: ['count', 'prev', 'page', 'next', 'limit', 'skip'],
            jump: function (obj, first) {
                //首次不执行
                if (!first) {
                    pageNum = obj.curr;
                    pageLimit = obj.limit;
                    getUserData();
                }
            }
        });

    }



    function getUserGroup(){ //获取所有用户组
        request.service({
            method: 'get',
            url: '/user/getAllUserGroup',
       
        })
        .then(function (res) {
         var userGroupList =res.data;
         var usreGroupHtml ='<option></option>';
         if(userGroupList.length>0){
             userGroupList.forEach(userGroup=>{
                usreGroupHtml+='<option value="'+userGroup.userGroupId+'">'+userGroup.userGroupName+'</option>'
             })
             $("#userGourp").html(usreGroupHtml);
             form.render();
         }
        })
        .catch(function(err) {
            console.log(err)
        })
    }
    var roleSelect; //定义 多选框类
      function getAllRoleList(){ //获取所有角色列表
        request.service({
            method: 'get',
            url: '/user/getAllRoles',
       
        })
        .then(function (res) {
         var userGroupList =res.data;
             roleSelect = xmSelect.render({
            el: '#role_select', 
            prop: {
                name: 'roleName',
                value: 'roleId',
            },
            data:userGroupList
        })
        })
        .catch(err => {
            console.log(err)
        })
      }
      laydate.render({  //渲染到期时间
        elem: '#expireDate',
        type: 'date' 
        ,done: function(value, date, endDate){
            console.log(value); //得到日期生成的值，如：2017-08-18
       
          }
      });

    $("#add_btn").on('click', function () { //新增点击事件
        $("#add_user_Id").attr('disabled',false);
        $("#add_user_Id").val('') 
        $("#add_user_name").val('')
        $("#userGourp").val('')
        $("#expireDate").val('')
        roleSelect.setValue([])
        $("#memo").val('') 
       $("input[id='isEnabled']").attr("checked",  false);
       $("input[id='isLocked']").attr("checked",  false);
        form.render();
        showModelIndexBox(null, "新增")
    })

    $("#edit_btn").on('click', function () { //修改点击事件
        $("#add_user_Id").attr('disabled',true);
        var tableSelect = table.checkStatus('role_table').data;
        if (tableSelect.length === 0) {
            return layer.msg("请选择需要修改的用户！")
        }
        if (tableSelect.length > 1) {
            return layer.msg("只能选择单个用户！")
        }
        var editData = tableSelect[0];
        $("#add_user_Id").val(editData.userId) 
        $("#add_user_name").val(editData.userName);
        $("#userGourp").val(editData.userGroupId);
        $("#expireDate").val(editData.expireDate);
        var renderRoleSelect=[];
        if(editData.roleList.length>0){
            editData.roleList.forEach(editRole=>{
                renderRoleSelect.push(editRole.roleId)
            })
        }
        roleSelect.setValue(renderRoleSelect)
        $("#memo").val(editData.memo) 
       $("input[id='isEnabled']").attr("checked",  editData.enabled);
       $("input[id='isLocked']").attr("checked",  editData.locked);
         form.render();
        showModelIndexBox(editData.id, "编辑")
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
            content: $('#role_add'),
            btn: ['保存', '关闭'],
            success: function () {
                $('#role_add').removeClass('layui-hide').addClass('layui-show');
            },
            yes: function (index) {
               
               
                var userId = $("#add_user_Id").val() //用户ID
                var userName = $("#add_user_name").val(); //用户名
                var userGroupId =$("#userGourp").val();//用户组
                var roleSelectArr =roleSelect.getValue();//所选择角色
                var  expireDate = $("#expireDate").val(); //到期时间
                var memo = $("#memo").val(); //memo
                if (!userId) {
                    return layer.msg('请输入用户标识！')
                }
                if (!userName) {
                    return layer.msg('请输入用户名！')
                }
                if (!userGroupId) {
                    return layer.msg('请选择用户组！')
                }
                //  if(roleSelectArr.length == 0){
                //      return layer.msg("请选择用户角色！")
                //  }
                var roleList =[];
                roleSelectArr.forEach(roleSelect=>{
                    var roleObj ={};
                    roleObj.roleId =roleSelect.roleId
                    roleList.push(roleObj);
                })
                var formData = {};
                formData.userId = userId;
                formData.userGroupId = userGroupId;
                formData.userName = userName;
                formData.roleList =roleList;
                formData.memo = memo;
                formData.expireDate =expireDate;
                formData.id = editId;
                formData.enabled = $("#isEnabled").is(":checked");
                formData.locked = $("#isLocked").is(":checked")
                var url ='/user/add'
                if(editId){
                    url ='/user/edit'
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
                        getUserData();
                    })
                    .catch(err => {
                        console.log(err)
                    })
            },
            end: function (index) { // 模态框关闭事件
                $('#role_add').removeClass('layui-show').addClass('layui-hide');
            }
        });
    }

    $('#del_btn').click(function () { //删除点击事件
        var tableSelect = table.checkStatus('role_table').data;
        if (tableSelect.length == 0) {
            return layer.msg('请选择需要删除的用户！')
        }
        var delArr =[];
        delArr.push(tableSelect[0].userId);
        layer.open({
            type: 1,
            title: "删除用户",
            shade: 0.3,
            btn: ['确定', '取消'],
            area: ['320px', '300px'], //宽高
            content: '<div id="park_select_tree"> <form class="layui-form" style="margin:30px;">' +
                '   <div class="layui-form-item "><label>用户标识：</label>' +
                '<div class="layui-inline">' + tableSelect[0].userId + '</div></div>' +
                '   <div class="layui-form-item "><label>用户名称：</label>' +
                '<div class="layui-inline" >' + tableSelect[0].userName + '</div></div>' +
                '   <div class="layui-form-item " ><span style="color:red;">确定要删除此用户吗？</span>   <i class="fa fa-exclamation-circle tip_icon " id="deleteParkTip"></i></div>' +

                '</form></div>',
            success: function () {

            },
            yes: function (index) {      
                load();
                request.service({
                    method: 'post',
                    url: '/user/delete',
                    data: delArr
                    })
                    .then(function (res) {
                        disLoad();
                        layer.close(index);
                        getUserData();
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


    $("#rest_pwd").on('click', function () { //重置密码事件
        var tableSelect = table.checkStatus('role_table').data;
        if (tableSelect.length === 0) {
            return layer.msg("请选择需要重置密码的用户！")
        }
        if (tableSelect.length > 1) {
            return layer.msg("只能选择单个用户！")
        }
        layer.confirm('确认重置此用户密码？', {
            icon: 3,
            title: '提示'
        }, function (index) {
            load();
            request.service({
                    method: 'get',
                    url: '/user/resetPwd/'+tableSelect[0].userId,
              
                })
                .then(function (res) {
                    disLoad();
                    layer.close(index);
                    getUserData();
                    layer.msg(res.msg);
                })
        });



    })
    $(document).on("click", '#deleteParkTip', function () { 
        layer.tips('删除用户信息表和用户角色关联表的相关记录。', '#deleteParkTip');
    });
   
    initFun(); //页面加载时执行初始函数
});