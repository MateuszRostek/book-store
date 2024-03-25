package book.store.repository.book;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import book.store.model.Book;
import book.store.model.Category;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class BookRepositoryTest {
    @Autowired
    private BookRepository bookRepository;

    @Test
    @DisplayName("Find all books by valid category ID")
    @Sql(scripts = {"classpath:database/books/add-three-books-with-categories.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"classpath:database/books/remove-all-books-with-categories.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findAllByCategoryId_ValidCategoryId_ReturnsTwoBooks() {
        Long validCategoryId = 1L;
        List<Book> actual = bookRepository.findAllByCategoryId(
                        validCategoryId,
                        PageRequest.of(0, 10))
                .stream()
                .toList();
        assertEquals(2, actual.size());
    }

    @Test
    @DisplayName("Find all books by invalid category ID")
    @Sql(scripts = {"classpath:database/books/add-three-books-with-categories.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"classpath:database/books/remove-all-books-with-categories.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findAllByCategoryId_InvalidCategoryId_ReturnsEmptyListOfBooks() {
        Long notValidCategoryId = 10L;
        List<Book> expected = new ArrayList<>();
        List<Book> actual = bookRepository.findAllByCategoryId(
                        notValidCategoryId,
                        PageRequest.of(0, 10))
                .stream()
                .toList();
        assertEquals(0, actual.size());
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Find book by valid ID with categories")
    @Sql(scripts = {"classpath:database/books/add-three-books-with-categories.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"classpath:database/books/remove-all-books-with-categories.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findByIdWithCategories_ValidBookId_ReturnsOneBook() {
        Category expectedCategory = new Category();
        expectedCategory.setId(1L);
        expectedCategory.setName("Thriller");
        expectedCategory.setDescription("Thriller category");
        Long validId = 1L;
        Book expected = new Book();
        expected.setId(validId);
        expected.setTitle("Basic Book");
        expected.setAuthor("Basic Author");
        expected.setIsbn("9911-123");
        expected.setPrice(new BigDecimal("20.50"));
        expected.setDescription("Basic Description");
        expected.setCoverImage("images.com/image.jpg");
        expected.setCategories(Set.of(expectedCategory));

        Optional<Book> actual = bookRepository.findByIdWithCategories(validId);

        assertFalse(actual.isEmpty());
        assertThat(actual.get()).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    @DisplayName("Find book by invalid ID with categories")
    @Sql(scripts = {"classpath:database/books/add-three-books-with-categories.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"classpath:database/books/remove-all-books-with-categories.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findByIdWithCategories_InvalidBookId_ReturnsEmptyOptional() {
        Long notValidId = 100L;
        Optional<Book> expected = Optional.empty();
        Optional<Book> actual = bookRepository.findByIdWithCategories(notValidId);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Find all books with categories")
    @Sql(scripts = {"classpath:database/books/add-three-books-with-categories.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"classpath:database/books/remove-all-books-with-categories.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findAllWithCategories_ThreeBooksInDB_ReturnsThreeBooks() {
        List<Book> actualList = bookRepository.findAllWithCategories(
                PageRequest.of(0, 10)).stream()
                .toList();
        assertEquals(3, actualList.size());
    }
}
