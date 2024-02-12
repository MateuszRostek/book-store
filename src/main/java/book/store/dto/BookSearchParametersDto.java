package book.store.dto;

public record BookSearchParametersDto(String[] titles, String[] authors, String[] prices) {
}
