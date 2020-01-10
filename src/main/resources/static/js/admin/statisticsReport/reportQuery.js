

/*
 * @Author: jpp
 * @Date: 2019-11-07 15:07:34
 * @Last Modified by: xzl
 * @Last Modified time: 2019-12-17 16:39:22
 */
layui.use(['request', 'form', 'element', 'layer', 'jquery', 'laydate'], function () {
  var form = layui.form,
      layer = layui.layer,
      treeGrid = layui.treeGrid,
      laydate = layui.laydate,
      $ = layui.jquery,
      element = layui.element;
  var siteObj = JSON.parse(sessionStorage.getItem('parkId'));
  var timeType = 'day'; //年、月、日

  var tableCols = [],
      tableData = [];
  funcsAuthority(); //点击 年、月、日

  $(".timeType a").on('click', function () {
    $(this).siblings().removeClass('layui-this');
    $(this).addClass('layui-this');
    var idx = $(this).attr('data-id');

    if (idx !== timeType) {
      timeType = idx;
    }

    createInterval();
    return false;
  }); //查询

  $(document).on("click", ".query", function () {
    getTableData();
    return false;
  });
  $(".export").on('click', function () {
    var reportName = $("#reportId option:selected").attr('data-title');

    if (reportName) {
      $("#table_title").html(reportName + '<span id="subTitle">(' + $("#timeInput").val() + ')</span>');
    } else {
      $("#table_title").html('');
    }

    var time = $("#timeInput").val(),
        timestamp;

    if (timeType == 'year') {
      timestamp = moment(time, 'YYYY').valueOf();
    } else if (timeType == 'month') {
      timestamp = moment(time, 'YYYY-MM').valueOf();
    } else {
      timestamp = moment(time, 'YYYY-MM-DD').valueOf();
    }

    var title = reportName + '(' + $("#timeInput").val() + ')';
    var url = '/reportQuery/exportExcel?fileName=' + title + '&timeUnit=' + timeType + '&data=' + timestamp + '&reportId=' + $("#reportId").val() + '&objType=' + siteObj.type + '&objId=' + siteObj.id;
    window.location.href = encodeURI(url);
  }); //监听提交

  form.on('submit(*)', function (data) {
    return false; //阻止表单跳转
  });
  /*----------------------------------函数------------------------------------*/

  function funcsAuthority() {
    var date = new Date();
    $("#timeInput").val(formateDate(date.valueOf()));
    laydate.render({
      elem: '#timeInput',
      type: 'date',
      lang: 'cn',
      value: new Date(),
      btns: ['now', 'confirm']
    });
    form.render('select');
    getReportData();
  }

  function createInterval() {
    $("#timeDiv").html('<input type="text" class="layui-input dateInput" id="timeInput">');
    var typeTime = 'date';

    if (timeType == 'day') {
      typeTime = 'date';
    } else if (timeType == 'month') {
      typeTime = 'month';
    } else {
      typeTime = 'year';
    }

    laydate.render({
      elem: '#timeInput',
      type: typeTime,
      lang: 'cn',
      value: new Date(),
      btns: ['now', 'confirm']
    });
    element.render('breadcrumb');
  } //报表下拉框


  function getReportData() {
    if (siteObj) {
      load();
      request.service({
        method: 'post',
        url: '/reportQuery/queryReport',
        data: {
          'objType': siteObj.type,
          'objId': siteObj.id
        }
      }).then(function (res) {
        disLoad();
        var str = '';
        $.each(res.data, function (ii, vv) {
          str += '<option value="' + vv.reportId + '" data-title="' + vv.reportName + '">' + vv.showName + '</option>';
        });
        $("#reportId").html(str);
        form.render('select');
        getTableData();
      })["catch"](function (err) {
      disLoad();
      console.log(err);
    });
    }
  }

  function getTableData() {
    var reportName = $("#reportId option:selected").attr('data-title');

    if (reportName) {
      $("#table_title").html(reportName + '<span id="subTitle">(' + $("#timeInput").val() + ')</span>');
    } else {
      $("#table_title").html('');
    }

    var time = $("#timeInput").val(),
        timestamp;

    if (timeType == 'year') {
      timestamp = moment(time, 'YYYY').valueOf();
    } else if (timeType == 'month') {
      timestamp = moment(time, 'YYYY-MM').valueOf();
    } else {
      timestamp = moment(time, 'YYYY-MM-DD').valueOf();
    }

    if (siteObj) {
      load();
      request.service({
        method: 'post',
        url: '/reportQuery/query',
        data: {
          'objType': siteObj.type,
          'objId': siteObj.id,
          'reportId': $("#reportId").val(),
          'date': timestamp,
          'timeUnit': timeType
        }
      }).then(function (res) {
        disLoad();
        var cols = renderReprotHeader(res.one);
        var list = recursiveBody(res.one);
        renderTable(list, cols);
      })["catch"](function (err) {
      disLoad();
      console.log(err);
    });
    }
  } //拼接表头数据


  function renderReprotHeader(ls) {
    var cols = [];
    var colsFirst = [{
      type: 'numbers',
      rowspan: 3,
      width: 70,
      align: 'center',
      title: '序号'
    }, {
      field: 'nodeName',
      rowspan: 3,
      width: 200,
      title: '节点名称'
    }];
    var colsSecond = [],
        colThree = [];
    var timeArr = ls.timeStamps ? ls.timeStamps : [];
    $.each(timeArr, function (ii, vv) {
      var colObj1 = {},
          len1 = 0;
      colObj1.align = 'center';
      var arr = ls.reportTableVOS;
      $.each(arr, function (i2, v2) {
        var colObj2 = {};
        colObj2.align = 'center';
        colObj2.title = v2.displayName;
        var len2 = 0;

        if (v2.avgValue == 'Y') {
          len2 += 1;
          colThree.push({
            field: 'avgValue' + ii + v2.energyParaId,
            title: '平均值',
            width: 100,
            align: 'center'
          });
        }

        if (v2.diffValue == 'Y') {
          len2 += 1;
          colThree.push({
            field: 'diffValue' + ii + v2.energyParaId,
            title: '差值',
            width: 100,
            align: 'center'
          });
        }

        if (v2.maxValue == 'Y') {
          len2 += 1;
          colThree.push({
            field: 'maxValue' + ii + v2.energyParaId,
            title: '最大值',
            width: 100,
            align: 'center'
          });
        }

        if (v2.minValue == 'Y') {
          len2 += 1;
          colThree.push({
            field: 'minValue' + ii + v2.energyParaId,
            title: '最小值',
            width: 100,
            align: 'center'
          });
        }

        if (v2.timeValue == 'Y') {
          len2 += 1;
          colThree.push({
            field: 'timeValue' + ii + v2.energyParaId,
            title: '时刻值',
            width: 100,
            align: 'center'
          });
        }

        len1 += len2;

        if (len2 != 1) {
          colObj2.colspan = len2;
        } else {
          colObj2.colspan = 1.5;
          colObj2.rowspan = 1.5;
        }

        colsSecond.push(colObj2);
      });
      colObj1.colspan = len1;
      var date = new Date(vv);

      switch (timeType) {
        case 'day':
          //当所选为天时
          var h = date.getHours();
          h = h < 10 ? '0' + h : h;
          colObj1.title = h + '时';
          break;

        case 'month':
          //当所选为月时
          var d = date.getDate();
          d = d < 10 ? '0' + d : d;
          colObj1.title = d + '日';
          break;

        case 'year':
          //当所选为年时
          var M = date.getMonth() + 1;
          M = M < 10 ? '0' + M : M;
          colObj1.title = M + '月';
          break;
      }

      colsFirst.push(colObj1);
    });
    cols.push(colsFirst, colsSecond, colThree);
    return cols;
  } //渲染表格数据


  function renderTable(data, cols) {
    tableCols = cols;
    tableData = data;
    treeGrid.render({
      id: 'table1',
      height: $("#table1Content").height() - 80,
      elem: '#table1',
      idField: 'nodeId',
      // cellMinWidth: 150,
      treeId: 'nodeId' //树形id字段名称
      ,
      treeUpId: 'parentId' //树形父id字段名称
      ,
      treeShowName: 'nodeName' //以树形式显示的字段
      ,
      isOpenDefault: true //节点默认是展开还是折叠【默认展开】
      // ,cols: [[ //表头
      //     {field: 'id', title: 'ID',rowspan:2, width:80}
      //     ,{title: '用户名',colspan:1.5, width:80}
      //     ,{title: '其他',colspan:1.5,width:80}
      // ],[
      //     {field: 'nodeName', title: '最大值', width:80}
      //     ,{field: 'parentId', title: '最小值', width:80}
      // ]],
      // data:[{'id':'2','nodeId':1,'nodeName':5,'parentId':4}]
      ,
      cols: cols,
      data: data,
      page: false,
      limit: 999999
    });

    $.each($("#table1Content .layui-table-header tr:nth-child(3) th"),function (index) {
        var w = $(this).width();
        var idx = index+3;
        $(".layui-table-body tr td:nth-child("+idx+") .layui-table-cell").width(w-30);
    });
    $("#table1Content .layui-table-body td[data-field='0'] .layui-table-cell").width(70);
    $("#table1Content .layui-table-header tr:nth-child(1) th .laytable-cell-numbers").width(70);
    $("#table1Content>.layui-table-view").height($("#table1Content").height()-10);
  } //表格数据


  function recursiveBody(ls) {
    var data = [];
    var arrTable = ls.reportInfoVOS ? ls.reportInfoVOS : [];
    $.each(arrTable, function (i0, v0) {
      var obj = {
        'nodeName': v0.nodeName,
        'parentId': v0.parentId,
        'nodeId': v0.nodeId
      };
      var timeArr = ls.timeStamps ? ls.timeStamps : [];
      $.each(timeArr, function (i3, v3) {
        var arr = v0.result;
        $.each(arr, function (i4, v4) {
          if (v4.avg && v4.avg.length > 0) {
            var val = v4.avg[i3] != null ? v4.avg[i3] == 0 ? 0 : parseFloat(parseFloat(v4.avg[i3]).toFixed(2)) : null;
            obj['avgValue' + i3 + v4.energyParaId] = val;
          }

          if (v4.diff && v4.diff.length > 0) {
            var val = v4.diff[i3] != null ? v4.diff[i3] == 0 ? 0 : parseFloat(parseFloat(v4.diff[i3]).toFixed(2)) : null;
            obj['diffValue' + i3 + v4.energyParaId] = val;
          }

          if (v4.max && v4.max.length > 0) {
            var val = v4.max[i3] != null ? v4.max[i3] == 0 ? 0 : parseFloat(parseFloat(v4.max[i3]).toFixed(2)) : null;
            obj['maxValue' + i3 + v4.energyParaId] = val;
          }

          if (v4.min && v4.min.length > 0) {
            var val = v4.min[i3] != null ? v4.min[i3] == 0 ? 0 : parseFloat(parseFloat(v4.min[i3]).toFixed(2)) : null;
            obj['minValue' + i3 + v4.energyParaId] = val;
          }

          if (v4.first && v4.first.length > 0) {
            var val = v4.first[i3] != null ? v4.first[i3] == 0 ? 0 : parseFloat(parseFloat(v4.first[i3]).toFixed(2)) : null;
            obj['timeValue' + i3 + v4.energyParaId] = val;
          }
        });
      });
      data.push(obj);
    });
    return data;
  }

  function formateDate(timestamp) {
    var date = new Date(timestamp);
    var y = date.getFullYear();
    var M = date.getMonth() + 1;
    M = M < 10 ? '0' + M : M;
    var d = date.getDate();
    d = d < 10 ? '0' + d : d;

    if (timeType == 'year') {
      return y;
    } else if (timeType == 'month') {
      return y + '-' + M;
    } else {
      return y + '-' + M + '-' + d;
    }
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