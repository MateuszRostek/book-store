package book.store.repository.book.specification;

import book.store.model.Book;
import book.store.repository.SpecificationProvider;
import java.util.Arrays;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class PriceSpecificationProvider implements SpecificationProvider<Book> {
    private static final String FIELD_NAME = "price";

    @Override
    public Specification<Book> getSpecification(String[] params) {
        return (root, query, criteriaBuilder) ->
                root.get(FIELD_NAME).in(Arrays.stream(params).toArray());
    }

    @Override
    public String getKey() {
        return FIELD_NAME;
    }
}
