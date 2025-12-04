package predef_java;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface BenefitService<AssignBenefitResult, UnassignBenefitResult, ScheduleResult> {
	List<UserResponse> getUsers(GetUsersFilter filter);

	AssignBenefitResult assignBenefit(Set<UserId> users, Benefit benefit);

	UnassignBenefitResult unassignBenefit(Set<UserId> users, Benefit benefit);

	ScheduleResult scheduleBenefitAssigning(Set<UserId> users, Benefit benefit, LocalDateTime at);
}
