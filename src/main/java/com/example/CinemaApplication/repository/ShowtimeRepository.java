package com.example.CinemaApplication.repository;

import com.example.CinemaApplication.dto.RoomShowTimecount;
import com.example.CinemaApplication.enums.ShowTimeStatus;
import com.example.CinemaApplication.model.Showtime;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
@Repository
public interface ShowtimeRepository extends JpaRepository<Showtime,Long> {
    boolean existsByMovie_Id(Long id);
    boolean existsByRoom_Id(Long id);

    List<Showtime> findByMovie_IdAndStatusEqualsAndStartTimeAfterOrderByStartTimeAsc
            (Long id, ShowTimeStatus status, LocalDateTime after);


    @Query("""
            Select s
            From Showtime s join s.room r
            Where r.id = :roomId AND ( s.startTime < :end and s.endTime > :start ) and s.id <> :excludedShowtimeId
                  AND s.status <> :status
            """)
    List<Showtime> findOverlappingIgnoreCancelled(@Param("roomId") Long roomId,@Param("start") LocalDateTime start,
                                   @Param("end") LocalDateTime end,@Param("excludedShowtimeId") Long showtimeExcludedId,
                                                  @Param("status") ShowTimeStatus status);
@Query(value = """
        Select r.name , Count(s) as  showTimeCount
        From rooms r left join showtime s on r.room_id = s.room_id
        And s.status = 'OPEN' AND CAST( s.startTime as DATE) = CURRENT_DATE
        Group By r.name""",nativeQuery = true)

    List<RoomShowTimecount> countTodayShowtimeByRoom();
@Lock(LockModeType.PESSIMISTIC_WRITE)
   @Query("""
           Select DISTINCT s
           From Showtime s
            Where s.id =:id""")
    Optional<Showtime> findByIdForUpdate(@Param("id") Long id);
}
