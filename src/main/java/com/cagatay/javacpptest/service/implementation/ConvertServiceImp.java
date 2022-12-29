package com.cagatay.javacpptest.service.implementation;
import java.io.*;

import com.cagatay.javacpptest.constants.PathConfig;
import com.cagatay.javacpptest.model.VideoModel;
import com.cagatay.javacpptest.service.ConvertService;
import com.cagatay.javacpptest.utils.ConvertBase;
import org.bytedeco.javacpp.*;
import org.springframework.stereotype.Service;


@Service
public class ConvertServiceImp implements ConvertService {
    /*
    *
    * Deprecated
    *
    * */
    @Override
    public VideoModel convertVideo(String videoName) throws IOException, InterruptedException {
        String ffmpeg = Loader.load(org.bytedeco.ffmpeg.ffmpeg.class);
        ProcessBuilder pb = new ProcessBuilder(ffmpeg, "-i", "C:/xampp/htdocs/javacpptest/" + videoName + "", "-vcodec", "h264", "C:/xampp/htdocs/javacpptest/"+videoName.replace(".","")+"output.mp4");
        pb.inheritIO().start().waitFor();
        VideoModel videoModel = new VideoModel();
        videoModel.setName(videoName+"output.mp4");
        return videoModel;
    }

    @Override
    public Object convertVideo2(String videoName) throws IOException, InterruptedException {
     String videoPath = PathConfig.VIDEO_PATH.getPath()+ videoName;
        ConvertBase convertBase = new ConvertBase();
     return  convertBase.convert(videoPath);
    }



}
