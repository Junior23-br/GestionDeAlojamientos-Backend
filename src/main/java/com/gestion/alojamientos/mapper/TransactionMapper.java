package com.gestion.alojamientos.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.gestion.alojamientos.dto.TransactionDTO;
import com.gestion.alojamientos.model.Transaction;

@Mapper(componentModel = "spring")
public interface TransactionMapper {
    

    @Mapping(target = "bookingId", source = "bookingId")
    @Mapping(target = "currency", source = "currency", expression = "java(transaction.getCurrency().getCurrencyCode())")
    @Mapping(target = "status", source = "paymentStatus", expression = "java(transaction.getPayment_status().name())")
    @Mapping(target = "amount", source = "amount")
    @Mapping(target = "id", source = "id")
    TransactionDTO toDTO(Transaction transaction);

    @Mapping(target = "bookingId", source = "bookingId")
    @Mapping(target = "currency", source = "currency", expression = "java(java.util.Currency.getInstance(transactionDTO.currency()))")
    @Mapping(target = "paymentStatus", source = "status", expression = "java(com.gestion.alojamientos.model.TransactionStatus.valueOf(transactionDTO.status()))")
    @Mapping(target = "amount", source = "amount")
    @Mapping(target = "id", source = "id")
    Transaction toEntity(TransactionDTO transactionDTO);

}
