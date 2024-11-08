package com.trace4eu.tsaClient;

import com.trace4eu.tsaClient.services.TsaVerifierService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class TsaControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private TsaVerifierService tsaVerifierService;

	@Test
	public void testGetTimestamp() throws Exception {
		String testData = "sample data for timestamp";

		mockMvc.perform(MockMvcRequestBuilders.get("/timestamp")
						.content(testData)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.timestamp").exists())
				.andExpect(jsonPath("$.timestampToken").exists());
	}

	@Test
	public void testVerifyTimestampToken_ValidToken() throws Exception {
		String testToken = "dmFsaWRUb2tlbkJhc2U2NA=="; // "validTokenBase64" in Base64 encoding
		String testData = "sample original data";

		when(tsaVerifierService.verifyTimeStampToken(testToken, testData)).thenReturn(true);

		mockMvc.perform(MockMvcRequestBuilders.post("/verify")
						.content("{\"timestampToken\": \"" + testToken + "\", \"originalData\": \"" + testData + "\"}")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("Timestamp token is valid.")));
	}

	@Test
	public void testVerifyTimestampToken_InvalidToken() throws Exception {
		String testToken = "dmFsaWRUb2tlbkJhc2U2NA=="; // "invalidTokenBase64" in Base64 encoding
		String testData = "sample original data";

		when(tsaVerifierService.verifyTimeStampToken(testToken, testData)).thenReturn(false);

		mockMvc.perform(MockMvcRequestBuilders.post("/verify")
						.content("{\"timestampToken\": \"" + testToken + "\", \"originalData\": \"" + testData + "\"}")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andExpect(content().string(containsString("Invalid timestamp token.")));
	}

	@Test
	public void testVerifyTimestampToken_Error() throws Exception {
		String testToken = "dmFsaWRUb2tlbkJhc2U2NA==";
		String testData = "sample original data";

		when(tsaVerifierService.verifyTimeStampToken(testToken, testData))
				.thenThrow(new RuntimeException("Verification error"));

		mockMvc.perform(MockMvcRequestBuilders.post("/verify")
						.content("{\"timestampToken\": \"" + testToken + "\", \"originalData\": \"" + testData + "\"}")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isInternalServerError())
				.andExpect(content().string(containsString("Error during verification: Verification error")));
	}
}
