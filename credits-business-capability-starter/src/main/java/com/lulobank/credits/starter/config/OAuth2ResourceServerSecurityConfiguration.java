package com.lulobank.credits.starter.config;

import com.lulobank.core.security.spring.LuloBankClaimsSetVerifier;
import com.lulobank.core.security.spring.LuloBankJwtDecoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.security.oauth2.server.resource.authentication.JwtBearerTokenAuthenticationConverter;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

/**
 * @author Josh Cummings
 */
@EnableWebSecurity
public class OAuth2ResourceServerSecurityConfiguration extends WebSecurityConfigurerAdapter {
    @Value("${spring.security.oauth2.resourceserver.jwt.private-key-value}")
    RSAPrivateKey privateKey;
    @Value("${spring.security.oauth2.resourceserver.jwt.public-key-value}")
    RSAPublicKey publicKey;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable().cors().and()
        .authorizeRequests(authorizeRequests -> authorizeRequests
                .antMatchers("/swagger-ui/**").permitAll()
                .antMatchers("/swagger-resources/**").permitAll()
                .antMatchers("/webjars/springfox-swagger-ui/**").permitAll()
                .antMatchers("/**/api-docs").permitAll()
                .antMatchers("/info").permitAll()
                .antMatchers("/actuator/**").permitAll()
                .antMatchers("/info.json").permitAll()
                .antMatchers("/**/client/{idClient}/**").access("@webSecurity.checkClientId(authentication,#idClient)")
                .anyRequest()
                .authenticated())
                .oauth2ResourceServer(oauth2ResourceServer -> oauth2ResourceServer.authenticationManagerResolver(request ->  luloBank()));
    }

    AuthenticationManager luloBank() {
        LuloBankJwtDecoder jwtDecoder = LuloBankJwtDecoder.withEncryptionKey(this.privateKey, this.publicKey)
                .build(new LuloBankClaimsSetVerifier());
        JwtAuthenticationProvider authenticationProvider = new JwtAuthenticationProvider(jwtDecoder);
        authenticationProvider.setJwtAuthenticationConverter(new JwtBearerTokenAuthenticationConverter());
        return authenticationProvider::authenticate;
    }


}
