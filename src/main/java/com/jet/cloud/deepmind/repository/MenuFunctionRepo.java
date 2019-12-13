package com.jet.cloud.deepmind.repository;

import com.jet.cloud.deepmind.entity.SysMenuFunction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author yhy
 * @create 2019-10-11 15:31
 */
@Repository
public interface MenuFunctionRepo extends JpaRepository<SysMenuFunction, Integer> {

    List<SysMenuFunction> findAllByMenuId(String menuId);

    SysMenuFunction findByMenuIdAndFunctionId(String menuId, String functionId);


}
