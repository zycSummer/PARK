

/*
 * @Author: xzl 
 * @Date: 2019-10-29 09:40:39 
 * @Last Modified by: xzl
 * @Last Modified time: 2019-12-25 14:50:35
 */
layui.use(['form', 'element', 'layer', 'table', 'jquery', 'laypage', 'laydate'], function () {
  var form = layui.form,
      layer = layui.layer,
      $ = layui.jquery,
      table = layui.table,
      laypage = layui.laypage,
      laydate = layui.laydate,
      element = layui.element;
  var indexLoading; //定义加载类；

  var dataSource_alarmId; //报警条件所选择的alarmId

  var parkRtdbId = sessionStorage.getItem('parkRtdb'); //园区标识对应的实时库租户标识

  var siteRtdbId; //仪表所属的企业对应的实时库项目标识

  var meterRtdbId; //仪表标识

  var addType = 'end'; //参数双击添加方式

  function load() {
    //加载事件
    indexLoading = layer.load(1, {
      shade: [0.3, '#fff']
    });
  }

  function disLoad() {
    //取消加载事件
    layer.close(indexLoading);
  } //初始函数


  function initFun() {
    getAlarmData();
  } //############################################## 报警配置-配置 ###########################################################
  //查询点击事件


  $("#search_btn_conf").on("click", function () {
    getAlarmData();
  });
  var pageNum = 1;
  var pageLimit;

  function getAlarmData() {
    //ajax获取 数据
    var tnum = parseInt(($(".tab_content").height() - 60) / 42); //动态生成表格展示条数

    var formData = {};
    formData.limit = pageLimit ? pageLimit : tnum;
    formData.page = pageNum;
    var key = {};
    key.alarmName = $("#alarmName_search").val();
    key.objType = JSON.parse(sessionStorage.getItem("parkId")).type;
    key.objId = JSON.parse(sessionStorage.getItem("parkId")).id;
    formData.key = key;
    load();
    request.service({
      method: 'post',
      url: '/alarm/queryLeftAlarmInfos',
      data: formData
    }).then(function (res) {
      disLoad();
      var tabData = res.data;

      if (tabData.length > 0) {
        tabData[0].LAY_CHECKED = true;
        dataSource_alarmId = tabData[0].alarmId;
        tabData.forEach(function (item) {
          item.msgRecvText = item.msgRecv.join(";");
          item.mailRecvText = item.mailRecv.join(";");

          switch (item.isUse) {
            case true:
              item.isUseName = 'Y';
              break;

            default:
              item.isUseName = 'N';
              break;
          }
        });
      }

      var data = handleNum(tabData, pageNum, pageLimit);
      renderTableContent(data, formData.limit);
      renderAlarmPage(res.count, tnum);
      getDataSourceList();
    })["catch"](function (err) {
      disLoad();
      console.log(err);
    });
  } //渲染表格数据


  function renderTableContent(data, limit) {
    table.render({
      elem: '#alarm_table',
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
        field: 'alarmId',
        align: "center",
        width: 120,
        title: '报警标识',
        fixed: "left"
      }, {
        field: 'alarmName',
        align: "center",
        width: 200,
        title: '报警名称'
      }, {
        field: 'alarmType',
        align: "center",
        width: 100,
        title: '报警类型'
      }, {
        field: 'isUseName',
        align: "center",
        width: 100,
        title: '是否启用'
      }, {
        field: 'multiConditionsLogic',
        align: "center",
        width: 120,
        title: '多条件间关系'
      }, {
        field: 'msgRecvText',
        align: "center",
        width: 120,
        title: '短信接收人'
      }, {
        field: 'mailRecvText',
        align: "center",
        title: '邮件接收人',
        width: 250
      }, {
        field: 'memo',
        align: "center",
        title: '备注',
        width: 150
      }]],
      data: data,
      page: false,
      limit: limit
    });
  } //渲染分页模块


  function renderAlarmPage(count, tnum) {
    laypage.render({
      elem: 'alarm_page',
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
          getAlarmData();
        }
      }
    });
  } //报警配置 新增点击事件


  $("#add_btn_conf").on('click', function () {
    $("#alarmId_conf").val(''); //报警标示

    $('#alarmId_conf').attr("disabled", false);
    $("#alarmName_cof").val(''); //报警名称

    $("#alarmType_conf").val(''); //报警类型        

    $("#msgRecv_conf").val(''); //短信接收人

    $("#mailRecv_conf").val(''); //邮件接收人

    $("#conf_memo").val(''); //memo

    $("input[id='isEnabled']").attr("checked", false);
    $("#addConditionAnd").prop("checked", true);
    $("#addConditionOr").prop("checked", false);
    form.render();
    showConfDialog(null, '新增报警');
  }); //报警配置 修改点击事件

  $("#edit_btn_conf").on('click', function () {
    var checkStatusData = table.checkStatus('alarm_table').data; //选中状态值

    if (checkStatusData.length == 0) {
      return layer.msg("请选择需要修改的记录");
    }

    load();
    request.service({
      method: 'get',
      url: '/alarm/queryLeftAlarmById/' + checkStatusData[0].id
    }).then(function (res) {
      disLoad();
      var updateData = res.one;
      $('#alarmId_conf').attr("disabled", true);
      $("#alarmId_conf").val(updateData.alarmId); //报警标示

      $("#alarmName_cof").val(updateData.alarmName); //报警名称

      $("#alarmType_conf").val(updateData.alarmType); //报警类型

      $("input[id='isEnabled']").attr("checked", updateData.isUse);
      $("#addMultiConditionsLogic").val(); //多条件间关系

      $("#msgRecv_conf").val(updateData.msgRecv.join(";")); //短信接收人

      $("#mailRecv_conf").val(updateData.mailRecv.join(";")); //邮件接收人

      $("#conf_memo").val(updateData.memo); //memo

      if (updateData.multiConditionsLogic == "AND") {
        $("#addConditionAnd").prop("checked", true);
        $("#addConditionOr").prop("checked", false);
      } else {
        $("#addConditionOr").prop("checked", true);
        $("#addConditionAnd").prop("checked", false);
      }

      form.render();
      showConfDialog(updateData.id, '修改报警');
    })["catch"](function (err) {
      disLoad();
      console.log(err);
    });
  }); //模态框事件

  function showConfDialog(editId, title) {
    //模态框调用事件
    layer.open({
      type: 1,
      title: title,
      closeBtn: 1,
      shade: 0.3,
      maxmin: true,
      anim: 1,
      area: ['780px', '750px'],
      content: $('#conf_dialog'),
      btn: ['保存', '关闭'],
      success: function success() {
        $('#conf_dialog').removeClass('layui-hide').addClass('layui-show');
      },
      yes: function yes(index) {
        console.log($("#addIsUSe").val());
        var alarmId = $("#alarmId_conf").val(); //报警标示

        var alarmName = $("#alarmName_cof").val(); //报警名称

        var alarmType = $("#alarmType_conf").val(); //报警类型

        var isUse = $("#isEnabled").is(":checked"); //是否启用

        var multiConditionsLogic = $('input:radio[name="addCondition"]:checked').val();
        var msgRecv = $("#msgRecv_conf").val(); //短信接收人

        var mailRecv = $("#mailRecv_conf").val(); //邮件接收人

        var memo = $("#conf_memo").val(); //memo

        if (!alarmId) {
          return layer.msg('请输入报警标示');
        }

        if (!alarmName) {
          return layer.msg('请输入报警名称');
        }

        if (!alarmType) {
          return layer.msg('请输入报警类型');
        }

        if (!multiConditionsLogic) {
          return layer.msg('请选择多条件间关系');
        }

        var addMsgRecvArr = [];

        if (msgRecv) {
          //当存在短信接收人时
          var msgTest = /^((1[3-9][0-9])|(15[^4,\D])|(18[0,5-9]))\d{8}$/;

          if (msgRecv.indexOf(';') < 0) {
            if (!msgTest.test(msgRecv)) {
              layer.msg("短信接收人填写格式错误");
              return;
            }
          }

          ;
          addMsgRecvArr = msgRecv.split(';');

          if (addMsgRecvArr.length > 0) {
            for (var i = 0; i < addMsgRecvArr.length; i++) {
              var msg = addMsgRecvArr[i];

              if (!msgTest.test(msg)) {
                layer.msg("短信接收人填写格式错误");
                return false;
              }
            }
          }
        }

        var addMailRecv = [];

        if (mailRecv) {
          //当存在邮件接收人时
          var mailTest = /^([a-zA-Z0-9]*[-_]?[a-zA-Z0-9]+)*@([a-zA-Z0-9]*[-_]?[a-zA-Z0-9]+)+[\.][A-Za-z]{2,3}([\.][A-Za-z]{2})?$/;

          if (mailRecv.indexOf(';') < 0) {
            if (!mailTest.test(mailRecv)) {
              layer.msg("邮件接收人填写格式错误");
              return;
            }
          }

          addMailRecv = mailRecv.split(';');

          if (addMailRecv.length > 0) {
            for (var j = 0; j < addMailRecv.length; j++) {
              var mail = addMailRecv[j];

              if (!mailTest.test(mail)) {
                layer.msg("邮件接收人填写格式错误");
                return false;
              }
            }
          }
        }

        var formData = {};
        formData.alarmId = alarmId;
        formData.alarmName = alarmName;
        formData.alarmType = alarmType;
        formData.isUse = isUse;
        formData.multiConditionsLogic = multiConditionsLogic;
        formData.msgRecv = addMsgRecvArr;
        formData.mailRecv = addMailRecv;
        formData.memo = memo;
        formData.id = editId;
        formData.objType = JSON.parse(sessionStorage.getItem("parkId")).type;
        formData.objId = JSON.parse(sessionStorage.getItem("parkId")).id;
        load();
        var url = '/alarm/add';

        if (editId) {
          url = '/alarm/edit';
        }

        request.service({
          method: 'post',
          url: url,
          data: formData
        }).then(function (res) {
          disLoad();
          layer.close(index);
          getAlarmData();
        })["catch"](function (err) {
          console.log(err);
        });
      },
      end: function end(index) {
        // 模态框关闭事件
        $('#conf_dialog').removeClass('layui-show').addClass('layui-hide');
      }
    });
  } //报警删除事件


  $("#del_btn_conf").on('click', function () {
    var checkStatusData = table.checkStatus('alarm_table').data; //选中状态值

    if (checkStatusData.length == 0) {
      return layer.msg("请选择需要删除的记录");
    }

    layer.open({
      type: 1,
      title: "删除报警",
      shade: 0.3,
      btn: ['确定', '取消'],
      area: ['320px', '280px'],
      //宽高
      content: '<div id="park_select_tree"> <form class="layui-form" style="margin:30px;">' + '   <div class="layui-form-item "><label>报警标识：</label>' + '<div class="layui-inline">' + checkStatusData[0].alarmId + '</div></div>' + '   <div class="layui-form-item "><label>报警名称：</label>' + '<div class="layui-inline" >' + checkStatusData[0].alarmName + '</div></div>' + '   <div class="layui-form-item " style="color:red;">确定要删除此报警吗？此报警下配置的报警条件也将一并删除。</div>' + '</form></div>',
      success: function success() {},
      yes: function yes(index) {
        var formData = {};
        formData.objType = JSON.parse(sessionStorage.getItem("parkId")).type;
        formData.objId = JSON.parse(sessionStorage.getItem("parkId")).id;
        formData.alarmId = checkStatusData[0].alarmId;
        load();
        request.service({
          method: 'post',
          url: '/alarm/delete',
          data: formData
        }).then(function (res) {
          disLoad();
          layer.close(index);
          getAlarmData();
        })["catch"](function (err) {
          console.log(err);
        });
      },
      end: function end(index) {
        // 模态框关闭事件
        layer.close(index);
      }
    });
  }); //报警条件单选事件

  table.on('radio(alarm_table)', function (obj) {
    dataSource_alarmId = obj.data.alarmId;
    getDataSourceList();
  }); //######################################################## 数据源配置############################################################################

  $("#search_btn_data").on('click', function () {
    //查询点击事件
    if (!dataSource_alarmId) {
      return layer.msg("请选择左侧报警记录");
    }

    getDataSourceList();
  });

  function getDataSourceList() {
    //根据所配置的报警信息 获取数据源信息
    var tnum = parseInt(($(".tab_content").height() - 60) / 42); //动态生成表格展示条数

    var formData = {};
    formData.limit = pageLimit ? pageLimit : tnum;
    formData.page = pageNum;
    var key = {};
    key.dataSource = $("#user_name").val();
    key.alarmId = dataSource_alarmId;
    key.objType = JSON.parse(sessionStorage.getItem("parkId")).type;
    key.objId = JSON.parse(sessionStorage.getItem("parkId")).id;
    formData.key = key;
    load();
    request.service({
      method: 'post',
      url: '/alarmCondition/query',
      data: formData
    }).then(function (res) {
      disLoad();
      var rightTabData = res.data;

      if (rightTabData.length > 0) {
        rightTabData.forEach(function (item) {
          switch (item.valueType) {
            case 'LAST_VALUE':
              item.valueTypeText = '最新值';
              break;

            case 'LAST_VALUE_TIMESTAMP':
              item.valueTypeText = '最新值时间';
              break;

            case 'TODAY_DIFF':
              item.valueTypeText = '当日差值';
              break;

            case 'THIS_MONTH_DIFF':
              item.valueTypeText = '当月差值';
              break;

            case 'THIS_YEAR_DIFF':
              item.valueTypeText = '当年差值';
              break;

            case 'NOW_MINUS_LAST_VALUE_TIMESTAMP':
              item.valueTypeText = '当前时间减去最新值时间';
              break;
          }
        });
      }

      var data = handleNum(rightTabData, pageNum, pageLimit);
      renderDataSourceTable(data, formData.limit);
      renderDataSourcePage(res.count, tnum);
    })["catch"](function (err) {
      console.log(err);
    });
  }

  var sourcePage = 1;
  var sourceLimit;

  function renderDataSourceTable(data, limit) {
    //渲染数据源表格
    table.render({
      elem: '#data_source_table',
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
        field: 'alarmConditionId',
        align: "center",
        minWidth: 120,
        title: '报警条件标识',
        fixed: "left"
      }, {
        field: 'dataSource',
        align: "center",
        minWidth: 260,
        title: '数据源'
      }, {
        field: 'valueTypeText',
        align: "center",
        width: 120,
        title: '数值类型'
      }, {
        field: 'condition1Op',
        align: "center",
        width: 110,
        title: '条件1操作符'
      }, {
        field: 'condition1Value',
        align: "center",
        width: 100,
        title: '条件1值'
      }, {
        field: 'conditionsLogic',
        align: "center",
        width: 100,
        title: '条件间关系'
      }, {
        field: 'condition2Op',
        align: "center",
        width: 110,
        title: '条件2操作符'
      }, {
        field: 'condition2Value',
        align: "center",
        width: 100,
        title: '条件2值'
      }, {
        field: 'alarmMsg',
        align: "center",
        width: 200,
        title: '报警信息'
      }, {
        field: 'sortId',
        align: "center",
        width: 90,
        title: '排序标识'
      }, {
        field: 'memo',
        width: 150,
        align: "center",
        title: '备注'
      }]],
      data: data,
      page: false,
      limit: limit
    });
  }

  function renderDataSourcePage(count, tnum) {
    //渲染数据源分页
    laypage.render({
      elem: 'table_page_data',
      count: count,
      //数据总数，从服务端得到
      curr: sourcePage,
      limit: sourceLimit ? sourceLimit : tnum,
      limits: [tnum, 20, 30, 50],
      layout: ['count', 'prev', 'page', 'next', 'limit', 'skip'],
      jump: function jump(obj, first) {
        //首次不执行
        if (!first) {
          sourcePage = obj.curr;
          sourceLimit = obj.limit;
          getDataSourceList();
        }
      }
    });
  } //新增 修改模态框  企业查询点击事件


  $("#add_btn_company").on("click", function () {
    getCompanyList();
  }); //获取当前用户的所有的企业

  function getCompanyList() {
    var userSelect = JSON.parse(sessionStorage.getItem("parkId"));
    var selectType = userSelect.type;
    var key = $("#add_key_company").val(); // 查询key值

    var keyUrl = '';
    var companyList = [];

    if (key) {
      keyUrl = key; //判断是否存在key  存在则拼接url
    }

    load();

    if (selectType == 'PARK') {
      //当为园区时请求获取当前园区下的企业
      showCompanySelect();
      request.service({
        method: 'get',
        url: '/common/getAllCurrentSite/' + keyUrl
      }).then(function (res) {
        disLoad();
        companyList = res.data;
        renderCompanyList(companyList, 'park');
      })["catch"](function (err) {
        console.log(err);
      });
    } else {
      //当不为园区时 选择为某个企业时
      var selectObj = {};
      selectObj.objId = userSelect.id;
      selectObj.siteName = userSelect.title;
      selectObj.objType = userSelect.type;
      selectObj.rtdbProjectId = userSelect.rtdbProjectId;
      companyList[0] = selectObj;
      renderCompanyList(companyList, 'site');
      getMeterData(userSelect.type, userSelect.id);
      siteRtdbId = userSelect.rtdbProjectId;
      hideCompanySelect();
    }
  } //meterRtdbId


  function renderCompanyList(list, type) {
    //渲染企业列表
    var renderCompanyHtml = '';

    if (list.length > 0) {
      list.forEach(function (item, index) {
        if (type == 'site') {
          renderCompanyHtml += '<tr  class="company_tr company_tr_focus"   data-rtdbprojectid="' + item.rtdbProjectId + '" data-type="' + item.objType + '" data-objid="' + item.objId + '"><td>[' + item.objId + ']' + item.siteName + '</td></tr>';
        } else {
          renderCompanyHtml += '<tr  class="company_tr"  data-rtdbprojectid="' + item.rtdbProjectId + '" data-type="' + item.objType + '"  data-objid="' + item.objId + '"><td>[' + item.objId + ']' + item.siteName + '</td></tr>';
        }
      });
    }

    $("#company_tbody").html(renderCompanyHtml);
  } //企业选择点击事件


  var meter_objType;
  var meter_objId;
  $(document).on("click", '.company_tr', function () {
    $(this).siblings().removeClass('company_tr_focus');
    $(this).addClass('company_tr_focus');
    meter_objType = $(this).data('type');
    meter_objId = $(this).data('objid');
    siteRtdbId = $(this).data("rtdbprojectid");
    getMeterData(meter_objType, meter_objId);
  });
  $("#add_btn_meter").on('click', function () {
    //仪表查询事件
    getMeterData(meter_objType, meter_objId);
  });

  function getMeterData(objType, objId) {
    //根据所选企业获取仪表数据
    var formData = {};
    formData.objType = objType;
    formData.objId = objId;
    formData.key = $("#add_key_meter").val(); //关键词

    load();
    request.service({
      method: 'post',
      url: '/meter/getAllCurrentSite',
      data: formData
    }).then(function (res) {
      disLoad();
      var meterData = res.data;
      renderMeterTab(meterData);
    })["catch"](function (err) {
      console.log(err);
    });
  }

  function renderMeterTab(list) {
    //根据所获取的数据 渲染仪表表格
    var meterHtml = '';

    if (list.length > 0) {
      list.forEach(function (item) {
        meterHtml += '<tr  class="meter_tr" data-meterid ="' + item.meterId + '" data-energytypeid="' + item.energyTypeId + '"><td>[' + item.energyTypeName + '][' + item.meterId + ']' + item.meterName + '</td></tr>';
      });
    }

    $("#meter_tbody").html(meterHtml);
    element.tabChange('modelSelect', 'meter'); //渲染完成跳转到仪表选择界面
  } //仪表选择点击事件


  var parameter_energyTypeId;
  $(document).on("click", '.meter_tr', function () {
    $(this).siblings().removeClass('meter_tr_focus');
    $(this).addClass('meter_tr_focus');
    parameter_energyTypeId = $(this).data('energytypeid');
    meterRtdbId = $(this).data('meterid');
    getParameterTabData(parameter_energyTypeId);
  });
  $("#add_btn_parameter").on('click', function () {
    //参数查询点击事件
    getParameterTabData(parameter_energyTypeId);
  });

  function getParameterTabData(energyTypeId) {
    //根据所选择仪表获取参数数据
    var formData = {};
    formData.key = $("#add_key_parameter").val(); //关键词

    formData.energyTypeId = energyTypeId;
    load();
    request.service({
      method: 'POST',
      url: '/common/queryEnergyParaIdOrEnergyParaName',
      data: formData
    }).then(function (res) {
      disLoad();
      var parameterData = res.data;
      renderParameterTab(parameterData);
    })["catch"](function (err) {
      console.log(err);
    });
  }

  function renderParameterTab(list) {
    //根据所获取的数据渲染参数列表
    var parameterHtml = '';

    if (list.length > 0) {
      list.forEach(function (item) {
        parameterHtml += '<tr  class="parameter_tr" data-energyparaid="' + item.energyParaId + '"><td>[' + item.energyParaId + ']' + item.energyParaName + '</td></tr>';
      });
    }

    $("#parameter_tbody").html(parameterHtml);
    element.tabChange('modelSelect', 'parameter'); //渲染完成跳转到仪表选择界面
  }

  element.on('tab(modelSelect)', function (data) {
    //tab 选项卡点击事件
    if (data.index == 2) {
      //当选项卡为参数时  显示按钮
      $(".parameter_btns").show();
    } else {
      $(".parameter_btns").hide();
    }
  });
  $(document).on("click", '.datasoucre_tr', function () {
    //仪表参数选中事件
    $('.datasoucre_tr').removeClass('datasoucre_tr_status');
    $(this).addClass('datasoucre_tr_status');
  });
  $(".parameter_btn_type").on('click', function () {
    //添加至所选之前
    if ($(".parameter_btn_status").length > 0) {
      //当存在所选参数时 判断 仪表参数列表是否存在
      var std_coalHtml = ''; //拼接数据源        

      var energyParaId = $(".parameter_btn_status").data("energyparaid"); //参数标识

      var addDataSocureText = parkRtdbId + '.' + siteRtdbId + '.' + meterRtdbId + '.' + energyParaId + std_coalHtml; //拼接数据源

      var addDatasocureClass = parkRtdbId + siteRtdbId + meterRtdbId + energyParaId;
      var addDataSocureHtml = '<tr class="datasoucre_tr  ' + addDatasocureClass + '"  data-socure="' + addDataSocureText + '"><td> <input type="checkbox" name="dataSocurecheckbox"  value="' + addDatasocureClass + '" title="" lay-skin="primary" >' + addDataSocureText + '</td></tr>';

      if (document.getElementsByClassName('datasoucre_tr').length > 0) {
        //当仪表参数长度大于1时
        var dataSocureStatusLength = $(".datasoucre_tr_status").length;

        switch (dataSocureStatusLength) {
          case 0:
            layer.msg("请选择左侧仪表参数");
            break;

          default:
            $(".datasoucre_tr_status").before(addDataSocureHtml);
            break;
        }
      } else {
        $("#data_source_tbody").prepend(addDataSocureHtml);
      }
    } else {
      return layer.msg("请选择参数");
    }
  });
  var timer = null;
  $(document).on("click", '.parameter_tr', function () {
    //参数选中事件
    var that = this;
    clearTimeout(timer);
    timer = setTimeout(function () {
      //在单击事件中添加一个setTimeout()函数，设置单击事件触发的时间间隔 
      $('.parameter_tr').removeClass('parameter_btn_status');
      $(that).addClass('parameter_btn_status');
    }, 300);
  });
  $(document).on("dblclick", '.parameter_tr', function () {
    //参数双击事件
    clearTimeout(timer);
    var energyParaId = $(this).data("energyparaid"); //参数标识

    var addDataSocureText = parkRtdbId + '.' + siteRtdbId + '.' + meterRtdbId + '.' + energyParaId; //拼接数据源

    var addDatasocureClass = parkRtdbId + siteRtdbId + meterRtdbId + energyParaId;
    var addDataSocureHtml = '<tr class="datasoucre_tr  ' + addDatasocureClass + '" data-socure="' + addDataSocureText + '"><td> <input type="checkbox" name="dataSocurecheckbox"  value="' + addDatasocureClass + '" title="" lay-skin="primary" >' + addDataSocureText + '</td></tr>';

    if (addType == 'end') {
      $("#data_source_tbody").append(addDataSocureHtml);
    } else {
      $("#data_source_tbody").prepend(addDataSocureHtml);
    }
  }); //删除所选的数据源

  $("#deleteDataSocureSelect").on("click", function () {
    $.each($("input:checkbox[name='dataSocurecheckbox']:checked"), function () {
      var delClass = $(this).val();
      $("." + delClass).remove();
    });
  }); //清空数据源

  $("#delAllDataSocure").on("click", function () {
    $("#data_source_tbody").html('');
  });
  $("#formula_check").on('click', function () {
    //公式检查事件
    var dataSocureLength = document.getElementsByClassName('datasoucre_tr').length; //数据源添加的数据源长度

    var formulaVal = $("#alarm_formulaVal").val();

    if (!formulaVal && dataSocureLength > 1) {
      return layer.msg("未填写公式");
    }

    var selectValArr = [];

    if (formulaVal.length > 0) {
      for (var index = 0; index < formulaVal.length; index++) {
        var _element = formulaVal[index];

        if (_element == '?') {
          selectValArr.push(_element);
        }
      }

      if (dataSocureLength == 1) {
        //如果仪表参数列表中只有一个仪表参数，公式可以为空，但不能只有1个问号
        if (selectValArr.length == 1) {
          return layer.msg('公式中的问号个数不能为一个');
        }
      }

      if (selectValArr.length != dataSocureLength) {
        //检查数据源-仪表参数列表的个数 和 公式中的问号个数是否一致，不一致时提醒用户
        return layer.msg('数据源-仪表参数列表的个数 和 公式中的问号个数不一致');
      }
    }
  }); //清空公式

  $("#formula_delAll").on("click", function () {
    $("#alarm_formulaVal").val('');
  }); //所有仪表参数相加

  $("#formula_addVal").on("click", function () {
    var dataSocureLength = document.getElementsByClassName('datasoucre_tr').length; //数据源添加的数据源长度

    if (dataSocureLength == 0) {
      return layer.msg('未添加仪表参数');
    }

    var addFormulaVal = '';

    for (var index = 0; index < dataSocureLength; index++) {
      if (index != dataSocureLength - 1) {
        addFormulaVal += '?+';
      } else {
        addFormulaVal += '?';
      }
    }

    if (dataSocureLength == 1) {
      //如果只有一个仪表参数，则公式结果为空。
      addFormulaVal = '';
    }

    $("#alarm_formulaVal").val(addFormulaVal);
  });
  $("#add_btn_data").on('click', function () {
    //新增点击事件
    $('#add_alarmConditionId').attr("disabled", false);
    $("#add_alarmConditionId").val(''); //报警条件标识

    $("#add_valueType").val(''); //数值类型

    $("#add_condition1Op").val(''); //条件1操作符

    $("#add_condition1Value").val(''); //条件1值

    $("#add_conditionsLogic").val(''); //条件间关系

    $("#add_condition2Op").val(''); //条件2操作符

    $("#add_condition2Value").val(''); //条件2值

    $("#add_alarmMsg").val(''); //报警信息

    $("#add_sortId").val(''); //

    $("#add_memo").val(''); //memo

    $("#alarm_formulaVal").val("");
    $("#data_source_tbody").html("");
    form.render();
    getCompanyList();
    showDataSourceDialog(null, "新增报警条件");
  });
  $("#edit_btn_data").on('click', function () {
    //修改报警条件点击事件
    $('#add_alarmConditionId').attr("disabled", true);
    var checkStatusData = table.checkStatus('data_source_table').data; //选中状态值

    if (checkStatusData.length == 0) {
      return layer.msg("请选择需要修改的报警条件");
    }

    if (checkStatusData.length > 1) {
      return layer.msg("只能选择单个报警条件！");
    }

    var formData = {};
    formData.objType = JSON.parse(sessionStorage.getItem("parkId")).type;
    formData.objId = JSON.parse(sessionStorage.getItem("parkId")).id;
    formData.alarmId = checkStatusData[0].alarmId;
    formData.conditionId = checkStatusData[0].alarmConditionId;
    load();
    request.service({
      method: 'post',
      url: '/alarmCondition/getByIndex',
      data: formData
    }).then(function (res) {
      disLoad();
      var editData = res.one;
      $("#add_alarmConditionId").val(editData.alarmConditionId); //报警条件标识

      $("#add_valueType").val(editData.valueType); //数值类型

      $("#add_condition1Op").val(editData.condition1Op); //条件1操作符

      $("#add_condition1Value").val(editData.condition1Value); //条件1值

      $("#add_conditionsLogic").val(editData.conditionsLogic); //条件间关系

      $("#add_condition2Op").val(editData.condition2Op); //条件2操作符

      $("#add_condition2Value").val(editData.condition2Value); //条件2值

      $("#add_alarmMsg").val(editData.alarmMsg); //报警信息

      $("#add_sortId").val(editData.sortId); //

      $("#add_memo").val(editData.memo); //memo

      var editDataSocure = editData.dataSource;
      var editDataSocure_top = '';
      var editDataSocure_formula = '';

      if (editDataSocure) {
        //截取数据源
        editDataSocure_formula = editDataSocure.split('#')[1];
        var editDataSocureArr = editDataSocure.split('#')[0].split(',');

        if (editDataSocureArr.length > 0) {
          editDataSocureArr.forEach(function (item) {
            var editDatasocureClass = item.split('.')[0] + item.split('.')[1] + item.split('.')[2] + item.split('.')[3];
            editDataSocure_top += '<tr class="datasoucre_tr  ' + editDatasocureClass + '" data-socure="' + item + '"><td> <input type="checkbox" name="dataSocurecheckbox"  value="' + editDatasocureClass + '" title="" lay-skin="primary" >' + item + '</td></tr>';
          });
        }
      }

      $("#alarm_formulaVal").val(editDataSocure_formula);
      $("#data_source_tbody").html(editDataSocure_top);
      form.render();
      getCompanyList();
      showDataSourceDialog(editData.id, "编辑报警条件");
    })["catch"](function (err) {
      console.log(err);
    });
  });

  function showDataSourceDialog(editId, title) {
    //数据源模态框调用事件
    layer.open({
      type: 1,
      title: title,
      closeBtn: 1,
      shade: 0.3,
      maxmin: true,
      anim: 1,
      area: ['1200px', '780px'],
      content: $('#data_source_dialog'),
      btn: ['保存', '关闭'],
      success: function success() {
        $('#data_source_dialog').removeClass('layui-hide').addClass('layui-show');
      },
      yes: function yes(indexCon) {
        var alarmConditionId = $("#add_alarmConditionId").val() || null; //报警条件标识

        var valueType = $("#add_valueType").val() || null; //数值类型

        var condition1Op = $("#add_condition1Op").val() || null; //条件1操作符

        var condition1Value = $("#add_condition1Value").val() || null; //条件1值

        var conditionsLogic = $("#add_conditionsLogic").val() || null; //条件间关系

        var condition2Op = $("#add_condition2Op").val() || null; //条件2操作符

        var condition2Value = $("#add_condition2Value").val() || null; //条件2值

        var alarmMsg = $("#add_alarmMsg").val() || null; //报警信息

        var sortId = $("#add_sortId").val() || null; //排序标识

        var memo = $("#add_memo").val() || null; //memo

        if (!alarmConditionId) {
          return layer.msg('请输入报警条件标识');
        }

        if (!valueType) {
          return layer.msg('请选择数值类型');
        }

        if (!condition1Op) {
          return layer.msg('请选择条件1操作符！');
        }

        if (!condition1Value) {
          return layer.msg("请输入条件1的值");
        } //当设置条件2时


        if (condition2Op || condition2Value) {
          if (!conditionsLogic) {
            return layer.msg('当设置条件2时，条件间关系必需选择一个。');
          }

          if (!condition2Op) {
            return layer.msg('请选择条件2操作符');
          }

          if (!condition2Value) {
            return layer.msg('请输入条件2的值');
          }
        } //获取 数据源


        var dataSource;
        var dataSourceArr = [];
        var addFormulaVals = ''; //公式

        $('#data_source_tbody tr').each(function (i) {
          dataSourceArr.push($(this).data('socure'));
        });

        if (dataSourceArr.length === 0) {
          //当为数据源时 值为null
          dataSource = null;
        } else {
          dataSource = dataSourceArr.join(',');
          var dataSocureLength = document.getElementsByClassName('datasoucre_tr').length; //数据源添加的数据源长度

          var formulaVal = $("#alarm_formulaVal").val();

          if (!formulaVal) {
            //当公式为空检查通过
            addFormulaVals = '';
          } else {
            var selectValArr = [];

            if (formulaVal.length > 0) {
              for (var index = 0; index < formulaVal.length; index++) {
                var _element2 = formulaVal[index];

                if (_element2 == '?') {
                  selectValArr.push(_element2);
                }
              }

              if (dataSocureLength == 1) {
                //如果仪表参数列表中只有一个仪表参数，公式可以为空，但不能只有1个问号
                if (selectValArr.length == 1) {
                  return layer.msg('公式检查失败!');
                }
              }

              if (selectValArr.length != dataSocureLength) {
                //检查数据源-仪表参数列表的个数 和 公式中的问号个数是否一致，不一致时提醒用户
                return layer.msg('公式检查失败!');
              }

              addFormulaVals = '#' + formulaVal;
            } else {
              addFormulaVals = formulaVal;
            }
          }

          dataSource = dataSource + addFormulaVals;
        }

        var formData = {};
        formData.dataSource = dataSource;
        formData.alarmConditionId = alarmConditionId;
        formData.valueType = valueType;
        formData.condition1Op = condition1Op;
        formData.condition1Value = condition1Value;
        formData.conditionsLogic = conditionsLogic;
        formData.condition2Op = condition2Op;
        formData.condition2Value = condition2Value;
        formData.alarmMsg = alarmMsg;
        formData.sortId = sortId;
        formData.memo = memo;
        formData.objType = JSON.parse(sessionStorage.getItem("parkId")).type;
        formData.objId = JSON.parse(sessionStorage.getItem("parkId")).id;
        formData.alarmId = dataSource_alarmId; //左侧所选择的报警标识

        var url = '/alarmCondition/add';

        if (editId) {
          url = '/alarmCondition/update';
          formData.id = editId;
        }

        load();
        request.service({
          method: 'post',
          url: url,
          data: formData
        }).then(function (res) {
          disLoad();
          layer.msg('新增报警条件成功');
          layer.close(indexCon);
          getDataSourceList();
        })["catch"](function (err) {
          console.log(err);
        });
      },
      end: function end(index) {
        // 模态框关闭事件
        $('#data_source_dialog').removeClass('layui-show').addClass('layui-hide');
      }
    });
  }

  $("#del_btn_data").on('click', function () {
    var checkStatusData = table.checkStatus('data_source_table').data; //选中状态值

    if (checkStatusData.length == 0) {
      return layer.msg("请选择需要删除的报警条件");
    }

    if (checkStatusData.length > 1) {
      return layer.msg("只能选择单个报警条件！");
    }

    var valueTypeText;

    switch (checkStatusData[0].valueType) {
      case 'LAST_VALUE':
        valueTypeText = '最新值';
        break;

      case 'LAST_VALUE_TIMESTAMP':
        valueTypeText = '最新值时间';
        break;

      case 'TODAY_DIFF':
        valueTypeText = '当日差值';
        break;

      case 'THIS_MONTH_DIFF':
        valueTypeText = '当月差值';
        break;

      case 'THIS_YEAR_DIFF':
        valueTypeText = '当年差值';
        break;

      case 'NOW_MINUS_LAST_VALUE_TIMESTAMP':
        valueTypeText = '当前时间减去最新值时间';
        break;
    }

    layer.open({
      type: 1,
      title: "删除报警条件",
      shade: 0.3,
      btn: ['确定', '取消'],
      area: ['500px', '600px'],
      //宽高
      content: '<div id="park_select_tree"> <form class="layui-form" style="margin:30px;">' + '   <div class="layui-form-item "><label>报警条件标识：</label>' + '<div class="layui-inline">' + checkStatusData[0].alarmConditionId + '</div></div>' + '   <div class="layui-form-item "><label>数据源：</label>' + '<div class="layui-inline" >' + checkStatusData[0].dataSource + '</div></div>' + '   <div class="layui-form-item "><label>数值类型：</label>' + '<div class="layui-inline" >' + valueTypeText + '</div></div>' + '   <div class="layui-form-item "><label>条件1操作符：</label>' + '<div class="layui-inline" >' + checkStatusData[0].condition1Op + '</div></div>' + '   <div class="layui-form-item "><label>条件1值：</label>' + '<div class="layui-inline" >' + checkStatusData[0].condition1Value + '</div></div>' + '   <div class="layui-form-item "><label>条件间关系：</label>' + '<div class="layui-inline" >' + checkStatusData[0].conditionsLogic + '</div></div>' + '   <div class="layui-form-item "><label>条件2操作符：</label>' + '<div class="layui-inline" >' + checkStatusData[0].condition2Op + '</div></div>' + '   <div class="layui-form-item "><label>条件2值：</label>' + '<div class="layui-inline" >' + checkStatusData[0].condition2Value + '</div></div>' + '   <div class="layui-form-item "><label>报警信息：</label>' + '<div class="layui-inline" >' + checkStatusData[0].alarmMsg + '</div></div>' + '   <div class="layui-form-item "><label>备注：</label>' + '<div class="layui-inline" >' + checkStatusData[0].memo + '</div></div>' + '   <div class="layui-form-item " style="color:red;">确定要删除此报警条件吗？</div>' + '</form></div>',
      success: function success() {},
      yes: function yes(index) {
        var formData = {};
        formData.objType = JSON.parse(sessionStorage.getItem("parkId")).type;
        formData.objId = JSON.parse(sessionStorage.getItem("parkId")).id;
        formData.alarmId = checkStatusData[0].alarmId;
        formData.conditionId = checkStatusData[0].alarmConditionId;
        load();
        request.service({
          method: 'post',
          url: '/alarmCondition/delete',
          data: formData
        }).then(function (res) {
          disLoad();
          layer.close(index);
          getDataSourceList();
        })["catch"](function (err) {
          console.log(err);
        });
      },
      end: function end(index) {
        // 模态框关闭事件
        layer.close(index);
      }
    });
  }); //提示弹框点击 -报警条件标识

  $("#add_alarmConditionId_tip").on("click", function () {
    layer.tips('字母、数字、下划线组合，长度不超过10，在当前报警内唯一。', '#add_alarmConditionId_tip');
  }); //提示弹框点击 -多条件间关系

  $("#add_conditionsLogic_tip").on("click", function () {
    layer.tips('当设置条件2时，条件间关系必需选择一个。', '#add_conditionsLogic_tip');
  }); //提示弹框点击 -报警标识

  $("#alarmId_conf_tip").on("click", function () {
    layer.tips('字母、数字、下划线组合，长度不超过10，在园区/当前企业内唯一。', '#alarmId_conf_tip');
  }); //提示弹框点击 -报警名称

  $("#alarmName_cof_tip").on("click", function () {
    layer.tips('长度不超过30。', '#alarmName_cof_tip');
  }); //提示弹框点击 -报警类型

  $("#alarmType_conf_tip").on("click", function () {
    layer.tips('长度不超过30。', '#alarmType_conf_tip');
  }); //提示弹框点击 -短信接收人

  $("#msgRecv_conf_tip").on("click", function () {
    layer.tips('多个接收人以英文分号分隔。', '#msgRecv_conf_tip');
  }); //提示弹框点击 -邮件接收人

  $("#mailRecv_conf_tip").on("click", function () {
    layer.tips('多个接收人以英文分号分隔。', '#mailRecv_conf_tip');
  }); //提示弹框点击 -邮件接收人

  $("#add_condition1Value_tip").on("click", function () {
    layer.tips('数字，当操作符为=或者!=的时候，可以不输入', '#add_condition1Value_tip');
  }); //提示弹框点击 -邮件接收人

  $("#add_condition2Value_tip").on("click", function () {
    layer.tips('数字，当操作符为=或者!=的时候，可以不输入', '#add_condition2Value_tip');
  }); //提示弹框点击 -邮件接收人

  $("#add_sortId_tip").on("click", function () {
    layer.tips('字母、数字、下划线组合，长度不超过10，系统按照字符串升序。', '#add_sortId_tip');
  });
  initFun(); //页面加载时执行初始函数
});