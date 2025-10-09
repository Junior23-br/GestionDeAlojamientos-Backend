package com.gestion.alojamientos.dto.transaction.ServiceFee;

public record ServiceFeeDTO(Long id, String description,
                            double value, String typeFee, double promCalification,
                            Integer numberBookingsMinimum) {
}
