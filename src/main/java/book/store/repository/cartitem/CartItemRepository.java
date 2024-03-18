package book.store.repository.cartitem;

import book.store.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    @Modifying
    @Query("update CartItem ci set ci.isDeleted = true where ci.shoppingCart.id = :cartId")
    void deleteAllByCartId(Long cartId);
}
