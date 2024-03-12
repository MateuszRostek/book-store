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

    @Query("select b from Book b join fetch b.categories c where c.id = :categoryId")
    Page<Book> findAllByCategoryId(Long categoryId, Pageable pageable);

    @Query("from Book b join fetch b.categories where b.id = :id")
    Optional<Book> findByIdWithCategories(Long id);

    @Query("select b from Book b left join fetch b.categories")
    Page<Book> findAllWithCategories(Pageable pageable);
}
