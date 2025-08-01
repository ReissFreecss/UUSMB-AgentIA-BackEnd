package ibt.unam.mx.email;

import ibt.unam.mx.user.model.UserDTO;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class EmailDTO {

    @NotBlank(groups = {UserDTO.Register.class, UserDTO.Modify.class}, message = "Email cannot be empty.")
    @Email(message = "Email must be valid.")
    @Size(max = 100, message = "Email cannot exceed 100 characters.")
    private String to;

    @NotBlank(groups = {UserDTO.Modify.class, UserDTO.ChangeStatus.class}, message = "Subject cannot be empty")
    @Size(max = 100, message = "Subject cannot exceed 100 characters")
    private String subject;

    @NotBlank(groups = {UserDTO.Modify.class, UserDTO.ChangeStatus.class}, message = "Body cannot be empty")
    @Size(max = 5000, message = "Subject cannot exceed 5000 characters")
    private String body;

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
