package com.example.application.videoEnhancment;

import com.google.api.gax.longrunning.OperationFuture;
import com.google.cloud.videointelligence.v1.*;
import com.google.protobuf.ByteString;
import com.google.protobuf.Duration;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class AutoCaption {
    public static ArrayList<ArrayList> transcribeVideo(MemoryBuffer buffer){
        ArrayList<ArrayList> list = new ArrayList<>();
        ArrayList<Duration> startTime = new ArrayList<>();
        ArrayList<Duration> endTime = new ArrayList<>();
        ArrayList<String> words = new ArrayList<>();
        list.add(startTime);
        list.add(endTime);
        list.add(words);
        try(VideoIntelligenceServiceClient client = VideoIntelligenceServiceClient.create()){
            SpeechTranscriptionConfig config =
                    SpeechTranscriptionConfig.newBuilder()
                            .setLanguageCode("en-US")
                            .setEnableAutomaticPunctuation(true)
                            .build();

            VideoContext context = VideoContext.newBuilder().setSpeechTranscriptionConfig(config).build();

            AnnotateVideoRequest request = AnnotateVideoRequest.newBuilder()
                    .setInputContent(ByteString.copyFrom(buffer.getInputStream().readAllBytes()))
                    .addFeatures(Feature.SPEECH_TRANSCRIPTION).setVideoContext(context).build();

            OperationFuture<AnnotateVideoResponse, AnnotateVideoProgress> response = client.annotateVideoAsync(request);
            System.out.println("Waiting for operation to complete...");
            // Display the results
            for (VideoAnnotationResults results : response.get(600, TimeUnit.SECONDS).getAnnotationResultsList()) {
                for (SpeechTranscription speechTranscription : results.getSpeechTranscriptionsList()) {
                    try {
                        // Print the transcription
                        if (speechTranscription.getAlternativesCount() > 0) {
                            SpeechRecognitionAlternative alternative = speechTranscription.getAlternatives(0);
                            for (WordInfo wordInfo : alternative.getWordsList()) {
                                startTime.add(wordInfo.getStartTime());
                                endTime.add(wordInfo.getEndTime());
                                words.add(wordInfo.getWord());
                            }
                        } else {
                            System.out.println("No transcription found");
                        }
                    } catch (IndexOutOfBoundsException ioe) {
                        System.out.println("Could not retrieve frame: " + ioe.getMessage());
                    }
                }
            }

        } catch (Exception e){
            e.printStackTrace();
        }
        return list;
    }
}
