

/*
 * @Author: jpp
 * @Date: 2019-11-18 10:38:00
 * @Last Modified by: xzl
 * @Last Modified time: 2019-12-18 10:25:00
 */
layui.use(['form', 'element', 'layer', 'table', 'laydate', 'jquery', 'laypage'], function () {
  var form = layui.form,
      layer = layui.layer,
      $ = layui.jquery,
      laydate = layui.laydate,
      table = layui.table,
      laypage = layui.laypage,
      element = layui.element;
  var siteObj = JSON.parse(sessionStorage.getItem('parkId'));
  var startTime = moment().add('months', -1).format('YYYY-MM-DD 00:00:00');
  var endTime = moment().add('days', 1).format('YYYY-MM-DD 00:00:00');
  var pageNum = 1;
  var pageLimit;
  laydate.render({
    //渲染开始时间
    elem: '#timeInput1',
    value: startTime,
    type: 'datetime',
    done: function done(value) {
      startTime = value;
    }
  });
  laydate.render({
    //渲染结束时间
    elem: "#timeInput2",
    value: endTime,
    type: 'datetime',
    done: function done(value) {
      endTime = value;
    }
  });
  getTableData(); //查询点击事件

  $(".query").on("click", function () {
    pageNum = 1;
    getTableData();
  }); //新增点击事件

  $(".add").on('click', function () {
    $(".edit_announcementTitle").addClass('layui-hide');
    $(".add_announcementTitle").removeClass('layui-hide');
    $("#announcementTitle").val('');
    $("#announcementContent").val('');
    $("#memo").val('');
    showModel("新增公告");
  }); //修改点击事件

  $(".edit").on('click', function () {
    $(".edit_announcementTitle").removeClass('layui-hide');
    $(".add_announcementTitle").addClass('layui-hide');
    var tableSelect = table.checkStatus('table1').data;

    if (tableSelect.length === 0) {
      return layer.msg("请选择需要修改的数据！");
    }

    if (tableSelect.length > 1) {
      return layer.msg("只能选择单条数据！");
    }

    var editData = tableSelect[0];
    $("#edit_announcementTitle").html(editData.noticeTitle);
    $("#announcementContent").val(editData.noticeContent);
    $("#memo").val(editData.memo);
    showModel("修改公告", editData);
  }); //删除点击事件

  $('.delete').click(function () {
    var tableSelect = table.checkStatus('table1').data;

    if (tableSelect.length == 0) {
      return layer.msg('请选择需要删除的数据');
    }

    if (tableSelect.length > 1) {
      return layer.msg("只能选择单条数据！");
    }

    $("#delete_announcementTitle").html(tableSelect[0].noticeTitle);
    layer.open({
      type: 1,
      title: '删除公告',
      closeBtn: 1,
      shade: 0,
      anim: 1,
      maxmin: true,
      //开启最大化最小化按钮
      area: ['350px', '250px'],
      content: $('#announcement_delete'),
      btn: ['确定', '取消'],
      success: function success() {
        $("#announcement_delete").removeClass('layui-hide');
      },
      yes: function yes(index) {
        load();
        var tableSelect = table.checkStatus('table1').data;
        request.service({
          method: 'get',
          url: '/notice/delete/' + tableSelect[0].id
        }).then(function (res) {
          disLoad();
          getTableData();
          layer.close(index);
        })["catch"](function (err) {
      disLoad();
      console.log(err);
    });
      },
      end: function end(index) {
        $("#announcement_delete").addClass('layui-hide');
      }
    });
  }); //新增修改 提示点击 - 公告标题

  $("#announcementTitle_tip").on("click", function () {
    layer.tips('限制30个字符', '#announcementTitle_tip');
  }); //新增修改 提示点击 - 公告内容

  $("#announcementContent_tip").on("click", function () {
    layer.tips('限制1000个字符', '#announcementContent_tip');
  }); //监听提交

  form.on('submit(*)', function (data) {
    return false; //阻止表单跳转
  });
  /*----------------------------------函数------------------------------------*/

  function getTableData() {
    if (startTime && endTime) {
      if (startTime > endTime) {
        return layer.msg("开始时间不能大于结束时间!");
      }
    }

    var tnum = parseInt(($(".announcement_table").height() - 90) / 42); //动态生成表格展示条数

    load();
    request.service({
      method: 'post',
      url: '/notice/query',
      data: {
        'limit': pageLimit ? pageLimit : tnum,
        'page': pageNum,
        'key': {
          'objType': siteObj.type,
          'objId': siteObj.id,
          'start': moment(startTime).valueOf(),
          'end': moment(endTime).valueOf()
        }
      }
    }).then(function (res) {
      disLoad();
      var data = handleNum(res.data, pageNum, pageLimit);
      renderTableContent(data, pageLimit ? pageLimit : tnum);
      renderPage(res.count, pageLimit ? pageLimit : tnum);
    })["catch"](function (err) {
      disLoad();
      console.log(err);
    });
  } //渲染表格数据


  function renderTableContent(data, limit) {
    table.render({
      elem: '#table1',
      id: 'table1',
      height: 'full-165',
      cols: [[{
        field: 'number',
        align: "center",
        width: 48,
        fixed: "left"
      }, {
        type: "radio",
        fixed: "left"
      }, {
        field: 'noticeTitle',
        align: "center",
        width: 120,
        title: '公告标题'
      }, {
        field: 'noticeContent',
        align: "center",
        title: '公告内容'
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
      limit: limit
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
          getTableData();
        }
      }
    });
  }

  function showModel(title, editData) {
    //模态框调用事件
    layer.open({
      type: 1,
      title: title,
      closeBtn: 1,
      shade: 0.3,
      maxmin: true,
      anim: 1,
      area: ['450px', '400px'],
      content: $('#announcement_add'),
      btn: ['确定', '取消'],
      success: function success() {
        $('#announcement_add').removeClass('layui-hide').addClass('layui-show');
      },
      yes: function yes(index) {
        var url = '';
        var data = {
          "objType": siteObj.type,
          "objId": siteObj.id,
          "id": '',
          "noticeTitle": '',
          "noticeContent": $("#announcementContent").val(),
          "memo": $("#memo").val()
        };

        if (editData) {
          var tableSelect = table.checkStatus('table1').data;
          data['id'] = tableSelect[0].id;
          data['noticeTitle'] = tableSelect[0].noticeTitle;
          data['createTime'] = tableSelect[0].createTime;
          url = '/notice/edit';
        } else {
          data['noticeTitle'] = $("#announcementTitle").val();

          if (data.noticeTitle == '') {
            return layer.msg('请输入公告标题!');
          }

          if (data.noticeTitle.length > 30) {
            return layer.msg('公告标题限制为30个字符!');
          }

          url = '/notice/add';
        }

        if (data.noticeContent == '') {
          return layer.msg('请输入公告内容!');
        }

        if (data.noticeContent.length > 1000) {
          return layer.msg('公告内容限制为1000个字符!');
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
        })["catch"](function (err) {
      disLoad();
      console.log(err);
    });
      },
      end: function end(index) {
        $('#announcement_add').removeClass('layui-show').addClass('layui-hide');
      }
    });
  }

  var indexLoading;

  function load() {
    indexLoading = layer.load(1, {
      shade: [0.3, '#fff']
    });
  }

  function disLoad() {
    layer.close(indexLoading);
  }
});