package com.mt.access.port.adapter.persistence;

import com.mt.access.domain.model.permission.PermissionId;
import com.mt.common.domain.model.domainId.DomainId;

import javax.persistence.AttributeConverter;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class PermissionIdSetConverter implements AttributeConverter<Set<PermissionId>, String> {
    @Override
    public String convertToDatabaseColumn(Set<PermissionId> attribute) {
        if (attribute == null) {
            return null;
        }
        return String.join(",", attribute.stream().map(DomainId::getDomainId).collect(Collectors.toSet()));
    }

    @Override
    public Set<PermissionId> convertToEntityAttribute(String dbData) {
        if (dbData == null)
            return null;
        if (dbData.isBlank() || dbData.isEmpty())
            return null;
        return Arrays.stream(dbData.split(",")).map(PermissionId::new).collect(Collectors.toSet());
    }
}
