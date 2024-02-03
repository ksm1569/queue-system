package com.smsoft.flow.controller;

import com.smsoft.flow.dto.AllowUserResponse;
import com.smsoft.flow.dto.AllowedUserResponse;
import com.smsoft.flow.dto.RankNumberResponse;
import com.smsoft.flow.dto.RegisterUserResponse;
import com.smsoft.flow.service.UserQueueService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.security.NoSuchAlgorithmException;
import java.time.Duration;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/queue")
public class UserQueueController {
    private final UserQueueService userQueueService;

    @PostMapping("")
    public Mono<RegisterUserResponse> registerUser(
            @RequestParam(name = "queue", defaultValue = "default") String queue,
            @RequestParam(name = "user_id") Long userId
    ) {
        return userQueueService.registerWaitQueue(queue, userId)
                .map(RegisterUserResponse::new);
    }

    @PostMapping("/allow")
    public Mono<AllowUserResponse> allowUser(
            @RequestParam(name = "queue", defaultValue = "default") String queue,
            @RequestParam(name = "count") Long count
    ) {
        return userQueueService.allowUser(queue, count)
                .map(allowed -> new AllowUserResponse(count, allowed));
    }

    @GetMapping("/allowed")
    public Mono<AllowedUserResponse> isAllowedUser(
            @RequestParam(name = "queue", defaultValue = "default") String queue,
            @RequestParam(name = "user_id") Long userId
    ) {
        return userQueueService.isAllowed(queue, userId)
                .map(AllowedUserResponse::new);
    }

    @GetMapping("/rank")
    public Mono<RankNumberResponse> getUserRank(
            @RequestParam(name = "queue", defaultValue = "default") String queue,
            @RequestParam(name = "user_id") Long userId
    ) {
        return userQueueService.getRank(queue, userId)
                .map(RankNumberResponse::new);
    }

    @GetMapping("/touch")
    Mono<?> touch(@RequestParam(name = "queue", defaultValue = "default") String queue,
                  @RequestParam(name = "user_id") Long userId,
                  ServerWebExchange exchange
    ) {
        return Mono.defer(() -> userQueueService.generateToken(queue, userId))
                .map(token -> {
                    exchange.getResponse().addCookie(
                            ResponseCookie.from("user-queue-%s-token".formatted(queue), token)
                                    .maxAge(Duration.ofSeconds(300))
                                    .path("/")
                                    .build()
                    );

                    return token;
                });
    }
}