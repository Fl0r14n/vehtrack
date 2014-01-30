package com.rhcloud.application.vehtrack.web.security;

import com.rhcloud.application.vehtrack.dao.repository.AccountRepository;
import com.rhcloud.application.vehtrack.dao.repository.UserRepository;
import com.rhcloud.application.vehtrack.domain.Account;
import com.rhcloud.application.vehtrack.domain.ROLE;
import com.rhcloud.application.vehtrack.domain.User;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class UserDetailsServiceImpl implements UserDetailsService {

    private static final String admin = "admin";
    private static final String password = "hackme";
    
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private AccountRepository accountRepo;

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        Account account = accountRepo.findOne(login);
        if (account == null) {
            throw new UsernameNotFoundException(login);
        } else if (account.getLocked()) {
            throw new LockedException(login);
        }
        Set<ROLE> roles = account.getRoles();
        List<GrantedAuthority> auths = new ArrayList<>(roles.size());
        for (ROLE role : roles) {
            auths.add(new SimpleGrantedAuthority(role.name()));
        }
        return new org.springframework.security.core.userdetails.User(account.getLogin(), account.getPassword(), auths);
    }

    public void init() {
        if (!setupDone) {
            Account account = accountRepo.findOne("admin");
            if (account == null) {
                User user = new com.rhcloud.application.vehtrack.domain.User();
                {
                    user.setName("Administrator");
                    user.setEmail("admin@vehtrack-application.rhcloud.com");
                    user.setAccount(account = new Account());
                    {
                        account.setLogin(admin);
//                        account.setPassword(new Md5PasswordEncoder().encodePassword(password, null));
                        account.setPassword(password);
                        account.setRoles(new HashSet<>(Arrays.asList(ROLE.ADMIN)));
                        accountRepo.save(account);
                    }
                }
                userRepo.save(user);
                setupDone = true;
            }
        }
    }
    private boolean setupDone = false;
}