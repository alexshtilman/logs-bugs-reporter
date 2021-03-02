/**
 * 
 */
package telran.logs.bugs.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Alex Shtilman Feb 27, 2021
 *
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class SeriousnessBugCount {
	Seriousness seriousness;
	long count;
}
