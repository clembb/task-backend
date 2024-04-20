package com.task.todoapps.services;

import com.task.todoapps.model.User;
import com.task.todoapps.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@Slf4j
public class UserService implements UserServices {

    @Autowired
    private UserRepository userRepository;


    private  final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository,PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder=passwordEncoder;
    }



    public List<User> findAll(){
        return userRepository.findAll();
    }

    public User findbyID(int theid){
        Optional<User> user=userRepository.findById(theid);
        User theuser=null;
        if(user.isPresent()){
            theuser=user.get();
        }else{
            throw new RuntimeException("not found"+theid);
        }
        return theuser;
    }

    public User save(User user){
        User theuser=userRepository.save(user);
        return theuser;
    }

    public void delete(User user){
        userRepository.delete(user);
    }

    public void deleteById(int theId)
    {
        userRepository.deleteById(theId);
    }

    @Override
    public User saveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return userRepository.save(user);
    }



    @Override
    public void addToUser(String username) {
        if(!userRepository.findByEmail(username).isPresent()){
            throw new IllegalArgumentException("User with email"+username+"does not exist");
        }


        User user  = userRepository.findByEmail(username).get();

    }
}
