package com.goviesco.orders.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.goviesco.orders.enumeration.Status;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@ToString @EqualsAndHashCode
@Getter @Setter
@Entity // JPA annotation to make this object ready for storage in a JPA-based data store.
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Order {

    // JPA annotations to indicate it’s the primary key and automatically populated by the JPA provider.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "Order Id - auto generated")
    private Long id;

    @ApiModelProperty(value = "Date - auto generated when order is created (yyyy-mm-dd).")
    private LocalDate date;

    @Column(length = 4) // Note: @Size and @Length are used to validate the size of a field. @Column is used to control DDL statements.
    @ApiModelProperty(value = "Status - automatically set to PROCESSING when order is created.")
    private Status status;

    @Size(min = 1, max = 25, message = "First name cannot be greater than 25 characters.")
    @Column(length = 25)
    @NotBlank(message = "First name is required.")
    private String firstName;

    @Size(min = 1, max = 25, message = "Last name cannot be greater than 25 characters.")
    @Column(length = 25)
    @NotBlank(message = "Last name is required.")
    private String lastName;

    @Column(length = 50)
    @NotBlank(message = "Email is required.")
    // Regexp provided by RFC 5322. Allows all characters except | and ' due to sql injection risk.
    @Email(regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$", message = "Email format is invalid.")
    private String email;

    @Column(length = 25)
    @Pattern(regexp = "^((\\+\\d{1,3}( )?)?((\\(\\d{3}\\))|\\d{3})[- .]?\\d{3}[- .]?\\d{4})?$", message = "Phone number format is invalid. Valid formats include (but are not limited to) 2134541324, (213) 454-1324, and +111 (213) 454-1324.")
    private String phone;

    @Valid
    @NotNull(message = "Address is required.")
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private Address address;

    @Valid
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderLine> orderLines;

    @NotNull(message = "Tax is required.")
    @PositiveOrZero(message = "Tax must be positive or zero.")
    private BigDecimal tax;

    @NotNull(message = "Shipping is required.")
    @PositiveOrZero(message = "Shipping must be positive or zero.")
    private BigDecimal shipping;

    @Transient
    @ApiModelProperty(value = "Subtotal is calculated.")
    private BigDecimal subtotal;

    @Transient
    @ApiModelProperty(value = "Total is calculated.")
    private BigDecimal total;

    public Order(long id, Status status, String firstName, String lastName, String email, String phone, Address address,
                 List<OrderLine> orderLines, BigDecimal tax, BigDecimal shipping, BigDecimal subtotal, BigDecimal total) {
        
        this.id = id;
        this.status = status;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.orderLines = orderLines;
        this.tax = tax;
        this.shipping = shipping;
        this.subtotal = subtotal;
        this.total = total;
    }

    @PostLoad
    private void calculateTotals() {
        this.subtotal = new BigDecimal("0")
                .setScale(2, RoundingMode.HALF_UP);

        for(OrderLine line : orderLines) {
            BigDecimal quantity = new BigDecimal(line.getQuantity());
            BigDecimal lineTotal = quantity.multiply(line.getCost());
            subtotal = subtotal.add(lineTotal);
        }

        this.total = this.subtotal.add(this.tax)
                .add(this.shipping);
    }
}
