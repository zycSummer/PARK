/*
 * @Author: xzl 
 * @Date: 2019-10-16 15:47:56 
 * @Last Modified by: xzl
 * @Last Modified time: 2019-11-28 16:28:11
 */

layui.define(["load", "layer"], function (exports) {
  var layer = layui.layer;
  var load = layui.load;
  load.config({
    axios: {
      path: '{/}/public/lib/axios/axios.min',
      then: function () {
        // then 回调非必须的，一般用来引入一些对应的css或者用来返回layui[modelName] = 被引入的插件的某些特殊的变量可以在then回调返回相关的值
        return window.axios;
      }
    }
  });
  request = new function () {
    load('axios', function () {
      var axios = layui.axios; // 创建axios实例
      const service = axios.create({
        timeout: 5000000 // 请求超时时间
      })

 

      var _this = void 0;
      
      // request拦截器
      service.interceptors.request.use(function (config) {
        config.headers['X-Requested-With'] ='XMLHttpRequest'
        return config;
      }, function (error) {
        // Do something with request error
        console.log(error); // for debug
      
        Promise.reject(error);
      }); // response 拦截器
      
      service.interceptors.response.use(function (response) {
        /**
         * code为非20000是抛错 可结合自己业务进行修改
         */
        var res = response.data;
        if(res.code== 0 || res.code){
        if (res.code != 0) {
          layer.msg('提示：'+res.msg, {icon: 2, shade: this.shade, scrollbar: false, time: 6000, shadeClose: true});
          layer.closeAll('loading'); //关闭所有loading

          //导入表格错误提示
          if(res.code == -1){
            layer.msg('提示：'+res.data.join('<br>'), {icon: 2, shade: this.shade, scrollbar: false, time: 6000, shadeClose: true});
          }
          //方法请求无权限
          if (res.code === 402) {
            layer.msg('提示：您无此方法权限', {icon: 2, shade: this.shade, scrollbar: false, time: 6000, shadeClose: true});

          }
          // 401:登录过期;
          if (res.code === 401) {
            layer.confirm('您已登出，是否重新登录？', {
              btn: ['确定', '取消'] //按钮
      
            }, function () {
              window.location = '/logout';
            }, function () {});
          }
        
          return Promise.reject('error');
        } else {
          return response.data;
        }
      }
      else{
        return response; 
      }
      }, function (error) {
        layer.msg("错误："+error, {icon: 2, shade: this.shade, scrollbar: false, time: 6000, shadeClose: true});
        layer.closeAll('loading'); //关闭所有loading
      
        return Promise.reject(error);
      });
      request.service =service;

    });


  }
  exports("request", request);
});