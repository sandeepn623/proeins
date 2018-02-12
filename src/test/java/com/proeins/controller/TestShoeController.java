package com.proeins.controller;

import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.proeins.TestUtils;
import com.proeins.config.TestContext;
import com.proeins.exception.ShoeNotFoundException;
import com.proeins.model.Shoe;
import com.proeins.model.ShoeBuilder;
import com.proeins.service.ShoeService;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {TestContext.class})
@WebMvcTest(controllers = ShoeService.class, secure = false)
public class TestShoeController {

	@MockBean
	private ShoeService shoeService;

	@Autowired
	private MockMvc mockMvc;
	
	@Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(new ShoeController(shoeService)).setControllerAdvice(new RestErrorHandler()).build();
    }
	
	
	/******************************************
	 * 		Test cases for save operation
	 ******************************************/
    @Test
    public void save_EmptyShoeEntry_ShouldReturnHttpStatusCode404() throws Exception {
    	Shoe shoe = new ShoeBuilder().build();
    	when(shoeService.save(any(Shoe.class))).thenThrow(new IllegalArgumentException(""));
        mockMvc.perform(post("/shoe")
                .contentType(TestUtils.APPLICATION_JSON_UTF8)
                .content(TestUtils.convertObjectToJsonBytes(shoe))
        		)
        		.andExpect(MockMvcResultMatchers.status().isBadRequest());

        ArgumentCaptor<Shoe> dtoCaptor = ArgumentCaptor.forClass(Shoe.class);
        verify(shoeService, times(1)).save(dtoCaptor.capture());
        verifyZeroInteractions(shoeService);
    }
    
    @Test
    public void save_NewShoeEntry_ShouldAddShoeEntryAndReturnAddedEntry() throws Exception {
        Shoe shoe = new ShoeBuilder()
        		.setArticleNumber("S123456")
        		.setBrand("Nike")
				.setName("Nike Sports Shoe")
				.setColor("White")
				.setSize("11")
				.setStock("10")
				.build();

        Shoe added = new ShoeBuilder()
        			.setId(1)
        			.setArticleNumber("S123456")
        			.setBrand("Nike")
					.setName("Nike Sports Shoe")
					.setColor("White")
					.setSize("11")
					.setStock("10")
					.build();

        when(shoeService.save(any(Shoe.class))).thenReturn(added);

        mockMvc.perform(post("/shoe")
                	.contentType(MediaType.APPLICATION_JSON_UTF8)
                	.content(TestUtils.convertObjectToJsonBytes(shoe))
        		)
                .andExpect(status().isCreated())
                .andExpect(content().contentType(TestUtils.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.id", is(1)));

        ArgumentCaptor<Shoe> dtoCaptor = ArgumentCaptor.forClass(Shoe.class);
        verify(shoeService, times(1)).save(dtoCaptor.capture());
        verifyNoMoreInteractions(shoeService);
    }
    
    /******************************************
	 * 		Test cases for delete operation
	 ******************************************/
	@Test
	public void deleteById_ShoeIsNotFound_ShouldReturnHttpStatusCode404() throws Exception {
		when(shoeService.delete(3L)).thenThrow(new ShoeNotFoundException(""));
		mockMvc.perform(delete("/shoe/{id}", 3L)).
				andExpect(MockMvcResultMatchers.status().isNotFound());

		verify(shoeService, times(1)).delete(3L);
		verifyNoMoreInteractions(shoeService);
	}
	
	@Test
	public void deleteById_ShoeEntryFound_ShouldDeleteShoeEntryAndReturnIt() throws Exception {
		Shoe deleted = new ShoeBuilder()
					.setId(1)
					.setArticleNumber("S123456")
					.setBrand("Nike")
					.setName("Nike Sports Shoe")
					.setColor("White")
					.setSize("11")
					.setStock("10")
					.build();

		when(shoeService.delete(1L)).thenReturn(deleted);
		
		mockMvc.perform(delete("/shoe/{id}", 1L))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(1)));

		verify(shoeService, times(1)).delete(1L);
		verifyNoMoreInteractions(shoeService);
	}
	
	/******************************************
	 * 		Test cases for search operation
	 ******************************************/
	@Test
    public void findById_ShoeEntryFound_ShouldReturnFoundShoeEntry() throws Exception {
		Shoe found = new ShoeBuilder()
				.setId(1)
                .setArticleNumber("S123456")
                .setBrand("Nike")
				.setName("Nike Sports Shoe")
				.setColor("White")
				.setSize("11")
				.setStock("10")
				.build();
		List<Shoe> shoeList = singletonList(found);
        when(shoeService.searchShoes("id:9")).thenReturn(shoeList);

        mockMvc.perform(get("/shoe?search=id:9"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(TestUtils.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)));

        verify(shoeService, times(1)).searchShoes("id:9");
        verifyNoMoreInteractions(shoeService);
    }
	
	@Test
    public void findByEmptyId_ShoeEntryNotFound_ShouldReturnEmptyList() throws Exception {
	    List<Shoe> shoeList = new ArrayList<>();
		when(shoeService.searchShoes("id:")).thenReturn(shoeList);
        mockMvc.perform(get("/shoe?search=id:")).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(0)));
        verify(shoeService, times(1)).searchShoes("id:");
        verifyNoMoreInteractions(shoeService);
    }
	
	@Test
    public void findByArticleNumber_ShoeEntryFound_ShouldReturnFoundShoeEntry() throws Exception {
		Shoe found = new ShoeBuilder()
                .setArticleNumber("S1234")
                .setBrand("Nike")
				.setName("Nike Sports Shoe")
				.setColor("White")
				.setSize("11")
				.setStock("10")
				.build();
		List<Shoe> shoeList = singletonList(found);
        when(shoeService.searchShoes("articleNumber:S1234")).thenReturn(shoeList);

        mockMvc.perform(get("/shoe?search=articleNumber:S1234"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(TestUtils.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].articleNumber", is("S1234")));

        verify(shoeService, times(1)).searchShoes("articleNumber:S1234");
        verifyNoMoreInteractions(shoeService);
    }
	
	@Test
    public void findByEmptyArticleNumber_ShoeEntryNotFound_ShouldReturnEmptyList() throws Exception {
	    List<Shoe> shoeList = new ArrayList<>();
		when(shoeService.searchShoes("articleNumber:")).thenReturn(shoeList);
        mockMvc.perform(get("/shoe?search=articleNumber:")).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(0)));
        verify(shoeService, times(1)).searchShoes("articleNumber:");
        verifyNoMoreInteractions(shoeService);
    }
	
	@Test
    public void findByBrand_ShoeEntryFound_ShouldReturnFoundShoeEntry() throws Exception {
		Shoe found = new ShoeBuilder()
                .setArticleNumber("S1234")
                .setBrand("Nike")
				.setName("Nike Sports Shoe")
				.setColor("White")
				.setSize("11")
				.setStock("10")
				.build();
		List<Shoe> shoeList = singletonList(found);
        when(shoeService.searchShoes("brand:Nike")).thenReturn(shoeList);

        mockMvc.perform(get("/shoe?search=brand:Nike"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(TestUtils.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].brand", is("Nike")));

        verify(shoeService, times(1)).searchShoes("brand:Nike");
        verifyNoMoreInteractions(shoeService);
    }
	
	@Test
    public void findByEmptyBrand_ShoeEntryNotFound_ShouldReturnEmptyList() throws Exception {
	    List<Shoe> shoeList = new ArrayList<>();
		when(shoeService.searchShoes("brand:")).thenReturn(shoeList);
        mockMvc.perform(get("/shoe?search=brand:")).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(0)));
        verify(shoeService, times(1)).searchShoes("brand:");
        verifyNoMoreInteractions(shoeService);
    }
	
	@Test
    public void findByName_ShoeEntryFound_ShouldReturnFoundShoeEntry() throws Exception {
		Shoe found = new ShoeBuilder()
                .setArticleNumber("S1234")
                .setBrand("Nike")
				.setName("Nike Sports Shoe")
				.setColor("White")
				.setSize("11")
				.setStock("10")
				.build();
		List<Shoe> shoeList = singletonList(found);
        when(shoeService.searchShoes("name:Nike Sports Shoe")).thenReturn(shoeList);

        mockMvc.perform(get("/shoe?search=name:Nike Sports Shoe"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(TestUtils.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Nike Sports Shoe")));

        verify(shoeService, times(1)).searchShoes("name:Nike Sports Shoe");
        verifyNoMoreInteractions(shoeService);
    }
	
	@Test
    public void findByEmptyName_ShoeEntryNotFound_ShouldReturnEmptyList() throws Exception {
	    List<Shoe> shoeList = new ArrayList<>();
		when(shoeService.searchShoes("name:")).thenReturn(shoeList);
        mockMvc.perform(get("/shoe?search=name:")).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(0)));
        verify(shoeService, times(1)).searchShoes("name:");
        verifyNoMoreInteractions(shoeService);
    }
	
	@Test
    public void findByColor_ShoeEntryFound_ShouldReturnFoundShoeEntry() throws Exception {
		Shoe found = new ShoeBuilder()
                .setArticleNumber("S1234")
                .setBrand("Nike")
				.setName("Nike Sports Shoe")
				.setColor("white")
				.setSize("11")
				.setStock("10")
				.build();
		List<Shoe> shoeList = singletonList(found);
        when(shoeService.searchShoes("color:white")).thenReturn(shoeList);

        mockMvc.perform(get("/shoe?search=color:white"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(TestUtils.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].color", is("white")));

        verify(shoeService, times(1)).searchShoes("color:white");
        verifyNoMoreInteractions(shoeService);
    }
	
	@Test
    public void findByEmptyColor_ShoeEntryNotFound_ShouldReturnEmptyList() throws Exception {
	    List<Shoe> shoeList = new ArrayList<>();
		when(shoeService.searchShoes("color:")).thenReturn(shoeList);
        mockMvc.perform(get("/shoe?search=color:")).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(0)));
        verify(shoeService, times(1)).searchShoes("color:");
        verifyNoMoreInteractions(shoeService);
    }
	
	@Test
    public void findBySize_ShoeEntryFound_ShouldReturnFoundShoeEntry() throws Exception {
		Shoe found = new ShoeBuilder()
                .setArticleNumber("S1234")
                .setBrand("Nike")
				.setName("Nike Sports Shoe")
				.setColor("White")
				.setSize("11")
				.setStock("10")
				.build();
		List<Shoe> shoeList = singletonList(found);
        when(shoeService.searchShoes("size:11")).thenReturn(shoeList);

        mockMvc.perform(get("/shoe?search=size:11"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(TestUtils.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].size", is("11")));

        verify(shoeService, times(1)).searchShoes("size:11");
        verifyNoMoreInteractions(shoeService);
    }
	
	@Test
    public void findByEmptySize_ShoeEntryNotFound_ShouldReturnEmptyList() throws Exception {
	    List<Shoe> shoeList = new ArrayList<>();
		when(shoeService.searchShoes("size:")).thenReturn(shoeList);
        mockMvc.perform(get("/shoe?search=size:")).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(0)));
        verify(shoeService, times(1)).searchShoes("size:");
        verifyNoMoreInteractions(shoeService);
    }
	
	@Test
    public void findByStock_ShoeEntryFound_ShouldReturnFoundShoeEntry() throws Exception {
		Shoe found = new ShoeBuilder()
                .setArticleNumber("S1234")
                .setBrand("Nike")
				.setName("Nike Sports Shoe")
				.setColor("White")
				.setSize("11")
				.setStock("1")
				.build();
		List<Shoe> shoeList = singletonList(found);
        when(shoeService.searchShoes("stock:1")).thenReturn(shoeList);

        mockMvc.perform(get("/shoe?search=stock:1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(TestUtils.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].stock", is("1")));

        verify(shoeService, times(1)).searchShoes("stock:1");
        verifyNoMoreInteractions(shoeService);
    }
	
	@Test
    public void findByEmptyStock_ShoeEntryNotFound_ShouldReturnEmptyList() throws Exception {
	    List<Shoe> shoeList = new ArrayList<>();
		when(shoeService.searchShoes("stock:")).thenReturn(shoeList);
        mockMvc.perform(get("/shoe?search=stock:")).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(0)));
        verify(shoeService, times(1)).searchShoes("stock:");
        verifyNoMoreInteractions(shoeService);
    }
	
	@Test
    public void findAll_ShoeEntriesFound_ShouldReturnFoundShoeEntries() throws Exception {
		Shoe found = new ShoeBuilder()
                .setArticleNumber("S1234")
                .setBrand("Nike")
				.setName("Nike Sports Shoe")
				.setColor("White")
				.setSize("11")
				.setStock("25")
				.build();
		List<Shoe> shoeList = singletonList(found);
        when(shoeService.searchShoes(null)).thenReturn(shoeList);

        mockMvc.perform(get("/shoe"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(TestUtils.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$", hasSize(1)));

        verify(shoeService, times(1)).searchShoes(null);
        verifyNoMoreInteractions(shoeService);
    }
	
	/******************************************
	 * 		Test cases for update operation
	 ******************************************/
	@Test
    public void update_ShouldReturnErrorForArticleNumber() throws Exception {
        Shoe shoe = new ShoeBuilder()
				.setArticleNumber("S1234")
                .setBrand("Nike")
				.setName("Nike Sports Shoe")
				.setColor("White")
				.setSize("11")
				.setStock("25")
				.build();
        
        when(shoeService.update(anyLong(), any(Shoe.class))).thenThrow(new IllegalArgumentException(""));
        
        mockMvc.perform(put("/shoe/{id}", 10L)
                .contentType(TestUtils.APPLICATION_JSON_UTF8)
                .content(TestUtils.convertObjectToJsonBytes(shoe))
        )
        .andExpect(status().isBadRequest());
        
        ArgumentCaptor<Shoe> dtoCaptor = ArgumentCaptor.forClass(Shoe.class);
        verify(shoeService, times(1)).update(anyLong(), dtoCaptor.capture());
        verifyNoMoreInteractions(shoeService);
    }
	
	@Test
    public void update_ShouldReturnErrorForId() throws Exception {
        Shoe shoe = new ShoeBuilder()
				.setId(2)
                .setBrand("Nike")
				.setName("Nike Sports Shoe")
				.setColor("White")
				.setSize("11")
				.setStock("25")
				.build();
        
        when(shoeService.update(anyLong(), any(Shoe.class))).thenThrow(new IllegalArgumentException(""));
        
        mockMvc.perform(put("/shoe/{id}", 10L)
                .contentType(TestUtils.APPLICATION_JSON_UTF8)
                .content(TestUtils.convertObjectToJsonBytes(shoe))
        )
        .andExpect(status().isBadRequest());
        
        ArgumentCaptor<Shoe> dtoCaptor = ArgumentCaptor.forClass(Shoe.class);
        verify(shoeService, times(1)).update(anyLong(), dtoCaptor.capture());
        verifyNoMoreInteractions(shoeService);
    }
	
	@Test
    public void update_ShoeEntryNotFound_ShouldReturnHttpStatusCode404() throws Exception {
        Shoe shoe = new ShoeBuilder()
                .setBrand("Nike")
				.setName("Nike Sports Shoe")
				.setColor("White")
				.setSize("11")
				.setStock("25")
				.build();

        when(shoeService.update(anyLong(), any(Shoe.class))).thenThrow(new ShoeNotFoundException(""));

        mockMvc.perform(put("/shoe/{id}", 3L)
                .contentType(TestUtils.APPLICATION_JSON_UTF8)
                .content(TestUtils.convertObjectToJsonBytes(shoe))
        )
        .andExpect(status().isNotFound());

        ArgumentCaptor<Shoe> dtoCaptor = ArgumentCaptor.forClass(Shoe.class);
        verify(shoeService, times(1)).update(anyLong(), dtoCaptor.capture());
        verifyNoMoreInteractions(shoeService);
    }
	
	@Test
    public void update_ShoeEntryFound_ShouldUpdateShoeEntryAndReturnIt() throws Exception {
        Shoe shoe = new ShoeBuilder()
                .setBrand("Nike")
				.setName("Nike Sports Shoe")
				.setColor("White")
				.setSize("11")
				.setStock("25")
				.build();

        Shoe updated = new ShoeBuilder()
        		.setId(10)
        		.setArticleNumber("S1234")
                .setBrand("Adidas")
				.setName("Adidas Sports Shoe")
				.setColor("White")
				.setSize("11")
				.setStock("25")
				.build();
        
        when(shoeService.update(anyLong(), any(Shoe.class))).thenReturn(updated);
        
        mockMvc.perform(put("/shoe/{id}", 10L)
                .contentType(TestUtils.APPLICATION_JSON_UTF8)
                .content(TestUtils.convertObjectToJsonBytes(shoe))
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(10)));
        
        ArgumentCaptor<Shoe> dtoCaptor = ArgumentCaptor.forClass(Shoe.class);
        verify(shoeService, times(1)).update(anyLong(), dtoCaptor.capture());
        verifyNoMoreInteractions(shoeService);
    }
}
