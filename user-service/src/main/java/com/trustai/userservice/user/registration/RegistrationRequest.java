package com.trustai.userservice.user.registration;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class RegistrationRequest {
    @NotBlank(message = "username is required")
//    @Pattern(regexp = "^[a-z0-9_]{3,20}$", message = "Username can contain only lowercase letters, numbers, and underscores") // Username must be 3–20 chars
    private String username;

//    @NotBlank(message = "password is required")
//    @Size(min = 5, message = "password must be at least 5 characters long")
//    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "password must be alphanumeric (letters and digits only)")
    @NotBlank
    private String password;

//    @NotBlank(message = "firstname is required")
//    @Size(min = 3, max = 15, message = "firstname must be at least 3 characters long")
    private String firstname; // required

//    @NotBlank(message = "lastname is required")
//    @Size(min = 3, max = 15, message = "lastname must be at least 3 characters long")
    private String lastname;

//    @Email(message = "Invalid email format")
//    @Pattern(regexp = "^$|^.+$", message = "Email must not be only whitespace")
//    @Column(unique = true)
    @NotBlank
    private String email; // required|string|email|unique

//    @Pattern(regexp = "^$|^[0-9]{10}$", message = "mobile number must be exactly 10 digits") // Mobile number must be exactly 10 digits if provided
    private String mobile;

    @NotBlank(message = "referralCode is mandatory")
    private String referralCode;
}
