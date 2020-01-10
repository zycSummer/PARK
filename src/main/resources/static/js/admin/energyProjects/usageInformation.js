

/*
 * @Author: xzl
 * @Date: 2019-11-01 16:14:02
 * @Last Modified by: xzl
 * @Last Modified time: 2019-12-02 10:25:33
 */
layui.use(["form", "element", "layer", "jquery", "table", "laypage", "laydate"], function () {
  var form = layui.form,
      layer = layui.layer,
      $ = layui.jquery,
      table = layui.table,
      laypage = layui.laypage,
      laydate = layui.laydate,
      element = layui.element;
  var indexLoading; //定义loading 类

  var energyTypeId; //定义能源种类

  var timeUnit = 'month'; //时间类型

  function load() {
    //加载事件
    indexLoading = layer.load(1, {
      shade: [0.3, "#fff"]
    });
  }

  function disLoad() {
    //取消加载事件
    layer.close(indexLoading);
  }

  function initFun() {
    //初始化加载事件
    renderHeadTitle();
    renderEnergyType();
    getleftChartData();
  }

  function renderHeadTitle() {
    //渲染界面标题
    if (sessionStorage.getItem("parkId")) {
      var objSiteType = JSON.parse(sessionStorage.getItem("parkId")).type;

      switch (objSiteType) {
        case "PARK":
          $(".content_main_title").text(JSON.parse(sessionStorage.getItem("parkId")).name + " - 用量信息");
          break;

        default:
          $(".content_main_title").text(JSON.parse(sessionStorage.getItem("parkId")).title + " - 用量信息");
          break;
      }
    }

    $(".date_p").text(moment().add('days', 0).format('YYYY-MM'));
  } //日期类型选择点击事件


  $(document).on("click", ".date_select", function () {
    $(this).siblings().removeClass("layui-this");
    $(this).addClass("layui-this");
    timeUnit = $(this).data('unit');

    switch (timeUnit) {
      case 'month':
        $("#left_date").text(moment().add('days', 0).format('YYYY-MM'));
        break;

      default:
        $("#left_date").text(moment().add('days', 0).format('YYYY'));
        break;
    }

    getleftChartData();
  });

  function getleftChartData() {
    //获取饼图数据
    var formData = {};
    formData.objType = JSON.parse(sessionStorage.getItem("parkId")).type;
    formData.objId = JSON.parse(sessionStorage.getItem("parkId")).id;
    formData.timeUnit = timeUnit; //日期类型

    load();
    request.service({
      method: 'post',
      url: '/projectEnergyConsume/usageInfoPie',
      data: formData
    }).then(function (res) {
      disLoad();
      var list = res.data;
      var nameList = [];

      if (list.length > 0) {
        list.forEach(function (item) {
          if (item.val) {
            item.value = item.val.toFixed(2);
          }

          if (item.compare) {
            item.compare = (item.compare * 100).toFixed(2);
          }

          nameList.push(item.name);
        });
      }

      renderLeftChart(list, nameList);
    })["catch"](function (err) {
      throw err;
    });
  }

  function renderLeftChart(list, nameList) {
    //渲染左侧饼图
    var leftChart = echarts.init(document.getElementById("content_left_chart"));
    var option = {
      tooltip: {
        trigger: "item",
        formatter: function formatter(params) {
          //自定义弹框显示
          return "<div><p>" + params.name + "</p><p>占比：" + params.data.compare + "%</p><p>用量：" + params.value + "</p></div>";
        }
      },
      legend: {
        orient: "vertical",
        left: "left",
        data: nameList
      },
      grid: {
        left: 15,
        right: 15,
        bottom: 10,
        top: 10,
        containLabel: true
      },
      series: [{
        type: "pie",
        radius: "55%",
        center: ["50%", "60%"],
        data: list,
        itemStyle: {
          emphasis: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: "rgba(0, 0, 0, 0.5)"
          }
        }
      }]
    };
    leftChart.setOption(option);
  } // ######################################   月度对比#######################################################################


  function renderEnergyType() {
    //渲染能源类型选择
    request.service({
      method: "get",
      url: "/common/queryHistoryLeftData"
    }).then(function (res) {
      var typeData = res.data;
      var tpyeHtml;

      if (typeData.length > 0) {
        typeData.forEach(function (tpye, index) {
          switch (index) {
            case 0:
              tpyeHtml += '<a  class="typeObj layui-this"   data-objid="' + tpye.energyTypeId + '">' + tpye.energyTypeName + "</q>";
              energyTypeId = tpye.energyTypeId;
              break;

            default:
              tpyeHtml += '<a  class="typeObj"   data-objid="' + tpye.energyTypeId + '">' + tpye.energyTypeName + "</q>";
              break;
          }
        });
      }

      $("#typeBreacrumb").html(tpyeHtml);
      element.render();
      getRightChartData();
    })["catch"](function (err) {
      console.log(err);
    });
  } //能源类型选择点击事件


  $(document).on("click", ".typeObj", function () {
    $(this).siblings().removeClass("layui-this");
    $(this).addClass("layui-this");
    energyTypeId = $(this).data("objid");
    getRightChartData();
  });

  function getRightChartData() {
    //获取右侧图表数据
    var formData = {};
    formData.objType = JSON.parse(sessionStorage.getItem("parkId")).type;
    formData.objId = JSON.parse(sessionStorage.getItem("parkId")).id;
    formData.energyTypeId = energyTypeId;
    formData.timestamp = moment(moment().add('days', 0).format('YYYY-MM')).valueOf();
    load();
    request.service({
      method: 'post',
      url: '/projectEnergyConsume/usageInfoCompare',
      data: formData
    }).then(function (res) {
      disLoad();
      var renderData = res.one;

      if (res.one) {
        renderRightChart(renderData);
      }
    })["catch"](function (err) {});
  }

  function renderRightChart(renderData) {
    //渲染右侧图表
    var firstData = [];
    firstData[0] = renderData.nowResp ? renderData.nowResp.toFixed(2) : '';
    firstData[1] = renderData.nowResp ? renderData.nowResp.toFixed(2) : '';
    var secondData = [];
    secondData[0] = renderData.lastYearResp ? renderData.lastYearResp.toFixed(2) : '';
    secondData[1] = renderData.lastMonthResp ? renderData.lastMonthResp.toFixed(2) : '';
    var thridData = [];
    thridData[0] = renderData.lastYearTermResp ? renderData.lastYearTermResp.toFixed(2) : '';
    thridData[1] = renderData.lastMonthTermResp ? renderData.lastMonthTermResp.toFixed(2) : '';
    var rightChart = echarts.init(document.getElementById("content_right_chart"));
    var name = renderData.name ? renderData.name : '';
    var unit = renderData.unit ? renderData.unit : '';
    var seriesLabel = {
      normal: {
        show: true,
        textBorderColor: '#333',
        textBorderWidth: 2
      }
    }; // var option = {
    //     tooltip: {
    //         trigger: 'axis',
    //         axisPointer: {
    //             type: 'shadow'
    //         }
    //     },
    //     grid: {
    //         left: 35,
    //         right: 35,
    //         bottom: 10,
    //         top: 35,
    //         containLabel: true
    //       },
    //         xAxis: {
    //             type: 'category',
    //             inverse: true,
    //             data: ['环比', '同比'],
    //             axisLabel: {
    //                 formatter: function (value) {
    //                     return value;
    //                 },
    //                 margin: 20,
    //             }
    //         },       
    //     yAxis: {
    //         type: 'value',
    //         name: name+'（'+unit+'）',
    //     },
    //     series: [ {
    //         type: 'bar',
    //         data: firstData,
    //         label: seriesLabel,
    //     },
    //     {
    //         type: 'bar',
    //          stack: "使用情况",
    //         label: seriesLabel,
    //         data: thridData
    //     },
    //     {
    //         type: 'bar',
    //          stack: "使用情况",
    //         label: seriesLabel,
    //         data: secondData
    //     }]
    // };

    var option = {
      legend: {
        bottom: 20,
        textStyle: {}
      },
      grid: {
        left: 35,
        right: 35,
        bottom: 10,
        top: 35,
        containLabel: true
      },
      tooltip: {
        show: "true",
        trigger: 'axis',
        axisPointer: {
          // 坐标轴指示器，坐标轴触发有效
          type: 'shadow' // 默认为直线，可选为：'line' | 'shadow'

        }
      },
      yAxis: {
        type: 'value',
        name: name + '（' + unit + '）'
      },
      xAxis: [{
        type: 'category',
        data: ['同比', '环比']
      }, {
        type: 'category',
        axisLine: {
          show: false
        },
        axisTick: {
          show: false
        },
        axisLabel: {
          show: false
        },
        splitArea: {
          show: false
        },
        splitLine: {
          show: false
        },
        data: ['同比', '环比']
      }],
      series: [{
        type: 'bar',
        xAxisIndex: 1,
        itemStyle: {
          normal: {
            show: true
          }
        },
        label: {
          normal: {
            show: true,
            position: 'top',
            textStyle: {
              color: '#000'
            }
          }
        },
        barWidth: '22%',
        data: firstData
      }, {
        type: 'bar',
        xAxisIndex: 1,
        itemStyle: {
          normal: {
            show: true
          }
        },
        label: {
          normal: {
            show: true,
            position: 'top',
            textStyle: {
              color: '#000'
            }
          }
        },
        barWidth: '26%',
        barGap: '100%',
        data: secondData
      }, {
        type: 'bar',
        itemStyle: {
          normal: {
            show: true
          }
        },
        label: {
          normal: {
            show: true,
            position: 'top'
          }
        },
        barWidth: '22%',
        data: ['', '']
      }, {
        type: 'bar',
        barWidth: '22%',
        itemStyle: {
          normal: {
            show: true
          }
        },
        label: {
          normal: {
            show: true,
            position: 'top',
            textStyle: {
              color: '#fff'
            }
          }
        },
        barGap: '100%',
        data: thridData
      }]
    };
    rightChart.setOption(option);
    var lastMonthCompare = renderData.lastMonthCompare; //上月环比

    var lastYearCompare = renderData.lastYearCompare; //上年同比

    var lastYearIocn = '';
    var lastYearVal = '--';

    if (lastYearCompare) {
      if (lastYearCompare > 0) {
        lastYearIocn = '<i class="fa fa-arrow-up" style="color:red;"></i>';
      } else {
        lastYearIocn = '<i class="fa fa-arrow-down" style="color:red;"></i>';
      }

      lastYearVal = (parseFloat(Math.abs(lastYearCompare)) * 100).toFixed(2);
    }

    $("#lastYearVal").html('<p>去年同比：' + lastYearIocn + '&nbsp;&nbsp;' + lastYearVal + ' %</p>');
    var lastMonthIocn = '';
    var lastMonthVal = '--';

    if (lastMonthCompare) {
      if (lastMonthCompare > 0) {
        lastMonthIocn = '<i class="fa fa-arrow-up" style="color:red;"></i>';
      } else {
        lastMonthIocn = '<i class="fa fa-arrow-down" style="color:#6fbf86;"></i>';
      }

      lastMonthVal = (parseFloat(Math.abs(lastMonthCompare)) * 100).toFixed(2);
    }

    $("#lastMonthVal").html('<p>上月环比' + lastMonthIocn + '&nbsp;&nbsp;' + lastMonthVal + ' %</p>');
  }

  initFun(); //页面加载时执行初始函数
});