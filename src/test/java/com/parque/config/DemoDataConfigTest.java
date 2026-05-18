package com.parque.config;

import com.parque.auth.repository.InternalCredentialRepository;
import com.parque.attraction.model.Attraction;
import com.parque.attraction.repository.AttractionRepository;
import com.parque.booking.dto.BookingCreateRequest;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DemoDataConfigTest {

    @Mock
    private InternalCredentialRepository internalCredentialRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private HotelRepository hotelRepository;

    @Mock
    private AttractionRepository attractionRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private OfferRepository offerRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private BookingService bookingService;

    @Mock
    private ShiftService shiftService;

    @Mock
    private MaintenanceService maintenanceService;

    @Mock
    private PasswordEncoder passwordEncoder;

    private DemoDataConfig demoDataConfig;

    @BeforeEach
    void setUp() {
        demoDataConfig = new DemoDataConfig();
        ReflectionTestUtils.setField(demoDataConfig, "demoAdminUsername", "admin");
        ReflectionTestUtils.setField(demoDataConfig, "demoAdminEmail", "admin@parque.local");
        ReflectionTestUtils.setField(demoDataConfig, "demoAdminPassword", "admin12345");

        lenient().when(passwordEncoder.encode(any())).thenAnswer(invocation -> "encoded-" + invocation.getArgument(0));
        lenient().when(employeeRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        AtomicLong hotelIds = new AtomicLong(1);
        AtomicLong attractionIds = new AtomicLong(10);
        AtomicLong offerIds = new AtomicLong(20);
        AtomicLong userIds = new AtomicLong(30);

        lenient().when(hotelRepository.save(any(Hotel.class))).thenAnswer(invocation -> {
            Hotel hotel = invocation.getArgument(0);
            if (hotel.getId() == null) {
                hotel.setId(hotelIds.getAndIncrement());
            }
            return hotel;
        });
        lenient().when(attractionRepository.save(any(Attraction.class))).thenAnswer(invocation -> {
            Attraction attraction = invocation.getArgument(0);
            if (attraction.getId() == null) {
                attraction.setId(attractionIds.getAndIncrement());
            }
            return attraction;
        });
        lenient().when(offerRepository.save(any(Offer.class))).thenAnswer(invocation -> {
            Offer offer = invocation.getArgument(0);
            if (offer.getId() == null) {
                offer.setId(offerIds.getAndIncrement());
            }
            return offer;
        });
        lenient().when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            if (user.getId() == null) {
                user.setId(userIds.getAndIncrement());
            }
            return user;
        });
        lenient().when(internalCredentialRepository.save(any(InternalCredential.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void shouldSeedFullDemoDataWhenRepositoriesAreEmpty() throws Exception {
        when(internalCredentialRepository.count()).thenReturn(0L);
        when(userRepository.count()).thenReturn(0L);
        when(bookingRepository.count()).thenReturn(0L);
        when(employeeRepository.count()).thenReturn(0L);
        when(hotelRepository.findByName(any())).thenReturn(Optional.empty());
        when(attractionRepository.findByName(any())).thenReturn(Optional.empty());
        when(offerRepository.findAll()).thenReturn(List.of());

        demoDataConfig.demoDataInitializer(
                internalCredentialRepository,
                userRepository,
                hotelRepository,
                attractionRepository,
                employeeRepository,
                offerRepository,
                bookingRepository,
                bookingService,
                shiftService,
                maintenanceService,
                passwordEncoder
        ).run();

        verify(internalCredentialRepository, times(4)).save(any(InternalCredential.class));
        verify(hotelRepository, times(3)).save(any(Hotel.class));
        verify(attractionRepository, times(3)).save(any(Attraction.class));
        verify(offerRepository, times(2)).save(any(Offer.class));
        verify(employeeRepository).saveAll(anyList());
        verify(bookingService, times(3)).create(any(BookingCreateRequest.class));
        verify(shiftService).generate(any(ShiftGenerateRequest.class));
        verify(maintenanceService).generate(any(MaintenanceGenerateRequest.class));

        ArgumentCaptor<BookingCreateRequest> bookingCaptor = ArgumentCaptor.forClass(BookingCreateRequest.class);
        verify(bookingService, times(3)).create(bookingCaptor.capture());

        assertThat(bookingCaptor.getAllValues())
                .extracting(BookingCreateRequest::hotelId)
                .containsExactly(1L, 2L, 3L);
        assertThat(bookingCaptor.getAllValues())
                .extracting(BookingCreateRequest::companions)
                .allSatisfy(companions -> assertThat(companions).isNotEmpty());
    }

    @Test
    void shouldSyncCatalogWithoutDuplicatingDemoUsersOrOperations() throws Exception {
        Hotel existingHotel = Hotel.builder()
                .id(8L)
                .name("Hotel Umbral Nocturno")
                .description("Old")
                .imageUrl("https://old.example/hotel.png")
                .build();
        Attraction existingAttraction = Attraction.builder()
                .id(18L)
                .name("Torre del Terror")
                .description("Old")
                .imageUrl("https://old.example/attraction.png")
                .build();
        Offer canonicalOffer = Offer.builder()
                .id(28L)
                .title("Escapada Familiar Magic Park")
                .description("Old")
                .imageUrl("https://old.example/offer.png")
                .build();
        Offer duplicatedOffer = Offer.builder()
                .id(29L)
                .title("Escapada familiar magic park!!!")
                .description("Old duplicate")
                .imageUrl("https://other.example/offer.png")
                .build();

        when(internalCredentialRepository.count()).thenReturn(1L);
        when(userRepository.count()).thenReturn(2L);
        when(hotelRepository.findByName("Hotel Umbral Nocturno")).thenReturn(Optional.of(existingHotel));
        when(hotelRepository.findByName("Hotel Sendero Carmesí")).thenReturn(Optional.empty());
        when(hotelRepository.findByName("Hotel Refugio de las Sombras")).thenReturn(Optional.empty());
        when(attractionRepository.findByName("Torre del Terror")).thenReturn(Optional.of(existingAttraction));
        when(attractionRepository.findByName("Río de Sangre")).thenReturn(Optional.empty());
        when(attractionRepository.findByName("Laberinto de las Sombras")).thenReturn(Optional.empty());
        when(offerRepository.findAll()).thenReturn(List.of(canonicalOffer, duplicatedOffer));
        when(bookingRepository.existsByOfferId(29L)).thenReturn(false);

        demoDataConfig.demoDataInitializer(
                internalCredentialRepository,
                userRepository,
                hotelRepository,
                attractionRepository,
                employeeRepository,
                offerRepository,
                bookingRepository,
                bookingService,
                shiftService,
                maintenanceService,
                passwordEncoder
        ).run();

        verify(userRepository, never()).save(any(User.class));
        verify(employeeRepository, never()).saveAll(anyList());
        verify(bookingService, never()).create(any(BookingCreateRequest.class));
        verify(shiftService, never()).generate(any(ShiftGenerateRequest.class));
        verify(maintenanceService, never()).generate(any(MaintenanceGenerateRequest.class));
        verify(offerRepository).delete(duplicatedOffer);

        assertThat(existingHotel.getDescription()).contains("Hotel familiar situado");
        assertThat(existingAttraction.getDescription()).contains("Atracción intensa");
    }
}
