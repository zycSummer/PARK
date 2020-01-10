

/*
 * @Author: xzl 
 * @Date: 2019-11-15 14:42:23 
 * @Last Modified by: xzl
 * @Last Modified time: 2019-12-30 15:22:31
 */
layui.use(['form', 'element', 'layer', 'table', 'jquery', 'laypage', 'tree'], function () {
  var form = layui.form,
      layer = layui.layer,
      $ = layui.jquery,
      table = layui.table,
      laypage = layui.laypage,
      tree = layui.tree,
      element = layui.element;
  var indexLoading;

  function load() {
    //加载事件
    indexLoading = layer.load(1, {
      shade: [0.3, '#fff']
    });
  }

  function disLoad() {
    //取消加载事件
    layer.close(indexLoading);
  } //查询点击事件


  $("#search_btn").on("click", function () {
    getParkDataTab();
  }); //初始函数

  function initFun() {
    getParkDataTab();
  }

  function getParkDataTab() {
    //ajax获取 数据
    var parkId = $("#search_parkId").val();
    var parkName = $("#search_parkName").val();
    load();
    request.service({
      method: 'post',
      url: '/park/queryPark',
      data: {
        'parkId': parkId,
        'parkName': parkName
      }
    }).then(function (res) {
      disLoad();
      var data = res.data;
      renderTableContent(data);
    })["catch"](function (err) {
      console.log(err);
    });
  } //渲染表格数据


  function renderTableContent(data) {
    table.render({
      elem: '#park_table',
      height: 'full-155',
      cols: [[{
        type: 'numbers',
        fixed: 'left'
      }, {
        type: 'radio',
        fixed: 'left'
      }, {
        field: 'parkId',
        align: "center",
        title: '园区标识',
        width: 120,
        fixed: 'left'
      }, {
        field: 'parkName',
        align: "center",
        title: '园区名称',
        width: 200
      }, {
        field: 'parkAbbrName',
        align: "center",
        title: '园区简称',
        width: 200
      }, {
        field: 'rtdbTenantId',
        align: "center",
        title: '实时库租户标识',
        width: 200
      }, {
        field: 'longitude',
        align: "center",
        title: '百度地图-中心坐标经度',
        width: 200
      }, {
        field: 'latitude',
        align: "center",
        title: '百度地图-中心坐标纬度',
        width: 200
      }, {
        field: 'scale',
        align: "center",
        title: '百度地图-默认缩放级别',
        width: 200
      }, {
        field: 'worldLongitude',
        align: "center",
        title: '世界地图-经度',
        width: 200
      }, {
        field: 'worldLatitude',
        align: "center",
        title: '世界地图-纬度',
        width: 200
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
      limit: 100
    });
  }

  $("#file").on("change", function (e) {
    var filePath = $(this).val(),
        //获取到input的value，里面是文件的路径
    fileFormat = filePath.substring(filePath.lastIndexOf(".")).toLowerCase(),
        src = window.URL.createObjectURL(this.files[0]); //转成可以在本地预览的格式
    // 检查是否是图片

    if (!fileFormat.match(/.png|.jpg|.jpeg/)) {
      layer.msg('上传错误,文件格式必须为：png/jpg/jpeg');
      return;
    }

    $('#imagePreview').attr('src', src);
  }); //将base64转换为文件

  function dataURLtoFile(dataurl, filename) {
    var arr = dataurl.split(',');
    var mime = arr[0].match(/:(.*?);/)[1];
    var bstr = atob(arr[1]);
    var n = bstr.length;
    var u8arr = new Uint8Array(n);

    while (n--) {
      u8arr[n] = bstr.charCodeAt(n);
    } //转换成file对象


    return new File([u8arr], filename, {
      type: mime
    }); //转换成成blob对象
    //return new Blob([u8arr],{type:mime});
  }

  $("#add_btn").on('click', function () {
    //新增点击事件
    request.service({
      method: 'get',
      url: '/park/isExistPark'
    }).then(function (res) {
      $('#addParkId').attr("disabled", false);
      $("#addParkId").val(''); //园区标识

      $("#addParkName").val(''); //园区名称

      $("#addRtdbTenantId").val(''); //实时库租户标识

      $("#addLongitude").val(''); //百度地图 中心经度

      $("#addLatitude").val(''); //百度地图 -中心经度

      $("#addScale").val(''); //百度地图 -缩放级别

      $("#addMemo").val(''); //memo

      $("#imagePreview").attr('src', '');
      $("#addWordLatitude").val(''); //世界地图 -中心经度

      $("#addWordLongitude").val(''); //世界地图 中心经度

      $("#addProfile").val('');
      $("#addParkAbbrName").val('');
      showModelIndexBox(null, '新增园区', null);
    })["catch"](function (err) {
      console.log(err);
    });
  });
  $("#edit_btn").on("click", function () {
    //修改点击事件
    var tableSelect = table.checkStatus('park_table').data;

    if (tableSelect.length == 0) {
      return layer.msg("请选择需要修改的园区");
    }

    load();
    request.service({
      method: 'get',
      url: '/park/queryParkById/' + tableSelect[0].id
    }).then(function (res) {
      disLoad();
      var tabObj = res.one;
      $('#addParkId').attr("disabled", true);
      $("#addParkId").val(tabObj.parkId); //园区标识

      $("#addParkName").val(tabObj.parkName); //园区名称

      $("#addRtdbTenantId").val(tabObj.rtdbTenantId); //实时库租户标识

      $("#addLongitude").val(tabObj.longitude); //百度地图 中心经度

      $("#addLatitude").val(tabObj.latitude); //百度地图 -中心经度

      $("#addScale").val(tabObj.scale); //百度地图 -缩放级别

      $("#addMemo").val(tabObj.memo); //memo

      $("#addWordLatitude").val(tabObj.worldLatitude); //世界地图 -中心经度

      $("#addWordLongitude").val(tabObj.worldLongitude); //世界地图 中心经度

      $("#addProfile").val(tabObj.profile);
      $("#addParkAbbrName").val(tabObj.parkAbbrName);
      $("#imagePreview").attr('src', 'data:image/png;base64,' + tabObj.img);
      $("#file").val('');
      showModelIndexBox(tableSelect[0].id, '编辑园区', tabObj);
    })["catch"](function (err) {
      disLoad();
      console.log(err);
    });
  });

  function showModelIndexBox(editId, title, editData) {
    //模态框调用事件
    layer.open({
      type: 1,
      title: title,
      closeBtn: 1,
      shade: 0.3,
      maxmin: true,
      anim: 1,
      area: ['950px', '820px'],
      content: $('#park_add'),
      btn: ['保存', '关闭'],
      success: function success() {
        $('#park_add').removeClass('layui-hide').addClass('layui-show');
      },
      yes: function yes(index) {
        var parkId = $("#addParkId").val(); //园区标识

        var parkName = $("#addParkName").val(); //园区名称

        var rtdbTenantId = $("#addRtdbTenantId").val(); //实时库租户标识

        var longitude = $("#addLongitude").val(); //百度地图 中心经度

        var latitude = $("#addLatitude").val(); //百度地图 -中心经度

        var longitude = $("#addLongitude").val(); //百度地图 中心经度

        var wordLatitude = $("#addWordLatitude").val(); //世界地图 -中心经度

        var wordLongitude = $("#addWordLongitude").val(); //世界地图 中心经度

        var parkAbbrName = $("#addParkAbbrName").val();
        var scale = $("#addScale").val(); //百度地图 -缩放级别

        var memo = $("#addMemo").val(); //memo

        if (!parkId) {
          return layer.msg('请输入园区标识！');
        }

        if (!parkName) {
          return layer.msg('请输入园区名称！');
        }

        if (!parkAbbrName) {
          return layer.msg('请输入园区简称！');
        }

        var formData = new FormData($("#siteImage.layui-form")[0]);
        var file = formData.get('file');
        formData.append("parkId", parkId);
        formData.append("parkName", parkName);
        formData.append("rtdbTenantId", rtdbTenantId);
        formData.append("longitude", longitude);
        formData.append("latitude", latitude);
        formData.append("worldLatitude", wordLatitude);
        formData.append("worldLongitude", wordLongitude);
        formData.append("scale", scale);
        formData.append("parkAbbrName", parkAbbrName);
        formData.append("memo", memo);
        formData.append("profile", $("#addProfile").val());
        var url = '/park/add';

        if (editId) {
          formData.append("id", editId);
          url = '/park/edit';

          if (file.size == 0 && editData.img) {
            formData.set("file", dataURLtoFile('data:image/png;base64,' + editData.img, '123.png'));
          }

          if (file.size == 0 && !editData.img) {
            formData.set("file", null);
          }
        } else {
          if (file.size == 0) {
            formData.set('file', null);
          }
        }

        if (file.size > 500000) {
          return layer.msg('图片要求小于500k！');
        }

        load();
        request.service({
          method: 'post',
          url: url,
          data: formData
        }).then(function (res) {
          disLoad();
          layer.close(index);
          getParkDataTab();
        })["catch"](function (err) {
          console.log(err);
        });
      },
      end: function end(index) {
        // 模态框关闭事件
        $('#park_add').removeClass('layui-show').addClass('layui-hide');
      }
    });
  }

  $('#del_btn').click(function () {
    //删除点击事件
    var tableSelect = table.checkStatus('park_table').data;

    if (tableSelect.length == 0) {
      return layer.msg("请选择需要删除的园区");
    }

    layer.open({
      type: 1,
      title: "删除园区",
      shade: 0.3,
      btn: ['确定', '取消'],
      area: ['320px', '300px'],
      //宽高
      content: '<div id="park_select_tree"> <form class="layui-form" style="margin:30px;">' + '   <div class="layui-form-item "><label>园区标识：</label>' + '<div class="layui-inline">' + tableSelect[0].parkId + '</div></div>' + '   <div class="layui-form-item "><label>园区名称：</label>' + '<div class="layui-inline" >' + tableSelect[0].parkName + '</div></div>' + '   <div class="layui-form-item " ><span style="color:red;">确定要删除此园区吗？</span>   <i class="fa fa-exclamation-circle tip_icon  " id="deleteParkTip"></i></div>' + '</form></div>',
      success: function success() {},
      yes: function yes(index) {
        load();
        request.service({
          method: 'get',
          url: '/park/delete/' + tableSelect[0].parkId
        }).then(function (res) {
          disLoad();
          layer.close(index);
          getParkDataTab();
        })["catch"](function (err) {
          console.log(err);
        });
      },
      end: function end(index) {
        // 模态框关闭事件
        layer.close(index);
      }
    });
  }); //提示弹框点击-园区标识

  $("#addParkIdTip").on("click", function () {
    layer.tips('字母、数字、下划线组合，长度不超过20 ', '#addParkIdTip');
  }); //提示弹框点击 

  $("#addParkNameTip").on("click", function () {
    layer.tips('长度不超过30', '#addParkNameTip');
  }); //提示弹框点击 

  $("#addParkAbbrNameTip").on("click", function () {
    layer.tips('长度不超过15', '#addParkAbbrNameTip');
  });
  $("#addRtdbTenantIdTip").on("click", function () {
    layer.tips('此园区在实时库中对应的租户标识， 字母、数字、下划线组合  长度不超过20', '#addRtdbTenantIdTip');
  });
  $("#addParkMapTip").on("click", function () {
    layer.tips('在能耗地图模块需要用到此处百度地图的相关配置', '#addParkMapTip');
  });
  $(document).on("click", '#deleteParkTip', function () {
    layer.tips('只删除园区信息表中的信息，不删除其他关联信息。', '#deleteParkTip');
  }); //新增企业弹框 提示点击 - 企业图片

  $("#upload_tip").on("click", function () {
    layer.tips('请上传jpg、png，大小限制在500K以内，尺寸建议200*200。', '#upload_tip');
  });
  $("#addDescTip").on("click", function () {
    layer.tips('长度不超过1000。', '#addDescTip');
  });
  $(".pickingCoordinates").on("click", function () {
    window.open("http://api.map.baidu.com/lbsapi/getpoint/index.html", "_blank");
  });
  initFun(); //页面加载时执行初始函数
});