package com.example.airbnbbookingspring.repositories.writes;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.airbnbbookingspring.models.User;

@Repository
public interface UserWriteRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

}