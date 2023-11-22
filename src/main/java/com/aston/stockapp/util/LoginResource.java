//package com.aston.stockapp.account.auth.web;
//
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.core.Authentication;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//
//@Controller
//public class LoginResource {
////
////    private final AuthenticationManager authenticationManager;
////
////    public LoginController(AuthenticationManager authenticationManager) {
////        this.authenticationManager = authenticationManager;
////    }
////
////    @PostMapping("/login")
////    public ResponseEntity<Void> login(@RequestBody LoginRequest loginRequest) {
////        Authentication authenticationRequest =
////                UsernamePasswordAuthenticationToken.unauthenticated(loginRequest.username(), loginRequest.password());
////        Authentication authenticationResponse =
////                this.authenticationManager.authenticate(authenticationRequest);
////        // ...
////    }
////
////    public record LoginRequest(String username, String password) {
////    }
//}