package com.asterisk.backend.store.user;

import com.asterisk.backend.domain.User;
import com.asterisk.backend.infrastructure.exception.UserNotFoundException;
import com.asterisk.backend.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Component;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;
import java.util.UUID;

import static com.asterisk.backend.application.config.CacheConfig.AUTHENTICATION_CACHE;
import static com.asterisk.backend.application.config.CacheConfig.USER_CACHE;

@Component
@Caching
public class UserStore implements UserManager {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Autowired
    public UserStore(final UserRepository userRepository, final UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public User findUserByEmail(final String email) throws UserNotFoundException{
        final Optional<UserEntity> userEntity = this.userRepository.findByEmail(email);

        if (userEntity.isEmpty()) throw new UserNotFoundException();

        return this.userMapper.toUser(userEntity.get());
    }

    @Override
//    @CachePut(value = USER_CACHE, key = "#user.id", condition = "#user.id != null")
    public User save(final User user) {
        UserEntity userEntity = this.userMapper.toUserEntity(user);
        userEntity = this.userRepository.save(userEntity);

        return this.userMapper.toUser(userEntity);
    }

    @Override
    @Cacheable(value = USER_CACHE, key = "#userId")
    public User findUserById(final UUID userId) throws UserNotFoundException {
        final Optional<UserEntity> userEntity = this.userRepository.findById(userId);

        if (userEntity.isEmpty()) throw new UserNotFoundException();

        return this.userMapper.toUser(userEntity.get());
    }
}
