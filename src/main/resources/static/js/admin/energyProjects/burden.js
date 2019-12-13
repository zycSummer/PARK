/*
 * @Author: xzl 
 * @Date: 2019-10-25 14:52:08 
 * @Last Modified by: xzl
 * @Last Modified time: 2019-11-08 16:05:24
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
  var energyTypeId;

  function load() { //加载事件
    indexLoading = layer.load(1, {
      shade: [0.3, '#fff']
    });
  }

  function disLoad() { //取消加载事件
    layer.close(indexLoading);
  }

  function initFun() { //初始化加载事件
    renderChartTitle(); //渲染标题
    renderEnergyType(); //渲染能源类型
  }

  function renderChartTitle() { //渲染表格头部
    if (sessionStorage.getItem('parkId')) {
      var objSiteType = JSON.parse(sessionStorage.getItem("parkId")).type;
      switch (objSiteType) {
        case 'PARK':
          $("#chart_head_title").text(JSON.parse(sessionStorage.getItem("parkId")).name + ' - 实时负荷')
          break;

        default:
          $("#chart_head_title").text(JSON.parse(sessionStorage.getItem("parkId")).title + ' - 实时负荷')
          break;
      }
    }
    $(".head_date").text(moment().add('days', 0).format('YYYY-MM-DD'))
  }

  function renderEnergyType() { //渲染能源类型选择
    request.service({
        method: 'get',
        url: '/common/queryHistoryLeftData',
      })
      .then(function (res) {
        var typeData = res.data;
        var tpyeHtml;
        if (typeData.length > 0) {
          typeData.forEach(function (tpye, index) {
            switch (index) {
              case 0:
                tpyeHtml += '<a  class="typeObj layui-this"   data-objid="' + tpye.energyTypeId + '">' + tpye.energyTypeName + '</q>'
                energyTypeId = tpye.energyTypeId;
                break;

              default:
                tpyeHtml += '<a  class="typeObj"   data-objid="' + tpye.energyTypeId + '">' + tpye.energyTypeName + '</q>'
                break;
            }
          });

        }
        $("#typeBreacrumb").html(tpyeHtml);
        element.render();
        getChartData(); //初始化图表
      }).catch(function (err) {
        console.log(err);
      })

  }
  //能源类型选择点击事件
  $(document).on("click", '.typeObj', function () {
    $(this).siblings().removeClass('layui-this');
    $(this).addClass('layui-this');
    energyTypeId = $(this).data('objid');
    getChartData();
  });

  function getChartData() { //请求获取图表的数据
    var formData = {};
    formData.objType = JSON.parse(sessionStorage.getItem("parkId")).type;
    formData.objId = JSON.parse(sessionStorage.getItem("parkId")).id;
    formData.energyTypeId = energyTypeId; //能源类型
    load();
    request.service({
        method: 'post',
        url: '/projectEnergyConsume/realTimeLoad',
        data: formData
      })
      .then(function (res) {
        disLoad();

        var vals = res.one.values;
        var times = res.one.timestamps;
        if (times.length > 0) {
          for (let i = 0; i < times.length; i++) {
            times[i] = moment(times[i]).format('HH:mm');
          }
        }
        if(vals.length>0){
          for (let j = 0; j < vals.length; j++) {
            if(vals[j]){
              vals[j] = vals[j].toFixed(2);
            }
          
          }
        }
        var tableData = [];
        var objTab = {};
        objTab.avg = res.one.avg;
        objTab.maxTime = res.one.maxTime;
        objTab.maxVal = res.one.maxVal;
        objTab.minTime = res.one.minTime;
        objTab.minVal = res.one.minVal;
        tableData.push(objTab);
        renderChart(vals, times);
        renderChartTable(tableData);
      }).catch(function (err) {
        console.log(err);
      })


  }

  function renderChart(vals, times) { //根据获取的数据渲染折线图
    var myChart = echarts.init(document.getElementById('chartMain'));
    var option = {
      color: ['#359159'],
      tooltip: {
        trigger: 'axis'
      },

      grid: {
        left: 15,
        right: '2%',
        bottom: 10,
        top: 30,
        containLabel: true
      },
      xAxis: {
        type: 'category',
        boundaryGap: false,
        data: times
      },
      yAxis: {
        name: '实时负荷(Kw)',
        type: 'value',
        scale: true,
      },
      dataZoom: [{
        type: 'inside',

      }, ],
      series: [{
        name: '实时负荷',
        type: 'line',
        stack: '总量',
        data: vals
      }]
    };
    myChart.setOption(option);
  }

  function renderChartTable(data) { //渲染表格
    table.render({
      elem: '#chart_tab',
      cols: [
        [{
            field: 'avg',
            minWidth: 150,
            title: "平均值",
            align: 'center',
            rowspan: 2
          },
          {

            minWidth: 150,
            title: "最大值",
            align: 'center',
            colspan: 2
          },
          {

            minWidth: 150,
            title: "最小值",
            align: 'center',
            colspan: 2
          }
        ],
        [{
          field: 'maxVal',
          title: '数值',
          align: 'center'
        }, {
          field: 'maxTime',
          title: '时间',
          align: 'center'
        }, {
          field: 'minVal',
          title: '数值',
          align: 'center'
        }, {
          field: 'minTime',
          title: '时间',
          align: 'center'
        }]
      ],
      data: data

    })
  }
  initFun(); //页面加载时执行初始函数
});