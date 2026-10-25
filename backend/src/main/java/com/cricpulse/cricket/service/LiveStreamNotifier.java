package com.cricpulse.cricket.service;

import com.cricpulse.cricket.dto.MatchDetailDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class LiveStreamNotifier {

    private static final Logger log = LoggerFactory.getLogger(LiveStreamNotifier.class);
    private final Map<Long, CopyOnWriteArrayList<SseEmitter>> matchEmitters = new ConcurrentHashMap<>();

    public SseEmitter subscribe(Long matchId) {
        // 30-minute timeout for live stream
        SseEmitter emitter = new SseEmitter(1800000L);

        matchEmitters.computeIfAbsent(matchId, k -> new CopyOnWriteArrayList<>()).add(emitter);

        emitter.onCompletion(() -> removeEmitter(matchId, emitter));
        emitter.onTimeout(() -> removeEmitter(matchId, emitter));
        emitter.onError(e -> removeEmitter(matchId, emitter));

        try {
            emitter.send(SseEmitter.event().name("connected").data("Subscribed to live match #" + matchId));
        } catch (IOException e) {
            removeEmitter(matchId, emitter);
        }

        return emitter;
    }

    public void broadcast(Long matchId, MatchDetailDTO matchData) {
        List<SseEmitter> emitters = matchEmitters.get(matchId);
        if (emitters == null || emitters.isEmpty()) {
            return;
        }

        List<SseEmitter> deadEmitters = new CopyOnWriteArrayList<>();
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event()
                        .name("score-update")
                        .data(matchData, MediaType.APPLICATION_JSON));
            } catch (Exception e) {
                deadEmitters.add(emitter);
            }
        }

        if (!deadEmitters.isEmpty()) {
            emitters.removeAll(deadEmitters);
        }
    }

    private void removeEmitter(Long matchId, SseEmitter emitter) {
        List<SseEmitter> emitters = matchEmitters.get(matchId);
        if (emitters != null) {
            emitters.remove(emitter);
        }
    }
}
