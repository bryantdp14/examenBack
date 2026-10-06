package com.examTecnico.Back.Controller;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.examTecnico.Back.Dto.OrderStatusDTO;
import com.examTecnico.Back.Models.Order;
import com.examTecnico.Back.Service.OrderService;

import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody Order order) {
        return ResponseEntity.ok(orderService.createOrder(order));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrder(@PathVariable UUID id) {
        return ResponseEntity.ok(orderService.getOrder(id));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Order> changeStatus(@PathVariable UUID id, @RequestParam OrderStatusDTO status) {
        return ResponseEntity.ok(orderService.changeStatus(id, status));
    }

    @PostMapping("/{orderId}/assign/{driverId}")
    public ResponseEntity<?> assignDriver(@PathVariable UUID orderId, @PathVariable UUID driverId,
            @RequestParam("pdf") MultipartFile pdf, @RequestParam("image") MultipartFile image) {
        String pdfPath = "uploads/" + pdf.getOriginalFilename();
        String imagePath = "uploads/" + image.getOriginalFilename();

        return ResponseEntity.ok(orderService.assignDriver(orderId, driverId, pdfPath, imagePath));
    }
}