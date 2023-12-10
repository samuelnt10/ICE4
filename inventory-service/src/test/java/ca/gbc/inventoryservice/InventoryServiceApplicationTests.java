package ca.gbc.inventoryservice;

import ca.gbc.inventoryservice.dto.InventoryRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
class InventoryServiceApplicationTests extends AbstractContainerBaseTest {

	private static final String API_URL = "/api/inventory";
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;

	private List<InventoryRequest> getRequest() {
		InventoryRequest r1 = InventoryRequest.builder()
				.skuCode("sku_12345")
				.quantity(2)
				.build();

		InventoryRequest r2 = InventoryRequest.builder()
				.skuCode("sku_789123")
				.quantity(5)
				.build();

		List<InventoryRequest> list = new ArrayList<>();
		list.add(r1);
		list.add(r2);

		return list;
	}

	@Test
	void isInStock() throws Exception {
		String payload = objectMapper.writeValueAsString(getRequest());
		MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post(API_URL)
						.contentType(MediaType.APPLICATION_JSON)
						.content(payload))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andDo(MockMvcResultHandlers.print())
				.andReturn();

		String response = result.getResponse().getContentAsString();
		JsonNode json = objectMapper.readTree(response);

		assertEquals(2, json.size());
		JsonNode firstNodeStock = json.get(0).get("sufficientStock");
		assertNotNull(firstNodeStock, "First node 'sufficientStock' field is missing or null");
		assertTrue(firstNodeStock.asBoolean());

		JsonNode secondNodeStock = json.get(1).get("sufficientStock");
		assertNotNull(secondNodeStock, "Second node 'sufficientStock' field is missing or null");
		assertFalse(secondNodeStock.asBoolean());
	}
}
