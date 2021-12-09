package com.goviesco.orders.service;

import com.goviesco.orders.entity.Order;
import com.goviesco.orders.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class OrderServiceImpl implements OrderService {

    private final OrderRepository repo;

    @Autowired
    public OrderServiceImpl(OrderRepository repo) {
        this.repo = repo;
    }

    @Override
    public Iterable<Order> findAll() {
        return repo.findAll();
    }

    @Override
    public Optional<Order> findById(Long id) {
        return repo.findById(id);
    }

    @Override
    public Order save(Order order) {
        return repo.save(order);
    }

    @Override
    public void delete(Order order) {
        repo.delete(order);
    }
}
