package com.example.CinemaApplication.repository;

import com.example.CinemaApplication.model.Account;
import com.example.CinemaApplication.model.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface CustomerRepository extends JpaRepository<Customer,Long> {
    Optional<Customer> findByAccount_Id(Long id);
    boolean existsByEmailIgnoreCase(String email);
    boolean existsByPhone(String phone);
    boolean existsByIdentityCard(String phone);
    boolean existsByEmailIgnoreCaseAndIdNot(String email,Long id);
    @Query( """
            SELECT c 
            FROM Customer c 
            Where LOWER(c.fullName) LIKE LOWER(CONCAT('%',:keyword,'%')) OR
                  LOWER(c.email) LIKE LOWER(CONCAT('%',:keyword,'%')) OR
                  c.phone = :keyword
            """)
    Page<Customer> searchKeyword(@Param("keyword") String keyword, Pageable pageable);
    boolean existsByPhoneAndIdNot(String phone, Long id);
    boolean existsByIdentityCardAndIdNot(String identityCard,Long id);

}
