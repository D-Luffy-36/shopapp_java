package com.demo.shopapp.domain.user.service.user;
import com.demo.shopapp.shared.components.JwtTokenUtils;
import com.demo.shopapp.domain.user.dto.UserDTO;
import com.demo.shopapp.domain.user.dto.UserLoginDTO;
import com.demo.shopapp.domain.user.dto.AdminUserUpdateRequest;
import com.demo.shopapp.domain.role.entity.Role;
import com.demo.shopapp.domain.user.entity.Token;
import com.demo.shopapp.domain.user.entity.User;
import com.demo.shopapp.shared.exceptions.DataNotFoundException;
import com.demo.shopapp.shared.exceptions.InvalidParamException;
import com.demo.shopapp.shared.exceptions.PermissionDeniedException;
import com.demo.shopapp.domain.role.repository.RoleRepository;
import com.demo.shopapp.domain.user.repository.UserRepository;
import com.demo.shopapp.domain.user.service.TokenService;
import com.demo.shopapp.utils.MessageKeys;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.Authentication;

import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService, UserDetailsService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtils jwtTokenUtils;
    private final TokenService tokenService;
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);


    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^(0[1-9][0-9]{8,9}|\\+84[1-9][0-9]{8})$");


    /**
     * Locates the user based on the username. In the actual implementation, the search
     * may possibly be case sensitive, or case insensitive depending on how the
     * implementation instance is configured. In this case, the <code>UserDetails</code>
     * object that comes back may have a username that is of a different case than what
     * was actually requested..
     *
     * @param identifier the username identifying the user whose data is required.
     * @return a fully populated user record (never <code>null</code>)
     * @throws UsernameNotFoundException if the user could not be found or the user has no
     *                                   GrantedAuthority
     */
    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        Optional<User> user = Optional.empty();

        // Kiểm tra nếu identifier là email
        if (identifier.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            // Tìm người dùng qua email
            user = userRepository.findByEmail(identifier);

        }
        // Kiểm tra nếu identifier là số điện thoại
        else if (identifier.matches("^[0-9]+$")) {
            // Tìm người dùng qua số điện thoại
            user = userRepository.findByPhoneNumber(identifier);
        }

        if (user.isEmpty()) {
            throw new UsernameNotFoundException("User not found with identifier: " + identifier);
        }

        return new org.springframework.security.core.userdetails.User(user.get().getEmail(), user.get().getPassword(), user.get().getAuthorities());
    }


    @Transactional
    @Override
    public User create(UserDTO userDTO, boolean isAdmin) throws Exception {

        // check phone đã tồn tại chưa
        boolean existPhonumber = this.userRepository.existsByPhoneNumber(userDTO.getPhoneNumber());
        if (existPhonumber) {
            throw new RuntimeException("existed phone number: " + userDTO.getPhoneNumber());
        }
        // check email tồn tại chưa
        boolean existEmail = this.userRepository.existsByEmail(userDTO.getEmail());
        if(existEmail){
            throw new RuntimeException("existed email: " + userDTO.getEmail());
        }

        Set<Role> roles = new HashSet<>();
        Role defaultRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new DataNotFoundException("Role USER not found"));

        Set<String> roleNames = userDTO.getRoleNames() != null ? userDTO.getRoleNames() : Set.of();

        if (isAdmin && !roleNames.isEmpty()) {
            List<Role> foundRoles = roleRepository.findByNameIn(roleNames);
            if (foundRoles.size() != roleNames.size()) {
                throw new DataNotFoundException("One or more roles not found");
            }
            roles.addAll(foundRoles);
        } else {
            roles.add(defaultRole);
        }


        User newUser = User.builder()
                .fullName(userDTO.getFullName())
                .email(userDTO.getEmail())
                .phoneNumber(userDTO.getPhoneNumber())
                .dateOfBirth(userDTO.getDateOfBirth())
                .address(userDTO.getAddress())
                .isActive(true)
                .roles(roles)
                .build();


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

        }
        return this.userRepository.save(newUser);
    }

    @Override
    public User getUserById(long id) throws RuntimeException {
        return this.userRepository.findById(id).orElseThrow(() -> new RuntimeException("user not found"));
    }

    @Override
    @Transactional
    public Token login(UserLoginDTO userLoginDTO, HttpServletRequest request) throws Exception {

        logger.info("Attempting login with email: {} or phone: {}", userLoginDTO.getEmail(), userLoginDTO.getPhoneNumber());

        // Lấy userAgent để lưu thông tin thiết bị
        String userAgent = request.getHeader("User-Agent");
        if (userAgent == null || userAgent.isEmpty()) {
            logger.warn("User-Agent header is missing");
            userAgent = "Unknown";
        }

        Optional<User> existingUser;
        String email = userLoginDTO.getEmail();
        String phone = userLoginDTO.getPhoneNumber();

        if (email != null && !email.isEmpty()) {
            if (!EMAIL_PATTERN.matcher(email).matches()) {
                logger.error("Invalid email format: {}", email);
                throw new InvalidParamException("Invalid email format");
            }
            existingUser = userRepository.findByEmail(email);
        } else if (phone != null && !phone.isEmpty()) {
            if (!PHONE_PATTERN.matcher(phone).matches()) {
                logger.error("Invalid phone format: {}", phone);
                throw new InvalidParamException("Invalid phone format");
            }
            existingUser = userRepository.findByPhoneNumber(phone);
        } else {
            logger.error("Both email and phone are null or empty");
            throw new InvalidParamException("Email or phone number is required");
        }


        // Kiểm tra user có tồn tại không
        User user = existingUser.orElseThrow(() -> {
            logger.error("User not found with email: {} or phone: {}", email, phone);
            return new DataNotFoundException("User not found");
        });

        // Kiểm tra tài khoản bị khóa
        if (!user.getIsActive()) {
            logger.warn("Login attempt for locked user: {}", user.getId());
            throw new AccessDeniedException(MessageKeys.USER_IS_LOCKED);
        }

        // Kiểm tra phương thức đăng nhập
        // 1. Đăng nhập bằng mật khẩu
        if (userLoginDTO.getPassword() != null && !userLoginDTO.getPassword().isEmpty()) {
            if (!passwordEncoder.matches(userLoginDTO.getPassword().trim(), user.getPassword())) {
                logger.warn("Invalid password for user: {}", user.getId());
                throw new InvalidParamException("Invalid password");
            }
        }

        // Vô hiệu hóa token cũ của thiết bị không phải mobile (nếu cần)
//        if (!isMobileDevice(userAgent)) {
//            tokenService.revokeTokensForNonMobileDevices(user);
//        }

        String token = jwtTokenUtils.generateToken(user);
        Token tokenEntity = tokenService.saveToken(user, token, userAgent);
        logger.info("Login successful for user with id: {}, token generated: {}", user.getId(), token);
        return tokenEntity;
    }



    private UserDetails getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            return (UserDetails) authentication.getPrincipal();
        }
        return null;
    }


    public User getUserDetailsFromToken(String token) throws Exception {

        logger.info("Extracting user details from token: {}", token);



        UserDetails userDetails = getAuthenticatedUser();
        if (userDetails == null || !jwtTokenUtils.validateToken(token, userDetails)) {
            throw new SecurityException("Invalid token or user not authenticated");
        }

        String identifier = jwtTokenUtils.extractIdentifier(token);
        User user;
        if (identifier != null) {
            if (jwtTokenUtils.extractEmail(token) != null) {
                user = userRepository.findByEmail(identifier)
                        .orElseThrow(() -> new DataNotFoundException("User not found with email"));
            } else if (jwtTokenUtils.extractPhoneNumber(token) != null) {
                user = userRepository.findByPhoneNumber(identifier)
                        .orElseThrow(() -> new DataNotFoundException("User not found with phone number"));
            } else {
                logger.error("Invalid identifier type in token: {}", identifier);
                throw new SecurityException("Token contains invalid identifier");
            }
        } else {
            logger.error("No identifier found in token");
            throw new SecurityException("No identifier found in token");
        }

        if (!user.getIsActive()) {
            logger.warn("Login attempt for locked user: {}", user.getId());
            throw new AccessDeniedException(MessageKeys.USER_IS_LOCKED);
        }

        logger.info("Successfully retrieved user details for userId: {}", user.getId());
        return user;
    }

    public User getUserDetailsForAdmin(String token, Long userId) throws Exception {
        User currentUser = getUserDetailsFromToken(token);

        // Kiểm tra nếu user hiện tại là ADMIN
        boolean isAdmin = currentUser.getRoles().stream()
                .anyMatch(role -> role.getName().equalsIgnoreCase("ADMIN"));

        if (!isAdmin) {
            throw new RuntimeException("You do not have permission to view this user.");
        }

        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

    }




//    public String getCurrentUserPhoneNumber() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//
//        if (authentication != null && authentication.getPrincipal() instanceof User) {
//            return ((User) authentication.getPrincipal()).getPhoneNumber();
//        }
//        return null;
//    }

    @Override
    public Page<User> searchUsers(String keyWord, int page, int limit) {
        return this.userRepository.findByKeyWord(keyWord, PageRequest.of(page, limit));
    }

    @Transactional
    public User updateUserProfile(String bearerToken, UserDTO userDTO) throws Exception {
        if (bearerToken == null || !bearerToken.startsWith("Bearer ") || bearerToken.length() < 8) {
            throw new Exception("Invalid Bearer Token");
        }

        // Cắt "Bearer " từ token
        String token = bearerToken.substring(7);

        // Trích xuất phoneNumber từ JWT
        String phoneNumber = jwtTokenUtils.extractPhoneNumber(token);

        Long userIdFromToken = jwtTokenUtils.extractUserId(token);  // Lấy userId từ token

        // nếu user khác có token của 1 user nào đó không cho update
        User user = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new DataNotFoundException("User not found"));

        // So sánh userId từ token với userId của request
        if (!user.getId().equals(userIdFromToken)) {
            throw new PermissionDeniedException("You are not allowed to update this user!");
        }

        user.setFullName(userDTO.getFullName());

        if(userDTO.getPhoneNumber() != null && !userDTO.getPhoneNumber().isEmpty() && !userDTO.getPhoneNumber().isBlank() ){
            // Nếu số điện thoại mới khác số hiện tại, kiểm tra xem đã tồn tại chưa
            if (!user.getPhoneNumber().equals(userDTO.getPhoneNumber())) {
                Optional<User> existingUser = userRepository.findByPhoneNumber(userDTO.getPhoneNumber());
                if (existingUser.isPresent()) {
                    throw new Exception("Phone number is already in use");
                }
            }
            user.setPhoneNumber(userDTO.getPhoneNumber());
        }

        user.setAddress(userDTO.getAddress());

        if (userDTO.getDateOfBirth() != null) {
            user.setDateOfBirth(userDTO.getDateOfBirth());
        }

        // Nếu user nhập mật khẩu mới, kiểm tra và cập nhật
        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
            if (!userDTO.getPassword().equals(userDTO.getRetypePassword())) {
                throw new InvalidParamException("Password does not match retypePassword");
            }
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        }

        userRepository.save(user);
        return user;
    }

    @Transactional
    public User updateUserByAdmin(Long userId, AdminUserUpdateRequest request) throws Exception {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User not found"));

        user.setFullName(request.getFullName());

        if(request.getPhoneNumber() != null && !request.getPhoneNumber().isEmpty() && !request.getPhoneNumber().isBlank() ){
            // Nếu số điện thoại mới khác số hiện tại, kiểm tra xem đã tồn tại chưa
            if (!user.getPhoneNumber().equals(request.getPhoneNumber())) {
                Optional<User> existingUser = userRepository.findByPhoneNumber(request.getPhoneNumber());
                if (existingUser.isPresent()) {
                    throw new Exception("Phone number is already in use");
                }
            }
            user.setPhoneNumber(request.getPhoneNumber());
        }
        user.setAddress(request.getAddress());

        if (request.getRoleNames() != null && !request.getRoleNames().isEmpty()) {
            Set<Role> roles = new HashSet<>();
            for (String roleName : request.getRoleNames()) {
                Role role = roleRepository.findByName(roleName)
                        .orElseThrow(() -> new DataNotFoundException("Role " + roleName + " not found"));
                roles.add(role);
            }
            user.setRoles(roles); // Cập nhật danh sách roles mới
        }


        // cập nhật ngày sinh nếu có nhập
        if (request.getDateOfBirth() != null) {
            user.setDateOfBirth(request.getDateOfBirth());
        }


        if (request.getIsActive() != null) {
            user.setIsActive(request.getIsActive());
        }

        // **Cho phép admin đặt lại mật khẩu của user**
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        userRepository.save(user);
        return user;
    }

}
