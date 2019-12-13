/*
 * @Author: xzl 
 * @Date: 2019-10-17 14:38:00 
 * @Last Modified by: xzl
 * @Last Modified time: 2019-12-10 14:27:07
 */
layui.use(['form', 'element', 'layer', 'table', 'jquery','laypage','laydate'], function () {
    var form = layui.form,
        layer = layui.layer,
        $ = layui.jquery,
        table = layui.table,
        laypage = layui.laypage,
        laydate   =  layui.laydate,
        element = layui.element;


var startTime  = moment().add('days', -1).format('YYYY-MM-DD 00:00:00');
var  endTime  =  moment().add('days', 1).format('YYYY-MM-DD 00:00:00');
 var indexLoading;
   function load(){
    indexLoading =layer.load(1, {shade: [0.3, '#fff']});
   }
   function disLoad(){
       layer.close(indexLoading);
   }
    //查询点击事件
    $("#search_btn").on("click", function () {
        getRoleTabData()
    })

    //初始函数
    function initFun() {
        laydate.render({   //渲染开始时间
            elem: '#search_start_time',
            value:startTime,
            type:'datetime',
            done:function(value){
                startTime =value 
            }
          });
          laydate.render({  //渲染结束时间
            elem: "#search_end_time",
            value:endTime,
            type:'datetime',
            done:function(value){
                endTime =value 
            }
          });  
      getRoleTabData();
    }
      var pageNum =1;
      var pageLimit;
     function getRoleTabData(){  //ajax获取 数据
        if(startTime && endTime){
             if(startTime >endTime){
             return layer.msg("结束时间不能大于开始时间")
            }
        }
        var tnum = parseInt(($(".tab_content").height() - 60) / 40);  //动态生成表格展示条数
         var formData ={};
         formData.limit =pageLimit ? pageLimit : tnum;
         formData.page =pageNum;
         var key ={};
         key.useId =$("#search_userId").val()  || null;
         key.beginTime = moment(startTime).valueOf();
         key.endTime =  moment(endTime).valueOf();
         key.code = $("#role_code").val();
         key.name = $("#role_name").val();
         formData.key = key;
          load();
          request.service({
                method: 'post',
                url: '/sysLog/query',
                data: formData
            })
            .then(function (res) {
              disLoad();
              var  data =res.data;
              if(data.length>0){
               
              }
             renderTableContent(data,formData.limit);
             renderPage(res.count,tnum);
            })
            .catch(err => {
             console.log(err)
            })
       
     }
     
    //渲染表格数据
    function renderTableContent(data,limit) {
        table.render({
            elem: '#role_table',
            height:'full-155',
            cols: [
                [
                    {
                        field: 'userId',
                         align:"center",
                        title: '用戶标识'
                    },
                    {
                        field: 'operateTime',
                      align:"center",
                        title: '操作时间'
                     
                    },
                    {
                        field: 'menu',
                      align:"center",
                        title: '操作菜单'
                     
                    },
                    {
                        field: 'function',
                      align:"center",
                        title: '操作菜单功能'
                     
                    },
                    {
                        field: 'method',
                      align:"center",
                        title: '请求方式'
                     
                    },
                    {
                        field: 'url',
                      align:"center",
                        title: '接口地址'
                     
                    }, {
                        field: 'operateContent',
                      align:"center",
                        title: '操作内容'
                     
                    },
                    {
                        field: 'result',
                      align:"center",
                        title: '操作结果'
                     
                    },
                    {
                        field: 'memo',
                        align:"center",
                        title: '备注'
                    }
                ]
            ],
            data: data,
            page:false,
            limit:limit
        })
        
    }
    //渲染分页模块
    function renderPage(count,tnum) {
        laypage.render({
            elem: 'table_page',
            count: count, //数据总数，从服务端得到
            curr:pageNum,
            limit:pageLimit ? pageLimit : tnum,
            limits:[tnum,20,30,50],
            layout: ['count', 'prev', 'page', 'next', 'limit', 'skip'],
            jump: function (obj, first) {
                //首次不执行
                if (!first) {
                pageNum =obj.curr;
                pageLimit =obj.limit;
                getRoleTabData();
                }
            }
        });

    }

     $("#export_btn").on("click",function(){
         var userId =$("#search_userId").val();
        if(startTime && endTime){
            if(startTime >endTime){
            return layer.msg("结束时间不能大于开始时间")
           }
       }
       var otherUrl='';  //判断用户标识是否存在
       if(userId){
        otherUrl = "&userId="+userId
       }
       window.location.href="/sysLog/exportExcel?start="+moment(startTime).valueOf()+"&end="+moment(endTime).valueOf()+otherUrl
     })
    initFun(); //页面加载时执行初始函数
   


 
  

  

 



});