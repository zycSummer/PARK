/*
 * @Author: jpp
 * @Date: 2019-10-22 17:07:34
 * @Last Modified by: jpp
 * @Last Modified time: 2019-11-07 13:37:35
 */

layui.use(['request','form', 'element', 'layer', 'jquery','laydate'], function () {
    var form = layui.form,
        layer = layui.layer,
        laydate = layui.laydate,
        $ = layui.jquery,
        element = layui.element;

    var siteObj = JSON.parse(sessionStorage.getItem('parkId'));
    var tree1;//左侧具体节点
    var editor,name='',isLoad = false;//图纸id，图纸name

    funcsAuthority();

    //新增
    $('.add').on('click',function(){
        $('.add_htImgId').removeClass('layui-hide');
        $('.edit_htImgId').addClass('layui-hide');
        $("#htImgId").val('');
        $("#isMainPage").attr('checked',false);
        $("#memo").val('');
        add('add','新增综合展示画面');
    });

    $('.edit').on('click',function(){
        var val = tree1.getSelectedNodes();
        if(val.length == 0) return layer.msg('请选择综合展示画面');
        $('.edit_htImgId').removeClass('layui-hide');
        $('.add_htImgId').addClass('layui-hide');
        $('#edit_htImgId').html(val[0].htImgId);
        if(val[0].isMainPage == 'Y'){
            $("#isMainPage").attr('checked',true);
        }else{
            $("#isMainPage").attr('checked',false);
        }
        $("#memo").val(val[0].memo);
        add('edit','修改综合展示画面');

    });

    $('.delete').on('click',function(){
        var val1 = tree1.getSelectedNodes();
        if(val1.length == 0) return layer.msg('请选择要删除的综合展示画面');
        $("#de_htImgId").html(val1[0].htImgId);
        if(val1[0].isMainPage == 'Y'){
            $("#de_isMainPage").html('是');
        }else{
            $("#de_isMainPage").html('否');
        }
        deletes();
    });

    $('.setup').on('click',function(){
        var val1 = tree1.getSelectedNodes();
        if(val1.length == 0) return layer.msg('请选择要设定为主页的综合展示画面');
        $("#set_htImgId").html(val1[0].htImgId);
        setupPage();
    });

    //新增弹框 提示点击 - 综合展示画面标识
    $("#htImgId_tip").on("click", function () {
        layer.tips('字母、数字组合，长度不超过20。', '#htImgId_tip');
    });
    //新增弹框 提示点击 - 是否为主页
    $("#isMainPage_tip").on("click", function () {
        layer.tips('综合展示模块默认展示设置为主页的综合展示画面。', '#isMainPage_tip');
    });
    /*----------------------------------函数------------------------------------*/
    function add(type,title){
        layer.open({
            type: 1,
            title: title,
            closeBtn: 1,
            shade: 0,
            anim: 1,
            maxmin: true, //开启最大化最小化按钮
            area: ['550px', '350px'],
            content: $('#addForm'),
            btn: ['确定', '取消'],
            success: function () {
                $("#addForm").removeClass('layui-hide');
                form.render();
            },
            yes: function (index) {
                var val = tree1.getSelectedNodes();
                var isMainPageArr = $('#isMainPage:checked');
                var isMainPage = 'N';
                if(isMainPageArr.length > 0){
                    isMainPage = 'Y';
                }
                if(type == 'edit'){
                    var data = {
                        "id":val[0].id,
                        "objType": siteObj.type,
                        "objId":siteObj.id,
                        "htImgId":val[0].htImgId,
                        "cfgPic":val[0].cfgPic,
                        "isMainPage": isMainPage,
                        "memo": $("#memo").val()
                    }
                }else if(type == 'add'){
                    var data = {
                        "objType": siteObj.type,
                        "objId":siteObj.id,
                        "htImgId":$("#htImgId").val(),
                        "isMainPage": isMainPage,
                        "memo": $("#memo").val()
                    };

                    if(data.htImgId == '') return layer.msg('综合展示画面标识不能为空！');
                    if(data.htImgId.length > 20) return layer.msg('综合展示画面标识长度不能超过20！');

                    var re = /^[0-9a-zA-Z]+$/g;
                    if(!(re.test(data.htImgId))){
                        return layer.msg('综合展示画面标识为字母、数字组合！');
                    }
                }

                var url = data.id?'/comprehensiveShow/updateSiteImg':'/comprehensiveShow/insertSiteImg';
                load();
                request.service({
                    method: 'post',
                    url: url,
                    data: data
                }).then(function (res) {
                    disLoad();
                    layer.msg(res.msg);
                    layer.close(index);
                    getTree1();
                }).catch(function(err){
                    disLoad();
                    console.log(err)
                });
            },
            end: function(index){
                $("#addForm").addClass('layui-hide');
            }
        });
    }

    function deletes(){
        var val = tree1.getSelectedNodes();
        layer.open({
            type: 1,
            title: '删除综合展示画面',
            closeBtn: 1,
            shade: 0,
            anim: 1,
            maxmin: true, //开启最大化最小化按钮
            area: ['400px', '300px'],
            content: $('#deleteForm'),
            btn: ['确定', '取消'],
            success: function () {
                $("#deleteForm").removeClass('layui-hide');
            },
            yes: function (index) {
                load();
                request.service({
                    method: 'post',
                    url: '/comprehensiveShow/deleteSiteImg',
                    data: {
                        'objType': siteObj.type,
                        'objId': siteObj.id,
                        'htImgId':val[0].htImgId
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
                $("#deleteForm").addClass('layui-hide');
            }
        });
    }

    function setupPage(){
        var val = tree1.getSelectedNodes();
        var isMainPage = 'N';
        if(val[0].isMainPage == 'Y'){
            isMainPage = 'N';
        }else{
            isMainPage = 'Y';
        }
        layer.open({
            type: 1,
            title: '设定主页',
            closeBtn: 1,
            shade: 0,
            anim: 1,
            maxmin: true, //开启最大化最小化按钮
            area: ['400px', '250px'],
            content: $('#setupForm'),
            btn: ['确定', '取消'],
            success: function () {
                $("#setupForm").removeClass('layui-hide');
            },
            yes: function (index) {
                load();
                request.service({
                    method: 'post',
                    url: '/comprehensiveShow/updateSiteImg',
                    data: {
                        "id":val[0].id,
                        "objType": siteObj.type,
                        "objId":siteObj.id,
                        "htImgId":val[0].htImgId,
                        "cfgPic":val[0].cfgPic,
                        "isMainPage": isMainPage,
                        "memo": val[0].memo
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
                $("#setupForm").addClass('layui-hide');
            }
        });
    }

    function getTree1(){
        if(siteObj){
            load();
            request.service({
                method: 'post',
                url: '/comprehensiveShow/queryAllSiteImg',
                data: {
                    'objType': siteObj.type,
                    'objId': siteObj.id
                }
            }).then(function (res) {
                disLoad();
                if(res.data){
                    var zNodes1 = res.data;
                    if(zNodes1.length>0) {
                        var setting = {
                            view: {
                                addDiyDom:null,//用于在节点上固定显示用户自定义控件
                                autoCancelSelected: false,
                                dblClickExpand: true,//双击节点时，是否自动展开父节点的标识
                                showLine: false,//设置 zTree 是否显示节点之间的连线。
                                selectedMulti: false
                            },
                            data: {
                                keep:{
                                    leaf:false,//zTree 的节点叶子节点属性锁，是否始终保持 isParent = false
                                    parent:false//zTree 的节点父节点属性锁，是否始终保持 isParent = true
                                },
                                key:{
                                    children:"childs",//节点数据中保存子节点数据的属性名称。
                                    name:"htImgId",//节点数据保存节点名称的属性名称。
                                    title:"id"//节点数据保存节点提示信息的属性名称
                                },
                                simpleData: {
                                    enable: true,//是否采用简单数据模式 (Array)
                                    idKey: "id",//节点数据中保存唯一标识的属性名称。
                                    pIdKey: "parentId",//节点数据中保存其父节点唯一标识的属性名称
                                    rootPId: ""//用于修正根节点父节点数据，即 pIdKey 指定的属性值。
                                }
                            },
                            callback: {
                                onClick:function(event,treeId,treeNode){
                                    getChartData();
                                }
                            }
                        };
                        tree1 = $.fn.zTree.init($("#tree"), setting, zNodes1);
                        tree1.expandAll(true);
                        tree1.selectNode(tree1.getNodeByParam("id", zNodes1[0].id));
                        getChartData();
                    }else{
                        $("#tree").html('');
                        editor.closeTab();
                        return layer.msg('没有树结构');
                    }
                }
            }).catch(function(err){
                disLoad();
                console.log(err)
            });
        }
    }

    function getChartData(){
        var val1 = tree1.getSelectedNodes();
        if(siteObj){
            load();
            request.service({
                method: 'post',
                url: '/comprehensiveShow/queryAllSiteImgByHtImgId',
                data: {
                    'objType': siteObj.type,
                    'objId': siteObj.id,
                    'htImgId': val1[0].htImgId
                }
            }).then(function (res) {
                disLoad();
                if(res.one){
                    var data = null;
                    if(res.one.cfgPic){
                        data = res.one.cfgPic;
                        name = val1[0].htImgId;
                    }
                    isLoad = true;
                    handleEditorCreated(data,name);
                }
            }).catch(function(err){
                disLoad();
                console.log(err)
            });
        }
    }

    function handleEditorCreated(data,name) {
        // addImageItem(editor.assets.tree, 'editor.assets.tree');
        // addImageItem(editor.assets.list, 'editor.assets.list');
        var mainTabView = editor.mainTabView,
            tabModel2 = mainTabView.getTabModel();console.log(name)
        if(data){
            editor.openByJSON('display', '', name, data);console.log(name)
        }else{
            editor.newDisplayView(name);// 创建一个新图纸
        }
        editor.closeOtherTabs();
        tabModel2.each(function(data1){
            data1._closable = false;
            data1.setTag(data1.getName());
        });

        editor.mainMenu.showOnView(editor.mainToolbar, 4, 20);
        editor.mainMenu.hide();
        editor.mainMenu.getItemById('newDisplayView').disabled = true;
        editor.assets.list.menu.setItemVisible('open',true);

        var leftSplitView = editor.leftSplitView,
            leftTopTabView = editor.leftTopTabView,
            tabModel = leftTopTabView.getTabModel(),
            displaysTab;
        tabModel.each(function(data){
            if(data.getView() === editor.displays) {
                displaysTab = data;
            }
        });
        tabModel.remove(displaysTab);
        leftTopTabView.select(tabModel.getDatas().get(0));

        // var view = editor.displayView.getView();
        // view.style.background = '#000';

        window.addEventListener('resize', function() {
            editor.mainPane.iv();
        });


    }

    function funcsAuthority(){
        var urls = hteditor_config.subConfigs || [];
        urls.push('client.js');
        ht.Default.loadJS(urls, function(){
            urls = [
                'locales/' + hteditor.config.locale + '.js',
                'custom/locales/' + hteditor.config.locale + '.js'
            ];
            urls.push(hteditor.init);
            if (hteditor.config.libs) {
                urls = urls.concat(hteditor.config.libs);
            }
            urls.push('vs/loader.js');
            urls.push('vs/editor/editor.main.nls.js');
            urls.push('vs/editor/editor.main.js');
            ht.Default.loadJS(urls, function(){
                window.editor = hteditor.createEditor({
                    container: 'editor1',
                    onEditorCreated: function(editor1) {
                        editor = editor1;
                        getTree1();
                        //通过添加监听器拦截图纸、图标和组件的保存事件，该例子将数据存于内容
                        editor.addEventListener(function(event){
                            var params = event.params;
                            if (event.type === 'displayViewSaving') {
                                params.preventDefault = true;// 阻止默认的文件存储过程
                                params.displayView.dirty = false;// 清除图纸页签上标示图纸 dirty 的标志
                                if(isLoad == true){
                                    var val1 = tree1.getSelectedNodes();
                                    var data = {
                                        "id":val1[0].id,
                                        "objType": siteObj.type,
                                        "objId":siteObj.id,
                                        "htImgId": val1[0].htImgId,
                                        "cfgPic":editor.dm.serialize(),
                                        "isMainPage": val1[0].isMainPage,//默认大屏
                                        "memo": val1[0].memo
                                    };
                                    // var data = {
                                    //     "objType": siteObj.type,
                                    //     "objId":siteObj.id,
                                    //     "htImgId":'',
                                    //     "filePath":filePath,
                                    //     "cfgPic":editor.dm.serialize(),
                                    //     "isMainPage": 'N',//默认大屏
                                    //     "memo": params.displayView.tab._name
                                    // };
                                    load();
                                    request.service({
                                        method: 'post',
                                        url: '/comprehensiveShow/updateSiteImg',
                                        data: data
                                    }).then(function (res) {
                                        disLoad();
                                        window.addEventListener('resize', function() {
                                            editor.mainPane.iv();
                                        });
                                    }).catch(function(err){
                                        disLoad();
                                        console.log(err)
                                    });
                                }
                            }
                            if(event.type === 'displayViewNewNameInputing'){
                                params.name = name;
                                // params.preventDefault = true;
                            }
                        });
                    }
                });
            });
        });
    }

    var indexLoading;
    function load(){
        indexLoading =layer.load(1, {shade: [0.3, '#fff']});
    }
    function disLoad(){
        layer.close(indexLoading);
    }
});