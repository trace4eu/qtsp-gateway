package com.trace4eu.tsaClient;

import com.trace4eu.tsaClient.dtos.TimestampGenerationResponse;
import com.trace4eu.tsaClient.dtos.TimestampVerificationResponse;
import com.trace4eu.tsaClient.services.TsaRequestGeneratorService;
import com.trace4eu.tsaClient.services.TsaVerifierService;
import org.bouncycastle.cms.CMSException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.security.test.context.support.WithMockUser;


@SpringBootTest
@AutoConfigureMockMvc
public class TsaControllerTest {

    static final String bearerToken = "ory_at_etblZgxdEx4Q3G-ddoKkEOf0XMY3BOLSpVxVHb9Yepo.yl-3f56IVPEpaQdTHoDNSmXAeucZ8kydZuu97VZ6ESQ";
    @Autowired
	private MockMvc mockMvc;

	@MockBean
	private TsaVerifierService tsaVerifierService;
	@MockBean
	private TsaRequestGeneratorService tsaRequestGeneratorService;

	@Test
	@WithMockUser(username = "test-user")
	public void testGetTimestamp() throws Exception {
		String testData = "sample data for timestamp";
		String timestampToken = "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";
		String timestamp = "2024-11-09T23:51:06Z";

		when(tsaRequestGeneratorService.requestTimestampToTsa(testData)).thenReturn(new TimestampGenerationResponse(timestamp, timestampToken));

		mockMvc.perform(MockMvcRequestBuilders.post("/timestamp")
						.content("{\"data\": \"" + testData + "\"}")
						.contentType(MediaType.APPLICATION_JSON)
						.header("Authorization", "Bearer" + bearerToken))
				.andExpect(status().is(201))
				.andExpect(jsonPath("$.timestamp").value(timestamp))
				.andExpect(jsonPath("$.timestampToken").value(timestampToken));
	}

	@Test
	@WithMockUser(username = "test-user")
	public void testVerifyTimestampToken_ValidToken() throws Exception {
		String testToken = "dmFsaWRUb2tlbkJhc2U2NA=="; // "validTokenBase64" in Base64 encoding
		String testData = "sample original data";
		String timestamp = "2024-11-08T11:49:54Z";

		when(tsaVerifierService.verifyTimeStampToken(testToken, testData)).thenReturn(new TimestampVerificationResponse(true, timestamp));

		mockMvc.perform(MockMvcRequestBuilders.post("/verify")
						.content("{\"timestampToken\": \"" + testToken + "\", \"originalData\": \"" + testData + "\"}")
						.contentType(MediaType.APPLICATION_JSON)
						.header("Authorization", "Bearer" + bearerToken))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.result").value(true))
				.andExpect(jsonPath("$.timestamp").value(timestamp));
	}

	@Test
	@WithMockUser(username = "test-user")
	public void testVerifyTimestampToken_InvalidToken() throws Exception {
		String testToken = "dmFsaWRUb2tlbkJhc2U2NA=="; // "invalidTokenBase64" in Base64 encoding
		String testData = "sample original data";

		when(tsaVerifierService.verifyTimeStampToken(testToken, testData)).thenReturn(new TimestampVerificationResponse(false, null));

		mockMvc.perform(MockMvcRequestBuilders.post("/verify")
						.content("{\"timestampToken\": \"" + testToken + "\", \"originalData\": \"" + testData + "\"}")
						.contentType(MediaType.APPLICATION_JSON)
						.header("Authorization", "Bearer" + bearerToken))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.result").value(false))
				.andExpect(jsonPath("$.timestamp").doesNotExist());
	}

	@Test
	@WithMockUser(username = "test-user")
	public void testVerifyTimestampToken_Error() throws Exception {
		String testToken = "dmFsaWRUb2tlbkJhc2U2NA==";
		String testData = "sample original data";

		when(tsaVerifierService.verifyTimeStampToken(testToken, testData))
				.thenThrow(new CMSException("error"));

		mockMvc.perform(MockMvcRequestBuilders.post("/verify")
						.content("{\"timestampToken\": \"" + testToken + "\", \"originalData\": \"" + testData + "\"}")
						.contentType(MediaType.APPLICATION_JSON)
						.header("Authorization", "Bearer" + bearerToken))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.status").value(400))
				.andExpect(jsonPath("$.message").exists())
				.andExpect(jsonPath("$.timestamp").exists());
	}
}
