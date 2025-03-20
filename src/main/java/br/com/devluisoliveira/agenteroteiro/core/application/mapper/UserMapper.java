package br.com.devluisoliveira.agenteroteiro.core.application.mapper;

import br.com.devluisoliveira.agenteroteiro.core.domain.entity.User;
import br.com.devluisoliveira.agenteroteiro.core.port.in.dto.UserRequestDto;
import br.com.devluisoliveira.agenteroteiro.core.port.out.response.dto.RoleResponseDTO;
import br.com.devluisoliveira.agenteroteiro.core.port.out.response.dto.UserResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
public class UserMapper {

    public User userRequestDtoToUser(UserRequestDto userRequestDto) {
        User user = User.builder()
                .username(userRequestDto.getUsername())
                .email(userRequestDto.getEmail())
                .active(true)
                .phoneNumber(userRequestDto.getPhoneNumber())
                .createdAt(LocalDateTime.now(ZoneId.of("UTC")))
                .roles(new HashSet<>())
                .build();
        return user;
    }

    public Page<UserResponseDto> userToUserResponseDtoPage(Page<User> users) {
        return users.map(user -> {
           Set<RoleResponseDTO> roles = user.getRoles().stream()
                    .map(role -> RoleResponseDTO.builder()
                            .roleName(String.valueOf(role.getRoleName()))
                            .build())
                    .collect(Collectors.toSet());

            return UserResponseDto.builder()
                    .userId(user.getUserId().toString())
                    .fullName(user.getFullName())
                    .email(user.getEmail())
                    .active(user.isActive())
                    .birthDate(user.getBirthDate())
                    .createdAt(user.getCreatedAt())
                    .updateAt(user.getUpdateAt())
                    .phoneNumber(user.getPhoneNumber())
                    .roles(roles)
                    .build();
        });
    }

    public UserResponseDto userToUserResponseDto(User user) {

        return UserResponseDto.builder()
                .userId(user.getUserId().toString())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .active(user.isActive())
                .birthDate(user.getBirthDate())
                .createdAt(user.getCreatedAt())
                .updateAt(user.getUpdateAt())
                .phoneNumber(user.getPhoneNumber())
                .build();
    }

}
