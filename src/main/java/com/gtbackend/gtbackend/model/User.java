package com.gtbackend.gtbackend.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name="users")
public class User implements UserDetails {
    @NotBlank
    private String name;
    @Id
    private String email;
    @NotBlank
    private String password;
    @NotNull
    private LocalDate dob;
    @Transient
    private Integer age; //transient means it will not be a column in database, so we calculate it
    @Column(name="role")
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "signupRank", columnDefinition = "INT DEFAULT 0", nullable = false)
    private Integer signupRank = 0;
    @Column(name = "bestScore", columnDefinition = "INT DEFAULT 0", nullable = false)
    private Integer bestScore = 0;

    @Column(name = "unlimitedBestScore", columnDefinition = "INT DEFAULT 0", nullable = false)
    private Integer unlimitedBestScore = 0;

    public User() {
    }

    public User(String email, String password, String name, LocalDate dob, boolean isAdmin) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.dob = dob;
        this.role = isAdmin? Role.ADMIN : Role.USER;
    }

    public void setBestScore(Integer bestScore) {
        this.bestScore = bestScore;
    }

    public void setUnlimitedBestScore(Integer unlimitedBestScore) {
        this.unlimitedBestScore = unlimitedBestScore;
    }

    public Role getRole() {
        return role;
    }

    public Integer getSignupRank() {
        return signupRank;
    }

    public void setSignupRank(Integer signupRank) {
        this.signupRank = signupRank;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();

        if(role.equals(Role.ADMIN)){
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        }else{
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        }
//        authorities.add()
        return authorities;
    }
    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public String getEmail(){
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
