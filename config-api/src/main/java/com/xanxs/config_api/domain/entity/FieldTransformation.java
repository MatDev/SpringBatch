package com.xanxs.config_api.domain.entity;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Map;

@Entity
@Table(name = "bte_field_transformation",
       uniqueConstraints = @UniqueConstraint(
           name = "uq_transformation_field_order",
           columnNames = {"field_id", "order_index"}
       ))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FieldTransformation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "field_id", nullable = false)
    private TemplateField field;

    @Column(name = "transformer_name", nullable = false, length = 100)
    private String transformerName;
    /*
        Valores: trim | uppercase | lowercase | parseDate |
        formatDate | replace | removeLeadingZeros |
        toInteger | toLong | formatRut | regexReplace
    */

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, String> parameters;
    /*
        Ejemplos:
        formatDate  → {inputFormat: "yyyyMMdd", outputFormat: "yyyy-MM-dd"}
        replace     → {target: "_", replacement: " "}
        regexReplace→ {pattern: "[^0-9]", replacement: ""}
    */

    @Column(name = "order_index", nullable = false)
    private Integer orderIndex;
}
