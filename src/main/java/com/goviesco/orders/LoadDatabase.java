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

        OrderLine orderLine1 = new OrderLine("Apple", "Phone", "1000", 1);
        OrderLine orderLine2 = new OrderLine("Apple", "Tablet", "5000", 2);
        OrderLine orderLine3 = new OrderLine("Samsung", "Watch", "3500", 1);
        OrderLine orderLine4 = new OrderLine("Emerson", "TV", "8000", 1);
        OrderLine orderLine5 = new OrderLine("Apple", "Laptop", "2000", 1);
        OrderLine orderLine6 = new OrderLine("LG", "Phone", "1200", 1);

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
            log.info("Preloading " + repository.save(new Order(Status.PROCESSING, "Albert", "Einsten", address1, orderLines1, "200", "300", "11500")));
            log.info("Preloading " + repository.save(new Order(Status.COMPLETED, "Stephen", "Hawking", address2, orderLines2, "300", "500", "14300")));
            log.info("Preloading " + repository.save(new Order(Status.CANCELED, "Nikola", "Tesla", address3, orderLines3, "100", "200", "1500")));
        };
    }
}
