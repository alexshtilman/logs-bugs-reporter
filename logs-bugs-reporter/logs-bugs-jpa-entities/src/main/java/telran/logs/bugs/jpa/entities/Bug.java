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

import org.apache.velocity.tools.config.SkipSetters;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import telran.logs.bugs.dto.BugStatus;
import telran.logs.bugs.dto.OpenningMethod;
import telran.logs.bugs.dto.Seriousness;

@Entity
@Table(name = "bugs")
@NoArgsConstructor
@Getter
@Setter
@SkipSetters()
@EqualsAndHashCode
@ToString
public class Bug {
	// FIXME remove setters!
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
	Seriousness seriosness;

	@Enumerated(EnumType.STRING)
	@Column(name = "oppening_method", nullable = false)
	OpenningMethod oppeningMethod;

	@ManyToOne
	@JoinColumn(name = "programmer_id", nullable = true)
	Programmer programmer;

	public Bug(String description, LocalDate dateOppen, LocalDate dateClose, BugStatus status, Seriousness seriosness,
			OpenningMethod oppeningMethod, Programmer programmer) {
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
