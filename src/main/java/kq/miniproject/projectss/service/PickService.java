package kq.miniproject.projectss.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.springframework.stereotype.Service;

@Service
public class PickService {

    private List<Integer> numbers;

    public Boolean checkLast() {
        return numbers.size() == 2;
    }

    public void setNumbers(Integer num) {

        numbers = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            numbers.add(i + 1);
        }
    }

    public Integer getRandInt(Integer remove) {

        if (remove != null) {
            numbers.remove(numbers.indexOf(remove));
        }
        
        if (numbers.size() == 1) {
            return numbers.get(0);
        }

        Random r = new Random();
        return numbers.get(r.nextInt(numbers.size()));
    }
}
