package com.serine.library.repository;

import com.serine.library.model.Member;
import java.util.*;


public class InMemoryMemberRepository implements MemberRepository {
    private final Map<Integer, Member> store = new HashMap<>();
    private int nextId = 1;
    
    @Override
    public Member save(Member member) {
        if (member.getId() == 0) {
            member.setId(nextId++);
        }
        store.put(member.getId(), member);
        return member;
    }

    @Override
    public Optional<Member> findById(int id) {
        return Optional.ofNullable(store.get(id));
    }


    @Override
    public List<Member> findAll() { return new ArrayList<>(store.values()); }
}