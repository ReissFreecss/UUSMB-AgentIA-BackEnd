package ibt.unam.mx.user.controller;

import ibt.unam.mx.user.model.ChangePasswordDTO;
import ibt.unam.mx.user.model.UserDTO;
import ibt.unam.mx.user.service.UserService;
import ibt.unam.mx.utils.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    //Ver todos los usuarios
    @GetMapping("/all")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<Message> getAllUsers() {
        return userService.findAll();
    }
    
    //Ver usuario by id
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'INTERNO', 'EXTERNO')")
    public ResponseEntity<Message> getUser(@PathVariable long id) {
        return userService.findById(id);
    }

    //Ver usuarios activos
    @GetMapping("/active")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<Message> getActiveUsers() {
        return userService.findActiveUsers();
    }

    //Ver usuarios inactivos
    @GetMapping("/inactive")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<Message> getInactiveUsers() {
        return userService.findInactiveUsers();
    }

    //Crear Usuario
    @PostMapping("/save")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Message> saveUser(@Validated(UserDTO.Register.class) @RequestBody UserDTO dto) {
        return userService.save(dto);
    }

    //Actualizar Usuario
    @PostMapping("/update")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Message> updateUser(@Validated(UserDTO.Modify.class) @RequestBody UserDTO dto) {
        return userService.update(dto);
    }

    //Cambiar contrasena
    @PutMapping("/change-password")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'INTERNO', 'EXTERNO')")
    public ResponseEntity<Message> changePassword(@Validated @RequestBody ChangePasswordDTO dto) {
        return userService.changePassword(dto);
    }

    @PostMapping("/change-password-user")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'INTERNO', 'EXTERNO')")
    public ResponseEntity<Message> changePasswordUser(@RequestBody ChangePasswordDTO dto) {
        return userService.changePasswordUser(dto);
    }

    @PutMapping("/change-status/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Message> changeStatus(@PathVariable Long id) {
        return userService.changeStatus(id);
    }

    @PutMapping("/change-rol/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<Message> changeRol(@PathVariable Long id) {
        return userService.changeRole(id);
    }
}
