

/*
 * @Author: xzl 
 * @Date: 2019-10-25 14:52:08 
 * @Last Modified by: xzl
 * @Last Modified time: 2019-11-07 10:20:01
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

  var defaultDate = moment().add('year', 0).format('YYYY'); //当前年  

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
    renderChartTable();
  }

  function renderChartTitle() {
    //渲染表格头部
    if (sessionStorage.getItem('parkId')) {
      var objSiteType = JSON.parse(sessionStorage.getItem("parkId")).type;

      switch (objSiteType) {
        case 'PARK':
          $("#chart_head_title").text(JSON.parse(sessionStorage.getItem("parkId")).name + ' - 能耗日历');
          break;

        default:
          $("#chart_head_title").text(JSON.parse(sessionStorage.getItem("parkId")).title + ' - 能耗日历');
          break;
      }
    }

    $(".head_date").text(defaultDate); //渲染时间
  }

  $(".datePrv").on("click", function () {
    //上一年点击事件
    defaultDate = moment(defaultDate).add('year', -1).format('YYYY');
    $(".head_date").text(defaultDate); //渲染时间

    renderChartTable();
  });
  $(".dateNext").on("click", function () {
    //下一年点击事件
    defaultDate = moment(defaultDate).add('year', 1).format('YYYY');
    $(".head_date").text(defaultDate); //渲染时间 

    renderChartTable();
  });
  $(".head_date").on("click", function () {
    //年份点击弹框事件
    var dateSelectDialog = layer.open({
      //年份选择弹框
      type: 1,
      title: false,
      //不显示标题栏
      closeBtn: false,
      shade: 0.3,
      anim: 1,
      area: ['275px', '325px'],
      //宽高
      content: '<div id="timePick" style="margin:auto;"></div>',
      success: function success() {
        laydate.render({
          elem: '#timePick',
          show: true,
          value: defaultDate,
          type: 'year',
          position: 'static',
          done: function done(value, date) {
            defaultDate = value;
            $(".head_date").text(defaultDate);
            layer.close(dateSelectDialog);
            $("#timePick").html('');
          }
        });
      }
    });
  });

  function renderChartTable() {
    //渲染表格
    var formData = {};
    formData.objType = JSON.parse(sessionStorage.getItem("parkId")).type;
    formData.objId = JSON.parse(sessionStorage.getItem("parkId")).id;
    formData.timestamp = moment(defaultDate + '-01-01').valueOf();
    load();
    request.service({
      method: 'post',
      url: '/projectEnergyConsume/calendar',
      data: formData
    }).then(function (res) {
      var dataList = res.one.dataList;
      var gradeList = res.one.gradeList;
      var monthColorArr = [];

      if (dataList.length > 0 && gradeList.length > 0) {
        dataList.forEach(function (item, index) {
          var colorObj = {};
          index = index + 1;
          gradeList.forEach(function (grade, indexTwo) {
            if (!grade.upper) {
              grade.upper = Infinity;
            }

            if (item) {
              //当存在值时
              if (parseFloat(item) > parseFloat(grade.lower) && parseFloat(item) < parseFloat(grade.upper)) {
                colorObj.color = grade.color;
                colorObj.val = parseFloat(item);
              }
            } else {
              colorObj.color = null;
              colorObj.val = '--';
            }
          });
          monthColorArr.push(colorObj);
        });
      }

      renderTableColor(monthColorArr);
      renderTabStandard(gradeList);
      disLoad();
    })["catch"](function (err) {
      console.log(err);
    });
  }

  initFun(); //页面加载时执行初始函数

  function renderTableColor(monthColorArr) {
    //渲染表格颜色
    var tabColorHtml = ' <tr><td>第1季度</td>' + '<td   style="background-color:' + monthColorArr[0].color + ';"><div class="colorDiv"><p>' + monthColorArr[0].val + '</p><p>1</p></div></td>' + '<td style="background-color:' + monthColorArr[1].color + ';"><div class="colorDiv"><p>' + monthColorArr[1].val + '</p><p>2</p></div></td>' + '<td style="background-color:' + monthColorArr[2].color + ';"><div class="colorDiv"><p>' + monthColorArr[2].val + '</p><p>3</p></div></td></tr>' + '<tr>  <td>第2季度</td>' + '<td style="background-color:' + monthColorArr[3].color + ';"><div class="colorDiv"><p>' + monthColorArr[3].val + '</p><p>4</p></div></td>' + '<td style="background-color:' + monthColorArr[4].color + ';"><div class="colorDiv"><p>' + monthColorArr[4].val + '</p><p>5</p></div></td>' + '<td style="background-color:' + monthColorArr[5].color + ';"><div class="colorDiv"><p>' + monthColorArr[5].val + '</p><p>6</p></div></td></tr>' + '<tr> <td>第3季度</td>' + '<td style="background-color:' + monthColorArr[6].color + ';"><div class="colorDiv"><p>' + monthColorArr[6].val + '</p><p>7</p></div></td>' + '<td style="background-color:' + monthColorArr[7].color + ';"><div class="colorDiv"><p>' + monthColorArr[7].val + '</p><p>8</p></div></td>' + '<td style="background-color:' + monthColorArr[8].color + ';"><div class="colorDiv"><p>' + monthColorArr[8].val + '</p><p>9</p></div></td></tr>' + '<tr> <td>第4季度</td>' + '<td style="background-color:' + monthColorArr[9].color + ';"><div class="colorDiv"  ><p>' + monthColorArr[9].val + '</p><p>10</p></div></td>' + '<td style="background-color:' + monthColorArr[10].color + ';"><div class="colorDiv" ><p>' + monthColorArr[10].val + '</p><p>11</p></div></td>' + '<td style="background-color:' + monthColorArr[11].color + ';"><div class="colorDiv"  ><p>' + monthColorArr[11].val + '</p><p>12</p></div></td></tr>';
    $("#tab_tbody").html(tabColorHtml);
  }

  $(document).on("mouseover", '.colorDiv', function () {
    //鼠标移入事件
    $(this).children(":first").show();
    $(this).children(":last").hide();
  });
  $(document).on("mouseout", '.colorDiv', function () {
    //鼠标移出事件
    $(this).children(":first").hide();
    ;
    $(this).children(":last").show();
  });

  function renderTabStandard(gradeList) {
    //渲染标准图
    var tabStandardHtml = '';

    if (gradeList.length > 0) {
      gradeList.forEach(function (item) {
        if (item.upper == Infinity) {
          item.upper = '';
        }

        tabStandardHtml += '<div  class="standardDiv" style="background-color:' + item.color + ';">' + item.lower + '~' + item.upper + '</div>';
      });
    }

    $(".chart_standard").html(tabStandardHtml);
  }
});