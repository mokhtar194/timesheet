package com.example.timesheet.security;

import com.example.timesheet.service.UserDetailServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig  {

    private PasswordEncoder passwordEncoder;
    private UserDetailServiceImpl userDetailServiceImpl;

    public SecurityConfig(PasswordEncoder passwordEncoder,UserDetailServiceImpl userDetailServiceImpl) {
        this.passwordEncoder = passwordEncoder;
        this.userDetailServiceImpl = userDetailServiceImpl;
    }
    // @Bean
    public InMemoryUserDetailsManager inMemoryUserDetailsManager(){
        return new InMemoryUserDetailsManager(
                User.withUsername("user1").password(passwordEncoder.encode("1234")).roles("USER").build(),
                User.withUsername("user2").password(passwordEncoder.encode("1234")).roles("USER").build(),
                User.withUsername("user3").password(passwordEncoder.encode("1234")).roles("USER").build(),

                User.withUsername("admin").password(passwordEncoder.encode("1234")).roles("USER","ADMIN").build()
        );
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.formLogin((authz) -> authz.loginPage("/login")
                        .defaultSuccessUrl("/",true)
                        .permitAll());
        httpSecurity.rememberMe(withDefaults());
        httpSecurity.authorizeHttpRequests((authz) -> authz
                .requestMatchers("/webjars/**","/h2-console/**").permitAll());
        httpSecurity.authorizeHttpRequests((authz) -> authz
                .anyRequest().authenticated());
        httpSecurity.exceptionHandling(exceptionHandling ->
                exceptionHandling.accessDeniedPage("/notAuthorized")
        );
        httpSecurity.userDetailsService(userDetailServiceImpl);
        return httpSecurity.build();

    }
}
