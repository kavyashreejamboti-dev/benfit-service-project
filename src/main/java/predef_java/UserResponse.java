package predef_java;

import java.time.LocalDateTime;
import java.util.Set;

public record UserResponse(UserId id, String firstName, String lastName, LocalDateTime createdAt,
		Set<Benefit> benefits) {
}
