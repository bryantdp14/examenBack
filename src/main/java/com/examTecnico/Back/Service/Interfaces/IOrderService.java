package com.examTecnico.Back.Service.Interfaces;

import java.util.UUID;
import com.examTecnico.Back.Models.Order;
import com.examTecnico.Back.Models.OrderAssignment;
import com.examTecnico.Back.Dto.OrderStatusDTO;

public interface IOrderService {
	   
	Order createOrder(Order order);
    Order changeStatus(UUID id, OrderStatusDTO newStatus);
    Order getOrder(UUID id);
    OrderAssignment assignDriver(UUID orderId, UUID driverId, String pdfPath, String imagePath);
}
