/*
 * @Author: jpp
 * @Date: 2019-11-25 10:05:35
 * @Last Modified by: xzl
 * @Last Modified time: 2019-11-26 15:20:04
 */
layui.use(['form', 'element', 'layer', 'table', 'jquery', 'laypage', 'upload'], function () {
    var form = layui.form,
        layer = layui.layer,
        $ = layui.jquery,
        table = layui.table,
        laypage = layui.laypage,
        upload = layui.upload,
        element = layui.element;

    var siteObj = JSON.parse(sessionStorage.getItem('parkId'));

    var indexLoading;
    function load() { //加载事件
        indexLoading = layer.load(1, {
            shade: [0.3, '#fff']
        });
    }

    function disLoad() { //取消加载事件
        layer.close(indexLoading);
    }

    //查询点击事件
    $("#search_btn").on("click", function () {
        getSiteTabData()
    });
    //新增点击事件
    $("#add_btn").on("click", function () {
        $(".add_siteId").removeClass('layui-hide');
        $(".edit_siteId").addClass('layui-hide');

        $("#siteId").val('');
        $("#siteName").val('');
        $("#siteShortName").val('');
        $("#siteAddress").val('');
        $("#longitude").val('');
        $("#latitude").val('');
        $("#add_isOnline").attr('checked',false);
        $("#sortId").val('');
        $("#rtdbProjectId").val('');
        $("#memo").val('');
        $("#imagePreview").attr('src','');

        $("#file").val('');

        form.val("site_add", {
            "isOnline":false
        });

        add('add','新增企业');
    });
    //修改点击事件
    $("#edit_btn").on("click", function () {
        var tableSelect = table.checkStatus('company_table').data;
        if(tableSelect.length === 0){
            return layer.msg("请选择需要修改的数据！")
        }
        if(tableSelect.length > 1){
            return layer.msg("只能选择单条数据！")
        }
        $(".edit_siteId").removeClass('layui-hide');
        $(".add_siteId").addClass('layui-hide');

        $("#file").val('');

        load();
        request.service({
            method: 'get',
            url: '/site/querySiteById/' + tableSelect[0].id
        }).then(function (res) {
            disLoad();
            $("#edit_siteId").html(tableSelect[0].siteId);
            $("#siteName").val(tableSelect[0].siteName);
            $("#siteShortName").val(tableSelect[0].siteAbbrName);
            $("#siteAddress").val(tableSelect[0].addr);
            $("#longitude").val(tableSelect[0].longitude);
            $("#latitude").val(tableSelect[0].latitude);
            $("#add_isOnline").attr('checked',tableSelect[0].isOnline);
            $("#sortId").val(tableSelect[0].sortId);
            $("#rtdbProjectId").val(tableSelect[0].rtdbProjectId);
            $("#memo").val(tableSelect[0].memo);

            form.val("site_add", {
                "isOnline":tableSelect[0].isOnline
            });

            $("#imagePreview").attr('src','data:image/png;base64,'+res.one.img);
            add('edit','修改企业',res.one);
        }).catch(function(err){
            disLoad();
            console.log(err)
        });
    });
    //删除点击事件
    $("#del_btn").on("click", function () {
        var tableSelect = table.checkStatus('company_table').data;
        if(tableSelect.length === 0){
            return layer.msg("请选择需要修改的数据！")
        }
        if(tableSelect.length > 1){
            return layer.msg("只能选择单条数据！")
        }
        deletes();
    });

    //新增企业弹框 提示点击 - 企业标识
    $("#siteId_tip").on("click", function () {
        layer.tips('字母、数字组合，长度不超过20。', '#siteId_tip');
    });
    //新增企业弹框 提示点击 - 企业名称
    $("#siteName_tip").on("click", function () {
        layer.tips('长度不超过30。', '#siteName_tip');
    });
    //新增企业弹框 提示点击 - 百度地图-经度
    $("#longitude_tip").on("click", function () {
        layer.tips('在能耗地图模块需要用到此处的百度地图的相关配置。', '#longitude_tip');
    });
    //新增企业弹框 提示点击 - 排序标识
    $("#sortId_tip").on("click", function () {
        layer.tips('字母、数字组合，长度不超过10，系统按照字符串升序。', '#sortId_tip');
    });
    //新增企业弹框 提示点击 - 实时库项目标识
    $("#rtdbProjectId_tip").on("click", function () {
        layer.tips('此企业在实时库中对应的项目标识。\n' + '字母、数字组合，长度不超过20。', '#rtdbProjectId_tip');
    });
    //新增企业弹框 提示点击 - 企业图片
    $("#upload_tip").on("click", function () {
        layer.tips('请上传jpg、png，大小限制在500K以内，尺寸建议200*200。', '#upload_tip');
    });
    //删除企业弹框 提示点击
    $("#delete_tip").on("click", function () {
        layer.tips('只删除企业信息表中的信息，不删除其他关联信息。', '#delete_tip');
    });

    //初始函数
    function initFun() {
        getSiteTabData();
    }
    var pageNum = 1;
    var pageLimit;
    function getSiteTabData() { //ajax获取 数据
        var tnum = parseInt(($(".company_content").height() - 60) / 40); //动态生成表格展示条数
        var formData = {};
        formData.limit = pageLimit ? pageLimit : tnum;
        formData.page = pageNum;
        var key = {};
        key.siteId = $("#site_id").val();
        key.siteName = $("#site_name").val();
        key.isOnline = $("#isOnline").val();
        formData.key = key;
        load();
        request.service({
            method: 'post',
            url: '/site/querySite',
            data: formData
        })
            .then(function (res) {
                disLoad();
                var data = res.data;
                if (data.length > 0) {

                }
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
            elem: '#company_table',
            height: 'full-155',
            cols: [
                [{
                    type: "radio",
                    width: 50,
                    fixed: "left"
                }, {
                    type: "numbers",
                    width: 50,
                    title: '序号'
                }, {
                    field: 'siteId',
                    width: 100,
                    align: "center",
                    title: '企业标识'
                }, {
                    field: 'siteName',
                    width: 250,
                    align: "center",
                    title: '企业名称'
                }, {
                    field: 'siteAbbrName',
                    width: 100,
                    align: "center",
                    title: '企业简称'
                }, {
                    field: 'addr',
                    width: 350,
                    align: "center",
                    title: '企业地址'
                }, {
                    field: 'longitude',
                    width: 120,
                    align: "center",
                    title: '百度地图-经度'
                }, {
                    field: 'latitude',
                    width: 120,
                    align: "center",
                    title: '百度地图-纬度'
                }, {
                    field: 'isOnline',
                    width: 100,
                    align: "center",
                    title: '是否上线',
                    templet:function(d){
                        if(d.isOnline == true){
                            return '是';
                        }else{
                            return '否';
                        }
                    }
                }, {
                    field: 'sortId',
                    width: 90,
                    align: "center",
                    title: '排序标识'
                }, {
                    field: 'rtdbProjectId',
                    width: 130,
                    align: "center",
                    title: '实时库项目标识'
                }, {
                    field: 'memo',
                    align: "center",
                    title: '备注'
                }, {
                    field: 'createUserId',
                    width: 135,
                    align: "center",
                    title: '创建者'
                }, {
                    field: 'createTime',
                    width: 180,
                    align: "center",
                    title: '创建时间'

                }, {
                    field: 'updateUserId',
                    align: "center",
                    title: '修改者',
                    width: 135

                }, {
                    field: 'updateTime',
                    align: "center",
                    title: '修改时间',
                    width: 180

                }]
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

    $("#file").on("change", function(e){
        var filePath = $(this).val(),         //获取到input的value，里面是文件的路径
            fileFormat = filePath.substring(filePath.lastIndexOf(".")).toLowerCase(),
            src = window.URL.createObjectURL(this.files[0]); //转成可以在本地预览的格式

        // 检查是否是图片
        if( !fileFormat.match(/.png|.jpg|.jpeg/) ) {
            layer.msg('上传错误,文件格式必须为：png/jpg/jpeg');
            return;
        }
        $('#imagePreview').attr('src',src);
    });

    //将base64转换为文件
    function dataURLtoFile(dataurl, filename) {
        var arr = dataurl.split(',');
        var mime = arr[0].match(/:(.*?);/)[1];
        var bstr = atob(arr[1]);
        var n = bstr.length;
        var u8arr = new Uint8Array(n);
        while(n--){
            u8arr[n] = bstr.charCodeAt(n);
        }
        //转换成file对象
        return new File([u8arr], filename, {type:mime});
        //转换成成blob对象
        //return new Blob([u8arr],{type:mime});
    }

    // 新增企业
    function add(type,title,editData){
        layui.layer.open({
            type: 1,
            title: title,
            closeBtn: 1,
            shade: 0,
            anim: 1,
            maxmin: true, //开启最大化最小化按钮
            area: ['800px', '690px'],
            content: $('#site_add'),
            btn: ['确定', '取消'],
            success: function () {
                $("#site_add").removeClass('layui-hide');
                form.render();
            },
            yes: function (index) {
                var isOnlineArr = $('#add_isOnline:checked');
                var isOnline = false;
                if(isOnlineArr.length > 0){
                    isOnline = true;
                }
                var formData = new FormData($("#siteImage.layui-form")[0]);
                var file = formData.get('file');
                formData.append("objType", siteObj.type);
                formData.append("objId", siteObj.id);
                if(type == 'edit'){
                    var tableSelect = table.checkStatus('company_table').data;
                    formData.append("id", tableSelect[0].id);
                    formData.append("siteId", tableSelect[0].siteId);

                    if(file.size == 0 && editData.img){
                        formData.set("file",dataURLtoFile('data:image/png;base64,'+editData.img,'123.png'));
                    }
                    if(file.size == 0 && !editData.img){
                        formData.set("file",null);
                    }
                }else if(type == 'add'){
                    var siteId = $("#siteId").val();
                    if(siteId == '') return layer.msg('企业标识不能为空！');
                    if(siteId.length > 20) return layer.msg('企业标识长度不能超过20！');
                    var re = /^[0-9a-zA-Z]+$/g;
                    if(!(re.test(siteId))){
                        return layer.msg('企业标识为字母、数字组合！');
                    }
                    formData.append("siteId", siteId);

                    if(file.size == 0){
                        formData.set('file',null);
                    }
                }

                var data = {
                    "siteName": $("#siteName").val(),
                    "sortId": $("#sortId").val(),
                    "rtdbProjectId": $("#rtdbProjectId").val()
                };

                if(data.siteName == '') return layer.msg('企业名称不能为空！');
                if(data.siteName.length > 30) return layer.msg('企业名称长度不能超过30！');
                if(data.sortId.length > 10) return layer.msg('排序标识长度不能超过10！');
                var re2 = /^[0-9a-zA-Z]+$/g;
                if(data.sortId.length > 0 && !(re2.test(data.sortId))){
                    return layer.msg('排序标识为字母、数字组合！');
                }

                if(data.rtdbProjectId.length > 20) return layer.msg('实时库项目标识长度不能超过20！');
                var re3 = /^[0-9a-zA-Z]+$/g;
                if(data.rtdbProjectId.length > 0 && !(re3.test(data.rtdbProjectId))){
                    return layer.msg('实时库项目标识为字母、数字组合！');
                }

                formData.append("siteName", $("#siteName").val());
                formData.append("siteAbbrName", $("#siteShortName").val());
                formData.append("addr", $("#siteAddress").val());
                formData.append("longitude", $("#longitude").val());
                formData.append("latitude", $("#latitude").val());
                formData.append("isOnline", isOnline);
                formData.append("sortId", $("#sortId").val());
                formData.append("rtdbProjectId", $("#rtdbProjectId").val());
                formData.append("memo", $("#memo").val());

                if(file.size > 500000){
                    return layer.msg('图片要求小于500k！');
                }

                var url ='/site/add';
                if(type =="edit"){
                    url ='/site/edit';
                }
                load();
                request.service({
                    method: 'post',
                    url: url,
                    data: formData
                }).then(function (res) {
                    disLoad();
                    getSiteTabData();
                    layer.close(index);
                }).catch(function(err){
                    disLoad();
                    console.log(err)
                });
            },
            end: function(index){
                $("#site_add").addClass('layui-hide');
            }
        });
    }

    // 删除企业
    function deletes(){
        layer.open({
            type: 1,
            title: '删除企业',
            closeBtn: 1,
            shade: 0,
            anim: 1,
            maxmin: true, //开启最大化最小化按钮
            area: ['400px', '350px'],
            content: $('#site_delete'),
            btn: ['确定', '取消'],
            success: function () {
                $("#site_delete").removeClass('layui-hide');

                var tableSelect = table.checkStatus('company_table').data;
                $("#delete_siteId").html(tableSelect[0].siteId);
                $("#delete_siteName").html(tableSelect[0].siteName);
            },
            yes: function (index) {
                var tableSelect = table.checkStatus('company_table').data;
                load();
                request.service({
                    method: 'get',
                    url: '/site/delete/'+tableSelect[0].siteId
                }).then(function (res) {
                    disLoad();
                    getSiteTabData();
                    layer.close(index);
                }).catch(function(err){
                    disLoad();
                    console.log(err)
                });
            },
            end: function(index){
                $("#site_delete").addClass('layui-hide');
            }
        });
    }


    initFun(); //页面加载时执行初始函数












});