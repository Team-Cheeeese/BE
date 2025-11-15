package com.cheeeese.cheese4cut.infrastructure.persistence;

import com.cheeeese.cheese4cut.domain.Cheese4cutPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface Cheese4cutPhotoRepository extends JpaRepository<Cheese4cutPhoto, Long> {
    @Query("""
    SELECT c4p
    FROM Cheese4cutPhoto c4p
    JOIN FETCH c4p.cheese4cut c4c
    WHERE c4c.album.id IN :albumIds
    ORDER BY c4c.album.createdAt DESC, c4p.photoRank ASC
""")
    List<Cheese4cutPhoto> findAllCheese4cutPhotosByAlbumIds(@Param("albumIds") List<Long> albumIds);
}
