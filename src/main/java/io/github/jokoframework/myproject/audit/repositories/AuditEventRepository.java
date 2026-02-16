package io.github.jokoframework.myproject.audit.repositories;

import io.github.jokoframework.myproject.audit.entities.AuditEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Repository for audit event persistence and querying.
 *
 * @author ana bernal
 */
@Repository
public interface AuditEventRepository extends JpaRepository<AuditEventEntity, Long>,
        JpaSpecificationExecutor<AuditEventEntity> {

}