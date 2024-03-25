package _6nehemie.com.evoke_estate.config;

import _6nehemie.com.evoke_estate.exceptions.CustomAccessDeniedHandler;
import _6nehemie.com.evoke_estate.filters.JwtAuthenticationFilter;
import _6nehemie.com.evoke_estate.services.UserDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserDetailsServiceImpl userDetailsServiceImpl;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(UserDetailsServiceImpl userDetailsServiceImpl, JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.userDetailsServiceImpl = userDetailsServiceImpl;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        RestAuthenticationEntryPoint restAuthenticationEntryPoint = new RestAuthenticationEntryPoint();
        CustomAccessDeniedHandler customAccessDeniedHandler = new CustomAccessDeniedHandler();

        return http
                .csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(ex -> {
                    ex.authenticationEntryPoint(restAuthenticationEntryPoint);
                    ex.accessDeniedHandler(customAccessDeniedHandler);
                }) // Handle auth errors
                .authorizeHttpRequests(
                        request -> request
                                //? Auth
                                .requestMatchers(HttpMethod.POST ,"/auth/login").permitAll()
                                .requestMatchers(HttpMethod.POST,"/auth/register").permitAll()
                                
                                //? Users
                                .requestMatchers(HttpMethod.GET,"/users/me").authenticated()
                                .requestMatchers(HttpMethod.GET,"/users/{username}").permitAll()
                                
                                //? Posts
                                .requestMatchers(HttpMethod.GET,"/posts/user/*").permitAll()
                                .requestMatchers(HttpMethod.GET,"/posts/*").permitAll()
                                .requestMatchers(HttpMethod.GET,"/posts").permitAll()
                                .requestMatchers(HttpMethod.GET,"/follows/*").permitAll()
                                
                                .requestMatchers(HttpMethod.GET,"/test").authenticated()
                                
                                .anyRequest().authenticated()
                )
                // tells what userDetailsService we need to use to spring
                .userDetailsService(userDetailsServiceImpl)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();

    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
