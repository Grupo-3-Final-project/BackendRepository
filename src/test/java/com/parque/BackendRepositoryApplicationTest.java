package com.parque;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;

import static org.mockito.Mockito.mockStatic;

class BackendRepositoryApplicationTest {

    @Test
    void main_shouldDelegateToSpringApplicationRun() {
        String[] args = {"--spring.main.web-application-type=none"};

        try (MockedStatic<SpringApplication> springApplication = mockStatic(SpringApplication.class)) {
            BackendRepositoryApplication.main(args);

            springApplication.verify(() -> SpringApplication.run(BackendRepositoryApplication.class, args));
        }
    }
}
