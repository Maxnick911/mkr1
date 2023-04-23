package com.example.mkr1.repos;

import com.example.mkr1.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepo extends JpaRepository<Book, Long> {
}
