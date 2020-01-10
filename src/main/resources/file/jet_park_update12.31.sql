-- 删除菜单-设备管理的Query
delete FROM `tb_sys_menu_function` where menu_id='MENU1200' and function_id='Query';
delete FROM `tb_sys_role_mapping_menu_function` where menu_id='MENU1200' and function_id='Query';

-- 新增设备管理[menu_id=MENU1200]页面菜单方法
INSERT INTO `tb_sys_menu_function`(`menu_id`, `function_id`, `function_name`, `function_desc`, `method`, `url`, `memo`, `create_user_id`, `create_time`, `update_user_id`, `update_time`) VALUES ('MENU1200', 'equip_download', '模板文件下载', '导入-模板文件下载', 'GET', '/equip/download', NULL, 'chuzhenbin', '2019-12-12 16:46:12', NULL, NULL);

INSERT INTO `tb_sys_role_mapping_menu_function`(`role_id`, `menu_id`, `function_id`, `memo`, `create_user_id`, `create_time`, `update_user_id`, `update_time`) VALUES ('ROLE1', 'MENU1200', 'equip_download', NULL, 'chuzhenbin', '2019-12-26 13:51:11', NULL, NULL);


-- 调整菜单顺序
update tb_sys_menu set sort_id='10001' where menu_id='MENU1600';
update tb_sys_menu set sort_id='10002' where menu_id='MENU0200';
update tb_sys_menu set sort_id='10003' where menu_id='MENU0100';
update tb_sys_menu set sort_id='10004' where menu_id='MENU0300';
update tb_sys_menu set sort_id='10005' where menu_id='MENU0400';
update tb_sys_menu set sort_id='10006' where menu_id='MENU0401';
update tb_sys_menu set sort_id='10007' where menu_id='MENU0402';
update tb_sys_menu set sort_id='10008' where menu_id='MENU0500';
update tb_sys_menu set sort_id='10009' where menu_id='MENU0501';
update tb_sys_menu set sort_id='10010' where menu_id='MENU0502';
update tb_sys_menu set sort_id='10011' where menu_id='MENU0503';
update tb_sys_menu set sort_id='10012' where menu_id='MENU0504';
update tb_sys_menu set sort_id='10013' where menu_id='MENU0600';
update tb_sys_menu set sort_id='10014' where menu_id='MENU0601';
update tb_sys_menu set sort_id='10015' where menu_id='MENU0602';
update tb_sys_menu set sort_id='10016' where menu_id='MENU0700';
update tb_sys_menu set sort_id='10017' where menu_id='MENU0800';
update tb_sys_menu set sort_id='10018' where menu_id='MENU0801';
update tb_sys_menu set sort_id='10019' where menu_id='MENU0802';
update tb_sys_menu set sort_id='10020' where menu_id='MENU0900';
update tb_sys_menu set sort_id='10021' where menu_id='MENU0901';
update tb_sys_menu set sort_id='10022' where menu_id='MENU0902';
update tb_sys_menu set sort_id='10023' where menu_id='MENU1000';
update tb_sys_menu set sort_id='10024' where menu_id='MENU1001';
update tb_sys_menu set sort_id='10025' where menu_id='MENU1002';
update tb_sys_menu set sort_id='10026' where menu_id='MENU1100';
update tb_sys_menu set sort_id='10027' where menu_id='MENU1200';
update tb_sys_menu set sort_id='10028' where menu_id='MENU1400';
update tb_sys_menu set sort_id='10029' where menu_id='MENU1401';
update tb_sys_menu set sort_id='10030' where menu_id='MENU1402';
update tb_sys_menu set sort_id='10031' where menu_id='MENU1403';
update tb_sys_menu set sort_id='10032' where menu_id='MENU1404';
update tb_sys_menu set sort_id='10033' where menu_id='MENU1405';
update tb_sys_menu set sort_id='10034' where menu_id='MENU1406';
update tb_sys_menu set sort_id='10035' where menu_id='MENU1407';
update tb_sys_menu set sort_id='10036' where menu_id='MENU1408';
update tb_sys_menu set sort_id='10037' where menu_id='MENU1409';
update tb_sys_menu set sort_id='10038' where menu_id='MENU1410';
update tb_sys_menu set sort_id='10039' where menu_id='MENU1500';
update tb_sys_menu set sort_id='10040' where menu_id='MENU1501';
update tb_sys_menu set sort_id='10041' where menu_id='MENU1502';
update tb_sys_menu set sort_id='10042' where menu_id='MENU1503';
update tb_sys_menu set sort_id='10043' where menu_id='MENU1504';
update tb_sys_menu set sort_id='10044' where menu_id='MENU1505';
update tb_sys_menu set sort_id='10045' where menu_id='MENU1506';
update tb_sys_menu set sort_id='10046' where menu_id='MENU1507';
update tb_sys_menu set sort_id='10047' where menu_id='MENU1508';

-- 更新企业世界坐标
update tb_site set world_longitude=119.63985, world_latitude=34.556763 where site_id='DGWSCL';
update tb_site set world_longitude=119.632081, world_latitude=34.526566 where site_id='ZJLQJJS';
update tb_site set world_longitude=119.63585, world_latitude=34.566763 where site_id='RTHGCC';
update tb_site set world_longitude=119.615255, world_latitude=34.55775 where site_id='HYRD';
update tb_site set world_longitude=119.622892, world_latitude=34.56275 where site_id='SEBSH';
update tb_site set world_longitude=119.633542, world_latitude=34.560035 where site_id='HGSH';
update tb_site set world_longitude=119.5995, world_latitude=34.533996 where site_id='WBFHB';
update tb_site set world_longitude=119.5995, world_latitude=34.535996 where site_id='PCGS';

-- 更新园区世界坐标及简介
UPDATE tb_park SET world_longitude=119.621849060059, world_latitude=34.5510092565918, PROFILE='&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;徐圩新区是国务院批准设立的国家东中西区域合作示范区的先导区，是江苏沿海开发的主要实施载体，是连云港市委市政府“十三五”规划确定的发展新型临港产业的核心区。新区将依托陆桥经济带，服务中西部，面向东北亚，建成服务中西部地区对外开放的重要门户、东中西产业合作示范基地、区域合作体制机制创新试验区。
</br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;徐圩新区总规划面积约467平方公里，其中，徐圩港区74平方公里，临港产业区153平方公里，适宜布局和发展临港大工业。按照“生态、智能、融合、示范”的发展理念，新区主要发展石化、先进装备制造、节能环保和现代港口物流等主导产业，重点打造世界一流石化产业基地、国家生态工业示范园区、智能化新区，努力发展成为江苏沿海地区新的经济增长极。' WHERE park_id='LYGSHCYJD';


-- 更新对标对象的世界坐标
UPDATE tb_obj_benchmarking_obj SET world_longitude=5.291266, world_latitude=52.132632 WHERE obj_type='PARK' AND obj_id='LYGSHCYJD' AND benchmarking_obj_id='1001';
UPDATE tb_obj_benchmarking_obj SET world_longitude=4.402464, world_latitude=51.219447 WHERE obj_type='PARK' AND obj_id='LYGSHCYJD' AND benchmarking_obj_id='1002';
UPDATE tb_obj_benchmarking_obj SET world_longitude=10.451526, world_latitude=51.165689 WHERE obj_type='PARK' AND obj_id='LYGSHCYJD' AND benchmarking_obj_id='1003';
UPDATE tb_obj_benchmarking_obj SET world_longitude=-95.369803, world_latitude=29.760427 WHERE obj_type='PARK' AND obj_id='LYGSHCYJD' AND benchmarking_obj_id='1004';
UPDATE tb_obj_benchmarking_obj SET world_longitude=103.830319, world_latitude=1.249404 WHERE obj_type='PARK' AND obj_id='LYGSHCYJD' AND benchmarking_obj_id='1005';
UPDATE tb_obj_benchmarking_obj SET world_longitude=121.433517, world_latitude=30.953581 WHERE obj_type='PARK' AND obj_id='LYGSHCYJD' AND benchmarking_obj_id='2001';
UPDATE tb_obj_benchmarking_obj SET world_longitude=114.481934, world_latitude=22.791075 WHERE obj_type='PARK' AND obj_id='LYGSHCYJD' AND benchmarking_obj_id='2002';
UPDATE tb_obj_benchmarking_obj SET world_longitude=121.612823, world_latitude=30.040739 WHERE obj_type='PARK' AND obj_id='LYGSHCYJD' AND benchmarking_obj_id='2003';
UPDATE tb_obj_benchmarking_obj SET world_longitude=118.744408, world_latitude=32.21788 WHERE obj_type='PARK' AND obj_id='LYGSHCYJD' AND benchmarking_obj_id='2004';
UPDATE tb_obj_benchmarking_obj SET world_longitude=117.557777, world_latitude=38.74189 WHERE obj_type='PARK' AND obj_id='LYGSHCYJD' AND benchmarking_obj_id='2005';
UPDATE tb_obj_benchmarking_obj SET world_longitude=117.642967, world_latitude=23.929794 WHERE obj_type='PARK' AND obj_id='LYGSHCYJD' AND benchmarking_obj_id='2006';
UPDATE tb_obj_benchmarking_obj SET world_longitude=121.646233, world_latitude=38.923431 WHERE obj_type='PARK' AND obj_id='LYGSHCYJD' AND benchmarking_obj_id='2007';

