/*
 * @Author: xzl 
 * @Date: 2019-10-17 14:38:00 
 * @Last Modified by: xzl
 * @Last Modified time: 2019-12-12 13:41:03
 */
layui.config({
    base: '/public/lib/layui_exts/'
}).extend({
    treeGrid: 'treeGrid'
}).use(['form', 'element', 'layer', 'table', 'jquery', 'laypage', 'treeGrid'], function () {
    var form = layui.form,
        layer = layui.layer,
        $ = layui.jquery,
        table = layui.table,
        laypage = layui.laypage,
        treeGrid = layui.treeGrid,
        element = layui.element;

    var roleTreeTable;
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
        getRoleTableData()
    })

    //初始函数
    function initFun() {
        getRoleTableData();
    }
    var pageNum = 1;
    var pageLimit;
    function getRoleTableData() { //ajax获取 数据
        var tnum = parseInt(($(".tab_content").height() - 60) / 40); //动态生成表格展示条数
        var formData = {};
        formData.limit = pageLimit ? pageLimit : tnum;
        formData.page = pageNum;
        var key = {};
        key.code = $("#role_code").val();
        key.name = $("#role_name").val();
        formData.key = key;
        load();
        request.service({
                method: 'post',
                url: '/role/query',
                data: formData
            })
            .then(function (res) {
                disLoad();
                var data = res.data;
                
                renderTableContent(data, formData.limit);
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
                        type: 'numbers',
                        align: "center",
                        title: '序号'

                    },
                    {
                        field: 'roleId',
                        align: "center",
                        title: '角色标识'
                    },
                    {
                        field: 'roleName',
                        align: "center",
                        title: '角色名称'

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
                    getRoleTableData();
                }
            }
        });

    }

    $("#add_btn").on('click', function () { //新增点击事件
        $("#add_role_code").val('')
        $("#add_role_name").val('')
        $("#memo").val('')
        showModelIndexBox(null, "新增")
    })

    $("#edit_btn").on('click', function () { //修改点击事件
        var tableSelect = table.checkStatus('role_table').data;
        if (tableSelect.length === 0) {
            return layer.msg("请选择需要修改的数据！")
        }
        if (tableSelect.length > 1) {
            return layer.msg("只能选择单条数据！")
        }
        var editData = tableSelect[0];
        $("#add_role_code").val(editData.roleId)
        $("#add_role_name").val(editData.roleName)
        $("#memo").val(editData.memo);
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
            area: ['600px', '400px'],
            content: $('#role_add'),
            btn: ['保存', '关闭'],
            success: function () {
                $('#role_add').removeClass('layui-hide').addClass('layui-show');
            },
            yes: function (index) {
                var roleCode = $("#add_role_code").val() //角色标识
                var roleName = $("#add_role_name").val(); //角色名称
                var memo = $("#memo").val(); //memo
                if (!roleCode) {
                    return layer.msg('请输入角色标识')
                }
                if (!roleName) {
                    return layer.msg('请选择角色名称')
                }



                var formData = {};
                formData.roleId = roleCode;
                formData.roleName = roleName;
                formData.memo = memo;
                formData.id = editId;
                var url ='role/add'
                  if(editId){
                    url ='role/edit'
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
                        getRoleTableData();
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
            return layer.msg('请选择需要删除的角色')
        }
        var delArr = [];
        delArr.push(tableSelect[0].roleId);

        layer.open({
            type: 1,
            title: "删除角色",
            shade: 0.3,
            btn: ['确定', '取消'],
            area: ['320px', '300px'], //宽高
            content: '<div id="park_select_tree"> <form class="layui-form" style="margin:30px;">' +
                '   <div class="layui-form-item "><label>角色标识：</label>' +
                '<div class="layui-inline">' + tableSelect[0].roleId + '</div></div>' +
                '   <div class="layui-form-item "><label>角色名称：</label>' +
                '<div class="layui-inline" >' + tableSelect[0].roleName + '</div></div>' +
                '   <div class="layui-form-item " ><span style="color:red;">确定要删除此角色吗？此角色与菜单、功能的关联关系一并删除! 用户管理中涉及到的角色关联需要重新手动设置! </span> </div>' +

                '</form></div>',
            success: function () {

            },
            yes: function (index) {      
                load();
                request.service({
                    method: 'post',
                    url: '/role/delete',
                    data: delArr
                })
                    .then(function (res) {
                        layer.close(index);
                        layer.msg("删除成功")
                        getRoleTableData();
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
            area: ['1150px', '650px'],
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
                        getRoleTableData();
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
    var tableId = 'treeTable';

    function renderRoleTable(data) {
        roleTreeTable = treeGrid.render({
            id: tableId,
            elem: '#' + tableId,
           
            idField: 'id', //必須字段  
            treeId: 'id', //树形id字段名称 
            treeUpId: 'parentId', //树形父id字段名称
            treeShowName: 'title', //以树形式显示的字段
            isFilter: false,
            iconOpen: false, //是否显示图标【默认显示】  
            isOpenDefault: true, //节点默认是展开还是折叠【默认展开】     
            loading: true,
            isPage: false,
            limit:100,
            cols: [
                [ {
                    field: 'title',
                    width: 260,
                    title: '菜单',
                    templet: function (d) {
                        var menuHtml ='';
                        if(d.checked){
                            menuHtml+= '<a  lay-event="menuClick"><input type="checkbox" name="menu_role"  lay-filter="selectMenu" class ="selectMenu cbox '+d.menuId+'"  data-type="'+d.type+'" data-parent ="'+d.parentId+'"  data-menuid="'+d.menuId+'" checked="checked" value="'+d.menuId+'" title="' + d.title + '" lay-ignore >' + d.title + '</a>'
                       
                        }else{
                            menuHtml+= '<a  lay-event="menuClick"><input type="checkbox" name="menu_role"    lay-filter="selectMenu" class ="selectMenu cbox '+d.menuId+'"  data-type="'+d.type+'"  data-parent ="'+d.parentId+'"  data-menuid="'+d.menuId+'"  value="'+d.menuId+'" title="' + d.title + '"  lay-ignore >' + d.title + '</a>'
    
                        }
                        
                        return menuHtml;
                    }
   
                }, {
                    field: 'id',
                    title: '功能',
                    templet: function (d) {
                        var btnHtml = '';
                        var btnList  =d.btnList;
                           if(btnList.length>0){
                               btnList.forEach(function(btn){
                                   if(btn.checked){
                                    btnHtml+= '<a lay-event="btnClick"><input type="checkbox" class ="selectBtn cbox '+btn.menuId+'_'+btn.functionId+'" name="button_role"   checked="checked"  data-functionid ="'+btn.functionId+'"  data-type ="'+btn.type+'"  data-parent ="'+btn.menuId+'" value="" title="' + btn.title + '" lay-ignore >' + btn.title + '</a>'                                  
                                   }
                                   else{
                                    btnHtml+= '<a lay-event="btnClick"><input type="checkbox" class ="selectBtn cbox '+btn.menuId+'_'+btn.functionId+'"  name="button_role"  data-functionid ="'+btn.functionId+'"   data-type ="'+btn.type+'"   data-parent ="'+btn.menuId+'" value="" title="' + btn.title + '" lay-ignore >' + btn.title + '</a>'                                  

                                   }
                               })
                           }
                     
                      
                        return btnHtml;
                    }
                }]
            ],
            data: data
        });
    }
    treeGrid.on('tool('+tableId+')',function (obj) {
       if(obj.event==="menuClick"){//当操作菜单时
            var  menuObjData = obj.data;  //菜单数据
           var  selectStatus =$("."+menuObjData.menuId+"").prop('checked'); //菜单选中状态
           switch (selectStatus) {
               case true:  //当为选中时   判断是否存在上级菜单  是则勾选上级菜单
                 if(menuObjData.parentId){
                $("."+menuObjData.parentId+"").prop("checked", true);
                 }else{
                     if(menuObjData.children.length>0){ //默认勾选第一个
                        $("."+menuObjData.children[0].menuId+"").prop("checked", true);     
                     }
                 }    
                   break;
           
               case false:  //当取消选中时
              
                if(menuObjData.children.length>0){  //当为菜单时  子类为菜单
                    menuObjData.children.forEach(function(item){
                        $("."+item.menuId+"").prop("checked", false);  //子菜单取消勾选
                     if(item.btnList.length>0){  //当子菜单存在按钮时
                        item.btnList.forEach(function(btn_one){
                            $("."+btn_one.menuId+"_"+btn_one.functionId+"").prop("checked", false);  //子菜单按钮取消勾选
                        })
                     }
                    })
                }
                if(menuObjData.btnList.length>0){  //当子类为菜单时  下一级 为按钮时
                    menuObjData.btnList.forEach(function(btn){
                        $("."+btn.menuId+"_"+btn.functionId+"").prop("checked", false);  //按钮取消勾选
                    })
                }
        
                   break;
           }
        }
    });
       
   //能源类型选择点击事件
   $(document).on("click", '.selectBtn', function () {
     var   parentId =$(this).data("parent");
      var  btnSelectStatus =$(this).prop('checked');  //按钮选中状态
         if(btnSelectStatus){
            judgeParent(parentId)
         }
  });

      function judgeParent(parentId){  //判断父类元素是否存在 
        if(parentId){
            var btnSelectStatusParent= $("."+parentId+"").prop('checked');   //判断父类选择状态
            if(!btnSelectStatusParent){ //当父类不选中时 选择父类
                $("."+parentId+"").prop('checked',true); 
                if($("."+parentId+"").data('parent')){
                judgeParent($("."+parentId+"").data('parent'))   
                }
            }
        }
      }
     

    //递归循环 拼接菜单数据
    function handleRoleTreeData(data){ //处理权限数据
        var checkedData =[];
        if(data.length>0){
        var  forfun = function(arr){
            arr.forEach(function(item){
                item.id =item.menuId;
                checkedData.push(item);
             if(item.children){
                 if(item.children[0].type == 'function'){  //当子类为按钮时
                    item.btnList =item.children;
                    item.children =null
                 }
                 else{  //当子类为菜单时
                    forfun(item.children)
                    item.btnList =[];
                 }
              
             }else{
                item.btnList =[];  
             }
          })  
        }
    }
       forfun(data);
       return checkedData;
    }
    initFun(); //页面加载时执行初始函数 
});