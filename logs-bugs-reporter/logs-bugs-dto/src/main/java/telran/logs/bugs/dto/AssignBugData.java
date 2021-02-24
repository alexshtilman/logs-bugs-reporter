/**
 * 
 */
package telran.logs.bugs.dto;

import javax.validation.constraints.Min;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Alex Shtilman Feb 21, 2021
 *
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class AssignBugData {
	@Min(1)
	public long bugId;
	@Min(1)
	public long programmerId;
	public String description;
}
