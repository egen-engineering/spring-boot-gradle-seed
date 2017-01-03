package org.marketplace.entity;

import java.util.Map;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@MappedSuperclass
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AbstractEntity {
	@Id
	private String id;
	
	@Convert(converter = MapToJsonConverter.class)
	@Column(columnDefinition = "JSON")
	private Map<String, Object> details;
	
	public AbstractEntity() {
		this.id = UUID.randomUUID().toString();
	}
}
