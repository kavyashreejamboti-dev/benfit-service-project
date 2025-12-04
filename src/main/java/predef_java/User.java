package predef_java;

import java.time.LocalDateTime;

public record User(UserId id, String firstName, String lastName, LocalDateTime createdAt) {
}
