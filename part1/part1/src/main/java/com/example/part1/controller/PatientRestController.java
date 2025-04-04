package com.example.part1.controller;

import com.example.part1.domain.Appointment;
import com.example.part1.domain.MedicalRecord;
import com.example.part1.domain.Patient;
import com.example.part1.repo.AppointmentRepo;
import com.example.part1.repo.MedicalRecordRepo;
import com.example.part1.repo.PatientRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/patients")
public class PatientRestController {
    private final PatientRepo patientRepository;
    private final AppointmentRepo appointmentRepository;
    private final MedicalRecordRepo medicalRecordRepository;

    public PatientRestController(PatientRepo patientRepository,
                                 AppointmentRepo appointmentRepository,
                                 MedicalRecordRepo medicalRecordRepository) {
        this.patientRepository = patientRepository;
        this.appointmentRepository = appointmentRepository;
        this.medicalRecordRepository = medicalRecordRepository;
    }

    @GetMapping
    public ResponseEntity<List<Patient>> getAllPatients() {
        return ResponseEntity.ok(patientRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Patient> getPatientById(@PathVariable Long id) {
        return patientRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Patient> createPatient(@RequestBody Patient patient) {
        patient.setId(null);  // Ensure it's treated as a new entity
        Patient savedPatient = patientRepository.save(patient);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedPatient);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Patient> updatePatient(@PathVariable Long id, @RequestBody Patient patientDetails) {
        return patientRepository.findById(id)
                .map(patient -> {
                    patient.setName(patientDetails.getName());
                    patient.setEmail(patientDetails.getEmail());
                    patient.setPhoneNumber(patientDetails.getPhoneNumber());
                    patient.setAddress(patientDetails.getAddress());
                    return ResponseEntity.ok(patientRepository.save(patient));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePatient(@PathVariable Long id) {
        return patientRepository.findById(id)
                .map(patient -> {
                    patientRepository.delete(patient);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/appointments")
    public ResponseEntity<List<Appointment>> getPatientAppointments(@PathVariable Long id) {
        return patientRepository.findById(id)
                .map(patient -> ResponseEntity.ok(patient.getAppointments()))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/medical-records")
    public ResponseEntity<List<MedicalRecord>> getPatientMedicalRecords(@PathVariable Long id) {
        return patientRepository.findById(id)
                .map(patient -> {
                    List<MedicalRecord> records = patient.getAppointments().stream()
                            .map(Appointment::getMedicalRecord)
                            .filter(record -> record != null)
                            .collect(Collectors.toList());
                    return ResponseEntity.ok(records);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}