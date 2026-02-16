package io.github.jokoframework.myproject.web.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * Request DTO for creating a new user.
 *
 * @author jose
 */
public class CreateUserRequestDTO {

    @NotBlank(message = "Username is required")
    @Size(min = 1, max = 255)
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 1, max = 255)
    private String password;

    private String profile;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }
}