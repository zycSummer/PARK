package com.jet.cloud.deepmind.repository;

import com.jet.cloud.deepmind.entity.SysParameter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author zhuyicheng
 * @create 2019/10/23 15:02
 * @desc
 */
@Repository
public interface SysParameterRepo extends JpaRepository<SysParameter, Integer> {

    SysParameter findByParaId(String paraId);

    List<SysParameter> findAllByParaId(String paraId);

    @Query("select paraValue from SysParameter where paraId=?1")
    String findParaValue(String paraId);
}
