package com.goviesco.orders.assembler;

import com.goviesco.orders.controller.OrderController;
import com.goviesco.orders.entity.Order;
import com.goviesco.orders.enumeration.Status;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class OrderModelAssembler implements RepresentationModelAssembler<Order, EntityModel<Order>> {

    @Override
    public EntityModel<Order> toModel(Order order) {

        // EntityModel<T> is a generic container from Spring HATEOAS that includes not only the data but a collection of links.
        EntityModel<Order> orderModel = EntityModel.of(order,
                linkTo(methodOn(OrderController.class).read(order.getId())).withSelfRel(),
                linkTo(methodOn(OrderController.class).readAll()).withRel("orders"));

        if (order.getStatus() == Status.PROCESSING) {
            orderModel.add(linkTo(methodOn(OrderController.class).cancel(order.getId())).withRel("cancel"));
            orderModel.add(linkTo(methodOn(OrderController.class).complete(order.getId())).withRel("complete"));
        }

        return orderModel;
    }
}
