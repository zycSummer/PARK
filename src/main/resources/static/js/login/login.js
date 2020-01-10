

layui.config({
  base: "public/js/",
  version: true
}).extend({
  request: "request"
}).use(['form', 'element', 'layer', 'request', 'jquery'], function () {
  var form = layui.form,
      layer = layui.layer,
      $ = layui.$,
      element = layui.element; // 登录过期的时候，跳出ifram框架

  if (top.location != self.location) top.location = self.location;

  document.onkeydown = function (e) {
    // 回车提交表单
    // 兼容FF和IE和Opera
    var theEvent = window.event || e;
    var code = theEvent.keyCode || theEvent.which || theEvent.charCode;

    if (code == 13) {
      loginEvent();
    }
  }; // 进行登录操作


  $('#loginBtn').click(function () {
    loginEvent();
  }); //登录点击事件

  function loginEvent() {
    var name = $("#loginUserName").val();
    var pwd = $("#loginUserPwd").val();

    if (!name) {
      layer.msg('用户名不能为空');
      return false;
    }

    if (!pwd) {
      layer.msg('密码不能为空');
      return false;
    }

    var formData = new FormData();
    formData.append('username', name);
    formData.append('password', pwd);
    formData.append('userGroup', "jet"); // formData.username =name;
    // formData.password = pwd;

    request.service({
      method: 'post',
      url: '/login',
      data: formData
    }).then(function (response) {
      window.location.href = '/';
    })["catch"](function () {});
  } //执行清除默认园区或者企业事件


  sessionStorage.removeItem('parkId');
});