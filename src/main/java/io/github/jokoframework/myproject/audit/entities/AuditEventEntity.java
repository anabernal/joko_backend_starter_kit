package io.github.jokoframework.myproject.audit.entities;

import io.github.jokoframework.myproject.audit.enums.AuditAction;
import io.github.jokoframework.myproject.audit.enums.ResourceType;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * Entity representing an audit event in the system.
 *
 * @author ana bernal
 */
@Entity
@Table(name = "audit_event", schema = "audit")
public class AuditEventEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @GenericGenerator(
            name = "auditEventSequenceGenerator",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {
                    @Parameter(name = "sequence_name", value = "audit.audit_event_id_seq"),
                    @Parameter(name = "initial_value", value = "1"),
                    @Parameter(name = "increment_size", value = "1")
            }
    )
    @Id
    @GeneratedValue(generator = "auditEventSequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "created_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column(name = "actor")
    private String actor;

    @NotNull
    @Column(name = "action", nullable = false)
    @Enumerated(EnumType.STRING)
    private AuditAction action;

    @NotNull
    @Column(name = "resource_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private ResourceType resourceType;

    @NotNull
    @Column(name = "resource_id", nullable = false)
    private String resourceId;

    @Column(name = "metadata", columnDefinition = "TEXT")
    private String metadata;

    @NotNull
    @Column(name = "correlation_id", nullable = false)
    private String correlationId;

    public AuditEventEntity() {
        super();
    }

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = new Date();
        }
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getActor() {
        return actor;
    }

    public void setActor(String actor) {
        this.actor = actor;
    }

    public AuditAction getAction() {
        return action;
    }

    public void setAction(AuditAction action) {
        this.action = action;
    }

    public ResourceType getResourceType() {
        return resourceType;
    }

    public void setResourceType(ResourceType resourceType) {
        this.resourceType = resourceType;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("createdAt", createdAt)
                .append("actor", actor)
                .append("action", action)
                .append("resourceType", resourceType)
                .append("resourceId", resourceId)
                .append("correlationId", correlationId)
                .toString();
    }
}