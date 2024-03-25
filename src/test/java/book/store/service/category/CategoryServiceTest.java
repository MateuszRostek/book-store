package book.store.service.category;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import book.store.dto.category.CategoryDto;
import book.store.dto.category.CreateCategoryRequestDto;
import book.store.exception.EntityNotFoundException;
import book.store.mapper.CategoryMapper;
import book.store.model.Category;
import book.store.repository.category.CategoryRepository;
import book.store.service.category.impl.CategoryServiceImpl;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {
    private static final String SAMPLE_CATEGORY_NAME = "Basic Category";
    private static final String SAMPLE_CATEGORY_DESCRIPTION = "Basic Description";
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private CategoryMapper categoryMapper;
    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Test
    @DisplayName("Find all categories with valid pageable - Returns all categories")
    void findAll_ValidPageable_ReturnsAllCategoryDto() {
        Category firstCategory = createTestCategory(1L, SAMPLE_CATEGORY_NAME);
        Category secondCategory = createTestCategory(2L, SAMPLE_CATEGORY_NAME + " 2");
        Category thirdCategory = createTestCategory(3L, SAMPLE_CATEGORY_NAME + " 3");
        CategoryDto firstDto =
                createTestCategoryDto(firstCategory.getId(), firstCategory.getName());
        CategoryDto secondDto =
                createTestCategoryDto(secondCategory.getId(), secondCategory.getName());
        CategoryDto thirdDto =
                createTestCategoryDto(thirdCategory.getId(), thirdCategory.getName());
        Pageable pageable = PageRequest.of(0, 10);
        List<Category> categories = List.of(firstCategory, secondCategory, thirdCategory);
        PageImpl<Category> categoriesPage =
                new PageImpl<>(categories, pageable, categories.size());
        when(categoryRepository.findAll(pageable)).thenReturn(categoriesPage);
        when(categoryMapper.toDto(firstCategory)).thenReturn(firstDto);
        when(categoryMapper.toDto(secondCategory)).thenReturn(secondDto);
        when(categoryMapper.toDto(thirdCategory)).thenReturn(thirdDto);
        List<CategoryDto> expected = List.of(firstDto, secondDto, thirdDto);

        List<CategoryDto> actual = categoryService.findAll(pageable);

        assertEquals(expected, actual);
        verify(categoryRepository, times(1)).findAll(pageable);
        verify(categoryMapper, times(3)).toDto(any());
        verifyNoMoreInteractions(categoryMapper, categoryRepository);
    }

    @Test
    @DisplayName("Find category by valid ID - Returns category DTO")
    void findById_ValidId_ReturnsCategoryDto() {
        Long validId = 1L;
        Category modelCategory = createTestCategory(validId, SAMPLE_CATEGORY_NAME);
        CategoryDto expected =
                createTestCategoryDto(modelCategory.getId(), modelCategory.getName());
        when(categoryRepository.findById(validId)).thenReturn(Optional.of(modelCategory));
        when(categoryMapper.toDto(modelCategory)).thenReturn(expected);

        CategoryDto actual = categoryService.findById(validId);

        assertNotNull(actual);
        assertEquals(expected, actual);
        verify(categoryRepository, times(1)).findById(any());
        verify(categoryMapper, times(1)).toDto(any());
        verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }

    @Test
    @DisplayName("Find category by invalid ID - Throws EntityNotFoundException")
    void findById_InvalidId_ThrowsEntityNotFoundException() {
        Long invalidId = 100L;
        String expected = "Can't find category with id: " + invalidId;
        when(categoryRepository.findById(invalidId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class, () -> categoryService.findById(invalidId));
        String actual = exception.getMessage();

        assertEquals(expected, actual);
        verify(categoryRepository, times(1)).findById(any());
        verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }

    @Test
    @DisplayName("Save new category with valid request - Returns saved category DTO")
    void save_ValidCreateCategoryRequestDto_ReturnsCategoryDto() {
        CreateCategoryRequestDto requestDto = createTestCreateCategoryRequestDto();
        Category modelCategory = createTestCategory(1L, requestDto.name());
        CategoryDto expected =
                createTestCategoryDto(modelCategory.getId(), modelCategory.getName());
        when(categoryMapper.toModel(requestDto)).thenReturn(modelCategory);
        when(categoryRepository.save(modelCategory)).thenReturn(modelCategory);
        when(categoryMapper.toDto(modelCategory)).thenReturn(expected);

        CategoryDto actual = categoryService.save(requestDto);

        assertEquals(expected, actual);
        verify(categoryMapper, times(1)).toModel(any());
        verify(categoryRepository, times(1)).save(any());
        verify(categoryMapper, times(1)).toDto(any());
        verifyNoMoreInteractions(categoryMapper, categoryRepository);
    }

    @Test
    @DisplayName("Update category by valid ID and request "
            + "(or create new when ID doesn't exist) - Returns updated or created category DTO")
    void update_ValidCreateCategoryRequestDto_ReturnsCategoryDto() {
        Long newId = 10L;
        CreateCategoryRequestDto requestDto = createTestCreateCategoryRequestDto();
        Category modelCategory = createTestCategory(newId, requestDto.name());
        CategoryDto expected =
                createTestCategoryDto(modelCategory.getId(), modelCategory.getName());
        when(categoryMapper.toModel(requestDto)).thenReturn(modelCategory);
        when(categoryRepository.save(modelCategory)).thenReturn(modelCategory);
        when(categoryMapper.toDto(modelCategory)).thenReturn(expected);

        CategoryDto actual = categoryService.update(newId, requestDto);

        assertEquals(expected, actual);
        verify(categoryMapper, times(1)).toModel(any());
        verify(categoryRepository, times(1)).save(any());
        verify(categoryMapper, times(1)).toDto(any());
        verifyNoMoreInteractions(categoryMapper, categoryRepository);
    }

    private Category createTestCategory(Long id, String name) {
        Category category = new Category(id);
        category.setName(name);
        category.setDescription(SAMPLE_CATEGORY_DESCRIPTION);
        return category;
    }

    private CategoryDto createTestCategoryDto(Long id, String name) {
        return new CategoryDto(id, name, SAMPLE_CATEGORY_DESCRIPTION);
    }

    private CreateCategoryRequestDto createTestCreateCategoryRequestDto() {
        return new CreateCategoryRequestDto(SAMPLE_CATEGORY_NAME, SAMPLE_CATEGORY_DESCRIPTION);
    }
}
