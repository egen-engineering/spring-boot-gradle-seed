package org.marketplace.repository;

import java.util.List;
import java.util.Optional;

import org.marketplace.entity.AbstractEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface AbstractRepository<ENTITY extends AbstractEntity> extends Repository<ENTITY, String> {
	public List<ENTITY> findAll();

    public List<ENTITY> findAll(Specification<ENTITY> spec, Sort sort);

    public Optional<ENTITY> findOne(String id);

    public ENTITY save(ENTITY entity);

    public void delete(ENTITY entity);
}
