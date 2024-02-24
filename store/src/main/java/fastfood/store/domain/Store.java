package fastfood.store.domain;

import jakarta.persistence.*;
import lombok.*;
import java.util.Date;

import org.springframework.beans.BeanUtils;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.support.MessageBuilder;

import fastfood.store.StoreApplication;
import fastfood.store.events.CookStarted;

@Entity
@Table(name = "stores")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Store {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long orderId;
    private String userId;
    private Long menuId;
    private String menuName;
    private Integer qty;
    private String address;
    private String status;

    @Temporal(TemporalType.TIMESTAMP)
    private Date cookingDt;

    @PostUpdate
    public void onPostUpdate() {
        StreamBridge streamBridge = StoreApplication.applicationContext.getBean(StreamBridge.class);
        BeanUtils.copyProperties(this, CookStarted.class);

            streamBridge.send("producer-out-0", MessageBuilder
            .withPayload(CookStarted.class)
            .setHeader("type", CookStarted.class.getSimpleName())
            .build()
         );
    }    
}