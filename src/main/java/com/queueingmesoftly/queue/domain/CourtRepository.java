package com.queueingmesoftly.queue.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

interface CourtRepository extends JpaRepository<CourtEntity, Long> {

    Optional<CourtEntity> findByCourtNumber(int courtNumber);
}

