package com.uber.orchestrator.controller;

import com.uber.orchestrator.dto.StartSagaRequest;
import com.uber.orchestrator.service.OrchestratorService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/orchestrator")
public class OrchestratorController {

    private final OrchestratorService orchestratorService;

    public OrchestratorController(OrchestratorService orchestratorService) {
        this.orchestratorService = orchestratorService;
    }

    @PostMapping("/start")
    public ResponseEntity<Map<String, Object>> start(@Valid @RequestBody StartSagaRequest req) {
        return ResponseEntity.accepted().body(orchestratorService.startSaga(req));
    }

    @PostMapping("/{sagaId}/reserve-driver")
    public ResponseEntity<Map<String, Object>> reserveDriver(@PathVariable String sagaId) {
        return ResponseEntity.ok(orchestratorService.reserveDriver(sagaId));
    }

    @PostMapping("/{sagaId}/compensate")
    public ResponseEntity<Map<String, Object>> compensate(@PathVariable String sagaId,
                                                          @RequestParam(defaultValue = "requested_by_user") String reason) {
        return ResponseEntity.ok(orchestratorService.compensate(sagaId, reason));
    }

    @GetMapping("/{sagaId}")
    public ResponseEntity<Map<String, Object>> get(@PathVariable String sagaId) {
        return ResponseEntity.ok(orchestratorService.get(sagaId));
    }
}
