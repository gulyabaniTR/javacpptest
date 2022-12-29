package com.cagatay.javacpptest.service;

import com.cagatay.javacpptest.model.VideoModel;

import java.io.IOException;

public interface ConvertService {
    VideoModel convertVideo(String videoName) throws IOException, InterruptedException;
    Object convertVideo2(String videoName) throws IOException, InterruptedException;
}
