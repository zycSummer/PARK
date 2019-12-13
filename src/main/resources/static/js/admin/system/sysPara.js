/*
 * @Author: xzl 
 * @Date: 2019-11-25 09:46:52 
 * @Last Modified by: xzl
 * @Last Modified time: 2019-11-25 10:05:46
 */
layui.use(['form', 'element', 'layer', 'table', 'jquery', 'laypage','colorpicker'], function () {
    var form = layui.form,
        layer = layui.layer,
        $ = layui.jquery,
        table = layui.table,
        colorpicker =layui.colorpicker,
        laypage = layui.laypage,
        element = layui.element;
    var indexLoading; 
    function load() {
        indexLoading = layer.load(1, {
            shade: [0.3, '#fff']
        });
    }

    function disLoad() {
        layer.close(indexLoading);
    }
    //查询点击事件
    $("#search_btn").on("click", function () {
        getSysParaData()
    })

    //初始函数
    function initFun() {
        getSysParaData();
    }
    function getSysParaData() { //ajax获取 数据
        var formData = {};
        formData.sysParaId =$("#search_sysParaId").val();
        load();
        request.service({
                method: 'post',
                url: '/sysParameter/query',
                data: formData
            })
            .then(function (res) {
                disLoad();
                var data = res.data;      
                renderTableContent(data);
          
            })
            .catch(err => {
                console.log(err)
            })

    }

    //渲染表格数据
    function renderTableContent(data) {
        table.render({
            elem: '#role_table',
            height: 'full-155',
            cols: [
                [{
                        type: "radio",
                        width: 50,
                        fixed: "left"
                    },

                    {
                        type: 'numbers',
                        align: "center",
                        title: '序号'

                    },
                    {
                        field: 'paraId',
                        align: "center",
                        title: '参数标识'
                    },
                    {
                        field: 'paraValue',
                        align: "center",
                        title: '参数值'

                    },
                    {
                        field: 'paraDesc',
                        align: "center",
                        title: '参数描述'

                    },
                    {
                        field: 'memo',
                        align: "center",
                        title: '备注'
                    }, {
                        field: 'createUserId',
                        align: "center",
                        title: '创建者',


                    }, {
                        field: 'createTime',
                        align: "center",
                        title: '创建时间',

                    }, {
                        field: 'updateTime',
                        align: "center",
                        title: '修改者',
                        width: 135

                    }, {
                        field: 'updateUserId',
                        align: "center",
                        title: '修改时间',
                        width: 135

                    }
                ]
            ],
            data: data,
            page: false,
            limit: 1000
        })

    }

   
    $("#edit_btn").on('click', function () { //修改点击事件
        var tableSelect = table.checkStatus('role_table').data;
        if (tableSelect.length === 0) {
            return layer.msg("请选择需要修改的数据！")
        }
       
        var editData = tableSelect[0];
        $('#paraId').attr("disabled", true);
        
        $("#paraValue").val(editData.paraValue)
        $("#paraDesc").val(editData.paraDesc)
        $("#paraId").val(editData.paraId)
        $("#addmemo").val(editData.memo)
        showModelIndexBox(editData.id, "编辑系统参数")
    })


    function showModelIndexBox(editId, title) { //模态框调用事件
        layer.open({
            type: 1,
            title: title,
            closeBtn: 1,
            shade: 0.3,
            maxmin: true,
            anim: 1,
            area: ['500px', '550px'],
            content: $('#energyGrade_add'),
            btn: ['保存', '关闭'],
            success: function () {
                $('#energyGrade_add').removeClass('layui-hide').addClass('layui-show');
            },
            yes: function (index) {
                var paraValue = $("#paraValue").val() //参数值
                var paraId = $("#paraId").val(); //参数标识
                var paraDesc = $("#paraDesc").val()  //参数描述
                var memo   =$("#addmemo").val();
                if (!paraValue) {
                    return layer.msg('请输入系统参数值')
                }
                var formData = {};
                formData.paraValue = paraValue;
                formData.paraId = paraId;
                formData.paraDesc = paraDesc;
                formData.memo  =memo;
                load();
                request.service({
                        method: 'post',
                        url: '/sysParameter/edit',
                        data: formData
                    })
                    .then(function (res) {
                        disLoad();
                        layer.close(index);
                        getSysParaData();
                    })
                    .catch(err => {
                        console.log(err)
                    })
            },
            end: function (index) { // 模态框关闭事件
                $('#energyGrade_add').removeClass('layui-show').addClass('layui-hide');
            }
        });
    }

   

    $("#lowerTip").on("click", function () {
        layer.tips('长度不超过250', '#lowerTip');
    })
    $("#upperTip").on("click", function () {
        layer.tips('长度不超过250', '#upperTip');
    })
    initFun(); //页面加载时执行初始函数 
});