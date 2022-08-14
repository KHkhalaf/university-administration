package com.universityadministration.controller;

import com.energymanagementsystem.ems.dto.datasetDto;
import com.energymanagementsystem.ems.exceptions.ResourceNotFoundException;
import com.energymanagementsystem.ems.helper.UserService;
import com.energymanagementsystem.ems.model.dataset;
import com.energymanagementsystem.ems.repository.DatasetRepository;
import com.energymanagementsystem.ems.model.User;
import com.energymanagementsystem.ems.service.StatisticsService;
import com.energymanagementsystem.ems.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private StatisticsService statisticsService;
    @Autowired
    private DatasetRepository datasetRepository;

    @RequestMapping("/dashboard/{id}")
    public ModelAndView dashboard(@PathVariable(name = "id") int id , @AuthenticationPrincipal UserDetails currentUser ) throws ResourceNotFoundException {
        ModelAndView modelAndView = new ModelAndView("UserViews/dashboard");
        List<datasetDto> datasets = new ArrayList<>();
        User user = userService.getByEmail(currentUser.getUsername());

        if(id == -1) id = user.getId();

        List<dataset> statistics = statisticsService.getStatisticsByUserId(id);
        if(statistics.size() > 10)
            statistics = statistics.subList(0, 10);

        statisticsService.migrateaToStatisticsTable(statistics);
        if(statistics.size() > 0){
            statistics.forEach(s->{datasets.add(new datasetDto(s, user.getUsername()));});
            modelAndView.addObject("datasets", datasets);
          }
        else{
            modelAndView.setViewName("Shared/error");
            modelAndView.addObject("message","UNFORTUNATELY .. this user doesn't have statistics !!");
        }
        return modelAndView;
    }
    @RequestMapping("/dashboardJSON/{id}/{offset}")
    public ResponseEntity<List<datasetDto>> dashboardJSON(@PathVariable(name = "id") int id,@PathVariable(name = "offset") int offset) throws ResourceNotFoundException {

        List<datasetDto> datasets = new ArrayList<>();
        String username = userService.get(id).getUsername();
        List<dataset> statistics = statisticsService.getStatisticsByUserId(id);
        if(statistics.size() > 10)
            statistics = statistics.subList(offset, offset + 10);
        statisticsService.migrateaToStatisticsTable(statistics);
        statistics.forEach(s->{datasets.add(new datasetDto(s, username));});

        return ResponseEntity.ok().body(datasets);
    }
    @RequestMapping("/details/{id}")
    public ModelAndView profileUser(@PathVariable(name = "id") int id, @AuthenticationPrincipal UserDetails currentUser) throws ResourceNotFoundException {
        ModelAndView modelAndView = new ModelAndView("UserViews/profile");
        User user = userService.getByEmail(currentUser.getUsername());
        if(id == -1)
            modelAndView.addObject("user", user);
        else
            modelAndView.addObject(userService.get(id));
        return modelAndView;
    }

    @RequestMapping("/list")
    public String showListOfUsers(Model model, @AuthenticationPrincipal UserDetails currentUser) {
        User user = userService.getByEmail(currentUser.getUsername());
        List<User> users = userService.listAll();
        List<User> _users = new ArrayList<>();
        if(user.getCompany() != null){
            for (User u:users )
                if(u.getCompany() != null)
                    if(u.getCompany().getId() == user.getCompany().getId() && u.getId() != user.getId())
                        _users.add(u);
        }
        else
            _users = users;

        model.addAttribute("users", _users);

        return "UserViews/List";
    }

    @RequestMapping("/edit/{id}")
    public ModelAndView showEditUserPage(@PathVariable(name = "id") int id) throws ResourceNotFoundException {
        ModelAndView mav = new ModelAndView("UserViews/edit");
        User user = userService.get(id);
        mav.addObject("user", user);
        return mav;
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public String saveUser(@ModelAttribute("user") User user) throws ResourceNotFoundException {
        User _user = userService.get(user.getId());
        _user.setUsername(_user.getUsername());

        userService.save(_user);

        return "redirect:/user/list";
    }

    @RequestMapping("/delete/{id}")
    public String deleteUser(@PathVariable(name = "id") int id) {
        userService.delete(id);
        return "redirect:/user/list";
    }

}
