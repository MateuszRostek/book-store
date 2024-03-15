package book.store.service.category.impl;

import book.store.dto.category.CategoryDto;
import book.store.dto.category.CreateCategoryRequestDto;
import book.store.exception.EntityNotFoundException;
import book.store.mapper.CategoryMapper;
import book.store.model.Category;
import book.store.repository.category.CategoryRepository;
import book.store.service.category.CategoryService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public List<CategoryDto> findAll(Pageable pageable) {
        return categoryRepository.findAll(pageable).stream()
                .map(categoryMapper::toDto)
                .toList();
    }

    @Override
    public CategoryDto findById(Long id) {
        return categoryMapper.toDto(
                categoryRepository.findById(id).orElseThrow(
                        () -> new EntityNotFoundException("Can't find category with id: " + id)));
    }

    @Override
    public CategoryDto save(CreateCategoryRequestDto requestDto) {
        Category modelCategory = categoryMapper.toModel(requestDto);
        return categoryMapper.toDto(categoryRepository.save(modelCategory));
    }

    @Override
    public CategoryDto update(Long id, CreateCategoryRequestDto requestDto) {
        Category newModelCategory = categoryMapper.toModel(requestDto);
        newModelCategory.setId(id);
        return categoryMapper.toDto(categoryRepository.save(newModelCategory));
    }

    @Override
    public void deleteById(Long id) {
        categoryRepository.deleteById(id);
    }
}
