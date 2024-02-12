package book.store.repository.book;

import book.store.model.Book;
import book.store.repository.SpecificationProvider;
import book.store.repository.SpecificationProviderManager;
import org.springframework.stereotype.Component;

@Component
public class BookSpecificationProviderManager implements SpecificationProviderManager<Book> {
    @Override
    public SpecificationProvider<Book> getSpecificationProvider(String key) {
        return null;
    }
}
