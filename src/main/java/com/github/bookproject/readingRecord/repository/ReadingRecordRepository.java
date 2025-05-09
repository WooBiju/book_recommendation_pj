package com.github.bookproject.readingRecord.repository;

import com.github.bookproject.readingRecord.entity.ReadingRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReadingRecordRepository extends JpaRepository<ReadingRecord, Long> {

    Optional<ReadingRecord> findByUserIdAndBookId(Long userId, Long id);

    int countByUserId(Long userId);

    List<ReadingRecord> findByUserId(Long userId);

}
