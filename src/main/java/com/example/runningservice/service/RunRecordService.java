package com.example.runningservice.service;

import com.example.runningservice.entity.MemberEntity;
import com.example.runningservice.entity.RunRecordEntity;
import com.example.runningservice.repository.RunRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RunRecordService {

    @Autowired
    private RunRecordRepository runRecordRepository;

    public List<RunRecordEntity> findByUserId(MemberEntity userId){
        return runRecordRepository.findByUserId(userId);
    }

    public Optional<RunRecordEntity> findById(Long id) {
        return runRecordRepository.findById(id);
    }

    public RunRecordEntity save(RunRecordEntity runRecord) {
        return runRecordRepository.save(runRecord);
    }

    public void deleteById(Long id) {
        runRecordRepository.deleteById(id);
    }
}
