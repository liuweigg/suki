package com.ccs.trolls.suki.domain;

import java.util.List;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface FluppfRepository extends PagingAndSortingRepository<Fluppf, FluppfPk> {
  List<Fluppf> findByChdrcoyAndChdrnum(String chdrcoy, String chdrnum);
}
