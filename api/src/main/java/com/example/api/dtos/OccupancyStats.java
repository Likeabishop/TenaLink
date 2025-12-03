package com.example.api.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class OccupancyStats {
    private Integer totalUnits;
    private Integer occupiedUnits;
    private Integer vacantUnits;
    private Integer maintenanceUnits;
    private Double occupancyRate;
    private Double vacancyRate;
}
