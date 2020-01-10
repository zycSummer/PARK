-- 在表（tb_sys_log）中添加字段operate_ip
ALTER TABLE `tb_sys_log` ADD COLUMN `operate_ip` varchar(50) NULL COMMENT '操作ip' AFTER `operate_time`;

-- 连云港平台基础数据中新增对象公告模块
-- 新增对象公告
CREATE TABLE `tb_obj_notice` (
  `seq_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `obj_type` enum('PARK','SITE') NOT NULL COMMENT '对象类型',
  `obj_id` varchar(30) NOT NULL COMMENT '对象标识',
  `notice_title` varchar(30) DEFAULT NULL COMMENT '公告标题',
  `notice_content` text COMMENT '公告内容',
  `memo` varchar(1000) DEFAULT NULL COMMENT '备注',
  `create_user_id` varchar(30) NOT NULL COMMENT '创建者标识',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_user_id` varchar(30) DEFAULT NULL COMMENT '修改者标识',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`seq_id`),
  UNIQUE KEY `UN_OBJ_NOTICE` (`obj_type`,`obj_id`,`create_time`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='对象公告信息';

-- 新增基础数据中的对象公告[menu_id=MENU1410]菜单
INSERT INTO `tb_sys_menu`( `menu_id`, `menu_name`, `parent_id`, `icon`, `sort_id`, `method`, `url`, `memo`, `create_user_id`, `create_time`, `update_user_id`, `update_time`) VALUES ( 'MENU1410', '对象公告', 'MENU1400', 'fa fa-window-restore', '10037', 'GET', '/admin/objectAnnouncement', NULL , 'chuzhenbin', '2019-12-23 13:51:57', NULL, NULL);

-- 新增基础数据对象公告[menu_id=MENU1410]页面菜单方法
INSERT INTO `tb_sys_menu_function`( `menu_id`, `function_id`, `function_name`, `function_desc`, `method`, `url`, `memo`, `create_user_id`, `create_time`, `update_user_id`, `update_time`) VALUES ( 'MENU1410', 'Query', '查询', '查询', 'POST', '/notice/query', NULL, 'chuzhenbin', '2019-12-23 14:06:55', NULL, NULL);
INSERT INTO `tb_sys_menu_function`( `menu_id`, `function_id`, `function_name`, `function_desc`, `method`, `url`, `memo`, `create_user_id`, `create_time`, `update_user_id`, `update_time`) VALUES ( 'MENU1410', 'Add', '新增', '新增', 'POST', '/notice/add', NULL, 'chuzhenbin', '2019-12-23 14:07:49', NULL, NULL);
INSERT INTO `tb_sys_menu_function`( `menu_id`, `function_id`, `function_name`, `function_desc`, `method`, `url`, `memo`, `create_user_id`, `create_time`, `update_user_id`, `update_time`) VALUES ( 'MENU1410', 'Modify', '修改', '修改', 'POST', '/notice/edit', NULL, 'chuzhenbin', '2019-12-23 14:08:20', NULL, NULL);
INSERT INTO `tb_sys_menu_function`( `menu_id`, `function_id`, `function_name`, `function_desc`, `method`, `url`, `memo`, `create_user_id`, `create_time`, `update_user_id`, `update_time`) VALUES ( 'MENU1410', 'Delete', '删除', '删除', 'GET', '/notice/delete/*', NULL, 'chuzhenbin', '2019-12-23 14:09:10', NULL, NULL);

-- 新增基础数据对象公告[menu_id=MENU1410]角色菜单关联表
INSERT INTO `tb_sys_role_mapping_menu`(`role_id`, `menu_id`, `memo`, `create_user_id`, `create_time`, `update_user_id`, `update_time`) VALUES ('ROLE1', 'MENU1410', NULL, 'chuzhenbin', '2019-12-13 15:48:43', NULL, NULL);

-- 删除role_id='ROLE1' and menu_id='MENU1410'对应的菜单方法
delete FROM `tb_sys_role_mapping_menu_function` where role_id='ROLE1' and menu_id='MENU1410';

-- 新增role_id='ROLE1' and menu_id='MENU1410'对应的菜单方法
INSERT INTO `tb_sys_role_mapping_menu_function`( `role_id`, `menu_id`, `function_id`, `memo`, `create_user_id`, `create_time`, `update_user_id`, `update_time`) VALUES ('ROLE1', 'MENU1410', 'Query', NULL, 'chuzhenbin', '2019-12-23 14:05:02', NULL, NULL);
INSERT INTO `tb_sys_role_mapping_menu_function`( `role_id`, `menu_id`, `function_id`, `memo`, `create_user_id`, `create_time`, `update_user_id`, `update_time`) VALUES ('ROLE1', 'MENU1410', 'Add', NULL, 'chuzhenbin', '2019-12-23 14:05:02', NULL, NULL);
INSERT INTO `tb_sys_role_mapping_menu_function`( `role_id`, `menu_id`, `function_id`, `memo`, `create_user_id`, `create_time`, `update_user_id`, `update_time`) VALUES ('ROLE1', 'MENU1410', 'Modify', NULL, 'chuzhenbin', '2019-12-23 14:05:02', NULL, NULL);
INSERT INTO `tb_sys_role_mapping_menu_function`( `role_id`, `menu_id`, `function_id`, `memo`, `create_user_id`, `create_time`, `update_user_id`, `update_time`) VALUES ('ROLE1', 'MENU1410', 'Delete', NULL, 'chuzhenbin', '2019-12-23 14:05:02', NULL, NULL);



insert into `tb_sys_parameter` (`para_id`, `para_value`, `para_desc`, `memo`, `create_user_id`, `create_time`, `update_user_id`, `update_time`) values('thisDiscardMinutesBeforeNow','5','综合展示-实时负荷、项目能耗-当日负荷、用能监测-历史数据等查询当日、当月、当年曲线时将 当前时间-此配置项值 至 当前时间 这段时间内的值设置为NULL（不包含开始，包含结束时间）',NULL,'chuzhenbin','2019-12-24 09:18:06',NULL,NULL);


