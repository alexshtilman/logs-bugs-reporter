/**
 * 
 */
package telran.logs.bugs.dto;

import java.time.LocalDate;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author Alex Shtilman Feb 21, 2021
 *
 */
@EqualsAndHashCode(callSuper = false)
@ToString
public class BugResponseDto extends BugAssignDto {

	public BugResponseDto(Seriousness seriousness, String description, LocalDate dateOpen, long programmerId,
			LocalDate dateClose, BugStatus status, OpenningMethod openningMethod, long bugId) {
		super(seriousness, description, dateOpen, programmerId);
		this.bugId = bugId;
		this.dateClose = dateClose;
		this.status = status;
		this.openningMethod = openningMethod;
	}

	public long bugId;
	public LocalDate dateClose;
	public BugStatus status;
	public OpenningMethod openningMethod;
}
