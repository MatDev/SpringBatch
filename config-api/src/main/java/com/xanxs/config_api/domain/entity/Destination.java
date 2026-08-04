package com.xanxs.config_api.domain.entity;


import com.xanxs.config_api.domain.enums.DestinationType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Entity
@Table(name = "bte_destination")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Destination {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id", nullable = false)
    private Template template;

    @Enumerated(EnumType.STRING)
    @Column(name = "destination_type", nullable = false, length = 20)
    private DestinationType destinationType;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "target_table", length = 100)
    private String targetTable;          // POSTGRESQL

    @Column(name = "target_collection", length = 100)
    private String targetCollection;     // MONGODB

    @Column(name = "target_topic", length = 100)
    private String targetTopic;          // KAFKA

    @Column(name = "target_path", length = 500)
    private String targetPath;           // FILE

    @Column(name = "target_url", length = 500)
    private String targetUrl;            // REST

    @Column(nullable = false)
    private Boolean active = true;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "extra_config", columnDefinition = "jsonb")
    private Map<String, Object> extraConfig;

    @OneToMany(mappedBy = "destination",
               cascade = CascadeType.ALL,
               orphanRemoval = true,
               fetch = FetchType.LAZY)
    @OrderBy("orderIndex ASC")
    @Builder.Default
    private List<DestinationMapping> mappings = new ArrayList<>();

    public void addMapping(DestinationMapping mapping) {
        mappings.add(mapping);
        mapping.setDestination(this);
    }

    public void removeMapping(DestinationMapping mapping) {
        mappings.remove(mapping);
        mapping.setDestination(null);
    }
}
