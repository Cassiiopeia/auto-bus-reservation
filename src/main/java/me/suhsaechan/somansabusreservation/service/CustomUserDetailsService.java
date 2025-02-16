package me.suhsaechan.somansabusreservation.service;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import me.suhsaechan.somansabusreservation.global.exception.CustomException;
import me.suhsaechan.somansabusreservation.global.exception.ErrorCode;
import me.suhsaechan.somansabusreservation.object.dao.Member;
import me.suhsaechan.somansabusreservation.object.dto.CustomUserDetails;
import me.suhsaechan.somansabusreservation.repository.MemberRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

  private final MemberRepository memberRepository;

  // 로그인 시 사용 (nickname 로 체크)
  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    Member member = memberRepository.findByEmail(email)
        .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
    return new CustomUserDetails(member);
  }

  // JWT 토큰 검증 시 사용 (memberId 로 체크)
  public UserDetails loadUserByMemberId(UUID memberId) throws UsernameNotFoundException {
    Member member = memberRepository.findById(memberId)
        .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
    return new CustomUserDetails(member);
  }
}
