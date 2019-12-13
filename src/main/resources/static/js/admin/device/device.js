/*
 * @Author: xzl 
 * @Date: 2019-11-11 10:05:35 
 * @Last Modified by: jpp
 * @Last Modified time: 2019-12-11 10:40:11
 */
layui.use(['form', 'element', 'layer', 'table', 'jquery', 'laypage', 'laydate'], function () {
    var form = layui.form,
        layer = layui.layer,
        $ = layui.jquery,
        table = layui.table,
        laypage = layui.laypage,
        laydate = layui.laydate;
        element = layui.element;

    var siteObj = JSON.parse(sessionStorage.getItem('parkId'));
    var equipSysId = '';//左侧
    
    var indexLoading;
    function load() { //加载事件
        indexLoading = layer.load(1, {
            shade: [0.3, '#fff']
        });
    }

    function disLoad() { //取消加载事件
        layer.close(indexLoading);
    }

    //监听提交
    form.on('submit(*)', function (data) {
        return false;//阻止表单跳转
    });

    //左侧列表 点击
    $(document).on('click','#leftList li',function(){
        $(this).siblings().removeClass('layui-this');
        $(this).addClass('layui-this');
        equipSysId = $(this).attr('data-sysId');
        return false;
    });
    //左侧 新增设备系统
    $('#add').on('click',function(){
        $(".add_deviceSysId").removeClass('layui-hide');
        $(".edit_deviceSysId").addClass('layui-hide');

        $("#deviceSysId").val('');
        $("#deviceSysName").val('');
        $("#sortIdSys").val('');
        $("#memoSys").val('');
        add('add','新增设备系统');
    });
    //左侧 修改设备系统
    $('#edit').on('click',function(){
        if(!equipSysId) return layer.msg('请选择一条设备系统');
        $(".edit_deviceSysId").removeClass('layui-hide');
        $(".add_deviceSysId").addClass('layui-hide');

        var id = $("#leftList li.layui-this").attr('data-id');
        load();
        request.service({
            method: 'get',
            url: '/equip/queryEquipSysById/'+id
        }).then(function (res) {
            disLoad();
            $("#edit_deviceSysId").html(res.one.equipSysId);
            $("#deviceSysName").val(res.one.equipSysName);
            $("#sortIdSys").val(res.one.sortId);
            $("#memoSys").val(res.one.memo);
            add('edit','修改设备系统');
        }).catch(function(err){
            disLoad();
            console.log(err)
        });
    });
    //左侧 删除设备系统
    $('#delete').on('click',function(){
        if(!equipSysId) return layer.msg('请选择要删除的设备系统');
        deletes();
    });

    //右侧 查询点击事件
    $("#search_btn").on("click", function () {
        getDeviceTabData()
    });
    //右侧 新增设备列表
    $('#add_btn').on('click',function(){
        $(".edit_equipId").addClass('layui-hide');
        $(".add_equipId").removeClass('layui-hide');

        $("#equipId").val('');
        $("#equipName").val('');
        $("#deviceSystem").val($("#deviceSystem option:first-child").val());
        $("#manufacturer").val('');
        $("#model").val('');
        $("#location").val('');
        // $("#productionDate").val('');
        // $("#firstUseDate").val('');
        $("#sortId").val('');
        $("#memo").val('');
        $("#file").val('');
        $("#imagePreview").attr('src','');
        form.val("equip_add_from", {
            "deviceSystem": $("#deviceSystem option:first-child").val()
        });
        addEquip('add','新增设备');
    });
    //右侧 修改设备
    $('#edit_btn').on('click',function(){
        $(".edit_equipId").removeClass('layui-hide');
        $(".add_equipId").addClass('layui-hide');

        var tableSelect = table.checkStatus('device_table').data;
        if(tableSelect.length === 0){
            return layer.msg("请选择需要修改的数据！")
        }
        if(tableSelect.length > 1){
            return layer.msg("只能选择单条数据！")
        }
        var editData = tableSelect[0];
        $("#edit_equipId").html(editData.equipId);

        load();
        request.service({
            method: 'post',
            url: '/equip/queryImage',
            data: {
                'objType': siteObj.type,
                'objId': siteObj.id,
                'equipId':editData.equipId
            }
        }).then(function (res) {
            disLoad();
            $("#equipName").val(res.one.equipName);
            $("#deviceSystem").val(res.one.equipSysId);
            $("#manufacturer").val(res.one.manufacturer);
            $("#model").val(res.one.model);
            $("#location").val(res.one.location);
            $("#productionDate").val(res.one.productionDate);
            $("#firstUseDate").val(res.one.firstUseDate);
            $("#sortId").val(res.one.sortId);
            $("#memo").val(res.one.memo);

            form.val("equip_add_from", {
                "deviceSystem": editData.equipSysId
            });

            if(res.one.imgBase64){
                $("#file").val('');
                $("#imagePreview").attr('src','data:image/png;base64,'+res.one.imgBase64);
            }else{
                $("#file").val('');
                $("#imagePreview").attr('src','');
            }

            addEquip('edit','修改设备',res.one);
        }).catch(function(err){
            disLoad();
            console.log(err)
        });
    });
    //右侧 删除设备
    $('#del_btn').on('click',function(){
        var tableSelect = table.checkStatus('device_table').data;
        if(tableSelect.length === 0){
            return layer.msg("请选择需要删除的设备！")
        }

        deleteEquip();
    });
    //右侧 导入设备
    $('#import_btn').on('click',function(){
        importEquip();
    });
    //右侧 导出设备
    $('#export_btn').on('click',function(){
        var searchData = form.val("searchForm");
        var equipSysIdSelect,equipId,equipName;
        if(searchData.range == 'equipSysId'){
            equipSysIdSelect = equipSysId;
        }else{
            equipSysIdSelect = '';
        }

        equipId = $("#search_equipId").val();
        equipName = $("#search_equipName").val();
        var url = '/equip/exportExcel?equipSysId='+equipSysIdSelect+'&equipId='+equipId+'&equipName='+equipName+'&objId='+siteObj.id+'&objType='+siteObj.type;
        window.location.href = encodeURI(url);
    });

    //右侧 导入设备 模板文件下载
    $("#equip_download_demo").on('click',function(){
        var url = '/equip/download';
        window.location.href = encodeURI(url);
    });

    //设备系统 提示点击 - 设备系统标识
    $("#deviceSysId_tip").on("click", function () {
        layer.tips('字母、数字组合，长度不超过20,在园区/当前企业内唯一。', '#deviceSysId_tip');
    });
    //设备系统 提示点击 - 设备系统名称
    $("#deviceSysName_tip").on("click", function () {
        layer.tips('长度不超过30。', '#deviceSysName_tip');
    });
    //设备系统 提示点击 - 排序标识
    $("#sortIdSys_tip").on("click", function () {
        layer.tips('字母、数字组合，长度不超过10，系统按照字符串升序。', '#sortIdSys_tip');
    });
    //设备 提示点击 - 设备标识
    $("#equipId_tip").on("click", function () {
        layer.tips('字母、数字组合，长度不超过20。在园区/当前企业内唯一。', '#equipId_tip');
    });
    //设备 提示点击 - 设备名称
    $("#equipName_tip").on("click", function () {
        layer.tips('长度不超过30。', '#equipName_tip');
    });
    //设备 提示点击 - 排序标识
    $("#sortId_tip").on("click", function () {
        layer.tips('字母、数字组合，长度不超过10，系统按照字符串升序。', '#sortId_tip');
    });
    //设备 提示点击 - 铭牌
    $("#upload_tip").on("click", function () {
        layer.tips('请上传jpg、png，大小限制在1M以内，尺寸建议800*800。', '#upload_tip');
    });

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
    /*----------------------------------函数------------------------------------*/
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

    // 新增修改设备系统
    function add(type,title){
        layer.open({
            type: 1,
            title: title,
            closeBtn: 1,
            shade: 0,
            anim: 1,
            maxmin: true, //开启最大化最小化按钮
            area: ['450px', '380px'],
            content: $('#deviceSys_add'),
            btn: ['确定', '取消'],
            success: function () {
                $("#deviceSys_add").removeClass('layui-hide');
                form.render();
            },
            yes: function (index) {
                var url = '';
                if(type == 'edit'){
                    var id = $("#leftList li.layui-this").attr('data-id');
                    var data = {
                        "objType": siteObj.type,
                        "objId": siteObj.id,
                        "id":id,
                        "equipSysId": equipSysId,
                        "equipSysName": $("#deviceSysName").val(),
                        "sortId": $("#sortIdSys").val(),
                        "memo":$("#memoSys").val()
                    };
                    url = '/equip/updateEquipSys';
                }else if(type == 'add'){
                    var data = {
                        "objType": siteObj.type,
                        "objId": siteObj.id,
                        "equipSysId": $("#deviceSysId").val(),
                        "equipSysName": $("#deviceSysName").val(),
                        "sortId": $("#sortIdSys").val(),
                        "memo":$("#memoSys").val()
                    };
                    url = '/equip/insertEquipSys';

                    if(data.equipSysId == '') return layer.msg('设备系统标识不能为空！');
                    if(data.equipSysId.length > 20) return layer.msg('设备系统标识长度不能超过20！');
                    var re = /^[0-9a-zA-Z]+$/g;
                    if(!(re.test(data.equipSysId))){
                        return layer.msg('只能输入字母和数字！');
                    }
                }

                if(data.equipSysName == '') return layer.msg('设备系统名称不能为空！');
                if(data.equipSysName.length > 30) return layer.msg('设备系统名称长度不能超过30！');
                if(data.sortId.length > 10) return layer.msg('排序标识长度不能超过10！');

                var re2 = /^[0-9a-zA-Z]+$/g;
                if(data.sortId.length > 0 && !(re2.test(data.sortId))){
                    return layer.msg('排序标识为字母、数字组合！');
                }

                load();
                request.service({
                    method: 'post',
                    url: url,
                    data: data
                }).then(function (res) {
                    disLoad();
                    getTree1();
                    layer.close(index);
                }).catch(function(err){
                    disLoad();
                    console.log(err)
                });
            },
            end: function(index){
                $("#deviceSys_add").addClass('layui-hide');
            }
        });
    }

    // 删除设备系统
    function deletes(){
        layer.open({
            type: 1,
            title: '删除设备系统',
            closeBtn: 1,
            shade: 0,
            anim: 1,
            maxmin: true, //开启最大化最小化按钮
            area: ['400px', '350px'],
            content: $('#deviceSys_delete'),
            btn: ['确定', '取消'],
            success: function () {
                $("#deviceSys_delete").removeClass('layui-hide');

                var equipSysName = $("#leftList li.layui-this").attr('data-name');
                $("#delete_deviceSysId").html(equipSysId);
                $("#delete_deviceSysName").html(equipSysName);
                form.render();
            },
            yes: function (index) {
                load();
                request.service({
                    method: 'post',
                    url: '/equip/deleteEquipSys',
                    data:{
                        "objType": siteObj.type,
                        "objId": siteObj.id,
                        "equipSysId": equipSysId
                    }
                }).then(function (res) {
                    disLoad();
                    getTree1();
                    layer.close(index);
                }).catch(function(err){
                    disLoad();
                    console.log(err)
                });
            },
            end: function(index){
                $("#deviceSys_delete").addClass('layui-hide');
            }
        });
    }

    //左侧设备系统
    function getTree1(){
        if(siteObj){
            load();
            request.service({
                method: 'post',
                url: '/equip/queryEquipSys',
                data: {
                    'objType': siteObj.type,
                    'objId': siteObj.id
                }
            }).then(function (res) {
                disLoad();
                if(res.data){
                    var list = res.data;
                    var str = '',str2 = '';
                    if(list.length>0){
                        $.each(list,function (kk,jj) {
                            var showName = '['+jj.equipSysId+']'+jj.equipSysName;
                            if(kk==0){
                                equipSysId = jj.equipSysId;
                                str+='<li class="layui-this" data-id="'+jj.id+'" data-sysId="'+jj.equipSysId+'" data-name="'+jj.equipSysName+'">'+showName+'</li>';
                            }else{
                                str+='<li data-id="'+jj.id+'" data-sysId="'+jj.equipSysId+'" data-name="'+jj.equipSysName+'">'+showName+'</li>';
                            }
                            str2 += '<option value="'+jj.equipSysId+'">'+showName+'</option>';
                        });
                        getDeviceTabData();
                    }
                    $("#leftList").html(str);
                    $("#deviceSystem").html(str2);
                    form.render('select');
                }
            }).catch(function(err){
                disLoad();
                console.log(err)
            });
        }
    }

    // 新增修改设备
    function addEquip(type,title,editData){
        layer.open({
            type: 1,
            title: title,
            closeBtn: 1,
            shade: 0,
            anim: 1,
            maxmin: true, //开启最大化最小化按钮
            area: ['800px', '670px'],
            content: $('#equip_add'),
            btn: ['确定', '取消'],
            success: function () {
                $("#equip_add").removeClass('layui-hide');
                form.render();
            },
            yes: function (index) {
                var formData = new FormData($("#siteImage.layui-form")[0]);
                var file = formData.get('file');
                formData.append("objType", siteObj.type);
                formData.append("objId", siteObj.id);

                var url = '';
                if(type == 'edit'){
                    var tableSelect = table.checkStatus('device_table').data;
                    formData.append("id", tableSelect[0].id);
                    formData.append("equipId", tableSelect[0].equipId);

                    if(file.size == 0 && editData.imgSuffix){
                        formData.set("file",dataURLtoFile('data:image/png;base64,'+editData.imgBase64,'123.png'));
                    }

                    if(file.size == 0 && !editData.imgSuffix){
                        formData.set("file",null);
                    }

                    url = '/equip/editEquip';
                }else if(type == 'add'){
                    var equipId = $("#equipId").val();
                    if(equipId == '') return layer.msg('设备标识不能为空！');
                    if(equipId.length > 20) return layer.msg('设备标识长度不能超过20！');
                    var re = /^[0-9a-zA-Z]+$/g;
                    if(!(re.test(equipId))){
                        return layer.msg('只能输入字母和数字！');
                    }
                    formData.append("equipId", equipId);

                    if(file.size == 0){
                        formData.set('file',null);
                    }

                    url = '/equip/addEquip';
                }
                var data = {
                    "equipName": $("#equipName").val(),
                    "deviceSystem":$("#deviceSystem").val(),
                    "sortId": $("#sortId").val()
                };

                if(data.equipName == '') return layer.msg('设备名称不能为空！');
                if(data.equipName.length > 30) return layer.msg('设备名称长度不能超过30！');
                if(data.deviceSystem == '') return layer.msg('设备系统不能为空！');
                if(data.sortId.length > 10) return layer.msg('排序标识长度不能超过10！');

                var re2 = /^[0-9a-zA-Z]+$/g;
                if(data.sortId.length > 0 && !(re2.test(data.sortId))){
                    return layer.msg('排序标识为字母、数字组合！');
                }

                if(file.size > 1024000){
                    return layer.msg('图片要求小于1M！');
                }

                formData.append("equipName", $("#equipName").val());
                formData.append("equipSysId", $("#deviceSystem").val());
                formData.append("manufacturer", $("#manufacturer").val());
                formData.append("model", $("#model").val());
                formData.append("location", $("#location").val());
                formData.append("productionDate", $("#productionDate").val());
                formData.append("firstUseDate", $("#firstUseDate").val());
                formData.append("sortId", $("#sortId").val());
                formData.append("memo", $("#memo").val());

                load();
                request.service({
                    method: 'post',
                    url: url,
                    data: formData
                }).then(function (res) {
                    disLoad();
                    getDeviceTabData();
                    layer.close(index);
                }).catch(function(err){
                    disLoad();
                    console.log(err)
                });
            },
            end: function(index){
                $("#equip_add").addClass('layui-hide');
            }
        });
    }

    // 删除设备
    function deleteEquip(){
        layer.open({
            type: 1,
            title: '删除设备',
            closeBtn: 1,
            shade: 0,
            anim: 1,
            maxmin: true, //开启最大化最小化按钮
            area: ['300px', '150px'],
            content: $('#equip_delete'),
            btn: ['确定', '取消'],
            success: function () {
                $("#equip_delete").removeClass('layui-hide');
                var tableSelect = table.checkStatus('device_table').data;
                $("#number").html(tableSelect.length);
            },
            yes: function (index) {
                var tableSelect = table.checkStatus('device_table').data;
                var list = [];
                $.each(tableSelect,function(ii,vv){
                    list.push({
                        "objType": siteObj.type,
                        "objId": siteObj.id,
                        "equipId": vv.equipId
                    });
                });
                load();
                request.service({
                    method: 'post',
                    url: '/equip/deleteEquip',
                    data:list
                }).then(function (res) {
                    disLoad();
                    getDeviceTabData();
                    layer.close(index);
                }).catch(function(err){
                    disLoad();
                    console.log(err)
                });
            },
            end: function(index){
                $("#equip_delete").addClass('layui-hide');
            }
        });
    }

    // 导入设备明细
    function importEquip(){
        layer.open({
            type: 1,
            title: '导入设备',
            closeBtn: 1,
            shade: 0,
            anim: 1,
            maxmin: true, //开启最大化最小化按钮
            area: ['500px', '300px'],
            content: $('#equip_import'),
            btn: ['确定', '取消'],
            success: function () {
                $("#equip_import").removeClass('layui-hide');
                $("#equip_import_report").html(siteObj.title);
            },
            yes: function (index) {
                var formData = new FormData($("#equip_import .layui-form")[0]);
                var file = formData.get('file');
                if(file.size == 0){
                    return layer.msg('请上传文件！');
                }
                formData.append("objType", siteObj.type);
                formData.append("objId", siteObj.id);
                load();
                request.service({
                    method: 'post',
                    url: '/equip/importExcel',
                    data:formData
                }).then(function (res) {
                    disLoad();
                    getDeviceTabData();
                    layer.close(index);
                }).catch(function(err){
                    disLoad();
                    console.log(err)
                });
            },
            end: function(index){
                $("#equip_import").addClass('layui-hide');
            }
        });
    }

    //初始函数
    function initFun() {
        laydate.render({
            elem: '#productionDate'
            ,value: new Date()
        });
        laydate.render({
            elem: '#firstUseDate'
            ,value: new Date()
        });
        getTree1();
    }
    var pageNum = 1;
    var pageLimit;
    function getDeviceTabData() { //ajax获取 数据
        var tnum = parseInt(($(".device_content").height() - 60) / 40); //动态生成表格展示条数
        var formData = {};
        formData.limit = pageLimit ? pageLimit : tnum;
        formData.page = pageNum;
        var key = {};

        var searchData = form.val("searchForm");
        if(searchData.range == 'equipSysId'){
            key.equipSysId = equipSysId;
        }else{
            key.equipSysId = '';
        }

        key.equipId = $("#search_equipId").val();
        key.equipName = $("#search_equipName").val();
        key.objType = siteObj.type;
        key.objId = siteObj.id;
        formData.key = key;
        load();
        request.service({
                method: 'post',
                url: '/equip/queryEquip',
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
            elem: '#device_table',
            id:'device_table',
            height: 'full-155',
            cols: [
                [{
                    type: "checkbox",
                    width: 50,
                    fixed: "left"
                }, {
                    type: "numbers",
                    align: "center",
                    width: 50,
                    title: '序号'
                }, {
                    field: 'equipId',
                    align: "center",
                    width: 100,
                    title: '设备标识'
                },
                {
                    field: 'equipName',
                    align: "center",
                    width: 120,
                    title: '设备名称'
                },
                {
                    field: 'equipSysName',
                    align: "center",
                    width: 140,
                    title: '所属系统'
                },
                {
                    field: 'manufacturer',
                    align: "center",
                    width: 150,
                    title: '厂家'
                },
                {
                    field: 'model',
                    align: "center",
                    width: 100,
                    title: '型号'
                },
                {
                    field: 'location',
                    align: "center",
                    width: 150,
                    title: '位置'
                },
                {
                    field: 'productionDate',
                    align: "center",
                    width: 120,
                    title: '生产日期'
                },
                {
                    field: 'firstUseDate',
                    align: "center",
                    width: 120,
                    title: '投用日期'
                },
                {
                    field: 'imgSuffix',
                    align: "center",
                    width: 100,
                    title: '设备铭牌',
                    toolbar:'#queryImage'
                },
                {
                    field: 'sortId',
                    align: "center",
                    width: 100,
                    title: '排序标识'
                },
                {
                    field: 'memo',
                    align: "center",
                    width: 100,
                    title: '备注'
                }, {
                    field: 'createUserId',
                    align: "center",
                    width: 100,
                    title: '创建者'
                }, {
                    field: 'createTime',
                    align: "center",
                    width: 150,
                    title: '创建时间'
                }, {
                    field: 'updateTime',
                    align: "center",
                    title: '修改者',
                    width: 100
                }, {
                    field: 'updateUserId',
                    align: "center",
                    title: '修改时间',
                    width: 150
                }]
            ],
            data: data,
            page: false,
            limit: limit
        });
        //监听工具条
        table.on('tool(device_table)', function(obj){
            var data = obj.data; //获得当前行数据
            var layEvent = obj.event;

            if(layEvent === 'detail'){ //查看
                load();
                request.service({
                    method: 'post',
                    url: '/equip/queryImage',
                    data: {
                        'objType': siteObj.type,
                        'objId': siteObj.id,
                        'equipId':data.equipId
                    }
                }).then(function (res) {
                    disLoad();
                    showImage(res);
                }).catch(function(err){
                    disLoad();
                    console.log(err)
                });
            }
        });
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
                    getDeviceTabData();
                }
            }
        });

    }

    function showImage(res){
        layer.open({
            type: 1,
            title: '设备铭牌',
            shade: 0,
            anim: 1,
            maxmin: true, //开启最大化最小化按钮
            area: ['500px', '500px'],
            content: '<div id="showImage"><img src="data:image/png;base64,'+res.one.imgBase64+'" /></div>'
        });
    }


    initFun(); //页面加载时执行初始函数












});