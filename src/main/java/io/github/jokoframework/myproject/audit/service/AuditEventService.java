package io.github.jokoframework.myproject.audit.service;

import io.github.jokoframework.myproject.audit.dto.AuditEventDTO;
import io.github.jokoframework.myproject.audit.enums.AuditAction;
import io.github.jokoframework.myproject.audit.enums.ResourceType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Date;
/**
 * Service interface for audit event operations.
 *
 * @author ana bernal
 */
public interface AuditEventService {

    /**
     * Registers a new audit event in the system.
     *
     * @param action       the action performed
     * @param resourceType the type of resource affected
     * @param resourceId   the ID of the affected resource
     * @param actor        the username of the user who performed the action (nullable)
     * @param metadata     optional JSON metadata with additional details
     */
    void registerEvent(AuditAction action, ResourceType resourceType, String resourceId,
                       String actor, String metadata);

    Page<AuditEventDTO> findAll(AuditAction action, ResourceType resourceType, String resourceId,
                                Date from, Date to, Pageable pageable);
}