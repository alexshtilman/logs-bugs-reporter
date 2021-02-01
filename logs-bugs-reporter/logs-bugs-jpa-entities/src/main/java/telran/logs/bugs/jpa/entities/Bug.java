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

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "bugs")
@NoArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
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
}
