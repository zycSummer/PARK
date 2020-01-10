/*
 * @Author: xzl 
 * @Date: 2019-11-07 10:42:32 
 * @Last Modified by: xzl
 * @Last Modified time: 2019-12-02 11:19:09
 */
layui.config({
    base: "public/js/",
    version: true
}).extend({
    layuimini: "layuimini"
}).use(['request','element', 'layer', 'layuimini'], function () {
    var $ = layui.jquery,
        element = layui.element,
        layer = layui.layer;

    var indexLoading;    
        function load() { //加载事件
            indexLoading = layer.load(1, {
                shade: [0.3, '#fff']
            });
        }
    
        function disLoad() { //取消加载事件
            layer.close(indexLoading);
        }
    
   function initMenu(){  //初始化菜单
    
    load();
    request.service({
        method: 'get',
        url: '/common/getCurrentMenuTree'
    })
    .then(function (res) {
         disLoad();
        var  menuObj ={};
       var menuArr =res.data;
       if(menuArr.length == 0){
            layer.msg("无菜单权限");
             window.location = '/logout';
            return;
       }
     
          var  homeObj =menuArr[0];
             if(homeObj.children){
                menuObj.homeInfo =homeObj.children[0];
             }
             else
             {
                menuObj.homeInfo =homeObj;;   
             }
         var  menuInfo={};
         var  currency={};
         currency.child=menuArr;
         menuInfo. currency =currency;
         menuObj.menuInfo =menuInfo;
         layuimini.init(menuObj);
    })
    .catch(function (err) {
      console.log(err);
    })
   }
   initMenu();


    $('.login-out').on("click", function () { //登出点击事件
        layer.confirm('确认退出系统？', {
            btn: ['确定', '取消'] //按钮
        }, function () {
            window.location = '/logout';
        }, function () {

        });
    });
    $('.userUpdarte').on("click", function () { //修改密码点击事件
        layer.open({
            type: 1,
            title:"修改密码",
            shade: 0.3,
            anim: 1,
            maxmin: true, //开启最大化最小化按钮
            area: ['550px', '300px'],
            content: $('#changePwd'),
            btn: ['确定', '取消'],
            success: function () {
                $("#changePwd").removeClass('layui-hide').addClass('layui-show');
            },
            yes: function (index) {

                var   newPwd= $("#newPassword").val();
                var   confirmPw= $("#password").val();
                 if(newPwd != confirmPw){
                     return layer.msg("两次输入的密码不一致，请重新输入！")
                 }
                var formData = {
                    oldPwd: $("#nurPassword").val(),
                    newPwd: $('#newPassword').val(),
                  
                };
                request.service({
                        method: 'post',
                        url: '/user/updatePwd',
                        data: formData
                    })
                    .then(function (res) {
                        layer.close(index);
                       layer.msg(res.msg); 
                      
                    })
                    .catch(function () {

                    })
            },
            btn2: function (index, layero) {
                // console.log('取消');
            },
            end: function (index) {
                $("#changePwd").removeClass('layui-show').addClass('layui-hide');
            }
        });
    });
});
