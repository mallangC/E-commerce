package com.zb.ecommerce.repository;

import com.zb.ecommerce.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> , MemberRepositoryCustom{

  boolean existsByEmail(String email);

  Optional<Member> findByEmail(String email);

  void deleteByEmail(String email);

}
