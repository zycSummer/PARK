

/*
 * @Author: jpp
 * @Date: 2019-11-20 10:07:34
 * @Last Modified by: xzl
 * @Last Modified time: 2019-12-24 14:35:02
 */
layui.use(['request', 'form', 'element', 'layer', 'jquery', 'slider', 'tree'], function () {
  var form = layui.form,
      layer = layui.layer,
      tree = layui.tree,
      $ = layui.jquery,
      slider = layui.slider,
      treeGrid = layui.treeGrid,
      element = layui.element;
  var siteObj = JSON.parse(sessionStorage.getItem('parkId'));
  var tree1; //左侧具体节点

  var nodeTableData = [];
  var parentSelect; //弹窗 父节点 单选

  var objNavThis; //弹窗 非标煤 企业/仪表

  var objIdMeter, objTypeMeter; //弹窗 非标煤 企业选中的

  var parkRtdbId = sessionStorage.getItem('parkRtdb'); //园区标识对应的实时库租户标识

  var siteRtdbId; //非标煤 仪表所属的企业对应的实时库项目标识

  var stdNavThis; //弹窗 标煤 企业/仪表/参数

  var stdObjId, stdObjType; //弹窗 标煤 企业选中的

  var stdMeterId, stdEnergyTypeId; //弹窗 标煤 仪表选中的

  var stdSiteRtdbId; //弹窗 标煤 仪表所属的企业对应的实时库项目标识

  var energyParaData; //弹窗 标煤参数 下拉框数据

  funcsAuthority(); //监听提交

  form.on('submit(*)', function (data) {
    return false; //阻止表单跳转
  }); //左侧列表 查询

  $('.left_query').on('click', function () {
    getTree1();
  }); //左侧 新增展示结构树

  $('.add').on('click', function () {
    $(".add_treeId,.add_tree_energyTypeId").removeClass('layui-hide');
    $(".edit_treeId,.edit_tree_energyTypeId").addClass('layui-hide');
    $("#treeId").val('');
    $("#treeName").val('');
    $("#tree_energyTypeId").val($("#tree_energyTypeId option:first-child").val());
    $("#tree_isUse").attr('checked', false);
    $("#tree_sortId").val('');
    $("#tree_memo").val('');
    add('add', '新增展示结构树');
  }); //左侧 修改展示结构树

  $('.edit').on('click', function () {
    var val = tree1.getSelectedNodes();
    if (val.length == 0) return layer.msg('请选择一条展示结构树');
    $(".edit_treeId,.edit_tree_energyTypeId").removeClass('layui-hide');
    $(".add_treeId,.add_tree_energyTypeId").addClass('layui-hide');
    $("#edit_treeId").html(val[0].orgTreeId);
    $("#treeName").val(val[0].orgTreeName);
    $("#edit_tree_energyTypeId").html(val[0].energyTypeName);
    $("#tree_isUse").attr('checked', val[0].isUse);
    $("#tree_sortId").val(val[0].sortId);
    $("#tree_memo").val(val[0].memo);
    add('edit', '修改展示结构树');
  }); //左侧 删除展示结构树

  $('.delete').on('click', function () {
    var val = tree1.getSelectedNodes();
    if (val.length == 0) return layer.msg('请选择要删除的展示结构树');
    deletes();
  }); //左侧 启用或停用展示结构树

  $('.startOrStop').on('click', function () {
    var val = tree1.getSelectedNodes();
    if (val.length == 0) return layer.msg('请选择要启用或停用的展示结构树');
    startOrStop();
  }); //右侧 新增展示结构树节点明细

  $('.node_add').on('click', function () {
    var val = tree1.getSelectedNodes();
    if (val.length == 0) return layer.msg('请选择一条报表');
    $(".add_treeDetail_nodeId").removeClass('layui-hide');
    $(".edit_treeDetail_nodeId").addClass('layui-hide');
    $("#treeDetail_nodeId").val('');
    var radioSelect = treeGrid.radioStatus('treeDetail_table');

    if (!radioSelect.nodeId) {
      parentSelect.setValue([]);
    } else {
      parentSelect.update({
        radio: true,
        clickClose: true,
        prop: {
          name: 'nodeName',
          value: 'nodeId'
        },
        data: [{
          "nodeName": radioSelect.title,
          "nodeId": radioSelect.nodeId
        }]
      });
      parentSelect.setValue([radioSelect.nodeId]);
    }

    $("#treeDetail_nodeName").val('');
    $("#treeDetail_sortId").val('');
    $("#treeDetail_memo").val('');

    if (val[0].energyTypeId == 'std_coal') {
      //标煤
      $("#std_ds_data1,#std_ds_data2,#std_meterParam_list").html(''); //仪表列表 数据源

      $("#pa_formula").val(''); //公式

      $("#std_coal_param").val($("#std_coal_param option:first-child").val());
    } else {
      //非标煤
      $("#ds_data").html(''); //仪表列表 数据源

      $("#formula").val(''); //公式
    }

    addTreeDetail('add', '新增展示结构树节点');
  }); //右侧 修改展示结构树节点明细

  $('.node_edit').on('click', function () {
    var radioSelect = treeGrid.radioStatus('treeDetail_table');

    if (!radioSelect.nodeId) {
      return layer.msg("请选择需要修改的数据！");
    }

    $(".edit_treeDetail_nodeId").removeClass('layui-hide');
    $(".add_treeDetail_nodeId").addClass('layui-hide');
    $("#edit_treeDetail_nodeId").html(radioSelect.nodeId);

    if (radioSelect.parentId) {
      parentSelect.update({
        radio: true,
        clickClose: true,
        prop: {
          name: 'nodeName',
          value: 'nodeId'
        },
        data: [{
          "nodeName": radioSelect.parentTitle,
          "nodeId": radioSelect.parentId
        }]
      });
      parentSelect.setValue([radioSelect.parentId]);
    } else {
      parentSelect.update({
        radio: true,
        clickClose: true,
        prop: {
          name: 'nodeName',
          value: 'nodeId'
        },
        data: []
      });
      parentSelect.setValue([]);
    }

    $("#treeDetail_nodeName").val(radioSelect.nodeName);
    $("#treeDetail_sortId").val(radioSelect.sortId);
    $("#treeDetail_memo").val(radioSelect.memo);
    var val = tree1.getSelectedNodes();

    if (val[0].energyTypeId == 'std_coal') {
      //标煤
      renderStdDataSource(radioSelect.dataSource); //仪表列表 数据源
    } else {
      //非标煤
      renderDataSource(radioSelect.dataSource); //仪表列表 数据源
    }

    addTreeDetail('edit', '修改展示结构树节点');
  }); //右侧 展示结构树明细节点 删除

  $('.node_delete').on('click', function () {
    var radioSelect = treeGrid.radioStatus('treeDetail_table');

    if (!radioSelect.nodeId) {
      return layer.msg('请选择需要删除的数据');
    }

    var val = tree1.getSelectedNodes();
    $('#treeDetail_delete_treeId').html(val[0].showName);
    $('#treeDetail_delete_nodeId').html('[' + radioSelect.nodeId + ']' + radioSelect.nodeName);
    deleteTreeDetail();
  }); //右侧 展示结构树明细节点 导入

  $('.node_import').on('click', function () {
    var val = tree1.getSelectedNodes();
    $('#treeDetail_import_report').html(val[0].showName);
    importTreeDetail();
  }); //右侧 展示结构树明细节点导入 模板文件下载

  $("#node_download_demo").on('click', function () {
    var url = '/tree/download';
    window.location.href = encodeURI(url);
  }); //右侧 展示结构树明细节点 导出

  $('.node_export').on('click', function () {
    var val = tree1.getSelectedNodes();
    var url = '/tree/exportExcel?orgTreeId=' + val[0].orgTreeId + '&energyTypeName=' + val[0].energyTypeName + '&objId=' + siteObj.id + '&objType=' + siteObj.type;
    window.location.href = encodeURI(url);
  }); //点击 展示结构树明细节点弹框 非标煤 企业/仪表

  $('.obj_nav-item').on('click', function () {
    $(this).siblings().removeClass('obj_nav-this');
    $(this).addClass('obj_nav-this');
    objNavThis = $(this).attr('data-id');

    if (objNavThis == 'park') {
      $('#parkSiteTableContent').removeClass('layui-hide');
      $('#meterTableContent,.meter_btnGroup,.meterTap').addClass('layui-hide');
    } else {
      $('#parkSiteTableContent').addClass('layui-hide');
      $('#meterTableContent,.meter_btnGroup,.meterTap').removeClass('layui-hide');
    }

    return false;
  }); //点击 展示结构树明细节点弹框 非标煤 企业查询

  $('.obj_parkSiteQuery').on('click', function () {
    getParkOrSite();
  }); //点击 展示结构树明细节点弹框 非标煤 仪表查询

  $('.obj_meterQuery').on('click', function () {
    getMeterTable();
  }); //点击 展示结构树明细节点弹框 非标煤 企业列表

  $(document).on("click", '#parkSiteTbody tr', function () {
    $(this).addClass('layui-this');
    $(this).siblings().removeClass('layui-this');
    objIdMeter = $(this).attr('data-id');
    objTypeMeter = $(this).attr('data-type');
    siteRtdbId = $(this).data("rtdbprojectid"); //弹窗 企业选中的

    getMeterTable(); //点击某一具体企业，切换到仪表标签页

    $('.obj_nav-item').removeClass('obj_nav-this');
    $('.obj_nav-item:nth-child(2)').addClass('obj_nav-this');
    $('#parkSiteTableContent').addClass('layui-hide');
    $('#meterTableContent,.meter_btnGroup,.meterTap').removeClass('layui-hide');
  }); //双击 展示结构树明细节点弹框 非标煤 仪表列表

  $(document).on("dblclick", '#meterTbody tr', function () {
    var meterId = $(this).attr('data-id');
    var dataSocureText = parkRtdbId + '.' + siteRtdbId + '.' + meterId; //拼接数据源
    // 园区标识对应的实时库租户标识（SELECT rtdb_tenant_id FROM `tb_park` LIMIT 1;）+ "."
    // + 仪表所属的企业对应的实时库项目标识（SELECT rtdb_project_id FROM `tb_site` WHERE site_id = ?;）+ "."
    // +  仪表标识

    var str = '<tr data-socure="' + dataSocureText + '"><td>' + '<input type="checkbox" name="dataSocurecheckbox[' + dataSocureText + ']" value="' + dataSocureText + '" title="' + dataSocureText + '" lay-skin="primary"></td></tr>';
    $('#ds_data').append(str);
    form.render('checkbox', 'ds_list');
  }); //点击 展示结构树明细节点弹框 非标煤 仪表列表 添加至所选之前

  $('#addSelectedBefore').on('click', function () {
    var checkboxArr = $("#ds_data input:checkbox");
    var checkedArr = $("#ds_data input:checkbox:checked");
    if (checkboxArr.length > 0 && checkedArr.length == 0) return layer.msg('请选择仪表列表');
    $("#meterTbody input:checkbox:checked").each(function (i) {
      var meterId = $(this).val();
      var dataSocureText = parkRtdbId + '.' + siteRtdbId + '.' + meterId; //拼接数据源

      var str = '<tr data-socure="' + dataSocureText + '"><td>' + '<input type="checkbox" name="dataSocurecheckbox[' + dataSocureText + ']" value="' + dataSocureText + '" title="' + dataSocureText + '" lay-skin="primary"></td></tr>';

      if (checkboxArr.length == 0) {
        $("#ds_data").append(str);
      } else {
        $("#ds_data input:checkbox:checked").eq(0).parent().parent().before(str);
      }
    });
    form.render('checkbox', 'ds_list');
  }); //点击 展示结构树明细节点弹框 非标煤 仪表列表 添加至末尾

  $('#addSelectedAfter').on('click', function () {
    $("#meterTbody input:checkbox:checked").each(function (i) {
      var meterId = $(this).val();
      var dataSocureText = parkRtdbId + '.' + siteRtdbId + '.' + meterId; //拼接数据源

      var str = '<tr data-socure="' + dataSocureText + '"><td>' + '<input type="checkbox" name="dataSocurecheckbox[' + dataSocureText + ']" value="' + dataSocureText + '" title="' + dataSocureText + '" lay-skin="primary"></td></tr>';
      $("#ds_data").append(str);
    });
    form.render('checkbox', 'ds_list');
  }); //点击 展示结构树明细节点弹框 非标煤 仪表列表 全选

  $('#allSelect').on('click', function () {
    var option = {};
    $("#meterTbody input:checkbox").each(function (i) {
      option["meterId[" + $(this).val() + "]"] = true;
    });
    form.val("meter_list", option);
  }); //点击 展示结构树明细节点弹框 非标煤 仪表列表 反选

  $('#allUnSelect').on('click', function () {
    var option = {};
    $("#meterTbody input:checkbox").each(function (i) {
      option["meterId[" + $(this).val() + "]"] = $(this)[0].checked == true ? false : true;
    });
    form.val("meter_list", option);
  }); //点击 展示结构树明细节点弹框 非标煤 仪表列表 取消

  $('#cancel').on('click', function () {
    var option = {};
    $("#meterTbody input:checkbox").each(function (i) {
      option["meterId[" + $(this).val() + "]"] = false;
    });
    form.val("meter_list", option);
  }); //点击 展示结构树明细节点弹框 非标煤 数据源 删除所选

  $('#removeSelected').on('click', function () {
    $("#ds_data input:checkbox:checked").each(function (i) {
      $(this).parent().parent('tr').remove();
    });
  }); //点击 展示结构树明细节点弹框 非标煤 数据源 清空

  $('#clearSelected').on('click', function () {
    $("#ds_data").html('');
  }); //点击 展示结构树明细节点弹框 非标煤 数据源公式 检查

  $('#checkFormula').on('click', function () {
    var dataSocureLength = $('#ds_data tr').length; //数据源添加的数据源长度

    var formulaVal = $("#formula").val();
    var selectValArr = [];

    if (formulaVal.length > 0) {
      for (var index = 0; index < formulaVal.length; index++) {
        var _element = formulaVal[index];

        if (_element == '?') {
          selectValArr.push(_element);
        }
      }
    }

    if (dataSocureLength == 0) {
      return layer.msg('数据源仪表列表列表为空');
    } else if (dataSocureLength == 1) {
      if (selectValArr.length == 1) {
        return layer.msg('公式中的问号个数不能为一个');
      } else if (selectValArr.length > 1) {
        return layer.msg('数据源-仪表参数列表的个数 和 公式中的问号个数不一致');
      }
    } else {
      if (formulaVal.length > 0) {
        if (selectValArr.length != dataSocureLength) {
          //检查仪表参数列表的个数和公式中的问号个数是否一致，不一致时提醒用户
          return layer.msg('数据源-仪表参数列表的个数 和 公式中的问号个数不一致');
        }
      } else {
        return layer.msg('未填写公式');
      }
    }
  }); //点击 展示结构树明细节点弹框 非标煤 数据源公式 所有仪表相加

  $('#addAllMeter').on('click', function () {
    // 点击 所有仪表相加 按钮，则将上面仪表列表中的所有仪表相加，即?+?+?....有多少个仪表就有多少个?+，
    // 最后将最后一个加号删除。如果只有一个仪表，则公式结果为空。
    var dataSocureLength = $('#ds_data tr').length; //数据源添加的数据源长度

    if (dataSocureLength.length == 0) {
      return layer.msg('未添加仪表参数');
    }

    var addFormulaVal = '';

    if (dataSocureLength == 1) {
      //如果只有一个仪表参数，则公式结果为空。
      addFormulaVal = '';
    } else {
      for (var index = 0; index < dataSocureLength; index++) {
        if (index != dataSocureLength - 1) {
          addFormulaVal += '?+';
        } else {
          addFormulaVal += '?';
        }
      }
    }

    $("#formula").val(addFormulaVal);
  }); //点击 展示结构树明细节点弹框 非标煤 数据源公式 清空

  $('#clearFormula').on('click', function () {
    $("#formula").val('');
  }); //点击 展示结构树明细节点弹框 标煤 企业/仪表/参数

  $('.std_nav-item').on('click', function () {
    $(this).siblings().removeClass('std_nav-this');
    $(this).addClass('std_nav-this');
    stdNavThis = $(this).attr('data-id');

    if (stdNavThis == 'park') {
      $('#std_parkSiteTableContent').removeClass('layui-hide');
      $('#std_meterTableContent').addClass('layui-hide');
      $('#std_paramTableContent,.param_btnGroup,.paramTap').addClass('layui-hide');
    } else if (stdNavThis == 'site') {
      $('#std_parkSiteTableContent').addClass('layui-hide');
      $('#std_meterTableContent').removeClass('layui-hide');
      $('#std_paramTableContent,.param_btnGroup,.paramTap').addClass('layui-hide');
    } else {
      $('#std_parkSiteTableContent').addClass('layui-hide');
      $('#std_meterTableContent').addClass('layui-hide');
      $('#std_paramTableContent,.param_btnGroup,.paramTap').removeClass('layui-hide');
    }

    return false;
  }); //点击 展示结构树明细节点弹框 标煤 企业查询

  $('.std_parkSiteQuery').on('click', function () {
    getParkOrSite();
  }); //点击 展示结构树明细节点弹框 标煤 仪表查询

  $('.std_meterQuery').on('click', function () {
    getStdMeterTable();
  }); //点击 展示结构树明细节点弹框 标煤 参数查询

  $('.std_paramQuery').on('click', function () {
    getStdParamTable();
  }); //点击 展示结构树明细节点弹框 标煤 企业列表

  $(document).on("click", '#std_parkSiteTbody tr', function () {
    $(this).addClass('layui-this');
    $(this).siblings().removeClass('layui-this');
    stdObjId = $(this).attr('data-id');
    stdObjType = $(this).attr('data-type');
    stdSiteRtdbId = $(this).data("rtdbprojectid"); //弹窗 企业选中的

    getStdMeterTable();
    $('#std_paramTbody').html(''); //点击某一具体企业，切换到仪表标签页

    $('.std_nav-item').removeClass('std_nav-this');
    $('.std_nav-item:nth-child(2)').addClass('std_nav-this');
    $('#std_parkSiteTableContent,#std_paramTableContent,.param_btnGroup,.paramTap').addClass('layui-hide');
    $('#std_meterTableContent').removeClass('layui-hide');
  }); //点击 展示结构树明细节点弹框 标煤 仪表列表

  $(document).on("click", '#std_meterTbody tr', function () {
    $(this).addClass('layui-this');
    $(this).siblings().removeClass('layui-this');
    stdMeterId = $(this).attr('data-id'); //弹窗 仪表选中的

    stdEnergyTypeId = $(this).attr('data-energyTypeId'); //弹窗 仪表选中的

    getStdParamTable(); //点击某一具体仪表，切换到参数标签页

    $('.std_nav-item').removeClass('std_nav-this');
    $('.std_nav-item:nth-child(3)').addClass('std_nav-this');
    $('#std_parkSiteTableContent,#std_meterTableContent').addClass('layui-hide');
    $('#std_paramTableContent,.param_btnGroup,.paramTap').removeClass('layui-hide');
  }); //双击 展示结构树明细节点弹框 标煤 参数列表

  $(document).on("dblclick", '#std_paramTbody tr', function () {
    var energyParaId = $(this).attr('data-id');
    var dataSocureText = parkRtdbId + '.' + stdSiteRtdbId + '.' + stdMeterId + '.' + energyParaId + '[' + stdEnergyTypeId + ']'; //拼接数据源

    /*园区标识对应的实时库租户标识（SELECT rtdb_tenant_id FROM `tb_park` LIMIT 1;）+ "." +
    仪表所属的企业对应的实时库项目标识（SELECT rtdb_project_id FROM `tb_site` WHERE site_id = ?;）+ "."
    +  仪表标识 + "." + 参数标识 + "[" + 仪表所属能源种类标识 + "]"
    */

    var str = '<tr data-socure="' + dataSocureText + '"><td>' + '<input type="checkbox" name="dataSocurecheckbox[' + dataSocureText + ']" value="' + dataSocureText + '" title="' + dataSocureText + '" lay-skin="primary"></td></tr>';
    $('#std_meterParam_list').append(str);
    form.render('checkbox', 'std_meterParam_list');
  }); //点击 展示结构树明细节点弹框 标煤 参数列表 添加至所选之前

  $('#std_addSelectedBefore').on('click', function () {
    var checkboxArr = $("#std_meterParam_list input:checkbox");
    var checkedArr = $("#std_meterParam_list input:checkbox:checked");
    if (checkboxArr.length > 0 && checkedArr.length == 0) return layer.msg('请选择仪表参数列表');
    $("#std_paramTbody input:checkbox:checked").each(function (i) {
      var energyParaId = $(this).val();
      var dataSocureText = parkRtdbId + '.' + stdSiteRtdbId + '.' + stdMeterId + '.' + energyParaId + '[' + stdEnergyTypeId + ']'; //拼接数据源

      var str = '<tr data-socure="' + dataSocureText + '"><td>' + '<input type="checkbox" name="dataSocurecheckbox[' + dataSocureText + ']" value="' + dataSocureText + '" title="' + dataSocureText + '" lay-skin="primary"></td></tr>';

      if (checkboxArr.length == 0) {
        $("#std_meterParam_list").append(str);
      } else {
        $("#std_meterParam_list input:checkbox:checked").eq(0).parent().parent().before(str);
      }
    });
    form.render('checkbox', 'std_meterParam_list');
  }); //点击 展示结构树明细节点弹框 标煤 参数列表 添加至末尾

  $('#std_addSelectedAfter').on('click', function () {
    $("#std_paramTbody input:checkbox:checked").each(function (i) {
      var energyParaId = $(this).val();
      var dataSocureText = parkRtdbId + '.' + stdSiteRtdbId + '.' + stdMeterId + '.' + energyParaId + '[' + stdEnergyTypeId + ']'; //拼接数据源

      var str = '<tr data-socure="' + dataSocureText + '"><td>' + '<input type="checkbox" name="dataSocurecheckbox[' + dataSocureText + ']" value="' + dataSocureText + '" title="' + dataSocureText + '" lay-skin="primary"></td></tr>';
      $("#std_meterParam_list").append(str);
    });
    form.render('checkbox', 'std_meterParam_list');
  }); //点击 展示结构树明细节点弹框 标煤 数据源 删除所选

  $('#ds_removeSelected').on('click', function () {
    $("#std_ds_data1 input:radio:checked").each(function (i) {
      var dataSource = $(this).parent().parent('tr').attr('data-source');

      if (dataSource == $("#std_ds_data2").html()) {
        $("#std_ds_data2").html('');
      }

      $(this).parent().parent('tr').remove();
    });
  }); //点击 展示结构树明细节点弹框 标煤 数据源 清空

  $('#ds_clearSelected').on('click', function () {
    $("#std_ds_data1,#std_ds_data2").html('');
  }); //点击 展示结构树明细节点弹框 标煤 仪表参数列表 删除所选

  $('#pa_removeSelected').on('click', function () {
    $("#std_meterParam_list input:checkbox:checked").each(function (i) {
      $(this).parent().parent('tr').remove();
    });
  }); //点击 展示结构树明细节点弹框 标煤 仪表参数列表 清空

  $('#pa_clearSelected').on('click', function () {
    $("#std_meterParam_list").html('');
  }); //点击 展示结构树明细节点弹框 标煤 公式 检查

  $('#pa_checkFormula').on('click', function () {
    var dataSocureLength = $('#std_meterParam_list tr').length; //仪表参数列表的个数

    var formulaVal = $("#pa_formula").val();
    var selectValArr = formulaVal ? formulaVal.split('+') : []; //公式中的问号个数

    if (dataSocureLength == 0) {
      return layer.msg('仪表参数列表为空');
    } else if (dataSocureLength == 1) {
      if (selectValArr.length == 1) {
        return layer.msg('公式中的问号个数不能为一个');
      } else if (selectValArr.length > 1) {
        return layer.msg('仪表参数列表的个数 和 公式中的问号个数不一致');
      }
    } else {
      if (formulaVal.length > 0) {
        if (selectValArr.length != dataSocureLength) {
          //检查仪表参数列表的个数和公式中的问号个数是否一致，不一致时提醒用户
          return layer.msg('仪表参数列表的个数 和 公式中的问号个数不一致');
        }
      } else {
        return layer.msg('未填写公式');
      }
    }
  }); //点击 展示结构树明细节点弹框 标煤 公式 所有仪表相加

  $('#pa_addAllMeter').on('click', function () {
    // 则将上面仪表参数列表中的所有仪表参数相加，即?+?+?....
    // 有多少个仪表参数就有多少个?+，最后将最后一个加号删除，如果只有一个仪表参数，则公式为空。
    var dataSocureLength = $('#std_meterParam_list tr').length; //仪表参数列表长度

    if (dataSocureLength.length == 0) {
      return layer.msg('未添加仪表参数');
    }

    var addFormulaVal = '';

    if (dataSocureLength == 1) {
      //如果只有一个仪表参数，则公式结果为空。
      addFormulaVal = '';
    } else {
      for (var index = 0; index < dataSocureLength; index++) {
        if (index != dataSocureLength - 1) {
          addFormulaVal += '?+';
        } else {
          addFormulaVal += '?';
        }
      }
    }

    $("#pa_formula").val(addFormulaVal);
  }); //点击 展示结构树明细节点弹框 标煤 公式 清空

  $('#pa_clearFormula').on('click', function () {
    $("#pa_formula").val('');
  }); //点击 展示结构树明细节点弹框 标煤 添加/更新到左侧数据源按钮

  $('#addOrUpdateDs').on('click', function () {
    var dataSocureLength = $('#std_meterParam_list tr').length; //仪表参数列表长度

    var formulaVal = $("#pa_formula").val();
    var selectValArr = formulaVal ? formulaVal.split('+') : []; //公式中的问号个数

    if (dataSocureLength == 0) {
      layer.msg('请至少选择一个仪表参数');
      return false;
    } else if (dataSocureLength == 1) {
      if (selectValArr.length == 1) {
        layer.msg('公式中的问号个数不能为一个');
        return false;
      } else if (selectValArr.length > 1) {
        layer.msg('仪表参数列表的个数 和 公式中的问号个数不一致');
        return false;
      }
    } else {
      if (formulaVal.length > 0) {
        if (selectValArr.length != dataSocureLength) {
          //检查仪表参数列表的个数和公式中的问号个数是否一致，不一致时提醒用户
          layer.msg('公式检查失败');
          return false;
        }
      } else {
        layer.msg('公式检查失败');
        return false;
      }
    }

    var energyParaId = $("#std_coal_param").val();
    var energyParaName = $("#std_coal_param option:selected").text();
    var isHas = false; //添加

    if ($("#std_ds_data1 tr").length > 0) {
      $("#std_ds_data1 tr").each(function (i) {
        var thisParaId = $(this).attr('data-paraId');

        if (thisParaId == energyParaId) {
          isHas = true; //更新
        }
      });
    } //如果公式为空，则直接将仪表参数列表中的唯一记录添加/更新到左侧数据源的右侧部分；
    //如果公式不为空，则将仪表参数列表（用逗号拼接） + “#” + 公式 添加/更新到左侧数据源的右侧部分


    var dataSource = '';

    if (!formulaVal && dataSocureLength == 1) {
      dataSource = $('#std_meterParam_list tr:first-child').attr('data-socure');
    } else {
      var dataSourceArr = [];
      $('#std_meterParam_list tr').each(function (i) {
        dataSourceArr.push($(this).attr('data-socure'));
      });
      var formula = $("#pa_formula").val();
      dataSource = dataSourceArr.join(',') + '#' + formula;
    }

    if (!isHas) {
      //添加
      var str = '<tr data-paraId="' + energyParaId + '" data-source="' + dataSource + '"><td><input type="radio" name="energyParaId" value="' + energyParaId + '" title="' + energyParaName + '"></td></tr>';
      $("#std_ds_data1").append(str);
      $("#std_ds_data2").html(dataSource);
    } else {
      if ($("#std_ds_data1 tr").length > 0) {
        $("#std_ds_data1 tr").each(function (i) {
          var thisParaId = $(this).attr('data-paraId');

          if (thisParaId == energyParaId) {
            $(this).attr('data-source', dataSource);
          }
        });
      }

      $("#std_ds_data2").html(dataSource);
    }

    form.render('radio', 'std_ds_list');
    return false;
  }); //双击 展示结构树明细节点弹框 数据源参数列表

  $(document).on("dblclick", '#std_ds_data1 tr', function () {
    /* 右侧的仪表参数列表、公式、标煤参数  3部分就会变成刚才双击的参数对应的参数列表及公式
    （即根据所选参数对应的公式解析出对应的参数列表及公式，当只有一个参数时，公式为空），用户
     在此修改后，点击下方的“<<< 添加/更新到左侧数据源”按钮即可将修改后的值更新到左侧的数据源中。
     */
    var dataSource = $(this).attr('data-source');
    var energyParaId = $(this).attr('data-paraId');
    $("#std_coal_param").val(energyParaId);
    $("#std_ds_data2").html(dataSource);
    var arr = dataSource.split('#');
    var formula = arr.length > 1 ? arr[1] : '';
    $("#pa_formula").val(formula);
    var dataSourceArr = arr[0].split(',');
    var str = '';
    $.each(dataSourceArr, function (i, v) {
      str += '<tr data-socure="' + v + '"><td>' + '<input type="checkbox" name="dataSocurecheckbox[' + v + ']" value="' + v + '" title="' + v + '" lay-skin="primary"></td></tr>';
    });
    $("#std_meterParam_list").html(str);
    form.render('checkbox', 'std_meterParam_list');
  }); //展示结构树弹框 提示点击 - 展示结构树标识

  $("#treeId_tip").on("click", function () {
    layer.tips('字母、数字、下划线组合，长度不超过20,在园区/当前企业内唯一。', '#treeId_tip');
  }); //展示结构树弹框 提示点击 - 展示结构树名称

  $("#treeName_tip").on("click", function () {
    layer.tips('长度不超过30。', '#treeName_tip');
  }); //展示结构树弹框 提示点击 - 排序标识

  $("#tree_sortId_tip").on("click", function () {
    layer.tips('字母、数字、下划线组合，长度不超过10。', '#tree_sortId_tip');
  }); //展示结构树节点明细弹框 提示点击 - 节点ID

  $("#treeDetail_nodeId_tip").on("click", function () {
    layer.tips('字母、数字、下划线组合，长度不超过20,在当前报表内唯一。', '#treeDetail_nodeId_tip');
  }); //展示结构树节点明细弹框 提示点击 - 节点名称

  $("#treeDetail_nodeName_tip").on("click", function () {
    layer.tips('长度不超过30。', '#treeDetail_nodeName_tip');
  }); //展示结构树节点明细弹框 提示点击 - 排序标识

  $("#treeDetail_sortId_tip").on("click", function () {
    layer.tips('字母、数字、下划线组合，长度不超过10。', '#treeDetail_sortId_tip');
  });
  /*----------------------------------函数------------------------------------*/
  // 新增修改展示结构树

  function add(type, title) {
    layui.layer.open({
      type: 1,
      title: title,
      closeBtn: 1,
      shade: 0,
      anim: 1,
      maxmin: true,
      //开启最大化最小化按钮
      area: ['450px', '500px'],
      content: $('#tree_add'),
      btn: ['确定', '取消'],
      success: function success() {
        $("#tree_add").removeClass('layui-hide');
        form.render();
      },
      yes: function yes(index) {
        var isMainPageArr = $('#tree_isUse:checked');
        var isUse = false;

        if (isMainPageArr.length > 0) {
          isUse = true;
        }

        var val = tree1.getSelectedNodes();

        if (type == 'edit') {
          var data = {
            "objType": siteObj.type,
            "objId": siteObj.id,
            "id": val[0].id,
            "orgTreeId": val[0].orgTreeId,
            "orgTreeName": $("#treeName").val(),
            "energyTypeId": val[0].energyTypeId,
            'isUse': isUse,
            "sortId": $("#tree_sortId").val(),
            "memo": $("#tree_memo").val()
          };
        } else if (type == 'add') {
          var data = {
            "objType": siteObj.type,
            "objId": siteObj.id,
            "orgTreeId": $("#treeId").val(),
            "orgTreeName": $("#treeName").val(),
            "energyTypeId": $("#tree_energyTypeId").val(),
            'isUse': isUse,
            "sortId": $("#tree_sortId").val(),
            "memo": $("#tree_memo").val()
          };
          if (data.orgTreeId == '') return layer.msg('展示结构树标识不能为空！');
          if (data.orgTreeId.length > 20) return layer.msg('展示结构树标识长度不能超过20！');
          var re = /^[0-9a-zA-Z]+$/g;

          if (!re.test(data.orgTreeId)) {
            return layer.msg('展示结构树标识为字母、数字组合！');
          }
        }

        if (data.orgTreeName == '') return layer.msg('展示结构树名称不能为空！');
        if (data.orgTreeName.length > 30) return layer.msg('展示结构树名称长度不能超过30！');
        if (data.sortId.length > 10) return layer.msg('排序标识长度不能超过10！');
        var re2 = /^[0-9a-zA-Z]+$/g;

        if (data.sortId.length > 0 && !re2.test(data.sortId)) {
          return layer.msg('排序标识为字母、数字组合！');
        }

        load();
        request.service({
          method: 'post',
          url: '/tree/insertOrUpdateOrgTree',
          data: data
        }).then(function (res) {
          disLoad();
          getTree1();
          layer.close(index);
        })["catch"](function (err) {
      disLoad();
      console.log(err);
    });
      },
      end: function end(index) {
        $("#tree_add").addClass('layui-hide');
      }
    });
  } // 删除展示结构树


  function deletes() {
    layer.open({
      type: 1,
      title: '删除展示结构树',
      closeBtn: 1,
      shade: 0,
      anim: 1,
      maxmin: true,
      //开启最大化最小化按钮
      area: ['400px', '350px'],
      content: $('#tree_delete'),
      btn: ['确定', '取消'],
      success: function success() {
        $("#tree_delete").removeClass('layui-hide');
        var val = tree1.getSelectedNodes();
        $("#delete_treeId").html(val[0].orgTreeId);
        $("#delete_treeName").html(val[0].orgTreeName);
        $("#delete_energyTypeId").html(val[0].energyTypeName);
        form.render();
      },
      yes: function yes(index) {
        var val = tree1.getSelectedNodes();
        load();
        request.service({
          method: 'post',
          url: '/tree/deleteOrgTree',
          data: {
            "objType": siteObj.type,
            "objId": siteObj.id,
            "orgTreeId": val[0].orgTreeId
          }
        }).then(function (res) {
          disLoad();
          getTree1();
          layer.close(index);
        })["catch"](function (err) {
      disLoad();
      console.log(err);
    });
      },
      end: function end(index) {
        $("#tree_delete").addClass('layui-hide');
      }
    });
  } // 启用或停用展示结构树


  function startOrStop() {
    var val = tree1.getSelectedNodes();

    if (siteObj) {
      load();
      request.service({
        method: 'post',
        url: '/tree/startOrOver',
        data: {
          'objType': siteObj.type,
          'objId': siteObj.id,
          'orgTreeId': val[0].orgTreeId,
          'isUse': val[0].isUse == true ? false : true
        }
      }).then(function (res) {
        disLoad();
        getTree1();
      })["catch"](function (err) {
      disLoad();
      console.log(err);
    });
    }
  } // 新增修改展示结构树节点明细


  function addTreeDetail(type, title) {
    layer.open({
      type: 1,
      title: title,
      closeBtn: 1,
      shade: 0,
      anim: 1,
      maxmin: true,
      //开启最大化最小化按钮
      area: ['1650px', '750px'],
      content: $('#treeDetail_add'),
      btn: ['确定', '取消'],
      success: function success() {
        $("#treeDetail_add").removeClass('layui-hide');
        var val = tree1.getSelectedNodes();
        $('#treeDetail_treeName').html(val[0].showName);
        form.render();

        if (val[0].energyTypeId == 'std_coal') {
          //标煤
          $("#is_std_coal").removeClass('layui-hide');
          $("#isNot_std_coal").addClass('layui-hide');

          if (siteObj.type == 'PARK') {
            //当为园区时请求获取当前园区下的企业
            $('.std_nav-item').removeClass('std_nav-this');
            $('.std_nav-item:nth-child(1)').addClass('std_nav-this');
            $('#std_parkSiteTableContent').removeClass('layui-hide');
            $('#std_meterTableContent,#std_paramTableContent,.param_btnGroup,.paramTap').addClass('layui-hide');
          } else {
            //当不为园区时 选择为某个企业时
            $('.std_nav-item').removeClass('std_nav-this');
            $('.std_nav-item:nth-child(2)').addClass('std_nav-this');
            $('#std_parkSiteTableContent,#std_paramTableContent,.param_btnGroup,.paramTap').addClass('layui-hide');
            $('#std_meterTableContent').removeClass('layui-hide');
            getStdMeterTable();
          }
        } else {
          //非标煤
          $("#isNot_std_coal").removeClass('layui-hide');
          $("#is_std_coal").addClass('layui-hide');

          if (siteObj.type == 'PARK') {
            //当为园区时请求获取当前园区下的企业
            $('.obj_nav-item').removeClass('obj_nav-this');
            $('.obj_nav-item:nth-child(1)').addClass('obj_nav-this');
            $('#parkSiteTableContent').removeClass('layui-hide');
            $('#meterTableContent,.meter_btnGroup,.meterTap').addClass('layui-hide');
          } else {
            $('.obj_nav-item').removeClass('obj_nav-this');
            $('.obj_nav-item:nth-child(2)').addClass('obj_nav-this');
            $('#parkSiteTableContent').addClass('layui-hide');
            $('#meterTableContent,.meter_btnGroup,.meterTap').removeClass('layui-hide');
            getMeterTable();
          }
        }
      },
      yes: function yes(index) {
        var val = tree1.getSelectedNodes();
        var selectData = parentSelect.getValue();
        selectData = selectData.length > 0 ? selectData[0].nodeId : ''; //获取 数据源

        var dataSource;

        if (val[0].energyTypeId == 'std_coal') {
          //标煤
          if ($("#std_ds_data1 tr").length > 0) {
            var dataSourceArr0 = [];
            $("#std_ds_data1 tr").each(function (i) {
              var obj = {
                "para_id": $(this).attr('data-paraId'),
                "data_source": $(this).attr('data-source')
              };
              dataSourceArr0.push(obj);
            });
            dataSource = JSON.stringify(dataSourceArr0);
          } else {
            dataSource = null;
          }
        } else {
          //非标煤
          var dataSourceArr = [];
          $('#ds_data tr').each(function (i) {
            dataSourceArr.push($(this).attr('data-socure'));
          });

          if (dataSourceArr.length === 0) {
            //当为数据源时 值为null
            dataSource = null;
          } else {
            var formulaVal = $("#formula").val(); //公式

            if (formulaVal.length > 0) {
              var selectValArr = formulaVal.split('+');

              if (dataSourceArr.length == 1) {
                //如果仪表参数列表中只有一个仪表参数，公式可以为空，但不能只有1个问号
                if (selectValArr.length == 1) {
                  return layer.msg('公式检查失败');
                }
              }

              if (selectValArr.length != dataSourceArr.length) {
                //检查数据源-仪表参数列表的个数 和 公式中的问号个数是否一致，不一致时提醒用户
                return layer.msg('公式检查失败');
              }

              var str = dataSourceArr.join(',');
              dataSource = str + '#' + formulaVal;
            } else {
              if (dataSourceArr.length == 1) {
                dataSource = dataSourceArr[0];
              } else {
                return layer.msg('公式检查失败');
              }
            }
          }
        }

        if (type == 'edit') {
          var radioSelect = treeGrid.radioStatus('treeDetail_table');
          var data = {
            "objType": siteObj.type,
            "objId": siteObj.id,
            "id": radioSelect.id,
            "orgTreeId": val[0].orgTreeId,
            "nodeId": radioSelect.nodeId,
            "parentId": selectData,
            'nodeName': $("#treeDetail_nodeName").val(),
            "sortId": $("#treeDetail_sortId").val(),
            "memo": $("#treeDetail_memo").val(),
            "dataSource": dataSource
          };
        } else if (type == 'add') {
          var data = {
            "objType": siteObj.type,
            "objId": siteObj.id,
            "orgTreeId": val[0].orgTreeId,
            "nodeId": $("#treeDetail_nodeId").val(),
            "parentId": selectData,
            'nodeName': $("#treeDetail_nodeName").val(),
            "sortId": $("#treeDetail_sortId").val(),
            "memo": $("#treeDetail_memo").val(),
            "dataSource": dataSource
          };
          if (data.nodeId == '') return layer.msg('节点ID不能为空！');
          if (data.nodeId.length > 20) return layer.msg('节点ID长度不能超过20！');
          var re4 = /^[0-9a-zA-Z]+$/g;

          if (!re4.test(data.nodeId)) {
            return layer.msg('节点ID为字母、数字组合！');
          }
        }

        if (data.nodeName == '') return layer.msg('节点名称不能为空！');
        if (data.nodeName.length > 30) return layer.msg('节点名称长度不能超过30！');
        if (data.sortId.length > 10) return layer.msg('排序标识长度不能超过10！');
        var re5 = /^[0-9a-zA-Z]+$/g;

        if (!re5.test(data.sortId)) {
          return layer.msg('排序标识为字母、数字组合！');
        }

        load();
        request.service({
          method: 'post',
          url: '/tree/insertOrUpdateOrgTreeDetail',
          data: data
        }).then(function (res) {
          disLoad();
          getTreeDetailTable();
          layer.close(index);
        })["catch"](function (err) {
      disLoad();
      console.log(err);
    });
      },
      end: function end(index) {
        $("#treeDetail_add").addClass('layui-hide');
      }
    });
  } //非标煤 修改时的数据源


  function renderDataSource(dataSource) {
    $("#ds_data").html('');
    $("#formula").val('');

    if (dataSource && dataSource.length > 0) {
      if (dataSource.indexOf('#') > 0) {
        var arr = dataSource.split('#');
        var dataSourceArr = arr[0].split(',');
        var formulaVal = arr[1];
        $("#formula").val(formulaVal); //公式

        $.each(dataSourceArr, function (ii, vv) {
          var str = '<tr data-socure="' + vv + '"><td>' + '<input type="checkbox" name="dataSocurecheckbox[' + vv + ']" value="' + vv + '" title="' + vv + '" lay-skin="primary"></td></tr>';
          $("#ds_data").append(str);
        });
      } else {
        var str = '<tr data-socure="' + dataSource + '"><td>' + '<input type="checkbox" name="dataSocurecheckbox[' + dataSource + ']" value="' + dataSource + '" title="' + dataSource + '" lay-skin="primary"></td></tr>';
        $("#ds_data").append(str);
        $("#formula").val('');
      }

      form.render('checkbox', 'ds_list');
    }
  } //标煤 修改时的数据源


  function renderStdDataSource(dataSource) {
    var ds = JSON.parse(dataSource);
    var paraStr = '';
    $.each(ds, function (i0, v) {
      if (i0 == 0) {
        var arr = v['data_source'].split('#');
        var formula = arr.length > 1 ? arr[1] : '';
        $("#std_ds_data2").html(v['data_source']);
        $("#pa_formula").val(formula);
        var dataSourceArr = arr[0].split(',');
        var str = '';
        $.each(dataSourceArr, function (dd, ff) {
          str += '<tr data-socure="' + ff + '"><td>' + '<input type="checkbox" name="dataSocurecheckbox[' + ff + ']" value="' + ff + '" title="' + ff + '" lay-skin="primary"></td></tr>';
        });
        $("#std_meterParam_list").html(str);
      }

      var energyParaId = v['para_id'];
      var energyParaName = '';
      $.each(energyParaData, function (ii, vv) {
        if (vv.energyParaId == energyParaId) {
          energyParaName = vv.energyParaName;
        }
      });
      paraStr += '<tr data-paraId="' + energyParaId + '" data-source="' + v['data_source'] + '"><td><input type="radio" name="energyParaId" value="' + energyParaId + '" title="[' + energyParaId + ']' + energyParaName + '"></td></tr>';
    });
    $("#std_ds_data1").html(paraStr);
    form.render('checkbox', 'std_meterParam_list');
    form.render('radio', 'std_ds_list');
  } //仪表选择 企业


  function getParkOrSite() {
    var key = '';

    if (tree1) {
      var val = tree1.getSelectedNodes();

      if (val[0].energyTypeId == 'std_coal') {
        //标煤
        key = $("#std_parkSiteName").val();
      } else {
        //非标煤
        key = $("#obj_parkSiteName").val();
      }
    }

    if (siteObj.type == 'PARK') {
      //当为园区时请求获取当前园区下的企业
      load();
      showCompanySelect();
      request.service({
        method: 'get',
        url: '/common/getAllCurrentSite/' + key
      }).then(function (res) {
        disLoad();
        var str = '';
        $.each(res.data, function (kk, jj) {
          str += '<tr data-rtdbprojectid="' + jj.rtdbProjectId + '" data-id="' + jj.objId + '" data-type="' + jj.objType + '"><td>[' + jj.objId + ']' + jj.siteName + '</td></tr>';
        });
        $('#parkSiteTbody').html(str);
        $('#std_parkSiteTbody').html(str);
      })["catch"](function (err) {
      disLoad();
      console.log(err);
    });
    } else {
      //当不为园区时 选择为某个企业时
      var str = '<tr data-rtdbprojectid="' + siteObj.rtdbProjectId + '" data-id="' + siteObj.id + '" data-type="' + siteObj.type + '"><td>[' + siteObj.id + ']' + siteObj.title + '</td></tr>';
      $('#parkSiteTbody').html(str);
      $('#std_parkSiteTbody').html(str);
      objIdMeter = siteObj.id;
      objTypeMeter = siteObj.type;
      siteRtdbId = siteObj.rtdbProjectId; //弹窗 非标煤 企业选中的

      getMeterTable();
      hideCompanySelect();
      $('.obj_nav-item').removeClass('obj_nav-this');
      $('.obj_nav-item:nth-child(2)').addClass('obj_nav-this');
      $('#parkSiteTableContent').addClass('layui-hide');
      $('#meterTableContent,.meter_btnGroup,.meterTap').removeClass('layui-hide');
      stdObjId = siteObj.id;
      stdObjType = siteObj.type;
      stdSiteRtdbId = siteObj.rtdbProjectId; //弹窗 标煤 企业选中的

      getStdMeterTable();
      $('.std_nav-item').removeClass('std_nav-this');
      $('.std_nav-item:nth-child(2)').addClass('std_nav-this');
      $('#std_parkSiteTableContent,#std_paramTableContent,.param_btnGroup,.paramTap').addClass('layui-hide');
      $('#std_meterTableContent').removeClass('layui-hide');
    }
  } //非标煤 根据所选企业获取仪表数据


  function getMeterTable() {
    var val1 = tree1 ? tree1.getSelectedNodes() : [];

    if (siteObj && val1.length > 0) {
      load();
      request.service({
        method: 'post',
        url: '/meter/getCurrentSiteByEnergyTypeId',
        data: {
          "objType": objTypeMeter,
          "objId": objIdMeter,
          "energyTypeId": val1[0].energyTypeId,
          "key": $("#obj_meterName").val()
        }
      }).then(function (res) {
        disLoad();
        var str = '';
        $.each(res.data, function (m, n) {
          str += '<tr data-id="' + n.meterId + '"><td ><input type="checkbox" name="meterId[' + n.meterId + ']" value="' + n.meterId + '" title="[' + n.meterId + ']' + n.meterName + '" lay-skin="primary"></td></tr>';
        });
        $('#meterTbody').html(str);
        form.render('checkbox', 'meter_list');
      })["catch"](function (err) {
      disLoad();
      console.log(err);
    });
    } else {
      $('#meterTbody').html('');
    }
  } //标煤 根据所选企业获取仪表数据


  function getStdMeterTable() {
    var val1 = tree1 ? tree1.getSelectedNodes() : [];

    if (siteObj && val1.length > 0) {
      load();
      request.service({
        method: 'post',
        url: '/meter/getAllCurrentSite',
        data: {
          "objType": stdObjType,
          "objId": stdObjId,
          "key": $("#std_meterName").val()
        }
      }).then(function (res) {
        disLoad();
        var str = '';
        $.each(res.data, function (m, n) {
          str += '<tr data-id="' + n.meterId + '" data-energyTypeId="' + n.energyTypeId + '"><td >[' + n.energyTypeName + '][' + n.meterId + ']' + n.meterName + '</td></tr>';
        });
        $('#std_meterTbody').html(str);
      })["catch"](function (err) {
      disLoad();
      console.log(err);
    });
    } else {
      $('#std_meterTbody').html('');
    }
  } //标煤 根据所选仪表获取参数数据


  function getStdParamTable() {
    load();
    request.service({
      method: 'post',
      url: '/common/queryEnergyParaIdOrEnergyParaName',
      data: {
        'energyTypeId': stdEnergyTypeId,
        "key": $("#std_paramName").val()
      }
    }).then(function (res) {
      disLoad();
      var str = '';
      $.each(res.data, function (m, n) {
        str += '<tr data-id="' + n.energyParaId + '"><td ><input type="checkbox" name="meterId[' + n.energyParaId + ']" value="' + n.energyParaId + '" title="[' + n.energyParaId + ']' + n.energyParaName + '" lay-skin="primary"></td></tr>';
      });
      $('#std_paramTbody').html(str);
      form.render('checkbox', 'std_paramTbody');
    })["catch"](function (err) {
      disLoad();
      console.log(err);
    });
  } //标煤 获取标煤参数下拉框数据


  function getStdCoalSelectData() {
    load();
    request.service({
      method: 'get',
      url: '/common/queryParameter/std_coal'
    }).then(function (res) {
      disLoad();
      energyParaData = res.data;
      var str = '';
      $.each(res.data, function (m, n) {
        str += '<option value="' + n.energyParaId + '">[' + n.energyParaId + ']' + n.energyParaName + '</option>';
      });
      $('#std_coal_param').html(str);
    })["catch"](function (err) {
      disLoad();
      console.log(err);
    });
  } //新增修改 弹窗 父节点


  function renderSelect(data) {
    parentSelect = xmSelect.render({
      el: '#treeDetail_parentNode',
      radio: true,
      clickClose: true,
      prop: {
        name: 'nodeName',
        value: 'nodeId'
      },
      height: 'auto',
      content: '<div id="ele1" lay-filter="ele1"></div>'
    }); //渲染自定义内容

    tree.render({
      elem: '#ele1' //绑定元素
      ,
      data: data,
      id: 'ele1Id',
      showLine: false //是否开启连接线
      ,
      onlyIconControl: true,
      click: function click(obj) {
        // console.log(obj.data); //得到当前点击的节点数据
        parentSelect.closed();
        parentSelect.update({
          radio: true,
          max: 1,
          clickClose: true,
          prop: {
            name: 'nodeName',
            value: 'nodeId'
          },
          data: [{
            "nodeName": obj.data.title,
            "nodeId": obj.data.nodeId
          }]
        });
        parentSelect.setValue([obj.data]);
      }
    });
  } // 删除展示结构树节点明细


  function deleteTreeDetail() {
    layer.open({
      type: 1,
      title: '删除展示结构树节点',
      closeBtn: 1,
      shade: 0,
      anim: 1,
      maxmin: true,
      //开启最大化最小化按钮
      area: ['400px', '350px'],
      content: $('#treeDetail_delete'),
      btn: ['确定', '取消'],
      success: function success() {
        $("#treeDetail_delete").removeClass('layui-hide');
      },
      yes: function yes(index) {
        var val = tree1.getSelectedNodes();
        var radioSelect = treeGrid.radioStatus('treeDetail_table');
        load();
        var data = {
          "objType": siteObj.type,
          "objId": siteObj.id,
          "orgTreeId": val[0].orgTreeId,
          "nodeId": radioSelect.nodeId
        };
        request.service({
          method: 'post',
          url: '/tree/deleteOrgTreeDetail',
          data: data
        }).then(function (res) {
          disLoad();
          getTreeDetailTable();
          layer.close(index);
        })["catch"](function (err) {
      disLoad();
      console.log(err);
    });
      },
      end: function end(index) {
        $("#treeDetail_delete").addClass('layui-hide');
      }
    });
  } // 导入展示结构树节点明细


  function importTreeDetail() {
    layer.open({
      type: 1,
      title: '导入展示结构树节点',
      closeBtn: 1,
      shade: 0,
      anim: 1,
      maxmin: true,
      //开启最大化最小化按钮
      area: ['550px', '350px'],
      content: $('#treeDetail_import'),
      btn: ['确定', '取消'],
      success: function success() {
        $("#treeDetail_import").removeClass('layui-hide');
      },
      yes: function yes(index) {
        var val = tree1.getSelectedNodes();
        var formData = new FormData($("#treeDetail_import .layui-form")[0]);
        var file = formData.get('file');

        if (file.size == 0) {
          return layer.msg('请上传文件！');
        }

        formData.append("objType", siteObj.type);
        formData.append("objId", siteObj.id);
        formData.append("orgTreeId", val[0].orgTreeId);
        load();
        request.service({
          method: 'post',
          url: '/tree/importExcel',
          data: formData
        }).then(function (res) {
          disLoad();
          getTreeDetailTable();
          layer.close(index);
        })["catch"](function (err) {
      disLoad();
      console.log(err);
    });
      },
      end: function end(index) {
        $("#treeDetail_import").addClass('layui-hide');
      }
    });
  } // 获取能源种类


  function getEnergyType() {
    load();
    request.service({
      method: 'get',
      url: '/common/queryEnergyTypesAll'
    }).then(function (res) {
      disLoad();
      var str = '';
      $.each(res.data, function (ii, vv) {
        str += '<option value="' + vv.energyTypeId + '" >' + vv.energyTypeName + '</option>';
      });
      $("#tree_energyTypeId").html(str);
      form.render('select');
    })["catch"](function (err) {
      disLoad();
      console.log(err);
    });
  } //左侧结构树


  function getTree1() {
    var treeName = $("#search_treeName").val() ? $("#search_treeName").val() : null;

    if (siteObj) {
      load();
      request.service({
        method: 'post',
        url: '/tree/queryLeftNavigation',
        data: {
          'objType': siteObj.type,
          'objId': siteObj.id,
          'orgTreeName': treeName
        }
      }).then(function (res) {
        disLoad();

        if (res.data) {
          var list = res.data ? res.data : [];

          if (list.length > 0) {
            $.each(list, function (kk, jj) {
              jj.showName = '[' + jj.energyTypeName + '][' + jj.orgTreeId + ']' + jj.orgTreeName;
            });
          }

          var setting = {
            view: {
              addDiyDom: null,
              //用于在节点上固定显示用户自定义控件
              autoCancelSelected: false,
              dblClickExpand: true,
              //双击节点时，是否自动展开父节点的标识
              showLine: false,
              //设置 zTree 是否显示节点之间的连线。
              selectedMulti: false,
              fontCss: function fontCss(treeId, treeNode) {
                return treeNode.isUse == true ? {} : {
                  color: "#bbb"
                };
              },
              showIcon: false
            },
            data: {
              keep: {
                leaf: false,
                //zTree 的节点叶子节点属性锁，是否始终保持 isParent = false
                parent: false //zTree 的节点父节点属性锁，是否始终保持 isParent = true

              },
              key: {
                children: "children",
                //节点数据中保存子节点数据的属性名称。
                name: "showName",
                //节点数据保存节点名称的属性名称。
                title: "orgTreeId" //节点数据保存节点提示信息的属性名称

              },
              simpleData: {
                enable: true,
                //是否采用简单数据模式 (Array)
                idKey: "id",
                //节点数据中保存唯一标识的属性名称。
                pIdKey: "parentId",
                //节点数据中保存其父节点唯一标识的属性名称
                rootPId: "" //用于修正根节点父节点数据，即 pIdKey 指定的属性值。

              }
            },
            callback: {
              onClick: function onClick(event, treeId, treeNode) {
                getTreeDetailTable();
              }
            }
          };
          tree1 = $.fn.zTree.init($("#tree"), setting, list);
          tree1.expandAll(true);
          var nodes = tree1.getNodes();

          if (nodes.length > 0) {
            tree1.selectNode(nodes[0]);
            getTreeDetailTable();
          } else {
            $("#tree").html('');
          }
        }
      })["catch"](function (err) {
      disLoad();
      console.log(err);
    });
    }
  } // 右侧展示结构树明细表格


  function getTreeDetailTable() {
    var val1 = tree1.getSelectedNodes();

    if (siteObj && val1.length > 0) {
      load();
      request.service({
        method: 'post',
        url: '/tree/queryTreeInfoDetail',
        data: {
          'objType': siteObj.type,
          'objId': siteObj.id,
          'orgTreeId': val1[0].orgTreeId
        }
      }).then(function (res) {
        disLoad();

        if (res.data) {
          renderSelect(renderParentList(res.data));
          nodeTableData = [];
          renderNodeTableData(res.data);
          console.log(nodeTableData);
          treeGrid.render({
            id: 'treeDetail_table',
            elem: '#treeDetail_table',
            height: $('.treeDetail_table').height() - 20,
            idField: 'nodeId',
            cellMinWidth: 100,
            iconOpen: false //是否显示图标【默认显示】
            ,
            treeId: 'nodeId' //树形id字段名称
            ,
            treeUpId: 'parentId' //树形父id字段名称
            ,
            treeShowName: 'nodeName' //以树形式显示的字段
            ,
            isOpenDefault: true,
            cols: [[{
              type: "numbers",
              fixed: 'left',
              align: 'center'
            }, {
              type: "radio",
              fixed: "left"
            }, {
              field: 'nodeName',
              title: '节点名称',
              width: 260
            }, {
              field: 'nodeId',
              title: '节点ID',
              width: 80,
              align: 'center'
            }, {
              field: 'parentId',
              title: '父节点ID',
              width: 90,
              align: 'center'
            }, {
              field: 'sortId',
              title: '排序',
              width: 80,
              align: 'center'
            }, {
              field: 'dataSource',
              title: '数据源',
              width: 450,
              align: 'center'
            }, {
              field: 'memo',
              title: '备注',
              width: 150,
              align: 'center'
            }, {
              field: 'createUserId',
              title: '创建者',
              width: 110,
              align: 'center'
            }, {
              field: 'createTime',
              title: '创建时间',
              width: 160,
              align: 'center'
            }, {
              field: 'updateUserId',
              title: '修改者',
              width: 110,
              align: 'center'
            }, {
              field: 'updateTime',
              title: '修改时间',
              width: 160,
              align: 'center'
            }]],
            data: nodeTableData,
            page: false,
            limit: 999999
          });
        }
      })["catch"](function (err) {
      disLoad();
      console.log(err);
    });
    }
  }

  function renderParentList(arr) {
    $.each(arr, function (i, v) {
      v.title = '[' + v.nodeId + ']' + v.nodeName;
      v.name = '[' + v.nodeId + ']' + v.nodeName;
      v.value = v.nodeId;
      v.parentTitle = '[' + v.parentId + ']' + v.parentName;
      v.spread = true;

      if (v.children && v.children.length > 0) {
        renderParentList(v.children);
      }
    });
    return arr;
  }

  function renderNodeTableData(data) {
    $.each(data, function (i2, v2) {
      v2.parentTitle = '[' + v2.parentId + ']' + v2.parentName;
      v2.title = '[' + v2.nodeId + ']' + v2.nodeName;
      nodeTableData.push(v2);

      if (v2.children && v2.children.length > 0) {
        renderNodeTableData(v2.children);
      }
    });
  }

  function funcsAuthority() {
    getEnergyType();
    getStdCoalSelectData();
    getParkOrSite();
    getTree1();
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