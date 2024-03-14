package book.store.repository.shoppingcart;

import book.store.model.ShoppingCart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, Long> {
    @Query("select sc from ShoppingCart sc "
            + "left join fetch sc.cartItems "
            + "join fetch sc.user u ")
    ShoppingCart findCartWithItems(Long userId);
}
