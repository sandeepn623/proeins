package com.proeins.dao;

import java.util.List;

import com.proeins.exception.ShoeNotFoundException;
import com.proeins.model.Shoe;

public interface ShoeDao {
	Shoe save(Shoe shoe);

	Shoe update(long id, Shoe shoe) throws ShoeNotFoundException;

	Shoe delete(long id) throws ShoeNotFoundException;
	
	List<Shoe> searchShoes(List<SearchCriteria> params); 
}
