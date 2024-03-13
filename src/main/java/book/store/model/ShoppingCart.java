package book.store.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "shopping_carts")
@Where(clause = "is_deleted=false")
@SQLDelete(sql = "UPDATE shopping_carts SET is_deleted = true WHERE id=?")
public class ShoppingCart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @OneToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;
    @OneToMany(mappedBy = "shoppingCart", cascade = CascadeType.ALL)
    private Set<CartItem> cartItems = new HashSet<>();
    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;
}
