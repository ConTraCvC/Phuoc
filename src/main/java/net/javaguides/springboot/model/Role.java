package net.javaguides.springboot.model;

import javax.persistence.*;

@Entity
@Table(name = "role")
public class Role {
	@Id
	@SequenceGenerator(
			name = "role_sequence",
			sequenceName = "role_sequence",
			allocationSize = 1

	)
	@GeneratedValue(
			strategy = GenerationType.SEQUENCE,
			generator = "role_sequence")
	private Long id;
	private String name;
	
	public Role() {
		
	}
	
	public Role(String name) {
		super();
		this.name = name;
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
