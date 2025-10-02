package com.serine.library.repository;

import com.serine.library.model.Member;
import java.util.List;
import java.util.Optional;


public interface MemberRepository {
    Member save(Member member);
    Optional<Member> findById(int id);
    List<Member> findAll();
}