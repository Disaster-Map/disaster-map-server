package com.disastermap.disastermapserver.disasterMap.service;

import com.disastermap.disastermapserver.disasterMap.domain.DisasterMessage;
import com.disastermap.disastermapserver.disasterMap.repository.DisasterMessageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class DisasterMessageService {
    private final DisasterMessageRepository disasterMessageRepository;

    public DisasterMessageService(DisasterMessageRepository disasterMessageRepository) {
        this.disasterMessageRepository = disasterMessageRepository;
    }

    public void saveAll(List<DisasterMessage> disasterMessage) {
        disasterMessageRepository.saveAll(disasterMessage);
    }


}
