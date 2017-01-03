package org.marketplace.service;

import java.util.List;

import org.marketplace.constants.URI;
import org.marketplace.entity.AbstractEntity;
import org.marketplace.repository.AbstractRepository;
import org.marketplace.repository.MarketplaceEntitySpecification;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;

public abstract class AbstractService<ENTITY extends AbstractEntity> implements CrudService<ENTITY> {

	protected abstract AbstractRepository<ENTITY> getRepository();

	protected abstract ENTITY merge(ENTITY existingEntity, ENTITY newEntity);

	@Override
	@Transactional(readOnly = true)
	public List<ENTITY> findAll(MultiValueMap<String, String> filters) {
		Sort sort = createSorter(filters);
		MarketplaceEntitySpecification<ENTITY> spec = new MarketplaceEntitySpecification<>(filters);
		return getRepository().findAll(spec, sort);
	}

	@Override
	@Transactional(readOnly = true)
	public ENTITY findOne(String id) throws Exception {
		return getRepository().findOne(id).orElseThrow(() -> new Exception("Entity Not Found"));
	}

	@Override
	@Transactional
	public void delete(String id) throws Exception {
		ENTITY existing = findOne(id);
		getRepository().delete(existing);
	}

	@Override
	@Transactional
	public ENTITY create(ENTITY entity) {
		return getRepository().save(entity);
	}

	@Override
	@Transactional
	public ENTITY update(String id, ENTITY entity) throws Exception {
		ENTITY existing = findOne(id);
		existing = merge(existing, entity);
		existing = getRepository().save(existing);
		return existing;
	}
	
	private Sort createSorter(MultiValueMap<String, String> filters) {
        // TODO: Sort on detail.* props
        // TODO: Multiple sorts
        String sortBy = filters.getFirst(URI.SORT_BY);
        if (sortBy == null || sortBy.length() == 0) {
            return null;
        }

        
        String sortOrder = filters.getFirst(URI.SORT_ORDER);
        filters.remove(URI.SORT_BY);
        filters.remove(URI.SORT_ORDER);

        Direction direction = (sortOrder == null || sortOrder.length() == 0) ? Direction.ASC : Direction.fromString(sortOrder);
        return new Sort(direction, sortBy);
    }
}
