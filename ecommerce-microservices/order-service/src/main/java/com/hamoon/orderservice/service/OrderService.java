package com.hamoon.orderservice.service;

import com.hamoon.orderservice.dto.InventoryResponse;
import com.hamoon.orderservice.dto.OrderItemDto;
import com.hamoon.orderservice.dto.OrderRequest;
import com.hamoon.orderservice.event.OrderPlacedEvent;
import com.hamoon.orderservice.model.Order;
import com.hamoon.orderservice.model.OrderItem;
import com.hamoon.orderservice.repository.OrderRepository;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final WebClient.Builder webClientBuilder;
    private final ObservationRegistry observationRegistry;
    private final ApplicationEventPublisher applicationEventPublisher;
    public Order placeOrder (OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        List<OrderItem> orderItemList= orderRequest.getOrderItemDtoList()
                .stream()
                .map(this::dtoToOrderItem)
                .toList();
        order.setOrderItemList(orderItemList);

        List<String> skuCodes = orderItemList.stream()
                .map(OrderItem::getSkuCode)
                .toList();
        log.info("Calling inventory service");
        Observation inventoryObservation = Observation.createNotStarted("inventory-service-lookup",
                this.observationRegistry);
        return inventoryObservation.observe(() -> {
            InventoryResponse[] inventoryResponses = webClientBuilder.build().get()
                    .uri("http://inventory-service/api/inventory",
                            uriBuilder -> uriBuilder.queryParam("skuCode", skuCodes).build())
                    .retrieve()
                    .bodyToMono(InventoryResponse[].class)
                    .block();

            boolean allProductsInStock = Arrays.stream(inventoryResponses)
                    .allMatch(InventoryResponse::isInStock);

            if (allProductsInStock) {
                applicationEventPublisher.publishEvent(new OrderPlacedEvent(this, order.getOrderNumber()));
                return orderRepository.save(order);
            }
            throw new RuntimeException("Item is not available or not in stock");
        });
    }

    private OrderItem dtoToOrderItem(OrderItemDto orderItemDto) {
        OrderItem orderItem = new OrderItem();
        orderItem.setQuantity(orderItemDto.getQuantity());
        orderItem.setPrice(orderItemDto.getPrice());
        orderItem.setSkuCode(orderItemDto.getSkuCode());
        return orderItem;
    }

}
