package com.asterisk.backend.mapper;

import com.asterisk.backend.adapter.rest.user.model.UserResponseDto;
import com.asterisk.backend.domain.User;
import com.asterisk.backend.store.user.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toUser(final UserEntity userEntity) {
        final User user = new User();
        user.setId(userEntity.getId());
        user.setFirstName(userEntity.getFirstName());
        user.setLastName(userEntity.getLastName());
        user.setUsername(userEntity.getUsername());
        user.setEmail(userEntity.getEmail());
        user.setPassword(userEntity.getPassword());
        user.setRole(userEntity.getRole());
        user.setEnabled(userEntity.isEnabled());
        user.setCreatedAt(userEntity.getCreatedAt());
        user.setUpdatedAt(userEntity.getUpdatedAt());

        return user;
    }

    public UserEntity toUserEntity(final User user) {
        final UserEntity userEntity = new UserEntity();
        userEntity.setId(user.getId());
        userEntity.setFirstName(user.getFirstName());
        userEntity.setLastName(user.getLastName());
        userEntity.setUsername(user.getUsername());
        userEntity.setEmail(user.getEmail());
        userEntity.setPassword(user.getPassword());
        userEntity.setRole(user.getRole());
        userEntity.setEnabled(user.isEnabled());
        userEntity.setCreatedAt(user.getCreatedAt());
        userEntity.setUpdatedAt(user.getUpdatedAt());

        return userEntity;
    }

    public UserResponseDto toUserResponseDto(final User user) {
        return new UserResponseDto(user.getId(), user.getFirstName(), user.getLastName(), user.getUsername(),
                user.getEmail(), user.getCreatedAt(), user.getUpdatedAt());
    }
}
