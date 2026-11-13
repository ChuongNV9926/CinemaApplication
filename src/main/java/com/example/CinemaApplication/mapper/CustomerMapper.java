package com.example.CinemaApplication.mapper;

import com.example.CinemaApplication.dto.response.CustomerResponse;
import com.example.CinemaApplication.model.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CustomerMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "accountId", source = "account.id")
    @Mapping(target = "username", source = "account.username")
    @Mapping(target = "role", source = "account.role")
    @Mapping(target = "accountStatus", source = "account.status")
    CustomerResponse toResponse(Customer customer);
}
