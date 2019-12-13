package com.jet.cloud.deepmind.repository;

import com.jet.cloud.deepmind.entity.DataSource;
import com.jet.cloud.deepmind.entity.Meter;
import com.jet.cloud.deepmind.entity.UserGroupMappingObj;
import com.jet.cloud.deepmind.model.ButtonVO;
import com.jet.cloud.deepmind.model.ComprehensiveShowVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author zhuyicheng
 * @create 2019/10/24 10:46
 * @desc 公共Repo(原生SQL)
 */
@Repository
@Transactional
public class CommonRepo {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<ComprehensiveShowVO> queryParkDetails(String userGroupId) {
        String sql = " SELECT a.*,b.longitude,b.latitude,b.scale,b.park_name,b.rtdb_tenant_id " +
                " FROM `tb_sys_user_group_mapping_obj` a LEFT JOIN tb_park b ON a.`obj_id` = b.`park_id` " +
                " WHERE a.user_group_id = ? " +
                " AND a.obj_type = 'PARK' " +
                " AND b.`park_id` " +
                " IS NOT NULL LIMIT 1 ";
        List<ComprehensiveShowVO> userGroupMappingObj = jdbcTemplate.query(sql, new Object[]{userGroupId}, new BeanPropertyRowMapper<ComprehensiveShowVO>(ComprehensiveShowVO.class));
        return userGroupMappingObj;
    }


    public List<ComprehensiveShowVO> querySiteDetails(String userGroupId) {
        String sql = " SELECT a.*,b.longitude,b.latitude,b.site_name,b.addr,b.rtdb_project_id " +
                " FROM `tb_sys_user_group_mapping_obj` a LEFT JOIN tb_site b ON a.`obj_id` = b.`site_id` " +
                " WHERE a.user_group_id = ? " +
                " AND a.obj_type = 'SITE' " +
                " AND b.`site_id` IS NOT NULL " +
                " AND b.`is_online` = 'Y' " +
                " ORDER BY b.`sort_id`";
        List<ComprehensiveShowVO> userGroupMappingObj = jdbcTemplate.query(sql, new Object[]{userGroupId}
                , new BeanPropertyRowMapper<>(ComprehensiveShowVO.class));
        return userGroupMappingObj;
    }

    public List<ComprehensiveShowVO> querySiteDetails(String userGroupId, @NotNull String keyLike) {
        String sql = " SELECT a.*,b.longitude,b.latitude,b.site_name,b.rtdb_project_id " +
                " FROM `tb_sys_user_group_mapping_obj` a LEFT JOIN tb_site b ON a.`obj_id` = b.`site_id` " +
                " WHERE a.user_group_id = ? " +
                " AND a.obj_type = 'SITE' " +
                " AND b.`site_id` IS NOT NULL " +
                " AND b.`is_online` = 'Y' " +
                "AND (b.site_name LIKE ? OR b.site_id LIKE ?)" +
                " ORDER BY b.`sort_id`";
        List<ComprehensiveShowVO> userGroupMappingObj = jdbcTemplate.query(sql, new Object[]{userGroupId, "%" + keyLike + "%", "%" + keyLike + "%"}
                , new BeanPropertyRowMapper<>(ComprehensiveShowVO.class));
        return userGroupMappingObj;
    }

    public List<DataSource> queryDatasource(String energyTypeId, String energyParaId, String userId) {
        String sql = "SELECT * FROM `tb_obj_data_source`" +
                " WHERE obj_type = 'SITE'" +
                " AND energy_type_id = ? " +
                " AND energy_para_id = ? " +
                " AND obj_id in " +
                "  ( " +
                "       SELECT obj_id FROM " +
                "       `tb_sys_user_group_mapping_obj` " +
                "       WHERE obj_type = 'SITE' " +
                "       AND user_group_id = " +
                "           (" +
                "               SELECT user_group_id FROM `tb_sys_user` WHERE user_id = ? " +
                "           )" +
                " );";
        List<DataSource> dataSources = jdbcTemplate.query(sql, new Object[]{energyTypeId, energyParaId, userId}, new BeanPropertyRowMapper<DataSource>(DataSource.class));
        return dataSources;
    }

    public List<ButtonVO> queryAllButtonsByMenuUrl(String url) {
        String sql = "SELECT f.* " +
                "FROM tb_sys_menu_function f " +
                "LEFT JOIN tb_sys_menu m ON m.menu_id = f.menu_id " +
                "WHERE m.url = ?;";
        return jdbcTemplate.query(sql, new Object[]{url}, new BeanPropertyRowMapper<>(ButtonVO.class));
    }


    public Meter queryMeterByMeterId(String meterId) {
        String sql = " select" +
                "        CONCAT(obj_id,'.',meter_id) meter_id, meter_name, energy_type_id, memo " +
                "      from `tb_obj_meter` " +
                "      where obj_type = 'SITE' AND CONCAT(obj_id,'.',meter_id) = ?;";
        Meter meter = jdbcTemplate.queryForObject(sql, new Object[]{meterId}, new BeanPropertyRowMapper<Meter>(Meter.class));
        return meter;
    }

    public List<Meter> queryMeter() {
        String sql = " select CONCAT(obj_id,'.',meter_id) meter_id, meter_name, energy_type_id, memo from `tb_obj_meter` where obj_type = 'SITE';";
        List<Meter> meters = jdbcTemplate.query(sql, new Object[]{}, new BeanPropertyRowMapper<Meter>(Meter.class));
        return meters;
    }

    public Meter queryMeterTypeByMeterId(String meterId) {
        String sql = " select" +
                "         meter_id, meter_name, energy_type_id " +
                "      from `tb_obj_meter` " +
                "      where obj_type = 'SITE' AND CONCAT(obj_id,'.',meter_id) = ?;";
        Meter meter = jdbcTemplate.queryForObject(sql, new Object[]{meterId}, new BeanPropertyRowMapper<Meter>(Meter.class));
        return meter;
    }

    public String queryEnergyUsageParaIdByMeterId(String meterId) {
        String sql = " SELECT " +
                "           energy_usage_para_id " +
                "      FROM tb_sys_energy_type " +
                "      WHERE energy_type_id = " +
                "           (SELECT " +
                "               energy_type_id " +
                "            FROM tb_obj_meter " +
                "            WHERE obj_type = 'SITE' " +
                "            AND CONCAT(obj_id,'.',meter_id) = ?) ";
        String energyUsageParaId = jdbcTemplate.queryForObject(sql, new Object[]{meterId}, String.class);
        return energyUsageParaId;
    }

    public String queryMeterNextMaxLoadValueByMeterId(String meterId) {
        String sql = " SELECT" +
                "         energy_load_para_id " +
                "      FROM tb_sys_energy_type" +
                "      WHERE energy_type_id = " +
                "       ( SELECT " +
                "           energy_type_id " +
                "         FROM tb_obj_meter " +
                "         WHERE obj_type = 'SITE' " +
                "         AND CONCAT( obj_id, '.', meter_id ) = ? ) ";
        String energyLoadParaId = jdbcTemplate.queryForObject(sql, new Object[]{meterId}, String.class);
        return energyLoadParaId;
    }

    public List<UserGroupMappingObj> queryUserGroupMappingObjs(String userId) {
        String sql = " SELECT * FROM `tb_sys_user_group_mapping_obj` WHERE obj_type = 'SITE' AND user_group_id = ( SELECT user_group_id FROM `tb_sys_user` WHERE user_id = ? )";
        List<UserGroupMappingObj> userGroupMappingObjs = jdbcTemplate.query(sql, new Object[]{userId}, new BeanPropertyRowMapper<UserGroupMappingObj>(UserGroupMappingObj.class));
        return userGroupMappingObjs;
    }
}
