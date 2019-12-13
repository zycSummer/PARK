/*
 * @Author: xzl 
 * @Date: 2019-10-23 10:16:48 
 * @Last Modified by: xzl
 * @Last Modified time: 2019-12-06 15:11:17
 * 公共组件 js
 */
layui.config({
	base: "/public/js/",
	version: true
}).extend({
	request: "request"
}).use(['request', 'form', 'element', 'layer', 'jquery','tree'], function () {
	var form = layui.form,
		layer = layui.layer,
		$ = layui.jquery,
		tree =layui.tree,
		element = layui.element;
	var indexLoading;

	function load() {  //load加载事件
		indexLoading = layer.load(1, {
			shade: [0.3, '#fff']
		});
	}

	function disLoad() { //关闭加载事件
		layer.close(indexLoading);
	}
      

	function initPark(){
		if(sessionStorage.getItem('parkId')){
			renderParkName()
		}
		else{
			getParkList()
		}
		getRtdbPark();
	}
	initPark();
	//获取park
	var treeDiaLog; //定义园区选中框类
	function  getParkList(){
		request.service({
			method: 'get',
			url: '/common/queryParkOrSite',
		})
		.then(function (res) {
		 var  parkList = res.data;
		if(parkList.length ==0 ){
			return layer.msg('没有相关权限！');
		}
		if(!sessionStorage.getItem('parkId')){  //初始化  默认渲染第一个
			sessionStorage.setItem('parkId',JSON.stringify(parkList[0]));
		}
		renderParkName();
		 tree.render({
			elem: '#park_select_tree'  //绑定元素
			,data: parkList
			 ,click: function(obj){
				 sessionStorage.setItem('parkId',JSON.stringify(obj.data));
				 $(".park_select").text(obj.data.title)
				 layer.close(treeDiaLog);
				 window.location.reload();
			 }
		  });
		})
		.catch(function(){

		})
	}
	$(".park_select").on("click", function () { // 园区选择事件
	    treeDiaLog=	layer.open({
			type: 1,
			title:"对象选择",
			shade: 0.3,
            maxmin: true,
			area: ['300px', '350px'], //宽高
			content: '<div id="park_select_tree"></div>',
			success: function () {
				getParkList()
			}
		})
	})

	//获取园区标识对应的实时库租户标识
	function getRtdbPark(){
		load();
		request.service({
			method: 'get',
			url: '/common/getRtdbTenantId'
		}).then(function (res) {
			disLoad();
			sessionStorage.setItem('parkRtdb',res.one);
		}).catch(function(err){
			disLoad();
			console.log(err)
		});
	}

	//获取所有园区 和园区下的公司企业
	function renderParkName() {
		$(".park_select").text(JSON.parse(sessionStorage.getItem('parkId')).title)
	}

    //数组去重
    function unique (arr) {
		return Array.from(new Set(arr))
		}

		 //递归循环菜单数据
		  function recursiveData(data){
			  var checkedData =[];
			data.forEach(function(item,index){
				if(item.checked){
				  checkedData.push(item.id);
				}

			  if(data.children){
				recursiveData(data.children)
			  }
			})
			return checkedData;
		  }




});