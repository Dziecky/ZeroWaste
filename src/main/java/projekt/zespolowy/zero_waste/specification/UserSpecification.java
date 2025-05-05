package projekt.zespolowy.zero_waste.specification;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import projekt.zespolowy.zero_waste.entity.User;

public class UserSpecification {

    public static Specification<User> withSearch(String search) {
        return (root, query, cb) -> {
            if (search == null || search.trim().isEmpty()) {
                return cb.conjunction();
            }
            String[] tokens = search.trim().toLowerCase().split("\\s+");
            Predicate combined = cb.conjunction();
            for (String token : tokens) {
                String pattern = "%" + token + "%";
                Predicate p = cb.or(
                        cb.like(cb.lower(root.get("username")), pattern),
                        cb.like(cb.lower(root.get("firstName")), pattern),
                        cb.like(cb.lower(root.get("lastName")), pattern)
                );
                combined = cb.and(combined, p);
            }
            return combined;
        };
    }
}
