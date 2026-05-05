package com.foodapp.fooddelivery.notification;

import com.foodapp.fooddelivery.entity.Delivery;
import com.foodapp.fooddelivery.entity.Order;
import com.foodapp.fooddelivery.notification.dto.OrderStatusEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderNotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    public void publishOrderUpdate(Order order, String message) {
        Delivery delivery = order.getDelivery();
        OrderStatusEvent event = OrderStatusEvent.builder()
                .orderId(order.getId())
                .customerId(order.getUser().getId())
                .vendorId(order.getRestaurant().getOwner().getId())
                .restaurantId(order.getRestaurant().getId())
                .orderStatus(order.getStatus())
                .paymentStatus(order.getPaymentStatus())
                .deliveryStatus(delivery != null ? delivery.getStatus() : null)
                .message(message)
                .updatedAt(order.getUpdatedAt())
                .build();

        messagingTemplate.convertAndSend("/topic/orders/" + order.getId(), event);
        messagingTemplate.convertAndSend("/topic/restaurants/" + order.getRestaurant().getId() + "/orders", event);
        messagingTemplate.convertAndSend("/topic/users/" + order.getUser().getId() + "/orders", event);
        messagingTemplate.convertAndSend("/topic/users/" + order.getRestaurant().getOwner().getId() + "/orders", event);
        messagingTemplate.convertAndSendToUser(order.getUser().getEmail(), "/queue/orders", event);
        messagingTemplate.convertAndSendToUser(order.getRestaurant().getOwner().getEmail(), "/queue/orders", event);

        if (delivery != null) {
            messagingTemplate.convertAndSend("/topic/users/" + delivery.getDeliveryPartner().getId() + "/orders", event);
            messagingTemplate.convertAndSendToUser(delivery.getDeliveryPartner().getEmail(), "/queue/orders", event);
        }
    }
}
