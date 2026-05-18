package com.parque.cloudinary;

import com.parque.cloudinary.service.CloudinaryUploadResult;
import com.parque.cloudinary.service.RestCloudinaryUploadClient;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class RestCloudinaryUploadClientTest {

    @Test
    void shouldReturnUploadedImageDataFromCloudinaryPayload() {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        server.expect(requestTo("https://api.cloudinary.com/v1_1/demo-cloud/image/upload"))
                .andRespond(withSuccess("""
                        {
                          "secure_url": "https://res.cloudinary.com/demo/image/upload/example.png",
                          "public_id": "hotels/example"
                        }
                        """, MediaType.APPLICATION_JSON));

        RestCloudinaryUploadClient client = new RestCloudinaryUploadClient(builder.build());

        CloudinaryUploadResult result = client.upload(
                new MockMultipartFile("file", "hotel.png", "image/png", new byte[]{1, 2, 3}),
                "hotels",
                "demo-cloud",
                "api-key",
                "api-secret"
        );

        assertThat(result.imageUrl()).isEqualTo("https://res.cloudinary.com/demo/image/upload/example.png");
        assertThat(result.publicId()).isEqualTo("hotels/example");
    }

    @Test
    void shouldRejectInvalidCloudinaryPayload() {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        server.expect(requestTo("https://api.cloudinary.com/v1_1/demo-cloud/image/upload"))
                .andRespond(withSuccess("{\"secure_url\":\"\",\"public_id\":\"\"}", MediaType.APPLICATION_JSON));

        RestCloudinaryUploadClient client = new RestCloudinaryUploadClient(builder.build());

        assertThatThrownBy(() -> client.upload(
                new MockMultipartFile("file", "", "image/png", new byte[]{1}),
                "hotels",
                "demo-cloud",
                "api-key",
                "api-secret"
        )).isInstanceOf(IllegalStateException.class)
                .hasMessage("Invalid Cloudinary response");
    }
}
