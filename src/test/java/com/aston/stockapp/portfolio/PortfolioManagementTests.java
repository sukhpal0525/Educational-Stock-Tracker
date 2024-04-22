package com.aston.stockapp.portfolio;

import com.aston.stockapp.domain.portfolio.PortfolioService;
import com.aston.stockapp.user.Role;
import com.aston.stockapp.user.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class PortfolioManagementTests {

    @Autowired private MockMvc mockMvc;
    @Autowired private PortfolioService portfolioService;

    @Test
    @WithMockUser(username="user", roles={"USER"})
    public void testPortfolioCreation() throws Exception {
        mockMvc.perform(post("/portfolio/create")
                        .with(csrf()) // Include CSRF token
                        .param("userId", "1"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    @WithMockUser(username="authorizedUser", roles={"USER"})
    public void testViewPortfolioDisplaysCorrectly() throws Exception {
        mockMvc.perform(get("/portfolio")
                        .param("page", "0")
                        .param("size", "12")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()) // Include CSRF
                        .sessionAttr("SPRING_SECURITY_CONTEXT", securityContext())) // Mimic an existing session with security context
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("itemsPage"))
                .andExpect(model().attributeExists("historicalPerformance"))
                .andExpect(model().attributeExists("portfolio"))
                .andExpect(model().attributeExists("totalCost"))
                .andExpect(model().attributeExists("totalValue"))
                .andExpect(model().attributeExists("totalChangeValue"))
                .andExpect(model().attributeExists("totalChangePercent"))
                .andExpect(model().attributeExists("hasSectorData"))
                .andExpect(model().attributeExists("sectorDistribution"))
                .andExpect(model().attributeExists("portfolioVolatility"));
    }

    private SecurityContext securityContext() {
        // Setup security context, return a  SecurityContext instance
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList("ROLE_USER");

        // Creating user instance using no-argument constructor
        User user = new User();
        user.setUsername("authorizedUser");
        user.setPassword("password");
        user.setIsAdmin(false);

        Set<Role> roles = new HashSet<>();
        Role userRole = new Role();
        userRole.setName(Role.RoleName.USER);
        roles.add(userRole);
        user.setRoles(roles);

        user.setBalance(new BigDecimal("10000"));

        Authentication auth = new UsernamePasswordAuthenticationToken(user, null, authorities);
        context.setAuthentication(auth);
        return context;
    }

    private Authentication authentication() {
        List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList("ROLE_USER");

        User user = new User();
        user.setUsername("authorizedUser");
        user.setPassword("password");

        Role userRole = new Role();
        userRole.setName(Role.RoleName.USER);
        user.setRoles(new HashSet<>(Arrays.asList(userRole)));

        user.setBalance(new BigDecimal("10000"));

        return new UsernamePasswordAuthenticationToken(user, null, authorities);
    }
}