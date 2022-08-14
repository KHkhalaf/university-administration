package com.universityadministration.dto;

import com.energymanagementsystem.ems.model.dataset;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class datasetDto {
    private int voltage;
    private int ampere;
    private String catchTime;
    private int userId;
    private String username;

    public datasetDto(dataset dataset, String username){
        this.ampere = dataset.getAmpere();
        this.voltage = dataset.getVoltage();
        this.catchTime = dataset.getCatchTime();
        this.userId = dataset.getUserId();
        this.username = username;
    }
}
