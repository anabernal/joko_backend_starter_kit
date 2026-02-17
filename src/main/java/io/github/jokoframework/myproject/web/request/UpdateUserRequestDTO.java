package io.github.jokoframework.myproject.web.request;

import javax.validation.constraints.Size;

/**
 * Request DTO for updating an existing user.
 *
 * @author jose
 */
public class UpdateUserRequestDTO {

    @Size(min = 1, max = 255)
    private String password;

    private String profile;

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