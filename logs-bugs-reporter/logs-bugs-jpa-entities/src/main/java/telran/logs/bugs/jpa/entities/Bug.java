package telran.logs.bugs.jpa.entities;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import telran.logs.bugs.dto.BugStatus;
import telran.logs.bugs.dto.OpenningMethod;
import telran.logs.bugs.dto.Seriousness;

@Entity
@Table(name = "bugs", indexes = { @Index(columnList = "programmer_id"), @Index(columnList = "seriousness"),
		@Index(columnList = "status"), })
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Bug {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Setter(value = AccessLevel.NONE)
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
	Seriousness seriousness;

	@Enumerated(EnumType.STRING)
	@Column(name = "oppening_method", nullable = false)
	OpenningMethod oppeningMethod;

	@ManyToOne
	@JoinColumn(name = "programmer_id", nullable = true)
	Programmer programmer;

	public Bug(String description, LocalDate dateOppen, LocalDate dateClose, BugStatus status, Seriousness seriousness,
			OpenningMethod oppeningMethod, Programmer programmer) {
		super();
		this.description = description;
		this.dateOppen = dateOppen;
		this.dateClose = dateClose;
		this.status = status;
		this.seriousness = seriousness;
		this.oppeningMethod = oppeningMethod;
		this.programmer = programmer;
	}
}
