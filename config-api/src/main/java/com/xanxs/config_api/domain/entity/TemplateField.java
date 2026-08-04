package com.xanxs.config_api.domain.entity;


import com.xanxs.config_api.domain.enums.FieldType;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "bte_template_field",
       uniqueConstraints = @UniqueConstraint(
           name = "uq_field_template_name",
           columnNames = {"template_id", "name"}
       ))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TemplateField {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id", nullable = false)
    private Template template;

    @Column(nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "field_type", nullable = false, length = 30)
    private FieldType fieldType;

    @Column(nullable = false)
    private Integer position;

    // Solo para FIXED length
    private Integer length;

    @Column(nullable = false)
    private Boolean required = false;

    @Column(name = "default_value", length = 255)
    private String defaultValue;

    @Column(name = "order_index", nullable = false)
    private Integer orderIndex;

    // Relación con transformaciones
    @OneToMany(mappedBy = "field",
               cascade = CascadeType.ALL,
               orphanRemoval = true,
               fetch = FetchType.LAZY)
    @OrderBy("orderIndex ASC")
    @Builder.Default
    private List<FieldTransformation> transformations = new ArrayList<>();

    public void addTransformation(FieldTransformation transformation) {
        transformations.add(transformation);
        transformation.setField(this);
    }

    public void removeTransformation(FieldTransformation transformation) {
        transformations.remove(transformation);
        transformation.setField(null);
    }
}