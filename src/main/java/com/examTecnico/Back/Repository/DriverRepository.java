package com.examTecnico.Back.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.examTecnico.Back.Models.Driver;
import java.util.List;
import java.util.UUID;

@Repository
public interface DriverRepository extends JpaRepository<Driver, UUID> {
    List<Driver> findByActiveTrue();
}
