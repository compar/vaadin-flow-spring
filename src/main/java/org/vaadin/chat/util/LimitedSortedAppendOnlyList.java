package org.vaadin.chat.util;

import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;
import java.util.TreeSet;
import java.util.stream.Stream;

public class LimitedSortedAppendOnlyList<T> {

    private final int limit;
    private final TreeSet<T> items;

    public LimitedSortedAppendOnlyList(int limit, Comparator<T> compartor){
        this.limit = limit;
        this.items = new TreeSet<>(compartor);
    }

    public void add(T item){
        items.add(item);
        if (items.size()>limit){
            items.pollFirst(); //pollFirst() 是用于删除 TreeSet 中第一项的内置方法。
        }
    }

    public void addAll(Collection<T> items){
        items.forEach(this::add);
    }

    public Stream<T> stream(){
        return items.stream();
    }

    public Optional<T> getLast(){
        if (items.isEmpty()){
            return Optional.empty();
        }
        return Optional.of(items.last());
    }
}
