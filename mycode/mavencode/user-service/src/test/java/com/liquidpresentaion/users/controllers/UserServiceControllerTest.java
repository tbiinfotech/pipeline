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
import com.liquidpresentaion.users.model.User;
import com.liquidpresentaion.users.services.UserService;

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

public class UserServiceControllerTest {

	@InjectMocks
	private UserServiceController userController;
	
	@Mock
	private UserService userService;
	
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
	public void testGetUsers() throws Exception{
		User user = new User();
		user.setFirstName("Jun");

		Page<User> users = new PageImpl<>(Collections.singletonList(user));
		
		given(userController.getUsers("", 0, 10, "", true,false)).willReturn(users);
		
		mvc.perform(get("http://localhost:8080/internal/v1/users/").contentType(APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$", hasSize(1)))
			.andExpect(jsonPath("$[0].regexp", is(user.getFirstName())));
	}
	
	@Test
	public void testGetUser() throws Exception{
		User user = new User();
		user.setFirstName("Jun");
		
		given(userController.getUser(user.getPkId())).willReturn(user);
		
		mvc.perform(get("http://localhost:8080/internal/v1/users/1")
				.contentType(APPLICATION_JSON)).andExpect(status().isOk()).andExpect(jsonPath("regexp", is(user.getFirstName())));
	}
}
