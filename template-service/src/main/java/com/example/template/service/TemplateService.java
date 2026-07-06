package com.example.template.service;

import com.example.template.client.RecipientClient;
import com.example.template.dto.request.TemplateRequest;
import com.example.template.dto.response.TemplateResponse;
import com.example.template.mapper.TemplateMapper;
import com.example.template.repository.TemplateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TemplateService {

    private final TemplateRepository templateRepository;
    private final RecipientClient recipientClient;
    private final TemplateMapper mapper;

    public TemplateResponse create(Long clientId, TemplateRequest request) {
        if (templateRepository.existsTemplateByClientIdAndTitle(clientId, request.title())) {
            throw new RuntimeException("This template exist yet");

        }

        return Optional.of(request)
                .map(mapper::mapToEntity)
                .map(template -> template.addClient(clientId))
                .map(templateRepository::save)
                .map(template -> mapper.mapToResponse(template, recipientClient))
                .orElseThrow(() -> new RuntimeException("Error")
                );
    }

    public TemplateResponse get(Long clientId, Long templateId){
        return templateRepository.findByIdAndClientId(templateId, clientId)
                .map(template -> mapper.mapToResponse(template, recipientClient))
                .orElseThrow(() -> new RuntimeException("Wrong when templateService try get template by client and template Id"));
    }

    public Boolean delete(Long clientId, Long templateId){
        return templateRepository.findByIdAndClientId(templateId, clientId)
                .map(template -> {
                    templateRepository.delete(template);
                    return template;
                }).isPresent();
    }
}