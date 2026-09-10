package com.aimock.interview.service;

import java.util.UUID;

public interface AuditLogService {
    void logAction(UUID userId, String action, String resourceType, String resourceId, String details);
}
