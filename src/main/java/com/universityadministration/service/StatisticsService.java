package com.universityadministration.service;

import com.energymanagementsystem.ems.exceptions.ResourceNotFoundException;
import com.energymanagementsystem.ems.model.Statistics;
import com.energymanagementsystem.ems.model.dataset;
import com.energymanagementsystem.ems.repository.DatasetRepository;
import com.energymanagementsystem.ems.repository.StatisticsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class StatisticsService {
    @Autowired
    private DatasetRepository datasetRepository;
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private StatisticsRepository statisticsRepository;

    public List<dataset> getStatisticsByUserId(int id){
        ArrayList<dataset> statistics = new ArrayList<>();
        datasetRepository.findAll().forEach(s -> {if(s.getUserId() == id) statistics.add(s); });

        return statistics;
    }

    public void migrateaToStatisticsTable(List<dataset> datasets) throws ResourceNotFoundException {
        for (dataset ds: datasets) {
            Statistics statistics = new Statistics(ds.getVoltage(), ds.getAmpere(), ds.getCatchTime(), userService.get(ds.getUserId()));
            statisticsRepository.save(statistics);
        }
    }
}
