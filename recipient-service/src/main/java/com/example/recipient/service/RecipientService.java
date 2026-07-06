package com.example.recipient.service;

import com.example.recipient.dto.request.RecipientRequest;
import com.example.recipient.dto.response.RecipientResponse;
import com.example.recipient.entity.Recipient;
import com.example.recipient.mapper.RecipientMapper;
import com.example.recipient.repository.RecipientRepository;
import com.example.recipient.repository.TemplateIdRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import com.example.recipient.entity.TemplateId;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class RecipientService {

    private final TemplateIdRepository templateIdRepository;
    private final RecipientRepository recipientRepository;
    private final RecipientMapper mapper;

    public RecipientResponse register(Long clientId, RecipientRequest request){
        Optional<Recipient> existing = recipientRepository.findByEmailAndClientId(request.email(), clientId);
        if (existing.isPresent()){
            return update(clientId, existing.get().getId(), request);
        }

        try {
            return Optional.of(request)
                    .map(mapper::mapToEntity)
                    .map(recipient -> recipient.addClient(clientId))
                    .map(recipientRepository::save)
                    .map(mapper::mapToResponse)
                    .orElseThrow(() -> new RuntimeException("Error in register"));

        }catch (DataIntegrityViolationException e){
            log.warn("problem in service when register ");
            throw new RuntimeException("problem in service when register ");
        }
    }

    public RecipientResponse receive(Long clientId, Long recipientId){
        return recipientRepository.findByIdAndClientId(recipientId, clientId)
                .map(mapper::mapToResponse)
                .orElseThrow(() -> new RuntimeException("Error when receive")
                );
    }

    public Boolean delete(Long clientId, Long recipientId){
        return recipientRepository.findByIdAndClientId(recipientId, clientId)
                .map(recipient -> {
                    recipientRepository.delete(recipient);
                    return recipient;
                })
                .isPresent();
    }


    public List<RecipientResponse> receiveByTemplate(Long clientId, Long templateId){
        return templateIdRepository.findAllByRecipient_clientIdAndTemplateId(clientId, templateId)
                .stream()
                .map(TemplateId::getRecipient)
                .map(mapper::mapToResponse)
                .toList();
    }


    public List<RecipientResponse> receiveByClient(Long clientId){
        return recipientRepository.findAllByClientId(clientId)
                .stream()
                .map(mapper::mapToResponse)
                .toList();
    }

    public RecipientResponse update(Long clientId, Long recipientId, RecipientRequest request){
        try{
            return recipientRepository.findByIdAndClientId(recipientId, clientId)
                    .map(recipient -> mapper.update(request, recipient))
                    .map(recipientRepository::saveAndFlush)
                    .map(mapper::mapToResponse)
                    .orElseThrow(() -> new RuntimeException("Error ocur when uodate")
                    );
        }catch (DataIntegrityViolationException e){
            log.warn("Problem when update");
            throw new RuntimeException("Problem when update");
        }
    }
}
