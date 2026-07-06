package com.example.recipient.kafka;

import com.example.recipient.entity.Recipient;
import com.example.recipient.entity.TemplateId;
import com.example.recipient.repository.RecipientRepository;
import com.example.recipient.repository.TemplateIdRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
@Slf4j
public class TemplateRecipientKafkaConsumer {

    private final RecipientRepository recipientRepository;
    private final TemplateIdRepository templateIdRepository;

    //TODO плохо написано надо разобраться
    @Transactional
    @KafkaListener(topics = "recipient-update", groupId = "recipient-group")
    public void consumeTemplateRecipient(ConsumerRecord<String, TemplateRecipientKafka> kafkaUpdate){
        TemplateRecipientKafka update = kafkaUpdate.value();

        switch (update.operation()){
            case PERSIST -> {
                if(!templateIdRepository.existsByTemplateIdAndRecipientId(
                        update.templateId(),
                        update.recipientId()
                )){
                    templateIdRepository.save(
                            TemplateId.builder()
                            .recipient(
                                    Recipient.builder().
                                            id(update.recipientId())
                                            .build()).
                                    templateId(update.templateId())
                                    .build()
                    );
                }
            }

            case REMOVE -> {
                recipientRepository.findById(update.recipientId())
                        .map(recipient -> recipient.removeTemplate(update.templateId()))
                        .ifPresent(recipientRepository::saveAndFlush);
            }


        }

        log.info("Received update template_recipient add link in db");

    }

}
