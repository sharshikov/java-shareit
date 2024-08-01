package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Integer> {
    @Query("SELECT i FROM Item i WHERE upper(i.name) LIKE upper(concat('%', ?1, '%')) OR upper(i.description) LIKE upper(concat('%', ?1, '%'))")
    List<Item> search(String text);

    List<Item> findAllByOwnerId(Integer ownerId);
}
