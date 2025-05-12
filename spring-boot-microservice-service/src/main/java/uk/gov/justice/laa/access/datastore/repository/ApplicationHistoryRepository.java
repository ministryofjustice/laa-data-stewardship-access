package uk.gov.justice.laa.access.datastore.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.gov.justice.laa.access.datastore.entity.ApplicationHistoryEntity;

/**
 * Repository for managing application entities.
 */
@Repository
public interface ApplicationHistoryRepository
    extends JpaRepository<ApplicationHistoryEntity, UUID> {
}