package com.goviesco.orders.service;

import com.goviesco.orders.entity.Order;

import java.util.Optional;

public interface OrderService {

    Iterable<Order> findAll();

    Optional<Order> findById(Long id);

    Order save(Order order);

    void delete(Order order);
}
