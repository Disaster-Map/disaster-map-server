package com.disastermap.disastermapserver.disasterMap.service;

import com.disastermap.disastermapserver.disasterMap.domain.DisasterMessage;
import com.disastermap.disastermapserver.disasterMap.repository.DisasterMessageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

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

    public Boolean existsBySn(Long sn) {
        return disasterMessageRepository.existsBySn(sn);
    }

    // 가장 최근에 저장된 메시지의 생성 일시(crtDt)를 가져오는 메서드
    public Optional<DisasterMessage> findLatestMessage() {
        return disasterMessageRepository.findTopByOrderByCrtDtDesc();
    }

}
