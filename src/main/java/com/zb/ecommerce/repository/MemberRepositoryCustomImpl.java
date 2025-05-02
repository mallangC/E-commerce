package com.zb.ecommerce.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.zb.ecommerce.model.Member;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import static com.zb.ecommerce.model.QCartProduct.cartProduct;
import static com.zb.ecommerce.model.QMember.member;
import static com.zb.ecommerce.model.QProduct.product;

@RequiredArgsConstructor
public class MemberRepositoryCustomImpl implements MemberRepositoryCustom {
  private final JPAQueryFactory queryFactory;

  @Override
  public Optional<Member> searchMemberByEmail(String email) {
    return Optional.ofNullable(queryFactory.selectFrom(member)
            .leftJoin(member.cart, cartProduct).fetchJoin()
            .leftJoin(cartProduct.product, product).fetchJoin()
            .where(member.email.eq(email))
            .fetchOne());
  }
}
