

/*
 * @Author: xzl 
 * @Date: 2019-11-04 15:58:56 
 * @Last Modified by: xzl
 * @Last Modified time: 2019-11-06 10:46:03
 */
layui.use(['form', 'element', 'layer', 'laydate', 'jquery', 'laypage', 'tree'], function () {
  var form = layui.form,
      layer = layui.layer,
      $ = layui.jquery,
      laydate = layui.laydate,
      element = layui.element;
  var indexLoading;
  var typeSelect = 'month'; //自定义时间类型

  var defaultDate; //默认时间

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

  $("#search_btn").on("click", function () {
    //查询点击事件
    getCoData();
  }); //初始函数

  function initFun() {
    renderSelectDateVal(); // 渲染值

    getCoData(); //初始渲染数据
  } //时间类型选择点击事件


  $(document).on("click", '.title_select', function () {
    $(this).siblings().removeClass('layui-this');
    $(this).addClass('layui-this');
    typeSelect = $(this).data('type');
    renderSelectDateVal();
  });

  function renderSelectDateVal() {
    //渲染时间 选择框值'  
    var selectId;

    switch (typeSelect) {
      case "month":
        defaultDate = moment().add('month', 0).format('YYYY-MM'); //获取当前月

        $("#dateSelectMonth").show();
        $("#dateSelectYear").hide();
        selectId = "#dateSelectMonth";
        break;

      default:
        defaultDate = moment().add('year', 0).format('YYYY'); //获取当年  

        $("#dateSelectMonth").hide();
        $("#dateSelectYear").show();
        selectId = "#dateSelectYear";
        break;
    }

    laydate.render({
      elem: selectId,
      value: defaultDate,
      type: typeSelect,
      done: function done(value) {
        defaultDate = value;
      }
    });
  }

  function getCoData() {
    //获取碳排数据
    var formData = {};
    formData.objType = JSON.parse(sessionStorage.getItem("parkId")).type;
    formData.objId = JSON.parse(sessionStorage.getItem("parkId")).id;
    formData.time = defaultDate; //所选时间

    formData.timeType = typeSelect;
    load();
    request.service({
      method: 'post',
      url: '/co2Manage/getData',
      data: formData
    }).then(function (res) {
      disLoad();
      renderChart(res.one.date, res.one.value);
    })["catch"](function (err) {
      console.log(err);
    });
  }

  function renderChart(dates, vals) {
    //渲染柱状图
    if (dates.length > 0) {
      for (var i = 0; i < dates.length; i++) {
        switch (typeSelect) {
          case 'month':
            dates[i] = dates[i] + '日';
            break;

          default:
            dates[i] = dates[i] + '月';
            break;
        }
      }
    }

    var chartLine = echarts.init(document.getElementById("chartLine"));
    var selectText;

    if (sessionStorage.getItem('parkId')) {
      var objSiteType = JSON.parse(sessionStorage.getItem("parkId")).type;

      switch (objSiteType) {
        case 'PARK':
          selectText = JSON.parse(sessionStorage.getItem("parkId")).name;
          break;

        default:
          selectText = JSON.parse(sessionStorage.getItem("parkId")).title;
          break;
      }
    }

    var option = {
      color: ['#359159'],
      tooltip: {
        trigger: 'axis',
        axisPointer: {
          // 坐标轴指示器，坐标轴触发有效
          type: 'shadow' // 默认为直线，可选为：'line' | 'shadow'

        }
      },
      title: {
        text: selectText + '- 碳排放量',
        subtext: defaultDate,
        x: 'center'
      },
      grid: {
        left: '2%',
        right: '4%',
        bottom: '3%',
        containLabel: true
      },
      xAxis: [{
        type: 'category',
        data: dates,
        axisTick: {
          alignWithLabel: true
        }
      }],
      yAxis: [{
        name: '碳排量(kg)',
        type: 'value'
      }],
      series: [{
        name: '碳排量',
        type: 'bar',
        barWidth: '60%',
        data: vals // label: {
        //     normal: {
        //         show: true,
        //         position: 'top'
        //     }}

      }]
    };
    chartLine.setOption(option);
  }

  initFun(); //页面加载时执行初始函数
});