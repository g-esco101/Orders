package com.goviesco.orders;

import com.goviesco.orders.entity.Address;
import com.goviesco.orders.entity.Order;
import com.goviesco.orders.entity.OrderLine;
import com.goviesco.orders.enumeration.Status;
import com.goviesco.orders.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class LoadDatabase {

    private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);

    // Spring Boot will run ALL CommandLineRunner beans once the application context is loaded.

    @Bean
    CommandLineRunner initDatabase(OrderRepository repository) {

        Address address1 = new Address("2213 Camelback Rd", "Apt 2", "Phoenix", "AZ", "85017");
        Address address2 = new Address("4200 Wilshire Blvd", "", "Los Angeles", "CA", "90025");
        Address address3 = new Address("4545 Wilshire Blvd", "Apt 3", "Los Angeles", "CA", "90025");

        OrderLine orderLine1 = new OrderLine("Apple", "Phone", new BigDecimal("1000"), 1);
        OrderLine orderLine2 = new OrderLine("Apple", "Tablet", new BigDecimal("5000"), 2);
        OrderLine orderLine3 = new OrderLine("Samsung", "Watch", new BigDecimal("3500"), 1);
        OrderLine orderLine4 = new OrderLine("Emerson", "TV", new BigDecimal("8000"), 1);
        OrderLine orderLine5 = new OrderLine("Apple", "Laptop", new BigDecimal("2000"), 1);
        OrderLine orderLine6 = new OrderLine("LG", "Phone", new BigDecimal("1200"), 1);

        List<OrderLine> orderLines1 = new ArrayList<>();
        List<OrderLine> orderLines2 = new ArrayList<>();
        List<OrderLine> orderLines3 = new ArrayList<>();
        orderLines1.add(orderLine1);
        orderLines1.add(orderLine2);
        orderLines2.add(orderLine3);
        orderLines2.add(orderLine4);
        orderLines2.add(orderLine5);
        orderLines3.add(orderLine6);

        return args -> {
            log.info("Preloading " + repository.save(new Order(Status.PROCESSING, "Albert", "Einsten", address1, orderLines1, new BigDecimal("200"), new BigDecimal("300"), new BigDecimal("11000"), new BigDecimal("11500"))));
            log.info("Preloading " + repository.save(new Order(Status.COMPLETED, "Stephen", "Hawking", address2, orderLines2, new BigDecimal("300"), new BigDecimal("500"), new BigDecimal("13500"), new BigDecimal("14300"))));
            log.info("Preloading " + repository.save(new Order(Status.CANCELED, "Nikola", "Tesla", address3, orderLines3, new BigDecimal("100"), new BigDecimal("200"), new BigDecimal("1200"), new BigDecimal("1500"))));
        };
    }
}
