package com.zb.ecommerce.repository;

import com.zb.ecommerce.model.Member;

import java.util.Optional;

public interface MemberRepositoryCustom {

  Optional<Member> searchMemberByEmail(String email);
}
