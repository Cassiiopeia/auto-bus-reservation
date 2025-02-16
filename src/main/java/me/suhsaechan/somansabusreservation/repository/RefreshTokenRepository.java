package me.suhsaechan.somansabusreservation.repository;

import java.util.Optional;
import java.util.UUID;
import me.suhsaechan.somansabusreservation.object.dao.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
  Optional<RefreshToken> findByToken(String token);
  Optional<RefreshToken> findByMemberId(UUID memberId);
  void deleteByToken(String token);
}
