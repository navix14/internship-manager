package de.propra.chicken.infrastructure.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.HashSet;
import java.util.List;

@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {
    private final List<String> organisatoren;
    private final List<String> tutoren;

    public WebSecurityConfiguration(
            @Value("${chicken.rollen.organisator}") List<String> organisatoren,
            @Value("${chicken.rollen.tutor}") List<String> tutoren) {
        this.organisatoren = organisatoren;
        this.tutoren = tutoren;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        HttpSecurity security = http.authorizeRequests(a -> a
                .antMatchers("/css/*", "/js/*", "/error", "/stats").permitAll()
                .anyRequest().authenticated()
        );
        security
                .logout()
                .clearAuthentication(true)
                .deleteCookies()
                .invalidateHttpSession(true)
                .permitAll()
                .and()
                .oauth2Login().userInfoEndpoint().userService(createUserService());
    }

    @Bean
    OAuth2UserService<OAuth2UserRequest, OAuth2User> createUserService() {
        DefaultOAuth2UserService defaultService = new DefaultOAuth2UserService();
        return userRequest -> {
            OAuth2User oauth2User = defaultService.loadUser(userRequest);

            var attributes = oauth2User.getAttributes(); //keep existing attributes

            var authorities = new HashSet<GrantedAuthority>();
            authorities.add(new SimpleGrantedAuthority("ROLE_STUDENT"));

            String login = attributes.get("login").toString();
            Object id = attributes.get("id");
            System.out.println(id);
            System.out.printf("STUDENT LOGIN: %s%n", login);

            if (organisatoren.contains(login)) {
                System.out.printf("GRANTING ORGANISATOR AND TUTOR PRIVILEGES TO USER %s%n", login);
                authorities.add(new SimpleGrantedAuthority("ROLE_ORGANISATOR"));
                authorities.add(new SimpleGrantedAuthority("ROLE_TUTOR"));
            } else if (tutoren.contains(login)) {
                System.out.printf("GRANTING TUTOR PRIVILEGES TO USER %s%n", login);
                authorities.add(new SimpleGrantedAuthority("ROLE_TUTOR"));
            } else {
                System.out.printf("DENYING PRIVILEGES TO USER %s%n", login);
            }

            return new DefaultOAuth2User(authorities, attributes, "login");
        };
    }
}
