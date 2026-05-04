package com.parque.hotel.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.parque.hotel.model.Hotel;

public interface HotelRepository extends JpaRepository<Hotel, Long> {
}

