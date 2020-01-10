-- 在tb_park新增world_longitude,world_latitude,profile
ALTER TABLE `tb_park` ADD COLUMN `world_longitude` DOUBLE NULL COMMENT '世界地图-经度' AFTER `scale`;
ALTER TABLE `tb_park` ADD COLUMN `world_latitude` DOUBLE NULL COMMENT '世界地图-纬度' AFTER `world_longitude`;
ALTER TABLE `tb_park` ADD COLUMN `profile` text NULL COMMENT '简介' AFTER `world_latitude`;
ALTER TABLE `tb_park` ADD COLUMN `img_suffix` VARCHAR(20) NULL COMMENT '园区图片后缀' AFTER `profile`;

-- 在tb_site新增world_longitude,world_latitude,profile
ALTER TABLE `tb_site` ADD COLUMN `world_longitude` double NULL COMMENT '世界地图-经度' AFTER `latitude`;
ALTER TABLE `tb_site` ADD COLUMN `world_latitude` double NULL COMMENT '世界地图-纬度' AFTER `world_longitude`;
ALTER TABLE `tb_site` ADD COLUMN `profile` text NULL COMMENT '简介' AFTER `rtdb_project_id`;

-- 在tb_obj_benchmarking_obj新增world_longitude,world_latitude
ALTER TABLE `tb_obj_benchmarking_obj` ADD COLUMN `world_longitude` double NULL COMMENT '世界地图-经度' AFTER `sort_id`;
ALTER TABLE `tb_obj_benchmarking_obj` ADD COLUMN `world_latitude` double NULL COMMENT '世界地图-纬度' AFTER `world_longitude`;

-- 修改菜单图标
UPDATE tb_sys_menu SET icon='fa fa-desktop' WHERE menu_id='MENU0200';
UPDATE tb_sys_menu SET icon='fa fa-sitemap' WHERE menu_id='MENU0300';
UPDATE tb_sys_menu SET icon='fa fa-line-chart' WHERE menu_id='MENU0400';
UPDATE tb_sys_menu SET icon='fa fa-eye' WHERE menu_id='MENU0401';
UPDATE tb_sys_menu SET icon='fa fa-line-chart' WHERE menu_id='MENU0402';
UPDATE tb_sys_menu SET icon='fa fa-product-hunt' WHERE menu_id='MENU0500';
UPDATE tb_sys_menu SET icon='fa fa-line-chart' WHERE menu_id='MENU0501';
UPDATE tb_sys_menu SET icon='fa fa-calendar' WHERE menu_id='MENU0502';
UPDATE tb_sys_menu SET icon='fa fa-bar-chart' WHERE menu_id='MENU0503';
UPDATE tb_sys_menu SET icon='fa fa-pie-chart' WHERE menu_id='MENU0504';
UPDATE tb_sys_menu SET icon='fa fa-bell' WHERE menu_id='MENU0600';
UPDATE tb_sys_menu SET icon='fa fa-pencil-square' WHERE menu_id='MENU0601';
UPDATE tb_sys_menu SET icon='fa fa-bell-o' WHERE menu_id='MENU0602';
UPDATE tb_sys_menu SET icon='fa fa-cloud-upload' WHERE menu_id='MENU0700';
UPDATE tb_sys_menu SET icon='fa fa-pie-chart' WHERE menu_id='MENU0800';
UPDATE tb_sys_menu SET icon='fa fa-align-left' WHERE menu_id='MENU0801';
UPDATE tb_sys_menu SET icon='fa fa-clock-o' WHERE menu_id='MENU0802';
UPDATE tb_sys_menu SET icon='fa fa-tasks' WHERE menu_id='MENU0900';
UPDATE tb_sys_menu SET icon='fa fa-table' WHERE menu_id='MENU1000';
UPDATE tb_sys_menu SET icon='fa fa-pencil-square' WHERE menu_id='MENU1001';
UPDATE tb_sys_menu SET icon='fa fa-tablet' WHERE menu_id='MENU1002';
UPDATE tb_sys_menu SET icon='fa fa-folder' WHERE menu_id='MENU1100';
UPDATE tb_sys_menu SET icon='fa fa-th-list' WHERE menu_id='MENU1200';
UPDATE tb_sys_menu SET icon='fa fa-database' WHERE menu_id='MENU1400';
UPDATE tb_sys_menu SET icon='fa fa-globe' WHERE menu_id='MENU1401';
UPDATE tb_sys_menu SET icon='fa fa-building' WHERE menu_id='MENU1402';
UPDATE tb_sys_menu SET icon='fa fa-microchip' WHERE menu_id='MENU1403';
UPDATE tb_sys_menu SET icon='fa fa-wrench' WHERE menu_id='MENU1404';
UPDATE tb_sys_menu SET icon='fa fa-sitemap' WHERE menu_id='MENU1405';
UPDATE tb_sys_menu SET icon='fa fa-object-group' WHERE menu_id='MENU1406';
UPDATE tb_sys_menu SET icon='fa fa-newspaper-o' WHERE menu_id='MENU1407';
UPDATE tb_sys_menu SET icon='fa fa-calendar-plus-o' WHERE menu_id='MENU1408';
UPDATE tb_sys_menu SET icon='fa fa-jpy' WHERE menu_id='MENU1409';
UPDATE tb_sys_menu SET icon='fa fa-cog' WHERE menu_id='MENU1500';
UPDATE tb_sys_menu SET icon='fa fa-user' WHERE menu_id='MENU1501';
UPDATE tb_sys_menu SET icon='fa fa-users' WHERE menu_id='MENU1502';
UPDATE tb_sys_menu SET icon='fa fa-graduation-cap' WHERE menu_id='MENU1503';
UPDATE tb_sys_menu SET icon='fa fa-bars' WHERE menu_id='MENU1504';
UPDATE tb_sys_menu SET icon='fa fa-bolt' WHERE menu_id='MENU1505';
UPDATE tb_sys_menu SET icon='fa fa-sort-numeric-asc' WHERE menu_id='MENU1506';
UPDATE tb_sys_menu SET icon='fa fa-cogs' WHERE menu_id='MENU1507';
UPDATE tb_sys_menu SET icon='fa fa-file-text-o' WHERE menu_id='MENU1508';
UPDATE tb_sys_menu SET icon='fa fa-bar-chart' WHERE menu_id='MENU0901';
UPDATE tb_sys_menu SET icon='fa fa-pencil-square' WHERE menu_id='MENU0902';
UPDATE tb_sys_menu SET icon='fa fa-info-circle' WHERE menu_id='MENU1410';
UPDATE tb_sys_menu SET icon='fa fa-globe' WHERE menu_id='MENU1600';
