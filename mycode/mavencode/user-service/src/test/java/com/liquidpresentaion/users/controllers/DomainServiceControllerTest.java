package com.liquidpresentaion.users.controllers;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.liquidpresentaion.users.UserServiceApplication;
import com.liquidpresentaion.users.model.Domain;
import com.liquidpresentaion.users.services.DomainService;

import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
//import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;


//@RunWith(SpringRunner.class)
//@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = UserServiceApplication.class)
@WebAppConfiguration
public class DomainServiceControllerTest {

	@InjectMocks
	private DomainServiceController domainController;
	
	@Mock
	private DomainService domainService;
	
	@Autowired
	private WebApplicationContext webApplicationContext;
	
	private MockMvc mvc;

	@Before
	public void setup(){
		// Process mock annotations
//		MockitoAnnotations.initMocks(this);
		
		// Setup Spring test in stand alone mode
		this.mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}
	
//	@Test
	public void testGetDomains() throws Exception{
		Domain domain = new Domain();
		domain.setName(".*@aaron.com");

		Page<Domain> domains = new PageImpl<>(Collections.singletonList(domain));
		
		given(domainController.getDomains("", 0, 10, "", true)).willReturn(domains);
		
		mvc.perform(get("http://localhost:8080/internal/v1/domains/").contentType(APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$", hasSize(1)))
			.andExpect(jsonPath("$[0].regexp", is(domain.getName())));
	}
	
	@Test
	public void testGetDomain() throws Exception{
		Domain domain = new Domain();
		domain.setName(".*@aaron.com");
		
		given(domainController.getDomain(domain.getPkId())).willReturn(domain);
		
		mvc.perform(get("http://localhost:8080/internal/v1/domains/1")
//				.with(user("blaze").password("Q1w2e3r4"))
				.contentType(APPLICATION_JSON)).andExpect(status().isOk()).andExpect(jsonPath("regexp", is(domain.getName())));
	}
}
