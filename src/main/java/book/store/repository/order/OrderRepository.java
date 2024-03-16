package book.store.repository.order;

import book.store.model.Order;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("from Order o "
            + "left join fetch o.orderItems "
            + "join fetch o.user u "
            + "where u.id = :userId")
    List<Order> findAllByUserId(Long userId);

    @Query("from Order o "
            + "left join fetch o.orderItems "
            + "join fetch o.user u "
            + "where o.id = :orderId")
    Optional<Order> findByIdWithItems(Long orderId);
}
