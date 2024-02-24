package fastfood.order.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.Date;

import org.springframework.beans.BeanUtils;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.support.MessageBuilder;
import fastfood.order.OrderApplication;
import fastfood.order.events.OrderPlaced;

@Entity
@Table(name = "Order_table")
@Getter
@Setter
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userId;
    private Long menuId;
    private String menuName;
    private Integer qty;
    private String address;
    private String status;
    @Temporal(TemporalType.TIMESTAMP)
    private Date orderDt;

    @PostPersist
    public void onPostPersist() {
        StreamBridge streamBridge = OrderApplication.applicationContext.getBean(StreamBridge.class);
        BeanUtils.copyProperties(this, OrderPlaced.class);

            streamBridge.send("producer-out-0", MessageBuilder
            .withPayload(OrderPlaced.class)
            .setHeader("type", OrderPlaced.class.getSimpleName())
            .build()
         );
    }
}