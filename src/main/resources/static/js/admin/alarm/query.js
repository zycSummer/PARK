

/*
 * @Author: xzl 
 * @Date: 2019-10-31 13:58:49 
 * @Last Modified by: xzl
 * @Last Modified time: 2019-12-17 16:21:36
 */
layui.use(['form', 'element', 'layer', 'table', 'jquery', 'laypage', 'laydate'], function () {
  var form = layui.form,
      layer = layui.layer,
      $ = layui.jquery,
      table = layui.table,
      laypage = layui.laypage,
      laydate = layui.laydate,
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
    getAlarmInfoData();
  });
  var startTime = moment().add('days', -1).format('YYYY-MM-DD 00:00:00');
  var endTime = moment().add('days', 1).format('YYYY-MM-DD 00:00:00');
  var ackStatusSelect; //初始函数

  function initFun() {
    laydate.render({
      //渲染开始时间
      elem: '#search_start_time',
      value: startTime,
      type: 'datetime',
      done: function done(value) {
        startTime = value;
      }
    });
    laydate.render({
      //渲染结束时间
      elem: "#search_end_time",
      value: endTime,
      type: 'datetime',
      done: function done(value) {
        endTime = value;
      }
    });
    ackStatusSelect = xmSelect.render({
      el: '#status_select',
      data: [{
        name: "Y",
        value: true
      }, {
        name: "N",
        value: false
      }]
    });
    getAlarmInfoData(); //getAllObjList();
  }

  var pageNum = 1;
  var pageLimit;

  function getAlarmInfoData() {
    //ajax获取 数据
    var ackStatusSelectArr = ackStatusSelect.getValue(); //获取确认状态 所选择的值

    var ackStatusList = [];

    if (ackStatusSelectArr.length > 0) {
      ackStatusSelectArr.forEach(function (item) {
        ackStatusList.push(item.value);
      });
    }

    var tnum = parseInt(($(".tab_content").height() - 60) / 40); //动态生成表格展示条数

    var formData = {};
    formData.limit = pageLimit ? pageLimit : tnum;
    formData.page = pageNum;
    var key = {};
    key.alarmName = $("#search_alarmName").val();
    key.ackStatusList = ackStatusList;
    key.start = moment(startTime).valueOf();
    key.end = moment(endTime).valueOf();

    if (key.start && key.end) {
      if (key.start > key.end) {
        return layer.msg("开始时间不能大于结束时间!");
      }
    }

    key.objType = JSON.parse(sessionStorage.getItem("parkId")).type;
    key.objId = JSON.parse(sessionStorage.getItem("parkId")).id;
    formData.key = key;
    load();
    request.service({
      method: 'post',
      url: '/alarmMsg/query',
      data: formData
    }).then(function (res) {
      disLoad();
      var tabData = res.data;

      if (tabData.length > 0) {
        tabData.forEach(function (item) {
          switch (item.isAck) {
            case true:
              item.isAckText = 'Y';
              break;

            case false:
              item.isAckText = 'N';
              break;
          }
        });
      }

      renderTableContent(res.data, formData.limit);
      renderPage(res.count, tnum);
    })["catch"](function (err) {
      console.log(err);
    });
  } //渲染表格数据


  function renderTableContent(data, limit) {
    table.render({
      elem: '#role_table',
      height: 'full-165',
      cols: [[{
        type: "checkbox",
        fixed: "left"
      }, {
        field: 'alarmId',
        align: "center",
        title: '报警标识',
        width: 120,
        fixed: "left"
      }, {
        field: 'alarmName',
        align: "center",
        width: 200,
        title: '报警名称'
      }, {
        field: 'alarmType',
        align: "center",
        title: '报警类型'
      }, {
        field: 'msg',
        align: "center",
        title: '报警信息'
      }, {
        field: 'alarmTime',
        align: "center",
        title: '报警时间',
        width: 160
      }, {
        field: 'recoveryTime',
        align: "center",
        title: '恢复时间',
        width: 160
      }, {
        field: 'isAckText',
        align: "center",
        width: 90,
        title: '确认状态'
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
          getAlarmInfoData();
        }
      }
    });
  }

  $("#confirm_btn").on('click', function () {
    //确认点击事件
    var tableSelect = table.checkStatus('role_table').data;

    if (tableSelect.length == 0) {
      return layer.msg('请选择需要确认的报警记录！');
    }

    load();
    var checkList = [];
    tableSelect.forEach(function (item) {
      checkList.push(item.id);
    });
    request.service({
      method: 'post',
      url: '/alarmMsg/ack',
      data: checkList
    }).then(function (res) {
      disLoad();
      getAlarmInfoData();
      layer.msg('确认成功！');
    })["catch"](function (err) {
      console.log(err);
    });
  });
  $("#memo_btn").on('click', function () {
    //修改点击事件
    var tableSelect = table.checkStatus('role_table').data;

    if (tableSelect.length === 0) {
      return layer.msg("请选择需要备注的报警记录！");
    }

    showModelIndexBox(tableSelect, "备注");
  });

  function showModelIndexBox(tableSelect, title) {
    //模态框调用事件
    layer.open({
      type: 1,
      title: title,
      closeBtn: 1,
      shade: 0.3,
      maxmin: true,
      anim: 1,
      area: ['500px', '300px'],
      content: $('#memo_add'),
      btn: ['保存', '关闭'],
      success: function success() {
        $('#memo_add').removeClass('layui-hide').addClass('layui-show');
      },
      yes: function yes(index) {
        var memo = $("#add_memo").val(); //memo

        if (!memo) {
          return layer.msg('请输入备注');
        }

        var formData = {};
        formData.memo = memo;
        var ids = [];
        tableSelect.forEach(function (item) {
          ids.push(item.id);
        });
        formData.ids = ids;
        load();
        request.service({
          method: 'post',
          url: '/alarmMsg/updateMemo',
          data: formData
        }).then(function (res) {
          disLoad();
          layer.close(index);
          getAlarmInfoData();
        })["catch"](function (err) {
          console.log(err);
        });
      },
      end: function end(index) {
        // 模态框关闭事件
        $('#memo_add').removeClass('layui-show').addClass('layui-hide');
      }
    });
  }

  initFun(); //页面加载时执行初始函数
});