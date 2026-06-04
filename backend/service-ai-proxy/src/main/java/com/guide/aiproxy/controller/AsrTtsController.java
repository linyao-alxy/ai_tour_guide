package com.guide.aiproxy.controller;

import com.guide.common.result.R;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

/**
 * ASR/TTS代理 Controller
 */
@RestController
@RequestMapping("/api/v1/tourist")
@RequiredArgsConstructor
public class AsrTtsController {

    @PostMapping(value = "/asr/recognize", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<R<String>> recognize(@RequestPart("audio") MultipartFile audio) {
        // TODO: 调用讯飞ASR
        return Mono.just(R.ok("语音识别结果(模拟)"));
    }

    @PostMapping("/tts/synthesize")
    public Mono<R<String>> synthesize(@RequestParam String text,
                                       @RequestParam(defaultValue = "xunfei_female_01") String voiceId) {
        // TODO: 调用讯飞TTS，返回音频URL
        return Mono.just(R.ok("https://minio.localhost/tts/audio.mp3"));
    }
}
