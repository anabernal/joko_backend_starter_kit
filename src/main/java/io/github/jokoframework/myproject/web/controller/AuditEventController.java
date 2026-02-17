package io.github.jokoframework.myproject.web.controller;

import io.github.jokoframework.myproject.audit.dto.AuditEventDTO;
import io.github.jokoframework.myproject.audit.dto.AuditEventResponseDTO;
import io.github.jokoframework.myproject.audit.enums.AuditAction;
import io.github.jokoframework.myproject.audit.enums.ResourceType;
import io.github.jokoframework.myproject.audit.service.AuditEventService;
import io.github.jokoframework.myproject.constants.ApiPaths;
import io.github.jokoframework.security.controller.SecurityConstants;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * REST controller for querying audit events.
 * Restricted to ADMIN users only.
 *
 * @author ana bernal
 */
@RestController
public class AuditEventController extends BaseRestController {

    @Autowired
    private AuditEventService auditEventService;

    @ApiOperation(value = "Query audit events",
            notes = "Query audit events with optional filters and pagination. " +
                    "Only accessible by users with ADMIN role.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Audit events retrieved successfully"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden - ADMIN role required")
    })
    @RequestMapping(value = ApiPaths.ROOT_AUDIT_EVENTS, method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = SecurityConstants.AUTH_HEADER_NAME, dataType = "String",
                    paramType = "header", required = true, value = "User Access Token"),
            @ApiImplicitParam(name = "X-Correlation-Id", dataType = "String",
                    paramType = "header", required = false, value = "Correlation ID for tracing")
    })
    public ResponseEntity<AuditEventResponseDTO> getAuditEvents(
            @ApiParam("Filter by action (USER_CREATED, USER_UPDATED, USER_DELETED)")
            @RequestParam(required = false) AuditAction action,

            @ApiParam("Filter by resource type (USER)")
            @RequestParam(required = false) ResourceType resourceType,

            @ApiParam("Filter by resource ID")
            @RequestParam(required = false) String resourceId,

            @ApiParam("From date (ISO-8601, e.g., 2026-01-01T00:00:00Z)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date from,

            @ApiParam("To date (ISO-8601, e.g., 2026-12-31T23:59:59Z)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date to,

            @ApiParam(value = "Page number (0-based)", defaultValue = "0")
            @RequestParam(defaultValue = "0") int page,

            @ApiParam(value = "Page size", defaultValue = "20")
            @RequestParam(defaultValue = "20") int size) {

        // Default sort: most recent first
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<AuditEventDTO> result = auditEventService.findAll(action, resourceType, resourceId,
                from, to, pageable);

        AuditEventResponseDTO response = new AuditEventResponseDTO();
        response.setSuccess(true);
        response.setContent(result.getContent());
        response.setPage(result.getNumber());
        response.setSize(result.getSize());
        response.setTotalElements(result.getTotalElements());
        response.setTotalPages(result.getTotalPages());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}