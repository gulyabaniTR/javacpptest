package com.cagatay.javacpptest.utils;

import com.cagatay.javacpptest.model.ErrorModel;
import com.cagatay.javacpptest.model.VideoModel;
import org.bytedeco.javacpp.*;

import lombok.Getter;
import lombok.Setter;

import org.bytedeco.ffmpeg.avcodec.*;
import org.bytedeco.ffmpeg.avformat.*;
import org.bytedeco.ffmpeg.avutil.*;
import org.bytedeco.ffmpeg.swscale.*;

import java.io.File;

import static org.bytedeco.ffmpeg.global.avcodec.*;
import static org.bytedeco.ffmpeg.global.avformat.*;
import static org.bytedeco.ffmpeg.global.avutil.*;
import static org.bytedeco.ffmpeg.global.swscale.*;

public class ConvertBase {

    @Getter File output;

    public Object convert(String inputPath) {
        output = new File(inputPath.replace(".", "") + "output.mp4");
        System.out.println("output path: " + output.getAbsolutePath());
        System.out.println("input path: " + inputPath);
        AVOutputFormat oformat = new AVOutputFormat();
        AVFormatContext ifmt_ctx = new AVFormatContext(null);
        AVFormatContext ofmt_ctx = new AVFormatContext(null);
        AVPacket pkt = new AVPacket();
        int ret;
        AVInputFormat avInputFormat = new AVInputFormat(null);
        AVDictionary avDictionary = new AVDictionary(null);

        if ((ret = avformat_open_input(ifmt_ctx, inputPath, avInputFormat, avDictionary)) < 0) {
            System.out.println("Could not open input file");
            return new ErrorModel("Could not open input file.");
        }

        av_dict_free(avDictionary);

        if ((ret = avformat_find_stream_info(ifmt_ctx, (PointerPointer) null)) < 0) {
            System.out.println("Failed to retrieve input stream information");
            return new ErrorModel("Failed to retrieve input stream information.");
        }

        //check input is h264
        if(ifmt_ctx.streams(0).codecpar().codec_id() != AV_CODEC_ID_H264){
            System.out.println("Input is not h264");
            return new ErrorModel("Input is not h264.");
        }

        System.out.println("==========Input Information==========");
        av_dump_format(ifmt_ctx, 0, inputPath, 0);
        System.out.println("=====================================");

        if ((ret = avformat_alloc_output_context2(ofmt_ctx, null, null, output.getAbsolutePath())) < 0) {
            System.out.println("Could not create output context");
            return new ErrorModel("Could not create output context.");
        }

        oformat = ofmt_ctx.oformat();

        for (int i = 0; i < ifmt_ctx.nb_streams(); i++) {
            AVStream in_stream = ifmt_ctx.streams(i);
            AVStream out_stream = avformat_new_stream(ofmt_ctx, null);
            if (out_stream == null) {
                System.out.println("Failed allocating output stream");
                return new ErrorModel("Failed allocating output stream.");
            }
            ret = avcodec_parameters_copy(out_stream.codecpar(), in_stream.codecpar());
            if (ret < 0) {
                System.out.println("Failed to copy codec parameters");
                return new ErrorModel("Failed to copy codec parameters.");
            }
            out_stream.codecpar().codec_tag(0);
        }
        System.out.println("==========Output Information==========");
        av_dump_format(ofmt_ctx, 0, output.getAbsolutePath(), 1);
        System.out.println("======================================");

        if ((oformat.flags() & AVFMT_NOFILE) == 0) {
            AVIOContext pb = new AVIOContext(null);

            //ret = avio_open(ofmt_ctx.pb(), output.getAbsolutePath(), AVIO_FLAG_WRITE);
            ret = avio_open(pb, output.getAbsolutePath(), AVIO_FLAG_WRITE);
            if (ret < 0) {
                System.out.println("Could not open output file");
                return new ErrorModel("Could not open output file.");
            }
            ofmt_ctx.pb(pb);
        }
        AVDictionary avOutDict = new AVDictionary(null);
        if ((ret = avformat_write_header(ofmt_ctx, avOutDict)) < 0) {
            System.out.println("Error occurred when opening output file");
            return new ErrorModel("Error occurred when opening output file.");
        }

        while (av_read_frame(ifmt_ctx,pkt) >= 0) {
            AVStream in_stream = ifmt_ctx.streams(pkt.stream_index());
            AVStream out_stream = ofmt_ctx.streams(pkt.stream_index());

            pkt.pts(av_rescale_q_rnd(pkt.pts(), in_stream.time_base(), out_stream.time_base(), AV_ROUND_NEAR_INF | AV_ROUND_PASS_MINMAX));
            pkt.dts(av_rescale_q_rnd(pkt.dts(), in_stream.time_base(), out_stream.time_base(), AV_ROUND_NEAR_INF | AV_ROUND_PASS_MINMAX));
            pkt.duration(av_rescale_q(pkt.duration(), in_stream.time_base(), out_stream.time_base()));
            pkt.pos(-1);

            //check video and audio package BUT ITS NOT WORKING on multiple Stream videos.
            //use only one stream audio or one stream video file for test.
            //whatever it's working now. I don't know why. I will check it later. :)
            if (pkt.stream_index() == 0) {
                System.out.println("Write Video Packet. size:" + pkt.size() + "pts:" + pkt.pts());
            } else {
                System.out.println("Write Audio Packet. size:" + pkt.size() + "pts:" + pkt.pts());
            }

            // WHY av_write_frame(ofmt_ctx, pkt) not working at all? check is it mutex and thread problem and lock
            ret = av_interleaved_write_frame(ofmt_ctx, pkt);
            if (ret < 0) {
                System.out.println("Error muxing packet");
                return new ErrorModel("Error muxing packet.");
            }
            av_packet_unref(pkt);
        }
        av_write_trailer(ofmt_ctx);
        avformat_close_input(ifmt_ctx);
        avformat_close_input(ofmt_ctx);

        VideoModel videoModel = new VideoModel();
        videoModel.setName(output.getAbsolutePath());
        return videoModel;
    }

}