package com.example.springbootproject.controller;

import com.example.springbootproject.model.Restaurant;
import com.example.springbootproject.service.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/restaurants")
public class RestaurantController {

    @Autowired
    private RestaurantService restaurantService;

    // POST: /api/restaurants/add (Requires ROLE_VENDOR Token)
    @PostMapping("/add")
    public ResponseEntity<?> addRestaurant(@RequestBody Restaurant restaurant, Principal principal) {
        try {
            // principal.getName() returns the email of the logged-in user from the JWT
            Restaurant savedRestaurant = restaurantService.addRestaurant(restaurant, principal.getName());
            return ResponseEntity.ok(savedRestaurant);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // GET: /api/restaurants/search (Public Endpoint, no token needed)
    @GetMapping("/search")
    public ResponseEntity<List<Restaurant>> searchRestaurants(
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String cuisine,
            @RequestParam(required = false) Double minRating,
            @RequestParam(required = false) Integer priceRange) {
        List<Restaurant> results = restaurantService.searchRestaurants(location, category, cuisine, minRating,
                priceRange);
        return ResponseEntity.ok(results);
    }
}