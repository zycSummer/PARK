

/*
 * @Author: jpp
 * @Date: 2019-12-26 10:07:34
 * @Last Modified by: jpp
 * @Last Modified time: 2019-12-30 13:37:35
 */
layui.use(['request', 'form', 'element', 'layer', 'jquery', 'slider', 'table'], function () {
  var form = layui.form,
      layer = layui.layer,
      $ = layui.jquery,
      slider = layui.slider,
      table = layui.table,
      element = layui.element;
  layer = parent.layer === undefined ? layui.layer : top.layer;
  var siteObj = JSON.parse(sessionStorage.getItem('parkId'));
  var chart1, chart2, chart3, chart4, chart5, chart6, chartMap;
  var fullScreenIndex; //全屏

  $(".fullScreen").on('click', function () {
    var ww = $(parent.window).width() + 'px',
        hh = $(parent.window).height() + 'px';
    layer.open({
      type: 2,
      title: false,
      closeBtn: false,
      area: [ww, hh],
      id: 'LAY_layuipro' //设定一个id，防止重复弹出
      ,
      content: window.location.href,
      success: function success(layero, index) {
        fullScreenIndex = index;
        layero.find('iframe').contents().find(".tabBreacrumb").hide();
        layero.find('iframe').contents().find(".fullScreen").hide();
      }
    });
  }); //退出全屏

  $(parent.window.document).keyup(function (event) {
    var theEvent = event || window.event; // 兼容FF和IE和Opera

    var code = theEvent.keyCode || theEvent.which || theEvent.charCode;

    if (code == 27) {
      // 按 Esc
      var parentHT = parent.window.document.getElementById("chart2");
      if ($(parentHT)) $(parentHT).remove();
    }
  }); //退出全屏

  $(document).keyup(function (event) {
    var theEvent = event || window.event; // 兼容FF和IE和Opera

    var code = theEvent.keyCode || theEvent.which || theEvent.charCode;

    if (code == 27) {
      // 按 Esc
      layer.closeAll('iframe');
    }
  });
  getElectricityData(); //电耗

  getWaterData(); //水耗

  getStdCoalData(); //能耗

  getAddValueData(); //万元工业增加值能耗

  getYearData(); //当年一整年数据

  getProfileData(); //简介信息

  getMapData(); //地图数据

  /*----------------------------------函数------------------------------------*/
  //获取 国际、国内 万元GDP电耗 数据

  function getElectricityData() {
    load();
    request.service({
      method: 'post',
      url: '/comprehensiveSummary/query',
      data: {
        "objType": siteObj.type,
        "objId": siteObj.id,
        "benchmarkingType": 'gdpElectricity'
      }
    }).then(function (res) {
      disLoad();
      var data = res.one;
      initISOLine(data.internationalList);
      initGBLine(data.domesticList);
    })["catch"](function (err) {
      console.log(err);
    });
  } //获取 国际、国内 万元GDP水耗 数据


  function getWaterData() {
    load();
    request.service({
      method: 'post',
      url: '/comprehensiveSummary/query',
      data: {
        "objType": siteObj.type,
        "objId": siteObj.id,
        "benchmarkingType": 'gdpWater'
      }
    }).then(function (res) {
      disLoad();
      var data = res.one;
      initISOTable(data.internationalList);
      initGBTable(data.domesticList);
    })["catch"](function (err) {
      console.log(err);
    });
  } //获取 国际、国内 万元GDP能耗 数据


  function getStdCoalData() {
    load();
    request.service({
      method: 'post',
      url: '/comprehensiveSummary/query',
      data: {
        "objType": siteObj.type,
        "objId": siteObj.id,
        "benchmarkingType": 'gdpStdCoal'
      }
    }).then(function (res) {
      disLoad();
      var data = res.one;
      initISOBar(data.internationalList);
      initGBBar(data.domesticList);
    })["catch"](function (err) {
      console.log(err);
    });
  } //获取 国际、国内 万元工业增加值能耗 数据


  function getAddValueData() {
    load();
    request.service({
      method: 'post',
      url: '/comprehensiveSummary/query',
      data: {
        "objType": siteObj.type,
        "objId": siteObj.id,
        "benchmarkingType": 'addValueStdCoal'
      }
    }).then(function (res) {
      disLoad();
      var data = res.one;
      initISOPie(data.internationalList);
      initGBPie(data.domesticList);
    })["catch"](function (err) {
      console.log(err);
    });
  } //国际 万元GDP电耗


  function initISOLine(data) {
    if (chart1 != null && chart1 != "" && chart1 != undefined) {
      chart1.dispose();
    }

    var seriesData = [],
        xData = [];
    $.each(data, function (i0, v0) {
      var val = v0.data != null ? v0.data.toFixed(2) : v0.data;
      seriesData.push({
        'name': v0.name,
        'value': val
      });
      xData.push(v0.abbrName);
    });
    var option = {
      tooltip: {
        trigger: 'axis',
        axisPointer: {
          type: 'line'
        },
        formatter: function formatter(params, ticket, callback) {
          return params[0].name + ':' + params[0].value;
        }
      },
      dataZoom: [{
        type: 'inside',
        throttle: 50
      }],
      grid: {
        left: '13%',
        right: '3%',
        bottom: '14%',
        top: '18%' // containLabel: true

      },
      xAxis: [{
        type: 'category',
        data: xData,
        axisLine: {
          show: true,
          lineStyle: {
            color: '#44a8d0'
          }
        }
      }],
      yAxis: [{
        type: 'value',
        scale: true,
        name: 'kWh/万元',
        nameGap: 7,
        splitLine: {
          show: false
        },
        //去除网格线
        axisLine: {
          show: true,
          lineStyle: {
            color: '#44a8d0'
          }
        }
      }],
      series: [{
        "name": '万元',
        "type": 'line',
        "smooth": true,
        "symbol": 'circle',
        "symbolSize": 5,
        "sampling": 'average',
        "itemStyle": {
          normal: {
            color: 'rgb(6,191,6)'
          }
        },
        "stack": 'a',
        "areaStyle": {
          normal: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{
              offset: 0,
              color: 'rgba(5,192,5,0.70)'
            }, {
              offset: 1,
              color: 'rgba(0,227,216,0.1)'
            }])
          }
        },
        "data": seriesData
      }]
    };
    chart1 = echarts.init(document.getElementById('ISO_line')).setOption(option, true);
  } //国际 万元GDP水耗


  function initISOTable(data) {
    table.render({
      elem: '#ISO_table',
      height: $(".tableCon").height(),
      page: false,
      limit: 99999,
      cols: [[{
        field: 'ranking',
        title: '排名',
        width: 55,
        align: 'center'
      }, {
        field: 'name',
        title: '名称',
        align: 'center'
      }, {
        field: 'data',
        title: '万元GDP水耗(t/万元)',
        align: 'center',
        width: 140,
        templet: function templet(t) {
          var val = t.data == null ? '' : t.data.toFixed(2);
          return val;
        }
      }]],
      data: data
    });
  } //国际 万元GDP能耗


  function initISOBar(data) {
    if (chart2 != null && chart2 != "" && chart2 != undefined) {
      chart2.dispose();
    }

    var seriesData = [],
        yData = [];
    $.each(data, function (i0, v0) {
      var val = v0.data ? v0.data.toFixed(2) : v0.data;
      seriesData.unshift({
        'name': v0.name,
        'value': val
      });
      yData.unshift(v0.abbrName);
    });
    var option = {
      color: ['#33b9eb'],
      textStyle: {
        color: '#fff'
      },
      tooltip: {
        trigger: 'axis',
        axisPointer: {
          // 坐标轴指示器，坐标轴触发有效
          type: 'shadow' // 默认为直线，可选为：'line' | 'shadow'

        },
        formatter: function formatter(params, ticket, callback) {
          return params[0].name + ':' + params[0].value;
        }
      },
      grid: {
        left: '20%',
        right: '16%',
        bottom: '17%',
        top: '5%' // containLabel: true

      },
      xAxis: [{
        name: 'tce/万元',
        type: 'value',
        splitLine: {
          show: false
        },
        axisLine: {
          show: true,
          lineStyle: {
            color: '#44a8d0'
          }
        }
      }],
      yAxis: [{
        type: 'category',
        data: yData,
        splitLine: {
          show: false
        },
        axisLine: {
          show: false
        }
      }],
      series: [{
        "name": '万元',
        "type": 'bar',
        "stack": 'stack',
        "barWidth": 6,
        "itemStyle": {
          normal: {
            color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [{
              offset: 0,
              color: '#2e77bb'
            }, {
              offset: 1,
              color: '#8f82bc'
            }]),
            barBorderRadius: [0, 10, 10, 0]
          }
        },
        "data": seriesData
      }]
    };
    chart2 = echarts.init(document.getElementById('ISO_bar')).setOption(option, true);
  } //国际 万元工业增加值能耗


  function initISOPie(data) {
    if (chart3 != null && chart3 != "" && chart3 != undefined) {
      chart3.dispose();
    }

    data.reverse();
    var colors4 = [['#05bf04', '#064d21'], ['#0168b7', '#064d21'], ['#7c48aa', '#064d21'], ['#b7a900', '#064d21'], ['#47cbcd', '#064d21'], ['#e18d47', '#064d21'], ['#7b9ae1', '#064d21'], ['#e15224', '#064d21']];
    var colorsLegent4 = []; // 以数值最大的除以0.75的结果定为100%，其他所有值除以这个结果得到各自的百分数进行展示

    var maxValue = data[0].data;
    var seriesData = [],
        legendData = [];
    $.each(data, function (i4, v4) {
      var num = parseInt(100 / data.length);
      var r1 = 98 - i4 * num;
      var r2 = 90 - i4 * num;
      var val4 = v4.data ? v4.data.toFixed(2) : v4.data;
      var total4 = maxValue / 0.75 - v4.data;
      total4 = total4 ? total4.toFixed(2) : total4;

      if (!colors4[i4]) {
        colors4.push([randomColor(), '#064d21']);
      }

      seriesData.push({
        name: v4.abbrName,
        type: 'pie',
        selectedMode: 'single',
        radius: [r2 + '%', r1 + '%'],
        label: {
          show: false
        },
        labelLine: {
          normal: {
            show: false
          }
        },
        data: [{
          value: val4,
          name: v4.name,
          type: 1,
          itemStyle: {
            color: colors4[i4][0]
          }
        }, {
          value: total4,
          name: '总计',
          type: 0,
          itemStyle: {
            color: colors4[i4][1]
          }
        }]
      });
      colorsLegent4.push(colors4[i4][0]);
      legendData.push(v4.abbrName);
    });
    var option = {
      tooltip: {
        trigger: 'item',
        formatter: function formatter(params, ticket, callback) {
          if (params.data.type) {
            return params.data.name + ':' + params.data.value;
          }
        }
      },
      color: colorsLegent4,
      legend: {
        orient: 'vertical',
        x: 'left',
        itemWidth: 15,
        data: legendData,
        textStyle: {
          color: '#fff',
          fontSize: 10
        }
      },
      series: seriesData
    };
    chart3 = echarts.init(document.getElementById('ISO_pie')).setOption(option, true);
  } //国内 万元GDP电耗


  function initGBLine(data) {
    if (chart4 != null && chart4 != "" && chart4 != undefined) {
      chart4.dispose();
    }

    var seriesData = [],
        xData = [];
    $.each(data, function (i0, v0) {
      var val = v0.data ? v0.data.toFixed(2) : v0.data;
      seriesData.push({
        'name': v0.name,
        'value': val
      });
      xData.push(v0.abbrName);
    });
    var option = {
      tooltip: {
        trigger: 'axis',
        axisPointer: {
          type: 'line'
        },
        formatter: function formatter(params, ticket, callback) {
          return params[0].name + ':' + params[0].value;
        }
      },
      dataZoom: [{
        type: 'inside',
        throttle: 50
      }],
      grid: {
        left: '13%',
        right: '3%',
        bottom: '14%',
        top: '18%' // containLabel: true

      },
      xAxis: [{
        type: 'category',
        data: xData,
        axisLine: {
          show: true,
          lineStyle: {
            color: '#44a8d0'
          }
        }
      }],
      yAxis: [{
        type: 'value',
        scale: true,
        name: 'kWh/万元',
        nameGap: 7,
        splitLine: {
          show: false
        },
        //去除网格线
        axisLine: {
          show: true,
          lineStyle: {
            color: '#44a8d0'
          }
        }
      }],
      series: [{
        "name": '万元',
        "type": 'line',
        "smooth": true,
        "symbol": 'circle',
        "symbolSize": 5,
        "sampling": 'average',
        "itemStyle": {
          normal: {
            color: 'rgb(6,191,6)'
          }
        },
        "stack": 'a',
        "areaStyle": {
          normal: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{
              offset: 0,
              color: 'rgba(5,192,5,0.70)'
            }, {
              offset: 1,
              color: 'rgba(0,227,216,0.1)'
            }])
          }
        },
        "data": seriesData
      }]
    };
    chart4 = echarts.init(document.getElementById('GB_line')).setOption(option, true);
  } //国内 万元GDP水耗


  function initGBTable(data) {
    table.render({
      elem: '#GB_table',
      height: $(".tableCon").height(),
      page: false,
      limit: 99999,
      cols: [[{
        field: 'ranking',
        title: '排名',
        width: 55,
        align: 'center'
      }, {
        field: 'name',
        title: '名称',
        align: 'center'
      }, {
        field: 'data',
        title: '万元GDP水耗(t/万元)',
        align: 'center',
        width: 140,
        templet: function templet(t) {
          var val = t.data == null ? '' : t.data.toFixed(2);
          return val;
        }
      }]],
      data: data
    });
  } //国内 万元GDP能耗


  function initGBBar(data) {
    if (chart5 != null && chart5 != "" && chart5 != undefined) {
      chart5.dispose();
    }

    var seriesData = [],
        yData = [];
    $.each(data, function (i0, v0) {
      var val = v0.data ? v0.data.toFixed(2) : v0.data;
      seriesData.unshift({
        'name': v0.name,
        'value': val
      });
      yData.unshift(v0.abbrName);
    });
    var option = {
      color: ['#33b9eb'],
      textStyle: {
        color: '#fff'
      },
      tooltip: {
        trigger: 'axis',
        axisPointer: {
          // 坐标轴指示器，坐标轴触发有效
          type: 'shadow' // 默认为直线，可选为：'line' | 'shadow'

        },
        formatter: function formatter(params, ticket, callback) {
          return params[0].name + ':' + params[0].value;
        }
      },
      grid: {
        left: '18%',
        right: '16%',
        bottom: '17%',
        top: '1%' // containLabel: true

      },
      xAxis: [{
        name: 'tce/万元',
        type: 'value',
        splitLine: {
          show: false
        },
        axisLine: {
          show: true,
          lineStyle: {
            color: '#44a8d0'
          }
        }
      }],
      yAxis: [{
        type: 'category',
        data: yData,
        splitLine: {
          show: false
        },
        axisLine: {
          show: false
        }
      }],
      series: [{
        "name": '万元',
        "type": 'bar',
        "stack": 'stack',
        "barWidth": 6,
        "itemStyle": {
          normal: {
            color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [{
              offset: 0,
              color: '#2e77bb'
            }, {
              offset: 1,
              color: '#8f82bc'
            }]),
            barBorderRadius: [0, 10, 10, 0]
          }
        },
        "data": seriesData
      }]
    };
    chart5 = echarts.init(document.getElementById('GB_bar')).setOption(option, true);
  } //国内 万元工业增加值能耗


  function initGBPie(data) {
    if (chart6 != null && chart6 != "" && chart6 != undefined) {
      chart6.dispose();
    }

    data.reverse();
    var colors6 = [['#05bf04', '#064d21'], ['#0168b7', '#064d21'], ['#7c48aa', '#064d21'], ['#b7a900', '#064d21'], ['#47cbcd', '#064d21'], ['#e18d47', '#064d21'], ['#7b9ae1', '#064d21'], ['#e15224', '#064d21']];
    var colorsLegent6 = []; // 以数值最大的除以0.75的结果定为100%，其他所有值除以这个结果得到各自的百分数进行展示

    var maxValue = data[0].data;
    var seriesData6 = [],
        legendData = [];
    $.each(data, function (i6, v6) {
      var num = parseInt(100 / data.length);
      var r1 = 98 - i6 * num;
      var r2 = 90 - i6 * num;
      var val6 = v6.data ? v6.data.toFixed(2) : v6.data;
      var total6 = maxValue / 0.75 - v6.data;
      total6 = total6 ? total6.toFixed(2) : total6;

      if (!colors6[i6]) {
        colors6.push([randomColor(), '#064d21']);
      }

      seriesData6.push({
        name: v6.abbrName,
        type: 'pie',
        selectedMode: 'single',
        radius: [r2 + '%', r1 + '%'],
        label: {
          show: false
        },
        labelLine: {
          normal: {
            show: false
          }
        },
        data: [{
          value: val6,
          name: v6.name,
          type: 1,
          itemStyle: {
            color: colors6[i6][0]
          }
        }, {
          value: total6,
          name: '总计',
          type: 0,
          itemStyle: {
            color: colors6[i6][1]
          }
        }]
      });
      colorsLegent6.push(colors6[i6][0]);
      legendData.push(v6.abbrName);
    });
    var option = {
      tooltip: {
        trigger: 'item',
        formatter: function formatter(params, ticket, callback) {
          if (params.data.type) {
            return params.data.name + ':' + params.data.value;
          }
        }
      },
      color: colorsLegent6,
      legend: {
        orient: 'vertical',
        x: 'left',
        itemWidth: 15,
        data: legendData,
        textStyle: {
          color: '#fff',
          fontSize: 10
        }
      },
      series: seriesData6
    };
    chart6 = echarts.init(document.getElementById('GB_pie')).setOption(option, true);
  } //获取 地图数据


  function getMapData() {
    load();
    request.service({
      method: 'post',
      url: '/comprehensiveSummary/queryPosition',
      data: {
        "objType": siteObj.type,
        "objId": siteObj.id
      }
    }).then(function (res) {
      disLoad();
      var data = res.data;
      initMap(data);
    })["catch"](function (err) {
      console.log(err);
    });
  }

  function initMap(mapData) {
    if (chartMap != null && chartMap != "" && chartMap != undefined) {
      chartMap.dispose();
    }

    var svg = "path://M32.597,9.782 L30.475,11.904 C30.085,12.294 29.452,12.294 29.061,11.904 C28.671,11.513 28.671,10.880 29.061,10.489 L31.182,8.368 C31.573,7.978 32.206,7.978 32.597,8.368 C32.987,8.759 32.987,9.392 32.597,9.782 ZM30.000,30.500 C30.000,31.328 29.329,32.000 28.500,32.000 L5.500,32.000 C4.672,32.000 4.000,31.328 4.000,30.500 C4.000,29.672 4.672,29.000 5.500,29.000 L8.009,29.000 L8.009,18.244 C8.009,13.139 12.034,9.000 17.000,9.000 C21.966,9.000 25.992,13.139 25.992,18.244 L25.992,29.000 L28.500,29.000 C29.329,29.000 30.000,29.672 30.000,30.500 ZM17.867,14.444 L13.000,22.000 L17.000,22.000 L17.133,26.556 L21.000,20.000 L17.000,20.000 L17.867,14.444 ZM25.221,6.327 C25.033,6.846 24.459,7.113 23.940,6.924 C23.421,6.735 23.153,6.162 23.342,5.643 L24.368,2.823 C24.557,2.304 25.131,2.037 25.650,2.226 C26.169,2.415 26.436,2.989 26.248,3.508 L25.221,6.327 ZM17.000,5.000 C16.448,5.000 16.000,4.552 16.000,4.000 L16.000,1.000 C16.000,0.448 16.448,0.000 17.000,0.000 C17.552,0.000 18.000,0.448 18.000,1.000 L18.000,4.000 C18.000,4.552 17.552,5.000 17.000,5.000 ZM10.028,7.197 C9.509,7.386 8.935,7.118 8.746,6.599 L7.720,3.780 C7.532,3.261 7.799,2.687 8.318,2.498 C8.837,2.309 9.411,2.577 9.600,3.096 L10.626,5.915 C10.815,6.434 10.547,7.008 10.028,7.197 ZM3.354,12.268 L1.232,10.146 C0.842,9.756 0.842,9.123 1.232,8.732 C1.623,8.342 2.256,8.342 2.646,8.732 L4.768,10.854 C5.158,11.244 5.158,11.877 4.768,12.268 C4.377,12.658 3.744,12.658 3.354,12.268 Z";
    var geoCoordMap = {},
        rootNodeName = '';
    var BJData = [];
    var GBData = [];
    var lengendData = [{
      name: '连云港石化产业基地',
      icon: svg
    }, {
      name: '国内',
      icon: svg
    }, {
      name: '国外',
      icon: svg
    }];

    if (mapData.length > 0) {
      if (siteObj.type == 'PARK') {
        //当为园区时请求获取当前园区下的企业
        rootNodeName = mapData[0].parkName;
        geoCoordMap[mapData[0].parkName] = [mapData[0].worldLongitude, mapData[0].worldLatitude];
      } else {
        rootNodeName = mapData[0].siteName;
        geoCoordMap[mapData[0].siteName] = [mapData[0].worldLongitude, mapData[0].worldLatitude];
      }

      lengendData[0].name = rootNodeName;

      if (mapData.length > 1) {
        $.each(mapData[1], function (i5, v5) {
          geoCoordMap[v5.benchmarkingObjName] = [v5.worldLongitude, v5.worldLatitude];
          var rootName = '';

          if (siteObj.type == 'PARK') {
            //当为园区时请求获取当前园区下的企业
            rootName = mapData[0].parkName;
          } else {
            rootName = mapData[0].siteName;
          }

          if (v5.benchmarkingObjType == 'International') {
            BJData.push([{
              name: v5.benchmarkingObjName,
              value: 50
            }, {
              name: rootName
            }]);
          } else {
            GBData.push([{
              name: v5.benchmarkingObjName,
              value: 200
            }, {
              name: rootName
            }]);
          }
        });
      } else {
        lengendData.length = 1;
      }
    } // var geoCoordMap = {
    //     连云港石化产业基地: [121.4648, 31.2891],
    //     美国休斯顿工业区: [-87.801833, 41.870975],
    //     荷兰切梅洛特化工园区: [-1.657222, 51.886863],
    //     德国切姆西特化工园区: [10.01959, 54.38474],
    //     新加坡裕廊岛石化园区: [104.88659, 11.545469],
    //     比利时安特卫普化工区: [9.189948, 45.46623],
    //     天津南港工业区: [121.4689, 31.2891],
    //     大连长兴岛石化产业基地: [122.4648, 31.2891],
    //     上海化学工业经济开发区: [120.4648, 31.2891],
    //     南京化学工业园区: [121.4648, 32.2891],
    //     宁波石化经济技术开发区: [121.4648, 30.2891],
    //     福建漳州古雷经济开发区: [121.4648, 33.2891],
    //     惠州大亚湾经济技术开发区: [121.4648, 34.2891]
    // };


    var convertData = function convertData(data) {
      var res = [];

      for (var i = 0; i < data.length; i++) {
        var dataItem = data[i];
        var fromCoord = geoCoordMap[dataItem[1].name];
        var toCoord = geoCoordMap[dataItem[0].name];

        if (fromCoord && toCoord) {
          res.push([{
            coord: fromCoord,
            value: dataItem[0].value
          }, {
            coord: toCoord
          }]);
        }
      }

      return res;
    };

    var colors = ['#3ed4ff', '#a6c84c', '#ffa022'];
    var series = [];
    [[rootNodeName, GBData, '国内'], [rootNodeName, BJData, '国外']].forEach(function (item, i) {
      if (i == 0) {
        series.push({
          //被攻击点
          name: item[0],
          type: "scatter",
          coordinateSystem: "geo",
          //使用地理坐标系
          zlevel: 2,
          rippleEffect: {
            period: 4,
            brushType: "stroke",
            scale: 4
          },
          label: {
            normal: {
              show: true,
              color: colors[2],
              position: "right",
              formatter: "{b}"
            },
            emphasis: {
              show: true,
              color: colors[2]
            }
          },
          symbol: 'pin',
          symbolSize: 30,
          itemStyle: {
            normal: {
              show: true,
              color: colors[2]
            },
            emphasis: {
              show: true,
              color: colors[2]
            }
          },
          data: [{
            name: item[0],
            value: geoCoordMap[item[0]].concat([100]),
            visualMap: false
          }]
        });
      }

      series.push({
        name: item[2],
        type: "lines",
        zlevel: 1,
        effect: {
          show: true,
          color: '#fff',
          period: 4,
          //箭头指向速度，值越小速度越快
          trailLength: 0.02,
          //特效尾迹长度[0,1]值越大，尾迹越长重
          symbol: "arrow",
          //箭头图标
          symbolSize: 5 //图标大小

        },
        lineStyle: {
          normal: {
            color: colors[i],
            width: 1,
            //尾迹线条宽度
            opacity: 0.2,
            //尾迹线条透明度
            curveness: 0.3 //尾迹线条曲直度

          }
        },
        data: convertData(item[1])
      }, {
        type: "effectScatter",
        coordinateSystem: "geo",
        zlevel: 2,
        rippleEffect: {
          //涟漪特效
          period: 4,
          //动画时间，值越小速度越快
          brushType: "stroke",
          //波纹绘制方式 stroke, fill
          scale: 4 //波纹圆环最大限制，值越大波纹越大

        },
        label: {
          normal: {
            show: false,
            position: "right",
            //显示位置
            offset: [5, 0],
            //偏移设置
            formatter: "{b}" //圆环显示文字

          },
          emphasis: {
            show: true,
            color: colors[i]
          }
        },
        symbol: "circle",
        symbolSize: function symbolSize(val) {
          return 8 + val[2] / 1000; //圆环大小
        },
        itemStyle: {
          normal: {
            show: true,
            color: colors[i]
          },
          emphasis: {
            show: true,
            color: colors[i]
          }
        },
        data: item[1].map(function (dataItem) {
          return {
            name: dataItem[0].name,
            value: geoCoordMap[dataItem[0].name].concat([dataItem[0].value])
          };
        })
      });
    });
    var option = {
      backgroundColor: '#000',
      // tooltip: {
      //     trigger: "item",
      //     backgroundColor: "#1540a1",
      //     borderColor: "#FFFFCC",
      //     showDelay: 0,
      //     hideDelay: 0,
      //     enterable: true,
      //     transitionDuration: 0,
      //     extraCssText: "z-index:100",
      //     // formatter: function(params, ticket, callback) {
      //     //     //根据业务自己拓展要显示的内容
      //     //     var name = params.name;
      //     //     return name;
      //     // }
      // },
      // visualMap: {
      //     //图例值控制
      //     show: false,
      //     type: 'piecewise',
      //     pieces: [{
      //         max: 80,
      //         color: colors[1]
      //     },
      //         {
      //             min: 80,
      //             max: 120,
      //             color: colors[3]
      //         },
      //         {
      //             min: 120,
      //             color: colors[4]
      //         }
      //     ],
      //     calculable: true,
      // },
      geo: {
        map: "world",
        show: true,
        label: {
          emphasis: {
            show: false
          }
        },
        roam: true,
        //是否允许缩放
        layoutCenter: ["45%", "50%"],
        //地图位置
        layoutSize: "180%",
        itemStyle: {
          normal: {
            show: 'true',
            color: "#04284e",
            //地图背景色
            borderColor: "#5bc1c9" //省市边界线

          },
          emphasis: {
            show: 'true',
            color: "rgba(37, 43, 61, .5)" //悬浮背景

          }
        }
      },
      legend: {
        orient: 'vertical',
        left: '2%',
        bottom: '5%',
        textStyle: {
          color: 'white'
        },
        data: lengendData //     itemWidth:50,
        //     itemHeight:30,
        //     selectedMode: 'multiple'

      },
      // legend: {
      //     orient: 'vertical',
      //     top: '30',
      //     left: 'center',
      //     align:'right',
      //     data:[
      //         {name:'攻击线1',icon:svg,},],
      //     textStyle: {
      //         color: '#fff',
      //         fontSize:20,
      //     },
      //     itemWidth:50,
      //     itemHeight:30,
      //     selectedMode: 'multiple'
      // },
      series: series
    };
    chartMap = echarts.init(document.getElementById('map')).setOption(option, true);
  } //获取 当年一整年数据


  function getYearData() {
    load();
    request.service({
      method: 'post',
      url: '/comprehensiveSummary/queryAllEnergy',
      data: {
        "objType": siteObj.type,
        "objId": siteObj.id
      }
    }).then(function (res) {
      disLoad();
      var data = res.data;
      $.each(data, function (i6, v6) {
        var val = v6.eneryValue == null ? '--' : v6.eneryValue.toFixed(2);

        switch (v6.eneryName) {
          case 'gdpElectricity':
            $("#gdpElectricity").html(val + ' kWh/万元');
            break;

          case 'gdpWater':
            $("#gdpWater").html(val + ' t/万元');
            break;

          case 'gdpSteam':
            $("#gdpSteam").html(val + ' t/万元');
            break;

          case 'gdpStdCoal':
            $("#gdpStdCoal").html(val + ' tce/万元');
            break;

          case 'addValueStdCoal':
            $("#addValueStdCoal").html(val + ' tce/万元');
            break;
        }
      });
    })["catch"](function (err) {
      console.log(err);
    });
  } //获取 简介信息


  function getProfileData() {
    load();
    request.service({
      method: 'post',
      url: '/comprehensiveSummary/queryInfo',
      data: {
        "objType": siteObj.type,
        "objId": siteObj.id
      }
    }).then(function (res) {
      disLoad();
      var data = res.one;
      $("#profileImg").attr('src', 'data:image/png;base64,' + res.one.img);
      $("#profileContent").html(data.profile);
    })["catch"](function (err) {
      console.log(err);
    });
  } //获取十六进制颜色


  function randomColor() {
    var r = Math.floor(Math.random() * 256);
    var g = Math.floor(Math.random() * 256);
    var b = Math.floor(Math.random() * 256);

    if (r < 16) {
      r = "0" + r.toString(16);
    } else {
      r = r.toString(16);
    }

    if (g < 16) {
      g = "0" + g.toString(16);
    } else {
      g = g.toString(16);
    }

    if (b < 16) {
      b = "0" + b.toString(16);
    } else {
      b = b.toString(16);
    }

    return "#" + r + g + b;
  }

  var indexLoading;

  function load() {
    indexLoading = layer.load(1, {
      shade: [0.3, '#fff']
    });
  }

  function disLoad() {
    layer.close(indexLoading);
  }
});