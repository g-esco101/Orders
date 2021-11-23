package com.goviesco.orders.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.goviesco.orders.enumeration.Status;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;


@ToString @EqualsAndHashCode
@Entity // JPA annotation to make this object ready for storage in a JPA-based data store.
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Order {

    // JPA annotations to indicate it’s the primary key and automatically populated by the JPA provider.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter @Setter
    @ApiModelProperty(value = "Order Id - auto generated")
    private Long id;

    @Getter @Setter
    @Column(length = 4) // Note: @Size and @Length are used to validate the size of a field. @Column is used to control DDL statements.
    private Status status;

    @Getter @Setter
    @NotBlank(message = "First name is required.")
    private String firstName;

    @Getter @Setter
    @NotBlank(message = "Last name is required.")
    private String lastName;

    @Getter @Setter
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @ApiModelProperty(notes = "Blank address indicates store pick-up")
    private Address address;

    @Getter @Setter
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderLine> orderLines;

    @Getter @Setter
    private String tax;

    @Getter @Setter
    private String shipping;

    @Getter @Setter
    private String total;

    public Order (Status status, String firstName, String lastName, Address address,
                  List<OrderLine> orderLines, String tax, String shipping, String total) {
        this.status = status;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.orderLines = orderLines;
        this.tax = tax;
        this.shipping = shipping;
        this.total = total;
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
        // Will throw MethodArgumentNotValidException with validation message for first name, if name is empty.
        setFirstName(fullName[0]);
        if (fullName.length < 2) {
            // Ensures that MethodArgumentNotValidException is thrown with validation message for lastName.
            setLastName("");
        } else {
            setLastName(fullName[1]);
        }
    }
}
