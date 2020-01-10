

/*
 * @Author: xzl 
 * @Date: 2019-11-25 16:20:42 
 * @Last Modified by: xzl
 * @Last Modified time: 2019-12-25 14:49:06
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
    getEnergyTypes();
  }

  var energyTypeSelect;

  function getEnergyTypes() {
    //获取能源种类
    load();
    request.service({
      method: 'get',
      url: '/datasource/queryCondition'
    }).then(function (res) {
      disLoad();
      var selectArr = [];
      renderAddEnergyTypeSelect(res.data);

      if (res.data.length > 0) {
        res.data.forEach(function (item) {
          selectArr.push(item.energyTypeId);
        });
      }

      energyTypeSelect = xmSelect.render({
        el: '#energyTypeSelect',
        prop: {
          name: 'energyTypeName',
          value: 'energyTypeId'
        },
        data: res.data
      });
      energyTypeSelect.setValue(selectArr, true);
      energyTypeSelect.closed();
      getDataSourceList();
    })["catch"](function (err) {
      console.log(err);
    });
  }

  function renderAddEnergyTypeSelect(data) {
    //渲染新增修改能源种类选择
    var selectHtml = '<option></option>';

    if (data.length > 0) {
      data.forEach(function (item) {
        selectHtml += '<option value=' + item.energyTypeId + '>' + item.energyTypeName + '</option>';
      });
    }

    $("#add_valueType").html(selectHtml);
    form.render();
  }

  var energyTypeIdSelect;
  form.on("select(energyTypeSelect_add)", function (obj) {
    //能源种类选择事件
    energyTypeIdSelect = obj.value; //能源种类选择值

    renderEnergyPara(obj.value, null);
  });

  function renderEnergyPara(energyTypeId, energyParaId) {
    //根据所选择的能源种类 获取参数列表
    load();
    var formData = {};
    formData.key = '';
    formData.energyTypeId = energyTypeId;
    request.service({
      method: 'post',
      url: '/common/queryEnergyParaIdOrEnergyParaName',
      data: formData
    }).then(function (res) {
      disLoad();
      renderParaSelect(res.data, energyParaId);
    })["catch"](function (err) {
      console.log(err);
    });
  }

  function renderParaSelect(data, energyParaId) {
    //参数选择渲染事件
    var paraHtml = '<option></option>';

    if (data.length > 0) {
      data.forEach(function (item) {
        paraHtml += '<option value=' + item.energyParaId + '>' + item.energyParaName + '</option>';
      });
    }

    $("#addParaSelect").html(paraHtml);
    $("#addParaSelect").val(energyParaId); //参数

    form.render();
  } //######################################################## 数据源配置############################################################################


  $("#search_btn_data").on('click', function () {
    //查询点击事件
    getDataSourceList();
  });

  function getDataSourceList() {
    //根据所配置的报警信息 获取数据源信息
    var formData = {};
    formData.objType = JSON.parse(sessionStorage.getItem("parkId")).type;
    formData.objId = JSON.parse(sessionStorage.getItem("parkId")).id;
    var selectEnergyTypeArr = energyTypeSelect.getValue();
    var energyTypeIds = [];

    if (selectEnergyTypeArr.length > 0) {
      selectEnergyTypeArr.forEach(function (item) {
        energyTypeIds.push(item.energyTypeId);
      });
    }

    formData.energyTypeIds = energyTypeIds;
    load();
    request.service({
      method: 'post',
      url: '/datasource/queryDataSource',
      data: formData
    }).then(function (res) {
      disLoad();
      var rightTabData = res.data;
      renderDataSourceTable(rightTabData);
    })["catch"](function (err) {
      console.log(err);
    });
  }

  function renderDataSourceTable(data) {
    //渲染数据源表格
    table.render({
      elem: '#data_source_table',
      height: 'full-155',
      cols: [[{
        type: 'numbers',
        fixed: 'left'
      }, {
        type: 'radio',
        fixed: 'left'
      }, {
        field: 'energyTypeName',
        align: "center",
        width: 120,
        title: '能源种类'
      }, {
        field: 'energyParaName',
        align: "center",
        width: 150,
        title: '参数'
      }, {
        field: 'dataSource',
        align: "center",
        title: '数据源'
      }, {
        field: 'memo',
        width: 150,
        align: "center",
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
      limit: 1000
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
      disLoad();
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
        meterHtml += '<tr  class="meter_tr"   data-meterid ="' + item.meterId + '" data-energytypeid="' + item.energyTypeId + '"><td>[' + item.energyTypeName + '][' + item.meterId + ']' + item.meterName + '</td></tr>';
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
    })["catch"](function (err)
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

      if (energyTypeIdSelect === 'std_coal') {
        //当为标煤时
        std_coalHtml = '[' + parameter_energyTypeId + ']';
      }

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
    var std_coalHtml = ''; //拼接数据源

    if (energyTypeIdSelect === 'std_coal') {
      //当为标煤时
      std_coalHtml = '[' + parameter_energyTypeId + ']';
    }

    var energyParaId = $(this).data("energyparaid"); //参数标识

    var addDataSocureText = parkRtdbId + '.' + siteRtdbId + '.' + meterRtdbId + '.' + energyParaId + std_coalHtml; //拼接数据源

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

    if (!formulaVal) {
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
    $('#add_valueType').attr("disabled", false);
    $('#addParaSelect').attr("disabled", false);
    $("#add_valueType").val(''); //能源种类

    $("#addParaSelect").val(''); //参数

    $("#add_memo").val(''); //memo

    $("#alarm_formulaVal").val("");
    $("#data_source_tbody").html("");
    form.render();
    getCompanyList();
    $("#parameter_tbody").html('');
    $("#meter_tbody").html('');
    element.render('tab', 'modelSelect');
    showDataSourceDialog(null, "新增数据源");
  });
  $("#edit_btn_data").on('click', function () {
    //修改报警条件点击事件
    $('#add_valueType').attr("disabled", true);
    $('#addParaSelect').attr("disabled", true);
    var checkStatusData = table.checkStatus('data_source_table').data; //选中状态值

    if (checkStatusData.length == 0) {
      return layer.msg("请选择需要修改的数据源");
    }

    load();
    request.service({
      method: 'get',
      url: '/datasource/queryDataSourceById/' + checkStatusData[0].id
    }).then(function (res) {
      disLoad();
      var editData = res.one;
      $("#add_valueType").val(editData.energyTypeId); //能源种类

      renderEnergyPara(editData.energyTypeId, editData.energyParaId);
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
      $("#parameter_tbody").html('');
      $("#meter_tbody").html('');
      showDataSourceDialog(editData.id, "编辑数据源");
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
        var energyTypeId = $("#add_valueType").val() || null; //能源种类

        var energyParaId = $("#addParaSelect").val() || null; //参数

        var memo = $("#add_memo").val() || null; //memo

        if (!energyTypeId) {
          return layer.msg('请选择能源种类');
        }

        if (!energyParaId) {
          return layer.msg('请选择参数');
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
        formData.energyTypeId = energyTypeId;
        formData.energyParaId = energyParaId;
        formData.memo = memo;
        formData.objType = JSON.parse(sessionStorage.getItem("parkId")).type;
        formData.objId = JSON.parse(sessionStorage.getItem("parkId")).id;
        var url = '/datasource/insert';

        if (editId) {
          url = '/datasource/update';
          formData.id = editId;
        }

        load();
        request.service({
          method: 'post',
          url: url,
          data: formData
        }).then(function (res) {
          disLoad();
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
      return layer.msg("请选择需要删除的数据源");
    }

    layer.open({
      type: 1,
      title: "删除数据源",
      shade: 0.3,
      btn: ['确定', '取消'],
      area: ['300px', '300px'],
      //宽高
      content: '<div id="park_select_tree"> <form class="layui-form" style="margin:30px;">' + '   <div class="layui-form-item "><label>能源种类：</label>' + '<div class="layui-inline">' + checkStatusData[0].energyTypeName + '</div></div>' + '   <div class="layui-form-item "><label>参数：</label>' + '<div class="layui-inline" >' + checkStatusData[0].energyParaName + '</div></div>' + '   <div class="layui-form-item " style="color:red;">确定要删除此数据源吗？</div>' + '</form></div>',
      success: function success() {},
      yes: function yes(index) {
        var formData = {};
        formData.objType = JSON.parse(sessionStorage.getItem("parkId")).type;
        formData.objId = JSON.parse(sessionStorage.getItem("parkId")).id;
        formData.energyTypeId = checkStatusData[0].energyTypeId;
        formData.energyParaId = checkStatusData[0].energyParaId;
        load();
        request.service({
          method: 'post',
          url: '/datasource/deleteDataSource',
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
    layer.tips('字母、数字组合，长度不超过10，在当前报警内唯一。', '#add_alarmConditionId_tip');
  }); //提示弹框点击 -多条件间关系

  $("#add_conditionsLogic_tip").on("click", function () {
    layer.tips('当设置条件2时，条件间关系必需选择一个。', '#add_conditionsLogic_tip');
  });
  initFun(); //页面加载时执行初始函数
});