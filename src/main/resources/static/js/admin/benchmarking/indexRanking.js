/*
 * @Author: jpp
 * @Date: 2019-12-11 10:07:34
 * @Last Modified by: jpp
 * @Last Modified time: 2019-12-11 13:37:35
 */

layui.use(['request','form', 'element', 'layer', 'jquery','slider','tree'], function () {
    var form = layui.form,
        layer = layui.layer,
        tree = layui.tree,
        $ = layui.jquery,
        slider = layui.slider,
        treeGrid = layui.treeGrid,
        element = layui.element;

    var siteObj = JSON.parse(sessionStorage.getItem('parkId'));
    var navThis = 'electricity';//左侧选中的
    var chart1,chart2;

    funcsAuthority();

    //监听提交
    form.on('submit(*)', function (data) {
        return false;//阻止表单跳转
    });

    form.on('select(year)', function(data){
        getBarChart();
    });
    form.on('select(month)', function(data){
        getBarChart();
    });

    //点击 万元GDP电耗/万元GDP水耗/万元GDP能耗/万元工业增加值能耗
    $('#typeNav li').on('click',function(){
        $(this).siblings().removeClass('layui-this');
        $(this).addClass('layui-this');

        navThis = $(this).attr('data-id');
        getBarChart();
        return false;
    });

    /*----------------------------------函数------------------------------------*/
    function getBarChart(){
        var year = $("#year").val();
        var month = $("#month").val();
        var timestamp,timeUnit;

        if(month == 'all'){
            timeUnit = 'years';
            timestamp = moment(year,'YYYY').valueOf();
        }else{
            timeUnit = 'months';
            timestamp = moment(year+'-'+month,'YYYY-MM').valueOf();
        }

        var obj = getTypeData();
        load();
        request.service({
            method: 'post',
            url: '/benchmarkingRanking/queryObj',
            data:{
                'objType': siteObj.type,
                'objId': siteObj.id,
                'timestamp': timestamp,
                'timeUnit': timeUnit,
                'benchmarkingType':obj.energyTypeId
            }
        })
            .then(function (res) {
                disLoad();
                var data = res.one;
                initBarChart1(data.internationalList);
                initBarChart2(data.domesticList);
            })
            .catch(err => {
            console.log(err)
        })
    }
    //国际
    function initBarChart1(data){
        if (chart1 != null && chart1 != "" && chart1 != undefined) {
            chart1.dispose();
        }

        var m = $("#month").val();
        if(m == 'all'){
            m = '全年';
        }else{
            m = m + '月';
        }
        var obj = getTypeData();
        var title = $("#year").val()+'-'+m+'-'+obj.title+' 国际对标';
        var unit = obj.unit;

        var dataAxis = [],list = [];
        $.each(data,function(i0,v0){
            dataAxis.push(v0.name);
            var val = v0.data == null?null:parseFloat((v0.data).toFixed(2));
            list.push(val);
        });

        var option = {
            title: {
                text: title,
                subtext: '',
                left:'center',
                top:10
            },
            tooltip:{
                show:true,
                trigger:'axis',
                axisPointer:{
                    type:'shadow'
                }
            },
            xAxis: {
                data: dataAxis,
                type:'category',
                axisLabel: {
                    interval:0,//0 强制显示所有标签
                    rotate:15,
                    textStyle: {
                        color: '#000'
                    }
                },
                axisTick: {
                    show: false
                },
                axisLine: {
                    show: false
                },
                z: 10
            },
            yAxis: {
                name:unit,
                axisLine: {
                    show: false
                },
                axisTick: {
                    show: false
                },
                axisLabel: {
                    textStyle: {
                        color: '#999'
                    }
                }
            },
            dataZoom: [
                {
                    type: 'inside'
                }
            ],
            series: [
                {
                    type: 'bar',
                    itemStyle: {
                        normal: {
                            color: new echarts.graphic.LinearGradient(
                                0, 0, 0, 1,
                                [
                                    {offset: 0, color: '#83bff6'},
                                    {offset: 0.5, color: '#188df0'},
                                    {offset: 1, color: '#188df0'}
                                ]
                            )
                        },
                        emphasis: {
                            color: new echarts.graphic.LinearGradient(
                                0, 0, 0, 1,
                                [
                                    {offset: 0, color: '#2378f7'},
                                    {offset: 0.7, color: '#2378f7'},
                                    {offset: 1, color: '#83bff6'}
                                ]
                            )
                        }
                    },
                    data: list
                }
            ]
        };
        chart1 = echarts.init(document.getElementById('chart1')).setOption(option, true);
    }
    //国内
    function initBarChart2(data){
        if (chart2 != null && chart2 != "" && chart2 != undefined) {
            chart2.dispose();
        }

        var m = $("#month").val();
        if(m == 'all'){
            m = '全年';
        }else{
            m = m + '月';
        }
        var obj = getTypeData();
        var title = $("#year").val()+'-'+m+'-'+obj.title+' 国内对标';
        var unit = obj.unit;

        var dataAxis = [],list = [];
        $.each(data,function(i2,v2){
            dataAxis.push(v2.name);
            var val = v2.data == null?null:parseFloat((v2.data).toFixed(2));
            list.push(val);
        });

        var option = {
            title: {
                text: title,
                subtext: '',
                left:'center',
                top:10
            },
            tooltip:{
                show:true,
                trigger:'axis',
                axisPointer:{
                    type:'shadow'
                }
            },
            xAxis: {
                data: dataAxis,
                type:'category',
                axisLabel: {
                    interval:0,//0 强制显示所有标签
                    rotate:15,
                    textStyle: {
                        color: '#000'
                    }
                },
                axisTick: {
                    show: false
                },
                axisLine: {
                    show: false
                },
                z: 10
            },
            yAxis: {
                name:unit,
                axisLine: {
                    show: false
                },
                axisTick: {
                    show: false
                },
                axisLabel: {
                    textStyle: {
                        color: '#999'
                    }
                }
            },
            dataZoom: [
                {
                    type: 'inside'
                }
            ],
            series: [
                {
                    type: 'bar',
                    itemStyle: {
                        normal: {
                            color: new echarts.graphic.LinearGradient(
                                0, 0, 0, 1,
                                [
                                    {offset: 0, color: '#83bff6'},
                                    {offset: 0.5, color: '#188df0'},
                                    {offset: 1, color: '#188df0'}
                                ]
                            )
                        },
                        emphasis: {
                            color: new echarts.graphic.LinearGradient(
                                0, 0, 0, 1,
                                [
                                    {offset: 0, color: '#2378f7'},
                                    {offset: 0.7, color: '#2378f7'},
                                    {offset: 1, color: '#83bff6'}
                                ]
                            )
                        }
                    },
                    data: list
                }
            ]
        };
        chart2 = echarts.init(document.getElementById('chart2')).setOption(option, true);
    }

    function getTypeData(){
        var obj = {
            "energyTypeId":'electricity',
            "title":'万元GDP电耗',
            "unit":'kWh/万元'
        };
        switch(navThis){
            case 'electricity':
                obj.energyTypeId = 'gdpElectricity';
                obj.title = '万元GDP电耗';
                obj.unit = 'kWh/万元';
                break;
            case 'water':
                obj.energyTypeId = 'gdpWater';
                obj.title = '万元GDP水耗';
                obj.unit = 't/万元';
                break;
            case 'energy':
                obj.energyTypeId = 'gdpStdCoal';
                obj.title = '万元GDP能耗';
                obj.unit = 'tce/万元';
                break;
            case 'addValue':
                obj.energyTypeId = 'addValueStdCoal';
                obj.title = '万元工业增加值能耗';
                obj.unit = 'tce/万元';
                break;
        }
        return obj;
    }

    function initYear(){
        var date = new Date();
        var nowYear = date.getFullYear();
        var str = '<option value="'+nowYear+'">'+nowYear+'</option>';
        for(var i=0;i<10;i++){
            nowYear = nowYear - 1;
            str += '<option value="'+nowYear+'">'+nowYear+'</option>';
        }
        $("#year").html(str);
        form.render('select');
    }

    function funcsAuthority(){
        initYear();
        getBarChart();
    }

    var indexLoading;
    function load(){
        indexLoading =layer.load(1, {shade: [0.3, '#fff']});
    }
    function disLoad(){
        layer.close(indexLoading);
    }
});