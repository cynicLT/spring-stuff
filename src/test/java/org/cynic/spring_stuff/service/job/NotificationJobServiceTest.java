package org.cynic.spring_stuff.service.job;

import org.cynic.spring_stuff.domain.entity.ItemOrderPrice;
import org.cynic.spring_stuff.domain.entity.Notification;
import org.cynic.spring_stuff.mapper.NotificationMapper;
import org.cynic.spring_stuff.repository.ItemOrderPriceRepository;
import org.cynic.spring_stuff.repository.NotificationRepository;
import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.*;
import java.util.List;
import java.util.Optional;


@ExtendWith({
        InstancioExtension.class,
        MockitoExtension.class
})
@Tag("unit")
class NotificationJobServiceTest {
    private final Clock clock = Clock.fixed(ZonedDateTime.of(
                            LocalDateTime.of(2000, 1, 1, 0, 0, 0, 154000000),
                            Clock.system(ZoneId.systemDefault()).getZone()
                    )
                    .toInstant(),
            Clock.system(ZoneId.systemDefault()).getZone()
    );


    private NotificationJobService notificationJobService;

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private ItemOrderPriceRepository itemOrderPriceRepository;

    @Mock
    private NotificationMapper notificationMapper;

    @BeforeEach
    void setUp() {
        this.notificationJobService = new NotificationJobService(notificationRepository, itemOrderPriceRepository, notificationMapper, clock);
    }

    @Test
    void createNotifications() {
        ItemOrderPrice itemOrderPrice = Instancio.create(ItemOrderPrice.class);
        OffsetDateTime now = OffsetDateTime.now(clock);
        Mockito.when(itemOrderPriceRepository.findAll(Mockito.any()))
                .thenReturn(List.of(itemOrderPrice));
        Notification notification = Instancio.create(Notification.class);

        Mockito.when(notificationMapper.toEntity(now, itemOrderPrice))
                .thenReturn(Optional.of(notification));

        notificationJobService.createNotifications();

        Mockito.verify(itemOrderPriceRepository, Mockito.times(1)).findAll(Mockito.any());
        Mockito.verify(notificationMapper, Mockito.times(1)).toEntity(now, itemOrderPrice);
        Mockito.verify(notificationRepository, Mockito.times(1)).save(notification);
    }
}