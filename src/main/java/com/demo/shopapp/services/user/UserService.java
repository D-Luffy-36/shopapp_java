package com.demo.shopapp.services.user;

import com.demo.shopapp.components.JwtTokenUtils;
import com.demo.shopapp.components.LocalizationUtils;
import com.demo.shopapp.dtos.UserDTO;
import com.demo.shopapp.dtos.UserLoginDTO;
import com.demo.shopapp.entities.Role;
import com.demo.shopapp.entities.User;
import com.demo.shopapp.exceptions.DataNotFoundException;
import com.demo.shopapp.exceptions.PermissionDeniedException;
import com.demo.shopapp.repositorys.RoleRepository;
import com.demo.shopapp.repositorys.UserRepository;
import com.demo.shopapp.utils.MessageKeys;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.Authentication;

import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtils jwtTokenUtils;
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final LocalizationUtils localizationUtils;

    @Transactional
    @Override
    public User create(UserDTO userDTO) throws Exception {
        boolean existPhonumber = this.userRepository.existsByPhoneNumber(userDTO.getPhoneNumber());
        if (existPhonumber) {
            throw new RuntimeException("existed phone number");
        }

        Role role = roleRepository.findRoleById(userDTO.getRoleId())
                .orElseThrow(   () -> new RuntimeException("no role found"));

        if (role.getName().equalsIgnoreCase(Role.ADMIN)) {
            throw new PermissionDeniedException("Registering admin accounts is not allowed");
        }


        // tao đăng nhập mạng xã hội
        User newUser = User.builder()
                .fullName(userDTO.getFullName())
                .phoneNumber(userDTO.getPhoneNumber())
                .dateOfBirth(userDTO.getDateOfBirth())
                .address(userDTO.getAddress())
                .isActive(true)
                .role(role)
                .build();

        // Nếu không có Facebook hoặc Google account (tức là cả hai là chuỗi rỗng), thì yêu cầu nhập mật khẩu
        if ((userDTO.getFacebookAccountId() == null || userDTO.getFacebookAccountId().isEmpty()) &&
                (userDTO.getGoogleAccountId() == null || userDTO.getGoogleAccountId().isEmpty())) {

            // Kiểm tra nếu password không null và không rỗng
            if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
                String password = userDTO.getPassword();

                // Mã hóa mật khẩu
                String encodedPassword = passwordEncoder.encode(password);
                newUser.setPassword(encodedPassword);

                // Kiểm tra nếu mã hóa không thành công (mặc dù thông thường sẽ không xảy ra)
                if (encodedPassword == null || encodedPassword.isEmpty()) {
                    throw new RuntimeException("Encoded password is null or empty");
                }
            } else {
               newUser.setGoogleAccountId(userDTO.getGoogleAccountId());
               newUser.setFaceBookAccountId(userDTO.getFacebookAccountId());
            }
        }
        return this.userRepository.save(newUser);
    }

    @Override
    public User getUserById(long id) throws RuntimeException {
        return this.userRepository.findById(id).orElseThrow(() -> new RuntimeException("user not found"));
    }

    @Override
    public String login(UserLoginDTO userLoginDTO) throws Exception {
        Optional<User> existingUser = this.userRepository
                .findUsersByPhoneNumber(userLoginDTO.getPhoneNumber());

        if(existingUser.isEmpty()){
            throw new DataNotFoundException("Incorrect phone number or password");
        }

        if(!existingUser.get().getIsActive()){
            throw new AccessDeniedException(localizationUtils.getLocalizationMessage(MessageKeys.USER_IS_LOCKED));
        }

        // nếu không đăng bằng bằng google or facebook
        if(passwordEncoder.matches(userLoginDTO.getPassword().trim(), existingUser.get().getPassword()) ||
            !userLoginDTO.isPasswordBlank() || userLoginDTO.isFacebookAccountIdValid() || userLoginDTO.isGoogleAccountIdValid()){
            // trả về JWT token
            String token = jwtTokenUtils.generateToken(existingUser.get());
            return token;
        }
        return null;

    }


    private UserDetails getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            return (UserDetails) authentication.getPrincipal();
        }
        return null;
    }


    public User getUserDetailsFromToken(String token) throws Exception {

        UserDetails userDetails = getAuthenticatedUser();
        if (userDetails == null || !jwtTokenUtils.validateToken(token, userDetails)) {
            throw new SecurityException("Token không hợp lệ hoặc user không được xác thực");
        }

        String phoneNumber = jwtTokenUtils.extractPhoneNumber(token);

        User user =  userRepository.findUsersByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new DataNotFoundException("User not found"));

        if(user.getIsActive()){
            throw new AccessDeniedException(localizationUtils.getLocalizationMessage(MessageKeys.USER_IS_LOCKED));
        }
        return user;
    }


}
