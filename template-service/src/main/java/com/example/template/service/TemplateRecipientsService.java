package com.example.template.service;

import com.example.template.client.RecipientClient;
import com.example.template.dto.request.RecipientListRequest;
import com.example.template.dto.response.RecipientResponse;
import com.example.template.dto.response.TemplateResponse;
import com.example.template.entity.Template;
import com.example.template.kafka.Operation;
import com.example.template.kafka.TemplateKafkaProducer;
import com.example.template.kafka.TemplateRecipientKafka;
import com.example.template.mapper.TemplateMapper;
import com.example.template.repository.RecipientIdRepository;
import com.example.template.repository.TemplateRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.PriorityQueue;
import java.util.Optional;

import static org.springframework.http.HttpStatus.CREATED;

@Slf4j
@Service
@RequiredArgsConstructor
public class TemplateRecipientsService {

    private final TemplateRepository templateRepository;
    private final RecipientIdRepository recipientIdRepository;
    private final RecipientClient recipientClient;
    private final TemplateMapper mapper;
    private final TemplateKafkaProducer templateKafkaProducer;

    public TemplateResponse addRecipients(Long clientId, Long templateId, RecipientListRequest request) {
        Template template = templateRepository.findByIdAndClientId(templateId, clientId)
                .orElseThrow(() -> new RuntimeException("bed request in templateRecipientsSrvice"));

        for(Long recipientId: request.recipientsIds()){
            if(recipientIdRepository.existsByTemplateIdAndRecipientId(templateId, recipientId)){
                log.warn("Recipient {} has already been registered for Template {}", recipientId, templateId);
                continue;
            }

            try {
                Optional<RecipientResponse> recipientResponse = Optional.ofNullable(recipientClient.receiveRecipientById(clientId, recipientId).getBody()); //recipientClient.receiveRecipientById(clientId, recipientId).getBody()
                recipientResponse.ifPresent(recipientResponse1 -> {
                    template.addRecipient(recipientResponse1.id());
                });
                templateRepository.save(template);

                templateKafkaProducer.sendTemplateRecipientUpdateToKafka(TemplateRecipientKafka.builder()
                        .recipientId(recipientId)
                        .templateId(templateId)
                        .operation(Operation.PERSIST)
                        .build());
            }catch (FeignException.NotFound e){
                log.warn("Recipient {} not found for Client {}", recipientId, clientId);
            }

        }

        return mapper.mapToResponse(template, recipientClient);


    }
}
