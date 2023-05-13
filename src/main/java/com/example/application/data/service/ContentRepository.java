package com.example.application.data.service;

import com.example.application.data.AnalyzedContent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContentRepository extends JpaRepository<AnalyzedContent, Integer> {
}
