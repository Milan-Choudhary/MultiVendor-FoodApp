package com.example.springbootproject.service;

import com.example.springbootproject.model.Restaurant;
import com.example.springbootproject.model.User;
import com.example.springbootproject.repository.RestaurantRepository;
import com.example.springbootproject.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RestaurantService {

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private UserRepository userRepository;

    // We now pass the email extracted from the JWT token
    public Restaurant addRestaurant(Restaurant restaurant, String ownerEmail) {
        User owner = userRepository.findByEmail(ownerEmail)
                .orElseThrow(() -> new RuntimeException("Vendor not found"));

        restaurant.setOwner(owner);
        return restaurantRepository.save(restaurant);
    }

    public List<Restaurant> searchRestaurants(String location, String category, String cuisine, Double minRating,
            Integer priceRange) {
        return restaurantRepository.searchRestaurants(location, category, cuisine, minRating, priceRange);
    }
}