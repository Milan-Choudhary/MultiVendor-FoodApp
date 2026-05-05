package com.example.springbootproject.repository;

import com.example.springbootproject.model.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    @Query("SELECT r FROM Restaurant r WHERE " +
            "(:location IS NULL OR LOWER(r.location) LIKE LOWER(CONCAT('%', :location, '%'))) AND " +
            "(:category IS NULL OR LOWER(r.category) = LOWER(:category)) AND " +
            "(:cuisine IS NULL OR LOWER(r.cuisine) = LOWER(:cuisine)) AND " +
            "(:minRating IS NULL OR r.rating >= :minRating) AND " +
            "(:priceRange IS NULL OR r.priceRange = :priceRange) AND " +
            "r.isOpen = true")
    List<Restaurant> searchRestaurants(
            @Param("location") String location,
            @Param("category") String category,
            @Param("cuisine") String cuisine,
            @Param("minRating") Double minRating,
            @Param("priceRange") Integer priceRange);
}