package com.examTecnico.Back.Service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.examTecnico.Back.Dto.OrderStatusDTO;
import com.examTecnico.Back.Models.Order;
import com.examTecnico.Back.Repository.OrderRepository;

@ExtendWith(MockitoExtension.class) // Usamos Mockito para simular dependencias sin levantar la base de datos real
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository; // Simulamos (Mock) el repositorio

    @InjectMocks
    private OrderService orderService; // Inyectamos el mock en nuestro servicio

    private Order testOrder;
    private UUID testOrderId;

    @BeforeEach
    void setUp() {
        // Esto se ejecuta antes de cada prueba para preparar datos limpios
        testOrderId = UUID.randomUUID();
        testOrder = new Order();
        testOrder.setId(testOrderId);
        testOrder.setOrigin("Origen A");
        testOrder.setDestination("Destino B");
    }

    // Estructura básica de un test: Arrange (Preparar), Act (Actuar), Assert (Afirmar/Verificar)

    @Test
    void createOrder_DebeAsignarEstadoCreatedYGuardar() {
        // 1. Arrange (Preparar): Le decimos al mock qué hacer cuando se llame
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        // 2. Act (Actuar): Ejecutamos el método que queremos probar
        Order savedOrder = orderService.createOrder(testOrder);

        // 3. Assert (Verificar): Comprobamos que el resultado sea el esperado
        assertEquals(OrderStatusDTO.CREATED, savedOrder.getStatus(), "El estado debe ser CREATED");
        verify(orderRepository, times(1)).save(testOrder); // Verificamos que se haya guardado 1 vez
    }

    @Test
    void getOrder_DebeRetornarOrden_CuandoLaOrdenExiste() {
        // Arrange
        when(orderRepository.findById(testOrderId)).thenReturn(Optional.of(testOrder));

        // Act
        Order result = orderService.getOrder(testOrderId);

        // Assert
        assertNotNull(result);
        assertEquals(testOrderId, result.getId());
    }

    @Test
    void getOrder_DebeLanzarExcepcion_CuandoNoExiste() {
        // Arrange
        when(orderRepository.findById(testOrderId)).thenReturn(Optional.empty());

        // Act & Assert (Se verifica que lance el error esperado)
        Exception exception = assertThrows(RuntimeException.class, () -> {
            orderService.getOrder(testOrderId);
        });
        
        assertEquals("Orden no encontrada", exception.getMessage());
    }

    @Test
    void changeStatus_DebeActualizarEstado_CuandoEsTransicionValida() {
        // Arrange: CREATED -> IN_TRANSIT es válido
        testOrder.setStatus(OrderStatusDTO.CREATED);
        when(orderRepository.findById(testOrderId)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        // Act
        Order updatedOrder = orderService.changeStatus(testOrderId, OrderStatusDTO.IN_TRANSIT);

        // Assert
        assertEquals(OrderStatusDTO.IN_TRANSIT, updatedOrder.getStatus());
        verify(orderRepository).save(testOrder); // Verifica que se guardó el cambio
    }

    @Test
    void changeStatus_DebeLanzarExcepcion_CuandoEsTransicionInvalida() {
        // Arrange: CREATED -> DELIVERED NO es válido según isValidStatusTransition
        testOrder.setStatus(OrderStatusDTO.CREATED);
        when(orderRepository.findById(testOrderId)).thenReturn(Optional.of(testOrder));

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            orderService.changeStatus(testOrderId, OrderStatusDTO.DELIVERED);
        });

        assertTrue(exception.getMessage().contains("Transición de estado inválida"));
        verify(orderRepository, never()).save(any(Order.class)); // Verificamos que NUNCA se guardó
    }
}
