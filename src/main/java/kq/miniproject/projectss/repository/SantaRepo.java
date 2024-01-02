package kq.miniproject.projectss.repository;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import jakarta.annotation.Resource;
import kq.miniproject.projectss.model.Exchange;
import kq.miniproject.projectss.model.Person;

@Repository
public class SantaRepo {

    @Resource(name = "personRedisTemplate")
    private ListOperations<String, Person> personListOps;

    @Autowired
    @Qualifier("personRedisTemplate")
    private RedisTemplate<String, Object> personTemplate;

    @Resource(name = "santaRedisTemplate")
    private HashOperations<String, Person, Person> santaHashOps;

    @Autowired
    @Qualifier("santaRedisTemplate")
    private RedisTemplate<String, Object> santaTemplate;

    @Resource(name = "exchangeRedisTemplate")
    private ValueOperations<String, Exchange> exchangeValueOps;

    @Autowired
    @Qualifier("exchangeRedisTemplate")
    private RedisTemplate<String, Object> exchangeTemplate;

    public Date getExpiry(Date date) {

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, 7);
        return cal.getTime();
    }

    public void setGroupExpiry(String id) {

        Date expiry = getExpiry((exchangeValueOps.get(id).getDate()));
        personTemplate.expireAt(id, expiry);
        santaTemplate.expireAt(id, expiry);

    }

    public void savePerson(String id, Person person) {
        personListOps.rightPush(id, person);
    }

    public List<Person> getGroup(String id) {

        if (personTemplate.hasKey(id)) {
            return personListOps.range(id, 0, personListOps.size(id));
        }

        return null;
    }

    public void saveExchange(String id, Exchange exchange) {
        exchangeValueOps.set(id, exchange);
        exchangeTemplate.expireAt(id, getExpiry(exchange.getDate()));
    }

    public Exchange getExchange(String id) {
        return exchangeValueOps.get(id);
    }

    public void saveSanta(String id, Map<Person, Person> santa) {
        for (Person person : santa.keySet()) {
            santaHashOps.put(id, person, santa.get(person));
        }
    }

    public Map<Person, Person> getSanta(String id) {

        Map<Person, Person> santa = new HashMap<>();
        for (Person person : santaHashOps.keys(id)) {
            santa.put(person, santaHashOps.get(id, person));
        }
        return santa;
    }

    public Person getSantee(String id, Person person) {

        return santaHashOps.get(id, person);
    }

    public void deleteGroup(String id) {
        personTemplate.delete(id);
        santaTemplate.delete(id);
        exchangeTemplate.delete(id);
    }

    public void updatePerson(String id, Person person, Long index) {
        personListOps.set(id, index, person);
    }

    public void removePerson(String id, Long index) {
        personListOps.set(id, index, null);
        personListOps.remove(id, 0, null);
    }
}
