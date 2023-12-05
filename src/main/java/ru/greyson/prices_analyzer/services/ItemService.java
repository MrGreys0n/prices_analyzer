package ru.greyson.prices_analyzer.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.greyson.prices_analyzer.models.Item;
import ru.greyson.prices_analyzer.repositories.ItemRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ItemService {
    private final ItemRepository itemRepository;

    @Autowired
    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @Transactional(readOnly = true)
    public List<Item> getAll(){
        return itemRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Item getOne(int id){
        Optional<Item> item = itemRepository.findById(id);
        return item.orElse(null);
    }

    @Transactional(readOnly = true)
    public Item getByName(String name){
        Optional<Item> item = itemRepository.findByName(name);
        return item.orElse(null);
    }

    public boolean create(Item item){
        Optional<Item> received_item = itemRepository.findByName(item.getName());
        if (received_item.isPresent()) return false;
        itemRepository.save(item);
        return true;
    }

    public void update(Item item){
        itemRepository.save(item);
    }

    public void delete(int id){
        itemRepository.deleteById(id);
    }
}