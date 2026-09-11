package it.uniroma3.siw.moviefestival_backend.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfigurationSource;

import javax.sql.DataSource;

import static it.uniroma3.siw.moviefestival_backend.model.Utente.ADMIN_ROLE;

@Configuration
public class SecurityConfiguration {

    private final DataSource dataSource;

    public SecurityConfiguration(DataSource dataSource){
        this.dataSource=dataSource;
    }

    @Bean
    public UserDetailsService userDetailsService(){
        JdbcUserDetailsManager manager = new JdbcUserDetailsManager(dataSource);
        manager.setUsersByUsernameQuery("SELECT username, password, 1 as enabled FROM utente WHERE username=?");
        manager.setAuthoritiesByUsernameQuery("SELECT username, role FROM utente WHERE username=?");
        return manager;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    protected SecurityFilterChain configure(final HttpSecurity httpSecurity) throws Exception{

        httpSecurity.authorizeHttpRequests(authorize->{
            authorize.requestMatchers(HttpMethod.GET,"/films/*/recensioni/nuova").authenticated();
            authorize.requestMatchers(HttpMethod.GET, "/",
                    "/festivals",
                    "/festivals/**",
                    "/films",
                    "/films/**",
                    "/registi",
                    "/registi/**",
                    "/register",
                    "/api/festivals/**",
                    "/css/**",
                    "/uploads/**",
                    "/images/**",
                    "/favicon.ico").permitAll();
            authorize.requestMatchers(HttpMethod.POST, "/register", "/login").permitAll();
            authorize.requestMatchers(HttpMethod.GET, "/admin/**").hasAnyAuthority(ADMIN_ROLE);
            authorize.requestMatchers(HttpMethod.POST, "/admin/**").hasAnyAuthority(ADMIN_ROLE);
            authorize.anyRequest().authenticated();
        });

        httpSecurity.formLogin(form ->{
           form.loginPage("/login").permitAll();
           form.defaultSuccessUrl("/",true);
           form.failureUrl("/login?error=true");
        });

        httpSecurity.logout(logout ->{
            logout.logoutUrl("/logout");
            logout.logoutSuccessUrl("/");
            logout.invalidateHttpSession(true);
            logout.deleteCookies("JSESSIONID");
            logout.clearAuthentication(true);
            logout.permitAll();
        });

        return httpSecurity.build();
    }
}
