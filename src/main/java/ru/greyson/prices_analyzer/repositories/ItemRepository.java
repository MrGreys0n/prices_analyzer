package ru.greyson.prices_analyzer.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.greyson.prices_analyzer.models.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Integer> {
    Optional<Item> findByName(String name);

    void deleteById(int id);
}
