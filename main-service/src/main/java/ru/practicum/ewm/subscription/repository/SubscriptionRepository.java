package ru.practicum.ewm.subscription.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.subscription.model.Subscription;

import java.util.List;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    List<Subscription> findByUserId(long userId);

    @Query("select e from Event e " +
            "inner join Request r on r.eventId = e.id " +
            "inner join Subscription s on s.friend.id = r.userId " +
            "where r.status = ru.practicum.request.type.RequestStatus.CONFIRMED " +
            "and e.state = ru.practicum.event.type.EventState.PUBLISHED " +
            "and s.user.id = :userId and e.eventDate > current_date " +
            "order by e.eventDate")
    List<Event> findEventsByUserId(Long userId);
}