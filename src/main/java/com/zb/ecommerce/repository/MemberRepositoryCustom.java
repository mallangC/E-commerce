package com.zb.ecommerce.repository;

import com.zb.ecommerce.model.Member;

public interface MemberRepositoryCustom {

  Member searchByEmail(String email);
}
