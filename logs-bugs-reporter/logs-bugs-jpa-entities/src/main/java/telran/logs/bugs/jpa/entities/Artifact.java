package telran.logs.bugs.jpa.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "artifacts")
//TODO lombock
public class Artifact {
	@Id
	@Column(name = "artifact_id")
	String artifatId;

	@ManyToOne
	@JoinColumn(name = "programmer_id", nullable = false)
	Programmer programmer;

	public Artifact() {

	}

	public Artifact(String artifatId, Programmer programmer) {
		super();
		this.artifatId = artifatId;
		this.programmer = programmer;
	}

	public String getArtifatId() {
		return artifatId;
	}

	public Programmer getProgrammer() {
		return programmer;
	}

}
