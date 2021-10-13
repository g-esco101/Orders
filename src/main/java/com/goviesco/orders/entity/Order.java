package com.goviesco.orders.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.goviesco.orders.enumeration.Status;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Data
@Entity // JPA annotation to make this object ready for storage in a JPA-based data store.
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Order {

    // JPA annotations to indicate it’s the primary key and automatically populated by the JPA provider.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 4) // Note: @Size and @Length are used to validate the size of a field. @Column is used to control DDL statements.
    private Status status;
    private String firstName;
    private String lastName;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private Address address;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderLine> orderLines;
    private String tax;
    private String shipping;
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

    public String getName() {
        return String.format("%s %s", firstName, lastName);
    }

    public void setName(String name) {
        String[] fullName = name.split(" ");
        setFirstName(fullName[0]);
        setLastName(fullName[1]);
    }
}
