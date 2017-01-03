package org.marketplace.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.marketplace.entity.AbstractEntity;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.MultiValueMap;


public class MarketplaceEntitySpecification<ENTITY extends AbstractEntity> implements Specification<ENTITY> {
	private final String OP_LIKE = ":";
    private final String OP_PERCENT = "%";
    private final String OP_NOT = "!";

    private final String FIELD_ID = "id";
    private final String FIELD_DETAILS = "details";
    private final String DETAILS_PROP = "details.";

    private final String DOT = ".";
    private final String DOT_ESCAPED = "\\.";

    private final String JSON_PATH_START = "$";
    private final String FN_JSON_EXTRACT = "JSON_EXTRACT";
    private final String FN_JSON_UNQUOTE = "JSON_UNQUOTE";

    private MultiValueMap<String, String> filters;

    public MarketplaceEntitySpecification(MultiValueMap<String, String> filters) {
        this.filters = filters;
    }

    @Override
    public Predicate toPredicate(Root<ENTITY> from, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        Map<String, List<Predicate>> predicateMap = new HashMap<String, List<Predicate>>();
        for (Entry<String, List<String>> e : filters.entrySet()) {
            String key = e.getKey();
            List<String> values = e.getValue();

            if (key != null && values != null) {

                if (!predicateMap.containsKey(key)) {
                    predicateMap.put(key, new ArrayList<Predicate>());
                }

                final Expression<String> expression;

                // details.* filters
                if (key.startsWith(DETAILS_PROP)) {
                    String jsonPath = JSON_PATH_START + key.substring(FIELD_DETAILS.length());
                    expression = criteriaBuilder.function(FN_JSON_UNQUOTE, String.class, criteriaBuilder.function(
                            FN_JSON_EXTRACT, Object.class, from.get(FIELD_DETAILS), criteriaBuilder.literal(jsonPath)));
                } else if (key.contains(DOT)) {
                    // supports only one level
                    String attrs[] = key.split(DOT_ESCAPED);
                    expression = from.join(attrs[0])
                                     .get(attrs[1]);
                } else {
                    expression = from.get(key);
                }

                if (expression != null) {
                    // use IN operator for ids (useful for ACL)
                    if (key.equals(FIELD_ID)) {
                        predicateMap.get(key)
                                    .add(expression.in(values));
                    } else {
                        values.stream()
                              .forEach(value -> addPredicate(predicateMap, criteriaBuilder, expression, key, value));
                    }
                }
            }
        }

        if (predicateMap.size() > 0) {
            List<Predicate> andPredicates = new ArrayList<Predicate>();
            for (String key : predicateMap.keySet()) {
                List<Predicate> predicates = predicateMap.get(key);
                andPredicates.add(criteriaBuilder.or(predicates.toArray(new Predicate[predicates.size()])));
            }
            return criteriaBuilder.and(andPredicates.toArray(new Predicate[andPredicates.size()]));
        }
        return null;
    }

    private void addPredicate(Map<String, List<Predicate>> predicateMap, CriteriaBuilder criteriaBuilder,
            Expression<String> exp, String key, String value) {
        List<Predicate> predicates = predicateMap.get(key);
        if (value.contains(OP_LIKE)) {
            if (value.startsWith(OP_NOT)) {
                predicates.add(criteriaBuilder.notLike(exp, value.substring(1)
                                                                 .replace(OP_LIKE, OP_PERCENT)));
            } else {
                predicates.add(criteriaBuilder.like(exp, value.replace(OP_LIKE, OP_PERCENT)));
            }
        } else {
            if (value.startsWith(OP_NOT)) {
                predicates.add(criteriaBuilder.notEqual(exp, value.substring(1)));
            } else {
                predicates.add(criteriaBuilder.equal(exp, value));
            }
        }
    }
}
