/**
 * 
 */
package telran.logs.bugs.dto;

import javax.validation.constraints.Min;

import lombok.AllArgsConstructor;

/**
 * @author Alex Shtilman Feb 21, 2021
 *
 */
@AllArgsConstructor
public class AssignBugData {
	@Min(1)
	public long bugId;
	@Min(1)
	public long programmerId;
	public String description;
}
