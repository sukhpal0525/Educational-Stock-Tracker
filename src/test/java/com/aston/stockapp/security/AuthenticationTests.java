package com.aston.stockapp.security;

import com.aston.stockapp.user.User;
import com.aston.stockapp.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthenticationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testAuthentication() throws Exception {
        User user = userRepository.findByUsername("test");
        mockMvc.perform(formLogin("/login")
                        .user(user.getUsername())
                        .password("test"))
                .andExpect(authenticated().withUsername("test"));
    }

    @Test
    @WithMockUser(username="admin", roles={"ADMIN"})
    public void testAdminAccess() throws Exception {
        mockMvc.perform(get("/admin"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin_dashboard"));
    }

    @Test
    @WithMockUser(username="user", roles={"USER"})
    public void testUserAccessRestriction() throws Exception {
        mockMvc.perform(get("/admin"))
                .andExpect(status().isForbidden());
    }
}