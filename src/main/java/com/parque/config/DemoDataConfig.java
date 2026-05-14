package com.parque.config;

import com.parque.auth.model.InternalRole;
import com.parque.auth.repository.InternalCredentialRepository;
import com.parque.attraction.model.Attraction;
import com.parque.attraction.repository.AttractionRepository;
import com.parque.booking.dto.BookingCreateRequest;
import com.parque.booking.dto.CompanionRequest;
import com.parque.booking.repository.BookingRepository;
import com.parque.booking.service.booking.BookingService;
import com.parque.employee.model.Employee;
import com.parque.employee.repository.EmployeeRepository;
import com.parque.entity.InternalCredential;
import com.parque.hotel.model.Hotel;
import com.parque.hotel.repository.HotelRepository;
import com.parque.maintenance.dto.MaintenanceGenerateRequest;
import com.parque.maintenance.service.MaintenanceService;
import com.parque.offer.model.Offer;
import com.parque.offer.repository.OfferRepository;
import com.parque.shift.dto.ShiftGenerateRequest;
import com.parque.shift.service.ShiftService;
import com.parque.user.model.User;
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
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@Configuration
@Profile({"dev", "e2e"})
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

            Hotel magicPark = syncHotel(hotelRepository, demoMagicParkHotel());
            Hotel adventure = syncHotel(hotelRepository, demoAdventureHotel());
            Hotel fantasy = syncHotel(hotelRepository, demoFantasyHotel());

            syncAttraction(attractionRepository, demoDragonCoasterAttraction());
            syncAttraction(attractionRepository, demoSplashRiverAttraction());
            syncAttraction(attractionRepository, demoFantasyCarouselAttraction());

            syncOffer(offerRepository, bookingRepository, demoMagicParkOffer(magicPark));
            syncOffer(offerRepository, bookingRepository, demoAdventureOffer(adventure));

            if (userRepository.count() > 0 || bookingRepository.count() > 0 || employeeRepository.count() > 0) {
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
        if (internalCredentialRepository.count() > 0) {
            return;
        }

        internalCredentialRepository.save(InternalCredential.builder()
                .username(demoAdminUsername)
                .email(demoAdminEmail)
                .passwordHash(passwordEncoder.encode(demoAdminPassword))
                .role(InternalRole.ADMIN)
                .active(true)
                .build());

        internalCredentialRepository.save(InternalCredential.builder()
                .username("manager")
                .email("manager@parque.local")
                .passwordHash(passwordEncoder.encode("manager123"))
                .role(InternalRole.MANAGER)
                .active(true)
                .build());

        internalCredentialRepository.save(InternalCredential.builder()
                .username("employee")
                .email("employee@parque.local")
                .passwordHash(passwordEncoder.encode("employee123"))
                .role(InternalRole.EMPLOYEE)
                .active(true)
                .build());

        internalCredentialRepository.save(InternalCredential.builder()
                .username("user")
                .email("user@parque.local")
                .passwordHash(passwordEncoder.encode("user12345"))
                .role(InternalRole.USER)
                .active(true)
                .build());
    }

    private Hotel syncHotel(HotelRepository hotelRepository, Hotel demoHotel) {
        Hotel hotel = hotelRepository.findByName(demoHotel.getName())
                .map(existingHotel -> {
                    existingHotel.setDescription(demoHotel.getDescription());
                    existingHotel.setHalfBoardPrice(demoHotel.getHalfBoardPrice());
                    existingHotel.setFullBoardPrice(demoHotel.getFullBoardPrice());
                    existingHotel.setImageUrl(demoHotel.getImageUrl());
                    return existingHotel;
                })
                .orElse(demoHotel);

        return hotelRepository.save(hotel);
    }

    private Attraction syncAttraction(AttractionRepository attractionRepository, Attraction demoAttraction) {
        Attraction attraction = attractionRepository.findByName(demoAttraction.getName())
                .map(existingAttraction -> {
                    existingAttraction.setDescription(demoAttraction.getDescription());
                    existingAttraction.setSize(demoAttraction.getSize());
                    existingAttraction.setImageUrl(demoAttraction.getImageUrl());
                    existingAttraction.setMaintenanceFrequencyDays(demoAttraction.getMaintenanceFrequencyDays());
                    return existingAttraction;
                })
                .orElse(demoAttraction);

        return attractionRepository.save(attraction);
    }

    private Offer syncOffer(OfferRepository offerRepository, BookingRepository bookingRepository, Offer demoOffer) {
        List<Offer> matchingOffers = offerRepository.findAll().stream()
                .filter(existingOffer -> matchesDemoOffer(existingOffer, demoOffer))
                .sorted(Comparator.comparing(Offer::getId))
                .toList();

        Offer offer = matchingOffers.isEmpty()
                ? demoOffer
                : matchingOffers.getFirst();

        offer.setTitle(demoOffer.getTitle());
        offer.setDescription(demoOffer.getDescription());
        offer.setHotel(demoOffer.getHotel());
        offer.setBoardType(demoOffer.getBoardType());
        offer.setIncludedTickets(demoOffer.getIncludedTickets());
        offer.setTotalPrice(demoOffer.getTotalPrice());
        offer.setImageUrl(demoOffer.getImageUrl());

        Offer savedOffer = offerRepository.save(offer);

        matchingOffers.stream()
                .skip(1)
                .filter(existingOffer -> !bookingRepository.existsByOfferId(existingOffer.getId()))
                .forEach(offerRepository::delete);

        return savedOffer;
    }

    private boolean matchesDemoOffer(Offer existingOffer, Offer demoOffer) {
        if (existingOffer.getTitle().equals(demoOffer.getTitle())) {
            return true;
        }

        if (existingOffer.getImageUrl().equals(demoOffer.getImageUrl())) {
            return true;
        }

        return normalizeOfferTitle(existingOffer.getTitle()).equals(normalizeOfferTitle(demoOffer.getTitle()));
    }

    private String normalizeOfferTitle(String title) {
        return title == null
                ? ""
                : title.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9 ]", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }

    private Hotel demoMagicParkHotel() {
        return Hotel.builder()
                .name("Hotel Magic Park")
                .description("Hotel familiar situado junto al parque.")
                .totalRooms(120)
                .availableRooms(120)
                .totalPlaces(240)
                .availablePlaces(240)
                .halfBoardPrice(new BigDecimal("80.00"))
                .fullBoardPrice(new BigDecimal("120.00"))
                .imageUrl("https://res.cloudinary.com/dp3qqp2ns/image/upload/v1778153079/hotels/publicHomeHeroGate_sytdho.png")
                .build();
    }

    private Hotel demoAdventureHotel() {
        return Hotel.builder()
                .name("Hotel Adventure")
                .description("Hotel tematizado para estancias cortas.")
                .totalRooms(90)
                .availableRooms(90)
                .totalPlaces(180)
                .availablePlaces(180)
                .halfBoardPrice(new BigDecimal("70.00"))
                .fullBoardPrice(new BigDecimal("110.00"))
                .imageUrl("https://res.cloudinary.com/dp3qqp2ns/image/upload/v1778494828/hotels/publicHomeParkMap_d23ikl.png")
                .build();
    }

    private Hotel demoFantasyHotel() {
        return Hotel.builder()
                .name("Hotel Fantasy")
                .description("Hotel premium para familias.")
                .totalRooms(80)
                .availableRooms(80)
                .totalPlaces(160)
                .availablePlaces(160)
                .halfBoardPrice(new BigDecimal("65.00"))
                .fullBoardPrice(new BigDecimal("95.00"))
                .imageUrl("https://res.cloudinary.com/dp3qqp2ns/image/upload/v1778234157/hotels/chef_bbsbqp.jpg")
                .build();
    }

    private Attraction demoDragonCoasterAttraction() {
        return Attraction.builder()
                .name("Dragon Coaster")
                .description("Montana rusa principal del parque.")
                .size("LARGE")
                .status("OPEN")
                .totalSeats(32)
                .availableSeats(32)
                .maintenanceFrequencyDays(7)
                .imageUrl("https://res.cloudinary.com/dp3qqp2ns/image/upload/v1778222227/attractions/attractionTerrorTower_hbkqm6.png")
                .build();
    }

    private Attraction demoSplashRiverAttraction() {
        return Attraction.builder()
                .name("Splash River")
                .description("Recorrido acuatico familiar.")
                .size("MEDIUM")
                .status("OPEN")
                .totalSeats(24)
                .availableSeats(24)
                .maintenanceFrequencyDays(14)
                .imageUrl("https://res.cloudinary.com/dp3qqp2ns/image/upload/v1778221870/attractions/attractionBloodRiver_kx4mxb.png")
                .build();
    }

    private Attraction demoFantasyCarouselAttraction() {
        return Attraction.builder()
                .name("Fantasy Carousel")
                .description("Atraccion infantil del area fantasy.")
                .size("SMALL")
                .status("OPEN")
                .totalSeats(18)
                .availableSeats(18)
                .maintenanceFrequencyDays(30)
                .imageUrl("https://res.cloudinary.com/dp3qqp2ns/image/upload/v1778221799/attractions/attractionDarkLabyrinth_yqjgnt.png")
                .build();
    }

    private Offer demoMagicParkOffer(Hotel hotel) {
        return Offer.builder()
                .title("Escapada Familiar Magic Park")
                .description("Hotel + entradas para una escapada de fin de semana.")
                .hotel(hotel)
                .boardType("FULL_BOARD")
                .includedTickets(4)
                .totalPrice(new BigDecimal("399.99"))
                .imageUrl("https://res.cloudinary.com/dp3qqp2ns/image/upload/v1778494828/offers/offerHotelTicket_d8hvg3.png")
                .build();
    }

    private Offer demoAdventureOffer(Hotel hotel) {
        return Offer.builder()
                .title("Oferta Aventura")
                .description("Hotel + entradas para dos adultos y un nino.")
                .hotel(hotel)
                .boardType("HALF_BOARD")
                .includedTickets(3)
                .totalPrice(new BigDecimal("249.99"))
                .imageUrl("https://res.cloudinary.com/dp3qqp2ns/image/upload/v1778494828/offers/offerFamilyPack_tzegmw.png")
                .build();
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
