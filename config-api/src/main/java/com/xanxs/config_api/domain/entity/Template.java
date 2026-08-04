package com.xanxs.config_api.domain.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.xanxs.config_api.domain.entity.Destination;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.xanxs.config_api.domain.enums.FileType;
import com.xanxs.config_api.domain.enums.OnErrorBehavior;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "bte_template")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Template {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "file_type", nullable = false, length = 20)
    private FileType fileType;

    @Column(length = 10)
    private String separator;

    @Column(nullable = false, length = 20)
    private String encoding = "UTF-8";

    @Column(name = "skip_empty_lines", nullable = false)
    private Boolean skipEmptyLines = true;

    @Enumerated(EnumType.STRING)
    @Column(name = "on_error", nullable = false, length = 20)
    private OnErrorBehavior onError = OnErrorBehavior.CONTINUE;

    @Column(nullable = false)
    private Boolean active = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Relaciones
    @OneToMany(mappedBy = "template",
               cascade = CascadeType.ALL,
               orphanRemoval = true,
               fetch = FetchType.LAZY)
    @OrderBy("orderIndex ASC")
    @Builder.Default
    private List<TemplateField> fields = new ArrayList<>();

    @OneToMany(mappedBy = "template",
               cascade = CascadeType.ALL,
               orphanRemoval = true,
               fetch = FetchType.LAZY)
    @Builder.Default
    private List<Destination> destinations = new ArrayList<>();


     public void addField(TemplateField field) {
        fields.add(field);
        field.setTemplate(this);
    }

    public void removeField(TemplateField field) {
        fields.remove(field);
        field.setTemplate(null);
    }

    public void addDestination(Destination destination) {
        destinations.add(destination);
        destination.setTemplate(this);
    }
}
