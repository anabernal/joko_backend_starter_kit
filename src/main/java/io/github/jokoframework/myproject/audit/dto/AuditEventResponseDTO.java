package io.github.jokoframework.myproject.audit.dto;

import io.github.jokoframework.myproject.web.response.BaseResponseDTO;

import java.util.List;

/**
 * Paginated response DTO for audit events API.
 *
 * @author ana bernal
 */
public class AuditEventResponseDTO extends BaseResponseDTO {

    private List<AuditEventDTO> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;

    public AuditEventResponseDTO() {
        super();
    }

    public List<AuditEventDTO> getContent() {
        return content;
    }

    public void setContent(List<AuditEventDTO> content) {
        this.content = content;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }
}