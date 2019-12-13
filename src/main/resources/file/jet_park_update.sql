-- 2019.12.12
-- 将按钮表（tb_sys_menu_function）中 -[MENU1001][菜单标识] - [Para_Delete][功能标识]的[method][请求方式]修改为"POST"
UPDATE `tb_sys_menu_function` SET `method` = 'POST' WHERE `menu_id` = 'MENU1001' AND `function_id` = 'Para_Delete';

-- 将按钮表（tb_sys_menu_function）中 -[MENU1001][菜单标识] - [Para_Add][功能标识]的[url][接口地址]修改为 "/reportParaDetail/add"
UPDATE `tb_sys_menu_function` SET `url` = '/reportParaDetail/add' WHERE `menu_id` = 'MENU1001' AND `function_id` = 'Para_Add';

-- 将按钮表（tb_sys_menu_function）中 -[MENU1001][菜单标识] - [Para_Modify][功能标识]的[url][接口地址]修改为 "/reportParaDetail/edit"
UPDATE `tb_sys_menu_function` SET `url` = '/reportParaDetail/edit' WHERE `menu_id` = 'MENU1001' AND `function_id` = 'Para_Modify';

-- 连云港平台新增对标管理模块
-- 新增对标对象信息表
CREATE TABLE `tb_obj_benchmarking_obj` (
  `seq_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `obj_type` enum('PARK','SITE') NOT NULL COMMENT '对象类型',
  `obj_id` varchar(30) NOT NULL COMMENT '对象标识',
  `benchmarking_obj_id` varchar(30) NOT NULL COMMENT '对标对象标识',
  `benchmarking_obj_name` varchar(30) NOT NULL COMMENT '对标对象名称',
  `benchmarking_obj_type` enum('Domestic','International') NOT NULL COMMENT '对标对象类型',
  `sort_id` varchar(10) DEFAULT NULL COMMENT '排序标识',
  `memo` varchar(1000) DEFAULT NULL COMMENT '备注',
  `create_user_id` varchar(30) NOT NULL COMMENT '创建者标识',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_user_id` varchar(30) DEFAULT NULL COMMENT '修改者标识',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`seq_id`),
  UNIQUE KEY `UN_OBJ_BENCHMARKING_OBJ` (`obj_type`,`obj_id`,`benchmarking_obj_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8 COMMENT='对标对象';

-- 新增对标对象指标数据信息表
CREATE TABLE `tb_obj_benchmarking_obj_data` (
  `seq_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `obj_type` enum('PARK','SITE') NOT NULL COMMENT '对象类型',
  `obj_id` varchar(30) NOT NULL COMMENT '对象标识',
  `benchmarking_obj_id` varchar(30) NOT NULL COMMENT '对标对象标识',
  `year` int(11) NOT NULL COMMENT '年',
  `gdp_electricity` double DEFAULT NULL COMMENT '万元GDP电耗（kWh/万元）',
  `gdp_water` double DEFAULT NULL COMMENT '万元GDP水耗（t/万元）',
  `gdp_std_coal` double DEFAULT NULL COMMENT '万元GDP能耗（tce/万元）',
  `add_value_std_coal` double DEFAULT NULL COMMENT '万元工业增加值能耗（kWh/万元）',
  `memo` varchar(1000) DEFAULT NULL COMMENT '备注',
  `create_user_id` varchar(30) NOT NULL COMMENT '创建者标识',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_user_id` varchar(30) DEFAULT NULL COMMENT '修改者标识',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`seq_id`),
  UNIQUE KEY `UN_OBJ_BENCHMARKING_OBJ_DATA` (`obj_type`,`obj_id`,`benchmarking_obj_id`,`year`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8 COMMENT='对标对象指标数据';

-- 在一级菜单为对标管理下新增指标排名和指标数据两个二级菜单
INSERT INTO `tb_sys_menu` ( `menu_id`, `menu_name`, `parent_id`, `icon`, `sort_id`, `method`, `url`, `memo`, `create_user_id`, `create_time`, `update_user_id`, `update_time` )
VALUES
  ( 'MENU0901', '指标排名', 'MENU0900', 'fa fa-window-restore', '10020', 'GET', '/admin/indexRanking', NULL, 'chuzhenbin', '2019-12-11 09:33:01', NULL, NULL );
INSERT INTO `tb_sys_menu` ( `menu_id`, `menu_name`, `parent_id`, `icon`, `sort_id`, `method`, `url`, `memo`, `create_user_id`, `create_time`, `update_user_id`, `update_time` )
VALUES
  ( 'MENU0902', '指标数据', 'MENU0900', 'fa fa-window-restore', '10021', 'GET', '/admin/indexData', NULL, 'chuzhenbin', '2019-12-11 09:35:23', NULL, NULL );

-- 新增指标排名[menu_id=MENU0901]页面菜单方法
INSERT INTO `tb_sys_menu_function` ( `menu_id`, `function_id`, `function_name`, `function_desc`, `method`, `url`, `memo`, `create_user_id`, `create_time`, `update_user_id`, `update_time` )
VALUES
  ( 'MENU0901', 'Query', '查询', '查询', 'POST', '/benchmarkingRanking/queryObj', NULL, 'chuzhenbin', '2019-10-24 15:37:00', NULL, NULL );

-- 新增指标数据[menu_id=MENU0901]页面菜单方法
INSERT INTO `tb_sys_menu_function` ( `menu_id`, `function_id`, `function_name`, `function_desc`, `method`, `url`, `memo`, `create_user_id`, `create_time`, `update_user_id`, `update_time` )
VALUES
  ( 'MENU0902', 'LeftQuery', '左侧查询', '左侧查询', 'POST', '/benchmarkingObj/query', NULL, 'chuzhenbin', '2019-12-11 11:40:35', NULL, NULL );
INSERT INTO `tb_sys_menu_function` ( `menu_id`, `function_id`, `function_name`, `function_desc`, `method`, `url`, `memo`, `create_user_id`, `create_time`, `update_user_id`, `update_time` )
VALUES
  ( 'MENU0902', 'LeftAdd', '左侧添加', '左侧添加', 'POST', '/benchmarkingObj/add', NULL, 'chuzhenbin', '2019-12-11 13:07:19', NULL, NULL );
INSERT INTO `tb_sys_menu_function` ( `menu_id`, `function_id`, `function_name`, `function_desc`, `method`, `url`, `memo`, `create_user_id`, `create_time`, `update_user_id`, `update_time` )
VALUES
  ( 'MENU0902', 'LeftEdit', '左侧修改', '左侧修改', 'POST', '/benchmarkingObj/edit', NULL, 'chuzhenbin', '2019-12-11 13:39:55', NULL, NULL );
INSERT INTO `tb_sys_menu_function` ( `menu_id`, `function_id`, `function_name`, `function_desc`, `method`, `url`, `memo`, `create_user_id`, `create_time`, `update_user_id`, `update_time` )
VALUES
  ( 'MENU0902', 'LeftDel', '左侧删除', '左侧删除', 'POST', '/benchmarkingObj/delete', NULL, 'chuzhenbin', '2019-12-11 13:51:25', NULL, NULL );
INSERT INTO `tb_sys_menu_function` ( `menu_id`, `function_id`, `function_name`, `function_desc`, `method`, `url`, `memo`, `create_user_id`, `create_time`, `update_user_id`, `update_time` )
VALUES
  ( 'MENU0902', 'RightQuery', '右侧查询', '右侧查询', 'POST', '/benchmarkingObjData/query', NULL, 'chuzhenbin', '2019-12-11 14:17:50', NULL, NULL );
INSERT INTO `tb_sys_menu_function` ( `menu_id`, `function_id`, `function_name`, `function_desc`, `method`, `url`, `memo`, `create_user_id`, `create_time`, `update_user_id`, `update_time` )
VALUES
  ( 'MENU0902', 'RightAdd', '右侧添加', '右侧添加', 'POST', '/benchmarkingObjData/add', NULL, 'chuzhenbin', '2019-12-11 14:18:20', NULL, NULL );
INSERT INTO `tb_sys_menu_function` ( `menu_id`, `function_id`, `function_name`, `function_desc`, `method`, `url`, `memo`, `create_user_id`, `create_time`, `update_user_id`, `update_time` )
VALUES
  ( 'MENU0902', 'RightEdit', '右侧修改', '右侧修改', 'POST', '/benchmarkingObjData/edit', NULL, 'chuzhenbin', '2019-12-11 14:18:54', NULL, NULL );
INSERT INTO `tb_sys_menu_function` ( `menu_id`, `function_id`, `function_name`, `function_desc`, `method`, `url`, `memo`, `create_user_id`, `create_time`, `update_user_id`, `update_time` )
VALUES
  ( 'MENU0902', 'RightDel', '右侧删除', '右侧删除', 'GET', '/benchmarkingObjData/delete/*', NULL, 'chuzhenbin', '2019-12-11 14:20:14', NULL, NULL );


-- 删除role_id='ROLE2' and menu_id='MENU0901' and menu_id='MENU0902'对应的菜单方法
delete FROM `tb_sys_role_mapping_menu_function` where role_id='ROLE2' and menu_id in ('MENU0901','MENU0902');

-- 新增role_id='ROLE2' and menu_id='MENU0901'对应的菜单方法
INSERT INTO `tb_sys_role_mapping_menu_function` ( `role_id`, `menu_id`, `function_id`, `memo`, `create_user_id`, `create_time`, `update_user_id`, `update_time` )
VALUES
  ( 'ROLE2', 'MENU0901', 'Query', NULL, 'chuzhenbin', '2019-12-12 15:49:21', NULL, NULL );

-- 新增role_id='ROLE2' and menu_id='MENU0902'对应的菜单方法
INSERT INTO `tb_sys_role_mapping_menu_function` ( `role_id`, `menu_id`, `function_id`, `memo`, `create_user_id`, `create_time`, `update_user_id`, `update_time` )
VALUES
  ( 'ROLE2', 'MENU0902', 'LeftQuery', NULL, 'chuzhenbin', '2019-12-12 15:50:12', NULL, NULL );
INSERT INTO `tb_sys_role_mapping_menu_function` ( `role_id`, `menu_id`, `function_id`, `memo`, `create_user_id`, `create_time`, `update_user_id`, `update_time` )
VALUES
  ( 'ROLE2', 'MENU0902', 'LeftAdd', NULL, 'chuzhenbin', '2019-12-12 15:52:16', NULL, NULL );
INSERT INTO `tb_sys_role_mapping_menu_function` ( `role_id`, `menu_id`, `function_id`, `memo`, `create_user_id`, `create_time`, `update_user_id`, `update_time` )
VALUES
  ( 'ROLE2', 'MENU0902', 'LeftEdit', NULL, 'chuzhenbin', '2019-12-12 15:52:45', NULL, NULL );
INSERT INTO `tb_sys_role_mapping_menu_function` ( `role_id`, `menu_id`, `function_id`, `memo`, `create_user_id`, `create_time`, `update_user_id`, `update_time` )
VALUES
  ( 'ROLE2', 'MENU0902', 'LeftDel', NULL, 'chuzhenbin', '2019-12-12 15:53:17', NULL, NULL );
INSERT INTO `tb_sys_role_mapping_menu_function` ( `role_id`, `menu_id`, `function_id`, `memo`, `create_user_id`, `create_time`, `update_user_id`, `update_time` )
VALUES
  ( 'ROLE2', 'MENU0902', 'RightQuery', NULL, 'chuzhenbin', '2019-12-12 15:54:44', NULL, NULL );
INSERT INTO `tb_sys_role_mapping_menu_function` ( `role_id`, `menu_id`, `function_id`, `memo`, `create_user_id`, `create_time`, `update_user_id`, `update_time` )
VALUES
  ( 'ROLE2', 'MENU0902', 'RightAdd', NULL, 'chuzhenbin', '2019-12-12 15:55:38', NULL, NULL );
INSERT INTO `tb_sys_role_mapping_menu_function` ( `role_id`, `menu_id`, `function_id`, `memo`, `create_user_id`, `create_time`, `update_user_id`, `update_time` )
VALUES
  ( 'ROLE2', 'MENU0902', 'RightEdit', NULL, 'chuzhenbin', '2019-12-12 15:56:17', NULL, NULL );
INSERT INTO `tb_sys_role_mapping_menu_function` ( `role_id`, `menu_id`, `function_id`, `memo`, `create_user_id`, `create_time`, `update_user_id`, `update_time` )
VALUES
  ( 'ROLE2', 'MENU0902', 'RightDel', NULL, 'chuzhenbin', '2019-12-12 15:56:45', NULL, NULL );


-- 连云港平台新增设备管理模块
-- 新增对象设备信息表
CREATE TABLE `tb_obj_equip` (
  `seq_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `obj_type` enum('PARK','SITE') NOT NULL COMMENT '对象类型',
  `obj_id` varchar(30) NOT NULL COMMENT '对象标识',
  `equip_id` varchar(30) NOT NULL COMMENT '设备标识',
  `equip_name` varchar(50) NOT NULL COMMENT '设备名称',
  `equip_sys_id` varchar(30) NOT NULL COMMENT '设备系统标识',
  `manufacturer` varchar(50) DEFAULT NULL COMMENT '厂家',
  `model` varchar(30) DEFAULT NULL COMMENT '型号',
  `location` varchar(100) DEFAULT NULL COMMENT '位置',
  `production_date` date DEFAULT NULL COMMENT '生产日期',
  `first_use_date` date DEFAULT NULL COMMENT '投用日期',
  `img_suffix` varchar(20) DEFAULT NULL COMMENT '图片后缀',
  `sort_id` varchar(10) DEFAULT NULL COMMENT '排序标识',
  `memo` varchar(1000) DEFAULT NULL COMMENT '备注',
  `create_user_id` varchar(30) NOT NULL COMMENT '创建者标识',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_user_id` varchar(30) DEFAULT NULL COMMENT '修改者标识',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`seq_id`),
  UNIQUE KEY `UN_OBJ_EQUIP` (`obj_type`,`obj_id`,`equip_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='对象设备信息表';

-- 新增对象设备系统信息表
CREATE TABLE `tb_obj_equip_sys` (
  `seq_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `obj_type` enum('PARK','SITE') NOT NULL COMMENT '对象类型',
  `obj_id` varchar(30) NOT NULL COMMENT '对象标识',
  `equip_sys_id` varchar(30) NOT NULL COMMENT '设备系统标识',
  `equip_sys_name` varchar(50) NOT NULL COMMENT '设备系统名称',
  `sort_id` varchar(10) DEFAULT NULL COMMENT '排序标识',
  `memo` varchar(1000) DEFAULT NULL COMMENT '备注',
  `create_user_id` varchar(30) NOT NULL COMMENT '创建者标识',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_user_id` varchar(30) DEFAULT NULL COMMENT '修改者标识',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`seq_id`),
  UNIQUE KEY `UN_OBJ_EQUIP_SYS` (`obj_type`,`obj_id`,`equip_sys_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8 COMMENT='对象设备系统信息表';

-- 新增设备管理[menu_id=MENU1200]页面菜单方法
INSERT INTO tb_sys_menu_function( `menu_id`, `function_id`, `function_name`, `function_desc`, `method`, `url`, `memo`, `create_user_id`, `create_time`, `update_user_id`, `update_time`) VALUES ('MENU1200', 'LeftAdd', '左侧新增', '左侧新增', 'POST', '/equip/insertEquipSys', NULL, 'chuzhenbin', '2019-12-12 14:21:32', NULL, NULL);
INSERT INTO tb_sys_menu_function( `menu_id`, `function_id`, `function_name`, `function_desc`, `method`, `url`, `memo`, `create_user_id`, `create_time`, `update_user_id`, `update_time`) VALUES ('MENU1200', 'LeftDel', '左侧删除', '左侧删除', 'POST', '/equip/deleteEquipSys', NULL, 'chuzhenbin', '2019-12-12 14:22:32', NULL, NULL);
INSERT INTO tb_sys_menu_function( `menu_id`, `function_id`, `function_name`, `function_desc`, `method`, `url`, `memo`, `create_user_id`, `create_time`, `update_user_id`, `update_time`) VALUES ('MENU1200', 'LeftEdit', '左侧修改', '左侧修改', 'POST', '/equip/updateEquipSys', NULL, 'chuzhenbin', '2019-12-12 14:23:27', NULL, NULL);
INSERT INTO tb_sys_menu_function( `menu_id`, `function_id`, `function_name`, `function_desc`, `method`, `url`, `memo`, `create_user_id`, `create_time`, `update_user_id`, `update_time`) VALUES ('MENU1200', 'LeftQuery', '左侧查询', '左侧查询', 'POST', '/equip/queryEquipSys', NULL, 'chuzhenbin', '2019-12-12 14:24:14', NULL, NULL);
INSERT INTO tb_sys_menu_function( `menu_id`, `function_id`, `function_name`, `function_desc`, `method`, `url`, `memo`, `create_user_id`, `create_time`, `update_user_id`, `update_time`) VALUES ('MENU1200', 'RightAdd', '右侧新增', '右侧新增', 'POST', '/equip/addEquip', NULL, 'chuzhenbin', '2019-12-12 14:25:53', NULL, NULL);
INSERT INTO tb_sys_menu_function( `menu_id`, `function_id`, `function_name`, `function_desc`, `method`, `url`, `memo`, `create_user_id`, `create_time`, `update_user_id`, `update_time`) VALUES ('MENU1200', 'RightDel', '右侧删除', '右侧删除', 'POST', '/equip/deleteEquip', NULL, 'chuzhenbin', '2019-12-12 14:26:39', NULL, NULL);
INSERT INTO tb_sys_menu_function( `menu_id`, `function_id`, `function_name`, `function_desc`, `method`, `url`, `memo`, `create_user_id`, `create_time`, `update_user_id`, `update_time`) VALUES ('MENU1200', 'RightEdit', '右侧修改', '右侧修改', 'POST', '/equip/editEquip', NULL, 'chuzhenbin', '2019-12-12 14:27:48', NULL, NULL);
INSERT INTO tb_sys_menu_function( `menu_id`, `function_id`, `function_name`, `function_desc`, `method`, `url`, `memo`, `create_user_id`, `create_time`, `update_user_id`, `update_time`) VALUES ('MENU1200', 'RightQuery', '右侧查询', '右侧查询', 'POST', '/equip/queryEquip', NULL, 'chuzhenbin', '2019-12-12 14:28:49', NULL, NULL);
INSERT INTO tb_sys_menu_function( `menu_id`, `function_id`, `function_name`, `function_desc`, `method`, `url`, `memo`, `create_user_id`, `create_time`, `update_user_id`, `update_time`) VALUES ('MENU1200', 'show_image', '查看图片', '查看图片', 'POST', '/equip/queryImage', NULL, 'chuzhenbin', '2019-12-12 14:32:37', NULL, NULL);
INSERT INTO tb_sys_menu_function( `menu_id`, `function_id`, `function_name`, `function_desc`, `method`, `url`, `memo`, `create_user_id`, `create_time`, `update_user_id`, `update_time`) VALUES ('MENU1200', 'equip_import', '导入', '导入', 'POST', '/equip/importExcel', NULL, 'chuzhenbin', '2019-12-12 14:41:50', NULL, NULL);
INSERT INTO tb_sys_menu_function( `menu_id`, `function_id`, `function_name`, `function_desc`, `method`, `url`, `memo`, `create_user_id`, `create_time`, `update_user_id`, `update_time`) VALUES ('MENU1200', 'equip_export', '导出', '导出', 'GET', '/equip/exportExcel', NULL, 'chuzhenbin', '2019-12-12 14:44:19', NULL, NULL);

-- 删除role_id='ROLE2' and menu_id='MENU1200'对应的菜单方法
delete FROM `tb_sys_role_mapping_menu_function` where role_id='ROLE2' and menu_id='MENU1200';

-- 新增role_id='ROLE2' and menu_id='MENU1200'对应的菜单方法
INSERT INTO `tb_sys_role_mapping_menu_function`(`role_id`, `menu_id`, `function_id`, `memo`, `create_user_id`, `create_time`, `update_user_id`, `update_time`) VALUES ('ROLE2', 'MENU1200', 'LeftAdd', NULL, 'chuzhenbin', '2019-12-12 15:26:36', NULL, NULL);

INSERT INTO `tb_sys_role_mapping_menu_function`(`role_id`, `menu_id`, `function_id`, `memo`, `create_user_id`, `create_time`, `update_user_id`, `update_time`) VALUES ('ROLE2', 'MENU1200', 'LeftDel', NULL, 'chuzhenbin', '2019-12-12 15:26:36', NULL, NULL);

INSERT INTO `tb_sys_role_mapping_menu_function`(`role_id`, `menu_id`, `function_id`, `memo`, `create_user_id`, `create_time`, `update_user_id`, `update_time`) VALUES ('ROLE2', 'MENU1200', 'LeftEdit', NULL, 'chuzhenbin', '2019-12-12 15:26:36', NULL, NULL);

INSERT INTO `tb_sys_role_mapping_menu_function`(`role_id`, `menu_id`, `function_id`, `memo`, `create_user_id`, `create_time`, `update_user_id`, `update_time`) VALUES ('ROLE2', 'MENU1200', 'LeftQuery', NULL, 'chuzhenbin', '2019-12-12 15:26:36', NULL, NULL);

INSERT INTO `tb_sys_role_mapping_menu_function`(`role_id`, `menu_id`, `function_id`, `memo`, `create_user_id`, `create_time`, `update_user_id`, `update_time`) VALUES ('ROLE2', 'MENU1200', 'RightAdd', NULL, 'chuzhenbin', '2019-12-12 15:26:36', NULL, NULL);

INSERT INTO `tb_sys_role_mapping_menu_function`(`role_id`, `menu_id`, `function_id`, `memo`, `create_user_id`, `create_time`, `update_user_id`, `update_time`) VALUES ('ROLE2', 'MENU1200', 'RightDel', NULL, 'chuzhenbin', '2019-12-12 15:26:36', NULL, NULL);

INSERT INTO `tb_sys_role_mapping_menu_function`(`role_id`, `menu_id`, `function_id`, `memo`, `create_user_id`, `create_time`, `update_user_id`, `update_time`) VALUES ('ROLE2', 'MENU1200', 'RightEdit', NULL, 'chuzhenbin', '2019-12-12 15:26:36', NULL, NULL);

INSERT INTO `tb_sys_role_mapping_menu_function`(`role_id`, `menu_id`, `function_id`, `memo`, `create_user_id`, `create_time`, `update_user_id`, `update_time`) VALUES ('ROLE2', 'MENU1200', 'RightQuery', NULL, 'chuzhenbin', '2019-12-12 15:26:36', NULL, NULL);

INSERT INTO `tb_sys_role_mapping_menu_function`(`role_id`, `menu_id`, `function_id`, `memo`, `create_user_id`, `create_time`, `update_user_id`, `update_time`) VALUES ('ROLE2', 'MENU1200', 'show_image', NULL, 'chuzhenbin', '2019-12-12 15:26:36', NULL, NULL);

INSERT INTO `tb_sys_role_mapping_menu_function`(`role_id`, `menu_id`, `function_id`, `memo`, `create_user_id`, `create_time`, `update_user_id`, `update_time`) VALUES ('ROLE2', 'MENU1200', 'equip_import', NULL, 'chuzhenbin', '2019-12-12 15:26:36', NULL, NULL);

INSERT INTO `tb_sys_role_mapping_menu_function`(`role_id`, `menu_id`, `function_id`, `memo`, `create_user_id`, `create_time`, `update_user_id`, `update_time`) VALUES ('ROLE2', 'MENU1200', 'equip_export', NULL, 'chuzhenbin', '2019-12-12 15:26:36', NULL, NULL);




