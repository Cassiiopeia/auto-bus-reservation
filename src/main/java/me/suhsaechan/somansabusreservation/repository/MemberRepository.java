package me.suhsaechan.somansabusreservation.repository;

import java.util.Optional;
import java.util.UUID;
import me.suhsaechan.somansabusreservation.object.dao.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, UUID> {
  Optional<Member> findByEmail(String email);
  Optional<Member> findByNickname(String nickname);
  boolean existsByNickname(String nickname);
  boolean existsByEmail(String email);
}
