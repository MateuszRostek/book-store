package book.store.repository.shoppingcart;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;

import book.store.model.Book;
import book.store.model.CartItem;
import book.store.model.Category;
import book.store.model.Role;
import book.store.model.ShoppingCart;
import book.store.model.User;
import java.math.BigDecimal;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ShoppingCartRepositoryTest {
    @Autowired
    private ShoppingCartRepository cartRepository;

    @Test
    @DisplayName("Find a shopping cart with items by valid user ID")
    @Sql(scripts = {"classpath:database/shoppingcart/add_shopping_cart_with_necessities.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"classpath:database/shoppingcart/delete_shopping_cart_with_necessities.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findCartWithItemsByUserId_ValidUserId_ReturnsShoppingCart() {
        long validUserId = 10L;
        ShoppingCart expected = getTestShoppingCart(validUserId);
        ShoppingCart actual = cartRepository.findCartWithItemsByUserId(validUserId);
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    @DisplayName("Find shopping cart with items by invalid user ID")
    @Sql(scripts = {"classpath:database/shoppingcart/add_shopping_cart_with_necessities.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"classpath:database/shoppingcart/delete_shopping_cart_with_necessities.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findCartWithItemsByUserId_InvalidUserId_ReturnsNull() {
        long invalidUserId = 1000L;
        ShoppingCart actual = cartRepository.findCartWithItemsByUserId(invalidUserId);
        assertNull(actual);
    }

    private ShoppingCart getTestShoppingCart(long userId) {
        Role role = new Role();
        role.setId(1L);
        role.setName(Role.RoleName.USER);

        User user = new User();
        user.setId(userId);
        user.setEmail("john@basic.com");
        user.setPassword("password");
        user.setFirstName("John");
        user.setLastName("Smith");
        user.setShippingAddress("123 Street, City, Country");
        user.setRoles(Set.of(role));

        Category category = new Category();
        category.setId(10L);
        category.setName("Thriller");
        category.setDescription("Thriller category");

        Book book = new Book();
        book.setId(10L);
        book.setTitle("Basic Book");
        book.setAuthor("Basic Author");
        book.setIsbn("9911-123");
        book.setPrice(new BigDecimal("20.50"));
        book.setDescription("Basic Description");
        book.setCoverImage("images.com/image.jpg");
        book.setCategories(Set.of(category));

        ShoppingCart expected = new ShoppingCart();
        expected.setId(10L);
        expected.setUser(user);

        CartItem cartItem = new CartItem();
        cartItem.setId(10L);
        cartItem.setShoppingCart(expected);
        cartItem.setBook(book);
        cartItem.setQuantity(2);

        expected.setCartItems(Set.of(cartItem));
        return expected;
    }
}
