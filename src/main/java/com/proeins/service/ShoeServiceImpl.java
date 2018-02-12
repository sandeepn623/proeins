package com.proeins.service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proeins.dao.SearchCriteria;
import com.proeins.dao.ShoeDao;
import com.proeins.exception.ShoeNotFoundException;
import com.proeins.model.Shoe;


@Service
@Transactional(readOnly = true)
public class ShoeServiceImpl implements ShoeService {

	private ShoeDao shoeDao;
	
	@Autowired
	public ShoeServiceImpl(ShoeDao shoeDao) {
		this.shoeDao = shoeDao;
	}
	
	@Transactional
	@Override
	public Shoe save(Shoe shoe) {
		return shoeDao.save(shoe);
	}

	@Transactional
	@Override
	public Shoe update(long id, Shoe shoe) throws ShoeNotFoundException {
		return shoeDao.update(id, shoe);
	}

	@Transactional
	@Override
	public Shoe delete(long id) throws ShoeNotFoundException {
		return shoeDao.delete(id);
	}

	@Override
	public List<Shoe> searchShoes(String search) {
        List<SearchCriteria> params = new ArrayList<SearchCriteria>();
		if (search != null) {
            Pattern pattern = Pattern.compile("(\\w+?)(:|<|>)(\\w+?),");
            Matcher matcher = pattern.matcher(search + ",");
            while (matcher.find()) {
                params.add(new SearchCriteria(matcher.group(1), matcher.group(2), matcher.group(3)));
            }
        }
		return shoeDao.searchShoes(params);
	}
}
