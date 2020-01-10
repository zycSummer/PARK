

/*
 * @Author: xzl 
 * @Date: 2019-10-31 15:57:12 
 * @Last Modified by: xzl
 * @Last Modified time: 2019-12-16 10:06:23
 */
layui.use(['form', 'element', 'layer', 'jquery', 'table', 'laypage', 'laydate'], function () {
  var form = layui.form,
      layer = layui.layer,
      $ = layui.jquery,
      table = layui.table,
      laypage = layui.laypage,
      laydate = layui.laydate,
      element = layui.element;
  var indexLoading; //定义loading 类

  var energyTypeId; //默认能源种类

  function load() {
    //加载事件
    indexLoading = layer.load(1, {
      shade: [0.3, '#fff']
    });
  }

  function disLoad() {
    //取消加载事件
    layer.close(indexLoading);
  }

  function initFun() {
    //初始化加载事件
    renderChartTitle();
    renderEnergyType();
  }

  function renderEnergyType() {
    //渲染能源类型选择
    request.service({
      method: 'get',
      url: '/common/queryHistoryLeftData'
    }).then(function (res) {
      var typeData = res.data;
      var tpyeHtml;

      if (typeData.length > 0) {
        typeData.forEach(function (tpye, index) {
          switch (index) {
            case 0:
              tpyeHtml += '<a  class="typeObj layui-this"   data-objid="' + tpye.energyTypeId + '">' + tpye.energyTypeName + '</q>';
              energyTypeId = tpye.energyTypeId;
              break;

            default:
              tpyeHtml += '<a  class="typeObj"   data-objid="' + tpye.energyTypeId + '">' + tpye.energyTypeName + '</q>';
              break;
          }
        });
      }

      $("#typeBreacrumb").html(tpyeHtml);
      element.render();
      getChartData();
    })["catch"](function (err) {
      console.log(err);
    });
  }

  function renderChartTitle() {
    //渲染表格头部
    if (sessionStorage.getItem('parkId')) {
      var objSiteType = JSON.parse(sessionStorage.getItem("parkId")).type;

      switch (objSiteType) {
        case 'PARK':
          $("#chart_head_title").text(JSON.parse(sessionStorage.getItem("parkId")).name + ' - 负荷排名');
          break;

        default:
          $("#chart_head_title").text(JSON.parse(sessionStorage.getItem("parkId")).title + ' - 负荷排名');
          break;
      }
    }

    $(".head_date").text(moment().add('days', 0).format('YYYY-MM-DD'));
  } //能源类型选择点击事件


  $(document).on("click", '.typeObj', function () {
    $(this).siblings().removeClass('layui-this');
    $(this).addClass('layui-this');
    energyTypeId = $(this).data('objid');
    getChartData();
  });

  function getChartData() {
    //请求获取图表的数据
    var formData = {};
    formData.objType = JSON.parse(sessionStorage.getItem("parkId")).type;
    formData.objId = JSON.parse(sessionStorage.getItem("parkId")).id;
    formData.energyTypeId = energyTypeId; //能源类型

    formData.timestamp = moment(moment().add('days', 0).format('YYYY-MM-DD')).valueOf();
    load();
    request.service({
      method: 'post',
      url: '/projectEnergyConsume/rank',
      data: formData
    }).then(function (res) {
      disLoad();
      var list = res.one.list;
      var timeList = [];
      var valList = [];

      if (list.length > 0) {
        list.forEach(function (item, index) {
          timeList.push(item.name);

          if (!item.val) {
            valList.push('');
          } else {
            valList.push(item.val);
          }
        });
      }

      renderChart(valList, timeList, res.one.name, res.one.unit);
    })["catch"](function (err) {
      console.log(err);
    });
  }

  function renderChart(valList, timeList, name, unit) {
    //根据获取的数据渲染折线图
    var myChart = echarts.init(document.getElementById('chartMain'));
    var option = {
      color: ['#359159'],
      tooltip: {
        trigger: 'axis',
        axisPointer: {
          // 坐标轴指示器，坐标轴触发有效
          type: 'shadow' // 默认为直线，可选为：'line' | 'shadow'

        }
      },
      xAxis: {
        type: 'category',
        data: timeList //   axisLabel: {  
        //     interval:0,  
        //     rotate:40  
        // }  

      },
      grid: {
        left: 35,
        right: '2%',
        bottom: 10,
        top: 35,
        containLabel: true
      },
      dataZoom: [{
        type: 'inside'
      }],
      yAxis: {
        name: name + '（' + unit + '）',
        type: 'value'
      },
      series: [{
        name: '实时功率',
        data: valList,
        type: 'bar',
        smooth: true
      }]
    };
    myChart.setOption(option);
  }

  initFun(); //页面加载时执行初始函数
});