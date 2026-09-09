package com.eldercare.config;

import com.eldercare.entity.*;
import com.eldercare.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final HospitalRepository hospitalRepository;
    private final DoctorRepository doctorRepository;
    private final DoctorAvailabilityRepository availabilityRepository;
    private final PatientRepository patientRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.count() > 0) {
            log.info("Database already initialized with users. Skipping data seeding.");
            return;
        }

        log.info("Seeding initial data for ElderCare Connect...");

        // 1. Create Default Admin User
        User adminUser = User.builder()
                .email("admin@eldercare.com")
                .password(passwordEncoder.encode("admin123"))
                .role(Role.ROLE_ADMIN)
                .isActive(true)
                .build();
        userRepository.save(adminUser);

        // 2. Create Departments
        Department depGeriatrics = departmentRepository.save(Department.builder()
                .name("Geriatric Medicine")
                .description("Comprehensive medical assessment and care for seniors and chronic condition management")
                .build());

        Department depCardio = departmentRepository.save(Department.builder()
                .name("Cardiology")
                .description("Heart disease management, hypertension, rhythm disorders, and cardiovascular wellness")
                .build());

        Department depOrtho = departmentRepository.save(Department.builder()
                .name("Orthopedics & Joint Care")
                .description("Arthritis, osteoporosis, senior joint pain, and non-surgical mobility support")
                .build());

        Department depNeuro = departmentRepository.save(Department.builder()
                .name("Neurology & Memory Care")
                .description("Dementia, Alzheimer's support, post-stroke rehabilitation, and Parkinson's management")
                .build());

        Department depOphth = departmentRepository.save(Department.builder()
                .name("Ophthalmology")
                .description("Cataracts, glaucoma, diabetic retinopathy, and vision restoration for seniors")
                .build());

        Department depGeneral = departmentRepository.save(Department.builder()
                .name("General Medicine")
                .description("Routine health checkups, diabetic care, and preventive health screenings")
                .build());

        Department depPhysio = departmentRepository.save(Department.builder()
                .name("Physiotherapy & Rehab")
                .description("Balance training, fall prevention, strength recovery, and post-surgery rehabilitation")
                .build());

        // 3. Create Hospitals
        Hospital hospitalApollo = hospitalRepository.save(Hospital.builder()
                .name("Apollo ElderCare Hospital")
                .address("21 Greams Lane, Thousand Lights, Chennai, Tamil Nadu 600006")
                .city("Chennai")
                .phone("+91 44 2829 0200")
                .email("chennai@apolloeldercare.com")
                .about("Comprehensive geriatric inpatient and outpatient facility with specialized memory & senior mobility clinics.")
                .openingHours("Mon-Sun, 24 Hours")
                .build());

        Hospital hospitalFortis = hospitalRepository.save(Hospital.builder()
                .name("Fortis Geriatric Center")
                .address("154/9 Bannerghatta Road, Bengaluru, Karnataka 560076")
                .city("Bengaluru")
                .phone("+91 80 6621 4444")
                .email("contact@fortisgeriatric.com")
                .about("State-of-the-art eldercare facility with dedicated senior ICU, cardiology wing, and fall rehab center.")
                .openingHours("Mon-Sun, 24 Hours")
                .build());

        Hospital hospitalCityCare = hospitalRepository.save(Hospital.builder()
                .name("City Care Elder Wellness Hospital")
                .address("Sector 14, Ring Road, Delhi 110001")
                .city("Delhi")
                .phone("+91 11 2334 5566")
                .email("info@citycarewellness.com")
                .about("Pioneer in community-based elderly health screenings, arthritis clinic, and accessible day care.")
                .openingHours("Mon-Sun, 24 Hours")
                .build());

        // 4. Create Sample Doctors (Approved)
        // Doctor 1: Dr. Ananya Sharma
        User docUser1 = User.builder()
                .email("doctor@eldercare.com")
                .password(passwordEncoder.encode("doctor123"))
                .role(Role.ROLE_DOCTOR)
                .isActive(true)
                .build();

        Doctor doctor1 = doctorRepository.save(Doctor.builder()
                .user(docUser1)
                .fullName("Dr. Ananya Sharma")
                .specialization(depGeriatrics)
                .qualification("MD (Geriatric Medicine), MBBS")
                .experienceYears(14)
                .about("Senior geriatrician passionate about healthy aging, polypharmacy reduction, and cognitive wellness.")
                .hospital(hospitalApollo)
                .consultationFee(new BigDecimal("600.00"))
                .isApproved(true)
                .build());

        // Doctor 2: Dr. Rajesh Rao
        User docUser2 = User.builder()
                .email("dr.rajesh@eldercare.com")
                .password(passwordEncoder.encode("doctor123"))
                .role(Role.ROLE_DOCTOR)
                .isActive(true)
                .build();

        Doctor doctor2 = doctorRepository.save(Doctor.builder()
                .user(docUser2)
                .fullName("Dr. Rajesh Rao")
                .specialization(depCardio)
                .qualification("DM (Cardiology), MD, MBBS")
                .experienceYears(18)
                .about("Specialist in senior cardiovascular health, hypertension management, and heart failure care.")
                .hospital(hospitalFortis)
                .consultationFee(new BigDecimal("800.00"))
                .isApproved(true)
                .build());

        // Doctor 3: Dr. Suresh Menon
        User docUser3 = User.builder()
                .email("dr.suresh@eldercare.com")
                .password(passwordEncoder.encode("doctor123"))
                .role(Role.ROLE_DOCTOR)
                .isActive(true)
                .build();

        Doctor doctor3 = doctorRepository.save(Doctor.builder()
                .user(docUser3)
                .fullName("Dr. Suresh Menon")
                .specialization(depOrtho)
                .qualification("MS (Ortho), Fellowship in Geriatric Mobility")
                .experienceYears(12)
                .about("Focused on arthritis, osteoporosis, non-surgical joint rehabilitation, and fall prevention.")
                .hospital(hospitalCityCare)
                .consultationFee(new BigDecimal("700.00"))
                .isApproved(true)
                .build());

        // Doctor 4: Pending Approval Doctor (for Admin approval demonstration)
        User docUser4 = User.builder()
                .email("dr.priya@eldercare.com")
                .password(passwordEncoder.encode("doctor123"))
                .role(Role.ROLE_DOCTOR)
                .isActive(true)
                .build();

        doctorRepository.save(Doctor.builder()
                .user(docUser4)
                .fullName("Dr. Priya Venkatesh")
                .specialization(depNeuro)
                .qualification("DM (Neurology), MD (Medicine)")
                .experienceYears(10)
                .about("Consultant neurologist specializing in memory disorders and Parkinson's management.")
                .hospital(hospitalApollo)
                .consultationFee(new BigDecimal("750.00"))
                .isApproved(false) // Pending admin approval
                .build());

        // 5. Seed Doctor Availability Schedules (Mon - Sat)
        List<DoctorAvailability> availabilities = new ArrayList<>();
        DayOfWeek[] weekdays = {DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY};

        for (DayOfWeek day : weekdays) {
            // Doctor 1: 09:00 to 13:00 and 14:00 to 17:00
            availabilities.add(DoctorAvailability.builder()
                    .doctor(doctor1)
                    .dayOfWeek(day)
                    .startTime(LocalTime.of(9, 0))
                    .endTime(LocalTime.of(13, 0))
                    .slotDurationMinutes(30)
                    .build());
            availabilities.add(DoctorAvailability.builder()
                    .doctor(doctor1)
                    .dayOfWeek(day)
                    .startTime(LocalTime.of(14, 0))
                    .endTime(LocalTime.of(17, 0))
                    .slotDurationMinutes(30)
                    .build());

            // Doctor 2: 10:00 to 15:00
            availabilities.add(DoctorAvailability.builder()
                    .doctor(doctor2)
                    .dayOfWeek(day)
                    .startTime(LocalTime.of(10, 0))
                    .endTime(LocalTime.of(15, 0))
                    .slotDurationMinutes(30)
                    .build());

            // Doctor 3: 09:30 to 14:30
            availabilities.add(DoctorAvailability.builder()
                    .doctor(doctor3)
                    .dayOfWeek(day)
                    .startTime(LocalTime.of(9, 30))
                    .endTime(LocalTime.of(14, 30))
                    .slotDurationMinutes(30)
                    .build());
        }
        availabilityRepository.saveAll(availabilities);

        // 6. Create Sample Patient
        User patUser = User.builder()
                .email("patient@eldercare.com")
                .password(passwordEncoder.encode("patient123"))
                .role(Role.ROLE_PATIENT)
                .isActive(true)
                .build();

        patientRepository.save(Patient.builder()
                .user(patUser)
                .fullName("Ramaswamy Iyer")
                .mobileNumber("9876543210")
                .dateOfBirth(LocalDate.of(1948, 5, 12))
                .gender("Male")
                .address("42 Heritage Enclave, T. Nagar")
                .city("Chennai")
                .emergencyContactName("Karthik Iyer (Son)")
                .emergencyContactNumber("9840123456")
                .build());

        log.info("Initial data seeded successfully!");
        log.info("Credentials for testing:");
        log.info("  Admin:   admin@eldercare.com   / admin123");
        log.info("  Doctor:  doctor@eldercare.com  / doctor123");
        log.info("  Patient: patient@eldercare.com / patient123");
    }
}
