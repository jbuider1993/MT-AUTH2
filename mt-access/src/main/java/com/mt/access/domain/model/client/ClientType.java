package com.mt.access.domain.model.client;

import com.mt.common.domain.model.sql.converter.EnumSetConverter;

public enum ClientType {
    ROOT_APPLICATION,
    FIRST_PARTY,
    THIRD_PARTY,
    FRONTEND_APP,
    BACKEND_APP;

    public static class DbConverter extends EnumSetConverter<ClientType> {
        public DbConverter() {
            super(ClientType.class);
        }
    }
}
