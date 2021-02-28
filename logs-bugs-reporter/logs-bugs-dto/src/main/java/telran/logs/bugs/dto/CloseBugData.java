/**
 * 
 */
package telran.logs.bugs.dto;

import java.time.LocalDate;

import javax.validation.constraints.Min;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

/**
 * @author Alex Shtilman Feb 21, 2021
 *
 */
@AllArgsConstructor
@EqualsAndHashCode
public class CloseBugData {
	@Min(1)
	public long bugId;
	public LocalDate dateClose;
	public String description;
}
