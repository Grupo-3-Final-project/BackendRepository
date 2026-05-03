package com.parque.config;

import com.parque.auth.model.InternalRole;
import com.parque.auth.repository.InternalCredentialRepository;
import com.parque.attraction.repository.AttractionRepository;
import com.parque.booking.dto.BookingCreateRequest;
import com.parque.booking.dto.CompanionRequest;
import com.parque.booking.repository.BookingRepository;
import com.parque.booking.service.BookingService;
import com.parque.employee.repository.EmployeeRepository;
import com.parque.entity.Attraction;
import com.parque.entity.Employee;
import com.parque.entity.Hotel;
import com.parque.entity.InternalCredential;
import com.parque.entity.Offer;
import com.parque.entity.User;
import com.parque.hotel.repository.HotelRepository;
import com.parque.maintenance.dto.MaintenanceGenerateRequest;
import com.parque.maintenance.service.MaintenanceService;
import com.parque.offer.repository.OfferRepository;
import com.parque.shift.dto.ShiftGenerateRequest;
import com.parque.shift.service.ShiftService;
import com.parque.user.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Configuration
@Profile("dev")
@ConditionalOnProperty(prefix = "app.demo-data", name = "enabled", havingValue = "true", matchIfMissing = true)
public class DemoDataConfig {

    @Value("${app.demo-admin.username}")
    private String demoAdminUsername;

    @Value("${app.demo-admin.email}")
    private String demoAdminEmail;

    @Value("${app.demo-admin.password}")
    private String demoAdminPassword;

    @Bean
    public CommandLineRunner demoDataInitializer(
            InternalCredentialRepository internalCredentialRepository,
            UserRepository userRepository,
            HotelRepository hotelRepository,
            AttractionRepository attractionRepository,
            EmployeeRepository employeeRepository,
            OfferRepository offerRepository,
            BookingRepository bookingRepository,
            BookingService bookingService,
            ShiftService shiftService,
            MaintenanceService maintenanceService,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {
            seedInternalCredential(internalCredentialRepository, passwordEncoder);

            if (userRepository.count() > 0 || hotelRepository.count() > 0 || bookingRepository.count() > 0) {
                return;
            }

            User david = userRepository.save(User.builder()
                    .firstName("David")
                    .lastName("Navarro")
                    .dni("12345678A")
                    .email("david@example.com")
                    .phone("600123123")
                    .birthDate(LocalDate.parse("1990-04-15"))
                    .build());

            User ana = userRepository.save(User.builder()
                    .firstName("Ana")
                    .lastName("Garcia")
                    .dni("87654321B")
                    .email("ana@example.com")
                    .phone("611222333")
                    .birthDate(LocalDate.parse("1988-03-10"))
                    .build());

            Hotel magicPark = hotelRepository.save(Hotel.builder()
                    .name("Hotel Magic Park")
                    .description("Hotel familiar situado junto al parque.")
                    .totalRooms(120)
                    .availableRooms(120)
                    .totalPlaces(240)
                    .availablePlaces(240)
                    .halfBoardPrice(new BigDecimal("80.00"))
                    .fullBoardPrice(new BigDecimal("120.00"))
                    .imageUrl("https://res.cloudinary.com/demo/image/upload/hotel.jpg")
                    .build());

            Hotel adventure = hotelRepository.save(Hotel.builder()
                    .name("Hotel Adventure")
                    .description("Hotel tematizado para estancias cortas.")
                    .totalRooms(90)
                    .availableRooms(90)
                    .totalPlaces(180)
                    .availablePlaces(180)
                    .halfBoardPrice(new BigDecimal("70.00"))
                    .fullBoardPrice(new BigDecimal("110.00"))
                    .imageUrl("https://res.cloudinary.com/demo/image/upload/hotel2.jpg")
                    .build());

            Hotel fantasy = hotelRepository.save(Hotel.builder()
                    .name("Hotel Fantasy")
                    .description("Hotel premium para familias.")
                    .totalRooms(80)
                    .availableRooms(80)
                    .totalPlaces(160)
                    .availablePlaces(160)
                    .halfBoardPrice(new BigDecimal("65.00"))
                    .fullBoardPrice(new BigDecimal("95.00"))
                    .imageUrl("https://res.cloudinary.com/demo/image/upload/hotel3.jpg")
                    .build());

            attractionRepository.saveAll(List.of(
                    Attraction.builder()
                            .name("Dragon Coaster")
                            .description("Montana rusa principal del parque.")
                            .size("LARGE")
                            .status("OPEN")
                            .totalSeats(32)
                            .availableSeats(32)
                            .maintenanceFrequencyDays(7)
                            .imageUrl("https://res.cloudinary.com/demo/image/upload/attraction.jpg")
                            .build(),
                    Attraction.builder()
                            .name("Splash River")
                            .description("Recorrido acuatico familiar.")
                            .size("MEDIUM")
                            .status("OPEN")
                            .totalSeats(24)
                            .availableSeats(24)
                            .maintenanceFrequencyDays(14)
                            .imageUrl("https://res.cloudinary.com/demo/image/upload/attraction2.jpg")
                            .build(),
                    Attraction.builder()
                            .name("Fantasy Carousel")
                            .description("Atraccion infantil del area fantasy.")
                            .size("SMALL")
                            .status("OPEN")
                            .totalSeats(18)
                            .availableSeats(18)
                            .maintenanceFrequencyDays(30)
                            .imageUrl("https://res.cloudinary.com/demo/image/upload/attraction3.jpg")
                            .build()
            ));

            employeeRepository.saveAll(List.of(
                    employee("Laura", "Gomez", "11111111A", "laura.gomez@example.com", "TECHNICIAN", "MORNING"),
                    employee("Mario", "Lopez", "22222222B", "mario.lopez@example.com", "TECHNICIAN", "AFTERNOON"),
                    employee("Clara", "Santos", "33333333C", "clara.santos@example.com", "TECHNICIAN", "MORNING"),
                    employee("Lucia", "Martin", "44444444D", "lucia.martin@example.com", "CLEANER", "MORNING"),
                    employee("Jorge", "Ruiz", "55555555E", "jorge.ruiz@example.com", "CLEANER", "AFTERNOON"),
                    employee("Paula", "Vega", "66666666F", "paula.vega@example.com", "CLEANER", "MORNING"),
                    employee("Elena", "Perez", "77777777G", "elena.perez@example.com", "ANIMATOR", "MORNING"),
                    employee("Diego", "Moreno", "88888888H", "diego.moreno@example.com", "ANIMATOR", "AFTERNOON"),
                    employee("Sofia", "Navarro", "99999999J", "sofia.navarro@example.com", "ANIMATOR", "MORNING")
            ));

            offerRepository.saveAll(List.of(
                    Offer.builder()
                            .title("Escapada Familiar Magic Park")
                            .description("Hotel + entradas para una escapada de fin de semana.")
                            .hotel(magicPark)
                            .boardType("FULL_BOARD")
                            .includedTickets(4)
                            .totalPrice(new BigDecimal("399.99"))
                            .imageUrl("https://res.cloudinary.com/demo/image/upload/offer.jpg")
                            .build(),
                    Offer.builder()
                            .title("Oferta Aventura")
                            .description("Hotel + entradas para dos adultos y un nino.")
                            .hotel(adventure)
                            .boardType("HALF_BOARD")
                            .includedTickets(3)
                            .totalPrice(new BigDecimal("249.99"))
                            .imageUrl("https://res.cloudinary.com/demo/image/upload/offer2.jpg")
                            .build()
            ));

            bookingService.create(new BookingCreateRequest(
                    david.getId(),
                    null,
                    magicPark.getId(),
                    "FULL_BOARD",
                    LocalDate.now().plusDays(10),
                    List.of(
                            new CompanionRequest("David", "Navarro", LocalDate.parse("1990-04-15")),
                            new CompanionRequest("Lucas", "Navarro", LocalDate.parse("2015-07-20")),
                            new CompanionRequest("Maria", "Navarro", LocalDate.parse("1952-02-11"))
                    )
            ));

            bookingService.create(new BookingCreateRequest(
                    ana.getId(),
                    null,
                    adventure.getId(),
                    "HALF_BOARD",
                    LocalDate.now().plusDays(20),
                    List.of(
                            new CompanionRequest("Ana", "Garcia", LocalDate.parse("1988-03-10")),
                            new CompanionRequest("Nora", "Garcia", LocalDate.parse("2014-09-22"))
                    )
            ));

            bookingService.create(new BookingCreateRequest(
                    david.getId(),
                    null,
                    fantasy.getId(),
                    "HALF_BOARD",
                    LocalDate.now().plusDays(30),
                    List.of(
                            new CompanionRequest("Pedro", "Lopez", LocalDate.parse("1984-11-05")),
                            new CompanionRequest("Laura", "Lopez", LocalDate.parse("1986-06-18"))
                    )
            ));

            LocalDate monthStart = LocalDate.now().withDayOfMonth(1);
            LocalDate monthEnd = monthStart.plusMonths(1).minusDays(1);

            shiftService.generate(new ShiftGenerateRequest(monthStart, monthEnd));
            maintenanceService.generate(new MaintenanceGenerateRequest(LocalDate.now(), LocalDate.now().plusDays(30)));
        };
    }

    private void seedInternalCredential(
            InternalCredentialRepository internalCredentialRepository,
            PasswordEncoder passwordEncoder
    ) {
        if (internalCredentialRepository.findByUsername(demoAdminUsername).isPresent()) {
            return;
        }

        internalCredentialRepository.save(InternalCredential.builder()
                .username(demoAdminUsername)
                .email(demoAdminEmail)
                .passwordHash(passwordEncoder.encode(demoAdminPassword))
                .role(InternalRole.ADMIN)
                .active(true)
                .build());
    }

    private Employee employee(
            String firstName,
            String lastName,
            String dni,
            String email,
            String employeeType,
            String shift
    ) {
        return Employee.builder()
                .firstName(firstName)
                .lastName(lastName)
                .dni(dni)
                .email(email)
                .employeeType(employeeType)
                .shift(shift)
                .active(true)
                .build();
    }
}
