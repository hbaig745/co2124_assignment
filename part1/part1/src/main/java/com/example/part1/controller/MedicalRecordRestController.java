package com.example.part1.controller;

import com.example.part1.domain.Appointment;
import com.example.part1.domain.MedicalRecord;
import com.example.part1.repo.AppointmentRepo;
import com.example.part1.repo.MedicalRecordRepo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/medical-records")
public class MedicalRecordRestController {
    private final MedicalRecordRepo medicalRecordRepository;
    private final AppointmentRepo appointmentRepository;

    public MedicalRecordRestController(MedicalRecordRepo medicalRecordRepository,
                                       AppointmentRepo appointmentRepository) {
        this.medicalRecordRepository = medicalRecordRepository;
        this.appointmentRepository = appointmentRepository;
    }

    @PostMapping
    public ResponseEntity<Object> createMedicalRecord(@RequestBody MedicalRecord medicalRecord) {
        // Check if ID is provided and already exists
        if (medicalRecord.getId() != null && medicalRecordRepository.existsById(medicalRecord.getId())) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Medical record with ID " + medicalRecord.getId() + " already exists");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

        // Validate appointment exists
        if (medicalRecord.getAppointment() == null || medicalRecord.getAppointment().getId() == null ||
                !appointmentRepository.existsById(medicalRecord.getAppointment().getId())) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Appointment does not exist or is not specified");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

        // Check if the appointment already has a medical record
        Appointment appointment = appointmentRepository.findById(medicalRecord.getAppointment().getId()).orElse(null);
        if (appointment != null && appointment.getMedicalRecord() != null) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Appointment already has a medical record");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

        // For new medical record creation, ensure ID is null to let the database generate it
        medicalRecord.setId(null);
        MedicalRecord savedRecord = medicalRecordRepository.save(medicalRecord);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedRecord);
    }
}
