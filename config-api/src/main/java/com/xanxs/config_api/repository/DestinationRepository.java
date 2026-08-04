package com.xanxs.config_api.repository;



import com.xanxs.config_api.domain.entity.Destination;
import com.xanxs.config_api.domain.enums.DestinationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DestinationRepository extends JpaRepository<Destination, Long> {

    List<Destination> findAllByTemplateId(Long templateId);

    List<Destination> findAllByTemplateIdAndActiveTrue(Long templateId);

    List<Destination> findAllByTemplateIdAndDestinationType(
            Long templateId, DestinationType destinationType);

    // Destino con sus mappings
    @Query("""
        SELECT d FROM Destination d
        LEFT JOIN FETCH d.mappings
        WHERE d.id = :id
    """)
    Optional<Destination> findByIdWithMappings(Long id);
}

