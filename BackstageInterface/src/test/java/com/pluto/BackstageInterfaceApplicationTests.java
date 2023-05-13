package com.pluto;

import cn.hutool.core.util.ArrayUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.ClientInfoStatus;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SpringBootTest
class BackstageInterfaceApplicationTests {

    @Test
    void test(){
        List<Integer> list = Arrays.asList(1, 2, 3,4,5,6);
        List<Integer> collect = list.stream().filter(i -> {
            if (i > 4) {
                return false;
            } else {
                return true;
            }
        }).collect(Collectors.toList());
        collect.forEach(System.out::println);
    }

}
