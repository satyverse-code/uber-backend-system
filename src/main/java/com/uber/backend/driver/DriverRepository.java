package com.uber.backend.driver;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DriverRepository extends JpaRepository<Driver, Long> {

    @Query(value = """
            SELECT d.id as id,
                   d.name as name,
                   d.car_number as carNumber,
                   d.status as status,
                   ST_Distance(d.location, ST_SetSRID(ST_MakePoint(:lng, :lat), 4326)::geography) as distance
            FROM drivers d
            WHERE d.status = 'AVAILABLE'
              AND d.location IS NOT NULL
              AND ST_DWithin(d.location, ST_SetSRID(ST_MakePoint(:lng, :lat), 4326)::geography, :radiusMeters)
            ORDER BY distance
            LIMIT :limit
            """,
            nativeQuery = true)
    List<DriverNearby> findNearestWithin(@Param("lat") double lat,
                                         @Param("lng") double lng,
                                         @Param("radiusMeters") double radiusMeters,
                                         @Param("limit") int limit);
}
