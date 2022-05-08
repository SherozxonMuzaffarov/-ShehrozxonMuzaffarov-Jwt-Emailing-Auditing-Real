package com.appjwtrealemailingauditing.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.security.Timestamp;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false,length = 50)
    private String firstName;

    @Column(nullable = false,length = 50)
    private String lastName;

    @Column(unique = true,nullable = false)
    private String email; //userni emaili(username sifatida ishlatiladi)

    @Column(nullable = false)
    private String password;

    @Column(unique = true,updatable = false) //update qilib bolmasin
    @CreationTimestamp
    private Timestamp createdAt; //qachon ro'yxatdan otganligi

    @UpdateTimestamp
    private Timestamp updatedAt;//oxirgi marta qachon edit qilingani

    @ManyToMany
    private Set<Role> roles;

    private boolean accountNonExpired=true;//userni amal qilish muddati otmaganligi

    private boolean accountNonLocked=true;//userni blocklanmaganligi

    private boolean credentialsNonExpired=true;//accountni ishonchliligini tekshiradi

    private boolean enabled=false;// active ligi

    private String emailCode;


                // UserDetailsning methodlari

    // userni huquqlari
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return this.accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }
}
