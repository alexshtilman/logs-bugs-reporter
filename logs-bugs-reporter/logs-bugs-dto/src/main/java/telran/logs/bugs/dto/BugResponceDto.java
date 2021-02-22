/**
 * 
 */
package telran.logs.bugs.dto;

import java.time.LocalDate;

import lombok.EqualsAndHashCode;

/**
 * @author Alex Shtilman Feb 21, 2021
 *
 */
@EqualsAndHashCode(callSuper = false)
public class BugResponceDto extends BugAssignDto {

	public BugResponceDto(Seriousness seriousness, String description, LocalDate dateOpen, long programmerId,
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
