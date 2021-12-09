package com.goviesco.orders;

import com.goviesco.orders.controller.OrderController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

@SpringBootTest // Spring boot application is started inside the test.
class OrdersApplicationTests {

	@Autowired
	OrderController orderController;

	@Test
	void contextLoads() {
		Assert.notNull(orderController, "orderController is null.");
	}

}
