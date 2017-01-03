package org.marketplace.service;

import java.util.List;

import org.marketplace.entity.AbstractEntity;
import org.springframework.util.MultiValueMap;

public interface CrudService<ENTITY extends AbstractEntity> {

	public List<ENTITY> findAll(MultiValueMap<String, String> filters);

    public ENTITY findOne(String id) throws Exception;

    public ENTITY create(ENTITY dto);

    public ENTITY update(String id, ENTITY dto) throws Exception;

    public void delete(String id) throws Exception;
}