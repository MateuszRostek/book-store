package book.store.mapper;

import book.store.config.MapperConfig;
import book.store.dto.book.BookDto;
import book.store.dto.book.BookDtoWithoutCategoryIds;
import book.store.dto.book.CreateBookRequestDto;
import book.store.model.Book;
import book.store.model.Category;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(config = MapperConfig.class)
public interface BookMapper {
    @Mapping(target = "categoryIds", source = "categories", qualifiedByName = "getIdsFromCategories")
    BookDto toDto(Book book);

    @Mapping(target = "categories", source = "categoryIds", qualifiedByName = "getCategoriesFromIds")
    Book toModel(CreateBookRequestDto createBookRequestDto);

    BookDtoWithoutCategoryIds toDtoWithoutCategories(Book book);

    @Named("getIdsFromCategories")
    default Set<Long> getIdsFromCategories(Set<Category> categories) {
        if (categories == null) {
            return null;
        }
        return categories.stream()
                .map(Category::getId)
                .collect(Collectors.toSet());
    }

    @Named("getCategoriesFromIds")
    default Set<Category> getCategoriesFromIds(Set<Long> ids) {
        if (ids == null) {
            return null;
        }
        return ids.stream()
                .map(Category::new)
                .collect(Collectors.toSet());
    }
}
