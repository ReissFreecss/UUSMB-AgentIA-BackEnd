package ibt.unam.mx.user.model;

import ibt.unam.mx.resource.Role;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

public class UserDTO {

    @NotNull(groups = {Modify.class, ChangeStatus.class}, message = "ID cannot be null")
    private Long id;

    @NotBlank(groups = {Modify.class, ChangeStatus.class}, message = "Name cannot be empty")
    @Size(max = 100, message = "Name cannot exceed 100 characters")
    private String fullName;

    @NotBlank(groups = {Modify.class, ChangeStatus.class}, message = "Name cannot be empty")
    @Size(max = 100, message = "Last Name cannot exceed 100 characters")
    private String firstLastName;

    @NotBlank(groups = {Modify.class, ChangeStatus.class}, message = "Name cannot be empty")
    @Size(max = 100, message = "Last Name cannot exceed 100 characters")
    private String secondLastName;

    @NotBlank(groups = {Register.class}, message = "Password cannot be empty.")
    @Size(min = 8, max = 255, message = "Password must be between 8 and 255 characters.")
    private String password;

    @NotBlank(groups = {Register.class, Modify.class}, message = "Email cannot be empty.")
    @Email(message = "Email must be valid.")
    @Size(max = 100, message = "Email cannot exceed 100 characters.")
    private String email;

    @NotBlank(groups = {Register.class}, message = "Password cannot be empty.")
    @Size(max = 12, message = "Phone cannot exceed 12 numbers.")
    private String phone;

    private Role role;

    private String recoveryCode;
    private LocalDateTime codeExpiration;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getFirstLastName() {
        return firstLastName;
    }

    public void setFirstLastName(String firstLastName) {
        this.firstLastName = firstLastName;
    }

    public String getSecondLastName() {
        return secondLastName;
    }

    public void setSecondLastName(String secondLastName) {
        this.secondLastName = secondLastName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getRecoveryCode() {
        return recoveryCode;
    }

    public void setRecoveryCode(String recoveryCode) {
        this.recoveryCode = recoveryCode;
    }

    public LocalDateTime getCodeExpiration() {
        return codeExpiration;
    }

    public void setCodeExpiration(LocalDateTime codeExpiration) {
        this.codeExpiration = codeExpiration;
    }

    public interface Register {}
    public interface Modify {}
    public interface ChangeStatus {}
}
