layui.use(['request', 'form', 'element', 'layer', 'jquery', 'laydate', 'table','laypage'], function () {
    var form = layui.form,
        layer = layui.layer,
        table = layui.table,
        $ = layui.jquery,
        laypage = layui.laypage,
        laydate = layui.laydate,
        element = layui.element;

    var siteObj = JSON.parse(sessionStorage.getItem('parkId'));
    var tree1; //左侧具体节点
    var nodeTableData = [];
    var parentSelect; //弹窗 父节点 单选
    var startTime = moment().add('year', -1).format('YYYY');
    var endTime = moment().add('year', 1).format('YYYY');

    function init() { //初始化界面
        laydate.render({ //渲染开始时间
            elem: '#search_start_time',
            value: startTime,
            type: 'year',
            done: function (value) {
                startTime = value
            }
        });
        laydate.render({ //渲染结束时间
            elem: "#search_end_time",
            value: endTime,
            type: 'year',
            done: function (value) {
                endTime = value
            }
        });
        laydate.render({ //渲染结束时间
            elem: "#rightYear",
            type: 'year',
            done: function (value) {
            }
        });
        getLeftData();

    }
    init();

    //左侧列表 查询
    $('.left_query').on('click', function () {
        getLeftData();
    });

    //左侧结构树
    function getLeftData() {
        var treeName = $("#search_treeName").val() ? $("#search_treeName").val() : null;
        if (siteObj) {
            load();
            request.service({
                method: 'post',
                url: '/benchmarkingObj/query',
                data: {
                    'objType': siteObj.type,
                    'objId': siteObj.id,
                    'benchmarkingObjName': treeName
                }
            }).then(function (res) {
                disLoad();
                if (res.data) {
                    var list = res.data ? res.data : [];
                    if (list.length > 0) {
                        for (var index = 0; index < list.length; index++) {
                            var obj = list[index];
                            switch (obj.benchmarkingObjType) {
                                case "Domestic":
                                    obj.benchmarkingObjTypename = '国内'
                                    break;
                                case "International":
                                    obj.benchmarkingObjTypename = '国外'
                                    break;
                            }
                            obj.title = '[' + obj.benchmarkingObjTypename + '][' + obj.benchmarkingObjId + ']' + obj.benchmarkingObjName;
                        }
                    }
                    var setting = {
                        view: {
                            addDiyDom: null, //用于在节点上固定显示用户自定义控件
                            autoCancelSelected: false,
                            dblClickExpand: true, //双击节点时，是否自动展开父节点的标识
                            showLine: false, //设置 zTree 是否显示节点之间的连线。
                            selectedMulti: false,

                            showIcon: false
                        },
                        data: {
                            keep: {
                                leaf: false, //zTree 的节点叶子节点属性锁，是否始终保持 isParent = false
                                parent: false //zTree 的节点父节点属性锁，是否始终保持 isParent = true
                            },
                            key: {
                                children: "children", //节点数据中保存子节点数据的属性名称。
                                name: "title", //节点数据保存节点名称的属性名称。
                                title: "orgTreeId" //节点数据保存节点提示信息的属性名称
                            },
                            simpleData: {
                                enable: true, //是否采用简单数据模式 (Array)
                                idKey: "id", //节点数据中保存唯一标识的属性名称。
                                pIdKey: "parentId", //节点数据中保存其父节点唯一标识的属性名称
                                rootPId: "" //用于修正根节点父节点数据，即 pIdKey 指定的属性值。
                            }
                        },
                        callback: {
                            onClick: function (event, treeId, treeNode) {
                                getRightData(treeNode.benchmarkingObjId)
                            }
                        }
                    };
                    tree1 = $.fn.zTree.init($("#tree"), setting, list);
                    tree1.expandAll(true);
                    var nodes = tree1.getNodes();
                    if (nodes.length > 0) {
                        tree1.selectNode(nodes[0]);
                        getRightData(nodes[0].benchmarkingObjId)
                    } else {
                        $("#tree").html('');
                    }
                }
            }).catch(function (err) {
                disLoad();
                console.log(err)
            });
        }
    }
    //左侧 新增点击事件
    $('#leftAdd').on('click', function () {
        $("#benchmarkingObjId").attr("disabled", false)
        $("#benchmarkingObjId").val('');
        $("#benchmarkingObjName").val('');
        $("#benchmarkingObjType").val('');
        $("#tree_sortId").val("");
        $("#tree_memo").val('');
        showLeftModel(null, '新增对标对象');
    });
    //左侧 修改展示结构树
    $('#leftEdit').on('click', function () {
        var val = tree1.getSelectedNodes();
        if (val.length == 0) {
            return layer.msg("请选择需要修改的对标对象")
        }
        $("#benchmarkingObjId").attr("disabled", true)
        request.service({
            method: 'get',
            url: "/benchmarkingObj/queryById/" + val[0].id,
        }).then(function (res) {
            disLoad();
            var editObj = res.one;

            $("#benchmarkingObjId").val(editObj.benchmarkingObjId);
            $("#benchmarkingObjName").val(editObj.benchmarkingObjName);
            $("#benchmarkingObjType").val(editObj.benchmarkingObjType);
            $("#tree_sortId").val(editObj.sortId);
            $("#tree_memo").val(editObj.memo);
            form.render();
            showLeftModel(editObj.id, '修改对标对象');
        }).catch(function (err) {
            console.log(err)
        });



    });

    // 新增修改对标对象
    function showLeftModel(editId, title) {
        layui.layer.open({
            type: 1,
            title: title,
            closeBtn: 1,
            shade: 0.3,
            maxmin: true,
            anim: 1,
            area: ['550px', '500px'],
            content: $('#tree_add'),
            btn: ['保存', '取消'],
            success: function () {
                $("#tree_add").removeClass('layui-hide');
            },
            yes: function (index) {
                var benchmarkingObjId = $("#benchmarkingObjId").val(); //对标对象标识
                var benchmarkingObjName = $("#benchmarkingObjName").val(); //对标对象名称
                var benchmarkingObjType = $("#benchmarkingObjType").val(); //对标对象类型
                var sortId = $("#treeSortId").val();
                var memo = $("#tree_memo").val();
                if (!benchmarkingObjId) {
                    return layer.msg("请输入对标对象标识")
                }
                if (!benchmarkingObjName) {
                    return layer.msg("请输入对标对象名称")
                }
                if (!benchmarkingObjType) {
                    return layer.msg("请输入对标对象类型")
                }

                var formData = {};
                formData.benchmarkingObjId = benchmarkingObjId;
                formData.benchmarkingObjName = benchmarkingObjName;
                formData.benchmarkingObjType = benchmarkingObjType;
                formData.sortId = sortId;
                formData.memo = memo;
                formData.id = editId;
                formData.objType = siteObj.type;
                formData.objId = siteObj.id;
                var url = '/benchmarkingObj/add'
                if (editId) {
                    url = '/benchmarkingObj/edit'
                }
                load();
                request.service({
                    method: 'post',
                    url: url,
                    data: formData
                }).then(function (res) {
                    disLoad();
                    getLeftData();
                    layer.close(index);
                }).catch(function (err) {
                    disLoad();
                    console.log(err)
                });
            },
            end: function (index) {
                $("#tree_add").addClass('layui-hide');
            }
        });
    }

    $('#leftDelete').click(function () { //左侧对标对象删除点击事件
        var val = tree1.getSelectedNodes();
        if (val.length == 0) {
            return layer.msg("请选择需要删除的对标对象")
        }
        var benchmarkingObjTypename;
        switch (val[0].benchmarkingObjType) {
            case "Domestic":
                benchmarkingObjTypename = '国内'
                break;
            case "International":
                benchmarkingObjTypename = '国外'
                break;
        }
        layer.open({
            type: 1,
            title: "删除对标对象",
            shade: 0.3,
            btn: ['确定', '取消'],
            area: ['320px', '350px'], //宽高
            content: '<div id="park_select_tree"> <form class="layui-form" style="margin:30px;">' +
                '   <div class="layui-form-item "><label>对标对象标识：</label>' +
                '<div class="layui-inline">' + val[0].benchmarkingObjId + '</div></div>' +
                '   <div class="layui-form-item "><label>对标对象名称：</label>' +
                '<div class="layui-inline">' + val[0].benchmarkingObjName + '</div></div>' +
                '   <div class="layui-form-item "><label>对标对象类型：</label>' +
                '<div class="layui-inline">' + benchmarkingObjTypename + '</div></div>' +
                '   <div class="layui-form-item " style="color:red;">确定要删除此对标对象吗？此对标对象的指标数据将一并删除!</div>' +
                '</form></div>',
            success: function () {

            },
            yes: function (index) {
                var formData = {};
                formData.benchmarkingObjId = val[0].benchmarkingObjId;
                formData.objType = siteObj.type;
                formData.objId = siteObj.id;
                load();
                request.service({
                        method: 'post',
                        url: '/benchmarkingObj/delete',
                        data: formData
                    })
                    .then(function (res) {
                        disLoad();
                        layer.close(index);
                        getLeftData();
                    })
                    .catch(function (err) {
                        console.log(err)
                    })
            },
            end: function (index) { // 模态框关闭事件
                layer.close(index);
            }
        })
    });


    $("#rightQuery").click(function () {
        var val = tree1.getSelectedNodes();
        if (val.length == 0) {
            return layer.msg("请选择对标对象")
        }
        getRightData(val[0].benchmarkingObjId)
    })

    var pageNum = 1;
    var pageLimit;

    function getRightData(benchmarkingObjId) { //ajax获取 数据
        if (startTime && endTime) {
            if (startTime > endTime) {
                return layer.msg("结束时间不能大于开始时间")
            }
        }
        var tnum = parseInt(($(".tab_content").height() - 60) / 40); //动态生成表格展示条数
        var formData = {};
        formData.limit = pageLimit ? pageLimit : tnum;
        formData.page = pageNum;
        var key = {};
        key.start = startTime;
        key.end = endTime;
        key.objType = siteObj.type;
        key.objId = siteObj.id;
        key.benchmarkingObjId = benchmarkingObjId;
        formData.key = key;
        load();
        request.service({
                method: 'post',
                url: '/benchmarkingObjData/query',
                data: formData
            })
            .then(function (res) {
                disLoad();
                var data = res.data;
                renderTableContent(data, formData.limit);
                renderPage(res.count, tnum);
            })
            .catch(err => {
                console.log(err)
            })

    }

    //渲染表格数据
    function renderTableContent(data, limit) {
        table.render({
            elem: '#role_table',
            height: 'full-155',
            cols: [
                [{
                        type: "radio",
                        width: 50,
                        fixed: "left"
                    }, {
                        type: 'numbers',
                        align: "center",
                        title: '序号'
                    },
                    {
                        field: 'year',
                        align: "center",
                        title: '年',
                        width: 100
                    },
                    {
                        field: 'gdpElectricity',
                        align: "center",
                        title: '万元GDP电耗（kWh/万元）',
                        width: 200

                    },
                    {
                        field: 'gdpWater',
                        align: "center",
                        title: '万元GDP水耗（t/万元）',
                        width: 200

                    },
                    {
                        field: 'gdpStdCoal',
                        align: "center",
                        title: '万元GDP能耗（tce/万元）',
                        width: 200

                    },
                    {
                        field: 'addValueStdCoal',
                        align: "center",
                        title: '万元工业增加值能耗（kWh/万元）',
                        width: 260

                    },
                    {
                        field: 'memo',
                        align: "center",
                        title: '备注'
                    }, {
                        field: 'createUserId',
                        align: "center",
                        title: '创建者',
                        width: 135

                    }, {
                        field: 'createTime',
                        align: "center",
                        title: '创建时间',
                        width: 135

                    }, {
                        field: 'updateUserId',
                        align: "center",
                        title: '修改者',
                        width: 135

                    }, {
                        field: 'updateTime',
                        align: "center",
                        title: '修改时间',
                        width: 135

                    }
                ]
            ],
            data: data,
            page: false,
            limit: limit
        })

    }
    //渲染分页模块
    function renderPage(count, tnum) {
        laypage.render({
            elem: 'table_page',
            count: count, //数据总数，从服务端得到
            curr: pageNum,
            limit: pageLimit ? pageLimit : tnum,
            limits: [tnum, 20, 30, 50],
            layout: ['count', 'prev', 'page', 'next', 'limit', 'skip'],
            jump: function (obj, first) {
                //首次不执行
                if (!first) {
                    pageNum = obj.curr;
                    pageLimit = obj.limit;
                    getRoleTabData();
                }
            }
        });

    }

      //对标对象数据新增点击事件
      $('#rightAdd').on('click', function () {
        var val = tree1.getSelectedNodes();
        if (val.length == 0) {
            return layer.msg("请选择左侧对标对象")
        }
        var  objData =val[0]
        switch (objData.benchmarkingObjType) {
            case "Domestic":
                objData.benchmarkingObjTypename = '国内'
                break;
            case "International":
                objData.benchmarkingObjTypename = '国外'
                break;
        }
       var objTitle = '[' + objData.benchmarkingObjTypename + '][' + objData.benchmarkingObjId + ']' + objData.benchmarkingObjName;
        $("#rightObjName").val(objTitle);
        $("#rightYear").val("");
        $("#gdpElectricity").val("");
        $("#gdpWater").val("");
        $("#gdpStdCoal").val("");
        $("#addValueStdCoal").val("");
        $("#right_memo").val("");
        showRightModel(null, '新增对标对象指标数据');
    });
    //对标对象数据修改
    $('#rightEdit').on('click', function () {
        var tableSelect = table.checkStatus('role_table').data;
        if (tableSelect.length === 0) {
            return layer.msg("请选择需要修改的指标数据！")
        }
        var val = tree1.getSelectedNodes();
        if (val.length == 0) {
            return layer.msg("请选择左侧对标对象")
        }
        var  objData =val[0]
        switch (objData.benchmarkingObjType) {
            case "Domestic":
                objData.benchmarkingObjTypename = '国内'
                break;
            case "International":
                objData.benchmarkingObjTypename = '国外'
                break;
        }
       var objTitle = '[' + objData.benchmarkingObjTypename + '][' + objData.benchmarkingObjId + ']' + objData.benchmarkingObjName;
        $("#rightObjName").val(objTitle);
        $("#benchmarkingObjId").attr("disabled", true)
        request.service({
            method: 'get',
            url: "/benchmarkingObjData/queryById/" + tableSelect[0].id,
        }).then(function (res) {
            disLoad();
            var editObj = res.one;
            $("#rightYear").val(editObj.year);
            $("#gdpElectricity").val(editObj.gdpElectricity);
            $("#gdpWater").val(editObj.gdpWater);
            $("#gdpStdCoal").val(editObj.gdpStdCoal);
            $("#addValueStdCoal").val(editObj.addValueStdCoal);
            $("#right_memo").val(editObj.memo);
            showRightModel(editObj.id, '修改对标对象指标数据');
        }).catch(function (err) {
            console.log(err)
        });



    });

    // 新增修改对标对象数据
    function showRightModel(editId, title) {
        layui.layer.open({
            type: 1,
            title: title,
            closeBtn: 1,
            shade: 0.3,
            maxmin: true,
            anim: 1,
            area: ['600px', '650px'],
            content: $('#objData_add'),
            btn: ['保存', '取消'],
            success: function () {
                $("#objData_add").removeClass('layui-hide');
            },
            yes: function (index) {
                var year = $("#rightYear").val();
                var gdpElectricity = $("#gdpElectricity").val()
                var gdpWater = $("#gdpWater").val();
                var gdpStdCoal = $("#gdpStdCoal").val();
                var addValueStdCoal = $("#addValueStdCoal").val();
                var memo = $("#right_memo").val();
                if (!year) {
                    return layer.msg("请选择年份")
                }
                var val = tree1.getSelectedNodes();
               
                var formData = {};
                formData.year = year;
                formData.gdpElectricity = gdpElectricity?parseFloat(gdpElectricity):null;
                formData.gdpWater = gdpWater?parseFloat(gdpElectricity):null;
                formData.gdpStdCoal = gdpStdCoal?parseFloat(gdpElectricity):null;
                formData.addValueStdCoal = addValueStdCoal?parseFloat(gdpElectricity):null;
                formData.memo = memo;
                formData.id = editId;
                formData.objType = siteObj.type;
                formData.objId = siteObj.id;
                formData.benchmarkingObjId =val[0].benchmarkingObjId;
                var url = '/benchmarkingObjData/add'
                if (editId) {
                    url = '/benchmarkingObjData/edit'
                }
                load();
                request.service({
                    method: 'post',
                    url: url,
                    data: formData
                }).then(function (res) {
                    disLoad();
                    getRightData(val[0].benchmarkingObjId);
                    layer.close(index);
                }).catch(function (err) {
                    disLoad();
                    console.log(err)
                });
            },
            end: function (index) {
                $("#objData_add").addClass('layui-hide');
            }
        });
    }

    $('#rightDel').click(function () { //左侧对标对象数据点击事件
      
        var tableSelect = table.checkStatus('role_table').data;
        if (tableSelect.length === 0) {
            return layer.msg("请选择需要删除的指标数据！")
        }
        var val = tree1.getSelectedNodes();
        var  objData =val[0]
        var benchmarkingObjTypename;
        switch (val[0].benchmarkingObjType) {
            case "Domestic":
                benchmarkingObjTypename = '国内'
                break;
            case "International":
                benchmarkingObjTypename = '国外'
                break;
        }
        var objTitle = '[' + benchmarkingObjTypename + '][' + objData.benchmarkingObjId + ']' + objData.benchmarkingObjName;
        layer.open({
            type: 1,
            title: "删除对标对象指标数据",
            shade: 0.3,
            btn: ['确定', '取消'],
            area: ['380px', '260px'], //宽高
            content: '<div id="park_select_tree"> <form class="layui-form" style="margin:30px;">' +
                '   <div class="layui-form-item "><label>对标对象：</label>' +
                '<div class="layui-inline">' + objTitle + '</div></div>' +
                '   <div class="layui-form-item "><label>年：</label>' +
                '<div class="layui-inline">' + tableSelect[0].year + '</div></div>' +
                '   <div class="layui-form-item " style="color:red;">确定要删除此对标对象指标数据吗？</div>' +
                '</form></div>',
            success: function () {

            },
            yes: function (index) {
                load();
                request.service({
                        method: 'get',
                        url: '/benchmarkingObjData/delete/'+tableSelect[0].id,
                    })
                    .then(function (res) {
                        disLoad();
                        layer.close(index);
                        getRightData(val[0].benchmarkingObjId);
                    })
                    .catch(function (err) {
                        console.log(err)
                    })
            },
            end: function (index) { // 模态框关闭事件
                layer.close(index);
            }
        })
    });


    var indexLoading;

    function load() {
        indexLoading = layer.load(1, {
            shade: [0.3, '#fff']
        });
    }

    function disLoad() {
        layer.close(indexLoading);
    }

        //提示弹框点击-园区标识
        $("#benchmarkingObjId_tip").on("click", function () {
            layer.tips('字母、数字组合，长度不超过20 ,在园区/当前企业内唯一', '#benchmarkingObjId_tip');
        })
        //提示弹框点击 
        $("#benchmarkingObjName_tip").on("click", function () {
            layer.tips('长度不超过30', '#benchmarkingObjName_tip');
        })
        $("#tree_sortId_tip").on("click", function () {
            layer.tips('字母、数字组合，长度不超过20 系统按照字符串升序', '#tree_sortId_tip');
        })
        $("#gdpElectricity_tip").on("click", function () {
            layer.tips('只能输入数值，单位为kWh/万元', '#gdpElectricity_tip');
        })

        $("#gdpWater_tip").on("click", function () {
            layer.tips('只能输入数值，单位为t/万元', '#gdpWater_tip');
        })
    
        $("#gdpStdCoal_tip").on("click", function () {
            layer.tips('只能输入数值，单位为tce/万元', '#gdpStdCoal_tip');
        })

        $("#addValueStdCoal_tip").on("click", function () {
            layer.tips('只能输入数值，单位为kWh/万元', '#addValueStdCoal_tip');
        })


     
});