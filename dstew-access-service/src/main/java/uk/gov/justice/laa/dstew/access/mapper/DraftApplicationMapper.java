package uk.gov.justice.laa.dstew.access.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.gov.justice.laa.dstew.access.entity.DraftApplicationEntity;
import uk.gov.justice.laa.dstew.access.model.DraftApplicationCreateReq;

/**
 * The mapper between DraftApplication and DraftApplicationEntity.
 */
@Mapper(componentModel = "spring")
public interface DraftApplicationMapper {

  /**
   * Maps the given application to an application entity.
   *
   * @param draftApplicationCreateReq the application
   * @return the application entity
  */
  @Mapping(target = "id", ignore = true)
  DraftApplicationEntity toDraftApplicationEntity(DraftApplicationCreateReq draftApplicationCreateReq);
}
