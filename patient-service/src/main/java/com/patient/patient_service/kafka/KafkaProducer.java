package com.patient.patient_service.kafka;

import com.patient.patient_service.model.Patient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import patient.events.PatientEvent;

import java.nio.file.attribute.AclEntry;

@Slf4j
@Service
public class KafkaProducer {

    private final KafkaTemplate<String,byte[]> kafkaTemplate;

    public KafkaProducer(KafkaTemplate<String,byte[]> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendEvent(Patient patient){
        PatientEvent event = PatientEvent.newBuilder().
                setPatientId(patient.getId().toString()).
                        setName(patient.getName()).
                setEmail(patient.getEmail()).
                setEventType("PATIENT_CREATED").build();
            try {
            kafkaTemplate.send("patient", event.toByteArray());
            }
            catch(Exception e){
                log.error("Error sending PatientCreated: {}",event);
            }
    }

}
