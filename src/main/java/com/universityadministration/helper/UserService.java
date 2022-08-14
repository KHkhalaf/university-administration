package com.universityadministration.helper;

import com.energymanagementsystem.ems.dto.LoginResponse;
import com.energymanagementsystem.ems.dto.UserSummary;
import com.energymanagementsystem.ems.exceptions.ResourceNotFoundException;
import com.energymanagementsystem.ems.model.User;
import org.springframework.http.ResponseEntity;

import java.util.List;


public interface UserService {

    ResponseEntity<LoginResponse> login(String email, String accessToken, String refreshToken);

    ResponseEntity<LoginResponse> refresh(String accessToken, String refreshToken);

    UserSummary getUserProfile();

    public List<User> listAll();

    public void save(User user);

    public User getByEmail(String email);

    public User get(int id) throws ResourceNotFoundException;

    public void delete(int id);

}