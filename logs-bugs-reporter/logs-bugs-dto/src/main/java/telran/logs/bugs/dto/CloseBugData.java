/**
 * 
 */
package telran.logs.bugs.dto;

import java.time.LocalDate;

import javax.validation.constraints.Min;

import lombok.AllArgsConstructor;

/**
 * @author Alex Shtilman Feb 21, 2021
 *
 */
@AllArgsConstructor
public class CloseBugData {
	@Min(1)
	public long bugId;
	public LocalDate dateClose;
	public String description;
}
