package com.asterisk.backend.adapter.rest.user;

import com.asterisk.backend.adapter.rest.authentication.model.PasswordChangeRequestDto;
import com.asterisk.backend.adapter.rest.user.model.UserChangeRequestDto;
import com.asterisk.backend.adapter.rest.user.model.UserResponseDto;
import com.asterisk.backend.domain.User;
import com.asterisk.backend.mapper.UserMapper;
import com.asterisk.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @Autowired
    public UserController(final UserService userService, final UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    /**
     * Reads and returns a user resource
     * Admins and User himself can read information
     *
     * @param userId corresponding user id
     * @return OK with the user as body or 404
     */
    @GetMapping(value = "/{userId}")
    @PreAuthorize("@userSecurity.isAccountOwner(authentication, #userId) or hasRole(T(com.asterisk.backend" +
            ".infrastructure.UserRole).ADMIN.getRoleName())")
    public ResponseEntity<?> readUser(@PathVariable final UUID userId) {
        final Optional<User> result = this.userService.readUser(userId);

        if (result.isEmpty()) return ResponseEntity.notFound().build();

        final UserResponseDto userResponseDto = this.userMapper.toUserResponseDto(result.get());
        return ResponseEntity.ok(userResponseDto);
    }

    @PutMapping(value = "/{userId}")
    @PreAuthorize("@userSecurity.isAccountOwner(authentication, #userId) or hasRole(T(com.asterisk.backend" +
            ".infrastructure.UserRole).ADMIN.getRoleName())")
    public ResponseEntity<?> updateUser(@PathVariable final UUID userId,
                                        @Valid @RequestBody final UserChangeRequestDto userChangeRequestDto) {
        final boolean result = this.userService.updateUser(userId, userChangeRequestDto);
        if (!result) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/{userId}/change-password")
    @PreAuthorize("@userSecurity.isAccountOwner(authentication, #userId) or hasRole(T(com.asterisk.backend" +
            ".infrastructure.UserRole).ADMIN.getRoleName())")
    public ResponseEntity<?> changeUserPassword(@PathVariable final UUID userId,
                                                @Valid @RequestBody final PasswordChangeRequestDto passwordChangeRequestDto) {
        final boolean result = this.userService.changePassword(userId, passwordChangeRequestDto);
        if (!result) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }
}
