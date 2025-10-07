package com.serine.library.repository;

import com.serine.library.model.Member;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


public class InMemoryMemberRepository implements MemberRepository {
    private final Map<Integer, Member> store = new ConcurrentHashMap<>();
    
    @Override
    public Member save(Member m) {
        store.put(m.getId(), m);
        return m;
    }

    @Override
    public Optional<Member> findById(int id) {
        return Optional.ofNullable(store.get(id));
    }


    @Override
    public List<Member> findAll() { 
        return new ArrayList<>(store.values()); }

    @Override
    public void delete(int id) { store.remove(id); }    
}