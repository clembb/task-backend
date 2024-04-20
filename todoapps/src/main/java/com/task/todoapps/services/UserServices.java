package com.task.todoapps.services;

import com.task.todoapps.model.User;

public interface UserServices {

    User saveUser(User user);

    void addToUser(String username);

}
