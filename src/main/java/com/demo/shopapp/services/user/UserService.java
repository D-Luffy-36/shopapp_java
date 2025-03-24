package com.demo.shopapp.services.user;

import com.demo.shopapp.components.JwtTokenUtils;
import com.demo.shopapp.components.LocalizationUtils;
import com.demo.shopapp.dtos.request.UserDTO;
import com.demo.shopapp.dtos.request.UserLoginDTO;
import com.demo.shopapp.dtos.request.AdminUserUpdateRequest;
import com.demo.shopapp.entities.Role;
import com.demo.shopapp.entities.Token;
import com.demo.shopapp.entities.User;
import com.demo.shopapp.exceptions.DataNotFoundException;
import com.demo.shopapp.exceptions.InvalidParamException;
import com.demo.shopapp.exceptions.PermissionDeniedException;
import com.demo.shopapp.repositorys.RoleRepository;
import com.demo.shopapp.repositorys.UserRepository;
import com.demo.shopapp.services.TokenService;
import com.demo.shopapp.utils.MessageKeys;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.Authentication;

import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtils jwtTokenUtils;
    private final TokenService tokenService;
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final LocalizationUtils localizationUtils;

    @Transactional
    @Override
    public User create(UserDTO userDTO, boolean isAdmin) throws Exception {
        boolean existPhonumber = this.userRepository.existsByPhoneNumber(userDTO.getPhoneNumber());
        if (existPhonumber) {
            throw new RuntimeException("existed phone number");
        }

        Set<Role> roles = new HashSet<>();
        if (isAdmin){
            for (String roleName : userDTO.getRoleNames()) {
                Role role = roleRepository.findByName(roleName)
                        .orElseThrow(() -> new RuntimeException("Role " + roleName + " not found"));
                roles.add(role);
            }
        }else {
            for (String roleName : userDTO.getRoleNames()) {
                Role role = roleRepository.findByName(roleName)
                        .orElseThrow(() -> new RuntimeException("Role " + roleName + " not found"));
                roles.add(role);
            }
        }


        // tao đăng nhập mạng xã hội
        User newUser = User.builder()
                .fullName(userDTO.getFullName())
                .phoneNumber(userDTO.getPhoneNumber())
                .dateOfBirth(userDTO.getDateOfBirth())
                .address(userDTO.getAddress())
                .isActive(true)
                .roles(roles)
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
    public Token login(UserLoginDTO userLoginDTO, HttpServletRequest request) throws Exception {

        // ưu tiên xóa token của các thiet bi khong phai cua mobile
        String userAgent = request.getHeader("User-Agent");
        System.out.println(userAgent);
        Optional<User> existingUser = this.userRepository
                .findUsersByPhoneNumber(userLoginDTO.getPhoneNumber());

        if(existingUser.isEmpty()){
            throw new DataNotFoundException("Incorrect phone number or password");
        }

        // tài khoản bị khóa
        if(!existingUser.get().getIsActive()){
            throw new AccessDeniedException(localizationUtils.getLocalizationMessage(MessageKeys.USER_IS_LOCKED));
        }

        // nếu không đăng bằng bằng google or facebook
        if(passwordEncoder.matches(userLoginDTO.getPassword().trim(), existingUser.get().getPassword()) ||
            !userLoginDTO.isPasswordBlank() || userLoginDTO.isFacebookAccountIdValid() || userLoginDTO.isGoogleAccountIdValid()){


            String token = jwtTokenUtils.generateToken(existingUser.get());
            // lưu token
            Token tokenEntity  =  this.tokenService.saveToken(existingUser.get(), token, userAgent);
            // trả về JWT token
            return tokenEntity;
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

        if(!user.getIsActive()){
            throw new AccessDeniedException(localizationUtils.getLocalizationMessage(MessageKeys.USER_IS_LOCKED));
        }
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
        User user = userRepository.findUsersByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new DataNotFoundException("User not found"));

        // So sánh userId từ token với userId của request
        if (!user.getId().equals(userIdFromToken)) {
            throw new PermissionDeniedException("You are not allowed to update this user!");
        }

        user.setFullName(userDTO.getFullName());

        if(userDTO.getPhoneNumber() != null && !userDTO.getPhoneNumber().isEmpty() && !userDTO.getPhoneNumber().isBlank() ){
            // Nếu số điện thoại mới khác số hiện tại, kiểm tra xem đã tồn tại chưa
            if (!user.getPhoneNumber().equals(userDTO.getPhoneNumber())) {
                Optional<User> existingUser = userRepository.findUsersByPhoneNumber(userDTO.getPhoneNumber());
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
                Optional<User> existingUser = userRepository.findUsersByPhoneNumber(request.getPhoneNumber());
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
