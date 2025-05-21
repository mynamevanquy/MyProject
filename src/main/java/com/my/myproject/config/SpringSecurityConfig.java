package com.my.myproject.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import com.my.myproject.jwt.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SpringSecurityConfig {
	@Autowired
	private JwtAuthenticationFilter jwtAuthenticationFilter;

	@Bean
	public static PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
	    http.csrf(AbstractHttpConfigurer::disable)
	        .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
	        .authorizeHttpRequests(auth -> auth
	        	    .requestMatchers(
	        	        new AntPathRequestMatcher("/api/auth/**"),
	        	        new AntPathRequestMatcher("/swagger-ui/**"),
	        	        new AntPathRequestMatcher("/v3/api-docs/**"),
	        	        new AntPathRequestMatcher("/swagger-ui.html"),
	        	        new AntPathRequestMatcher("/api/password/**")
	        	    ).permitAll()
	        	    .anyRequest().authenticated()
	        	)
	        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
	        .exceptionHandling(exceptions -> exceptions
	            .accessDeniedHandler((request, response, authException) ->
	                response.setStatus(HttpServletResponse.SC_FORBIDDEN))
	            .authenticationEntryPoint((request, response, accessDeniedException) ->
	                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED))
	        )
	        .rememberMe(me -> me.alwaysRemember(true));

	    return http.build();
	}


	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
		return configuration.getAuthenticationManager();
	}
	
	 @Bean
	    public OpenAPI customOpenAPI() {
	        final String securitySchemeName = "bearerAuth";
	        return new OpenAPI()
	            .info(new Info().title("Your API").version("v1"))
	            .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
	            .components(new Components()
	                .addSecuritySchemes(securitySchemeName,
	                    new SecurityScheme()
	                        .name(securitySchemeName)
	                        .type(SecurityScheme.Type.HTTP)
	                        .scheme("bearer")
	                        .bearerFormat("JWT")
	                )
	            );
	    }
}
