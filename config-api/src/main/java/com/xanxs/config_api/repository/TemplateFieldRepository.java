package com.xanxs.config_api.repository;



import com.xanxs.config_api.domain.entity.TemplateField;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TemplateFieldRepository extends JpaRepository<TemplateField, Long> {

    List<TemplateField> findAllByTemplateIdOrderByOrderIndexAsc(Long templateId);

    boolean existsByTemplateIdAndName(Long templateId, String name);

    Optional<TemplateField> findByTemplateIdAndName(Long templateId, String name);

    // Campo con sus transformaciones
    @Query("""
        SELECT f FROM TemplateField f
        LEFT JOIN FETCH f.transformations
        WHERE f.id = :id
    """)
    Optional<TemplateField> findByIdWithTransformations(Long id);

    // Máximo orderIndex para autoincremento
    @Query("""
        SELECT COALESCE(MAX(f.orderIndex), 0)
        FROM TemplateField f
        WHERE f.template.id = :templateId
    """)
    Integer findMaxOrderIndexByTemplateId(Long templateId);
}
