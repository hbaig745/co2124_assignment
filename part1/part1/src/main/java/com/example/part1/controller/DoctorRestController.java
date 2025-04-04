package com.example.part1.controller;

import com.example.part1.domain.Doctor;
import com.example.part1.repo.DoctorRepo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/doctors")
public class DoctorRestController {
    private final DoctorRepo doctorRepository;

    public DoctorRestController(DoctorRepo doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    @GetMapping
    public ResponseEntity<List<Doctor>> getAllDoctors() {
        return ResponseEntity.ok(doctorRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getDoctorById(@PathVariable Long id) {
        return doctorRepository.findById(id)
                .map(doctor -> ResponseEntity.ok((Object) doctor))
                .orElseGet(() -> {
                    Map<String, String> errorResponse = new HashMap<>();
                    errorResponse.put("error", "Doctor not found");
                    errorResponse.put("message", "No doctor exists with ID " + id);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
                });
    }

    @PostMapping
    public ResponseEntity<Object> createDoctor(@RequestBody Doctor doctor) {
        // Check if ID is provided and already exists
        if (doctor.getId() != null && doctorRepository.existsById(doctor.getId())) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Doctor with ID " + doctor.getId() + " already exists");
            errorResponse.put("message", "Use PUT request to /doctors/" + doctor.getId() + " to update existing doctor");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

        // For new doctor creation, ensure ID is null to let the database generate it
        doctor.setId(null);
        Doctor savedDoctor = doctorRepository.save(doctor);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedDoctor);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateDoctor(@PathVariable Long id, @RequestBody Doctor doctorDetails) {
        return doctorRepository.findById(id)
                .map(doctor -> {
                    doctor.setName(doctorDetails.getName());
                    doctor.setSpecialisation(doctorDetails.getSpecialisation());
                    doctor.setEmail(doctorDetails.getEmail());
                    doctor.setPhoneNumber(doctorDetails.getPhoneNumber());
                    return ResponseEntity.ok((Object) doctorRepository.save(doctor));
                })
                .orElseGet(() -> {
                    Map<String, String> errorResponse = new HashMap<>();
                    errorResponse.put("error", "Doctor not found");
                    errorResponse.put("message", "Cannot update doctor with ID " + id + " as it doesn't exist");
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
                });
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteDoctor(@PathVariable Long id) {
        return doctorRepository.findById(id)
                .map(doctor -> {
                    doctorRepository.delete(doctor);
                    return ResponseEntity.noContent().build();
                })
                .orElseGet(() -> {
                    Map<String, String> errorResponse = new HashMap<>();
                    errorResponse.put("error", "Doctor not found");
                    errorResponse.put("message", "Cannot delete doctor with ID " + id + " as it doesn't exist");
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
                });
    }

    @GetMapping("/{id}/appointments")
    public ResponseEntity<Object> getDoctorAppointments(@PathVariable Long id) {
        return doctorRepository.findById(id)
                .map(doctor -> ResponseEntity.ok((Object) doctor.getAppointments()))
                .orElseGet(() -> {
                    Map<String, String> errorResponse = new HashMap<>();
                    errorResponse.put("error", "Doctor not found");
                    errorResponse.put("message", "Cannot retrieve appointments for doctor with ID " + id + " as it doesn't exist");
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
                });
    }
}