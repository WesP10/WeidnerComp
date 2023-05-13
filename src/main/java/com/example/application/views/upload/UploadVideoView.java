package com.example.application.views.upload;

import com.example.application.data.service.Subtitle;
import com.example.application.videoEnhancment.AutoCaption;
import com.example.application.videoEnhancment.HashTagger;
import com.example.application.views.MainLayout;
import com.github.olafj.vaadin.flow.Video;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import jakarta.annotation.security.PermitAll;
import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameUtils;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

@PageTitle("UploadVideo")
@Route(value = "upload", layout = MainLayout.class)
@PermitAll
public class UploadVideoView extends VerticalLayout {
    private ArrayList<ArrayList> videoTranscript;
    public UploadVideoView(){
        setSpacing(true);
        setAlignItems(Alignment.CENTER);

        Video video = new Video();
        video.setHeight("400px");
        add(video);

        H2 fileNamee = new H2("Blank");
        add(fileNamee);

        MemoryBuffer memoryBuffer = new MemoryBuffer();
        Upload upload = new Upload(memoryBuffer);
        upload.addSucceededListener(e -> {
            video.setSource(new StreamResource("video.mp4", () -> memoryBuffer.getInputStream()));
            video.setAutoPlay(true);
            video.setControls(true);

            videoTranscript = AutoCaption.transcribeVideo(memoryBuffer);
            addCaptions(memoryBuffer, videoTranscript);
            //HashTagger.generateTags(memoryBuffer);
        });
        add(upload);
    }

    private void addCaptions(MemoryBuffer buffer, ArrayList<ArrayList> videoTranscript){
        try{
            ArrayList<Subtitle> subtitles = new ArrayList<>();
            for (int i = 0; i < videoTranscript.get(0).size(); i++)
                subtitles.add(new Subtitle((String) videoTranscript.get(2).get(i), (Long) videoTranscript.get(0).get(i), (Long) videoTranscript.get(1).get(i)));

            File file = new File("output.mp4");
            FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(buffer.getInputStream());
            grabber.start();
            FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(file, grabber.getImageWidth(), grabber.getImageHeight());
            recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
            recorder.setFormat("mp4");
            recorder.setFrameRate(grabber.getFrameRate());
            recorder.setPixelFormat(grabber.getPixelFormat());
            recorder.start();
            Font font = new Font("Arial", Font.BOLD, 30);

            Frame frame = null;
            int currentSubtitleIndex = 0;
            long currentSubtitleStartTime = subtitles.get(currentSubtitleIndex).getStartTime();
            long currentSubtitleEndTime = subtitles.get(currentSubtitleIndex).getEndTime();
            while ((frame = grabber.grabFrame()) != null){
                BufferedImage image = Java2DFrameUtils.toBufferedImage(frame);
                Graphics2D graphics = image.createGraphics();
                graphics.setFont(font);
                graphics.setColor(Color.WHITE);

                if (frame.timestamp >= currentSubtitleStartTime && frame.timestamp <= currentSubtitleEndTime) {
                    // Draw the subtitle onto the BufferedImage
                    String subtitleText = subtitles.get(currentSubtitleIndex).getText();
                    Rectangle2D bounds = font.getStringBounds(subtitleText, new FontRenderContext(null, true, false));
                    int x = (int) (image.getWidth() - bounds.getWidth()) / 2;
                    int y = image.getHeight() - (int) bounds.getHeight() - 20;
                    graphics.drawString(subtitleText, x, y);
                }

                Frame outputFrame = Java2DFrameUtils.toFrame(image);
                outputFrame.timestamp = frame.timestamp;
                recorder.record(outputFrame);
                if (frame.timestamp >= currentSubtitleEndTime && currentSubtitleIndex < subtitles.size() - 1) {
                    currentSubtitleIndex++;
                    currentSubtitleStartTime = subtitles.get(currentSubtitleIndex).getStartTime();
                    currentSubtitleEndTime = subtitles.get(currentSubtitleIndex).getEndTime();
                }
            }
            recorder.stop();
            recorder.release();
            grabber.stop();
            grabber.release();
        } catch (IOException e){
            e.printStackTrace();
        }

    }
}
