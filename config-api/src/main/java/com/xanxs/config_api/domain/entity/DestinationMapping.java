package com.xanxs.config_api.domain.entity;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "bte_destination_mapping",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_mapping_destination_field",
                columnNames = {"destination_id", "field_name"}
        ))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DestinationMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destination_id", nullable = false)
    private Destination destination;

    @Column(name = "field_name", nullable = false, length = 100)
    private String fieldName;
    // Referencia al nombre del campo en bte_template_field

    @Column(name = "target_column", nullable = false, length = 100)
    private String targetColumn;
    // Nombre columna/atributo/key en el destino

    @Column(name = "order_index", nullable = false)
    private Integer orderIndex;
}
