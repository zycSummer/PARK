
window.isPracticing = window.location.host.indexOf('hightopo') >= 0;

      window.hteditor_config = {
        // color_select: '#FF7733',

        // host: obj.one.host,
        // port: obj.one.port,
        locale: 'zh',
        locateFileEnabled: !isPracticing,
        componentsVisible: !isPracticing,
        displaysModifiable: !isPracticing,
        symbolsModifiable: !isPracticing,
        componentsModifiable: !isPracticing,
        assetsModifiable: !isPracticing,
        fontPreview: '图扑软件 - Hightopo',
        promptBeforeClosing:false,
        settingDefaultValueBeforeSaving: true, //保存图纸前将图标数据绑定默认值设置到图元 attr 上
        expandedTitles: {
            TitleExtension: false
        },
        subConfigs: [
            'custom/configs/config-handleEvent.js',
            'custom/configs/config-valueTypes.js',
            'custom/configs/config-dataBindings.js',
            'custom/configs/config-connectActions.js',
            'custom/configs/config-inspectorFilter.js',
            'custom/configs/config-customProperties.js',
            'custom/configs/config-onTitleCreating.js',
            'custom/configs/config-onTitleCreated.js',
            'custom/configs/config-onMainToolbarCreated.js',
            'custom/configs/config-onMainMenuCreated.js',
            'custom/configs/config-onRightToolbarCreated.js'
        ],
        // explorerMode: 'accordion',
         serviceClass: 'httpService',
        libs: [
            'custom/libs/echarts.js',
            'custom/libs/vintage.js',
            'custom/libs/roma.js',
            'custom/libs/shine.js',
            'custom/libs/infographic.js',
            'custom/libs/macarons.js',
            'custom/libs/httpService.js',
            'custom/libs/dark.js'
        ]
    };
    


