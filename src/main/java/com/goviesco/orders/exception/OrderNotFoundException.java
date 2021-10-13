package com.goviesco.orders.exception;

public class OrderNotFoundException extends RuntimeException{

    public OrderNotFoundException(Long id) {
        super(String.format("Order %d not found", id));
    }
}
