package com.goviesco.orders;


import static org.hamcrest.CoreMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.goviesco.orders.assembler.OrderModelAssembler;
import com.goviesco.orders.controller.OrderController;
import com.goviesco.orders.entity.Address;
import com.goviesco.orders.entity.Order;
import com.goviesco.orders.entity.OrderLine;
import com.goviesco.orders.enumeration.Status;
import com.goviesco.orders.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@WebMvcTest(OrderController.class) // confines Spring Boot to only autoconfiguring Spring MVC components, and only this one controller, making it a very precise test case.
@Import({OrderModelAssembler.class }) // pulls in one extra Spring component that would be ignored by @WebMvcTest.
public class OrderControllerTests {

    @Autowired // gives us a handle on a Spring Mock tester.
    private MockMvc mvc;

    @MockBean //  flags OrderRepository as a test collaborator.
    private OrderRepository repository;


    @Test
    public void cancelCanceledOrderShouldCreateProblem() throws Exception {

        Address address1 = new Address(1L, "2213 Camelback Rd", "Apt 2", "Phoenix", "AZ", "85017");
        OrderLine orderLine1 = new OrderLine(1L, "Apple", "Phone", "1000", 1);
        List<OrderLine> orderLines1 = new ArrayList<>();
        orderLines1.add(orderLine1);

        given(repository.findById(1L)).willReturn(
                java.util.Optional.of(new Order(1L, Status.CANCELED, "Marie", "Curie", address1, orderLines1, "100", "50", "1150"))
        );

        mvc.perform(put("/orders/1/cancel")
                        .accept(MediaTypes.HAL_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isMethodNotAllowed())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HTTP_PROBLEM_DETAILS_JSON_VALUE))
                .andExpect(jsonPath("$.title", is("Method not allowed")))
                .andExpect(jsonPath("$.detail", is("Not allowed to cancel an order with status CANCELED")))
                .andReturn();
    }

    @Test
    public void cancelCompletedOrderShouldCreateProblem() throws Exception {

        Address address1 = new Address(1L, "2213 Camelback Rd", "Apt 2", "Phoenix", "AZ", "85017");
        OrderLine orderLine1 = new OrderLine(1L, "Apple", "Phone", "1000", 1);
        List<OrderLine> orderLines1 = new ArrayList<>();
        orderLines1.add(orderLine1);

        given(repository.findById(1L)).willReturn(
                java.util.Optional.of(new Order(1L, Status.COMPLETED, "Marie", "Curie", address1, orderLines1, "100", "50", "1150"))
        );

        mvc.perform(put("/orders/1/cancel")
                        .accept(MediaTypes.HAL_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isMethodNotAllowed())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HTTP_PROBLEM_DETAILS_JSON_VALUE))
                .andExpect(jsonPath("$.title", is("Method not allowed")))
                .andExpect(jsonPath("$.detail", is("Not allowed to cancel an order with status COMPLETED")))
                .andReturn();
    }

    @Test
    public void completeCompletedOrderShouldCreateProblem() throws Exception {

        Address address1 = new Address(1L, "2213 Camelback Rd", "Apt 2", "Phoenix", "AZ", "85017");
        OrderLine orderLine1 = new OrderLine(1L, "Apple", "Phone", "1000", 1);
        List<OrderLine> orderLines1 = new ArrayList<>();
        orderLines1.add(orderLine1);

        given(repository.findById(1L)).willReturn(
                java.util.Optional.of(new Order(1L, Status.COMPLETED, "Marie", "Curie", address1, orderLines1, "100", "50", "1150"))
        );

        mvc.perform(put("/orders/1/complete")
                        .accept(MediaTypes.HAL_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isMethodNotAllowed())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HTTP_PROBLEM_DETAILS_JSON_VALUE))
                .andExpect(jsonPath("$.title", is("Method not allowed")))
                .andExpect(jsonPath("$.detail", is("Not allowed to complete an order with status COMPLETED")))
                .andReturn();
    }

    @Test
    public void completeCanceledOrderShouldCreateProblem() throws Exception {

        Address address1 = new Address(1L, "2213 Camelback Rd", "Apt 2", "Phoenix", "AZ", "85017");
        OrderLine orderLine1 = new OrderLine(1L, "Apple", "Phone", "1000", 1);
        List<OrderLine> orderLines1 = new ArrayList<>();
        orderLines1.add(orderLine1);

        given(repository.findById(1L)).willReturn(
                java.util.Optional.of(new Order(1L, Status.CANCELED, "Marie", "Curie", address1, orderLines1, "100", "50", "1150"))
        );

        mvc.perform(put("/orders/1/complete")
                        .accept(MediaTypes.HAL_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isMethodNotAllowed())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HTTP_PROBLEM_DETAILS_JSON_VALUE))
                .andExpect(jsonPath("$.title", is("Method not allowed")))
                .andExpect(jsonPath("$.detail", is("Not allowed to complete an order with status CANCELED")))
                .andReturn();
    }

    @Test
    public void completeShouldUpdateOrderStatusFromProcessingToComplete() throws Exception {

        Address address1 = new Address(1L, "2213 Camelback Rd", "Apt 2", "Phoenix", "AZ", "85017");
        OrderLine orderLine1 = new OrderLine(1L, "Apple", "Phone", "1000", 1);
        List<OrderLine> orderLines1 = new ArrayList<>();
        orderLines1.add(orderLine1);

        given(repository.findById(1L)).willReturn(
                java.util.Optional.of(new Order(1L, Status.PROCESSING, "Marie", "Curie", address1, orderLines1, "100", "50", "1150"))
        );

        given(repository.save(ArgumentMatchers.any(Order.class))).willReturn(
                new Order(1L, Status.COMPLETED,"Marie", "Curie", address1, orderLines1, "100", "50", "1150")
        );

        mvc.perform(put("/orders/1/complete")
                        .accept(MediaTypes.HAL_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.status", is("COMPLETED")))
                .andExpect(jsonPath("$.name", is("Marie Curie")))
                .andExpect(jsonPath("$.firstName", is("Marie")))
                .andExpect(jsonPath("$.lastName", is("Curie")))
                .andExpect(jsonPath("$.address.id", is(1)))
                .andExpect(jsonPath("$.address.address1", is("2213 Camelback Rd")))
                .andExpect(jsonPath("$.address.address2", is("Apt 2")))
                .andExpect(jsonPath("$.address.city", is("Phoenix")))
                .andExpect(jsonPath("$.address.state", is("AZ")))
                .andExpect(jsonPath("$.address.zip", is("85017")))
                .andExpect(jsonPath("$.orderLines[0].id", is(1)))
                .andExpect(jsonPath("$.orderLines[0].brand", is("Apple")))
                .andExpect(jsonPath("$.orderLines[0].model", is("Phone")))
                .andExpect(jsonPath("$.orderLines[0].cost", is("1000")))
                .andExpect(jsonPath("$.orderLines[0].quantity", is(1)))
                .andExpect(jsonPath("$.tax", is("100")))
                .andExpect(jsonPath("$.shipping", is("50")))
                .andExpect(jsonPath("$.total", is("1150")))
                .andExpect(jsonPath("$._links.self.href", is("http://localhost/orders/1")))
                .andExpect(jsonPath("$._links.orders.href", is("http://localhost/orders")))
                .andReturn();
    }

    @Test
    public void cancelShouldUpdateOrderStatusFromProcessingToCanceled() throws Exception {

        Address address1 = new Address(1L, "2213 Camelback Rd", "Apt 2", "Phoenix", "AZ", "85017");
        OrderLine orderLine1 = new OrderLine(1L, "Apple", "Phone", "1000", 1);
        List<OrderLine> orderLines1 = new ArrayList<>();
        orderLines1.add(orderLine1);

        given(repository.findById(1L)).willReturn(
                java.util.Optional.of(new Order(1L, Status.PROCESSING, "Marie", "Curie", address1, orderLines1, "100", "50", "1150"))
        );

        given(repository.save(ArgumentMatchers.any(Order.class))).willReturn(
                new Order(1L, Status.CANCELED,"Marie", "Curie", address1, orderLines1, "100", "50", "1150")
        );

        mvc.perform(put("/orders/1/cancel")
                        .accept(MediaTypes.HAL_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.status", is("CANCELED")))
                .andExpect(jsonPath("$.name", is("Marie Curie")))
                .andExpect(jsonPath("$.firstName", is("Marie")))
                .andExpect(jsonPath("$.lastName", is("Curie")))
                .andExpect(jsonPath("$.address.id", is(1)))
                .andExpect(jsonPath("$.address.address1", is("2213 Camelback Rd")))
                .andExpect(jsonPath("$.address.address2", is("Apt 2")))
                .andExpect(jsonPath("$.address.city", is("Phoenix")))
                .andExpect(jsonPath("$.address.state", is("AZ")))
                .andExpect(jsonPath("$.address.zip", is("85017")))
                .andExpect(jsonPath("$.orderLines[0].id", is(1)))
                .andExpect(jsonPath("$.orderLines[0].brand", is("Apple")))
                .andExpect(jsonPath("$.orderLines[0].model", is("Phone")))
                .andExpect(jsonPath("$.orderLines[0].cost", is("1000")))
                .andExpect(jsonPath("$.orderLines[0].quantity", is(1)))
                .andExpect(jsonPath("$.tax", is("100")))
                .andExpect(jsonPath("$.shipping", is("50")))
                .andExpect(jsonPath("$.total", is("1150")))
                .andExpect(jsonPath("$._links.self.href", is("http://localhost/orders/1")))
                .andExpect(jsonPath("$._links.orders.href", is("http://localhost/orders")))
                .andReturn();
    }

    @Test
    public void updateShouldUpdateOrder() throws Exception {

        Address address1 = new Address(1L, "2213 Camelback Rd", "Apt 2", "Phoenix", "AZ", "85017");
        OrderLine orderLine1 = new OrderLine(1L, "Apple", "Phone", "1000", 1);
        OrderLine orderLine2 = new OrderLine(2L, "LG", "Phone", "200", 1);
        List<OrderLine> orderLines1 = new ArrayList<>();
        orderLines1.add(orderLine1);
        List<OrderLine> orderLines2 = new ArrayList<>();
        orderLines2.add(orderLine2);

        given(repository.findById(1L)).willReturn(
                java.util.Optional.of(new Order(1L, Status.PROCESSING, "Marie", "Curie", address1, orderLines1, "100", "50", "1150"))
        );

        given(repository.save(ArgumentMatchers.any(Order.class))).willReturn(
                new Order(1L, Status.PROCESSING,"Marie", "Curie", address1, orderLines2, "20", "25", "245")
        );

        mvc.perform(put("/orders/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "    \"firstName\": \"Marie\",\n" +
                                "    \"lastName\": \"Curie\",\n" +
                                "    \"address\": {\n" +
                                "        \"address1\": \"2213 Camelback Rd\",\n" +
                                "        \"address2\": \"Apt 2\",\n" +
                                "        \"city\": \"Phoenix\",\n" +
                                "        \"state\": \"AZ\",\n" +
                                "        \"zip\": \"85017\"\n" +
                                "    },\n" +
                                "    \"orderLines\": [\n" +
                                "        {\n" +
                                "            \"brand\": \"LG\",\n" +
                                "            \"model\": \"Phone\",\n" +
                                "            \"cost\": \"200\",\n" +
                                "            \"quantity\": 1\n" +
                                "        }\n" +
                                "    ],\n" +
                                "    \"tax\": \"20\",\n" +
                                "    \"shipping\": \"25\",\n" +
                                "    \"total\": \"245\"\n" +
                                "}")
                        .accept(MediaTypes.HAL_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.status", is("PROCESSING")))
                .andExpect(jsonPath("$.name", is("Marie Curie")))
                .andExpect(jsonPath("$.firstName", is("Marie")))
                .andExpect(jsonPath("$.lastName", is("Curie")))
                .andExpect(jsonPath("$.address.id", is(1)))
                .andExpect(jsonPath("$.address.address1", is("2213 Camelback Rd")))
                .andExpect(jsonPath("$.address.address2", is("Apt 2")))
                .andExpect(jsonPath("$.address.city", is("Phoenix")))
                .andExpect(jsonPath("$.address.state", is("AZ")))
                .andExpect(jsonPath("$.address.zip", is("85017")))
                .andExpect(jsonPath("$.orderLines[0].id", is(2)))
                .andExpect(jsonPath("$.orderLines[0].brand", is("LG")))
                .andExpect(jsonPath("$.orderLines[0].model", is("Phone")))
                .andExpect(jsonPath("$.orderLines[0].cost", is("200")))
                .andExpect(jsonPath("$.orderLines[0].quantity", is(1)))
                .andExpect(jsonPath("$.tax", is("20")))
                .andExpect(jsonPath("$.shipping", is("25")))
                .andExpect(jsonPath("$.total", is("245")))
                .andExpect(jsonPath("$._links.self.href", is("http://localhost/orders/1")))
                .andExpect(jsonPath("$._links.complete.href", is("http://localhost/orders/1/complete")))
                .andExpect(jsonPath("$._links.cancel.href", is("http://localhost/orders/1/cancel")))
                .andExpect(jsonPath("$._links.orders.href", is("http://localhost/orders")))
                .andReturn();
    }

    @Test
    public void createShouldCreateOrder() throws Exception {

        Address address1 = new Address(1L, "2213 Camelback Rd", "Apt 2", "Phoenix", "AZ", "85017");

        OrderLine orderLine1 = new OrderLine(1L, "Apple", "Phone", "1000", 1);

        List<OrderLine> orderLines1 = new ArrayList<>();
        orderLines1.add(orderLine1);

        given(repository.save(ArgumentMatchers.any(Order.class))).willReturn(
                        new Order(1L, Status.PROCESSING,"Marie", "Curie", address1, orderLines1, "100", "50", "1150")
                );

        mvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "    \"firstName\": \"Marie\",\n" +
                                "    \"lastName\": \"Curie\",\n" +
                                "    \"address\": {\n" +
                                "        \"address1\": \"2213 Camelback Rd\",\n" +
                                "        \"address2\": \"Apt 2\",\n" +
                                "        \"city\": \"Phoenix\",\n" +
                                "        \"state\": \"AZ\",\n" +
                                "        \"zip\": \"85017\"\n" +
                                "    },\n" +
                                "    \"orderLines\": [\n" +
                                "        {\n" +
                                "            \"brand\": \"Apple\",\n" +
                                "            \"model\": \"Phone\",\n" +
                                "            \"cost\": \"1000\",\n" +
                                "            \"quantity\": 1\n" +
                                "        }\n" +
                                "    ],\n" +
                                "    \"tax\": \"100\",\n" +
                                "    \"shipping\": \"50\",\n" +
                                "    \"total\": \"1150\"\n" +
                                "}")
                        .accept(MediaTypes.HAL_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.status", is("PROCESSING")))
                .andExpect(jsonPath("$.name", is("Marie Curie")))
                .andExpect(jsonPath("$.firstName", is("Marie")))
                .andExpect(jsonPath("$.lastName", is("Curie")))
                .andExpect(jsonPath("$.address.id", is(1)))
                .andExpect(jsonPath("$.address.address1", is("2213 Camelback Rd")))
                .andExpect(jsonPath("$.address.address2", is("Apt 2")))
                .andExpect(jsonPath("$.address.city", is("Phoenix")))
                .andExpect(jsonPath("$.address.state", is("AZ")))
                .andExpect(jsonPath("$.address.zip", is("85017")))
                .andExpect(jsonPath("$.orderLines[0].id", is(1)))
                .andExpect(jsonPath("$.orderLines[0].brand", is("Apple")))
                .andExpect(jsonPath("$.orderLines[0].model", is("Phone")))
                .andExpect(jsonPath("$.orderLines[0].cost", is("1000")))
                .andExpect(jsonPath("$.orderLines[0].quantity", is(1)))
                .andExpect(jsonPath("$.tax", is("100")))
                .andExpect(jsonPath("$.shipping", is("50")))
                .andExpect(jsonPath("$.total", is("1150")))
                .andExpect(jsonPath("$._links.self.href", is("http://localhost/orders/1")))
                .andExpect(jsonPath("$._links.complete.href", is("http://localhost/orders/1/complete")))
                .andExpect(jsonPath("$._links.cancel.href", is("http://localhost/orders/1/cancel")))
                .andExpect(jsonPath("$._links.orders.href", is("http://localhost/orders")))
                .andReturn();
    }

    @Test
    public void readShouldReadOrder() throws Exception {

        Address address1 = new Address(1L, "2213 Camelback Rd", "Apt 2", "Phoenix", "AZ", "85017");
        OrderLine orderLine1 = new OrderLine(1L, "Apple", "Phone", "1000", 1);
        List<OrderLine> orderLines1 = new ArrayList<>();
        orderLines1.add(orderLine1);

        given(repository.findById(1L)).willReturn(
                java.util.Optional.of(new Order(1L, Status.PROCESSING, "Marie", "Curie", address1, orderLines1, "100", "50", "1150"))
                );

        mvc.perform(get("/orders/1")
                        .accept(MediaTypes.HAL_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.status", is("PROCESSING")))
                .andExpect(jsonPath("$.name", is("Marie Curie")))
                .andExpect(jsonPath("$.firstName", is("Marie")))
                .andExpect(jsonPath("$.lastName", is("Curie")))
                .andExpect(jsonPath("$.address.id", is(1)))
                .andExpect(jsonPath("$.address.address1", is("2213 Camelback Rd")))
                .andExpect(jsonPath("$.address.address2", is("Apt 2")))
                .andExpect(jsonPath("$.address.city", is("Phoenix")))
                .andExpect(jsonPath("$.address.state", is("AZ")))
                .andExpect(jsonPath("$.address.zip", is("85017")))
                .andExpect(jsonPath("$.orderLines[0].id", is(1)))
                .andExpect(jsonPath("$.orderLines[0].brand", is("Apple")))
                .andExpect(jsonPath("$.orderLines[0].model", is("Phone")))
                .andExpect(jsonPath("$.orderLines[0].cost", is("1000")))
                .andExpect(jsonPath("$.orderLines[0].quantity", is(1)))
                .andExpect(jsonPath("$._links.self.href", is("http://localhost/orders/1")))
                .andExpect(jsonPath("$._links.complete.href", is("http://localhost/orders/1/complete")))
                .andExpect(jsonPath("$._links.cancel.href", is("http://localhost/orders/1/cancel")))
                .andExpect(jsonPath("$._links.orders.href", is("http://localhost/orders")))
                .andReturn();
    }

    @Test
    public void readAllShouldReadAllOrders() throws Exception {

        Address address1 = new Address(1L, "2213 Camelback Rd", "Apt 2", "Phoenix", "AZ", "85017");
        Address address2 = new Address(2L, "4200 Wilshire Blvd", "", "Los Angeles", "CA", "90025");
        Address address3 = new Address(3L, "4545 Wilshire Blvd", "Apt 3", "Los Angeles", "CA", "90025");

        OrderLine orderLine1 = new OrderLine(1L, "Apple", "Phone", "1000", 1);
        OrderLine orderLine2 = new OrderLine(2L, "Dell", "Tablet", "5000", 2);
        OrderLine orderLine3 = new OrderLine(3L, "Samsung", "Watch", "3500", 1);

        List<OrderLine> orderLines1 = new ArrayList<>();
        List<OrderLine> orderLines2 = new ArrayList<>();
        List<OrderLine> orderLines3 = new ArrayList<>();
        orderLines1.add(orderLine1);
        orderLines2.add(orderLine2);
        orderLines3.add(orderLine3);

        given(repository.findAll()).willReturn(
                Arrays.asList(
                        new Order(1L, Status.PROCESSING,"Marie", "Curie", address1, orderLines1, "100", "50", "1150"),
                        new Order(2L, Status.COMPLETED,"Rosalind", "Franklin", address2, orderLines2, "1000", "200", "11200"),
                        new Order(3L, Status.CANCELED,"Nikola", "Tesla", address3, orderLines3, "500", "300", "4300")
                        ));

        mvc.perform(get("/orders").accept(MediaTypes.HAL_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
                .andExpect(jsonPath("$._embedded.orderList[0].id", is(1)))
                .andExpect(jsonPath("$._embedded.orderList[0].status", is("PROCESSING")))
                .andExpect(jsonPath("$._embedded.orderList[0].name", is("Marie Curie")))
                .andExpect(jsonPath("$._embedded.orderList[0].firstName", is("Marie")))
                .andExpect(jsonPath("$._embedded.orderList[0].lastName", is("Curie")))
                .andExpect(jsonPath("$._embedded.orderList[0].address.id", is(1)))
                .andExpect(jsonPath("$._embedded.orderList[0].address.address1", is("2213 Camelback Rd")))
                .andExpect(jsonPath("$._embedded.orderList[0].address.address2", is("Apt 2")))
                .andExpect(jsonPath("$._embedded.orderList[0].address.city", is("Phoenix")))
                .andExpect(jsonPath("$._embedded.orderList[0].address.state", is("AZ")))
                .andExpect(jsonPath("$._embedded.orderList[0].address.zip", is("85017")))
                .andExpect(jsonPath("$._embedded.orderList[0].orderLines[0].id", is(1)))
                .andExpect(jsonPath("$._embedded.orderList[0].orderLines[0].brand", is("Apple")))
                .andExpect(jsonPath("$._embedded.orderList[0].orderLines[0].model", is("Phone")))
                .andExpect(jsonPath("$._embedded.orderList[0].orderLines[0].cost", is("1000")))
                .andExpect(jsonPath("$._embedded.orderList[0].orderLines[0].quantity", is(1)))
                .andExpect(jsonPath("$._embedded.orderList[0].tax", is("100")))
                .andExpect(jsonPath("$._embedded.orderList[0].shipping", is("50")))
                .andExpect(jsonPath("$._embedded.orderList[0].total", is("1150")))
                .andExpect(jsonPath("$._embedded.orderList[0]._links.self.href", is("http://localhost/orders/1")))
                .andExpect(jsonPath("$._embedded.orderList[0]._links.complete.href", is("http://localhost/orders/1/complete")))
                .andExpect(jsonPath("$._embedded.orderList[0]._links.cancel.href", is("http://localhost/orders/1/cancel")))
                .andExpect(jsonPath("$._embedded.orderList[0]._links.orders.href", is("http://localhost/orders")))

                .andExpect(jsonPath("$._embedded.orderList[1].id", is(2)))
                .andExpect(jsonPath("$._embedded.orderList[1].status", is("COMPLETED")))
                .andExpect(jsonPath("$._embedded.orderList[1].name", is("Rosalind Franklin")))
                .andExpect(jsonPath("$._embedded.orderList[1].firstName", is("Rosalind")))
                .andExpect(jsonPath("$._embedded.orderList[1].lastName", is("Franklin")))
                .andExpect(jsonPath("$._embedded.orderList[1].address.id", is(2)))
                .andExpect(jsonPath("$._embedded.orderList[1].address.address1", is("4200 Wilshire Blvd")))
                .andExpect(jsonPath("$._embedded.orderList[1].address.address2", is("")))
                .andExpect(jsonPath("$._embedded.orderList[1].address.city", is("Los Angeles")))
                .andExpect(jsonPath("$._embedded.orderList[1].address.state", is("CA")))
                .andExpect(jsonPath("$._embedded.orderList[1].address.zip", is("90025")))
                .andExpect(jsonPath("$._embedded.orderList[1].orderLines[0].id", is(2)))
                .andExpect(jsonPath("$._embedded.orderList[1].orderLines[0].brand", is("Dell")))
                .andExpect(jsonPath("$._embedded.orderList[1].orderLines[0].model", is("Tablet")))
                .andExpect(jsonPath("$._embedded.orderList[1].orderLines[0].cost", is("5000")))
                .andExpect(jsonPath("$._embedded.orderList[1].orderLines[0].quantity", is(2)))
                .andExpect(jsonPath("$._embedded.orderList[1].tax", is("1000")))
                .andExpect(jsonPath("$._embedded.orderList[1].shipping", is("200")))
                .andExpect(jsonPath("$._embedded.orderList[1].total", is("11200")))
                .andExpect(jsonPath("$._embedded.orderList[1]._links.self.href", is("http://localhost/orders/2")))
                .andExpect(jsonPath("$._embedded.orderList[1]._links.orders.href", is("http://localhost/orders")))

                .andExpect(jsonPath("$._embedded.orderList[2].id", is(3)))
                .andExpect(jsonPath("$._embedded.orderList[2].status", is("CANCELED")))
                .andExpect(jsonPath("$._embedded.orderList[2].name", is("Nikola Tesla")))
                .andExpect(jsonPath("$._embedded.orderList[2].firstName", is("Nikola")))
                .andExpect(jsonPath("$._embedded.orderList[2].lastName", is("Tesla")))
                .andExpect(jsonPath("$._embedded.orderList[2].address.id", is(3)))
                .andExpect(jsonPath("$._embedded.orderList[2].address.address1", is("4545 Wilshire Blvd")))
                .andExpect(jsonPath("$._embedded.orderList[2].address.address2", is("Apt 3")))
                .andExpect(jsonPath("$._embedded.orderList[2].address.city", is("Los Angeles")))
                .andExpect(jsonPath("$._embedded.orderList[2].address.state", is("CA")))
                .andExpect(jsonPath("$._embedded.orderList[2].address.zip", is("90025")))
                .andExpect(jsonPath("$._embedded.orderList[2].orderLines[0].id", is(3)))
                .andExpect(jsonPath("$._embedded.orderList[2].orderLines[0].brand", is("Samsung")))
                .andExpect(jsonPath("$._embedded.orderList[2].orderLines[0].model", is("Watch")))
                .andExpect(jsonPath("$._embedded.orderList[2].orderLines[0].cost", is("3500")))
                .andExpect(jsonPath("$._embedded.orderList[2].orderLines[0].quantity", is(1)))
                .andExpect(jsonPath("$._embedded.orderList[2].tax", is("500")))
                .andExpect(jsonPath("$._embedded.orderList[2].shipping", is("300")))
                .andExpect(jsonPath("$._embedded.orderList[2].total", is("4300")))
                .andExpect(jsonPath("$._embedded.orderList[2]._links.self.href", is("http://localhost/orders/3")))
                .andExpect(jsonPath("$._embedded.orderList[2]._links.orders.href", is("http://localhost/orders")))
                .andExpect(jsonPath("$._links.self.href", is("http://localhost/orders")))
                .andReturn();
    }

    @Test
    public void readNonExistingOrderShouldThrowOrderNotFoundException() throws Exception {

        given(repository.findById(1L)).willReturn(
                java.util.Optional.empty()
        );

        mvc.perform(get("/orders/1")
                        .accept(MediaTypes.HAL_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HTTP_PROBLEM_DETAILS_JSON_VALUE))
                .andExpect(jsonPath("$.title", is("Not Found")))
                .andExpect(jsonPath("$.detail", is("Order 1 not found")))
                .andReturn();
    }

    @Test
    public void deleteNonExistingOrderShouldThrowOrderNotFoundException() throws Exception {

        given(repository.findById(1L)).willReturn(
                java.util.Optional.empty()
        );

        mvc.perform(delete("/orders/1").accept(MediaTypes.HAL_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HTTP_PROBLEM_DETAILS_JSON_VALUE))
                .andExpect(jsonPath("$.title", is("Not Found")))
                .andExpect(jsonPath("$.detail", is("Order 1 not found")))
                .andReturn();
    }

    @Test
    public void cancelNonExistingOrderShouldThrowOrderNotFoundException() throws Exception {

        given(repository.findById(1L)).willReturn(
                java.util.Optional.empty()
        );

        mvc.perform(put("/orders/1/cancel").accept(MediaTypes.HAL_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HTTP_PROBLEM_DETAILS_JSON_VALUE))
                .andExpect(jsonPath("$.title", is("Not Found")))
                .andExpect(jsonPath("$.detail", is("Order 1 not found")))
                .andReturn();
    }

    @Test
    public void completeNonExistingOrderShouldThrowOrderNotFoundException() throws Exception {

        given(repository.findById(1L)).willReturn(
                java.util.Optional.empty()
        );

        mvc.perform(put("/orders/1/complete").accept(MediaTypes.HAL_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HTTP_PROBLEM_DETAILS_JSON_VALUE))
                .andExpect(jsonPath("$.title", is("Not Found")))
                .andExpect(jsonPath("$.detail", is("Order 1 not found")))
                .andReturn();
    }

    @Test
    public void updateNonExistingOrderShouldThrowOrderNotFoundException() throws Exception {

        given(repository.findById(1L)).willReturn(
                java.util.Optional.empty()
        );

        mvc.perform(put("/orders/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "    \"firstName\": \"Marie\",\n" +
                                "    \"lastName\": \"Curie\",\n" +
                                "    \"address\": {\n" +
                                "        \"address1\": \"4545 Wilshire Blvd\",\n" +
                                "        \"address2\": \"Apt 3\",\n" +
                                "        \"city\": \"Los Angeles\",\n" +
                                "        \"state\": \"CA\",\n" +
                                "        \"zip\": \"90025\"\n" +
                                "    },\n" +
                                "    \"orderLines\": [\n" +
                                "        {\n" +
                                "            \"brand\": \"LG\",\n" +
                                "            \"model\": \"Phone\",\n" +
                                "            \"cost\": \"1200\",\n" +
                                "            \"quantity\": 1\n" +
                                "        }\n" +
                                "    ],\n" +
                                "    \"tax\": \"100\",\n" +
                                "    \"shipping\": \"200\",\n" +
                                "    \"total\": \"1500\"\n" +
                                "}")
                        .accept(MediaTypes.HAL_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HTTP_PROBLEM_DETAILS_JSON_VALUE))
                .andExpect(jsonPath("$.title", is("Not Found")))
                .andExpect(jsonPath("$.detail", is("Order 1 not found")))
                .andReturn();
    }
}
