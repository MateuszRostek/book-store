package book.store.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "cart_items")
@Where(clause = "is_deleted=false")
@SQLDelete(sql = "UPDATE cart_items SET is_deleted = true WHERE id=?")
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @ManyToOne
    @JoinColumn(name = "shopping_cart_id", nullable = false)
    private ShoppingCart shoppingCart;
    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;
    @Column(name = "quantity", nullable = false)
    private int quantity;
    @Column(name = "is_deleted")
    private boolean isDeleted = false;
}
