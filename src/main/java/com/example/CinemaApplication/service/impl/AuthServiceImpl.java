package com.example.CinemaApplication.service.impl;

import com.example.CinemaApplication.Security.JwtService;
import com.example.CinemaApplication.dto.request.LoginRequest;
import com.example.CinemaApplication.dto.request.RegisterRequest;
import com.example.CinemaApplication.dto.response.CurrentUserReponse;
import com.example.CinemaApplication.dto.response.TokenReponse;
import com.example.CinemaApplication.enums.AccountStatus;
import com.example.CinemaApplication.enums.Role;
import com.example.CinemaApplication.exception.BusinessException;
import com.example.CinemaApplication.model.Account;
import com.example.CinemaApplication.model.Customer;
import com.example.CinemaApplication.repository.AccountRepository;
import com.example.CinemaApplication.repository.CustomerRepository;
import com.example.CinemaApplication.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final AccountRepository accountRepository;
    private  final CustomerRepository customerRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder encoder;
    @Value("${jwt.expiration}")
    private long exprirationMillis;
    public TokenReponse login(LoginRequest loginRequest){
        if(loginRequest.username()==null || loginRequest.username().isBlank()||
             loginRequest.username().isEmpty()|| loginRequest.password().isBlank()||
                loginRequest.password().getBytes(StandardCharsets.UTF_8).length > 72){
            throw new BadCredentialsException("INVALID_CREDENTIALS");
        }
        Authentication authentication=authenticationManager.
                authenticate(UsernamePasswordAuthenticationToken.unauthenticated(loginRequest.username(),loginRequest.password()));
        var user=(UserDetails) authentication.getPrincipal();
        Account account=accountRepository.findByUsernameIgnoreCase(loginRequest.username()).
                orElseThrow(()-> new BadCredentialsException("INVALID_CREDENTIAL"));
        String token= jwtService.generateToken(user,account.getId(),account.getTokenVersion());
        return new TokenReponse(token,"Bearer",exprirationMillis/1000,toCustomer(account));
    }
    public void register(RegisterRequest registerRequest){
        if(accountRepository.existsByUsernameIgnoreCase(registerRequest.email())){
            throw new BusinessException("Email is existed!");
        }
        if(customerRepository.existsByIdentityCard(registerRequest.identityCard())){
            throw new BusinessException("Identity Card is existed!");
        }
        if(customerRepository.existsByPhone(registerRequest.mobile())){
            throw new BusinessException("Phone Number is existed!");
        }
        if(registerRequest.password().getBytes(StandardCharsets.UTF_8).length > 72){
            throw new BusinessException("Mật khẩu quá dài. Vui lòng chọn mật khẩu ngắn hơn");
        }
        if(!registerRequest.password().equals(registerRequest.confirmPassword())){
             throw new BusinessException("Confirm Password is different from Password");
        }
        String email= registerRequest.email();
        String password =registerRequest.password();
        String identityCard = registerRequest.identityCard();
        String mobile = registerRequest.mobile();
        String name = registerRequest.fullName();
        LocalDate birthday = registerRequest.birthday();
        try {
           Account account =accountRepository.save(new Account(email,encoder.encode(password), Role.CUSTOMER, AccountStatus.ACTIVE));
           Customer customer= customerRepository.saveAndFlush(new Customer(name,mobile,birthday,identityCard,email,account));
        } catch (DataIntegrityViolationException e){
            throw new BusinessException("Thông tin đăng ký đã được sử dụng. Vui lòng kiểm tra lại");
        }
    }
    private CurrentUserReponse toCustomer(Account account){
        var customer= customerRepository.findByAccount_Id((account.getId()));
        return new CurrentUserReponse(account.getId(),customer.map(Customer::getId).orElse(null),
                customer.map(Customer::getFullName).orElse(account.getUsername()),account.getRole());
    }
    private Account activeAccount(String username){
        return accountRepository.findByUsernameIgnoreCase(username).orElseThrow(()->new BadCredentialsException("INVALID_INCREDENTIAL"));
    }
    public CurrentUserReponse me(String username){
        return toCustomer(activeAccount(username));
    }
//    public TokenReponse login(LoginRequest request){
//        if (request.username() == null || request.username().isBlank()
//                || request.password() == null || request.password().isBlank()
//                || request.password().getBytes(StandardCharsets.UTF_8).length > 72) {
//            throw new BadCredentialsException("INVALID_CREDENTIALS");
//        }
//        Authentication authentication= authenticationManager.authenticate(
//                UsernamePasswordAuthenticationToken.unauthenticated(request.username().strip(),request.password())
//                );
//        var user =(UserDetails) authentication.getPrincipal();
//        Account account = accountRepository.findByUsernameIgnoreCase(request.username()).orElseThrow(()-> new IllegalArgumentException("Username not found"));
//        String token =  jwtService.generateToken(user,account.getId(),account.getTokenVersion());
//        return new TokenReponse(token,"Bearer",exprirationMillis/1000,toCustomer(account));
//    }
//    public CurrentUserReponse me(String username){
//        return toCustomer(activeAccount(username));
//    }
//    public Account activeAccount(String username){
//        return accountRepository.findByUsernameIgnoreCase(username).orElseThrow(()-> new BadCredentialsException("INVALID_CREDENTIALS"));
//    }
//    public CurrentUserReponse toCustomer(Account account){
//        var customer= customerRepository.findByAccount_Id(account.getId());
//        return new CurrentUserReponse(account.getId(),
//                customer.map(Customer::getId).orElse(null),
//                customer.map(Customer::getFullName).orElse(account.getUsername()),account.getRole());
//    }
}
