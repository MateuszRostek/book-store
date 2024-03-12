package book.store.service.user.impl;

import book.store.dto.user.UserRegistrationRequestDto;
import book.store.dto.user.UserResponseDto;
import book.store.exception.RegistrationException;
import book.store.mapper.UserMapper;
import book.store.model.Role;
import book.store.model.User;
import book.store.repository.role.RoleRepository;
import book.store.repository.user.UserRepository;
import book.store.service.user.UserService;
import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private static final Role.RoleName DEFAULT_ROLE_NAME = Role.RoleName.USER;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponseDto register(UserRegistrationRequestDto requestDto)
            throws RegistrationException {
        if (userRepository.findByEmail(requestDto.email()).isPresent()) {
            throw new RegistrationException(
                    "Can't register user - user with this email already exists!");
        }
        User modelUser = userMapper.toModel(requestDto);
        modelUser.setPassword(passwordEncoder.encode(requestDto.password()));
        Set<Role> roles = new HashSet<>();
        Role defaultRole = roleRepository.findByName(DEFAULT_ROLE_NAME)
                .orElseThrow(() -> new RegistrationException("Can't find default role"));
        roles.add(defaultRole);
        modelUser.setRoles(roles);
        return userMapper.toDto(userRepository.save(modelUser));
    }
}
