package book.store.service.book;

import book.store.dto.book.BookDto;
import book.store.dto.book.BookDtoWithoutCategoryIds;
import book.store.dto.book.BookSearchParametersDto;
import book.store.dto.book.CreateBookRequestDto;
import book.store.exception.EntityNotFoundException;
import book.store.mapper.BookMapper;
import book.store.model.Book;
import book.store.model.Category;
import book.store.repository.book.BookRepository;
import book.store.repository.book.BookSpecificationBuilder;
import book.store.repository.book.specification.PriceSpecificationProvider;
import book.store.service.book.impl.BookServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {
    @Mock
    private BookRepository bookRepository;
    @Mock
    private BookMapper bookMapper;
    @Mock
    private BookSpecificationBuilder bookSpecificationBuilder;
    @InjectMocks
    private BookServiceImpl bookService;

    @Test
    @DisplayName("TODO!")
    void save_ValidCreateBookRequestDto_ReturnsBookDto() {
        CreateBookRequestDto requestDtoBook = new CreateBookRequestDto(
                "Basic Book",
                "Basic Author",
                "9211-123",
                new BigDecimal("15.80"),
                "Basic Description",
                "images.com/basic-book",
                Set.of(1L, 2L));

        Book modelBook = new Book();
        modelBook.setTitle(requestDtoBook.title());
        modelBook.setAuthor(requestDtoBook.author());
        modelBook.setIsbn(requestDtoBook.isbn());
        modelBook.setPrice(requestDtoBook.price());
        modelBook.setDescription(requestDtoBook.description());
        modelBook.setCoverImage(requestDtoBook.coverImage());

        BookDto bookDto = new BookDto();
        bookDto.setId(1L);
        bookDto.setTitle(requestDtoBook.title());
        bookDto.setAuthor(requestDtoBook.author());
        bookDto.setIsbn(requestDtoBook.isbn());
        bookDto.setPrice(requestDtoBook.price());
        bookDto.setDescription(requestDtoBook.description());
        bookDto.setCoverImage(requestDtoBook.coverImage());
        bookDto.setCategoryIds(requestDtoBook.categoryIds());

        when(bookMapper.toModel(requestDtoBook)).thenReturn(modelBook);
        when(bookRepository.save(modelBook)).thenReturn(modelBook);
        when(bookMapper.toDto(modelBook)).thenReturn(bookDto);

        BookDto actual = bookService.save(requestDtoBook);

        assertEquals(bookDto, actual);
        verify(bookRepository, times(1)).save(any());
        verify(bookMapper, times(1)).toDto(any());
        verify(bookMapper, times(1)).toModel(any());
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @Test
    @DisplayName("TODO!")
    void findAll_ValidPageable_ReturnsAllBookDtoWithoutCategoryIds() {
        Book modelBook = new Book();
        modelBook.setId(1L);
        modelBook.setTitle("Book title");
        modelBook.setAuthor("Author");
        modelBook.setIsbn("9211-123");
        modelBook.setPrice(new BigDecimal("11.70"));
        modelBook.setDescription("Description");
        modelBook.setCoverImage("image.com");
        modelBook.setCategories(Set.of(new Category(1L)));

        BookDtoWithoutCategoryIds bookDto = new BookDtoWithoutCategoryIds(
                modelBook.getId(),
                modelBook.getTitle(),
                modelBook.getAuthor(),
                modelBook.getIsbn(),
                modelBook.getPrice(),
                modelBook.getDescription(),
                modelBook.getCoverImage());

        Pageable pageable = PageRequest.of(0, 10);
        List<Book> books = List.of(modelBook);
        PageImpl<Book> booksPage = new PageImpl<>(books, pageable, books.size());

        when(bookRepository.findAll(pageable)).thenReturn(booksPage);
        when(bookMapper.toDtoWithoutCategories(modelBook)).thenReturn(bookDto);

        List<BookDtoWithoutCategoryIds> booksDtos = bookService.findAll(pageable);

        assertEquals(1, booksDtos.size());
        assertEquals(bookDto, booksDtos.get(0));
        verify(bookRepository, times(1)).findAll(pageable);
        verify(bookMapper, times(1)).toDtoWithoutCategories(any());
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @Test
    @DisplayName("TODO!")
    void findById_ValidId_ReturnsBookDtoWithoutCategoryIds() {
        Long validId = 1L;
        Book modelBook = new Book();
        modelBook.setId(validId);
        modelBook.setTitle("Book title");
        modelBook.setAuthor("Author");
        modelBook.setIsbn("9211-123");
        modelBook.setPrice(new BigDecimal("11.70"));
        modelBook.setDescription("Description");
        modelBook.setCoverImage("image.com");
        modelBook.setCategories(Set.of(new Category(1L)));

        BookDtoWithoutCategoryIds bookDto = new BookDtoWithoutCategoryIds(
                modelBook.getId(),
                modelBook.getTitle(),
                modelBook.getAuthor(),
                modelBook.getIsbn(),
                modelBook.getPrice(),
                modelBook.getDescription(),
                modelBook.getCoverImage());

        when(bookRepository.findById(validId)).thenReturn(Optional.of(modelBook));
        when(bookMapper.toDtoWithoutCategories(modelBook)).thenReturn(bookDto);

        BookDtoWithoutCategoryIds actualBookDto = bookService.findById(validId);
        assertEquals(bookDto, actualBookDto);
        verify(bookRepository, times(1)).findById(any());
        verify(bookMapper, times(1)).toDtoWithoutCategories(any());
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @Test
    @DisplayName("TODO!")
    void findById_InvalidId_ThrowsEntityNotFoundException() {
        Long invalidId = 10L;

        when(bookRepository.findById(invalidId)).thenReturn(Optional.empty());

        EntityNotFoundException exception =
                assertThrows(EntityNotFoundException.class, () -> bookService.findById(invalidId));

        String expected = "Can't find book with id: " + invalidId;
        String actual = exception.getMessage();
        assertEquals(expected, actual);
        verify(bookRepository, times(1)).findById(any());
        verifyNoMoreInteractions(bookRepository);
    }

    @Test
    @DisplayName("TODO!")
    void updateById_ValidIdAndCreateBookRequestDto_ReturnsUpdatedBook() {
        Long validId = 3L;
        CreateBookRequestDto requestDtoBook = new CreateBookRequestDto(
                "Basic Book",
                "Basic Author",
                "9211-123",
                new BigDecimal("15.80"),
                "Basic Description",
                "images.com/basic-book",
                Set.of(1L, 2L));

        Book modelBook = new Book();
        modelBook.setId(validId);
        modelBook.setTitle(requestDtoBook.title());
        modelBook.setAuthor(requestDtoBook.author());
        modelBook.setIsbn(requestDtoBook.isbn());
        modelBook.setPrice(requestDtoBook.price());
        modelBook.setDescription(requestDtoBook.description());
        modelBook.setCoverImage(requestDtoBook.coverImage());

        BookDto bookDto = new BookDto();
        bookDto.setId(modelBook.getId());
        bookDto.setTitle(requestDtoBook.title());
        bookDto.setAuthor(requestDtoBook.author());
        bookDto.setIsbn(requestDtoBook.isbn());
        bookDto.setPrice(requestDtoBook.price());
        bookDto.setDescription(requestDtoBook.description());
        bookDto.setCoverImage(requestDtoBook.coverImage());
        bookDto.setCategoryIds(requestDtoBook.categoryIds());

        when(bookMapper.toModel(requestDtoBook)).thenReturn(modelBook);
        when(bookRepository.save(modelBook)).thenReturn(modelBook);
        when(bookMapper.toDto(modelBook)).thenReturn(bookDto);

        BookDto actual = bookService.updateById(validId, requestDtoBook);

        assertEquals(bookDto, actual);
        verify(bookRepository, times(1)).save(any());
        verify(bookMapper, times(1)).toDto(any());
        verify(bookMapper, times(1)).toModel(any());
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @Test
    @DisplayName("TODO!")
    void findAllByCategoryId_ValidCategoryIdAndPageable_ReturnsBookDtoWithoutCategoryIds() {
        Long validCategoryId = 1L;
        Book modelBook = new Book();
        modelBook.setId(1L);
        modelBook.setTitle("Book title");
        modelBook.setAuthor("Author");
        modelBook.setIsbn("9211-123");
        modelBook.setPrice(new BigDecimal("11.70"));
        modelBook.setDescription("Description");
        modelBook.setCoverImage("image.com");
        modelBook.setCategories(Set.of(new Category(validCategoryId)));

        BookDtoWithoutCategoryIds bookDto = new BookDtoWithoutCategoryIds(
                modelBook.getId(),
                modelBook.getTitle(),
                modelBook.getAuthor(),
                modelBook.getIsbn(),
                modelBook.getPrice(),
                modelBook.getDescription(),
                modelBook.getCoverImage());

        Pageable pageable = PageRequest.of(0, 10);
        List<Book> books = List.of(modelBook);
        PageImpl<Book> booksPage = new PageImpl<>(books, pageable, books.size());
        when(bookRepository.findAllByCategoryId(validCategoryId, pageable)).thenReturn(booksPage);
        when(bookMapper.toDtoWithoutCategories(modelBook)).thenReturn(bookDto);

        List<BookDtoWithoutCategoryIds> actual = bookService.findAllByCategoryId(validCategoryId, pageable);
        assertEquals(1, actual.size());
        assertEquals(bookDto, actual.get(0));
        verify(bookRepository, times(1)).findAllByCategoryId(anyLong(), eq(pageable));
        verify(bookMapper, times(1)).toDtoWithoutCategories(any());
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @Test
    @DisplayName("TODO!")
    void findByIdWithCategories_ValidId_ReturnsBookDto() {
        Long validId = 1L;
        Book modelBook = new Book();
        modelBook.setId(validId);
        modelBook.setTitle("Book title");
        modelBook.setAuthor("Author");
        modelBook.setIsbn("9211-123");
        modelBook.setPrice(new BigDecimal("11.70"));
        modelBook.setDescription("Description");
        modelBook.setCoverImage("image.com");
        modelBook.setCategories(Set.of(new Category(1L)));

        BookDto bookDto = new BookDto();
        bookDto.setId(modelBook.getId());
        bookDto.setTitle(modelBook.getTitle());
        bookDto.setAuthor(modelBook.getAuthor());
        bookDto.setIsbn(modelBook.getIsbn());
        bookDto.setPrice(modelBook.getPrice());
        bookDto.setDescription(modelBook.getDescription());
        bookDto.setCoverImage(modelBook.getCoverImage());
        bookDto.setCategoryIds(modelBook.getCategories().stream()
                .map(Category::getId)
                .collect(Collectors.toSet()));

        when(bookRepository.findByIdWithCategories(validId)).thenReturn(Optional.of(modelBook));
        when(bookMapper.toDto(modelBook)).thenReturn(bookDto);

        BookDto actual = bookService.findByIdWithCategories(validId);
        assertEquals(bookDto, actual);
        verify(bookRepository, times(1)).findByIdWithCategories(any());
        verify(bookMapper, times(1)).toDto(any());
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @Test
    @DisplayName("TODO!")
    void findByIdWithCategories_InvalidId_ThrowsEntityNotFoundException() {
        Long invalidId = 10L;

        when(bookRepository.findByIdWithCategories(invalidId)).thenReturn(Optional.empty());

        EntityNotFoundException exception =
                assertThrows(EntityNotFoundException.class, () -> bookService.findByIdWithCategories(invalidId));

        String expected = "Can't find book with id: " + invalidId;
        String actual = exception.getMessage();
        assertEquals(expected, actual);
        verify(bookRepository, times(1)).findByIdWithCategories(any());
        verifyNoMoreInteractions(bookRepository);
    }

    @Test
    @DisplayName("TODO!")
    void findAllWithCategories_ValidPageable_ReturnsAllBookDto() {
        Book modelBook = new Book();
        modelBook.setId(1L);
        modelBook.setTitle("Book title");
        modelBook.setAuthor("Author");
        modelBook.setIsbn("9211-123");
        modelBook.setPrice(new BigDecimal("11.70"));
        modelBook.setDescription("Description");
        modelBook.setCoverImage("image.com");
        modelBook.setCategories(Set.of(new Category(1L)));

        BookDto bookDto = new BookDto();
        bookDto.setId(modelBook.getId());
        bookDto.setTitle(modelBook.getTitle());
        bookDto.setAuthor(modelBook.getAuthor());
        bookDto.setIsbn(modelBook.getIsbn());
        bookDto.setPrice(modelBook.getPrice());
        bookDto.setDescription(modelBook.getDescription());
        bookDto.setCoverImage(modelBook.getCoverImage());
        bookDto.setCategoryIds(modelBook.getCategories().stream()
                .map(Category::getId)
                .collect(Collectors.toSet()));

        Pageable pageable = PageRequest.of(0, 10);
        List<Book> books = List.of(modelBook);
        PageImpl<Book> booksPage = new PageImpl<>(books, pageable, books.size());

        when(bookRepository.findAllWithCategories(pageable)).thenReturn(booksPage);
        when(bookMapper.toDto(modelBook)).thenReturn(bookDto);

        List<BookDto> booksDtos = bookService.findAllWithCategories(pageable);

        assertEquals(1, booksDtos.size());
        assertEquals(bookDto, booksDtos.get(0));
        verify(bookRepository, times(1)).findAllWithCategories(pageable);
        verify(bookMapper, times(1)).toDto(any());
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @Test
    @DisplayName("TODO!")
    void search_ValidPageableAndSearchParameters_ReturnsAllMatchingBookDtoWithoutCategoryIds() {
        Book modelBook = new Book();
        modelBook.setId(1L);
        modelBook.setTitle("Book title");
        modelBook.setAuthor("Author");
        modelBook.setIsbn("9211-123");
        modelBook.setPrice(new BigDecimal("11.70"));
        modelBook.setDescription("Description");
        modelBook.setCoverImage("image.com");
        modelBook.setCategories(Set.of(new Category(1L)));

        BookDtoWithoutCategoryIds bookDto = new BookDtoWithoutCategoryIds(
                modelBook.getId(),
                modelBook.getTitle(),
                modelBook.getAuthor(),
                modelBook.getIsbn(),
                modelBook.getPrice(),
                modelBook.getDescription(),
                modelBook.getCoverImage());

        String minPrice = "10.00";
        String maxPrice = "25.00";
        BookSearchParametersDto bookSearchParametersDto = new BookSearchParametersDto(
                null, null, "10", "15");
        Specification<Book> specification = Specification.where(
                new PriceSpecificationProvider().getSpecification(new String[]{minPrice, maxPrice}));

        Pageable pageable = PageRequest.of(0, 10);
        List<Book> books = List.of(modelBook);
        PageImpl<Book> booksPage = new PageImpl<>(books, pageable, books.size());

        when(bookSpecificationBuilder.build(bookSearchParametersDto)).thenReturn(specification);
        when(bookRepository.findAll(specification, pageable)).thenReturn(booksPage);
        when(bookMapper.toDtoWithoutCategories(modelBook)).thenReturn(bookDto);

        List<BookDtoWithoutCategoryIds> expected = new ArrayList<>(List.of(bookDto));
        List<BookDtoWithoutCategoryIds> actual = bookService.search(pageable, bookSearchParametersDto);

        assertEquals(expected, actual);
    }
}
