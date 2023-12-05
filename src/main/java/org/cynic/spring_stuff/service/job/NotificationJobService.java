package org.cynic.spring_stuff.service.job;

import org.cynic.spring_stuff.Constants;
import org.cynic.spring_stuff.mapper.NotificationMapper;
import org.cynic.spring_stuff.repository.ItemOrderPriceRepository;
import org.cynic.spring_stuff.repository.NotificationRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.Optional;

@Component
public class NotificationJobService {
    private final NotificationRepository notificationRepository;
    private final ItemOrderPriceRepository itemOrderPriceRepository;
    private final NotificationMapper notificationMapper;
    private final Clock clock;

    public NotificationJobService(NotificationRepository notificationRepository, ItemOrderPriceRepository itemOrderPriceRepository, NotificationMapper notificationMapper, Clock clock) {
        this.notificationRepository = notificationRepository;
        this.itemOrderPriceRepository = itemOrderPriceRepository;
        this.notificationMapper = notificationMapper;
        this.clock = clock;
    }

    @Transactional
    @Scheduled(cron = "${job.notifications.cron}")
    public void createNotifications() {
        itemOrderPriceRepository.findAll(ItemOrderPriceRepository.byNotCoveredAndDueDateTimeIsBeforeAndNotificationIsMissing(calculateDueOffsetDateTime()))
                .stream()
                .map(it -> notificationMapper.toEntity(OffsetDateTime.now(clock), it))
                .flatMap(Optional::stream)
                .forEach(notificationRepository::save);
    }

    private OffsetDateTime calculateDueOffsetDateTime() {
        return OffsetDateTime.now(clock).plus(Constants.BEFORE_DUE_DAY_EXPIRATION);
    }
}
