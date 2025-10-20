package iuh.fit.se.ace_store_www.repository;

import iuh.fit.se.ace_store_www.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findByUserId(Long userId);
}