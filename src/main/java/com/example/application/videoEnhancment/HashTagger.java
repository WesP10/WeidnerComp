package com.example.application.videoEnhancment;

import com.google.api.gax.longrunning.OperationFuture;
import com.google.cloud.videointelligence.v1.*;
import com.google.protobuf.ByteString;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import edu.mit.jwi.Dictionary;
import edu.mit.jwi.IDictionary;
import edu.mit.jwi.RAMDictionary;
import edu.mit.jwi.item.*;
import edu.mit.jwi.morph.WordnetStemmer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HashTagger {
    public static void generateTags(MemoryBuffer memoryBuffer){

        ArrayList<String> tags = new ArrayList<>();
        try(VideoIntelligenceServiceClient client = VideoIntelligenceServiceClient.create()){
            AnnotateVideoRequest request = AnnotateVideoRequest.newBuilder()
                    .setInputContent(ByteString.copyFrom(memoryBuffer.getInputStream().readAllBytes()))
                    .addFeatures(Feature.LABEL_DETECTION).build();

            OperationFuture<AnnotateVideoResponse, AnnotateVideoProgress> response =
                    client.annotateVideoAsync(request);
            System.out.println("Waiting for operation to complete...");
            for (VideoAnnotationResults results : response.get().getAnnotationResultsList()) {
                // process segment label annotations
                for (LabelAnnotation labelAnnotation : results.getSegmentLabelAnnotationsList()) {
                    tags.add("#" + labelAnnotation.getEntity().getDescription());
                    for (Entity categoryEntity : labelAnnotation.getCategoryEntitiesList()) {
                        tags.add("#" + categoryEntity.getDescription());
                    }
                }
                // process shot label annotations
                for (LabelAnnotation labelAnnotation : results.getShotLabelAnnotationsList()) {
                    tags.add("#" + labelAnnotation.getEntity().getDescription());
                    // categories
                    for (Entity categoryEntity : labelAnnotation.getCategoryEntitiesList()) {
                        tags.add("#" + categoryEntity.getDescription());
                    }
                }
                // process frame label annotations
                for (LabelAnnotation labelAnnotation : results.getFrameLabelAnnotationsList()) {
                    tags.add("#" + labelAnnotation.getEntity().getDescription());
                    // categories
                    for (Entity categoryEntity : labelAnnotation.getCategoryEntitiesList()) {
                        tags.add("#" + categoryEntity.getDescription());
                    }
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        for(String tag : tags.stream().distinct().toList())
            System.out.println(tag);
    }

//    private static String getClosestTrendingTerm(String target){
//
//    }
}
