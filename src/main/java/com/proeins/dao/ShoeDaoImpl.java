package com.proeins.dao;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.proeins.exception.ShoeNotFoundException;
import com.proeins.model.Shoe;

@Repository
public class ShoeDaoImpl implements ShoeDao {

	private SessionFactory sessionFactory;

	@Autowired
	public ShoeDaoImpl(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Override
	public Shoe save(Shoe shoe) {
		if (shoe.isEmpty()) {
			throw new IllegalArgumentException("Invalid argument passed");
		}
		sessionFactory.getCurrentSession().save(shoe);
		long id = shoe.getId();
		return sessionFactory.getCurrentSession().get(Shoe.class, id);
	}

	@Transactional(rollbackFor = {ShoeNotFoundException.class})
	@Override
	public Shoe update(long id, Shoe shoe) throws ShoeNotFoundException {
		Session session = sessionFactory.getCurrentSession();
		Shoe originalShoeEntity = findById(id);
		if (shoe.isEmpty()) {
			throw new IllegalArgumentException("Invalid argument passed");
		}
		originalShoeEntity.setName((shoe.getName() == null || shoe.getName().isEmpty()) ? originalShoeEntity.getName() : shoe.getName());
		originalShoeEntity.setBrand((shoe.getBrand() == null || shoe.getBrand().isEmpty()) ? originalShoeEntity.getBrand() : shoe.getBrand());
		originalShoeEntity.setColor((shoe.getColor() == null || shoe.getColor().isEmpty()) ? originalShoeEntity.getColor() : shoe.getColor());
		originalShoeEntity.setSize((shoe.getSize() == null || shoe.getSize().isEmpty()) ? originalShoeEntity.getSize() : shoe.getSize());
		originalShoeEntity.setStock((shoe.getStock() == null || shoe.getStock().isEmpty()) ? originalShoeEntity.getStock() : shoe.getStock());
		session.flush();
		return originalShoeEntity;
	}

	@Transactional(rollbackFor = {ShoeNotFoundException.class})
	@Override
	public Shoe delete(long id) throws ShoeNotFoundException {
		Session session = sessionFactory.getCurrentSession();
		Shoe shoe = findById(id);
		session.delete(shoe);
		return shoe;
	}

	@Override
	public List<Shoe> searchShoes(List<SearchCriteria> params) {
		Session session = sessionFactory.getCurrentSession();
		CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
		CriteriaQuery<Shoe> criteriaQuery = criteriaBuilder.createQuery(Shoe.class);
		Root<Shoe> root = criteriaQuery.from(Shoe.class);
		Predicate predicate = criteriaBuilder.conjunction();

		for (SearchCriteria param : params) {
			if (root.get(param.getKey()).getJavaType() == String.class) {
				predicate = criteriaBuilder.and(predicate,
						criteriaBuilder.like(root.get(param.getKey()), "%" + param.getValue() + "%"));
			} else {
				predicate = criteriaBuilder.and(predicate,
						criteriaBuilder.equal(root.get(param.getKey()), param.getValue()));
			}
		}
		criteriaQuery.where(predicate);
		List<Shoe> result = session.createQuery(criteriaQuery).getResultList();
		return result;
	}

	@Transactional(readOnly = true, rollbackFor = { ShoeNotFoundException.class })
	public Shoe findById(long id) throws ShoeNotFoundException {
		Session session = sessionFactory.getCurrentSession();
		Shoe found = session.byId(Shoe.class).load(id);
		if (found == null) {
			throw new ShoeNotFoundException("No to-entry found with id: " + id);
		}

		return found;
	}
}
