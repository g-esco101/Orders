package com.goviesco.orders.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@ToString @EqualsAndHashCode
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "addresses")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "Address Id - auto generated")
    private Long id;

    @NotBlank(message = "Address1 is required.")
    @Size(max = 50, message = "Address1 must be less than 50 characters, inclusive.")
    @Column(length = 50)
    private String address1;

    @Size(max = 25, message = "Address2 must be less than 25 characters, inclusive.")
    @Column(length = 25)
    private String address2;

    @NotBlank(message = "City is required.")
    @Size(min = 1, max = 25, message = "City must be between 1 and 25 characters, inclusive.")
    @Column(length = 25)
    private String city;

    @NotBlank(message = "State is required.")
    @Size(min = 2, max = 2, message = "State must be 2 characters.")
    @Column(length = 2)
    private String state;

    @NotBlank(message = "Zip code is required.")
    @Size(min = 5, max = 10, message = "Zip code must be between 5 and 10 characters, inclusive.")
    @Column(length = 10)
    private String zip;
}
