package com.example.rest_api.database.users.repository;

import com.example.rest_api.database.users.model.RoleEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Transactional
@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Long> {
    public List<RoleEntity> findAllByName(String name);
    Boolean existsByName(String name);

    Optional<RoleEntity> findByName(String name);
}
