package br.pucminas.lumen_coin_api.coin_transfer.controller;

import br.pucminas.lumen_coin_api.coin_transfer.dto.request.SendCoinTransferRequest;
import br.pucminas.lumen_coin_api.coin_transfer.dto.response.CoinTransferResponse;
import br.pucminas.lumen_coin_api.coin_transfer.service.CoinTransferService;
import br.pucminas.lumen_coin_api.security.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/coin-transfers")
@RequiredArgsConstructor
public class CoinTransferController {

    private final CoinTransferService coinTransferService;

    @PostMapping
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<CoinTransferResponse> send(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody SendCoinTransferRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(coinTransferService.send(principal.getUserId(), request));
    }

    @GetMapping("/sent")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<List<CoinTransferResponse>> getSent(
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(coinTransferService.getSentByTeacher(principal.getUserId()));
    }

    @GetMapping("/received")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<CoinTransferResponse>> getReceived(
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(coinTransferService.getReceivedByStudent(principal.getUserId()));
    }
}
