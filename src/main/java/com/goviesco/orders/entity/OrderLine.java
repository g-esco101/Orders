package com.goviesco.orders.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "order_lines")
public class OrderLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "OrderLine Id - auto generated")
    private Long id;
    private String brand;
    private String model;
    private String cost;
    private int quantity;

    public OrderLine(String brand, String model, String cost, int quantity) {
        this.brand = brand;
        this.model = model;
        this.cost = cost;
        this.quantity = quantity;
    }

}
