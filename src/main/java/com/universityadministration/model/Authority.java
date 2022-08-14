    package com.universityadministration.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Set;


@Entity
@Table(name = "authority")
public class Authority implements Serializable {
    public Authority(String authorityName) {
        this.authorityName = authorityName;
    }

    public Authority() {
    }

    @Id
    @Column(name="authority_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int Id;

    @Column(length = 100, unique = true)
    @NotNull
    private String authorityName;

    public int getAuthorityId() {
        return Id;
    }

    public void setAuthorityId(int Id) {
        this.Id = Id;
    }

    public String getAuthorityName() {
        return authorityName;
    }

    public void setAuthorityName(String authorityName) {
        this.authorityName = authorityName;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    @JsonIgnore
    @ToString.Exclude
    @ManyToMany(mappedBy = "authorities", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<User> users;

    public GrantedAuthority grantedAuthority() {
        return new SimpleGrantedAuthority(this.authorityName);
    }
}