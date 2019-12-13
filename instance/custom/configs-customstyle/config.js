window.isPracticing = window.location.host.indexOf('hightopo') >= 0;

window.hteditor_config = {
    color_select: '#3658EF',
    locale: 'zh',
    locateFileEnabled: !isPracticing,
    componentsVisible: !isPracticing,
    displaysModifiable: !isPracticing,
    symbolsModifiable: !isPracticing,
    componentsModifiable: !isPracticing,
    assetsModifiable: !isPracticing,
    fontPreview: '图扑软件 - Hightopo',
    inspectorTitleHeight: 32,
    settingDefaultValueBeforeSaving: true, //保存图纸前将图标数据绑定默认值设置到图元 attr 上
    explorerMode: 'accordion',
    fileSize: 58,
    expandedTitles: {
        TitleExtension: false
    },
    subConfigs: [
        'custom/configs-customstyle/config-handleEvent.js',
        'custom/configs-customstyle/config-valueTypes.js',
        'custom/configs-customstyle/config-dataBindings.js',
        'custom/configs-customstyle/config-customProperties.js',
        'custom/configs-customstyle/config-onTitleCreating.js',
        'custom/configs-customstyle/config-onTitleCreated.js',
        'custom/configs-customstyle/config-onMainToolbarCreated.js',
        'custom/configs-customstyle/config-onMainMenuCreated.js',
        'custom/configs-customstyle/config-onRightToolbarCreated.js'
    ],
    libs: [
        'custom/libs/echarts.js',
        'custom/libs/vintage.js',
        'custom/libs/roma.js',
        'custom/libs/shine.js',
        'custom/libs/infographic.js',
        'custom/libs/macarons.js',
        'custom/libs/dark.js'
    ],
    appendDisplayConnectActionTypes: ['HostParent', 'CreateEdge', 'CopySize'],
    appendSymbolConnectActionTypes: ['CopySize'],
    appendConnectActions: {
        CopySize: {
            action: function(gv, source, target) {
                if (source instanceof ht.Node && target instanceof ht.Node) {
                    source.setWidth(target.getWidth());
                    source.setHeight(target.getHeight());
                }
            },
            extraInfo: {
                visible: function (gv) {
                    return gv.sm().ld() instanceof ht.Node;
                }
            }
        },
        HostParent: {
            action: function(gv, source, target) {
                if (source instanceof ht.Node && target instanceof ht.Node) {
                    gv.dm().beginTransaction();
                    if (source instanceof ht.Node) source.setHost(target);
                    source.setParent(target);
                    gv.dm().endTransaction();
                }
            },
            extraInfo: {
                delete: {
                    visible: function(gv) {
                        var data = gv.sm().ld();
                        return data instanceof ht.Node && (data.getHost() || data.getParent());
                    },
                    action: function(gv, source) {
                        if (source instanceof ht.Node) {
                            gv.dm().beginTransaction();
                            source.setHost(undefined);
                            source.setParent(undefined);
                            gv.dm().endTransaction();
                        }
                    }
                },
                visible: function (gv) {
                    return gv.sm().ld() instanceof ht.Node;
                }
            }
        },
        CreateEdge: {
            action: function(gv, source, target) {
                if (source instanceof ht.Node && target instanceof ht.Node) {
                    var edge = new ht.Edge(source, target);
                    gv.editView.addData(edge, false, false, true);
                }
            },
            extraInfo: {
                visible: function (gv) {
                    return gv.sm().ld() instanceof ht.Node;
                }
            }
        }
    }
};
