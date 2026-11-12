package com.example.CinemaApplication.Security;

import com.example.CinemaApplication.enums.AccountStatus;
import com.example.CinemaApplication.model.Account;
import com.example.CinemaApplication.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerUserDetailsService implements UserDetailsService {
    private final AccountRepository accountRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account= accountRepository.findByUsernameIgnoreCase(username).orElseThrow(() ->
                new BadCredentialsException("Invalid Credential!"));
        return User.withUsername(username).
                password(account.getPassword()).
                authorities("ROLE_" + account.getRole().name()).
                disabled(!AccountStatus.ACTIVE.equals(account.getStatus())).build()
                ;


    }
//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        Account account=accountRepository.findByUsernameIgnoreCase(username).
//                orElseThrow(()-> new UsernameNotFoundException("Username not exists: "+username));
//      return   User.withUsername(account.getUsername()).password(account.getPassword()).
//                authorities("ROLE_"+account.getRole().name()).
//                disabled(!"ACTIVE".equals(account.getStatus().name())).build();
//    }

}
