package uk.gov.justice.laa.access.datastore.mapper;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import uk.gov.justice.laa.access.datastore.entity.ApplicationEntity;
import uk.gov.justice.laa.access.datastore.entity.ApplicationProceedingEntity;
import uk.gov.justice.laa.access.datastore.model.Application;
import java.util.Date;
import uk.gov.justice.laa.access.datastore.model.ApplicationRequestBody;

/**
 * The mapper between Application and ApplicationEntity.
 */
@Mapper(componentModel = "spring")
public interface ApplicationMapper {

  /**
   * Maps the given application entity to an application.
   *
   * @param applicationEntity the application entity
   * @return the application
   */
  Application toApplication(ApplicationEntity applicationEntity);

  /**
   * Maps the given application to an application entity.
   *
   * @param applicationRequestBody the application
   * @return the application entity
   */
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "recordHistory", ignore = true)
  ApplicationEntity toApplicationEntity(ApplicationRequestBody applicationRequestBody);

//  ApplicationProceedingEntity toApplicationProceedingEntity(ApplicationRequestBody applicationRequestBody);

  default OffsetDateTime toOffsetDateTime(Date date) {
    return date == null ? null : date.toInstant().atOffset(ZoneOffset.UTC);
  }

  default Date toDate(OffsetDateTime dateTime) {
    return dateTime == null ? null : Date.from(dateTime.toInstant());
  }
}
