package book.store.dto.book;

public record BookSearchParametersDto(
        String[] titles,
        String[] authors,
        String minPrice,
        String maxPrice) {
}
