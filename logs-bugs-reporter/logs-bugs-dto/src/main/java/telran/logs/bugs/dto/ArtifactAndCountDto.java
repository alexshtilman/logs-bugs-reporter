package telran.logs.bugs.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@ToString
@Getter
@EqualsAndHashCode
public class ArtifactAndCountDto {
	public final String artifact;
	public final int count;
}
