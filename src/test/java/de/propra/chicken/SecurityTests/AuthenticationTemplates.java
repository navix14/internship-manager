package de.propra.chicken.SecurityTests;

import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AuthenticationTemplates {
    public static MockHttpSession somebody() {
        return createSession("somebody", "SOMEBODY");
    }

    public static MockHttpSession studentSession() {
        return createSession("ilumary", "ROLE_STUDENT");
    }

    public static MockHttpSession tutorSession() {
        return createSession("ilumary", "ROLE_TUTOR", "ROLE_STUDENT");
    }

    public static MockHttpSession organisatorSession() {
        return createSession("ilumary", "ROLE_ORGANISATOR", "ROLE_TUTOR", "ROLE_STUDENT");
    }

    private static List<GrantedAuthority> buildAuthorities(Map<String, Object> attributes,
                                                           String[] roles) {
        List<GrantedAuthority> authorities =
                Arrays.stream(roles).map(r -> new OAuth2UserAuthority(r, attributes))
                        .collect(Collectors.toList());
        return authorities;
    }

    private static MockHttpSession createSession(String name, String... roles) {
        OAuth2AuthenticationToken principal = buildPrincipal(name, roles);
        //for (String role : roles) System.out.println(role);
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                new SecurityContextImpl(principal));
        return session;
    }

    private static OAuth2AuthenticationToken buildPrincipal(String name, String... roles) {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("sub", name);
        List<GrantedAuthority> authorities =
                buildAuthorities(attributes, roles);
        OAuth2User user = new DefaultOAuth2User(authorities, attributes, "sub");
        return new OAuth2AuthenticationToken(user, authorities, "whatever");
    }
}
