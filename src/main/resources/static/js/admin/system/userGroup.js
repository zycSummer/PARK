

/*
 * @Author: xzl 
 * @Date: 2019-10-17 14:38:00 
 * @Last Modified by: xzl
 * @Last Modified time: 2019-12-19 11:12:54
 */
layui.config({
  base: "/public/lib/layui_exts/",
  version: true
}).extend({
  eleTree: "eleTree"
}).use(['form', 'element', 'layer', 'table', 'jquery', 'laypage', 'laydate', 'eleTree'], function () {
  var form = layui.form,
      layer = layui.layer,
      $ = layui.jquery,
      table = layui.table,
      laypage = layui.laypage,
      laydate = layui.laydate,
      eleTree = layui.eleTree,
      element = layui.element;
  var indexLoading;

  function load() {
    indexLoading = layer.load(1, {
      shade: [0.3, '#fff']
    });
  }

  function disLoad() {
    layer.close(indexLoading);
  } //查询点击事件


  $("#search_btn").on("click", function () {
    getUserGroupData();
  }); //初始函数

  function initFun() {
    getUserGroupData(); //getAllObjList();
  }

  var pageNum = 1;
  var pageLimit;

  function getUserGroupData() {
    //ajax获取 数据
    var tnum = parseInt(($(".tab_content").height() - 60) / 40); //动态生成表格展示条数

    var formData = {};
    formData.limit = pageLimit ? pageLimit : tnum;
    formData.page = pageNum;
    var key = {};
    key.userGroupId = $("#user_code").val();
    key.userGroupName = $("#user_name").val();
    formData.key = key;
    load();
    request.service({
      method: 'post',
      url: '/userGroup/query',
      data: formData
    }).then(function (res) {
      disLoad();
      var tabData = res.data;

      if (tabData.length > 0) {
        tabData[0].LAY_CHECKED = true;
      }

      tabData = handleNum(tabData, pageNum, pageLimit);
      renderTableContent(tabData, formData.limit);
      renderPage(res.count, tnum);
    })["catch"](function (err) {
      console.log(err);
    });
  } //渲染表格数据


  function renderTableContent(data, limit) {
    table.render({
      elem: '#role_table',
      height: 'full-155',
      cols: [[{
        field: 'number',
        align: "center",
        width: 48,
        fixed: 'left'
      }, {
        type: 'radio',
        fixed: 'left'
      }, {
        field: 'userGroupId',
        align: "center",
        title: '用户组ID',
        width: 150,
        fixed: 'left'
      }, {
        field: 'userGroupName',
        align: "center",
        title: '用户组名'
      }, {
        field: 'memo',
        align: "center",
        width: 150,
        title: '备注'
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
      limit: limit,
      done: function done() {
        getRightTree();
      }
    });
  } //渲染分页模块


  function renderPage(count, tnum) {
    laypage.render({
      elem: 'table_page',
      count: count,
      //数据总数，从服务端得到
      curr: pageNum,
      limit: pageLimit ? pageLimit : tnum,
      limits: [tnum, 20, 30, 50],
      layout: ['count', 'prev', 'page', 'next', 'limit', 'skip'],
      jump: function jump(obj, first) {
        //首次不执行
        if (!first) {
          pageNum = obj.curr;
          pageLimit = obj.limit;
          getUserGroupData();
        }
      }
    });
  }

  table.on('radio(role_table)', function (obj) {
    //表格单选切换事件 
    getRightTree();
  });
  var rightTree;

  function getRightTree() {
    //获取右侧用户组权限树
    var checkStatusData = table.checkStatus('role_table').data; //选中状态值

    if (checkStatusData.length == 0) {
      return layer.msg("请选择用户组");
    }

    load();
    request.service({
      method: 'get',
      url: '/userGroup/queryParkAndSiteByUserGroupId/' + checkStatusData[0].userGroupId
    }).then(function (res) {
      disLoad();
      var roleTreeData = [];
      roleTreeData[0] = res.one;
      rightTree = '';
      roleTreeData = handleRoleTreeData(roleTreeData, true).data;
      rightTree = eleTree.render({
        elem: '#roleTree' //绑定元素
        ,
        data: roleTreeData,
        showCheckbox: true,
        checkStrictly: true,
        defaultExpandAll: true
      }); //重新加载树

      var checkedData = handleRoleTreeData(roleTreeData, true).checkedData;
      rightTree.setChecked(checkedData, true); //批量勾选
    })["catch"](function (err) {
      console.log(err);
    });
  } //获取所有园区


  var addRightTree;

  function getAllSite() {
    load();
    request.service({
      method: 'get',
      url: '/userGroup/queryParkAndSite'
    }).then(function (res) {
      disLoad();
      var addRoleTreeData = [];
      addRoleTreeData[0] = res.one;
      addRoleTreeData = handleRoleTreeData(addRoleTreeData, false).data;
      addRightTree = eleTree.render({
        elem: '#addRoleTree' //绑定元素
        ,
        data: addRoleTreeData,
        showCheckbox: true,
        checkStrictly: true,
        defaultExpandAll: true
      });
    })["catch"](function (err) {
      console.log(err);
    });
  }

  getAllSite();
  $("#add_btn").on('click', function () {
    //新增点击事件
    $('#add_user_groupId').attr("disabled", false);
    $("#add_user_groupId").val(''); //用户组ID

    $("#add_user_groupName").val(''); //用户组名

    $("#memo").val(''); //备注

    addRightTree.setChecked([], true); //批量勾选

    form.render();
    showModelIndexBox(null, "新增");
  });
  $("#edit_btn").on('click', function () {
    //修改点击事件
    var tableSelect = table.checkStatus('role_table').data;

    if (tableSelect.length === 0) {
      return layer.msg("请选择需要修改的用户组！");
    }

    request.service({
      method: 'get',
      url: '/userGroup/queryByUserGroupId/' + tableSelect[0].userGroupId
    }).then(function (res) {
      disLoad();
      var editData = res.one;
      $('#add_user_groupId').attr("disabled", true); //禁止修改用户组ID

      $("#add_user_groupId").val(editData.userGroupId); //用户组ID

      $("#add_user_groupName").val(editData.userGroupName); //用户组名

      $("#memo").val(editData.memo); //备注

      var editRoleTree = editData.list;
      var setSelectArr = [];

      if (editRoleTree.length > 0) {
        editRoleTree.forEach(function (item) {
          setSelectArr.push(item.objId + '_' + item.objType);
        });
      }

      addRightTree.setChecked(setSelectArr, true); //批量勾选

      form.render();
      showModelIndexBox(editData.id, "编辑");
    })["catch"](function (err) {
      console.log(err);
    });
  });

  function showModelIndexBox(editId, title) {
    //模态框调用事件
    layer.open({
      type: 1,
      title: title,
      closeBtn: 1,
      shade: 0.3,
      maxmin: true,
      anim: 1,
      area: ['800px', '600px'],
      content: $('#role_add'),
      btn: ['保存', '关闭'],
      success: function success() {
        $('#role_add').removeClass('layui-hide').addClass('layui-show');
      },
      yes: function yes(index) {
        var userGroupId = $("#add_user_groupId").val(); //用户组ID

        var userGroupName = $("#add_user_groupName").val(); //用户组名

        var roleArr = addRightTree.getChecked();

        if (!userGroupId) {
          return layer.msg('请输入用户组ID！');
        }

        if (!userGroupName) {
          return layer.msg('请输入用户组名！');
        }

        if (roleArr.length == 0) {
          return layer.msg('请选择所关联的对象');
        }

        var obj = [];
        roleArr.forEach(function (item) {
          var roleObj = {};
          roleObj.objId = item.id.split('_')[0];
          roleObj.objType = item.id.split('_')[1];
          obj.push(roleObj);
        });
        var formData = {};
        formData.userGroupId = userGroupId;
        formData.userGroupName = userGroupName;
        formData.obj = obj;
        formData.memo = $("#memo").val();
        formData.id = editId;
        var url = '/userGroup/add';

        if (editId) {
          url = '/userGroup/edit';
        }

        load();
        request.service({
          method: 'post',
          url: url,
          data: formData
        }).then(function (res) {
          disLoad();
          layer.close(index);
          getUserGroupData();
        })["catch"](function (err) {
          console.log(err);
        });
      },
      end: function end(index) {
        // 模态框关闭事件
        $('#role_add').removeClass('layui-show').addClass('layui-hide');
      }
    });
  }

  $('#del_btn').click(function () {
    //删除点击事件
    var tableSelect = table.checkStatus('role_table').data;

    if (tableSelect.length == 0) {
      return layer.msg('请选择需要删除的用户组！');
    }

    layer.open({
      type: 1,
      title: "删除用户组",
      shade: 0.3,
      btn: ['确定', '取消'],
      area: ['320px', '320px'],
      //宽高
      content: '<div id="park_select_tree"> <form class="layui-form" style="margin:30px;">' + '   <div class="layui-form-item "><label>用户组ID：</label>' + '<div class="layui-inline">' + tableSelect[0].userGroupId + '</div></div>' + '   <div class="layui-form-item "><label>用户组名称：</label>' + '<div class="layui-inline" >' + tableSelect[0].userGroupName + '</div></div>' + '   <div class="layui-form-item " style="color:red;">确定要删除此报警吗？此用户组与对象(园区、企业)的关联关系一并删除! 。用户管理中涉及到的用户组关联需要重新手动设置! </div>' + '</form></div>',
      success: function success() {},
      yes: function yes(index) {
        load();
        request.service({
          method: 'get',
          url: '/userGroup/delete/' + tableSelect[0].userGroupId
        }).then(function (res) {
          disLoad();
          layer.close(index);
          getUserGroupData();
        })["catch"](function (err) {
          console.log(err);
        });
      },
      end: function end(index) {
        // 模态框关闭事件
        layer.close(index);
      }
    });
  });

  function handleRoleTreeData(data, type) {
    //处理权限数据
    var checkedData = [];

    if (data.length > 0) {
      var forfun = function forfun(arr) {
        arr.forEach(function (item) {
          item.label = '[' + item.objId + ']' + item.name;
          item.id = item.objId + "_" + item.objType;

          if (type) {
            item.disabled = true;
          }

          if (item.isRelate) {
            checkedData.push(item.id);
          }

          if (item.children) {
            forfun(item.children);
          }
        });
      };
    }

    forfun(data);
    return {
      "data": data,
      "checkedData": checkedData
    };
  } //提示弹框点击 -用户组ID


  $("#add_userGroupId_tip").on("click", function () {
    layer.tips('字母、数字、下划线组合，长度不超过20。', '#add_userGroupId_tip');
  }); //提示弹框点击 -用户组名称

  $("#add_userGroupName_tip").on("click", function () {
    layer.tips('长度不超过30。', '#add_userGroupName_tip');
  });
  initFun(); //页面加载时执行初始函数
});