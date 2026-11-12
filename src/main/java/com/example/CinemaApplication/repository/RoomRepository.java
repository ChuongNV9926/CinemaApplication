package com.example.CinemaApplication.repository;

import com.example.CinemaApplication.enums.RoomStatus;
import com.example.CinemaApplication.model.Room;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface RoomRepository extends JpaRepository<Room,Long> {
   List<Room> findByStatus(RoomStatus status);
   Optional<Room> findByNameIgnoreCase(String roomName);
   boolean existsByNameIgnoreCaseAndIdNot(String newRoomName, Long id);

   @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            Select r
            From Room r 
            Where r.id = :id
            """)
    Optional<Room> findByIdWithShowTimeUpdate(Long id);

}
