package com.hamoon.productservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hamoon.productservice.dto.ProductRequest;
import com.hamoon.productservice.model.Product;
import com.hamoon.productservice.repository.ProductRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class ProductServiceApplicationTests {
	@Container
	static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:4.4.2");
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private ProductRepository productRepository;
	@DynamicPropertySource
	static void setProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
	}
	@Test
	void shouldCreateProduct() throws Exception {
		ProductRequest productRequest =  getProductRequest();
		String productRequestString = objectMapper.writeValueAsString(productRequest);
		mockMvc.perform(MockMvcRequestBuilders.post("/api/product")
				.contentType(MediaType.APPLICATION_JSON)
				.content(productRequestString))
				.andExpect(status().isCreated());
		Assertions.assertEquals(1, productRepository.findAll().size());
	}

	void addToRepo () {
		Product product = Product.builder()
				.id("1")
				.name("Macbook Pro M2 16 inch")
				.description("Macbook Pro M2 16 inch with 16GB RAM and 1TB SSD")
				.price(BigDecimal.valueOf(4500))
				.build();
		productRepository.save(product);
	}
	@Test
	void getProduct() throws Exception{
		addToRepo();
		mockMvc.perform(MockMvcRequestBuilders.get("/api/product")
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());

		Assertions.assertEquals("Macbook Pro M2 16 inch",
				productRepository.findByName("Macbook Pro M2 16 inch").get(0).getName());
	}
	private ProductRequest getProductRequest () {
		return ProductRequest.builder()
				.name("MacBook Pro M2 16 inch")
				.description("MacBook Pro M2 16 inch with 16GB RAM and 1TB SSD")
				.price(BigDecimal.valueOf(4500))
				.build();
	}
}
