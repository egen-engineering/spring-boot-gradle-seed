package org.marketplace.controller;

import org.marketplace.constants.URI;
import org.marketplace.entity.AbstractEntity;
import org.marketplace.service.CrudService;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public abstract class AbstractController<ENTITY extends AbstractEntity> {

	protected abstract CrudService<ENTITY> getService();
	
	@RequestMapping(method = RequestMethod.GET)
	public List<ENTITY> findAll(@RequestParam(required = false) MultiValueMap<String, String> filters) {
		return getService().findAll(filters);
    }
	
	@RequestMapping(method = RequestMethod.GET, value = URI.ID)
    public ENTITY findOne(@PathVariable("id") String id) throws Exception {
		return getService().findOne(id);
    }
	
	@RequestMapping(method = RequestMethod.POST)
    public ENTITY create(@RequestBody ENTITY entity) {
		return getService().create(entity);
    }

    @RequestMapping(method = RequestMethod.PUT, value = URI.ID)
    public ENTITY update(@PathVariable("id") String id, @RequestBody ENTITY entity) throws Exception {
        return getService().update(id, entity);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = URI.ID)
    public void delete(@PathVariable("id") String id) throws Exception {
        getService().delete(id);
    }
}
