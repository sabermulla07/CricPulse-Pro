package com.cricpulse.cricket.controller;

import com.cricpulse.cricket.service.LiveStreamNotifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/matches")
@CrossOrigin(origins = "*")
public class LiveStreamController {

    private final LiveStreamNotifier liveStreamNotifier;

    public LiveStreamController(LiveStreamNotifier liveStreamNotifier) {
        this.liveStreamNotifier = liveStreamNotifier;
    }

    @GetMapping(value = "/{id}/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamMatch(@PathVariable Long id) {
        return liveStreamNotifier.subscribe(id);
    }
}
