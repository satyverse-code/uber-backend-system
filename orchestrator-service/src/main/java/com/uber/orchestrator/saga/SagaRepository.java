package com.uber.orchestrator.saga;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SagaRepository extends JpaRepository<Saga, String> {
}
