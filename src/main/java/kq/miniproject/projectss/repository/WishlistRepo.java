package kq.miniproject.projectss.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import jakarta.annotation.Resource;
import kq.miniproject.projectss.model.Person;

@Repository
public class WishlistRepo {

    @Resource(name = "wishlistRedisTemplate")
    private ListOperations<Person, Object> wishlistListOps;

    @Autowired
    @Qualifier("wishlistRedisTemplate")
    private RedisTemplate<Object, Object> wishlstTemplate;

    public List<Object> updateWishlist(Person person, Object wish) {

        if (wish != null) {
            wishlistListOps.rightPush(person, wish);
        }

        if (!wishlstTemplate.hasKey(person)) {
            return null;
        }

        return wishlistListOps.range(person, 0, wishlistListOps.size(person));
    }

    public void deleteWish(Person person, Long id) {

        wishlistListOps.set(person, id, "to delete");
        wishlistListOps.remove(person, 0, "to delete");
    }
}
