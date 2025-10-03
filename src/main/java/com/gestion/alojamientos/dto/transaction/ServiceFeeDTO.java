package com.gestion.alojamientos.dto.transaction;

public record ServiceFeeDTO(Long id, String description,
                            double value, String typeFee, double promCalification,
                            Integer numberBookingsMinimum) {
}
