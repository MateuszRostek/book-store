package book.store.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import book.store.dto.cartitem.CartItemDto;
import book.store.dto.cartitem.CreateCartItemRequestDto;
import book.store.dto.cartitem.UpdateCartItemRequestDto;
import book.store.dto.shoppingcart.ShoppingCartDto;
import book.store.model.Book;
import book.store.model.CartItem;
import book.store.model.Category;
import book.store.model.Role;
import book.store.model.ShoppingCart;
import book.store.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ShoppingCartControllerTest {
    protected static MockMvc mockMvc;
    private static final String BASE_ENDPOINT = "/api/cart";
    private static final String SAMPLE_USER_EMAIL = "john@basic.com";

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll(
            @Autowired DataSource dataSource,
            @Autowired WebApplicationContext applicationContext) throws SQLException {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
        teardown(dataSource);
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource(
                            "database/shoppingcart/add_shopping_cart_with_necessities.sql"));
        }
    }

    @AfterAll
    static void afterAll(@Autowired DataSource dataSource) {
        teardown(dataSource);
    }

    @SneakyThrows
    static void teardown(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource(
                            "database/shoppingcart/delete_shopping_cart_with_necessities.sql"));
        }
    }

    @WithUserDetails(SAMPLE_USER_EMAIL)
    @Test
    void getCartWithItems_ValidUser_ReturnsShoppingCartDto() throws Exception {
        long validUserId = 10L;
        ShoppingCart modelCart = getTestShoppingCart(validUserId);
        ShoppingCartDto expected = getTestShoppingCartDtoFromModel(modelCart);

        MvcResult result = mockMvc.perform(
                        get(BASE_ENDPOINT).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        ShoppingCartDto actual = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(), ShoppingCartDto.class);

        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @WithUserDetails(SAMPLE_USER_EMAIL)
    @Test
    @Sql(scripts = {"classpath:database/books/add-three-books-with-categories.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"classpath:database/shoppingcart/delete_shopping_cart_with_necessities.sql",
            "classpath:database/shoppingcart/add_shopping_cart_with_necessities.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void addItemToCart_ValidUserAndRequestDto_ReturnsUpdatedShoppingCartDto() throws Exception {
        long validUserId = 10L;
        Long nonExistingCartItemBookId = 2L;
        CreateCartItemRequestDto requestDto = new CreateCartItemRequestDto(
                nonExistingCartItemBookId, 2);
        ShoppingCart modelCart = getTestShoppingCart(validUserId);
        CartItem modelItem = getTestCartItemFromRequest(modelCart, requestDto);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        ShoppingCartDto expected = getTestShoppingCartDtoFromModel(modelCart);
        expected.cartItems().add(new CartItemDto(
                        modelItem.getId(),
                        modelItem.getBook().getId(),
                        modelItem.getBook().getTitle(),
                        requestDto.quantity()));

        MvcResult result = mockMvc.perform(
                        post(BASE_ENDPOINT)
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();
        ShoppingCartDto actual = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(), ShoppingCartDto.class);

        assertNotNull(actual.id());
        EqualsBuilder.reflectionEquals(expected, actual, "id");
    }

    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    @Test
    @Sql(scripts = {"classpath:database/shoppingcart/delete_shopping_cart_with_necessities.sql",
            "classpath:database/shoppingcart/add_shopping_cart_with_necessities.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void updateItemQuantity_ValidCartItemIdAndRequestDto_ReturnsUpdatedCartItemDto()
            throws Exception {
        long validCartItemId = 10L;
        UpdateCartItemRequestDto requestDto = new UpdateCartItemRequestDto(10);
        CartItem modelItem = getTestShoppingCart(1L).getCartItems().stream()
                .findFirst()
                .get();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        CartItemDto expected = new CartItemDto(
                modelItem.getId(),
                modelItem.getBook().getId(),
                modelItem.getBook().getTitle(),
                modelItem.getQuantity() + requestDto.quantity());

        MvcResult result = mockMvc.perform(
                        put(BASE_ENDPOINT + "/cart-items/" + validCartItemId)
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        CartItemDto actual = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(), CartItemDto.class);

        assertNotNull(actual.id());
        EqualsBuilder.reflectionEquals(expected, actual, "id");
    }

    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    @Test
    @Sql(scripts = {"classpath:database/shoppingcart/delete_shopping_cart_with_necessities.sql",
            "classpath:database/shoppingcart/add_shopping_cart_with_necessities.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void deleteItemFromCart_ValidCartItemId_ReturnsNoContentStatus() throws Exception {
        long validCartItemId = 2L;
        mockMvc.perform(delete(BASE_ENDPOINT + "/cart-items/" + validCartItemId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andReturn();
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

        HashSet<CartItem> cartItemHashSet = new HashSet<>();
        cartItemHashSet.add(cartItem);
        expected.setCartItems(cartItemHashSet);
        return expected;
    }

    private ShoppingCartDto getTestShoppingCartDtoFromModel(ShoppingCart shoppingCart) {
        Set<CartItem> cartItems = shoppingCart.getCartItems();
        Set<CartItemDto> cartItemDtoSet = new HashSet<>();
        for (CartItem item : cartItems) {
            cartItemDtoSet.add(
                    new CartItemDto(
                            item.getId(),
                            item.getBook().getId(),
                            item.getBook().getTitle(),
                            item.getQuantity()));
        }
        return new ShoppingCartDto(
                shoppingCart.getId(),
                shoppingCart.getUser().getId(),
                cartItemDtoSet);
    }

    private CartItem getTestCartItemFromRequest(
            ShoppingCart shoppingCart, CreateCartItemRequestDto requestDto) {
        CartItem cartItem = new CartItem();
        cartItem.setId(2L);
        cartItem.setShoppingCart(shoppingCart);
        Book book = new Book(requestDto.bookId());
        book.setTitle("Basic Book 2");
        book.setAuthor("Basic Author 2");
        book.setIsbn("9911-124");
        book.setPrice(new BigDecimal("25.50"));
        book.setDescription("Basic Description 2");
        book.setCoverImage("images.com/image2.jpg");
        cartItem.setBook(book);
        cartItem.setQuantity(requestDto.quantity());
        return cartItem;
    }
}
