package com.exelaration.abstractmemery.filters;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;
import static com.exelaration.abstractmemery.constants.SecurityConstants.EXPIRATION_TIME;
import static com.exelaration.abstractmemery.constants.SecurityConstants.HEADER_STRING;
import static com.exelaration.abstractmemery.constants.SecurityConstants.SECRET;
import static com.exelaration.abstractmemery.constants.SecurityConstants.TOKEN_PREFIX;

import com.auth0.jwt.JWT;
import com.exelaration.abstractmemery.domains.ApplicationUser;
import com.exelaration.abstractmemery.repositories.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

  @Autowired private AuthenticationManager authenticationManager;
  @Autowired private UserRepository userRepository;

  public JWTAuthenticationFilter(
      AuthenticationManager authenticationManager, UserRepository userRepository) {
    this.authenticationManager = authenticationManager;
    this.userRepository = userRepository;
  }

  @Override
  public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res)
      throws AuthenticationException {
    try {
      ApplicationUser creds =
          new ObjectMapper().readValue(req.getInputStream(), ApplicationUser.class);

      return authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(
              creds.getUsername(), creds.getPassword(), new ArrayList<>()));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  protected void successfulAuthentication(
      HttpServletRequest req, HttpServletResponse res, FilterChain chain, Authentication auth)
      throws IOException, ServletException {

    String username = ((User) auth.getPrincipal()).getUsername();
    ApplicationUser applicationUser = findUser(username);
    int id = applicationUser.getId();
    String token =
        JWT.create()
            .withSubject(username)
            .withClaim("id", id)
            .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
            .sign(HMAC512(SECRET.getBytes()));
    res.addHeader(HEADER_STRING, TOKEN_PREFIX + token);
  }

  public ApplicationUser findUser(String username) {
    return userRepository.findByUsername(username);
  }
}
