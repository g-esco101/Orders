package com.goviesco.orders.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

@ToString @EqualsAndHashCode
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "order_lines")
public class OrderLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "OrderLine Id - auto generated")
    private Long id;

    @Size(min = 1, max = 25, message = "Brand must be between 1 and 25 characters, inclusive.")
    @Column(length = 25)
    @NotBlank(message = "Brand is required")
    private String brand;

    @Size(min = 1, max = 25, message = "Model must be between 1 and 25 characters, inclusive.")
    @Column(length = 25)
    @NotBlank(message = "Model is required")
    private String model;

    @NotNull(message = "Cost is required")
    @PositiveOrZero(message = "Cost must be positive or zero.")
    private BigDecimal cost;

    @NotNull(message = "Quantity is required.")
    @PositiveOrZero(message = "Quantity must be positive or zero.")
    private int quantity;

}
