## Java Method Level Security
Function level access control checks can be implemented in java Spring application using annotations. 
To enable function-level annotations for access control, use `@EnableMethodSecurity(securedEnabled = true)` in your security configuration.
This allows the annotations like `@PreAuthorize`, `@PostAuthorize`, `@PreFilter` and `@PostFilter` by default

### 1. @PreAuthorize: 
For example the following code uses the `@PreAuthorize` annotation to specify that only authenticated users are allowed 
to access the `updateProfile()` method

````java
@PreAuthorize("isAuthenticated()")
public void updateProfile() {
    // TODO...
}
````

The following code uses the @PreAuthorize annotations to specify that only users with the "ADMIN" role are allowed to access the `deleteUser()` method
````java
@PreAuthorize("hasRole('ADMIN')")
public void deleteUser(Long userId) {
    // TODO...
}
````

### 2. @Secured: 
@Secured annotation, provided by the SpringSecurity framework, allows the specification of the required roles or permissions for accessing a method.
For example, the `deleteUser()` method could be secured using a method security expression like this:
````java
@Secured("ROLE_ADMIN")
public void deleteUser(Long userId) {
    // TODO...
}
````
> This annotation does not support Spring Expression Language (SpEL), it uses a simple string-based syntax, so it is less flexible than "@PreAuthorize".
Whenever possible, it is recommended to use `@PreAuthorize` annotation


### 3. @RolesAllowed: 
`@RolesAllowed` annotation which is a standard Java annotation, can be used to specify the roles that are allowed to access a particular
method or class. However, this annotation must be used in conjunction with a security framework, such as Spring Security, in order to enforce the specified access control rules. For example:
````java
@RolesAllowed("ADMIN")
public void deleteUser(Long userId) {
    // TODO...
}
````


---


## Insecure Code Example

````java
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@PostMapping("/login")
public void login() {
    // Code to handle the request and perform user login
}

@PostMapping("/logout")
@PreAuthorize("isAuthenticated()")
public void logout() {
    // Code to handle the request and perform user logout
}


@GetMapping("/users")
@PreAuthorize("isAuthenticated()")
public void getListOfUsers() {
    // Only authorized admins can perform this action
    // Code to handle the request and return the list of users 
}
````
> This example is insecure because it will allow access to `/users` endpoint (to get the list of users) to any authenticated user. Only authorized user should have access to it


## Secure Code Example

````java
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@PostMapping("/login")
public void login() {
    // Code to handle the request and perform user login
}

@PostMapping("/logout")
@PreAuthorize("isAuthenticated()")
public void logout() {
    // Code to handle the request and perform user logout
}


@GetMapping("/users")
@PreAuthorize("hasRole('ADMIN')")
public void getListOfUsers() {
    // Only authorized admins can perform this action
    // Code to handle the request and return the list of users 
}
````




















