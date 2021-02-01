package telran.logs.bugs.jpa.entities;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "bugs")
//TODO add lombok
public class Bug {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	long id;
	@Column(nullable = false)
	String description;
	@Column(name = "date_oppen", nullable = false)
	LocalDate dateOppen;
	@Column(name = "date_close", nullable = true)
	LocalDate dateClose;
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	BugStatus status;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	Seriosness seriosness;

	@Enumerated(EnumType.STRING)
	@Column(name = "oppening_method", nullable = false)
	OppeningMethod oppeningMethod;

	@ManyToOne
	@JoinColumn(name = "programmer_id", nullable = false)
	Programmer programmer;

	public Bug() {

	}

	public Bug(String description, LocalDate dateOppen, LocalDate dateClose, BugStatus status, Seriosness seriosness,
			OppeningMethod oppeningMethod, Programmer programmer) {
		super();
		this.description = description;
		this.dateOppen = dateOppen;
		this.dateClose = dateClose;
		this.status = status;
		this.seriosness = seriosness;
		this.oppeningMethod = oppeningMethod;
		this.programmer = programmer;
	}

	public long getId() {
		return id;
	}

	public String getDescription() {
		return description;
	}

	public LocalDate getDateOppen() {
		return dateOppen;
	}

	public LocalDate getDateClose() {
		return dateClose;
	}

	public BugStatus getStatus() {
		return status;
	}

	public Seriosness getSeriosness() {
		return seriosness;
	}

	public OppeningMethod getOppeningMethod() {
		return oppeningMethod;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dateClose == null) ? 0 : dateClose.hashCode());
		result = prime * result + ((dateOppen == null) ? 0 : dateOppen.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((oppeningMethod == null) ? 0 : oppeningMethod.hashCode());
		result = prime * result + ((programmer == null) ? 0 : programmer.hashCode());
		result = prime * result + ((seriosness == null) ? 0 : seriosness.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Bug other = (Bug) obj;
		if (dateClose == null) {
			if (other.dateClose != null)
				return false;
		} else if (!dateClose.equals(other.dateClose))
			return false;
		if (dateOppen == null) {
			if (other.dateOppen != null)
				return false;
		} else if (!dateOppen.equals(other.dateOppen))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (oppeningMethod != other.oppeningMethod)
			return false;
		if (programmer == null) {
			if (other.programmer != null)
				return false;
		} else if (!programmer.equals(other.programmer))
			return false;
		if (seriosness != other.seriosness)
			return false;
		if (status != other.status)
			return false;
		return true;
	}

	public Programmer getProgrammer() {
		return programmer;
	}

}
