package com.xanxs.config_api.repository;



import com.xanxs.config_api.domain.entity.Template;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TemplateRepository extends JpaRepository<Template, Long> {

    Optional<Template> findByName(String name);

    List<Template> findAllByActiveTrue();

    boolean existsByName(String name);

    // Carga completa con fields y transformaciones en una sola query
    @Query("""
        SELECT t FROM Template t
        LEFT JOIN FETCH t.fields f
        LEFT JOIN FETCH f.transformations
        WHERE t.id = :id
    """)
    Optional<Template> findByIdWithFieldsAndTransformations(Long id);

    // Carga completa con destinations y mappings
    @Query("""
        SELECT t FROM Template t
        LEFT JOIN FETCH t.destinations d
        LEFT JOIN FETCH d.mappings
        WHERE t.id = :id
    """)
    Optional<Template> findByIdWithDestinations(Long id);
}