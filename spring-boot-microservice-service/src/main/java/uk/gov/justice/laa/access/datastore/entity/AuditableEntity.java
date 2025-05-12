package uk.gov.justice.laa.access.datastore.entity;

import java.time.Instant;

/**
 * Interface for entities that are auditable.
 * Helps with mapping the created and updated timestamps and user information.
 */
public interface AuditableEntity {
  Instant getCreatedAt();
  String getCreatedBy();
  Instant getUpdatedAt();
  String getUpdatedBy();
}
