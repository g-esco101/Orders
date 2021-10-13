package com.goviesco.orders.controller;

import com.goviesco.orders.assembler.OrderModelAssembler;
import com.goviesco.orders.entity.Order;
import com.goviesco.orders.enumeration.Status;
import com.goviesco.orders.exception.OrderNotFoundException;
import com.goviesco.orders.repository.OrderRepository;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.mediatype.problem.Problem;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class OrderController {

    private final OrderRepository repository;
    private final OrderModelAssembler assembler;

    public OrderController(OrderRepository repository, OrderModelAssembler assembler) {
        this.repository = repository;
        this.assembler = assembler;
    }

    // CollectionModel<> is another Spring HATEOAS container that encapsulates collections of resources, instead of a single
    // resource entity, like EntityModel<>. It also lets you include links.
    @GetMapping("/orders")
    public ResponseEntity<CollectionModel<EntityModel<Order>>> readAll() {
        return ResponseEntity.ok(assembler.toCollectionModel(repository.findAll())
                .add(linkTo(methodOn(OrderController.class).readAll()).withSelfRel()));
    }

    @PostMapping("/orders")
    public ResponseEntity<EntityModel<Order>> create(@RequestBody Order order) {
        order.setStatus(Status.PROCESSING);
        Order newOrder = repository.save(order);

        return ResponseEntity
                .created(linkTo(methodOn(OrderController.class).read(newOrder.getId())).toUri())
                .body(assembler.toModel(newOrder));
    }

    @GetMapping("/orders/{id}")
    public ResponseEntity<EntityModel<Order>> read(@PathVariable Long id) {
        return repository.findById(id)
                .map(assembler::toModel)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new OrderNotFoundException(id));
    }

    @PutMapping("/orders/{id}")
    public ResponseEntity<?> update(@RequestBody Order newOrder, @PathVariable Long id) {
        Order updatedOrder = repository.findById(id)
                .map(order -> {
                    order.setStatus(newOrder.getStatus());
                    order.setFirstName(newOrder.getFirstName());
                    order.setLastName(newOrder.getLastName());
                    order.setAddress(newOrder.getAddress());
                    // Note: the existing list is modified; reassigning it would lead to a persistence exception.
                    order.getOrderLines().clear();
                    order.getOrderLines().addAll(newOrder.getOrderLines());
                    return repository.save(order);
                })
                .orElseThrow(() -> new OrderNotFoundException(id));

        EntityModel<Order> entityModel = assembler.toModel(updatedOrder);

        return ResponseEntity
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }

    @DeleteMapping("/orders/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        Order order = repository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));

        repository.delete(order);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/orders/{id}/cancel")
    public ResponseEntity<?> cancel(@PathVariable Long id) {

        Order order = repository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));

        if (order.getStatus() == Status.PROCESSING) {
            order.setStatus(Status.CANCELED);
            return ResponseEntity.ok(assembler.toModel(repository.save(order)));
        }

        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .header(HttpHeaders.CONTENT_TYPE, MediaTypes.HTTP_PROBLEM_DETAILS_JSON_VALUE)
                .body(Problem.create()
                        .withTitle("Method not allowed")
                        .withDetail(String.format("Not allowed to cancel an order with status %s", order.getStatus())));
    }

    @PutMapping("/orders/{id}/complete")
    public ResponseEntity<?> complete(@PathVariable Long id) {

        Order order = repository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));

        if (order.getStatus() == Status.PROCESSING) {
            order.setStatus(Status.COMPLETED);
            return ResponseEntity.ok(assembler.toModel(repository.save(order)));
        }

        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .header(HttpHeaders.CONTENT_TYPE, MediaTypes.HTTP_PROBLEM_DETAILS_JSON_VALUE)
                .body(Problem.create()
                        .withTitle("Method not allowed")
                        .withDetail(String.format("Not allowed to complete an order with status %s", order.getStatus())));
    }
}
