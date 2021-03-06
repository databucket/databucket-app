package pl.databucket.mapper;

import org.modelmapper.PropertyMap;
import pl.databucket.dto.BucketDto;
import pl.databucket.entity.Bucket;

public class BucketPropertyMap extends PropertyMap<Bucket, BucketDto> {

    @Override
    protected void configure() {
        map().setId(source.getId());
        map().setName(source.getName());
        map().setDescription(source.getDescription());
        map().setIconName(source.getIconName());
        map().setClassId(source.getDataClass().getId());
        map().setGroupsIds(source.getGroupsIds());
        map().setUsersIds(source.getUsersIds());
        map().setHistory(source.isHistory());
        map().setProtectedData(source.isProtectedData());
        map().setRoleId(source.getRole().getId());
        map().setTeamsIds(source.getTeamsIds());

        map().setCreatedBy(source.getCreatedBy());
        map().setCreatedAt(source.getCreatedAt());
        map().setModifiedBy(source.getModifiedBy());
        map().setModifiedAt(source.getModifiedAt());
    }
}