

/*
 * @Author: xzl 
 * @Date: 2019-11-22 10:48:29 
 * @Last Modified by: xzl
 * @Last Modified time: 2019-12-19 13:26:09
 */
layui.use(['form', 'element', 'layer', 'table', 'jquery', 'laypage'], function () {
  var form = layui.form,
      layer = layui.layer,
      $ = layui.jquery,
      table = layui.table,
      laypage = layui.laypage,
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
    getEnergyTypeData();
  }); //初始函数

  function initFun() {
    getEnergyTypeData();
  }

  function getEnergyTypeData() {
    //ajax获取 数据  
    load();
    var formData = {};
    formData.energyTypeId = $("#add_energyTypeId").val();
    formData.energyTypeNameLike = $("#add_energyTypeNameLike").val();
    request.service({
      method: 'post',
      url: '/sysEnergyType/query',
      data: formData
    }).then(function (res) {
      disLoad();

      if (res.data.length > 0) {
        res.data[0].LAY_CHECKED = true;
        getParaData(res.data[0].energyTypeId);
      }

      renderEnergyTab(res.data);
    })["catch"](function (err) {
      console.log(err);
    });
  }

  function renderEnergyTab(data) {
    table.render({
      elem: '#energyTypeTable',
      height: 'full-95',
      cols: [[{
        type: 'numbers',
        fixed: 'left'
      }, {
        type: 'radio',
        fixed: 'left'
      }, {
        field: 'energyTypeId',
        align: "center",
        width: 120,
        title: '能源种类标识',
        fixed: 'left'
      }, {
        field: 'energyTypeName',
        align: "center",
        width: 120,
        title: '能源种类名称'
      }, {
        field: 'stdCoalCoeff',
        align: "center",
        width: 100,
        title: '折标系数'
      }, {
        field: 'co2Coeff',
        align: "center",
        width: 100,
        title: '碳排系数'
      }, {
        field: 'energyUsageParaId',
        align: "center",
        width: 140,
        title: '用量参数'
      }, {
        field: 'energyLoadParaId',
        align: "center",
        width: 140,
        title: '负荷参数'
      }, {
        field: 'sortId',
        align: "center",
        width: 90,
        title: '排序标识'
      }, {
        field: 'memo',
        align: "center",
        width: 150,
        title: '备注'
      }, {
        field: 'createUserId',
        align: "center",
        width: 110,
        title: '创建者'
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
        title: '修改时间',
        width: 160
      }]],
      data: data,
      page: false,
      limit: 1000
    });
  }

  $("#add_btn_type").on("click", function () {
    //能源种类新增点击事件
    $('#addEnergyTypeId').attr("disabled", false);
    $("#addEnergyTypeId").val(''); // 能源种类标识

    $("#addEnergyTypeName").val(''); // 能源种类名称

    $("#addStdCoalCoeff").val(''); // 折标系数

    $("#addCo2Coeff").val(''); // 碳排系数

    $("#addEnergyUsageParaId").val(''); // 用量参数标识

    $("#addEnergyLoadParaId").val(''); // 负荷参数标识

    $("#addsortId").val(''); // 排序标识

    $("#addMemo").val(''); // 备注

    showModelIndexBox(null, '新增能源种类');
  });
  $("#edit_btn_type").on("click", function () {
    //修改能源种类
    var tableSelectType = table.checkStatus('energyTypeTable').data;

    if (tableSelectType.length === 0) {
      return layer.msg("请选择需要编辑的能源种类！");
    }

    $('#addEnergyTypeId').attr("disabled", true);
    $("#addEnergyTypeId").val(tableSelectType[0].energyTypeId); // 能源种类标识

    $("#addEnergyTypeName").val(tableSelectType[0].energyTypeName); // 能源种类名称

    $("#addStdCoalCoeff").val(tableSelectType[0].stdCoalCoeff); // 折标系数

    $("#addCo2Coeff").val(tableSelectType[0].co2Coeff); // 碳排系数

    $("#addEnergyUsageParaId").val(tableSelectType[0].energyUsageParaId); // 用量参数标识

    $("#addEnergyLoadParaId").val(tableSelectType[0].energyLoadParaId); // 负荷参数标识

    $("#addsortId").val(tableSelectType[0].sortId); // 排序标识

    $("#addMemo").val(tableSelectType[0].memo); // 备注

    showModelIndexBox(tableSelectType[0].id, '新增能源种类');
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
      area: ['600px', '700px'],
      content: $('#menu_add'),
      btn: ['保存', '关闭'],
      success: function success() {
        $('#menu_add').removeClass('layui-hide').addClass('layui-show');
      },
      yes: function yes(index) {
        var energyTypeId = $("#addEnergyTypeId").val(); // 能源种类标识

        var energyTypeName = $("#addEnergyTypeName").val(); // 能源种类名称

        var stdCoalCoeff = $("#addStdCoalCoeff").val(); // 折标系数

        var co2Coeff = $("#addCo2Coeff").val(); // 碳排系数

        var energyUsageParaId = $("#addEnergyUsageParaId").val(); // 用量参数标识

        var energyLoadParaId = $("#addEnergyLoadParaId").val(); // 负荷参数标识

        var sortId = $("#addsortId").val(); // 排序标识

        var memo = $("#addMemo").val(); // 备注

        if (!energyTypeId) {
          return layer.msg("请输入能源种类标识");
        }

        if (!energyTypeName) {
          return layer.msg("请输入能源种类名称");
        }

        if (!stdCoalCoeff) {
          return layer.msg("请输入折标系数");
        }

        if (!co2Coeff) {
          return layer.msg("请输入碳排系数");
        }

        if (!energyUsageParaId) {
          return layer.msg("请输入用量参数标识");
        }

        if (!energyLoadParaId) {
          return layer.msg("请输入负荷参数标识");
        }

        var formData = {};
        formData.energyTypeId = energyTypeId;
        formData.energyTypeName = energyTypeName;
        formData.stdCoalCoeff = stdCoalCoeff;
        formData.co2Coeff = co2Coeff;
        formData.energyUsageParaId = energyUsageParaId;
        formData.energyLoadParaId = energyLoadParaId;
        formData.sortId = sortId;
        formData.memo = memo;
        formData.id = editId;
        var url = '/sysEnergyType/add';

        if (editId) {
          url = '/sysEnergyType/edit';
        }

        load();
        request.service({
          method: 'post',
          url: url,
          data: formData
        }).then(function (res) {
          disLoad();
          layer.close(index);
          getEnergyTypeData();
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

  $('#del_btn_type').click(function () {
    //能源种类删除点击事件
    var tableSelect = table.checkStatus('energyTypeTable').data;

    if (tableSelect.length == 0) {
      return layer.msg('请选择需要删除的能源种类！');
    }

    layer.open({
      type: 1,
      title: "删除能源种类",
      shade: 0.3,
      btn: ['确定', '取消'],
      area: ['320px', '280px'],
      //宽高
      content: '<div id="park_select_tree"> <form class="layui-form" style="margin:30px;">' + '   <div class="layui-form-item "><label>能源种类标识：</label>' + '<div class="layui-inline">' + tableSelect[0].energyTypeId + '</div></div>' + '   <div class="layui-form-item "><label>能源种类名称：</label>' + '<div class="layui-inline" >' + tableSelect[0].energyTypeName + '</div></div>' + '   <div class="layui-form-item " style="color:red;">确定要删除此能源种类吗？此能源种类下的参数将一并删除！</div>' + '</form></div>',
      success: function success() {},
      yes: function yes(index) {
        load();
        request.service({
          method: 'get',
          url: '/sysEnergyType/delete/' + tableSelect[0].energyTypeId
        }).then(function (res) {
          disLoad();
          layer.close(index);
          getEnergyTypeData();
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
  table.on('radio(energyTypeTable)', function (obj) {
    //表格单选切换事件 
    getParaData(obj.data.energyTypeId);
  });
  $("#search_btn_para").on("click", function () {
    var tableSelect = table.checkStatus('energyTypeTable').data;

    if (tableSelect.length == 0) {
      return layer.msg('请选择能源种类！');
    }

    getParaData(tableSelect[0].energyTypeId);
  });

  function getParaData(energyTypeId) {
    //获取参数列表
    var formData = {};
    formData.energyTypeId = energyTypeId;
    formData.energyParaId = $("#search_energyParaId").val();
    ;
    formData.energyParaNameLike = $("#search_energyParaNameLike").val();
    load();
    request.service({
      method: 'post',
      url: '/sysEnergyPara/query',
      data: formData
    }).then(function (res) {
      disLoad();
      var data = res.data;
      renderEnergyParaTab(data);
    })["catch"](function (err) {
      console.log(err);
    });
  } //渲染功能表格数据


  function renderEnergyParaTab(data) {
    table.render({
      elem: '#paraTable',
      height: 'full-100',
      cols: [[{
        type: 'numbers',
        align: "center",
        fixed: "left"
      }, {
        type: 'radio',
        fixed: "left"
      }, {
        field: 'energyParaId',
        align: "center",
        title: '能源参数标识',
        width: 120,
        fixed: "left"
      }, {
        field: 'energyParaName',
        align: "center",
        title: '能源参数名称',
        width: 200
      }, {
        field: 'unit',
        align: "center",
        title: '计量单位',
        width: 90
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
      limit: 1000
    });
  }

  $("#add_btn_para").on('click', function () {
    //能源种类参数添加事件
    var tableSelect = table.checkStatus('energyTypeTable').data;

    if (tableSelect.length == 0) {
      return layer.msg('请选择能源种类！');
    }

    $('#paraEnergyParaId').attr("disabled", false);
    $("#paraEnergy").val('[' + tableSelect[0].energyTypeId + ']' + tableSelect[0].energyTypeName); //能源种类

    $("#paraEnergyParaId").val(''); //能源参数标识

    $("#paraEnergyParaName").val(''); //能源参数名称

    $("#paraUnit").val(''); //计量单位

    $("#addParaMemo").val(''); //备注

    showParaModel(null, '新增能源参数');
  });
  $("#edit_btn_para").on('click', function () {
    //按钮修改事件
    var tableSelectPara = table.checkStatus('paraTable').data;
    var tableSelect = table.checkStatus('energyTypeTable').data;

    if (tableSelectPara.length === 0) {
      return layer.msg("请选择需要编辑的能源参数！");
    }

    $('#paraEnergyParaId').attr("disabled", true);
    $("#paraEnergy").val('[' + tableSelect[0].energyTypeId + ']' + tableSelect[0].energyTypeName); //能源种类

    $("#paraEnergyParaId").val(tableSelectPara[0].energyParaId); //能源参数标识

    $("#paraEnergyParaName").val(tableSelectPara[0].energyParaName); //能源参数名称

    $("#paraUnit").val(tableSelectPara[0].unit); //计量单位

    $("#addParaMemo").val(tableSelectPara[0].memo); //备注

    showParaModel(tableSelectPara[0].id, '修改能源参数');
  });

  function showParaModel(editId, title) {
    //模态框调用事件
    layer.open({
      type: 1,
      title: title,
      closeBtn: 1,
      shade: 0.3,
      maxmin: true,
      anim: 1,
      area: ['700px', '500px'],
      content: $('#para_add'),
      btn: ['保存', '关闭'],
      success: function success() {
        $('#para_add').removeClass('layui-hide').addClass('layui-show');
      },
      yes: function yes(index) {
        var tableSelect = table.checkStatus('energyTypeTable').data;
        var energyParaId = $("#paraEnergyParaId").val(); //能源参数标识

        var energyParaName = $("#paraEnergyParaName").val(); //能源参数名称

        var unit = $("#paraUnit").val(); //计量单位

        var memo = $("#addParaMemo").val(); //备注

        if (!energyParaId) {
          return layer.msg("请输入能源参数标识");
        }

        if (!energyParaName) {
          return layer.msg("请输入能源参数名称");
        }

        var formData = {};
        formData.energyParaId = energyParaId;
        formData.energyParaName = energyParaName;
        formData.unit = unit;
        formData.energyTypeId = tableSelect[0].energyTypeId;
        formData.memo = memo;
        formData.id = editId;
        var url = '/sysEnergyPara/add';

        if (editId) {
          url = '/sysEnergyPara/edit';
        }

        load();
        request.service({
          method: 'post',
          url: url,
          data: formData
        }).then(function (res) {
          disLoad();
          layer.close(index);
          getParaData(tableSelect[0].energyTypeId);
        })["catch"](function (err) {
          console.log(err);
        });
      },
      end: function end(index) {
        // 模态框关闭事件
        $('#para_add').removeClass('layui-show').addClass('layui-hide');
      }
    });
  }

  $('#del_btn_para').click(function () {
    //能源参数删除事件
    var tableSelectPara = table.checkStatus('paraTable').data;
    var tableSelect = table.checkStatus('energyTypeTable').data;

    if (tableSelectPara.length == 0) {
      return layer.msg('请选择需要删除的能源参数！');
    }

    layer.open({
      type: 1,
      title: "删除能源参数",
      shade: 0.3,
      btn: ['确定', '取消'],
      area: ['320px', '300px'],
      //宽高
      content: '<div id="park_select_tree"> <form class="layui-form" style="margin:30px;">' + '   <div class="layui-form-item "><label>能源种类：</label>' + '<div class="layui-inline">[' + tableSelect[0].energyTypeId + ']' + tableSelect[0].energyTypeName + '</div></div>' + '   <div class="layui-form-item "><label>能源参数标识：</label>' + '<div class="layui-inline">' + tableSelectPara[0].energyParaId + '</div></div>' + '   <div class="layui-form-item "><label>能源参数名称：</label>' + '<div class="layui-inline" >' + tableSelectPara[0].energyParaName + '</div></div>' + '   <div class="layui-form-item " style="color:red;">确定要删除此能源种参数吗？</div>' + '</form></div>',
      success: function success() {},
      yes: function yes(index) {
        load();
        request.service({
          method: 'get',
          url: '/sysEnergyPara/delete/' + tableSelectPara[0].energyParaId
        }).then(function (res) {
          disLoad();
          layer.close(index);
          getParaData(tableSelect[0].energyTypeId);
        })["catch"](function (err) {
          console.log(err);
        });
      },
      end: function end(index) {
        // 模态框关闭事件
        layer.close(index);
      }
    });
  }); //提示弹框点击

  $("#add_energyTypeId_tip").on("click", function () {
    layer.tips('字母、数字、下划线组合，长度不超过20。', '#add_energyTypeId_tip');
  }); //提示弹框点击 

  $("#addEnergyTypeNameTip").on("click", function () {
    layer.tips('长度不超过20。', '#addEnergyTypeNameTip');
  });
  $("#addStdCoalCoeffTip").on("click", function () {
    layer.tips('只能输入数字。', '#addStdCoalCoeffTip');
  });
  $("#addCo2CoeffTip").on("click", function () {
    layer.tips('只能输入数字。', '#addCo2CoeffTip');
  });
  $("#addEnergyUsageParaIdTip").on("click", function () {
    layer.tips('字母、数字、下划线组合，长度不超过20。', '#addCo2CoeffTip');
  });
  $("#addEnergyLoadParaIdTip").on("click", function () {
    layer.tips('字母、数字、下划线组合，长度不超过20。', '#addEnergyLoadParaIdTip');
  });
  $("#addSortIdTip").on("click", function () {
    layer.tips('字母、数字、下划线组合，长度不超过20。 系统按照字符串升序', '#addSortIdTip');
  });
  $("#paraEnergyParaIdTip").on("click", function () {
    layer.tips('字母、数字、下划线组合，长度不超过20。', '#paraEnergyParaIdTip');
  });
  $("#paraEnergyParaNameTip").on("click", function () {
    layer.tips('长度不超过20。', '#paraEnergyParaNameTip');
  });
  $("#paraUnitTip").on("click", function () {
    layer.tips('长度不超过15。', '#paraUnitTip');
  });
  initFun(); //页面加载时执行初始函数
});