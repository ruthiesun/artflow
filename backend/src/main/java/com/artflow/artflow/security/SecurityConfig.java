/**
 * All custom JWT authentication code was modified from https://medium.com/@ihor.polataiko/spring-security-guide-part-3-authentication-with-jwt-token-42c23a1c375d
 */

package com.artflow.artflow.security;

import com.artflow.artflow.common.UriUtil;
import com.artflow.artflow.security.filter.BearerAuthenticationFilter;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	private final BearerAuthenticationFilter filter;
	
	public SecurityConfig(BearerAuthenticationFilter filter) {
		this.filter = filter;
	}
	
	@Value("${google.app.credentials.path}")
	private String firebaseCredentialsPath;
	
	@PostConstruct
	public void init() throws IOException {
		if (FirebaseApp.getApps().isEmpty()) {
			FileInputStream serviceAccount = new FileInputStream(firebaseCredentialsPath);
			
			FirebaseOptions options = FirebaseOptions.builder()
				.setCredentials(GoogleCredentials.fromStream(serviceAccount))
				.build();
			
			FirebaseApp.initializeApp(options);
		}
	}
	
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		return http
				.addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class)
				.authorizeHttpRequests((requests) -> requests
						.requestMatchers(HttpMethod.POST,
							UriUtil.getLoginUri(),
							UriUtil.getSignupUri(),
							UriUtil.getResetRequestUri(),
							UriUtil.getResetUri()).permitAll()
						.requestMatchers(HttpMethod.GET,
							UriUtil.getVerifyUri(),
							UriUtil.getProjectWildcardUri(),
							UriUtil.getProjectsWildcardUri(),
							UriUtil.getProjectImagesWildcardUri(),
							UriUtil.getProjectImageWildcardUri(),
							UriUtil.getProjectTagsWildcardUri(),
							UriUtil.getProjectTagWildcardUri(),
							UriUtil.getTagsWildcardUri()).permitAll()
						.anyRequest().authenticated())
				.cors(Customizer.withDefaults())
				.csrf(AbstractHttpConfigurer::disable)
				.sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.build();
	}
	
	@Bean
	public PasswordEncoder encoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowedOrigins(List.of("http://localhost:5173"));
		config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
		config.setAllowedHeaders(List.of("*"));
		config.setAllowCredentials(true);
		
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config);
		return source;
	}
}
