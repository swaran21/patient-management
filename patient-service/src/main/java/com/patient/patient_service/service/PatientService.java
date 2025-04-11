package com.patient.patient_service.service;

import billing.BillingServiceGrpc;
import com.patient.patient_service.dto.PatientRequestDTO;
import com.patient.patient_service.dto.PatientResponseDTO;
import com.patient.patient_service.exception.EmailAlreadyExistsException;
import com.patient.patient_service.exception.PatientNotFoundException;
import com.patient.patient_service.grpc.BillingServiceGrpcClient;
import com.patient.patient_service.mapper.PatientMapper;
import com.patient.patient_service.model.Patient;
import com.patient.patient_service.repository.PatientRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class PatientService {
    private final PatientRepository patientRepository;
    private final BillingServiceGrpcClient billingServiceGrpcClient;

    public PatientService(PatientRepository patientRepository, BillingServiceGrpcClient billingServiceGrpcClient) {
        this.patientRepository = patientRepository;
        this.billingServiceGrpcClient = billingServiceGrpcClient;
    }

    public List<PatientResponseDTO> getPatients(){
        List<Patient> patients = patientRepository.findAll();
        //        List<PatientResponseDTO> patientResponseDTOs =
//                patients.stream()
//                        .map(PatientMapper::toDTO).toList();
//        return patientResponseDTOs;
        return patients.stream()
        .map(PatientMapper::toDTO).toList();
    }

    public PatientResponseDTO createPatient(PatientRequestDTO patientRequestDTO) {

        if(patientRepository.existsByEmail(patientRequestDTO.getEmail())) {
            throw new EmailAlreadyExistsException("A patient of this email exists already"+patientRequestDTO.getEmail());

        }

        Patient newPatient = patientRepository.save(
                PatientMapper.toModel(patientRequestDTO)
        );
        billingServiceGrpcClient.createBillingAccount(newPatient.getId().toString(),
                                                        newPatient.getName(),
                                                        newPatient.getEmail());
        return PatientMapper.toDTO(newPatient);
    }

    public PatientResponseDTO updatePatient(UUID id,
                        PatientRequestDTO patientRequestDTO) {
        Patient patient = patientRepository.findById(id).orElseThrow(
                ()->new PatientNotFoundException("Patient not found with ID:"+id));

        if(patientRepository.existsByEmailAndIdNot(patientRequestDTO.getEmail(),id)) {
            throw new EmailAlreadyExistsException("A patient of this email exists already"+patientRequestDTO.getEmail());

        }

        patient.setName(patientRequestDTO.getName());
        patient.setAddress(patientRequestDTO.getAddress());
        patient.setEmail(patientRequestDTO.getEmail());
        patient.setDateOfBirth(LocalDate.parse(patientRequestDTO.getDateOfBirth()));
        Patient updatedPatient = patientRepository.save(patient);
        return PatientMapper.toDTO(updatedPatient);
    }

    public void deletePatient(UUID id) {
        patientRepository.deleteById(id);
    }

}
