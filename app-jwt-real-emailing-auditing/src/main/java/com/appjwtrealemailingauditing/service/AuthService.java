package com.appjwtrealemailingauditing.service;

import com.appjwtrealemailingauditing.entity.User;
import com.appjwtrealemailingauditing.entity.enums.RoleName;
import com.appjwtrealemailingauditing.payload.ApiResponse;
import com.appjwtrealemailingauditing.payload.LoginDto;
import com.appjwtrealemailingauditing.payload.RegisterDto;
import com.appjwtrealemailingauditing.repository.RoleRepository;
import com.appjwtrealemailingauditing.repository.UserRepository;
import com.appjwtrealemailingauditing.security.JwtProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService implements UserDetailsService {

    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    JavaMailSender javaMailSender;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    JwtProvider jwtProvider;

    public ApiResponse registerUser(RegisterDto registerDto){

        boolean existsByEmail = userRepository.existsByEmail(registerDto.getEmail());
        if(existsByEmail)
            return new ApiResponse("User saved before with this email", false);

        User user = new User();
        user.setFirstName(registerDto.getFirstName());
        user.setLastName(registerDto.getLastName());
        user.setEmail(registerDto.getEmail());
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        user.setRoles(Collections.singleton(roleRepository.findByRoleName(RoleName.ROLE_USER)));
        user.setEmailCode(UUID.randomUUID().toString());
        userRepository.save(user);

        sendEmail(user.getEmail(), user.getEmailCode());

        return new ApiResponse("Muvaffaqiyatli royxatdan otdingiz. Active bolish uchun emailni tasdiqlang", true);
    }

    private Boolean sendEmail(String sendingEmail,String emailCode){
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("");
            message.setTo(sendingEmail);
            message.setSubject("Tasdiqlash Codi");
            message.setText("<a href='http://localhost:8080/api/auth/verifyEmail?emailCode=" + emailCode + "&sendingEmail=" + sendingEmail + "'>Tasdiqlang</a>");
            javaMailSender.send(message);
            return true;
        }catch (Exception e) {
            return false;
        }
    }

    public ApiResponse verifyEmail(String emailCode, String sendingEmail) {
        Optional<User> optionalUser = userRepository.findByEmailAndEmailCode(sendingEmail, emailCode);
        if (optionalUser.isPresent()){
            User user = optionalUser.get();
            user.setEnabled(true);
            user.setEmail(null);
            userRepository.save(user);
            return new ApiResponse("Account tasdiqlandi", true);
        }
        return new ApiResponse("account allaqachon tasdiqlangan", false);
    }

    public ApiResponse login(LoginDto loginDto) {

        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword()));

            User user = (User)authentication.getPrincipal(); // Principalni ichida UserDetails boladi uni userga cast qilaamiz rolini olish uchun

            String token = jwtProvider.generateToken(loginDto.getUsername(), user.getRoles());

            return new ApiResponse("token",true,token);

        }catch (BadCredentialsException badCredentialsException){
            return new ApiResponse("PArol yoki login xato",false);
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

//        Optional<User> optionalUser = userRepository.findByEmail(username);
//        if (optionalUser.isPresent())
//            return optionalUser.get();
//        throw  new UsernameNotFoundException(username + "topilmadi");

        return userRepository.findByEmail(username).orElseThrow(()-> new UsernameNotFoundException(username + "topilmadi"));
    }
}
