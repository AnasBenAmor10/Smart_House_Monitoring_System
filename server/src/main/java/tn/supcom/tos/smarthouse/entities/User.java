package tn.supcom.tos.smarthouse.entities;

import jakarta.nosql.Entity;
import jakarta.nosql.Id;
import jakarta.nosql.Column;
import java.io.Serializable;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import tn.supcom.tos.smarthouse.utils.Argon2Utils;
import tn.supcom.tos.smarthouse.enums.Role;

@Entity
public class User implements Serializable {
    @Id
    @Column("id")
    private String id;
    @Column("username")
    private String username;

    @Column("email")
    private String email;

    @Column("password")
    private String password;

    @Column("tenantId")
    private String tenantId;

    @Column("role")
    private Set<Role> roles;

    @Column("created_on")
    private String created_on;
    @Column("updatedAt")
    private String updatedAt;

    // Getters

    public String getId() {
        return id;
    }
    public String getUsername() {
        return username;
    }
    public String getEmail() {
        return email;
    }
    public String getPassword() {
        return password;
    }
    public String getTenantId() {
        return tenantId;
    }
    public Set<Role> getRoles() {
        return roles;
    }
    public String getCreated_on() {
        return created_on;
    }
    public String getUpdatedAt() { return updatedAt; }

//    Setters
    public void setUsername(String username) {
    this.username = username;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }
    public void setRoles(Set<Role> roles) {
        if (roles == null || roles.isEmpty()) {
            this.roles = Set.of(Role.USER);
        } else {
            this.roles = roles;
        }
    }
    public void setCreated_on(String created_on) {
        this.created_on = created_on;
    }
    public void setUpdatedAt(String updatedAt) {this.updatedAt = updatedAt;}

    public User() {
        this.id = UUID.randomUUID().toString();
        this.created_on = java.time.Instant.now().toString();
        this.tenantId = "CoT-CLIENTS";

    }
    public User (String id, String username, String email, String password,String tenantId, Set<Role> roles  ){
        this.id=id;
        this.email = email;
        this.username = username;
        this.tenantId = tenantId;
        this.password = password;
        this.created_on = java.time.Instant.now().toString();
        this.roles = roles != null && !roles.isEmpty() ? roles : Set.of(Role.USER);

    }
    @Override
    public boolean equals ( Object o){
        if (this == o) return true;
        if(o == null || getClass() !=getClass()) return false;
        User user = (User) o ;
        return Objects.equals(email,user.email) && Objects.equals(username, user.username);
    }

    @Override
    public String toString(){
        return "User{" +
                "email='" + email + '\'' +
                ",username'" + username + '\'' +
                ", tenantId='" + tenantId + '\'' +
                ", password='" + password + '\'' +
                ",created_on'" + created_on + '\'' +
                ", roles=" + roles +
                '}';
    }
//    @Override
//    public int hashCode(){
//        return Objects.hash(email,username,homeId,password,roles);
//    }
    public void hashPassword(String password, Argon2Utils argonUtility) {
        this.password = argonUtility.hash(password.toCharArray());
    }
}
