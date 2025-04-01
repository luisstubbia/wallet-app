package org.luisstubbia.walletapp.service;

import org.luisstubbia.walletapp.exception.ConflictEntityException;
import org.luisstubbia.walletapp.model.User;
import org.luisstubbia.walletapp.repository.UserRepository;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User Not Found with username: " + username);
        }
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                Collections.emptyList()
        );
    }

    public User createUser(User user) throws ConflictEntityException {
        if (user != null) {
            try {
                user = userRepository.save(user);
            } catch (DataIntegrityViolationException e) {
                throw new ConflictEntityException(String.format("Username %s already exists", user.getUsername()));
            }
        }
        return user;
    }

    public User retrieveUserByUsername(String username) {
        var user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not fount with username: " + username);
        }
        return user;
    }
}