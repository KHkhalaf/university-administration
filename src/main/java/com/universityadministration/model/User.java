package com.universityadministration.model;

import java.io.Serializable;
import java.util.*;

import com.energymanagementsystem.ems.dto.UserSummary;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import javax.validation.constraints.*;

@Entity
@Table(name = "users")
@Setter
@Getter
public class User implements Serializable {

    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String username;
    private String password;
    private boolean enabled;

    @Column(length = 50, name = "is_verified")
    private Boolean isVerified;
    public Boolean getIsVerified() {
        return isVerified;
    }
    public void setIsVerified(Boolean isVerified) {
        this.isVerified = isVerified;
    }

    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Column(length = 100, unique = true)
    @NotNull
    @Email
    private String email;

    @ManyToOne
    @ColumnDefault("-1")
    @JoinColumn(name="company_id")
    private Company company;

    @OneToMany(mappedBy="user")
    private Set<Statistics> statistics = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
            name = "user_authority",
            joinColumns = {@JoinColumn(name = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "authority_Id")})

    private Set<Authority> authorities;

    public Set<Authority> getAuthorities() {
        return authorities;
    }
    public void setAuthorities(Set<Authority> authorities) {
        this.authorities = authorities;
    }

    public UserSummary toUserSummary() {
        UserSummary userSummary = new UserSummary();
        userSummary.setEmail(this.email);
        userSummary.setUserId(this.id);
        return userSummary;
    }


}