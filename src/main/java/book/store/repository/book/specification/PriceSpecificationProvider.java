package book.store.repository.book.specification;

import book.store.model.Book;
import book.store.repository.SpecificationProvider;
import java.math.BigDecimal;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class PriceSpecificationProvider implements SpecificationProvider<Book> {
    private static final String FIELD_NAME = "price";

    @Override
    public Specification<Book> getSpecification(String[] params) {
        BigDecimal minPrice = params[0] != null
                ? new BigDecimal(params[0])
                : BigDecimal.ZERO;
        BigDecimal maxPrice = params[1] != null
                ? new BigDecimal(params[1])
                : BigDecimal.valueOf(Double.MAX_VALUE);
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.between(root.get(FIELD_NAME), minPrice, maxPrice);
    }

    @Override
    public String getKey() {
        return FIELD_NAME;
    }
}
