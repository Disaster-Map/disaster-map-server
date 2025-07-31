package com.disastermap.disastermapserver.disasterMap.repository;

import com.disastermap.disastermapserver.disasterMap.domain.DisasterMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DisasterMessageRepository extends JpaRepository<DisasterMessage, Long> {
}
