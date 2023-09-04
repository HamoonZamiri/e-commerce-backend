package com.hamoon;

import lombok.*;
import org.springframework.context.ApplicationEvent;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderPlacedEvent {
    private String orderNumber;
}

