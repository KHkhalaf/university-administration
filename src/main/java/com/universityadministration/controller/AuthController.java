package com.universityadministration.controller;


import com.energymanagementsystem.ems.dto.CompanyDto;
import com.energymanagementsystem.ems.dto.LoginRequest;
import com.energymanagementsystem.ems.dto.LoginResponse;
import com.energymanagementsystem.ems.dto.UserDto;
import com.energymanagementsystem.ems.helper.UserService;
import com.energymanagementsystem.ems.model.Company;
import com.energymanagementsystem.ems.model.ConfirmationToken;
import com.energymanagementsystem.ems.model.User;
import com.energymanagementsystem.ems.repository.AuthorityRepository;
import com.energymanagementsystem.ems.repository.CompanyRepository;
import com.energymanagementsystem.ems.repository.ConfirmationTokenRepository;
import com.energymanagementsystem.ems.service.EmailSenderService;
import com.energymanagementsystem.ems.service.InjectSqlCommand;
import com.energymanagementsystem.ems.util.SecurityCipher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserService userService;
    @Autowired
    private ConfirmationTokenRepository confirmationTokenRepository;
    @Autowired
    private AuthorityRepository authorityRepository;
    @Autowired
    private CompanyRepository companyRepository;

    @RequestMapping(value = "/register")
    public ModelAndView showRegisterPage(UserDto userDto) {
        ModelAndView mav = new ModelAndView("UserViews/AddUser");
        List<CompanyDto> companies = new ArrayList<>();
        companyRepository.findAll().forEach(c -> {
            CompanyDto companyDto = new CompanyDto(c.getId(),c.getName());
            companies.add(companyDto);
        });
        mav.addObject("userDto",userDto);
        mav.addObject("companies",companies);
        return mav;
    }

    @RequestMapping(value = "/registerUser", method = RequestMethod.POST)
    public ModelAndView Register(@ModelAttribute("userDto") UserDto userDto) throws IOException {
        ModelAndView modelAndView = new ModelAndView();
        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        user.setType(userDto.getType());
        Optional<Company> company = companyRepository.findById(userDto.getCompanyId());
        user.setCompany(company.get());
        user.setPassword(new BCryptPasswordEncoder().encode(userDto.getPassword()));

        User _user = userService.getByEmail(user.getEmail());
        if(_user != null)
        {
            modelAndView.addObject("message","This email already exists!");
            modelAndView.setViewName("Shared/error");
        }
        else
        {
            userService.save(user);
            long userId = userService.getByEmail(user.getEmail()).getId();
            InjectSqlCommand injectSqlCommand = new InjectSqlCommand();
            injectSqlCommand.addUserToAuthority(userId, user.getType());

            ConfirmationToken confirmationToken = new ConfirmationToken(user);

            EmailSenderService emailSenderService = new EmailSenderService();
            emailSenderService.sendEmail(confirmationToken.getConfirmationToken(), user.getEmail());

            confirmationTokenRepository.save(confirmationToken);

            modelAndView.addObject("emailId", user.getEmail());

            modelAndView.setViewName("Shared/successfulRegistration");
        }
        return modelAndView;
    }

    @RequestMapping(value="/confirm-account", method= {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView confirmUserAccount(ModelAndView modelAndView, @RequestParam("token")String confirmationToken)
    {
        ConfirmationToken token = confirmationTokenRepository.findByConfirmationToken(confirmationToken);

        if(token != null)
        {
            User user = userService.getByEmail(token.getUser().getEmail());
            user.setIsVerified(true);
            userService.save(user);
            modelAndView.setViewName("Shared/accountVerified");
        }
        else
        {
            modelAndView.addObject("message","The link is invalid or broken!");
            modelAndView.setViewName("Shared/error");
        }

        return modelAndView;
    }

    @RequestMapping(value = "/loginPage")
    public ModelAndView showLoginPage(LoginRequest loginRequest) {
        ModelAndView mav = new ModelAndView("UserViews/login");
        mav.addObject("loginRequest",loginRequest);
        return mav;
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity<LoginResponse> login(
            @CookieValue(name = "accessToken", required = false) String accessToken,
            @CookieValue(name = "refreshToken", required = false) String refreshToken,
            @RequestBody LoginRequest loginRequest
    ) throws IOException {

        if(!userService.getByEmail(loginRequest.getEmail()).getIsVerified())
            return null;
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String decryptedAccessToken = SecurityCipher.decrypt(accessToken);
        String decryptedRefreshToken = SecurityCipher.decrypt(refreshToken);

        return userService.login(loginRequest.getEmail(), decryptedAccessToken, decryptedRefreshToken);
    }

    @PostMapping(value = "/refresh", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LoginResponse> refreshToken(@CookieValue(name = "accessToken", required = false) String accessToken,
                                                      @CookieValue(name = "refreshToken", required = false) String refreshToken) {
        String decryptedAccessToken = SecurityCipher.decrypt(accessToken);
        String decryptedRefreshToken = SecurityCipher.decrypt(refreshToken);
        return userService.refresh(decryptedAccessToken, decryptedRefreshToken);
    }

    @RequestMapping(value = "/logout")
    public String logout( @RequestParam("token")String confirmationToken) throws ParseException {
        ConfirmationToken token = confirmationTokenRepository.findByConfirmationToken(confirmationToken);
        token.setCreatedDate(new SimpleDateFormat("yyyy-MM-dd : HH:mm:ss").parse("2020-08-05 17:06:05"));
        confirmationTokenRepository.save(token);

        return "redirect:/auth/loginPage";
    }
    @RequestMapping(value = "/accountNotVerified")
    public ModelAndView accountNotVerified(){

        ModelAndView mav = new ModelAndView("Shared/accountNotVerified");
        return mav;
    }
}