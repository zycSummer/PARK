-- 新增综合概况[menu_id=MENU1600]菜单
INSERT INTO `tb_sys_menu`( `menu_id`, `menu_name`, `parent_id`, `icon`, `sort_id`, `method`, `url`, `memo`, `create_user_id`, `create_time`, `update_user_id`, `update_time`) VALUES ( 'MENU1600', '综合概况', NULL, 'fa fa-globe', '10046', 'GET', '/admin/complexOverview', NULL, 'chuzhenbin', '2019-12-26 13:47:49', NULL, NULL);

-- 新增综合概述[menu_id=MENU16000]页面菜单方法
INSERT INTO `tb_sys_menu_function`( `menu_id`, `function_id`, `function_name`, `function_desc`, `method`, `url`, `memo`, `create_user_id`, `create_time`, `update_user_id`, `update_time`) VALUES ('MENU1600', 'query', '查询', '查询当前所选对象、国际和国内的gdp能耗信息', 'POST', '/comprehensiveSummary/query', NULL, 'chuzhenbin', '2019-12-30 14:56:08', NULL, NULL);
INSERT INTO `tb_sys_menu_function`( `menu_id`, `function_id`, `function_name`, `function_desc`, `method`, `url`, `memo`, `create_user_id`, `create_time`, `update_user_id`, `update_time`) VALUES ('MENU1600', 'queryPosition', '获取位置信息', '查询当前所选对象、国际和国内的位置信息', 'POST', '/comprehensiveSummary/queryPosition', NULL, 'chuzhenbin', '2019-12-30 14:59:26', NULL, NULL);
INSERT INTO `tb_sys_menu_function`( `menu_id`, `function_id`, `function_name`, `function_desc`, `method`, `url`, `memo`, `create_user_id`, `create_time`, `update_user_id`, `update_time`) VALUES ('MENU1600', 'queryAllEnergy', '获取能耗信息', '获取总的能耗信息', 'POST', '/comprehensiveSummary/queryAllEnergy', NULL, 'chuzhenbin', '2019-12-30 15:00:34', NULL, NULL);
INSERT INTO `tb_sys_menu_function`( `menu_id`, `function_id`, `function_name`, `function_desc`, `method`, `url`, `memo`, `create_user_id`, `create_time`, `update_user_id`, `update_time`) VALUES ('MENU1600', 'queryInfo', '查询', '查询基础简介信息', 'POST', '/comprehensiveSummary/queryInfo', NULL, 'chuzhenbin', '2019-12-30 15:01:37', NULL, NULL);

-- 新增综合概述[menu_id=MENU1600]角色菜单关联表
INSERT INTO `tb_sys_role_mapping_menu`( `role_id`, `menu_id`, `memo`, `create_user_id`, `create_time`, `update_user_id`, `update_time`) VALUES ('ROLE1', 'MENU1600', NULL, 'chuzhenbin', '2019-12-26 13:51:11', NULL, NULL);

-- 新增role_id='ROLE1' and menu_id='MENU1600'对应的菜单方法
INSERT INTO `tb_sys_role_mapping_menu_function`( `role_id`, `menu_id`, `function_id`, `memo`, `create_user_id`, `create_time`, `update_user_id`, `update_time`) VALUES ('ROLE1', 'MENU1600', 'Query', NULL, 'chuzhenbin', '2019-12-30 14:56:08', NULL, NULL);
INSERT INTO `tb_sys_role_mapping_menu_function`( `role_id`, `menu_id`, `function_id`, `memo`, `create_user_id`, `create_time`, `update_user_id`, `update_time`) VALUES ('ROLE1', 'MENU1600', 'QueryPosition', NULL, 'chuzhenbin', '2019-12-30 14:56:08', NULL, NULL);
INSERT INTO `tb_sys_role_mapping_menu_function`( `role_id`, `menu_id`, `function_id`, `memo`, `create_user_id`, `create_time`, `update_user_id`, `update_time`) VALUES ('ROLE1', 'MENU1600', 'QueryAllEnergy', NULL, 'chuzhenbin', '2019-12-30 14:56:08', NULL, NULL);
INSERT INTO `tb_sys_role_mapping_menu_function`( `role_id`, `menu_id`, `function_id`, `memo`, `create_user_id`, `create_time`, `update_user_id`, `update_time`) VALUES ('ROLE1', 'MENU1600', 'QueryInfo', NULL, 'chuzhenbin', '2019-12-30 14:56:08', NULL, NULL);


-- 在tb_park新增park_abbr_name
ALTER TABLE `tb_park` ADD COLUMN `park_abbr_name` VARCHAR(30) NOT NULL COMMENT '园区简称' AFTER `park_name`;

-- 在tb_obj_benchmarking_obj新增benchmarking_obj_abbr_name
ALTER TABLE `tb_obj_benchmarking_obj` ADD COLUMN `benchmarking_obj_abbr_name` VARCHAR(30) NOT NULL COMMENT '对标对象简称' AFTER `benchmarking_obj_name`;

-- 修改tb_obj_big_screen表的全部file_path字段(把MENU0200_放在最前面,原先PARK_LYGSHCYJD_mainpage.json改为MENU0200_PARK_LYGSHCYJD_mainpage.json)

-- 修改tb_obj_ht_img表的全部file_path字段(把MENU0401_放在最前面,原先PARK_LYGSHCYJD_1.json改为MENU0401_PARK_LYGSHCYJD_1.json)