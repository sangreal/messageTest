package com.martyn.message.data.repository;

import com.martyn.message.data.Offset;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OffsetRepository extends JpaRepository<Offset, Long> {
    List<Offset> findByUserId(String userId);
    List<Offset> findByTopicAndUserId(String topic, String userId);
}
