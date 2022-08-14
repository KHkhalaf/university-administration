package com.universityadministration.config;


import com.universityadministration.service.CustomUserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
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
@EnableGlobalMethodSecurity(
        securedEnabled = true,
        jsr250Enabled = true,
        prePostEnabled = true
)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private CustomUserDetailsServiceImpl customUserDetailsService;


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public TokenAuthenticationFilter tokenAuthenticationFilter() {
        return new TokenAuthenticationFilter();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(customUserDetailsService).passwordEncoder(passwordEncoder());
    }

    @Bean(BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http
                .authorizeRequests()
                .antMatchers("/auth/**").permitAll()
                .antMatchers("/css/**").permitAll()
                .antMatchers("/js/**").permitAll()
                .antMatchers("/company/home").hasAnyAuthority("USER","ADMIN","SUPERADMIN")
                .antMatchers("/company/list").hasAnyAuthority("SUPERADMIN")
                .antMatchers("/company/add").hasAnyAuthority("SUPERADMIN")
                .antMatchers("/company/save").hasAnyAuthority("SUPERADMIN")
                .antMatchers("/company/details/**").hasAnyAuthority("SUPERADMIN")
                .antMatchers("/company/delete/**").hasAnyAuthority("SUPERADMIN")
                .antMatchers("/user/dashboard").hasAnyAuthority("USER","ADMIN")
                .antMatchers("/user/dashboardJSON/**").hasAnyAuthority("USER","ADMIN","SUPERADMIN")
                .antMatchers("/user/details/**").hasAnyAuthority("USER","ADMIN","SUPERADMIN")
                .antMatchers("/user/list").hasAnyAuthority("ADMIN", "SUPERADMIN")
                .antMatchers("/user/edit/**").hasAnyAuthority("ADMIN","SUPERADMIN")
                .antMatchers("/user/save").hasAnyAuthority("ADMIN","SUPERADMIN")
                .antMatchers("/user/delete/**").hasAnyAuthority("ADMIN","SUPERADMIN")
                .anyRequest().authenticated()
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .csrf().disable()
                .formLogin()
                .loginPage("/auth/loginPage")
                .permitAll()
                .and().
                logout().logoutUrl("/auth/logout").logoutSuccessUrl("/auth/loginPage")
                .deleteCookies("accessToken", "refreshToken", "JSESSIONID").invalidateHttpSession(true);

        http.addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

    }
}