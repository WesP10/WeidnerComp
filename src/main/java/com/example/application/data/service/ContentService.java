package com.example.application.data.service;

import com.example.application.data.AnalyzedContent;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContentService {
    private final ContentRepository contentRepository;
    public ContentService(ContentRepository contentRepository){this.contentRepository=contentRepository;}
    public void addImage(AnalyzedContent content){contentRepository.save(content);}
    public AnalyzedContent updateImage(AnalyzedContent content){return contentRepository.save(content);}
    public List<AnalyzedContent> findAll(){return contentRepository.findAll();}
    public void deleteAllItems(){contentRepository.deleteAll();}
    public void deleteImageById(Integer id){contentRepository.deleteById(id);}
}
