package com.redhat.database.benchmark.client;

import com.redhat.database.benchmark.client.Fruit;

public interface IBenchmarkService {
    Fruit add(Fruit pojo);
    Fruit get(String id);
}