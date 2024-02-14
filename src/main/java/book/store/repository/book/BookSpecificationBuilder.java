package book.store.repository.book;

import book.store.dto.BookSearchParametersDto;
import book.store.model.Book;
import book.store.repository.SpecificationBuilder;
import book.store.repository.SpecificationProviderManager;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BookSpecificationBuilder implements SpecificationBuilder<Book> {
    private final SpecificationProviderManager<Book> specificationProviderManager;

    @Override
    public Specification<Book> build(BookSearchParametersDto searchParametersDto) {
        Specification<Book> specification = Specification.where(null);
        Map<String, String[]> parametersMap = new HashMap<>();
        parametersMap.put("title", searchParametersDto.titles());
        parametersMap.put("author", searchParametersDto.authors());
        parametersMap.put("price",
                new String[]{searchParametersDto.minPrice(), searchParametersDto.maxPrice()});

        for (Map.Entry<String, String[]> entry : parametersMap.entrySet()) {
            String[] paramValues = entry.getValue();
            if (paramValues != null && paramValues.length > 0) {
                specification = specification.and(
                        specificationProviderManager.getSpecificationProvider(entry.getKey())
                                .getSpecification(paramValues));
            }
        }
        return specification;
    }
}
