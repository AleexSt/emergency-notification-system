package com.example.template.kafka;

import com.example.template.dto.response.TemplateResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class TemplateKafkaProducer {
    private final Logger log = LoggerFactory.getLogger(TemplateKafkaProducer.class);

    private final KafkaTemplate<String, TemplateRecipientKafka> kafkaTemplate;

    public TemplateKafkaProducer(KafkaTemplate<String, TemplateRecipientKafka> kafkaTemplate){
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendTemplateRecipientUpdateToKafka(TemplateRecipientKafka templateRecipientKafka){
        kafkaTemplate.send("recipient-update", templateRecipientKafka);
    }
}
