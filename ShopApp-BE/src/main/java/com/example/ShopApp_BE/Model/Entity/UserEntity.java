package com.example.ShopApp_BE.Model.Entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserEntity extends AbstractEntity implements UserDetails {
    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name= "email")
    private String email;

    @Column(name= "address")
    private String address;

    @Column(name = "password")
    private String password;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(name = "facebook_account_id")
    private String facebookAccountId;

    @Column(name = "google_account_id")
    private String googleAccountId;

    @Column(name = "token_version")
    private Long tokenVersion;

    @Column(name = "is_active")
    private Boolean isActive;

    @OneToOne(mappedBy = "userEntity", cascade = CascadeType.ALL)
    private CartEntity cartEntity = new CartEntity();

    @OneToMany(mappedBy = "userEntity", cascade = CascadeType.ALL)
    private List<OrderEntity> orderEntities = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "role_id")
    private RoleEntity roleEntity = new RoleEntity();

    @OneToMany(mappedBy = "userEntity", cascade = CascadeType.ALL)
    private List<SocialAccountEntity> socialAccountEntities = new ArrayList<>();

    @OneToMany(mappedBy = "userEntity", cascade = CascadeType.ALL)
    private List<CommentEntity> commentEntities = new ArrayList<>();

    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL)
    private List<MessageEntity> messageEntities1 = new ArrayList<>();

    @OneToMany(mappedBy = "recipient", cascade = CascadeType.ALL)
    private List<MessageEntity> messageEntities2 = new ArrayList<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + getRoleEntity().getRole()));
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
        return isActive.equals(Boolean.TRUE);
    }

    @Override
    public boolean isAccountNonLocked() {
        return isActive.equals(Boolean.TRUE);
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return isActive.equals(Boolean.TRUE);
    }

    @Override
    public boolean isEnabled() {
        return isActive.equals(Boolean.TRUE);
    }

}
