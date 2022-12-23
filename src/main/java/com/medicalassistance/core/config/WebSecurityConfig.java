package com.medicalassistance.core.config;

import com.medicalassistance.core.security.JwtAuthenticationEntryPoint;
import com.medicalassistance.core.security.JwtAuthorizationTokenFilter;
import com.medicalassistance.core.security.JwtTokenUtil;
import com.medicalassistance.core.security.JwtUserDetailService;
import com.medicalassistance.core.util.CaseInsensitiveRequestFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private JwtAuthenticationEntryPoint unauthorizedHandler;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private JwtUserDetailService jwtUserDetailsService;

    @Value("${jwt.header}")
    private String tokenHeader;

    @Value("${jwt.cookieName}")
    private String tokenCookieName;

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        // configure AuthenticationManager so that it knows from where to load
        // user for matching credentials
        // Use BCryptPasswordEncoder
        auth.userDetailsService(jwtUserDetailsService).passwordEncoder(passwordEncoder());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        // We don't need CSRF for this example
        httpSecurity.cors().and().csrf().disable().exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().authorizeRequests()
                .antMatchers(HttpMethod.GET, "/").permitAll()
                .antMatchers(HttpMethod.POST, "/api/v1/patient/login").permitAll()
                .antMatchers(HttpMethod.POST, "/api/v1/patient/signup").permitAll()
                .antMatchers(HttpMethod.POST, "/api/v1/counselor/login").permitAll()
                .antMatchers(HttpMethod.POST, "/api/v1/counselor/signup").permitAll()
                .antMatchers(HttpMethod.POST, "/api/v1/doctor/login").permitAll()
                .antMatchers(HttpMethod.POST, "/api/v1/doctor/signup").permitAll()
                .antMatchers(HttpMethod.GET, "/api/v1/patient/profile").access("hasRole('ROLE_PATIENT')")
                .antMatchers(HttpMethod.PATCH, "/api/v1/patient/profile").access("hasRole('ROLE_PATIENT')")
                .antMatchers("/api/v1/patient/assessment/**").access("hasRole('ROLE_PATIENT')")
                .antMatchers(HttpMethod.GET, "/api/v1/patient/status").access("hasRole('ROLE_PATIENT')")
                .antMatchers("/api/v1/counselor/patient").access("hasRole('ROLE_COUNSELOR')")
                .antMatchers("/api/v1/counselor/patient/**").access("hasRole('ROLE_COUNSELOR')")
                .antMatchers("/api/v1/counselor/doctor").access("hasRole('ROLE_COUNSELOR')")
                .antMatchers(HttpMethod.GET, "/api/v1/counselor/profile").access("hasRole('ROLE_COUNSELOR')")
                .antMatchers(HttpMethod.PATCH, "/api/v1/counselor/profile").access("hasRole('ROLE_COUNSELOR')")
                .antMatchers("/api/v1/doctor/patient").access("hasRole('ROLE_DOCTOR')")
                .antMatchers("/api/v1/doctor/patient/**").access("hasRole('ROLE_DOCTOR')")
                .antMatchers(HttpMethod.GET, "/api/v1/doctor/profile").access("hasRole('ROLE_DOCTOR')")
                .antMatchers(HttpMethod.PATCH, "/api/v1/doctor/profile").access("hasRole('ROLE_DOCTOR')")
                .antMatchers(HttpMethod.GET, "/api/v1/admin/**").access("hasRole('ROLE_ADMIN')");

        // Custom JWT based security filter
        JwtAuthorizationTokenFilter authenticationTokenFilter = new JwtAuthorizationTokenFilter(userDetailsService(),
                jwtTokenUtil, tokenHeader, tokenCookieName);
        httpSecurity.addFilterBefore(authenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);
        CaseInsensitiveRequestFilter caseInsensitiveRequestFilter = new CaseInsensitiveRequestFilter();
        httpSecurity.addFilterAfter(caseInsensitiveRequestFilter, JwtAuthorizationTokenFilter.class);
        httpSecurity.headers().cacheControl();
    }
}
