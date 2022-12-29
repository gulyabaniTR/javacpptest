package com.cagatay.javacpptest.controller;

import com.cagatay.javacpptest.service.ConvertService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/video")
@Log4j2
@CrossOrigin(origins = "*")
public class VideoController {

    @Autowired
    private ConvertService convertService;

    @PostMapping
    public ResponseEntity convertVideo(@RequestParam("video") String video) throws IOException, InterruptedException {
        //return ResponseEntity.ok(convertService.convertVideo(video));
        return ResponseEntity.ok(convertService.convertVideo2(video));
    }
}
