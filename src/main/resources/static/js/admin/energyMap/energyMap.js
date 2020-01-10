

/*
 * @Author: xzl 
 * @Date: 2019-10-22 16:07:34 
 * @Last Modified by: xzl
 * @Last Modified time: 2019-12-30 13:37:20
 */
layui.use(['request', 'form', 'element', 'layer', 'jquery'], function () {
  var form = layui.form,
      layer = layui.layer,
      $ = layui.jquery,
      element = layui.element;
  var map; //定义地图实体类；

  var infoBox; //定义弹框实体类

  var companyArr = []; //定义所有公司数组

  var fullScreenIndex;
  var indexLoading; //全屏事件

  $(".fullScreen").on('click', function () {
    var ww = window.parent.window.innerWidth + 'px',
        hh = window.parent.window.innerHeight + 'px';
    window.parent.layer.open({
      type: 2,
      title: false,
      closeBtn: false,
      area: [ww, hh],
      id: 'LAY_layuipro' //设定一个id，防止重复弹出
      ,
      content: window.location.href,
      success: function success(layero, index) {
        layero.find('iframe').contents().find(".fullScreen").hide();
      }
    });
  }); //退出全屏

  $(document).keyup(function (event) {
    var theEvent = event || window.event; // 兼容FF和IE和Opera

    var code = theEvent.keyCode || theEvent.which || theEvent.charCode;

    if (code == 27) {
      // 按 Esc
      window.parent.layer.closeAll('iframe');
    }
  });

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
    //初始化
    load();
    request.service({
      method: 'get',
      url: '/park/queryFirstPark'
    }).then(function (res) {
      disLoad();
      initMap(res.one.longitude, res.one.latitude, res.one.scale);
    })["catch"](function (err) {
      console.log(err);
    }); // sessionStorage.setItem("isCar","n")
  }

  function initMap(longitude, latitude, scale) {
    //初始地图
    map = new BMap.Map('map_content');
    map.centerAndZoom(new BMap.Point(longitude, latitude), scale); //todo

    map.enableScrollWheelZoom(true); // map.setMapStyleV2({styleJson:styleJson});

    var tileLayer = new BMap.TileLayer();

    tileLayer.getTilesUrl = function (tileCoord, zoom) {
      var x = tileCoord.x;
      var y = tileCoord.y;
      return '/public/images/tiles/' + zoom + '/tile-' + x + '_' + y + '.png'; //自定义地图图层
    };

    map.addTileLayer(tileLayer);
    map.setMapStyle({
      styleJson: [{
        "featureType": "water",
        "elementType": "all",
        "stylers": {
          "color": "#021019"
        }
      }, {
        "featureType": "highway",
        "elementType": "geometry.fill",
        "stylers": {
          "color": "#000000"
        }
      }, {
        "featureType": "highway",
        "elementType": "geometry.stroke",
        "stylers": {
          "color": "#147a92"
        }
      }, {
        "featureType": "arterial",
        "elementType": "geometry.fill",
        "stylers": {
          "color": "#000000"
        }
      }, {
        "featureType": "arterial",
        "elementType": "geometry.stroke",
        "stylers": {
          "color": "#0b3d51"
        }
      }, {
        "featureType": "local",
        "elementType": "geometry",
        "stylers": {
          "color": "#000000"
        }
      }, {
        "featureType": "land",
        "elementType": "all",
        "stylers": {
          "color": "#08304b"
        }
      }, {
        "featureType": "railway",
        "elementType": "geometry.fill",
        "stylers": {
          "color": "#000000"
        }
      }, {
        "featureType": "railway",
        "elementType": "geometry.stroke",
        "stylers": {
          "color": "#08304b"
        }
      }, {
        "featureType": "subway",
        "elementType": "geometry",
        "stylers": {
          "lightness": -70
        }
      }, {
        "featureType": "building",
        "elementType": "geometry.fill",
        "stylers": {
          "color": "#000000"
        }
      }, {
        "featureType": "all",
        "elementType": "labels.text.fill",
        "stylers": {
          "color": "#857f7f"
        }
      }, {
        "featureType": "all",
        "elementType": "labels.text.stroke",
        "stylers": {
          "color": "#000000"
        }
      }, {
        "featureType": "building",
        "elementType": "geometry",
        "stylers": {
          "color": "#022338"
        }
      }, {
        "featureType": "green",
        "elementType": "geometry",
        "stylers": {
          "color": "#062032"
        }
      }, {
        "featureType": "boundary",
        "elementType": "all",
        "stylers": {
          "color": "#1e1c1c"
        }
      }, {
        "featureType": "manmade",
        "elementType": "geometry",
        "stylers": {
          "color": "#022338"
        }
      }, {
        "featureType": "poi",
        "elementType": "all",
        "stylers": {
          "visibility": "off"
        }
      }, {
        "featureType": "all",
        "elementType": "labels.icon",
        "stylers": {
          "visibility": "off"
        }
      }, {
        "featureType": "all",
        "elementType": "labels.text.fill",
        "stylers": {
          "color": "#2da0c6",
          "visibility": "on"
        }
      }]
    });
    getCompanyList();
  } //获取地图公司列表


  function getCompanyList() {
    load();
    request.service({
      method: 'get',
      url: '/energyMap/getSiteList'
    }).then(function (res) {
      disLoad();
      companyArr = res.data.concat();
      var mapCompanyList = res.data;
      renderRightTab(mapCompanyList);
      renderMapMark(mapCompanyList);
    })["catch"](function () {});
  } //渲染右侧公司展示列表


  function renderRightTab(mapCompanyList) {
    var companyHtml = '';

    if (mapCompanyList.length > 0) {
      mapCompanyList.forEach(function (company, index) {
        switch (index) {
          case 0:
            companyHtml += '<div class="layui-colla-item">' + '<h2 class="layui-colla-title companyTitle"   data-objname="' + company.siteName + '" data-objid="' + company.objId + '" >' + company.siteName + '</h2>' + '<div class="layui-colla-content   companyObj ' + company.objId + '"> <i class="layui-icon">&#xe715;</i> ' + company.addr + '</div> </div>';
            break;

          default:
            companyHtml += '<div class="layui-colla-item">' + '<h2 class="layui-colla-title companyTitle"   data-objname="' + company.siteName + '" data-objid="' + company.objId + '">' + company.siteName + '</h2>' + '<div class="layui-colla-content  companyObj ' + company.objId + '"><i class="layui-icon">&#xe715;</i>    ' + company.addr + '</div> </div>';
            break;
        }
      });
    }

    $(".company_list").html(companyHtml);
    element.render('collapse');
  }

  $(document).on("click", '.companyTitle', function () {
    if (infoBox) {
      infoBox.close();
    }

    rightClick($(this).data("objname"), $(this).data("objid"));
  });
  var markers = {};
  var points = {}; //渲染地图坐标标记

  function renderMapMark(list) {
    if (list.length > 0) {
      var firstPoint;
      list.forEach(function (element, index) {
        if (index == 0) {
          firstPoint = new BMap.Point(element.longitude, element.latitude);
        }

        var point = new BMap.Point(element.longitude, element.latitude);
        mapAddMarker(point, element.siteName, element.objId);
      }); //rightClick(list[0].siteName, list[0].objId); // 弹出第一个
    }
  } //配置信息数据


  function mapAddMarker(point, objName, mid) {
    var marker = new BMap.Marker(point);
    map.addOverlay(marker);
    markers[mid] = marker;
    points[mid] = point;
    BMapLib.EventWrapper.addListener(marker, 'click', function (e) {
      if (infoBox) {
        infoBox.close();
      }

      load();
      $(".companyObj").removeClass("layui-show");
      $('.' + mid).addClass("layui-show");
      request.service({
        method: 'get',
        url: '/energyMap/getDetail/' + mid
      }).then(function (res) {
        disLoad();
        var electric = res.one.electric ? res.one.electric : '--'; //电

        var water = res.one.water ? res.one.water : '--'; //水

        var lastYearCompareRate = res.one.lastYearCompareRate ? res.one.lastYearCompareRate : '--'; //与上年同比

        var lastIcon = '';

        if (res.one.lastYearCompareRate) {
          if (lastYearCompareRate > 0) {
            lastIcon = '<i class="fa fa-arrow-up"></i>';
          } else {
            lastIcon = '<i class="fa fa-arrow-down"></i>';
          }

          lastYearCompareRate = Math.abs(lastYearCompareRate);
        }

        var steam = res.one.steam ? res.one.steam : '--';
        var stdCoal = res.one.stdCoal ? res.one.stdCoal : '--';
        var gdp = res.one.gdp ? res.one.gdp : '--'; //gdp能耗

        var addr = res.one.addr ? res.one.addr : '--'; //地址

        var mgs = '<div class="mapShow"><div class="mapShowTitle">' + objName + '</div><div class="mapShowContent">' + '<div class="mapShowConent_left"> <div><p class="titleIcon"></p><p>实时功率（电）：</p><p>' + electric + '</p><p>kW</p></div>' + '<div> <p class="titleIcon"></p><p>瞬时流量（水）：</p><p>' + water + '</p><p>t/h</p></div>' + '<div><p class="titleIcon"></p><p>瞬时流量（蒸汽）：</p><p>' + steam + '</p><p>t/h</p></div>' + '<div><p class="titleIcon"></p><p>年能耗标煤总量：</p><p>' + stdCoal + '</p><p>tce</p></div>' + '<div class="comparedP"><p class="titleIcon"></p><p >与上年同期：</p><p><span>' + lastIcon + '</span>' + lastYearCompareRate + '</p><p>%</p></div>' + '<div> <p class="titleIcon"></p><p>单位GDP能耗：</p><p>' + gdp + '</p><p>tce/万元</p></div></div>' + '<div class="mapShowConent_right"><img src="data:image/png;base64,' + res.one.icon + '"></div></div>' + '</div>';
        infoBox = new BMapLib.InfoBox(map, mgs, {
          boxStyle: {
            width: "530px",
            height: "330px"
          },
          closeIconMargin: "15px 5px 0 0",
          closeIconUrl: "/public/images/common/close.png",
          enableAutoPan: true
        });
        infoBox.open(marker);
        infoBox.setPosition(point);
      })["catch"](function (err) {
        console.log(err);
      });
    });
  }

  function rightClick(objName, sid) {
    var marker = markers[sid];

    if (marker) {
      BMapLib.EventWrapper.trigger(marker, 'click', {
        'type': 'onclick',
        target: marker
      });
    }
  }

  var index = 1;

  function mapCarousel() {
    //地图轮播事件
    if (sessionStorage.getItem("isCar") && sessionStorage.getItem("isCar") == 'y') {
      if (companyArr.length > 0) {
        //当存在公司列表开始轮播
        if (index == companyArr.length) {
          index = 0; //重新开始计数
        }

        rightClick(companyArr[index].siteName, companyArr[index].objId); // 弹出第一个		

        index++;
      }
    }
  }

  function startCarousel() {
    if (sessionStorage.getItem("isCar") && sessionStorage.getItem("isCar") == 'y') {
      setInterval(mapCarousel, 6000); // 弹出第一个
    }
  }

  $(".showBtn").on("click", function () {
    //企业展示显示隐藏点击事件
    if ($(".showBtnIcon").hasClass("layui-icon-left")) {
      //判断是否存在展开的class
      $(".company_list").addClass("company_list_active");
      $(".showBtnIcon").addClass("layui-icon-right");
      $(".showBtnIcon").removeClass("layui-icon-left");
    } else {
      $(".showBtnIcon").removeClass("layui-icon-right");
      $(".showBtnIcon").addClass("layui-icon-left");
      $(".company_list").removeClass("company_list_active");
    }
  });
  initFun();
});