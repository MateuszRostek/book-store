package book.store.service.book.impl;

import book.store.dto.book.BookDto;
import book.store.dto.book.BookDtoWithoutCategoryIds;
import book.store.dto.book.BookSearchParametersDto;
import book.store.dto.book.CreateBookRequestDto;
import book.store.exception.EntityNotFoundException;
import book.store.mapper.BookMapper;
import book.store.model.Book;
import book.store.repository.book.BookRepository;
import book.store.repository.book.BookSpecificationBuilder;
import book.store.service.book.BookService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final BookSpecificationBuilder bookSpecificationBuilder;

    @Override
    public BookDto save(CreateBookRequestDto createBookRequestDto) {
        Book modelBook = bookMapper.toModel(createBookRequestDto);
        return bookMapper.toDto(bookRepository.save(modelBook));
    }

    @Override
    public List<BookDto> findAll(Pageable pageable) {
        return bookRepository.findAll(pageable).stream()
                .map(bookMapper::toDto)
                .toList();
    }

    @Override
    public BookDto findById(Long id) {
        return bookMapper.toDto(
                bookRepository.findById(id).orElseThrow(
                        () -> new EntityNotFoundException("Can't find book with id: " + id)));
    }

    @Override
    public void deleteById(Long id) {
        bookRepository.deleteById(id);
    }

    @Override
    public BookDto updateById(Long id, CreateBookRequestDto book) {
        Book modelBook = bookMapper.toModel(book);
        modelBook.setId(id);
        return bookMapper.toDto(bookRepository.save(modelBook));
    }

    @Override
    public List<BookDtoWithoutCategoryIds> search(
            Pageable pageable, BookSearchParametersDto searchParametersDto) {
        Specification<Book> bookSpecification = bookSpecificationBuilder.build(searchParametersDto);
        return bookRepository.findAll(bookSpecification, pageable).stream()
                .map(bookMapper::toDtoWithoutCategories)
                .toList();
    }

    @Override
    public List<BookDtoWithoutCategoryIds> findAllByCategoryId(
            Long categoryId, Pageable pageable) {
        return bookRepository.findAllByCategoryId(categoryId, pageable).stream()
                .map(bookMapper::toDtoWithoutCategories)
                .toList();
    }

    @Override
    public BookDto findByIdWithCategories(Long id) {
        return bookMapper.toDto(
                bookRepository.findByIdWithCategories(id).orElseThrow(
                        () -> new EntityNotFoundException("Can't find book with id: " + id)));
    }

    @Override
    public List<BookDto> findAllWithCategories(Pageable pageable) {
        return bookRepository.findAllWithCategories(pageable).stream()
                .map(bookMapper::toDto)
                .toList();
    }
}
