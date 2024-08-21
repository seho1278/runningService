package com.example.runningservice.service;

import com.example.runningservice.entity.RunGoalEntity;
import com.example.runningservice.repository.RunGoalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RunGoalService {

    @Autowired
    private RunGoalRepository runGoalRepository;

    public List<RunGoalEntity> findAll() {
        return runGoalRepository.findAll();
    }

    public Optional<RunGoalEntity> findById(Long id) {
        return runGoalRepository.findById(id);
    }

    public RunGoalEntity save(RunGoalEntity runGoal) {
        return runGoalRepository.save(runGoal);
    }

    public void deleteById(Long id) {
        runGoalRepository.deleteById(id);
    }
}
