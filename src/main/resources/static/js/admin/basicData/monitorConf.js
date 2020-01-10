

/*
 * @Author: jpp
 * @Date: 2019-10-22 17:07:34
 * @Last Modified by: xzl
 * @Last Modified time: 2019-12-19 11:04:34
 */
layui.use(['request', 'form', 'element', 'layer', 'jquery', 'tree'], function () {
  var form = layui.form,
      layer = layui.layer,
      tree = layui.tree,
      $ = layui.jquery,
      element = layui.element;
  var siteObj = JSON.parse(sessionStorage.getItem('parkId'));
  var tree1; //左侧具体节点

  var paramsSelect; //父级组态画面

  var chartData;
  var editor,
      name = '',
      isLoad = false; //图纸id，图纸name

  funcsAuthority(); //新增

  $('.add').on('click', function () {
    $('.add_htImgId').removeClass('layui-hide');
    $('.edit_htImgId').addClass('layui-hide');
    $("#htImgId").val('');
    $('#parentHtImgId').val('');
    var val = tree1.getSelectedNodes();

    if (val.length == 0) {
      paramsSelect.setValue([]);
    } else {
      paramsSelect.update({
        radio: true,
        clickClose: true,
        prop: {
          name: 'title',
          value: 'htImgId'
        },
        data: [{
          "title": val[0].title,
          "htImgId": val[0].htImgId
        }]
      });
      paramsSelect.setValue([val[0].htImgId]);
    }

    $("#htImgName").val('');
    $("#sortId").val('');
    $("#memo").val('');
    add('add', '新增组态画面');
  });
  $('.edit').on('click', function () {
    var val = tree1.getSelectedNodes();
    if (val.length == 0) return layer.msg('请选择组态画面节点');
    $('.edit_htImgId').removeClass('layui-hide');
    $('.add_htImgId').addClass('layui-hide');
    $('#edit_htImgId').html(val[0].htImgId);
    $('#parentHtImgId').val(val[0].parentId);

    if (val[0].parentId) {
      paramsSelect.update({
        radio: true,
        clickClose: true,
        prop: {
          name: 'title',
          value: 'htImgId'
        },
        data: [{
          "title": val[0].parentTitle,
          "htImgId": val[0].parentId
        }]
      });
      paramsSelect.setValue([val[0].parentId]);
    } else {
      paramsSelect.update({
        radio: true,
        clickClose: true,
        prop: {
          name: 'title',
          value: 'htImgId'
        },
        data: []
      });
      paramsSelect.setValue([]);
    }

    $("#htImgName").val(val[0].htImgName);
    $("#sortId").val(val[0].sortId);
    $("#memo").val(val[0].memo);
    add('edit', '修改组态画面');
  });
  $('.delete').on('click', function () {
    var val = tree1.getSelectedNodes();
    if (val.length == 0) return layer.msg('请选择要删除的组态画面节点');
    deletes();
  }); //弹框 提示点击 - 组态画面标识

  $("#htImgId_tip").on("click", function () {
    layer.tips('字母、数字、下划线组合，长度不超过20。', '#htImgId_tip');
  }); //弹框 提示点击 - 父级组态画面

  $("#parentHtImgId_tip").on("click", function () {
    layer.tips('如不选择父级组态画面，则新增的组态画面即为根节点。', '#parentHtImgId_tip');
  }); //弹框 提示点击 - 组态画面名称

  $("#htImgName_tip").on("click", function () {
    layer.tips('长度不超过30。', '#htImgName_tip');
  }); //弹框 提示点击 - 排序标识

  $("#sortId_tip").on("click", function () {
    layer.tips('字母、数字、下划线组合，长度不超过10，系统按照字符串升序。', '#sortId_tip');
  });
  /*----------------------------------函数------------------------------------*/

  function add(type, title) {
    layui.layer.open({
      type: 1,
      title: title,
      closeBtn: 1,
      shade: 0,
      anim: 1,
      maxmin: true,
      //开启最大化最小化按钮
      area: ['500px', '450px'],
      content: $('#addForm'),
      btn: ['保存', '取消'],
      success: function success() {
        $("#addForm").removeClass('layui-hide');
        form.render();
      },
      yes: function yes(index) {
        var selectData = paramsSelect.getValue();
        selectData = selectData.length > 0 ? selectData[0].htImgId : '';

        if (type == 'edit') {
          var val = tree1.getSelectedNodes();
          var data = {
            "objType": siteObj.type,
            "objId": siteObj.id,
            "id": val[0].id,
            "htImgId": val[0].htImgId,
            "htImgName": $("#htImgName").val(),
            "parentId": selectData,
            "sortId": $("#sortId").val(),
            "memo": $("#memo").val(),
            "cfgPic": ''
          };
        } else if (type == 'add') {
          var data = {
            "objType": siteObj.type,
            "objId": siteObj.id,
            "htImgId": $("#htImgId").val(),
            "htImgName": $("#htImgName").val(),
            "parentId": selectData,
            "sortId": $("#sortId").val(),
            "memo": $("#memo").val(),
            "cfgPic": ''
          };
        }

        if (data.htImgId == '') return layer.msg('组态画面标识不能为空！');
        if (data.htImgId.length > 20) return layer.msg('组态画面标识长度不能超过20！');
        var re = /^[0-9a-zA-Z]+$/g;

        if (!re.test(data.htImgId)) {
          return layer.msg('组态画面标识为字母、数字组合！');
        }

        if (data.htImgName == '') return layer.msg('组态画面名称不能为空！');
        if (data.htImgName.length > 30) return layer.msg('组态画面名称长度不能超过30！');
        if (data.sortId.length > 10) return layer.msg('排序标识长度不能超过10！');
        var re2 = /^[0-9a-zA-Z]+$/g;

        if (data.sortId.length > 0 && !re2.test(data.sortId)) {
          return layer.msg('排序标识为字母、数字组合！');
        }

        var url = type == 'edit' ? '/energyMonitoring/updateRightHtImg' : '/energyMonitoring/insertRightHtImg';
        load();
        request.service({
          method: 'post',
          url: url,
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
        $("#addForm").addClass('layui-hide');
      }
    });
  }

  function deletes() {
    var val = tree1.getSelectedNodes();
    layer.open({
      type: 1,
      title: '删除组态画面',
      closeBtn: 1,
      shade: 0,
      anim: 1,
      maxmin: true,
      //开启最大化最小化按钮
      area: ['400px', '250px'],
      content: $('#deleteForm'),
      btn: ['确定', '取消'],
      success: function success() {
        $("#deleteForm").removeClass('layui-hide');
        $("#de_htImgId").html(val[0].htImgId);
        $("#de_htImgName").html(val[0].htImgName);
        form.render();
      },
      yes: function yes(index) {
        load();
        request.service({
          method: 'post',
          url: '/energyMonitoring/deleteRightHtImg',
          data: {
            'objType': siteObj.type,
            'objId': siteObj.id,
            'htImgId': val[0].htImgId
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
        $("#deleteForm").addClass('layui-hide');
      }
    });
  }

  function getTree1() {
    if (siteObj) {
      load();
      request.service({
        method: 'post',
        url: '/energyMonitoring/queryLeftHtImg',
        data: {
          'objType': siteObj.type,
          'objId': siteObj.id
        }
      }).then(function (res) {
        disLoad();

        if (res.data) {
          var list = res.data ? renderList(res.data) : [];
          renderSelect(list);
          var setting = {
            view: {
              addDiyDom: null,
              //用于在节点上固定显示用户自定义控件
              autoCancelSelected: false,
              dblClickExpand: true,
              //双击节点时，是否自动展开父节点的标识
              showLine: true,
              //设置 zTree 是否显示节点之间的连线。
              selectedMulti: false
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
                name: "title",
                //节点数据保存节点名称的属性名称。
                title: "htImgId" //节点数据保存节点提示信息的属性名称

              },
              simpleData: {
                enable: true,
                //是否采用简单数据模式 (Array)
                idKey: "htImgId",
                //节点数据中保存唯一标识的属性名称。
                pIdKey: "parentId",
                //节点数据中保存其父节点唯一标识的属性名称
                rootPId: "" //用于修正根节点父节点数据，即 pIdKey 指定的属性值。

              }
            },
            callback: {
              onClick: function onClick(event, treeId, treeNode) {
                getChartData();
              }
            }
          };
          tree1 = $.fn.zTree.init($("#tree"), setting, list);
          tree1.expandAll(true);
          var nodes = tree1.getNodes();

          if (nodes.length > 0) {
            tree1.selectNode(nodes[0]);
            getChartData();
          } else {
            $("#tree").html('');
            editor.closeTab();
            return layer.msg('没有树结构');
          }
        }
      })["catch"](function (err) {
      disLoad();
      console.log(err);
    });
    }
  }

  function renderList(arr) {
    $.each(arr, function (i, v) {
      v.title = '[' + v.htImgId + ']' + v.htImgName;
      v.parentTitle = '[' + v.parentId + ']' + v.parentName;

      if (v.children && v.children.length > 0) {
        renderList(v.children);
      }
    });
    return arr;
  }

  function renderSelect(data) {
    paramsSelect = xmSelect.render({
      el: '#parentHtImgId',
      radio: true,
      clickClose: true,
      prop: {
        name: 'title',
        value: 'htImgId'
      },
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
        paramsSelect.closed();
        paramsSelect.update({
          radio: true,
          clickClose: true,
          prop: {
            name: 'title',
            value: 'htImgId'
          },
          data: [{
            "title": obj.data.title,
            "htImgId": obj.data.htImgId
          }]
        });
        paramsSelect.setValue([obj.data.htImgId]);
      }
    });
  }

  function getChartData() {
    var val1 = tree1.getSelectedNodes();

    if (siteObj) {
      load();
      request.service({
        method: 'post',
        url: '/htImg/queryRightHtImg',
        data: {
          'objType': siteObj.type,
          'objId': siteObj.id,
          'htImgId': val1[0].htImgId
        }
      }).then(function (res) {
        disLoad();

        if (res.one) {
          name = val1[0].htImgName;
          var data = null;

          if (res.one.cfgPic) {
            data = res.one.cfgPic;
            name = val1[0].htImgName;
          }

          chartData = res.one;
          isLoad = true;
          handleEditorCreated(data, name);
        }
      })["catch"](function (err) {
      disLoad();
      console.log(err);
    });
    }
  }

  function handleEditorCreated(data, name) {
    // addImageItem(editor.assets.tree, 'editor.assets.tree');
    // addImageItem(editor.assets.list, 'editor.assets.list');
    var mainTabView = editor.mainTabView,
        tabModel2 = mainTabView.getTabModel();

    if (data) {
      editor.openByJSON('display', '', name, data);
    } else {
      editor.newDisplayView(name); // 创建一个新图纸
    }

    editor.closeOtherTabs();
    tabModel2.each(function (data1) {
      data1._closable = false;
      data1.setTag(data1.getName());
    });
    editor.mainMenu.showOnView(editor.mainToolbar, 4, 20);
    editor.mainMenu.hide();
    editor.mainMenu.getItemById('newDisplayView').disabled = true;
    editor.assets.list.menu.setItemVisible('open', true);
    var leftSplitView = editor.leftSplitView,
        leftTopTabView = editor.leftTopTabView,
        tabModel = leftTopTabView.getTabModel(),
        displaysTab;
    tabModel.each(function (data) {
      if (data.getView() === editor.displays) {
        displaysTab = data;
      }
    });
    tabModel.remove(displaysTab);
    leftTopTabView.select(tabModel.getDatas().get(0)); // var view = editor.displayView.getView();
    // view.style.background = '#000';

    window.addEventListener('resize', function () {
      editor.mainPane.iv();
    });
  }

  function funcsAuthority() {
    var urls = hteditor_config.subConfigs || [];
    urls.push('client.js');
    ht.Default.loadJS(urls, function () {
      urls = ['locales/' + hteditor.config.locale + '.js', 'custom/locales/' + hteditor.config.locale + '.js'];
      urls.push(hteditor.init);

      if (hteditor.config.libs) {
        urls = urls.concat(hteditor.config.libs);
      }

      urls.push('vs/loader.js');
      urls.push('vs/editor/editor.main.nls.js');
      urls.push('vs/editor/editor.main.js');
      ht.Default.loadJS(urls, function () {
        window.editor = hteditor.createEditor({
          container: 'editor1',
          onEditorCreated: function onEditorCreated(editor1) {
            editor = editor1;
            getTree1(); //通过添加监听器拦截图纸、图标和组件的保存事件，该例子将数据存于内容

            editor.addEventListener(function (event) {
              var params = event.params;

              if (event.type === 'displayViewSaving') {
                params.preventDefault = true; // 阻止默认的文件存储过程

                params.displayView.dirty = false; // 清除图纸页签上标示图纸 dirty 的标志

                if (isLoad == true) {
                  var val1 = tree1.getSelectedNodes();
                  var data = {
                    "objType": siteObj.type,
                    "objId": siteObj.id,
                    "id": val1[0].id,
                    "htImgId": val1[0].htImgId,
                    "htImgName": chartData.htImgName,
                    "parentId": val1[0].parentId,
                    "sortId": chartData.sortId,
                    "filePath": chartData.filePath,
                    "cfgPic": editor.dm.serialize()
                  };
                  load();
                  request.service({
                    method: 'post',
                    url: '/energyMonitoring/updateRightHtImg',
                    data: data
                  }).then(function (res) {
                    disLoad();

                    if (res.one) {
                      getChartData();
                    }
                  })["catch"](function (err) {
      disLoad();
      console.log(err);
    });
                }
              }

              if (event.type === 'displayViewNewNameInputing') {
                params.name = name; // params.preventDefault = true;
              }
            });
          }
        });
      });
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