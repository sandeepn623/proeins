package com.proeins.controller;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.proeins.exception.ShoeNotFoundException;
import com.proeins.model.Shoe;
import com.proeins.service.ShoeService;

@RestController
@CrossOrigin(origins= {"https://9895fbf4.ap.ngrok.io"})
public class ShoeController {

	private ShoeService shoeService;
	
	@Autowired
	public ShoeController(ShoeService shoeService) {
		this.shoeService = shoeService;
	}
	/*---Add new shoe---*/
	@PostMapping("/shoe")
	public ResponseEntity<?> save(@RequestBody Shoe shoe) throws IllegalArgumentException {
		System.out.println("POST request initiated");
		Shoe savedShoe = shoeService.save(shoe);
		final URI location = ServletUriComponentsBuilder.fromCurrentServletMapping().path("/shoe/{id}").build().expand(shoe.getId()).toUri();
		return ResponseEntity.created(location).body(savedShoe);
	}

	
	/*---get all shoes or by Id, articleNumber, brand, name, color, stock---*/
	@GetMapping("/shoe")
	public ResponseEntity<List<Shoe>> searchShoes(@RequestParam(value = "search", required = false) String search) {
		System.out.println("search: " + search);
        List<Shoe> shoes = shoeService.searchShoes(search);
        return  ResponseEntity.ok().body(shoes);
    }

	/*---Update a shoe by id---*/
	@PutMapping("/shoe/{id}")
	public ResponseEntity<?> update(@PathVariable("id") long id, @RequestBody Shoe shoe) throws ShoeNotFoundException, IllegalArgumentException {
		Shoe updatedShoe = shoeService.update(id, shoe);
		return ResponseEntity.ok().body(updatedShoe);
	}

	/*---Delete a shoe by id---*/
	@DeleteMapping("/shoe/{id}")
	public ResponseEntity<?> delete(@PathVariable("id") long id) throws ShoeNotFoundException {
		Shoe deletedShoe = shoeService.delete(id);
		return ResponseEntity.ok().body(deletedShoe);
	}
}
