package kq.miniproject.projectss.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kq.miniproject.projectss.model.Person;
import kq.miniproject.projectss.repository.SantaRepo;

@Service
public class ShowService {

    @Autowired
    SantaRepo repo;

    public Person findPerson(String groupId, String personId) {

        List<Person> people = repo.getGroup(groupId);
        for (Person person : people) {
            if (person.getUniqueId().equals(personId)) {
                return person;
            }
        }
        return null;
    }

    public Person findSantee(String id, Person person) {
        return repo.getSantee(id, person);
    }
}
