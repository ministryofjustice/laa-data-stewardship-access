package uk.gov.justice.laa.dstew.access.entity;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.time.Instant;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;

/**
 * Represents the common audit fields in entities.
 */
@Embeddable
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class EmbeddedRecordHistoryEntity {

  @Column(name = "created_at", updatable = false)
  @CreationTimestamp
  private Instant createdAt;

  @CreatedBy
  @Column(name = "created_by", updatable = false)
  private String createdBy;

  @Column(name = "updated_at")
  @UpdateTimestamp
  private Instant updatedAt;

  @LastModifiedBy
  @Column(name = "updated_by")
  private String updatedBy;

}
