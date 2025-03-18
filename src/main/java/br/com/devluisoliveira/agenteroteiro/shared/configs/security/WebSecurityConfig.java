package br.com.devluisoliveira.agenteroteiro.shared.configs.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableMethodSecurity
@EnableWebSecurity
public class WebSecurityConfig {

    private static final String[] AUTH_WHITELIST_URLS = {
            "http://localhost:4200",
            "http://localhost:3000",
            "http://localhost:18512",
            "/swagger-ui/index.html",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/actuator/**",
            "https://editor.swagger.io",
            "editor.swagger.io/**"
    };

    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Autowired
    AuthenticationEntryPointImpl authenticationEntryPoint;

    private static final String[] AUTH_WHITELIST = {
            "/auth/**",
            "/public/**",
            "/callback/**",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/swagger-ui/index.html",
            "/swagger-resources/**",
            "/actuator/**",
            "/v3/api-docs.yaml"
    };


    @Bean
    public AuthenticationJwtFilter authenticationJwtFilter() {
        return new AuthenticationJwtFilter();
    }

    @Bean
    public RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        String hierarchy = """
                 ROLE_ADMIN > ROLE_OWNER
                 ROLE_OWNER > ROLE_MANAGER
                 ROLE_MANAGER > ROLE_HR_MANAGER
                 ROLE_HR_MANAGER > ROLE_ACCOUNTANT
                 ROLE_ACCOUNTANT > ROLE_SALES_MANAGER
                 ROLE_SALES_MANAGER > ROLE_SALES_REP
                 ROLE_SALES_REP > ROLE_CASHIER
                 ROLE_CASHIER > ROLE_MARKETING
                 ROLE_MARKETING > ROLE_LOGISTICS
                 ROLE_LOGISTICS > ROLE_TECH_SUPPORT
                 ROLE_TECH_SUPPORT > ROLE_DEVELOPER
                 ROLE_DEVELOPER > ROLE_CUSTOMER_SERVICE
                 ROLE_CUSTOMER_SERVICE > ROLE_INTERN
                 ROLE_INTERN > ROLE_ANALYST
                 ROLE_ANALYST > ROLE_AUDITOR
                 ROLE_AUDITOR > ROLE_PRODUCT_MANAGER
                 ROLE_PRODUCT_MANAGER > ROLE_PURCHASER
                 ROLE_PURCHASER > ROLE_STOCK_MANAGER
                 ROLE_STOCK_MANAGER > ROLE_OPERATOR
                 ROLE_OPERATOR > ROLE_TRAINER
                 ROLE_TRAINER > ROLE_SUPERVISOR
                 ROLE_SUPERVISOR > ROLE_USER
            """;
        roleHierarchy.setHierarchy(hierarchy);
        return roleHierarchy;
    }


    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(AUTH_WHITELIST_URLS));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type"));
        configuration.setAllowCredentials(true);
        configuration.setExposedHeaders(Arrays.asList("Authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .exceptionHandling(exception ->
                        exception.authenticationEntryPoint(authenticationEntryPoint)
                )
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(AUTH_WHITELIST).permitAll()
                        .anyRequest().authenticated()
                )
                .csrf(csrf -> csrf.disable())
                .headers(headers ->
                        headers.frameOptions(frame -> frame.disable())
                );


        http.addFilterBefore(authenticationJwtFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
