package com.jet.cloud.deepmind.repository;

import com.jet.cloud.deepmind.entity.SysMenu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Class MenuRepo
 *
 * @package
 */
@Repository
public interface MenuRepo extends JpaRepository<SysMenu, Integer> {

    @Query("select m from SysMenu m order by m.sortId asc")
    List<SysMenu> findAllOrderBySortIdAsc();

    SysMenu findByMenuId(String menuId);
}
