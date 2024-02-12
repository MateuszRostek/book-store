package book.store.repository.book;

import book.store.dto.BookSearchParametersDto;
import book.store.model.Book;
import book.store.repository.SpecificationBuilder;
import book.store.repository.SpecificationProviderManager;
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
        if (searchParametersDto.titles() != null && searchParametersDto.titles().length > 0) {
            specification = specification.and(
                    specificationProviderManager.getSpecificationProvider("title")
                            .getSpecification(searchParametersDto.titles()));
        }
        if (searchParametersDto.authors() != null && searchParametersDto.authors().length > 0) {
            specification = specification.and(
                    specificationProviderManager.getSpecificationProvider("author")
                            .getSpecification(searchParametersDto.authors()));
        }
        if (searchParametersDto.prices() != null && searchParametersDto.prices().length > 0) {
            specification = specification.and(
                    specificationProviderManager.getSpecificationProvider("price")
                            .getSpecification(searchParametersDto.prices()));
        }
        return specification;
    }
}
