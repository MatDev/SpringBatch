package com.xanxs.config_api.repository;


import com.xanxs.config_api.domain.entity.FieldTransformation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FieldTransformationRepository extends JpaRepository<FieldTransformation, Long> {

    List<FieldTransformation> findAllByFieldIdOrderByOrderIndexAsc(Long fieldId);

    boolean existsByFieldIdAndOrderIndex(Long fieldId, Integer orderIndex);

    // Máximo orderIndex para autoincremento
    @Query("""
        SELECT COALESCE(MAX(t.orderIndex), 0)
        FROM FieldTransformation t
        WHERE t.field.id = :fieldId
    """)
    Integer findMaxOrderIndexByFieldId(Long fieldId);
}