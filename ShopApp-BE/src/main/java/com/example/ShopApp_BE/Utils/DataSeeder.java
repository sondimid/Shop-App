package com.example.ShopApp_BE.Utils;

import com.example.ShopApp_BE.Model.Entity.RoleEntity;
import com.example.ShopApp_BE.Repository.RoleRepository;
import com.example.ShopApp_BE.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) {
        if (roleRepository.count() == 0) {
            roleRepository.save(RoleEntity
                    .builder()
                    .role("ADMIN")
                    .build());
            roleRepository.save(RoleEntity
                    .builder()
                    .role("USER")
                    .build());
        }
    }
}

