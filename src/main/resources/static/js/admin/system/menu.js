

/*
 * @Author: xzl 
 * @Date: 2019-10-17 14:38:00 
 * @Last Modified by: xzl
 * @Last Modified time: 2019-12-31 15:16:06
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
  } //查询点击事件


  $("#search_btn").on("click", function () {
    getRoleTabData();
  });
  var demo1 = xmSelect.render({
    el: '#selectIcon',
    prop: {
      name: 'icon',
      value: 'icon'
    },
    content: "\n            <table class=\"layui-table\"  lay-filter=\"demo\" >\n            <thead>\n            <tr>\n              <th lay-data=\"{field:'icon', width:180}\">\u6807\u8BC6</th>\n\t\t\t  <th lay-data=\"{field:'username', width:160}\">\u56FE\u6807</th>\n\t\t\t</tr> \n\t\t  </thead>\n              <tbody >\n<tr class=\"menuIcon map-o\" data-icon=\"fa fa-map-o\"><td>fa fa-map-o</td><td><i class=\"fa fa-map-o\"></i></td></tr>\n<tr class=\"menuIcon desktop\" data-icon=\"fa fa-desktop\"><td>fa fa-desktop</td><td><i class=\"fa fa-desktop\"></i></td></tr>\n<tr class=\"menuIcon sitemap\" data-icon=\"fa fa-sitemap\"><td>fa fa-sitemap</td><td><i class=\"fa fa-sitemap\"></i></td></tr>\n<tr class=\"menuIcon line-chart\" data-icon=\"fa fa-line-chart\"><td>fa fa-line-chart</td><td><i class=\"fa fa-line-chart\"></i></td></tr>\n<tr class=\"menuIcon eye\" data-icon=\"fa fa-eye\"><td>fa fa-eye</td><td><i class=\"fa fa-eye\"></i></td></tr>\n<tr class=\"menuIcon product-hunt\" data-icon=\"fa fa-product-hunt\"><td>fa fa-product-hunt</td><td><i class=\"fa fa-product-hunt\"></i></td></tr>\n<tr class=\"menuIcon calendar\" data-icon=\"fa fa-calendar\"><td>fa fa-calendar</td><td><i class=\"fa fa-calendar\"></i></td></tr>\n<tr class=\"menuIcon bar-chart\" data-icon=\"fa fa-bar-chart\"><td>fa fa-bar-chart</td><td><i class=\"fa fa-bar-chart\"></i></td></tr>\n<tr class=\"menuIcon pie-chart\" data-icon=\"fa fa-pie-chart\"><td>fa fa-pie-chart</td><td><i class=\"fa fa-pie-chart\"></i></td></tr>\n<tr class=\"menuIcon bell\" data-icon=\"fa fa-bell\"><td>fa fa-bell</td><td><i class=\"fa fa-bell\"></i></td></tr>\n<tr class=\"menuIcon pencil-square\" data-icon=\"fa fa-pencil-square\"><td>fa fa-pencil-square</td><td><i class=\"fa fa-pencil-square\"></i></td></tr>\n<tr class=\"menuIcon bell-o\" data-icon=\"fa fa-bell-o\"><td>fa fa-bell-o</td><td><i class=\"fa fa-bell-o\"></i></td></tr>\n<tr class=\"menuIcon cloud-upload\" data-icon=\"fa fa-cloud-upload\"><td>fa fa-cloud-upload</td><td><i class=\"fa fa-cloud-upload\"></i></td></tr>\n<tr class=\"menuIcon align-left\" data-icon=\"fa fa-align-left\"><td>fa fa-align-left</td><td><i class=\"fa fa-align-left\"></i></td></tr>\n<tr class=\"menuIcon clock-o\" data-icon=\"fa fa-clock-o\"><td>fa fa-clock-o</td><td><i class=\"fa fa-clock-o\"></i></td></tr>\n<tr class=\"menuIcon tasks\" data-icon=\"fa fa-tasks\"><td>fa fa-tasks</td><td><i class=\"fa fa-tasks\"></i></td></tr>\n<tr class=\"menuIcon table\" data-icon=\"fa fa-table\"><td>fa fa-table</td><td><i class=\"fa fa-table\"></i></td></tr>\n<tr class=\"menuIcon tablet\" data-icon=\"fa fa-tablet\"><td>fa fa-tablet</td><td><i class=\"fa fa-tablet\"></i></td></tr>\n<tr class=\"menuIcon folder\" data-icon=\"fa fa-folder\"><td>fa fa-folder</td><td><i class=\"fa fa-folder\"></i></td></tr>\n<tr class=\"menuIcon th-list\" data-icon=\"fa fa-th-list\"><td>fa fa-th-list</td><td><i class=\"fa fa-th-list\"></i></td></tr>\n<tr class=\"menuIcon database\" data-icon=\"fa fa-database\"><td>fa fa-database</td><td><i class=\"fa fa-database\"></i></td></tr>\n<tr class=\"menuIcon globe\" data-icon=\"fa fa-globe\"><td>fa fa-globe</td><td><i class=\"fa fa-globe\"></i></td></tr>\n<tr class=\"menuIcon building\" data-icon=\"fa fa-building\"><td>fa fa-building</td><td><i class=\"fa fa-building\"></i></td></tr>\n<tr class=\"menuIcon microchip\" data-icon=\"fa fa-microchip\"><td>fa fa-microchip</td><td><i class=\"fa fa-microchip\"></i></td></tr>\n<tr class=\"menuIcon wrench\" data-icon=\"fa fa-wrench\"><td>fa fa-wrench</td><td><i class=\"fa fa-wrench\"></i></td></tr>\n<tr class=\"menuIcon object-group\" data-icon=\"fa fa-object-group\"><td>fa fa-object-group</td><td><i class=\"fa fa-object-group\"></i></td></tr>\n<tr class=\"menuIcon newspaper-o\" data-icon=\"fa fa-newspaper-o\"><td>fa fa-newspaper-o</td><td><i class=\"fa fa-newspaper-o\"></i></td></tr>\n<tr class=\"menuIcon calendar-plus-o\" data-icon=\"fa fa-calendar-plus-o\"><td>fa fa-calendar-plus-o</td><td><i class=\"fa fa-calendar-plus-o\"></i></td></tr>\n<tr class=\"menuIcon jpy\" data-icon=\"fa fa-jpy\"><td>fa fa-jpy</td><td><i class=\"fa fa-jpy\"></i></td></tr>\n<tr class=\"menuIcon cog\" data-icon=\"fa fa-cog\"><td>fa fa-cog</td><td><i class=\"fa fa-cog\"></i></td></tr>\n<tr class=\"menuIcon user\" data-icon=\"fa fa-user\"><td>fa fa-user</td><td><i class=\"fa fa-user\"></i></td></tr>\n<tr class=\"menuIcon users\" data-icon=\"fa fa-users\"><td>fa fa-users</td><td><i class=\"fa fa-users\"></i></td></tr>\n<tr class=\"menuIcon graduation-cap\" data-icon=\"fa fa-graduation-cap\"><td>fa fa-graduation-cap</td><td><i class=\"fa fa-graduation-cap\"></i></td></tr>\n<tr class=\"menuIcon bars\" data-icon=\"fa fa-bars\"><td>fa fa-bars</td><td><i class=\"fa fa-bars\"></i></td></tr>\n<tr class=\"menuIcon bolt\" data-icon=\"fa fa-bolt\"><td>fa fa-bolt</td><td><i class=\"fa fa-bolt\"></i></td></tr>\n<tr class=\"menuIcon sort-numeric-asc\" data-icon=\"fa fa-sort-numeric-asc\"><td>fa fa-sort-numeric-asc</td><td><i class=\"fa fa-sort-numeric-asc\"></i></td></tr>\n<tr class=\"menuIcon cogs\" data-icon=\"fa fa-cogs\"><td>fa fa-cogs</td><td><i class=\"fa fa-cogs\"></i></td></tr>\n<tr class=\"menuIcon file-text-o\" data-icon=\"fa fa-file-text-o\"><td>fa fa-file-text-o</td><td><i class=\"fa fa-file-text-o\"></i></td></tr>\n<tr class=\"menuIcon info-circle\" data-icon=\"fa fa-info-circle\"><td>fa fa-info-circle</td><td><i class=\"fa fa-info-circle\"></i></td></tr>\n       </tbody>\n            </table>\n        ",
    height: 200
  });
  layui.table.init('demo', {
    height: 240,
    limit: 100,
    done: function done(res) {
      demo1.update({
        data: res.data
      });
    }
  }).on('row(demo)', function (obj) {
    var values = demo1.getValue();
    var item = obj.data;

    if (values.length > 0) {
      demo1["delete"](values);
      demo1.append([item]);
    } else {
      demo1.append([item]);
    }

    demo1.closed();
  }); //初始函数

  function initFun() {
    getRoleTabData();
  }

  function getRoleTabData() {
    //ajax获取 数据  
    load();
    request.service({
      method: 'get',
      url: '/menuAndFunction/getAllMenu'
    }).then(function (res) {
      disLoad();
      renderMenuSelect(res.data);
      var roleTableData = handleMenuListData(res.data);
      console.log(roleTableData, '11');

      if (roleTableData.length > 0) {
        roleTableData[0].lay_is_radio = true; //默认选中第一个

        renderMenuTable(roleTableData);
        getButtonList(roleTableData[0].menuId);
      }
    })["catch"](function (err) {
      console.log(err);
    });
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
      treeShowName: 'title' //以树形式显示的字段
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
      cols: [[{
        type: 'radio'
      }, {
        field: 'title',
        width: 200,
        title: '菜单名称'
      }, {
        field: 'menuId',
        align: "center",
        width: 100,
        title: '菜单标识'
      }, {
        field: 'parentId',
        align: "center",
        width: 120,
        title: '父级菜单标识'
      }, {
        field: 'icon',
        align: "center",
        width: 100,
        title: '图标',
        templet: function templet(d) {
          return '<i class="' + d.icon + '"></i>';
        }
      }, {
        field: 'sortId',
        align: "center",
        title: '排序标识'
      }, {
        field: 'method',
        align: "center",
        width: 100,
        title: '请求方式'
      }, {
        field: 'url',
        align: "center",
        width: 150,
        title: '接口地址'
      }, {
        field: 'memo',
        align: "center",
        title: '备注',
        width: 150
      }, {
        field: 'createUserId',
        align: "center",
        title: '创建者',
        width: 110
      }, {
        field: 'createTime',
        align: "center",
        width: 160,
        title: '创建时间'
      }, {
        field: 'updateUserId',
        align: "center",
        title: '修改者',
        width: 110
      }, {
        field: 'updateTime',
        align: "center",
        width: 160,
        title: '修改时间'
      }]],
      data: data,
      onRadio: function onRadio(obj) {
        //单选事件
        getButtonList(obj.menuId);
      }
    });
  }

  $("#search_btn_fun").on("click", function () {
    //按钮功能查询事件
    var tableSelect = treeGrid.radioStatus(tableId);

    if (!tableSelect) {
      return layer.msg("请选择需要查询的菜单");
    }

    getButtonList(tableSelect.menuId);
  });

  function getButtonList(menuId) {
    //获取按钮列表
    load();
    request.service({
      method: 'get',
      url: '/menuAndFunction/getFunctionsByMenuId/' + menuId
    }).then(function (res) {
      disLoad();
      var data = res.data;
      renderButtonTable(data);
    })["catch"](function (err) {
      console.log(err);
    });
  } //渲染功能表格数据


  function renderButtonTable(data) {
    table.render({
      elem: '#role_table',
      height: 'full-110',
      cols: [[{
        type: 'numbers',
        align: "center",
        fixed: "left"
      }, {
        type: 'radio',
        fixed: "left"
      }, {
        field: 'functionId',
        align: "center",
        title: '功能标识',
        width: 120,
        fixed: "left"
      }, {
        field: 'functionName',
        align: "center",
        title: '功能名称',
        width: 200
      }, {
        field: 'functionDesc',
        align: "center",
        title: '功能说明',
        width: 150
      }, {
        field: 'method',
        align: "center",
        title: '请求方式',
        width: 90
      }, {
        field: 'url',
        align: "center",
        title: '接口地址',
        width: 135
      }, {
        field: 'memo',
        align: "center",
        title: '备注',
        width: 150
      }, {
        field: 'createUserId',
        align: "center",
        title: '创建者',
        width: 110
      }, {
        field: 'createTime',
        align: "center",
        title: '创建时间',
        width: 160
      }, {
        field: 'updateUserId',
        align: "center",
        title: '修改者',
        width: 110
      }, {
        field: 'updateTime',
        align: "center",
        title: '修改时间',
        width: 160
      }]],
      data: data,
      page: false,
      limit: 100
    });
  }

  function renderMenuSelect(data) {
    layui.formSelects.config('addParentMenuSelect', {
      keyName: 'title',
      //自定义返回数据中name的key, 默认 name
      keyVal: 'menuId',
      //自定义返回数据中value的key, 默认 value
      keySel: 'selected',
      //自定义返回数据中selected的key, 默认 selected
      keyDis: 'disabled',
      //自定义返回数据中disabled的key, 默认 disabled
      keyChildren: 'children',
      //联动多选自定义children
      delay: 500,
      //搜索延迟时间, 默认停止输入500ms后开始搜索
      direction: 'auto'
    });
    layui.formSelects.data('addParentMenuSelect', 'local', {
      arr: data
    });
  }

  var disSelectArr = [];
  $("#edit_menu").on('click', function () {
    //修改点击事件
    var tableSelect = treeGrid.radioStatus(tableId);

    if (!tableSelect) {
      return layer.msg("请选择需要修改的菜单");
    }

    disSelectArr.push(tableSelect);
    $("#addMenuId").val(tableSelect.menuId); //菜单标识

    $("#addMenuName").val(tableSelect.title); //菜单名称

    $("#addMethod").val(tableSelect.method); //请求方式

    $("#addUrl").val(tableSelect.url); //接口地址

    $("#addsortId").val(tableSelect.sortId); //排序标识

    var iconSelectArr = [];

    if (tableSelect.icon) {
      iconSelectArr.push(tableSelect.icon);
    }

    demo1.setValue(iconSelectArr);
    $("#addMemo").val(tableSelect.memo);
    var selectIdArr = [];

    if (tableSelect.parentId) {
      selectIdArr.push(tableSelect.parentId);
    }

    layui.formSelects.value('addParentMenuSelect', selectIdArr);
    form.render();
    showModelIndexBox(tableSelect.id, "编辑菜单");
  });
  layui.formSelects.on('addParentMenuSelect', function (id, vals, val, isAdd, isDisabled) {
    var selectType = handleMenuDisArr(disSelectArr, val.value);

    if (!selectType) {
      return false;
    }
  });

  function handleMenuDisArr(data, val) {
    var checkStatus = true;

    var forMenu = function forMenu(arr) {
      for (var i = 0; i < arr.length; i++) {
        var obj = arr[i];

        if (obj.menuId == val) {
          checkStatus = false;
        }

        if (obj.children) {
          forMenu(obj.children);
        }
      }
    };

    forMenu(data);
    return checkStatus;
  }

  function showModelIndexBox(editId, title) {
    //模态框调用事件
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
      success: function success() {
        $('#menu_add').removeClass('layui-hide').addClass('layui-show');
      },
      yes: function yes(index) {
        var menuId = $("#addMenuId").val(); //菜单标识

        var menuName = $("#addMenuName").val(); //菜单名称

        var parentIdArr = layui.formSelects.value('addParentMenuSelect', 'val'); //父级菜单 

        var parentId = null;

        if (parentIdArr.length > 0) {
          parentId = parentIdArr[0];
        }

        var sortId = $("#addsortId").val() || null; //排序标识

        var iconArr = demo1.getValue(); //图标

        var icon;

        if (iconArr.length > 0) {
          icon = iconArr[0].icon;
        }

        var memo = $("#addMemo").val(); //memo

        if (!menuName) {
          return layer.msg('请输入菜单名称');
        }

        var formData = {};
        formData.menuId = menuId;
        formData.menuName = menuName;
        formData.parentId = parentId;
        formData.sortId = sortId;
        formData.icon = icon;
        formData.memo = memo;
        formData.id = editId;
        console.log(formData, '11');
        load();
        request.service({
          method: 'post',
          url: '/menuAndFunction/updateMenu',
          data: formData
        }).then(function (res) {
          disLoad();
          layer.close(index);
          getRoleTabData();
        })["catch"](function (err) {
          console.log(err);
        });
      },
      end: function end(index) {
        // 模态框关闭事件
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
    showModelAuthBox(null, tableSelectMenu, '新增功能');
  });
  $("#edit_btn").on('click', function () {
    //按钮修改事件
    var tableSelectBtn = table.checkStatus('role_table').data;
    var tableSelectMenu = treeGrid.radioStatus(tableId);

    if (tableSelectBtn.length === 0) {
      return layer.msg("请选择需要编辑的功能！");
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
    showModelAuthBox(tableSelectBtn[0].id, tableSelectBtn[0], '编辑功能');
  });

  function showModelAuthBox(editId, btnObj, title) {
    //模态框调用事件
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
      success: function success() {
        $('#fun_add').removeClass('layui-hide').addClass('layui-show');
      },
      yes: function yes(index) {
        var functionName = $("#addFunctionName").val();
        var functionId = $("#functionId").val();
        var functionDesc = $("#addFunctionDsc").val();
        var method = $("#addBtnMethod").val();
        var url = $("#addBtnUrl").val();

        if (!functionId) {
          return layer.msg("请输入功能标识");
        }

        if (!functionName) {
          return layer.msg("请输入功能名称");
        }

        if (!functionDesc) {
          return layer.msg("请输入功能描述");
        }

        var formData = {};
        formData.menuId = btnObj.menuId;
        formData.functionId = functionId;
        formData.functionName = functionName;
        formData.functionDesc = functionDesc;
        formData.url = url;
        formData.method = method;
        formData.memo = $("addBtnMemo").val();
        var url = '/menuAndFunction/add';

        if (editId) {
          url = '/menuAndFunction/updateFunction';
        }

        load();
        request.service({
          method: 'post',
          url: url,
          data: formData
        }).then(function (res) {
          disLoad();
          layer.close(index);
          getButtonList(btnObj.menuId);
        })["catch"](function (err) {
          console.log(err);
        });
      },
      end: function end(index) {
        // 模态框关闭事件
        $('#fun_add').removeClass('layui-show').addClass('layui-hide');
      }
    });
  } // //递归循环 拼接菜单数据
  // function handleMenuData(data) { //处理权限数据    
  //     var forFun = function (data, parentId) {
  //         var treeArr = [];
  //         for (var i = 0; i < data.length; i++) {
  //             var item = data[i];
  //             if (item.parentId == parentId) {
  //                 var itemObj = {
  //                     id: item.id,
  //                     value: item.menuId,
  //                     name: item.menuName,
  //                     parentId: item.parentId,
  //                     children: forFun(data, item.menuId)
  //                 };
  //                 treeArr.push(itemObj);
  //             }
  //         }
  //         return treeArr;
  //     }
  //     var menuArr = forFun(data, null);
  //     return menuArr;
  // }


  function handleMenuListData(data) {
    var treeArr = [];

    var forFun = function forFun(data) {
      for (var i = 0; i < data.length; i++) {
        var item = data[i];
        treeArr.push(item);

        if (item.children) {
          forFun(item.children);
        }
      }
    };

    forFun(data);
    return treeArr;
  } //提示弹框点击


  $(".addMenuNameTip").on("click", function () {
    layer.tips('长度不超过20。', '.addMenuNameTip');
  }); //提示弹框点击 

  $("#addSortIdTip").on("click", function () {
    layer.tips('字母、数字、下划线组合，长度不超过10，系统按照字符串升序。', '#addSortIdTip');
  });
  $("#addFunctionNameTip").on("click", function () {
    layer.tips('长度不超过20。', '#addFunctionNameTip');
  });
  initFun(); //页面加载时执行初始函数
});