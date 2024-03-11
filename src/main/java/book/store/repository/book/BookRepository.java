package book.store.repository.book;

import book.store.model.Book;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {
    @Query(value = "SELECT * FROM books b " +
            "JOIN books_categories bc ON b.id = bc.book_id " +
            "WHERE bc.category_id = :categoryId",
            countQuery = "SELECT count(*) FROM books b " +
                    "JOIN books_categories bc ON b.id = bc.book_id " +
                    "WHERE bc.category_id = :categoryId",
            nativeQuery = true)
    Page<Book> findAllByCategoryId(Long categoryId, Pageable pageable);

    @Query("from Book b join fetch b.categories where b.id = :id")
    Optional<Book> findByIdWithCategories(Long id);

    @Query(
            value = """
        select b
        from Book b
        left join fetch b.categories
        """,
            countQuery = """
        select count(b)
        from Book b
        """
    )
    Page<Book> findAllWithCategories(Pageable pageable);
}
