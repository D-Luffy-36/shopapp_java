//package com.demo.shopapp.components;
//
//import com.demo.shopapp.dtos.UserLoginDTO;
//import com.demo.shopapp.repositorys.TokenRepository;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Value;
//
//import java.util.HashMap;
//import java.util.Map;
//
//public class JwtTokenUtils {
//    @Value("${jwt.expiration}")
//    private int expiration; //save to an environment variable
//    @Value("${jwt.secretKey}")
//    private String secretKey;
//    private final TokenRepository tokenRepository;
//    private static final Logger logger = LoggerFactory.getLogger(JwtTokenUtils.class);
//
//    public JwtTokenUtils(TokenRepository tokenRepository) {
//        this.tokenRepository = tokenRepository;
//    }
//    public String generateToken(UserLoginDTO userLoginDTO) throws Exception{
//        Map<String, Object> claims = new HashMap<>();
////        this.generateSecretKey();
//        claims.put("email", userLoginDTO.getPhoneNumber());
//        claims.put("userId",user.getId());
//        try {
//            String token = Jwts.builder()
//                    .setClaims(claims)
//                    .setSubject(user.getEmail())
//                    .setExpiration(new Date(System.currentTimeMillis() + expiration * 1000L))
//                    .signWith(getSignInKey(), SignatureAlgorithm.HS256)
//                    .compact();
//            return token;
//        }catch (Exception e) {
//            throw new InvalidParameterException("Cannot create jwt token, error: "+ e.getMessage());
//        }
//    }
//}