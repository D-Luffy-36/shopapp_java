package com.demo.shopapp.controllers;
import com.demo.shopapp.components.JwtTokenUtils;
import com.demo.shopapp.components.LocalizationUtils;

import com.demo.shopapp.dtos.responses.user.LoginResponse;
import com.demo.shopapp.entities.Role;
import com.demo.shopapp.entities.SocialAccount;
import com.demo.shopapp.entities.Token;
import com.demo.shopapp.entities.User;
import com.demo.shopapp.exceptions.DataNotFoundException;

import com.demo.shopapp.repositorys.RoleRepository;
import com.demo.shopapp.repositorys.SocialAccountRepository;
import com.demo.shopapp.repositorys.UserRepository;
import com.demo.shopapp.services.TokenService;
import com.demo.shopapp.utils.MessageKeys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.*;


@RestController
@RequestMapping("${api.prefix}/users")
@RequiredArgsConstructor
public class OAuth2Controller {

    private final TokenService tokenService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final SocialAccountRepository socialAccountRepository;
    private final JwtTokenUtils jwtTokenUtils;
    private final LocalizationUtils localizationUtils;

    private final RestTemplate restTemplate;
    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String redirectUrl;

    @Value("${spring.security.oauth2.client.provider.google.authorization-uri}")
    private String authorizationUrl;

    @Value("${spring.security.oauth2.client.provider.google.token-uri}")
    private String tokenUrl;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;

    @Value("${spring.security.oauth2.client.provider.google.user-info-uri}")
    private String userInfoUrl;


    // Trường hợp 2  1: User đã đăng nhập bằng Google trước đó

    // Trường hợp 2: User trước đó đăng ký bằng email, giờ login bằng Google

    // Trường hợp 3: User chưa có tài khoản
    private User createUserIfNotExist(String email, String avatar) {
        // Mặc định gán ROLE_USER
        Role defaultRole = roleRepository.findByName(Role.USER)
                .orElseThrow(() -> new DataNotFoundException("Role not found"));

        User user = User.builder()
                .email(email)
                .phoneNumber("")
                .fullName("") // cho tự cập nhật
                .roles(Set.of(defaultRole))
                .password(UUID.randomUUID().toString()) // Generate random password thay vì để ""
                .avatarUrl(avatar)
                .isActive(true)
                .build();
        return userRepository.save(user);
    }



    @GetMapping("/login/social-login")
    ResponseEntity<?> getGoogleLoginUrl (
                @RequestParam("login_type") String type
    ){
        try{
            if(type != null && type.trim().equalsIgnoreCase("google")){
                // server ở đây sẽ là client dựa vào googleClientId
                // và redirectUrl để tạo ra url để angular chuyển hướng tới cho google
                // để xác thực người dùng, spring boot đã đăng kí credient với google
                String authUrl = authorizationUrl +
                        "?client_id=" + googleClientId +
                        "&redirect_uri=" + redirectUrl +
                        "&response_type=code" +
                        "&scope=email profile openid";

                return ResponseEntity.ok(Collections.singletonMap("authUrl", authUrl));
            }else{
                throw new DataNotFoundException("Login type not found");
            }
        }catch(DataNotFoundException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }


    @GetMapping("/login/social/callback")
    public ResponseEntity<?> handleGoogleCallback(
            @RequestParam("code") String code,
            @RequestHeader("User-Agent") String userAgent
    ) {
        try{
            // Đổi mã code lấy Access Token
            String accessToken = exchangeCodeForToken(code);

            //  Lấy thông tin User từ Access Token
            Map<String, Object> userInfo = getUserInfo(accessToken);
            String email = (String) userInfo.get("email");
            String name = (String) userInfo.get("name");
            // Nếu name bị null, thay thế bằng email hoặc chuỗi mặc định

            String googleId = (String) userInfo.get("sub");
            String avatar = (String) userInfo.get("picture");


            //  Kiểm tra user trong database
            Optional<User> userOptional = userRepository.findByEmail(email);
            User user;
            // chưa ton tai user trong he thống
            if(userOptional.isEmpty()){
                 user = createUserIfNotExist(email, avatar);
            } else {
                // lấy user tồn tại
                user = userOptional.get();
                // tài khoản bị khóa
                if(!user.getIsActive()){
                    throw new AccessDeniedException(localizationUtils.getLocalizationMessage(MessageKeys.USER_IS_LOCKED));
                }
            }

            // tạo token cho user
            String newToken = jwtTokenUtils.generateToken(user);
            Token tokenEntity = this.tokenService.saveToken(user, newToken, userAgent);

            // check mạng xã hội đã tồn tại chưa
            Optional<SocialAccount> socialAccountOptional = socialAccountRepository.findByProviderAndProviderId("GOOGLE", googleId);
            if(socialAccountOptional.isEmpty()){
                SocialAccount socialAccount = SocialAccount.builder()
                        .provider("GOOGLE")
                        .providerId(googleId)
                        .name(name == null ? "" : name)
                        .user(user)
                        .build();
                socialAccountRepository.save(socialAccount);
            }
            return ResponseEntity.ok(
                    LoginResponse.builder()
                            .token(newToken)
                            .tokenType("Bearer")
                            .refreshToken(tokenEntity.getRefreshToken())
                            .build()
            );
        }catch(Exception e){
            System.out.println("error" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Something went wrong!"));
        }
    }


    // nhận code và gửi request đổi thông tin lấy access token
    private String exchangeCodeForToken(String code) {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("code", code);
        body.add("client_id", googleClientId);
        body.add("client_secret", clientSecret);
        body.add("redirect_uri", redirectUrl);
        body.add("grant_type", "authorization_code");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(this.tokenUrl, request, Map.class);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            return (String) response.getBody().get("access_token");
        }
        throw new RuntimeException("Failed to exchange code for token");
    }

    // lấy thông tin từ access token
    private Map<String, Object> getUserInfo(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<Map> response = new RestTemplate().exchange(this.userInfoUrl, HttpMethod.GET, entity, Map.class);

        return response.getBody();
    }



}
