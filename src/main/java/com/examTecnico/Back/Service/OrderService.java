package com.examTecnico.Back.Service;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.examTecnico.Back.Dto.OrderStatusDTO;
import com.examTecnico.Back.Models.Driver;
import com.examTecnico.Back.Models.Order;
import com.examTecnico.Back.Models.OrderAssignment;
import com.examTecnico.Back.Repository.DriverRepository;
import com.examTecnico.Back.Repository.OrderAssignmentRepository;
import com.examTecnico.Back.Repository.OrderRepository;
import com.examTecnico.Back.Service.Interfaces.IOrderService;

import java.util.List;
import java.util.UUID;

@Service
public class OrderService implements IOrderService {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private DriverRepository driverRepository;
    @Autowired
    private OrderAssignmentRepository assignmentRepository;

    @Override
    @Transactional
    public Order createOrder(Order order) {
        order.setStatus(OrderStatusDTO.CREATED);
        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public Order changeStatus(UUID id, OrderStatusDTO newStatus) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada"));

        if (!isValidStatusTransition(order.getStatus(), newStatus)) {
            throw new RuntimeException("Transición de estado inválida de " + order.getStatus() + " a " + newStatus);
        }
        order.setStatus(newStatus);
        return orderRepository.save(order);
    }

    private boolean isValidStatusTransition(OrderStatusDTO current, OrderStatusDTO next) {
        if (current == OrderStatusDTO.CREATED
                && (next == OrderStatusDTO.IN_TRANSIT || next == OrderStatusDTO.CANCELLED)) {
            return true;
        }
        if (current == OrderStatusDTO.IN_TRANSIT
                && (next == OrderStatusDTO.DELIVERED || next == OrderStatusDTO.CANCELLED)) {
            return true;
        }
        return false;
    }

    @Override
    @Transactional(readOnly = true)
    public Order getOrder(UUID id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada"));
    }

    @Override
    @Transactional
    public OrderAssignment assignDriver(UUID orderId, UUID driverId, String pdfPath, String imagePath) {
        Order order = getOrder(orderId);
        if (order.getStatus() != OrderStatusDTO.CREATED) {
            throw new RuntimeException("La orden debe estar en estado CREATED");
        }

        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Conductor no encontrado"));

        if (!driver.isActive()) {
            throw new RuntimeException("El conductor no está activo");
        }

        OrderAssignment assignment = new OrderAssignment();
        assignment.setOrder(order);
        assignment.setDriver(driver);
        assignment.setPdfPath(pdfPath);
        assignment.setImagePath(imagePath);

        order.setStatus(OrderStatusDTO.IN_TRANSIT);
        orderRepository.save(order);

        return assignmentRepository.save(assignment);
    }

}