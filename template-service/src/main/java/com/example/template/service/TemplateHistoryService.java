package com.example.template.service;

import com.example.template.dto.response.TemplateHistoryResponse;
import com.example.template.entity.Template;
import com.example.template.entity.TemplateHistory;
import com.example.template.mapper.TemplateMapper;
import com.example.template.repository.TemplateHistoryRepository;
import com.example.template.repository.TemplateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TemplateHistoryService {

    private final TemplateHistoryRepository templateHistoryRepository;
    private final TemplateRepository templateRepository;
//    private final MessageSourceService message;
    private final TemplateMapper mapper;

    public TemplateHistoryResponse create(Long clientId, Long templateId) {
        Template template = templateRepository.findByIdAndClientId(templateId, clientId)
                .orElseThrow(() -> new RuntimeException("template.not_found"));


        Optional<TemplateHistory> optTemplateHistory = templateHistoryRepository.findByClientIdAndTitleAndContent(
                clientId,
                template.getTitle(),
                template.getContent()
        );
        if (optTemplateHistory.isPresent()) {
            return mapper.mapToTemplateHistoryResponse(optTemplateHistory.get());
        }

        return Optional.of(template)
                .map(mapper::mapToTemplateHistory)
                .map(templateHistory -> templateHistory.addClient(clientId))
                .map(templateHistoryRepository::saveAndFlush)
                .map(mapper::mapToTemplateHistoryResponse)
                .orElseThrow(() -> new RuntimeException("history.creation"));
    }

    public TemplateHistoryResponse get(Long clientId, Long historyId) {
        return templateHistoryRepository.findByIdAndClientId(historyId, clientId)
                .map(mapper::mapToTemplateHistoryResponse)
                .orElseThrow(() -> new RuntimeException("history.not_found"));
    }
}
