package com.goviesco.orders.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.goviesco.orders.enumeration.Status;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
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

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private Address address;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderLine> orderLines;

    @NotNull(message = "Tax is required.")
    @PositiveOrZero(message = "Tax must be positive or zero.")
    private BigDecimal tax;

    @NotNull(message = "Shipping is required")
    @PositiveOrZero(message = "Shipping must be positive or zero.")
    private BigDecimal shipping;

    @Transient
    @ApiModelProperty(value = "Subtotal is calculated.")
    private BigDecimal subtotal;

    @Transient
    @ApiModelProperty(value = "Total is calculated.")
    private BigDecimal total;

    public Order (Status status, String firstName, String lastName, Address address, List<OrderLine> orderLines,
                  BigDecimal tax, BigDecimal shipping, BigDecimal subtotal, BigDecimal total) {
        this.status = status;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.orderLines = orderLines;
        this.tax = tax;
        this.shipping = shipping;
        this.subtotal = subtotal;
        this.total = total;
    }

    @PostLoad
    private void calculateTotals() {
        orderLines.forEach(line -> {
            BigDecimal lineTotal = new BigDecimal(line.getQuantity()).multiply(line.getCost());
            this.subtotal = this.subtotal.add(lineTotal);
        });
        this.total = this.subtotal.add(this.tax)
                .add(this.shipping);
    }

    // Virtual getter for older clients that use name field instead of firstName and lastName.
    public String getName() {
        return String.format("%s %s", firstName, lastName);
    }

    // Virtual setter for older clients that use name field instead of firstName and lastName.
    public void setName(String name) {
        name = name.trim();
        // All characters after first space are the lastName. If name is blank, returns String[1] { "" }.
        String[] fullName = name.split(" ", 2);
        // Will throw MethodArgumentNotValidException with validation message for first name, if name is blank.
        setFirstName(fullName[0]);
        if (fullName.length < 2) {
            // Ensures that MethodArgumentNotValidException is thrown with validation message for lastName.
            setLastName("");
        } else {
            setLastName(fullName[1]);
        }
    }
}
