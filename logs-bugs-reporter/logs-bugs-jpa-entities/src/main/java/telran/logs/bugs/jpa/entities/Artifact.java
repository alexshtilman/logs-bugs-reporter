package telran.logs.bugs.jpa.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "artifacts")
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Artifact {
	@Id
	@Column(name = "artifact_id")
	String artifatId;

	@ManyToOne
	@JoinColumn(name = "programmer_id", nullable = false)
	Programmer programmer;

}
