package io.github.jokoframework.myproject.audit.service.impl;

import io.github.jokoframework.myproject.audit.correlation.CorrelationIdHolder;
import io.github.jokoframework.myproject.audit.entities.AuditEventEntity;
import io.github.jokoframework.myproject.audit.enums.AuditAction;
import io.github.jokoframework.myproject.audit.enums.ResourceType;
import io.github.jokoframework.myproject.audit.repositories.AuditEventRepository;
import io.github.jokoframework.myproject.audit.service.AuditEventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import io.github.jokoframework.myproject.audit.dto.AuditEventDTO;
import io.github.jokoframework.myproject.audit.repositories.AuditEventSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import java.util.Date;
import java.util.UUID;

/**
 * Implementation of AuditEventService.
 *
 * @author ana bernal
 */
@Service
@Transactional
public class AuditEventServiceImpl implements AuditEventService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuditEventServiceImpl.class);

    @Autowired
    private AuditEventRepository auditEventRepository;

    @Override
    public void registerEvent(AuditAction action, ResourceType resourceType, String resourceId,
                              String actor, String metadata) {
        // Get or generate correlation ID
        String correlationId = CorrelationIdHolder.get();
        if (correlationId == null || correlationId.trim().isEmpty()) {
            correlationId = UUID.randomUUID().toString();
        }

        AuditEventEntity entity = new AuditEventEntity();
        entity.setAction(action);
        entity.setResourceType(resourceType);
        entity.setResourceId(resourceId);
        entity.setActor(actor);
        entity.setMetadata(metadata);
        entity.setCorrelationId(correlationId);
        entity.setCreatedAt(new Date());

        auditEventRepository.save(entity);

        // Structured log (Requerimiento 6)
        LOGGER.info("AUDIT_EVENT | correlationId={} | action={} | resourceType={} | resourceId={} | actor={}",
                correlationId, action, resourceType, resourceId,
                actor != null ? actor : "N/A");
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AuditEventDTO> findAll(AuditAction action, ResourceType resourceType, String resourceId,
                                       Date from, Date to, Pageable pageable) {
        Specification<AuditEventEntity> spec = AuditEventSpecification.withFilters(
                action, resourceType, resourceId, from, to);

        Page<AuditEventEntity> page = auditEventRepository.findAll(spec, pageable);

        return page.map(this::toDTO);
    }

    private AuditEventDTO toDTO(AuditEventEntity entity) {
        AuditEventDTO dto = new AuditEventDTO();
        dto.setId(entity.getId());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setActor(entity.getActor());
        dto.setAction(entity.getAction());
        dto.setResourceType(entity.getResourceType());
        dto.setResourceId(entity.getResourceId());
        dto.setMetadata(entity.getMetadata());
        dto.setCorrelationId(entity.getCorrelationId());
        return dto;
    }
}