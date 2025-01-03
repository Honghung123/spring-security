package com.awad.tmdb.config;

import com.awad.tmdb.component.auth.CustomAuthenticationEntryPoint;
import com.awad.tmdb.component.auth.JwtFilterPerRequest;
import com.awad.tmdb.component.auth.oauth2.HttpCookieOAuth2AuthorizationRequestRepository;
import com.awad.tmdb.component.auth.oauth2.OAuth2AuthenticationFailureHandler;
import com.awad.tmdb.component.auth.oauth2.OAuth2AuthenticationSuccessHandler;
import com.awad.tmdb.component.auth.usernamepassword.CustomRequestCache;
import com.awad.tmdb.component.auth.usernamepassword.UsernamePasswordAuhenticationFailureHandler;
import com.awad.tmdb.component.auth.usernamepassword.UsernamePasswordAuhenticationSuccessHandler;
import com.awad.tmdb.constant.AppPropertiesConfig;
import com.awad.tmdb.service.user.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer;
import org.springframework.security.config.annotation.web.configurers.oauth2.client.OAuth2LoginConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity // Allow config security for each request
@EnableMethodSecurity // Allow authorize based-role for each method
@RequiredArgsConstructor
public class WebSecurityConfig {
    private final AppPropertiesConfig configProperties;
    private final JwtFilterPerRequest jwtFilter;
    private final DaoAuthenticationProvider daoAauthenticationProvider;
    private final AuthenticationConfiguration authenticationConfiguration;
    // OAuth2
    private final HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRqRepo;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
    private final OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;
    // Username password authentication
    private final UsernamePasswordAuhenticationSuccessHandler usernamePasswordAuthenticationSuccessHandler;
    private final UsernamePasswordAuhenticationFailureHandler usernamePasswordAuthenticationFailureHandler;

    private final String[] BYPASS_URLS = {
            "/", "/login-success",
            "/auth/**", "/auth",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/webjars/**",
            "/swagger-resources/**",
            "/swagger-ui.html",
            "/api-docs/**",
            "/swagger-resources/**"
    };

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        var customUsernamePasswordAuthenticationFilter = this.usernamePasswordAuthenticationFilter();
        return http.csrf(csrf -> csrf.disable())
                .addFilter(customUsernamePasswordAuthenticationFilter)
                .addFilterBefore(jwtFilter, customUsernamePasswordAuthenticationFilter.getClass())
                .authenticationProvider(daoAauthenticationProvider)
                .authorizeHttpRequests(
                        request -> {
                            request.requestMatchers(BYPASS_URLS)
                                    .permitAll()
                                    .anyRequest()
                                    .authenticated();
                        })
                .sessionManagement(this.statelessSessionCustomizer())
                .oauth2Login(this.oauth2LoginCustomizer())
                .formLogin(login -> login.disable()) // Disable default form login
                .requestCache(cache -> cache.requestCache(new CustomRequestCache())) // Custom request cache
                .exceptionHandling(exception ->
                        exception.authenticationEntryPoint(new CustomAuthenticationEntryPoint()))
                .build();
    }

    private Customizer<SessionManagementConfigurer<HttpSecurity>> statelessSessionCustomizer() {
        return session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    private Customizer<OAuth2LoginConfigurer<HttpSecurity>> oauth2LoginCustomizer() {
        return oauth2 -> {
            oauth2.loginPage("/auth/login")
                    .authorizationEndpoint(
                            auth -> auth.authorizationRequestRepository(httpCookieOAuth2AuthorizationRqRepo))
                    .userInfoEndpoint(endpoint -> endpoint.userService(customOAuth2UserService))
                    .successHandler(oAuth2AuthenticationSuccessHandler)
                    .failureHandler(oAuth2AuthenticationFailureHandler);
        };
    }

    @SneakyThrows
    private UsernamePasswordAuthenticationFilter usernamePasswordAuthenticationFilter() {
        var filter = new UsernamePasswordAuthenticationFilter();
        filter.setAuthenticationManager(authenticationConfiguration.getAuthenticationManager());
        filter.setAuthenticationSuccessHandler(usernamePasswordAuthenticationSuccessHandler);
        filter.setAuthenticationFailureHandler(usernamePasswordAuthenticationFailureHandler);
        var requestMatcher = new AntPathRequestMatcher("/auth/login",
                HttpMethod.POST.name());
        filter.setFilterProcessesUrl("/auth/login");
        filter.setRequiresAuthenticationRequestMatcher(requestMatcher);
        return filter;
    }

    // @Bean
    // public WebSecurityCustomizer webSecurityCustomizer() {
    //     return (web) -> web.ignoring().requestMatchers(this.BYPASS_URLS);
    // }
}

