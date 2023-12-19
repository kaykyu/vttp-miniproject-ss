package kq.miniproject.projectss.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kq.miniproject.projectss.model.Exchange;
import kq.miniproject.projectss.model.Person;
import kq.miniproject.projectss.repository.SantaRepo;

@Service
public class SantaService {

    @Autowired
    SantaRepo repo;

    public void addPerson(String id, Person person) {

        person.setUniqueId(UUID.randomUUID().toString().substring(0, 8));
        repo.savePerson(id, person);
    }

    public String addExchange(String id, Exchange exchange) {

        if (id == null) {
            id = UUID.randomUUID().toString().replace("-", "");
        }

        repo.saveExchange(id, exchange);
        return id;
    }

    public Map<Person, Person> generateSanta(String id) {

        List<Person> people = repo.getGroup(id);
        List<Person> list = new ArrayList<>(people);
        Map<Person, Person> santa = new HashMap<>();

        for (Person person : people) {
            Random r = new Random();
            int rand = r.nextInt(list.size());

            while (list.get(rand).equals(person)) {
                rand = r.nextInt(list.size());
            }

            santa.put(person, list.get(rand));
            list.remove(rand);
        }

        repo.saveSanta(id, santa);
        repo.setGroupExpiry(id);
        return santa;
    }

    public void removeGroup(String id) {
        repo.deleteGroup(id);
    }

    public List<Person> getPeople(String id) {
        return repo.getGroup(id);
    }

    public Exchange getExchange(String id) {
        return repo.getExchange(id);
    }

    public Person findPerson(String id, Integer index) {
        return repo.getGroup(id).get(index);
    }

    public void updatePerson(String id, Person person, String index) {
        person.setUniqueId(UUID.randomUUID().toString().substring(0, 8));
        repo.updatePerson(id, person, Long.valueOf(index));
    }

    public void removePerson(String id, String index) {
        repo.removePerson(id, Long.valueOf(index));
    }

    public Integer groupSize(String id) {

        if (repo.getGroup(id) != null) {
            return repo.getGroup(id).size();
        }

        return 0;
    }
}
