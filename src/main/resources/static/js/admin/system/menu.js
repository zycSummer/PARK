/*
 * @Author: xzl 
 * @Date: 2019-10-17 14:38:00 
 * @Last Modified by: xzl
 * @Last Modified time: 2019-12-12 14:07:04
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

    var indexLoading; //loading 加载类
    var tableId = 'treeTable';

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
        getRoleTabData()
    })
    var demo1 = xmSelect.render({
        el: '#selectIcon',
        prop: {
            name: 'icon',
            value: 'icon',
        },
        content: `
            <table class="layui-table"  lay-filter="demo" >
            <thead>
            <tr>
              <th lay-data="{field:'icon', width:180}">标识</th>
			  <th lay-data="{field:'username', width:160}">图标</th>
		
			</tr> 
		  </thead>
              <tbody >
                <tr class="menuIcon  users" data-icon="fa fa-users">
                <td>fa fa-users</td>
                  <td><i class="fa fa-users"></i></td>
             
                </tr>
                <tr class="menuIcon  desktop" data-icon="fa fa-desktop">
                <td>fa fa-desktop</td>
                <td><i class="fa fa-desktop"></i></td>
              </tr>
              <tr class="menuIcon   bell" data-icon="fa fa-bell">
              <td>fa fa-bell</td>
              <td><i class="fa fa-bell"></i></td>
            </tr>
            <tr class="menuIcon   calendar" data-icon="fa fa-calendar">
            <td>fa fa-calendar</td>
            <td><i class="fa fa-calendar"></i></td>
          </tr>
          <tr class="menuIcon    institution" data-icon="fa fa-institution">
          <td>fa fa-institution</td>
           <td><i class="fa fa-institution"></i></td>
        </tr>
        <tr class="menuIcon  line-chart" data-icon="fa fa-line-chart">
        <td>fa fa-line-chart</td>
        <td><i class="fa fa-line-chart"></i></td>
     </tr>
     <tr class="menuIcon    map-o" data-icon="fa fa-map-o">
     <td>fa fa-map-o</td>
     <td><i class="fa fa-map-o"></i></td>
  </tr>
   <tr class="menuIcon    pie-chart" data-icon="fa fa-pie-chart">
   <td>fa fa-pie-chart</td>
  <td><i class="fa fa-pie-chart"></i></td>
</tr>
<tr class="menuIcon     window-restore" data-icon="fa fa-window-restore">
<td>fa fa-window-restore</td>
<td><i class="fa fa-window-restore"></i></td>
</tr>
<tr class="menuIcon    map-signs" data-icon="fa fa-map-signs">
<td>fa fa-map-signs</td>
<td><i class="fa fa-map-signs"></i></td>
</tr>
<tr class="menuIcon  area-chart" data-icon="fa fa-area-chart">
<td>fa fa-area-chart</td>
<td><i class="fa fa-area-chart"></i></td>
</tr>
<tr class="menuIcon    pencil-square-o" data-icon="fa fa-pencil-square-o">
<td>fa fa-pencil-square-o</td>
<td><i class="fa fa-pencil-square-o"></i></td>
</tr>
       </tbody>
            </table>
        `,
        height: 200,
    })

    layui.table.init('demo', {
        height: 240,
        done: function(res){
            demo1.update({ data: res.data })
        }
    }).on('row(demo)', function(obj){
        var values = demo1.getValue();
        var item = obj.data;
        console.log(values,'11')
        if(values.length>0){
            demo1.delete(values);
            demo1.append([ item ]);
        }else{
            demo1.append([ item ]);
        }
         demo1.closed();
    })
    //初始函数
    function initFun() {
        getRoleTabData();
    }

    function getRoleTabData() { //ajax获取 数据  
        load();
        request.service({
                method: 'get',
                url: '/menuAndFunction/getAllMenu',

            })
            .then(function (res) {
                disLoad();
                renderMenuSelect(res.data);
                let roleTableData = res.data.concat();
                if (roleTableData.length > 0) {
                    roleTableData[0].lay_is_radio = true; //默认选中第一个
                    renderMenuTable(roleTableData)
                    getButtonList(roleTableData[0].menuId)
                }
            })
            .catch(err => {
                console.log(err)
            })

    }

    function renderMenuTable(data) {

        roleTreeTable = treeGrid.render({
            id: tableId,
            elem: '#' + tableId,
            cellMinWidth: 100,
            idField: 'menuId' //必須字段
                ,
            treeId: 'menuId' //树形id字段名称
                ,
            treeUpId: 'parentId' //树形父id字段名称
                ,
            treeShowName: 'menuName' //以树形式显示的字段
                ,
            heightRemove: [".dHead", 10] //不计算的高度,表格设定的是固定高度，此项不生效
                ,
            height: $(".tab_content_tab_menu").height(),
            isFilter: false,
            iconOpen: true //是否显示图标【默认显示】
                ,
            isOpenDefault: true //节点默认是展开还是折叠【默认展开】
                ,
            loading: true,
            isPage: false,
            limit: 200,
            cols: [
                [{
                    type: 'radio'
                }, {
                    field: 'menuName',
                    width: 200,
                    title: '菜单名称'
                }, {
                    field: 'menuId',
                    width: 100,
                    title: '菜单标识'
                }, {
                    field: 'parentId',
                    title: '父级菜单标识'
                }, {
                    field: 'icon',
                    width: 100,
                    title: '图标',templet: function(d){
                        return '<i class="'+d.icon+'"></i>'
                      }
                }, {
                    field: 'sortId',
                    title: '排序标识'
                }, {
                    field: 'method',
                    width: 100,
                    title: '请求方式'
                }, {
                    field: 'url',
                    width: 150,
                    title: '接口地址'
                }, {
                    field: 'memo',
                    title: '备注'
                }, {
                    field: 'createUserId',
                    title: '创建者'
                }, {
                    field: 'createTime',
                    width: 150,
                    title: '创建时间'
                }, {
                    field: 'updateUserId',
                    title: '修改者'
                }, {
                    field: 'updateTime',
                    width: 150,
                    title: '修改时间'
                }]
            ],
            data: data,
            onRadio: function (obj) { //单选事件
                getButtonList(obj.menuId);
            }
        });


    }

    $("#search_btn_fun").on("click", function () { //按钮功能查询事件
        var tableSelect = treeGrid.radioStatus(tableId);

        if (!tableSelect) {
            return layer.msg("请选择需要查询的菜单")
        }
        getButtonList(tableSelect.menuId)
    })

    function getButtonList(menuId) { //获取按钮列表
        load();
        request.service({
                method: 'get',
                url: '/menuAndFunction/getFunctionsByMenuId/' + menuId,
            })
            .then(function (res) {
                disLoad();
                var data = res.data;
                renderButtonTable(data)
            })
            .catch(function (err) {
                console.log(err)
            })
    }

    //渲染功能表格数据
    function renderButtonTable(data) {
        table.render({
            elem: '#role_table',
            height: 'full-110',
            cols: [
                [{
                        type: 'radio',
                        fixed: "left"
                    },
                    {
                        type: 'numbers',
                        align: "center",
                        title: '序号'
                    },
                    {
                        field: 'functionId',
                        align: "center",
                        title: '功能标识',
                        width: 135
                    },
                    {
                        field: 'functionName',
                        align: "center",
                        title: '功能名称',
                        width: 135

                    },
                    {
                        field: 'functionDesc',
                        align: "center",
                        title: '功能说明',
                        width: 135
                    }, {
                        field: 'method',
                        align: "center",
                        title: '请求方式',
                        width: 135


                    }, {
                        field: 'url',
                        align: "center",
                        title: '接口地址',
                        width: 135

                    }, {
                        field: 'memo',
                        align: "center",
                        title: '备注',
                        width: 135

                    }, {
                        field: 'createUserId',
                        align: "center",
                        title: '创建者',
                        width: 135

                    }, {
                        field: 'createTime',
                        align: "center",
                        title: '创建时间',
                        width: 135

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
            limit: 100
        })

    }

    function renderMenuSelect(data) {
        var handleMenuArr = handleMenuData(data);
        layui.formSelects.data('addParentMenuSelect', 'local', {
            arr: handleMenuArr
        });
    }
    var disSelectArr = [];
    $("#edit_menu").on('click', function () { //修改点击事件
        var tableSelect = treeGrid.radioStatus(tableId);

        if (!tableSelect) {
            return layer.msg("请选择需要修改的菜单")
        }
        disSelectArr.push(tableSelect);
        $("#addMenuId").val(tableSelect.menuId); //菜单标识
        $("#addMenuName").val(tableSelect.menuName); //菜单名称
        $("#addMethod").val(tableSelect.method); //请求方式
        $("#addUrl").val(tableSelect.url); //接口地址
        $("#addsortId").val(tableSelect.sortId); //排序标识
        var iconSelectArr=[];
         if(tableSelect.icon){
            iconSelectArr.push(tableSelect.icon)
         }
        demo1.setValue(iconSelectArr)
        $("#addMemo").val(tableSelect.memo)
        var selectIdArr = [];
        if (tableSelect.parentId) {
            selectIdArr.push(tableSelect.parentId)
        }
        layui.formSelects.value('addParentMenuSelect', selectIdArr);
        form.render();

        showModelIndexBox(tableSelect.id, "编辑菜单")
    })
    layui.formSelects.on('addParentMenuSelect', function (id, vals, val, isAdd, isDisabled) {
        var selectType = handleMenuDisArr(disSelectArr, val.value);
        if (!selectType) {
            return false;
        }
    });

    function handleMenuDisArr(data, val) {
        var checkStatus = true;
        var   forMenu = function(arr){
            for (var i = 0; i < arr.length; i++) {
                var obj = arr[i];
                if (obj.menuId == val) {
                    checkStatus = false;
                }
                if (obj.children) {
                    forMenu(obj.children);
                }
            }
        }
         forMenu(data);
        return checkStatus;
    }

    function showModelIndexBox(editId, title) { //模态框调用事件
        layer.open({
            type: 1,
            title: title,
            closeBtn: 1,
            shade: 0.3,
            maxmin: true,
            anim: 1,
            area: ['600px', '700px'],
            content: $('#menu_add'),
            btn: ['保存', '关闭'],
            success: function () {
                $('#menu_add').removeClass('layui-hide').addClass('layui-show');
            },
            yes: function (index) {
                var menuId = $("#addMenuId").val() //菜单标识
                var menuName = $("#addMenuName").val(); //菜单名称
                var parentIdArr = layui.formSelects.value('addParentMenuSelect','val'); //父级菜单 
                var parentId = null;
                if (parentIdArr.length > 0) {
                    parentId = parentIdArr[0];
                }
                var sortId = $("#addsortId").val() || null; //排序标识
                var iconArr =  demo1.getValue(); //图标
                var icon;
                  if(iconArr.length>0){
                    icon =iconArr[0].icon;
                  }
                var memo = $("#addMemo").val(); //memo
                if (!menuName) {
                    return layer.msg('请输入菜单名称')
                }

                var formData = {};
                formData.menuId = menuId;
                formData.menuName = menuName;
                formData.parentId = parentId;
                formData.sortId = sortId;
                formData.icon = icon;
                formData.memo = memo;
                formData.id = editId;
                console.log(formData, '11')
                load();
                request.service({
                        method: 'post',
                        url: '/menuAndFunction/updateMenu',
                        data: formData
                    })
                    .then(function (res) {
                        disLoad();
                        layer.close(index);
                        getRoleTabData();
                    })
                    .catch(err => {
                        console.log(err)
                    })
            },
            end: function (index) { // 模态框关闭事件
                $('#menu_add').removeClass('layui-show').addClass('layui-hide');
            }
        });
    }

    $("#add_btn_fun").on("click", function () {
        var tableSelectMenu = treeGrid.radioStatus(tableId);
        $("#funMenuId").val('[' + tableSelectMenu.menuId + ']' + tableSelectMenu.menuName);
        $("#functionId").attr("disabled", false);
        $("#functionId").val(''); //功能标识
        $("#addFunctionName").val('');
        $("#addFunctionDsc").val('');
        $("#addBtnMethod").val('');
        $("#addBtnUrl").val('');
        $("#addBtnMemo").val('');
        showModelAuthBox(null, tableSelectMenu, '新增功能')
    })

    $("#edit_btn").on('click', function () { //按钮修改事件
        var tableSelectBtn = table.checkStatus('role_table').data;
        var tableSelectMenu = treeGrid.radioStatus(tableId);
        if (tableSelectBtn.length === 0) {
            return layer.msg("请选择需要编辑的功能！")
        }
        $("#functionId").attr("disabled", true);
        $("#funMenuId").val('[' + tableSelectBtn[0].menuId + ']' + tableSelectMenu.menuName);
        $("#functionId").val(tableSelectBtn[0].functionId); //功能标识
        $("#addFunctionName").val(tableSelectBtn[0].functionName);
        $("#addFunctionDsc").val(tableSelectBtn[0].functionDesc);
        $("#addBtnMethod").val(tableSelectBtn[0].method);
        $("#addBtnUrl").val(tableSelectBtn[0].url);
        $("#addBtnMemo").val(tableSelectBtn[0].memo);
        form.render();
        showModelAuthBox(tableSelectBtn[0].id, tableSelectBtn[0], '编辑功能')

    })


    function showModelAuthBox(editId, btnObj, title) { //模态框调用事件
        layer.open({
            type: 1,
            title: title,
            closeBtn: 1,
            shade: 0.3,
            maxmin: true,
            anim: 1,
            area: ['700px', '650px'],
            content: $('#fun_add'),
            btn: ['保存', '关闭'],
            success: function () {
                $('#fun_add').removeClass('layui-hide').addClass('layui-show');
            },
            yes: function (index) {
                var functionName = $("#addFunctionName").val();
                var functionId = $("#functionId").val();
                var functionDesc = $("#addFunctionDsc").val();
                var method = $("#addBtnMethod").val();
                var url = $("#addBtnUrl").val();
                if (!functionId) {
                    return layer.msg("请输入功能标识")
                }
                if (!functionName) {
                    return layer.msg("请输入功能名称")
                }
                if (!functionDesc) {
                    return layer.msg("请输入功能描述")
                }
                var formData = {};
                formData.menuId = btnObj.menuId;
                formData.functionId = functionId;
                formData.functionName = functionName;
                formData.functionDesc = functionDesc;
                formData.url = url;
                formData.method = method;
                formData.memo = $("addBtnMemo").val();
                var url = '/menuAndFunction/add'
                if (editId) {
                    url = '/menuAndFunction/updateFunction'
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
                        getButtonList(btnObj.menuId);
                    })
                    .catch(err => {
                        console.log(err)
                    })
            },
            end: function (index) { // 模态框关闭事件
                $('#fun_add').removeClass('layui-show').addClass('layui-hide');
            }
        });
    }




    //递归循环 拼接菜单数据
    function handleMenuData(data) { //处理权限数据    
        var forFun = function (data, parentId) {
            var treeArr = [];
            for (var i = 0; i < data.length; i++) {
                var item = data[i];
                if (item.parentId == parentId) {
                    var itemObj = {
                        id: item.id,
                        value: item.menuId,
                        name: item.menuName,
                        parentId: item.parentId,
                        children: forFun(data, item.menuId)
                    };
                    treeArr.push(itemObj);
                }
            }
            return treeArr;
        }
        var menuArr = forFun(data, null);
        return menuArr;
    }

    //提示弹框点击
    $(".addMenuNameTip").on("click", function () {
        layer.tips('长度不超过20。', '.addMenuNameTip');
    })
    //提示弹框点击 
    $("#addSortIdTip").on("click", function () {
        layer.tips('字母、数字组合，长度不超过10，系统按照字符串升序。', '#addSortIdTip');
    })
    $("#addFunctionNameTip").on("click", function () {
        layer.tips('长度不超过20。', '#addFunctionNameTip');
    })

    initFun(); //页面加载时执行初始函数
});