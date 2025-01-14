package com.demo.shopapp.services.user;


import com.demo.shopapp.dtos.UserDTO;
import com.demo.shopapp.dtos.UserLoginDTO;
import com.demo.shopapp.entities.Role;
import com.demo.shopapp.entities.User;
import com.demo.shopapp.exceptions.DataNotFoundException;
import com.demo.shopapp.repositorys.RoleRepository;
import com.demo.shopapp.repositorys.UserRepository;
import org.springframework.stereotype.Service;


import java.util.Optional;

@Service
public class UserService implements IUserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;


    public UserService(UserRepository userRepository
                       , RoleRepository roleRepository
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public User create(UserDTO userDTO) throws RuntimeException {
        boolean existPhonumber = this.userRepository.existsByPhoneNumber(userDTO.getPhoneNumber());
        if (existPhonumber) {
            throw new RuntimeException("existed phone number");
        }
        Optional<Role> role = this.roleRepository.findRoleById(userDTO.getRoleId());
        if(role.isEmpty()){
            throw new RuntimeException("role not found");
        }

        // tao đăng nhập mạng xã hội

        User newUser = User.builder()
                .fullName(userDTO.getFullName())
                .phoneNumber(userDTO.getPhoneNumber())
                .dateOfBirth(userDTO.getDateOfBirth())
                .address(userDTO.getAddress())
                .googleAccountId(userDTO.getGoogleAccountId())
                .faceBookAccountId(userDTO.getFacebookAccountId())
                .isActive(true)
                .role(role.orElseThrow(() -> new DataNotFoundException("Role not found")))
                .build();

        if(userDTO.getFacebookAccountId() == 0 && userDTO.getGoogleAccountId() == 0){
//            String password = userDTO.getPassWord();
//            newUser.setPassword(passwordEncoder.encode(password));
        }

        return this.userRepository.save(newUser);

    }

    @Override
    public User getUserById(long id) throws RuntimeException {
        return this.userRepository.findById(id).orElseThrow(() -> new RuntimeException("user not found"));
    }

    @Override
    public String Login(String phone, String password) throws RuntimeException {
        return null;
    }

    public String Login(UserLoginDTO userLoginDTO) throws RuntimeException {
        return null;
    }
}
