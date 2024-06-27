package com.paymentgateway.service;

import com.paymentgateway.dto.StudentOrder;
import com.paymentgateway.repository.StudentOrderRepo;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class StudentService {

    @Autowired
    private StudentOrderRepo studentOrderRepo;

    @Value("${razorpay.key.id}")
    private String razorpayKey;

    @Value("${razorpay.secret.key}")
    private String razorpaySecret;

    private RazorpayClient client;


    public StudentOrder createOrder(StudentOrder studentOrder) throws Exception{

        JSONObject orderReq =new JSONObject();

        orderReq.put("amount", studentOrder.getAmount() * 100); //amount in paisa
        orderReq.put("currency", "INR");
        orderReq.put("receipt", studentOrder.getEmail());
        this.client =new RazorpayClient(razorpayKey, razorpaySecret);

//        create order in razorpay

        Order razorPayOrder= client.orders.create(orderReq);

        System.out.println(razorPayOrder);

        studentOrder.setRazorpayOrderId(razorPayOrder.get("id"));
        studentOrder.setOrderStatus(razorPayOrder.get("status"));

        studentOrderRepo.save(studentOrder);
        return studentOrder;
    }

    public StudentOrder updateOrder(Map<String, String> responsePayLoad){
       String razaorPayOrderId = responsePayLoad.get("razorpay_order_id");

       StudentOrder order = studentOrderRepo.findByRazorpayOrderId(razaorPayOrderId);
       order.setOrderStatus("PAYMENT_COMPLETED");
       StudentOrder updatedOrder= studentOrderRepo.save(order);
       return updatedOrder;
    }


}
