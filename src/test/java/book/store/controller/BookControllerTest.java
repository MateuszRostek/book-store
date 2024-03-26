package book.store.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import book.store.dto.book.BookDto;
import book.store.dto.book.BookDtoWithoutCategoryIds;
import book.store.dto.book.BookSearchParametersDto;
import book.store.dto.book.CreateBookRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BookControllerTest {
    protected static MockMvc mockMvc;
    private static final String BASE_ENDPOINT = "/api/books";

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll(
            @Autowired DataSource dataSource,
            @Autowired WebApplicationContext applicationContext) throws SQLException {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
        teardown(dataSource);
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/books/add-three-books-with-categories.sql"));
        }
    }

    @AfterAll
    static void afterAll(@Autowired DataSource dataSource) {
        teardown(dataSource);
    }

    @SneakyThrows
    static void teardown(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/books/remove-all-books-with-categories.sql"));
        }
    }

    @WithMockUser(username = "user", authorities = {"USER"})
    @Test
    void getAll_ThreeBooksInDb_ReturnsAllBooks() throws Exception {
        List<BookDto> expected = getThreeBookDto();

        MvcResult result = mockMvc.perform(
                        get(BASE_ENDPOINT).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        BookDto[] actual = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(), BookDto[].class);

        assertNotNull(actual);
        assertEquals(3, actual.length);
        assertEquals(expected, Arrays.stream(actual).toList());
    }

    @WithMockUser(username = "user", authorities = {"USER"})
    @Test
    void getBookById_ValidId_ReturnsBookDto() throws Exception {
        long validId = 2L;
        BookDto expected = getThreeBookDto().get(1);

        MvcResult result = mockMvc.perform(
                        get(BASE_ENDPOINT + "/" + validId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        BookDto actual = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(), BookDto.class);

        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    @Test
    @Sql(scripts = {"classpath:database/controller/book/delete-created-book.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void createBook_ValidRequestDto_ReturnsNewBook() throws Exception {
        CreateBookRequestDto requestDto = createTestCreateBookRequestDto();
        BookDto expected = getBookDtoFromRequest(4L, requestDto);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(post(BASE_ENDPOINT)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();
        BookDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), BookDto.class);

        assertNotNull(actual.getId());
        EqualsBuilder.reflectionEquals(expected, actual, "id");
    }

    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    @Test
    @Sql(scripts = {"classpath:database/books/remove-all-books-with-categories.sql",
            "classpath:database/books/add-three-books-with-categories.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void updateBookById_ValidRequestDto_ReturnsUpdatedBook() throws Exception {
        Long idPassed = 2L;
        CreateBookRequestDto requestDto = createTestCreateBookRequestDto();
        BookDto expected = getBookDtoFromRequest(idPassed, requestDto);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(put(BASE_ENDPOINT + "/" + idPassed)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        BookDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), BookDto.class);

        assertNotNull(actual.getId());
        EqualsBuilder.reflectionEquals(expected, actual, "id");
    }

    @WithMockUser(username = "user", authorities = {"USER"})
    @Test
    void search_ValidSearchParameters_ReturnsAllMatchingBookDtoWithoutCategoryIds()
            throws Exception {
        List<BookDtoWithoutCategoryIds> expected = getTwoMatchingBookDtoWithoutCategoryIds();
        BookSearchParametersDto searchParameters = new BookSearchParametersDto(
                null, null, "20", "29");

        MvcResult result = mockMvc.perform(
                        get(BASE_ENDPOINT
                                + "/search?minPrice="
                                + searchParameters.minPrice()
                                + "&maxPrice="
                                + searchParameters.maxPrice())
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        BookDtoWithoutCategoryIds[] actual = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(), BookDtoWithoutCategoryIds[].class);

        assertNotNull(actual);
        assertEquals(2, actual.length);
        assertEquals(expected, Arrays.stream(actual).toList());
    }

    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    @Test
    @Sql(scripts = {"classpath:database/books/remove-all-books-with-categories.sql",
            "classpath:database/books/add-three-books-with-categories.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void deleteBook_ValidId_ReturnsNoContentStatus() throws Exception {
        long idPassed = 2L;
        mockMvc.perform(delete(BASE_ENDPOINT + "/" + idPassed)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andReturn();
        mockMvc.perform(
                        get(BASE_ENDPOINT + "/" + idPassed)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();
    }

    private CreateBookRequestDto createTestCreateBookRequestDto() {
        return new CreateBookRequestDto(
                "Basic Book 44",
                "Basic Author 44",
                "9911-111",
                new BigDecimal("45.50"),
                "Basic Description 44",
                "images.com/image44.jpg",
                Set.of(1L));
    }

    private BookDto getBookDtoFromRequest(Long id, CreateBookRequestDto requestDto) {
        return new BookDto(
                id,
                requestDto.title(),
                requestDto.author(),
                requestDto.isbn(),
                requestDto.price(),
                requestDto.description(),
                requestDto.coverImage(),
                requestDto.categoryIds());
    }

    private List<BookDto> getThreeBookDto() {
        List<BookDto> expected = new ArrayList<>();
        expected.add(new BookDto(
                1L,
                "Basic Book",
                "Basic Author",
                "9911-123",
                new BigDecimal("20.50"),
                "Basic Description",
                "images.com/image.jpg",
                Set.of(1L)));
        expected.add(new BookDto(
                2L,
                "Basic Book 2",
                "Basic Author 2",
                "9911-124",
                new BigDecimal("25.50"),
                "Basic Description 2",
                "images.com/image2.jpg",
                Set.of(1L)));
        expected.add(new BookDto(
                3L,
                "Different Book",
                "Different Author",
                "9911-125",
                new BigDecimal("14.50"),
                "Different Description",
                "images.com/image3.jpg",
                Set.of(2L)));
        return expected;
    }

    private List<BookDtoWithoutCategoryIds> getTwoMatchingBookDtoWithoutCategoryIds() {
        List<BookDtoWithoutCategoryIds> expected = new ArrayList<>();
        expected.add(new BookDtoWithoutCategoryIds(
                1L,
                "Basic Book",
                "Basic Author",
                "9911-123",
                new BigDecimal("20.50"),
                "Basic Description",
                "images.com/image.jpg"));
        expected.add(new BookDtoWithoutCategoryIds(
                2L,
                "Basic Book 2",
                "Basic Author 2",
                "9911-124",
                new BigDecimal("25.50"),
                "Basic Description 2",
                "images.com/image2.jpg"));
        return expected;
    }
}
