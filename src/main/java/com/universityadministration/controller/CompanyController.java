package com.universityadministration.controller;

import com.energymanagementsystem.ems.repository.CompanyRepository;
import com.energymanagementsystem.ems.model.Company;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/company")
public class CompanyController {
    @Autowired
    private CompanyRepository companyRepository;

    @RequestMapping("/home")
    public String index() {
        return "CompanyViews/home";
    }

    @RequestMapping("/list")
    public String showListOfCompanies(Model model) {
        List<Company> companies = new ArrayList<>();
        companyRepository.findAll().forEach(companies::add);
        model.addAttribute("companies", companies);

        return "CompanyViews/List";
    }

    @RequestMapping("/add")
    public ModelAndView showAddCompanyPage(Company company){
        ModelAndView mav = new ModelAndView("CompanyViews/AddCompany");
        mav.addObject("company",company);
        return mav;
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public String saveCompany(@ModelAttribute("company") Company company){

        companyRepository.save(company);

        return "redirect:/company/list";
    }
    @RequestMapping("/details/{id}")
    public ModelAndView detailsCompany(@PathVariable(name = "id") int id) {
        Optional<Company> company = companyRepository.findById(id);
        ModelAndView mav = new ModelAndView("CompanyViews/details");
        mav.addObject("company",company.get());
        return mav;
    }

    @RequestMapping("/delete/{id}")
    public String deleteCompany(@PathVariable(name = "id") int id) {
        Optional<Company> company = companyRepository.findById(id);
        companyRepository.delete(company.get());
        return "redirect:/company/list";
    }

}
